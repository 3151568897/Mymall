package com.example.mymall.product.dao;

import com.example.mymall.product.entity.SkuInfoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.mymall.product.vo.ItemSaleAttrsVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * sku信息
 *
 * @author wupeng
 * @email wupeng@gmail.com
 * @date 2024-08-09 18:21:35
 */
@Mapper
public interface SkuInfoDao extends BaseMapper<SkuInfoEntity> {

    List<ItemSaleAttrsVO> getSaleAttrsBySpuId(Long spuId);
}
