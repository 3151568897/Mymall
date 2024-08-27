package com.example.mymall.product.service.impl;

import com.example.common.to.SpuBoundTO;
import com.example.common.utils.R;
import com.example.mymall.product.entity.*;
import com.example.mymall.product.feign.CouponFeignService;
import com.example.mymall.product.service.*;
import com.example.mymall.product.vo.SkuVO;
import com.example.mymall.product.vo.SpuInfoBaseAttrsSaveVO;
import com.example.mymall.product.vo.SpuInfoBoundsSaveVO;
import com.example.mymall.product.vo.SpuInfoSaveVO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.utils.PageUtils;
import com.example.common.utils.Query;

import com.example.mymall.product.dao.SpuInfoDao;
import org.springframework.transaction.annotation.Transactional;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Autowired
    private SpuInfoDescService spuInfoDescService;
    @Autowired
    private ProductAttrValueService productAttrValueService;
    @Autowired
    private SpuImagesService spuImagesService;
    @Autowired
    private AttrService attrService;
    @Autowired
    private SkuInfoService skuInfoService;
    @Autowired
    private CouponFeignService couponFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void saveSpuInfo(SpuInfoSaveVO spuInfoSaveVO) {
        //1.保持spuinfo基本信息 pms_spu_info
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(spuInfoSaveVO, spuInfoEntity);
        spuInfoEntity.setCreateTime(new Date());
        spuInfoEntity.setUpdateTime(new Date());
        this.saveInfo(spuInfoEntity);

        //2.保存spu图集 pms_spu_images
        spuImagesService.saveImages(spuInfoEntity.getId(), spuInfoSaveVO.getImages());

        //3.保存spu描述图片集 pms_spu_info_desc
        String[] decript = spuInfoSaveVO.getDecript();
        SpuInfoDescEntity spuInfoDescEntity = new SpuInfoDescEntity();
        spuInfoDescEntity.setSpuId(spuInfoEntity.getId());
        spuInfoDescEntity.setDecript(String.join(",", decript));
        spuInfoDescService.save(spuInfoDescEntity);

        //4.保存spu的规格参数 pms_product_attr_value
        List<SpuInfoBaseAttrsSaveVO> baseAttrs = spuInfoSaveVO.getBaseAttrs();
        List<ProductAttrValueEntity> productAttrValueEntityList = new ArrayList<>();
        for (SpuInfoBaseAttrsSaveVO attr : baseAttrs) {
            ProductAttrValueEntity productAttr = new ProductAttrValueEntity();
            productAttr.setSpuId(spuInfoEntity.getId());
            productAttr.setAttrId(attr.getAttrId());
            productAttr.setAttrValue(attr.getAttrValues());
            productAttr.setQuickShow(attr.getShowDesc());
            //根据attrId获取attrName
            AttrEntity attrEntity = attrService.getById(attr.getAttrId());
            productAttr.setAttrName(attrEntity.getAttrName());

            productAttrValueEntityList.add(productAttr);
        }
        productAttrValueService.saveBatch(productAttrValueEntityList);

        //5.保存spu的积分信息 mymall_sms->sms_spu_bounds
        SpuInfoBoundsSaveVO bounds = spuInfoSaveVO.getBounds();
        SpuBoundTO spuBoundTO = new SpuBoundTO();
        BeanUtils.copyProperties(bounds, spuBoundTO);
        spuBoundTO.setSpuId(spuInfoEntity.getId());
        R r = couponFeignService.saveSpuBounds(spuBoundTO);
        if(r.getCode() != 0){
            log.error("远程保存spu积分信息失败");
        }

        //6.保存当前spu对应的sku的详细信息
        List<SkuVO> skus = spuInfoSaveVO.getSkus();
        skuInfoService.saveSkus(spuInfoEntity, skus);
    }

    @Override
    public void saveInfo(SpuInfoEntity spuInfoEntity) {
        this.baseMapper.insert(spuInfoEntity);
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        QueryWrapper<SpuInfoEntity> wrapper = new QueryWrapper<>();

        String key = (String) params.get("key");
        if(!StringUtils.isEmpty((key))){
            wrapper.and((w)->{
                w.eq("id",key).or().like("spu_name",key);
            });
        }
        String status = (String) params.get("status");
        if(!StringUtils.isEmpty((status))){
            wrapper.eq("publish_status",status);
        }
        String brandId = (String) params.get("brandId");
        if(!StringUtils.isEmpty((brandId)) && !"0".equalsIgnoreCase(brandId)){
            wrapper.eq("brand_id",brandId);
        }

        String catalogId = (String) params.get("catelogId");
        if(!StringUtils.isEmpty((catalogId)) && !"0".equalsIgnoreCase(catalogId)){
            wrapper.eq("catalog_id",catalogId);
        }

        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

}
