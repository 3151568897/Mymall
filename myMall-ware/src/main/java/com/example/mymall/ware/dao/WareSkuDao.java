package com.example.mymall.ware.dao;

import com.example.mymall.ware.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品库存
 * 
 * @author wupeng
 * @email wupeng@gmail.com
 * @date 2024-08-10 16:10:48
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {
	
}
