package com.example.mymall.product.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.example.common.constant.ProductConstant;
import com.example.common.to.SkuHasStockTO;
import com.example.common.to.SpuBoundTO;
import com.example.common.to.es.SkuEsModel;
import com.example.common.utils.R;
import com.example.mymall.product.entity.*;
import com.example.mymall.product.feign.CouponFeignService;
import com.example.mymall.product.feign.SearchFeignService;
import com.example.mymall.product.feign.WareFeignService;
import com.example.mymall.product.service.*;
import com.example.mymall.product.vo.SkuVO;
import com.example.mymall.product.vo.SpuInfoBaseAttrsSaveVO;
import com.example.mymall.product.vo.SpuInfoBoundsSaveVO;
import com.example.mymall.product.vo.SpuInfoSaveVO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

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
    @Autowired
    private BrandService brandService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private WareFeignService wareFeignService;
    @Autowired
    private SearchFeignService searchFeignService;

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

    @Override
    public void up(Long spuId) {
        //将商品的信息新增到es中,方便前端检索
        //封装需要上架的商品
        //1.查询当前sku可以被检索的规格属性
        List<ProductAttrValueEntity> baseAttrs = productAttrValueService.AttrListForSpu(spuId);
        List<Long> attrIds = baseAttrs.stream().map(ProductAttrValueEntity::getAttrId).collect(Collectors.toList());
        //1.1 判断那些属性可以被快速检索
        List<Long> SearchAttrIds = attrService.selectSearchAttrIds(attrIds);
        Set<Long> searchAttrIdSet = new HashSet<>(SearchAttrIds);

        List<SkuEsModel.Attrs> attrs = baseAttrs.stream().filter(attr -> {
            return searchAttrIdSet.contains(attr.getAttrId());
        }).map(attr -> {
            SkuEsModel.Attrs attrs1 = new SkuEsModel.Attrs();
            BeanUtils.copyProperties(attr, attrs1);
            return attrs1;
        }).collect(Collectors.toList());
        //1.获取当前spu对应的所有sku信息
        List<SkuInfoEntity> skus = skuInfoService.getSkusBySpuId(spuId);

        //1.1远程调用,查询是否有库存
        List<Long> skuIds = skus.stream().map(SkuInfoEntity::getSkuId).collect(Collectors.toList());
        Map<Long, Boolean> hasSkuStock = null;
        try {
            //如果远程调用失败,默认都有库存
            R r = wareFeignService.getSkuHasStock(skuIds);
            TypeReference<List<SkuHasStockTO>> typeReference = new TypeReference<>() {
            };
            hasSkuStock = r.getData(typeReference).stream().collect(Collectors.toMap(SkuHasStockTO::getSkuId, SkuHasStockTO::getHasStock));
        }catch (Exception e){
            log.error("库存服务远程调用失败:{}", e);
        }

        //2.封装sku的信息
        Map<Long, Boolean> finalHasSkuStock = hasSkuStock;
        List<SkuEsModel> upProducts  = skus.stream().map(sku -> {
            SkuEsModel esModel = new SkuEsModel();
            BeanUtils.copyProperties(sku, esModel);
            //处理不一样的属性
            esModel.setSkuPrice(sku.getPrice());
            esModel.setSkuImg(sku.getSkuDefaultImg());
            //hasStock, hotScore
            //1.1远程调用,查询是否有库存
            if(finalHasSkuStock == null){
                esModel.setHasStock(true);
            }else {
                esModel.setHasStock(finalHasSkuStock.get(sku.getSkuId()));
            }
            //TODO 1.2 热度评分,现在默认0
            esModel.setHotScore(0L);
            //brandName, brandImg, catalogName
            BrandEntity brand = brandService.getById(sku.getBrandId());
            esModel.setBrandName(brand.getName());
            esModel.setBrandImg(brand.getLogo());
            CategoryEntity catalog = categoryService.getById(sku.getCatalogId());
            esModel.setCatalogName(catalog.getName());

            //attrs
            esModel.setAttrs(attrs);

            return esModel;
        }).collect(Collectors.toList());

        //将数据发送给search服务,然后由它发送给es
        R r = searchFeignService.productStuckUp(upProducts);
        System.out.println(r);
        if(r.getCode() == 0){
            //远程调用成功
            //修改商品的上架状态
            SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
            spuInfoEntity.setId(spuId);
            spuInfoEntity.setPublishStatus(ProductConstant.SpuStatusEnum.UP.getCode());
            spuInfoEntity.setUpdateTime(new Date());
            this.updateById(spuInfoEntity);
        }else{
            //远程调用失败
            //TODO 重复调用? 接口幂:重试机制
            throw new RuntimeException("商品上架保存es远程调用失败");
        }
    }

}
