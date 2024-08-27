package com.example.mymall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.example.mymall.product.dao.AttrAttrgroupRelationDao;
import com.example.mymall.product.dao.AttrDao;
import com.example.mymall.product.entity.AttrAttrgroupRelationEntity;
import com.example.mymall.product.entity.AttrEntity;
import com.example.mymall.product.service.AttrAttrgroupRelationService;
import com.example.mymall.product.service.AttrService;
import com.example.mymall.product.vo.AttrGroupWithAttrsVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.utils.PageUtils;
import com.example.common.utils.Query;

import com.example.mymall.product.dao.AttrGroupDao;
import com.example.mymall.product.entity.AttrGroupEntity;
import com.example.mymall.product.service.AttrGroupService;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Autowired
    private AttrAttrgroupRelationService attrAttrgroupRelationService;
    @Autowired
    private AttrDao attrDao;
    @Autowired
    private AttrService attrService;
    @Autowired
    private AttrAttrgroupRelationDao relationDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, Long catelogId) {
        IPage<AttrGroupEntity> page = null;
        String key = (String)params.get("key");
        QueryWrapper<AttrGroupEntity> wrapper = new QueryWrapper<AttrGroupEntity>();
        if(key != null && key.length() > 0){
            wrapper.and((w)->{
                w.eq("attr_group_id", key).or().like("attr_group_name", key);
            });
        }
        if(catelogId == 0){
            page = this.page(
                    new Query<AttrGroupEntity>().getPage(params),
                    wrapper
            );
        }else{
            wrapper.eq("catelog_id", catelogId);
            page = this.page(
                    new Query<AttrGroupEntity>().getPage(params),
                    wrapper
            );
        }

        return new PageUtils(page);
    }

    @Override
    public void removeByIdsWithoutCascade(List<Long> list) {
        this.removeByIds(list);
        //删除基本信息后 将关系表信息一起删除
        attrAttrgroupRelationService.remove(new QueryWrapper<AttrAttrgroupRelationEntity>().in("attr_group_id", list));

    }

    @Override
    public List<AttrGroupWithAttrsVO> getAttrGroupWithAttrsByCatelogId(Long catelogId) {
        List<AttrGroupWithAttrsVO> attrGroupWithAttrsVOList = new ArrayList<>();

        //获取分类下的所有属性分组
        List<AttrGroupEntity> attrGroupEntities = this.list(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId));

        //获取属性分组下的所有属性
        for (AttrGroupEntity attrGroupEntity : attrGroupEntities) {
            AttrGroupWithAttrsVO attrGroupWithAttrsVO = new AttrGroupWithAttrsVO();
            BeanUtils.copyProperties(attrGroupEntity, attrGroupWithAttrsVO);
            //获取属性分组下的所有属性
            List<AttrEntity> attrs = attrService.attrRelation(attrGroupEntity.getAttrGroupId());
            attrGroupWithAttrsVO.setAttrs(attrs);
            attrGroupWithAttrsVOList.add(attrGroupWithAttrsVO);
        }

        return attrGroupWithAttrsVOList;
    }

}
