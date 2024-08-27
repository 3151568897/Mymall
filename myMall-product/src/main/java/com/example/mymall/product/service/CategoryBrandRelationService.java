package com.example.mymall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.common.utils.PageUtils;
import com.example.mymall.product.entity.BrandEntity;
import com.example.mymall.product.entity.CategoryBrandRelationEntity;
import com.example.mymall.product.vo.BrandRelationVO;

import java.util.List;
import java.util.Map;

/**
 * 品牌分类关联
 *
 * @author wupeng
 * @email wupeng@gmail.com
 * @date 2024-08-09 18:21:33
 */
public interface CategoryBrandRelationService extends IService<CategoryBrandRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveDetail(CategoryBrandRelationEntity categoryBrandRelation);

    void updateBrand(Long brandId, String name);

    void updateCategory(Long catId, String name);

    /**
     * 根据分类id获取品牌
     * @param catId
     * @return
     */
    List<BrandEntity> getBrandsByCatId(Long catId);
}

