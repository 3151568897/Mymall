package com.example.mymall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.common.utils.PageUtils;
import com.example.mymall.product.entity.AttrEntity;
import com.example.mymall.product.entity.AttrGroupEntity;
import com.example.mymall.product.vo.AttrGroupWithAttrsVO;

import java.util.List;
import java.util.Map;

/**
 * 属性分组
 *
 * @author wupeng
 * @email wupeng@gmail.com
 * @date 2024-08-09 18:21:35
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

    PageUtils queryPage(Map<String, Object> params);


    /**
     * 根据分类属性获取列表
     */
    PageUtils queryPage(Map<String, Object> params, Long catelogId);

    void removeByIdsWithoutCascade(List<Long> list);

    List<AttrGroupWithAttrsVO> getAttrGroupWithAttrsByCatelogId(Long catelogId);
}

