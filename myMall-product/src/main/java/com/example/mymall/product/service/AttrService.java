package com.example.mymall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.common.utils.PageUtils;
import com.example.mymall.product.entity.AttrEntity;
import com.example.mymall.product.entity.ProductAttrValueEntity;
import com.example.mymall.product.vo.AttrVO;

import java.util.List;
import java.util.Map;

/**
 * 商品属性
 *
 * @author wupeng
 * @email wupeng@gmail.com
 * @date 2024-08-09 18:21:35
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPage(Map<String, Object> params, Long catelogId, String type);

    void saveAttr(AttrVO attr);

    AttrVO getInfo(Long attrId);

    void removeByIdsWithoutCascade(List<Long> list);

    void updateAttr(AttrVO attr);

    PageUtils queryNoRelationAttrPage(Map<String, Object> params, Long attrgroupId);

    List<AttrEntity> attrRelation(Long attrgroupId);

    List<ProductAttrValueEntity> baseAttrListForSpu(Long spuId);

    /**
     * 在attrid的list中获取可以被搜索的attrId的集合
     * @param attrIds
     * @return
     */
    List<Long> selectSearchAttrIds(List<Long> attrIds);
}

