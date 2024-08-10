package com.example.mymall.product.dao;

import com.example.mymall.product.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品三级分类
 * 
 * @author wupeng
 * @email wupeng@gmail.com
 * @date 2024-08-09 18:21:35
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {
	
}
