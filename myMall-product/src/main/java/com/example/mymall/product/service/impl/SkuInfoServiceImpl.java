package com.example.mymall.product.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.example.common.to.SkuHasStockTO;
import com.example.common.to.SkuReductionTO;
import com.example.common.utils.R;
import com.example.mymall.product.config.MyThreadConfig;
import com.example.mymall.product.entity.*;
import com.example.mymall.product.feign.CouponFeignService;
import com.example.mymall.product.feign.WareFeignService;
import com.example.mymall.product.service.*;
import com.example.mymall.product.vo.ItemSaleAttrsVO;
import com.example.mymall.product.vo.SkuImagesVO;
import com.example.mymall.product.vo.SkuItemVO;
import com.example.mymall.product.vo.SkuVO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.utils.PageUtils;
import com.example.common.utils.Query;

import com.example.mymall.product.dao.SkuInfoDao;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {

    @Autowired
    private SkuImagesService skuImagesService;
    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;
    @Autowired
    private CouponFeignService couponFeignService;
    @Autowired
    private SpuInfoDescService spuInfoDescService;
    @Autowired
    private AttrGroupService attrGroupService;
    @Autowired
    private WareFeignService wareFeignService;
    @Autowired
    private ThreadPoolExecutor executor;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                new QueryWrapper<SkuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveSkus(SpuInfoEntity spuInfoEntity, List<SkuVO> skus) {
        if (skus == null || skus.size() == 0) {
            return;
        }
        for (SkuVO sku : skus) {
            //1 保存sku基本信息 pms_sku_info
            SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
            BeanUtils.copyProperties(sku, skuInfoEntity);
            skuInfoEntity.setSpuId(spuInfoEntity.getId());
            skuInfoEntity.setBrandId(spuInfoEntity.getBrandId());
            skuInfoEntity.setCatalogId(spuInfoEntity.getCatalogId());
            skuInfoEntity.setSaleCount(0L);
            //获取默认图片
            String defaultImg = "";
            for (SkuImagesVO image : sku.getImages()) {
                if (image.getDefaultImg() == 1) {
                    defaultImg = image.getImgUrl();
                }
            }
            skuInfoEntity.setSkuDefaultImg(defaultImg);
            this.save(skuInfoEntity);

            Long skuId = skuInfoEntity.getSkuId();
            //2 保存sku的图片信息
            List<SkuImagesEntity> skuImagesEntityList = sku.getImages().stream().map(img -> {
                SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                BeanUtils.copyProperties(img, skuImagesEntity);
                skuImagesEntity.setSkuId(skuId);
                return skuImagesEntity;
            }).filter(entity -> {
                //返回true就是需要,false就是剔除
                return StringUtils.isNotEmpty(entity.getImgUrl());
            }).collect(Collectors.toList());
            System.out.println(skuImagesEntityList);
            skuImagesService.saveBatch(skuImagesEntityList);

            //3 保存当前sku对应的销售属性 pms_sku_sale_attr_value
            List<SkuSaleAttrValueEntity> skuSaleAttrValueEntities = sku.getAttr().stream().map(attrsVO -> {
                SkuSaleAttrValueEntity skuSaleAttrValueEntity = new SkuSaleAttrValueEntity();
                skuSaleAttrValueEntity.setSkuId(skuId);
                BeanUtils.copyProperties(attrsVO, skuSaleAttrValueEntity);
                System.out.println(attrsVO);
                System.out.println(skuSaleAttrValueEntity);
                return skuSaleAttrValueEntity;
            }).collect(Collectors.toList());
            skuSaleAttrValueService.saveBatch(skuSaleAttrValueEntities);

            //4 保存当前sku的优惠,满减等信息 mymall_sms->sms_sku_ladder、sms_sku_full_reduction、sms_member_price
            SkuReductionTO skuReductionTO = new SkuReductionTO();
            BeanUtils.copyProperties(sku, skuReductionTO);
            skuReductionTO.setSkuId(skuId);
            if(skuReductionTO.getFullCount() > 0 || skuReductionTO.getFullPrice().compareTo(new BigDecimal(0)) == 1){
                R r = couponFeignService.saveSkuReduction(skuReductionTO);
                if (r.getCode() != 0) {
                    log.error("远程保存sku优惠信息失败");
                }
            }

        }
    }

    @Override
    public PageUtils queryPagebyCondition(Map<String, Object> params) {
        QueryWrapper<SkuInfoEntity> wrapper = new QueryWrapper<>();
        System.out.println(params);
        String key = (String) params.get("key");
        if(!StringUtils.isEmpty((key))){
            wrapper.and((w)->{
                w.eq("sku_id",key).or().like("sku_name",key);
            });
        }
        String catalogId = (String) params.get("catelogId");
        if(!StringUtils.isEmpty((catalogId)) && !"0".equalsIgnoreCase(catalogId)){
            wrapper.eq("catalog_id", catalogId);
        }
        String brandId = (String) params.get("brandId");
        if(!StringUtils.isEmpty((brandId)) && !"0".equalsIgnoreCase(brandId)){
            wrapper.eq("brand_id", brandId);
        }
        String min = (String) params.get("min");
        if(!StringUtils.isEmpty((min)) && !min.equals("0")){
            wrapper.ge("price", min);
        }
        String max = (String) params.get("max");
        if(!StringUtils.isEmpty((max)) && !max.equals("0")){
            try{
                BigDecimal bigDecimal = new BigDecimal(max);
                if(bigDecimal.compareTo(new BigDecimal(0)) > 0){
                    wrapper.le("price", max);
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }

        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    @Override
    public List<SkuInfoEntity> getSkusBySpuId(Long spuId) {
        return list(new QueryWrapper<SkuInfoEntity>().eq("spu_id", spuId));
    }

    @Override
    public SkuItemVO item(Long skuId) {
        SkuItemVO skuItemVO = new SkuItemVO();

        CompletableFuture<SkuInfoEntity> future = CompletableFuture.supplyAsync(() -> {
            //1.sku基本信息获取 pms_sku_info
            SkuInfoEntity skuInfoEntity = this.getById(skuId);
            skuItemVO.setInfo(skuInfoEntity);
            return skuInfoEntity;
        }, executor);

        CompletableFuture<Void> saleAttrFuture = future.thenAcceptAsync(skuInfoEntity -> {
            Long spuId = skuInfoEntity.getSpuId();
            //3.查询当前spu的销售属性组合
            List<ItemSaleAttrsVO> saleAttrs = this.getSaleAttrsBySpuId(spuId);
            skuItemVO.setSaleAttr(saleAttrs);
        }, executor);

        CompletableFuture<Void> despFuture = future.thenAcceptAsync(skuInfoEntity -> {
            Long spuId = skuInfoEntity.getSpuId();
            //4.查询当前spu的描述信息 pms_spu_info_desc
            SpuInfoDescEntity spuInfoDesc = spuInfoDescService.getOne(new QueryWrapper<SpuInfoDescEntity>().eq("spu_id", spuId));
            skuItemVO.setDesp(spuInfoDesc);
        }, executor);

        CompletableFuture<Void> groupAttrFuture = future.thenAcceptAsync(skuInfoEntity -> {
            Long spuId = skuInfoEntity.getSpuId();
            Long catalogId = skuInfoEntity.getCatalogId();
            //5.获取spu的规格参数信息
            List<SkuItemVO.SpuItemAttrGroupVO> groupAttrs = attrGroupService.getAttrGroupWithAttrsBySpuId(spuId, catalogId);
            skuItemVO.setGroupAttrs(groupAttrs);
        }, executor);

        //2.sku的图片信息 pms_sku_images
        CompletableFuture<Void> imagesFuture = CompletableFuture.runAsync(() -> {
            List<SkuImagesEntity> images = skuImagesService.list(new QueryWrapper<SkuImagesEntity>().eq("sku_id", skuId));
            skuItemVO.setImages(images);
        }, executor);

        //6.是否有货
        CompletableFuture<Void> hasStockFuture = CompletableFuture.runAsync(() -> {
            R r = wareFeignService.getSkuHasStock(Collections.singletonList(skuId));
            if (r.getCode() == 0) {
                TypeReference<List<SkuHasStockTO>> typeReference = new TypeReference<>() {
                };
                List<SkuHasStockTO> hasSkuStock = r.getData(typeReference);
                skuItemVO.setHasStock(hasSkuStock.size() > 0);
            }
        }, executor);

        //等待所有任务完成
        CompletableFuture.allOf(groupAttrFuture, despFuture, saleAttrFuture, imagesFuture, hasStockFuture).join();

        return skuItemVO;
    }

    @Override
    public List<ItemSaleAttrsVO> getSaleAttrsBySpuId(Long spuId) {
        return baseMapper.getSaleAttrsBySpuId(spuId);
    }

//    @Override
//    public List<ItemSaleAttrsVO> getSaleAttrsBySpuId(Long spuId) {
//        List<SkuItemVO.ItemSaleAttrsVO> saleAttrs = new ArrayList<>();
//        //1.获取skuId
//        List<Long> skuIds = getSkusBySpuId(spuId).stream()
//                .map(SkuInfoEntity::getSkuId)
//                .collect(Collectors.toList());
//        //2.获取销售属性
//        List<SkuSaleAttrValueEntity> skuAttrValues = skuSaleAttrValueService.list(new QueryWrapper<SkuSaleAttrValueEntity>().in("sku_id", skuIds));
//        //3.attrIdSet 用于去重
//        HashSet<Long> attrIdSet = new HashSet<>();
//
//        for (SkuSaleAttrValueEntity skuSaleAttr : skuAttrValues) {
//            Long attrId = skuSaleAttr.getAttrId();
//            if(!attrIdSet.contains(attrId)){
//                SkuItemVO.ItemSaleAttrsVO itemSaleAttrsVO = new SkuItemVO.ItemSaleAttrsVO();
//                itemSaleAttrsVO.setAttrId(attrId);
//                itemSaleAttrsVO.setAttrName(skuSaleAttr.getAttrName());
//                //属性值
//                List<String> attrValue = skuAttrValues.stream()
//                        .filter(item -> item.getAttrId().equals(attrId))
//                        .map(SkuSaleAttrValueEntity::getAttrValue)
//                        .distinct()
//                        .collect(Collectors.toList());
//
//                itemSaleAttrsVO.setAttrValues(attrValue);
//                saleAttrs.add(itemSaleAttrsVO);
//                attrIdSet.add(attrId);
//            }
//        }
//
//        return saleAttrs;
//    }

}
