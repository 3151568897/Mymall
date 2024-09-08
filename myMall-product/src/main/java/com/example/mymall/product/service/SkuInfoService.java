package com.example.mymall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.common.utils.PageUtils;
import com.example.mymall.product.entity.SkuInfoEntity;
import com.example.mymall.product.entity.SpuInfoEntity;
import com.example.mymall.product.vo.ItemSaleAttrsVO;
import com.example.mymall.product.vo.SkuItemVO;
import com.example.mymall.product.vo.SkuVO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * sku信息
 *
 * @author wupeng
 * @email wupeng@gmail.com
 * @date 2024-08-09 18:21:35
 */
public interface SkuInfoService extends IService<SkuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveSkus(SpuInfoEntity spuInfoEntity, List<SkuVO> skus);

    PageUtils queryPagebyCondition(Map<String, Object> params);

    List<SkuInfoEntity> getSkusBySpuId(Long spuId);

    SkuItemVO item(Long skuId);

    List<ItemSaleAttrsVO> getSaleAttrsBySpuId(Long spuId);

    BigDecimal getPrice(Long skuId);
}

