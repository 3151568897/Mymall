package com.example.mymall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.common.utils.PageUtils;
import com.example.mymall.product.entity.CategoryEntity;
import com.example.mymall.product.vo.Catalog2VO;

import java.util.List;
import java.util.Map;

/**
 * 商品三级分类
 *
 * @author wupeng
 * @email wupeng@gmail.com
 * @date 2024-08-09 18:21:35
 */
public interface CategoryService extends IService<CategoryEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<CategoryEntity> listWithTree();

    void removeMenuByIds(List<Long> list);

    /**
     * 找到catelogId的完整路径
     * [2,25,225]
     * @param catelogId
     * @return
     */
    Long[] findCatelogPath(Long catelogId);

    void updateCascade(CategoryEntity category);

    List<CategoryEntity> getLevel1Categorys();

    Map<String, List<Catalog2VO>> getCatalogJson() throws InterruptedException;

    CategoryEntity getByCatId(Long catId);
}

