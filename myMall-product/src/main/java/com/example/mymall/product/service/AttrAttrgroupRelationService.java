package com.example.mymall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.common.utils.PageUtils;
import com.example.mymall.product.entity.AttrAttrgroupRelationEntity;
import com.example.mymall.product.entity.AttrEntity;

import java.util.List;
import java.util.Map;

/**
 * 属性&属性分组关联
 *
 * @author wupeng
 * @email wupeng@gmail.com
 * @date 2024-08-09 18:21:35
 */
public interface AttrAttrgroupRelationService extends IService<AttrAttrgroupRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void removeByList(List<AttrAttrgroupRelationEntity> relationList);
}

