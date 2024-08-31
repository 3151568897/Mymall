package com.example.mymall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.common.to.SkuHasStockTO;
import com.example.common.utils.PageUtils;
import com.example.mymall.ware.entity.WareSkuEntity;

import java.util.List;
import java.util.Map;

/**
 * 商品库存
 *
 * @author wupeng
 * @email wupeng@gmail.com
 * @date 2024-08-10 16:10:48
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void addStock(Long skuId, Long wareId, Integer skuNum);

    List<SkuHasStockTO> getSkuHasStock(List<Long> skuIds);
}

