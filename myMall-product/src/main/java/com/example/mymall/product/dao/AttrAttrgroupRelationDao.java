package com.example.mymall.product.dao;

import com.example.mymall.product.entity.AttrAttrgroupRelationEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 属性&属性分组关联
 *
 * @author wupeng
 * @email wupeng@gmail.com
 * @date 2024-08-09 18:21:35
 */
@Mapper
public interface AttrAttrgroupRelationDao extends BaseMapper<AttrAttrgroupRelationEntity> {

    //根据属性组id和属性id批量删除
    void deleteBatchRelation(List<AttrAttrgroupRelationEntity> relationList);
}
