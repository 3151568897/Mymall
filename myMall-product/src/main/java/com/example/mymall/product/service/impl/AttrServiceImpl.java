package com.example.mymall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.example.common.constant.ProductConstant;
import com.example.mymall.product.dao.AttrAttrgroupRelationDao;
import com.example.mymall.product.dao.AttrGroupDao;
import com.example.mymall.product.dao.CategoryDao;
import com.example.mymall.product.entity.AttrAttrgroupRelationEntity;
import com.example.mymall.product.entity.ProductAttrValueEntity;
import com.example.mymall.product.service.AttrAttrgroupRelationService;
import com.example.mymall.product.service.CategoryService;
import com.example.mymall.product.service.ProductAttrValueService;
import com.example.mymall.product.vo.AttrResponseVO;
import com.example.mymall.product.vo.AttrVO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.utils.PageUtils;
import com.example.common.utils.Query;

import com.example.mymall.product.dao.AttrDao;
import com.example.mymall.product.entity.AttrEntity;
import com.example.mymall.product.service.AttrService;
import org.springframework.transaction.annotation.Transactional;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

    @Autowired
    private AttrAttrgroupRelationService attrAttrgroupRelationService;
    @Autowired
    private AttrAttrgroupRelationDao relationDao;
    @Autowired
    private AttrGroupDao attrGroupDao;
    @Autowired
    private CategoryDao categoryDao;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ProductAttrValueService productAttrValueService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, Long catelogId, String type) {
        IPage<AttrEntity> page;
        String key = (String)params.get("key");
        QueryWrapper<AttrEntity> wrapper = new QueryWrapper<AttrEntity>();
        wrapper.eq("attr_type", "base".equalsIgnoreCase(type) ? ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode() : ProductConstant.AttrEnum.ATTR_TYPE_SALE.getCode());
        if(key != null && key.length() > 0){
            wrapper.and((w)->{
                w.eq("attr_id", key).or().like("attr_name", key);
            });
        }
        if(catelogId != 0){
            wrapper.eq("catelog_id", catelogId);
        }

        page = this.page(
                new Query<AttrEntity>().getPage(params),
                wrapper
        );

        //将entity转换为VO,以处理响应数据
        List<AttrEntity> records = page.getRecords();
        List<AttrResponseVO> attrId1 = records.stream().map((attr) -> {
            AttrResponseVO attrResponseVO = new AttrResponseVO();
            BeanUtils.copyProperties(attr, attrResponseVO);
            //设置分组和分类名
            //如果是base类型才设置分组名
            if("base".equalsIgnoreCase(type)){
                //设置分组名
                AttrAttrgroupRelationEntity attrId = attrAttrgroupRelationService.getOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attr.getAttrId()));
                if (attrId != null) {
                    String attrGroupName = attrGroupDao.selectById(attrId.getAttrGroupId()).getAttrGroupName();
                    attrResponseVO.setGroupName(attrGroupName);
                }
            }
            //设置分类名
            String catelogName = categoryDao.selectById(attr.getCatelogId()).getName();
            attrResponseVO.setCatelogName(catelogName);

            return attrResponseVO;
        }).collect(Collectors.toList());

        PageUtils pageUtils = new PageUtils(page);
        pageUtils.setList(attrId1);

        return pageUtils;
    }

    @Override
    @Transactional
    public void saveAttr(AttrVO attr) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr, attrEntity);
        //保存基本数据
        this.save(attrEntity);

        //保存关联关系
        if(attr.getAttrGroupId() != null && attr.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_SALE.getCode()){
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            relationEntity.setAttrGroupId(attr.getAttrGroupId());
            relationEntity.setAttrId(attrEntity.getAttrId());
            attrAttrgroupRelationService.save(relationEntity);
        }

    }

    @Override
    public AttrVO getInfo(Long attrId) {
        //设置分类路径和分组id
        AttrEntity attr = this.getById(attrId);
        AttrVO attrVO = new AttrVO();
        BeanUtils.copyProperties(attr, attrVO);

        //设置分类路径
        Long catelogId = attr.getCatelogId();
        Long[] catelogPath = categoryService.findCatelogPath(catelogId);
        attrVO.setCatelogPath(catelogPath);
        //基本属性才设置分组id
        if(attr.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()){
            AttrAttrgroupRelationEntity attrId1 = attrAttrgroupRelationService.getOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrId));
            if (attrId1 != null) {
                attrVO.setAttrGroupId(attrId1.getAttrGroupId());
            }
        }

        return attrVO;
    }

    @Override
    public void removeByIdsWithoutCascade(List<Long> list) {
        baseMapper.deleteBatchIds(list);

        attrAttrgroupRelationService.remove(new QueryWrapper<AttrAttrgroupRelationEntity>().in("attr_id", list));
    }

    @Override
    @Transactional
    public void updateAttr(AttrVO attr) {

        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr, attrEntity);
        this.updateById(attrEntity);

        //基本属性才修改关系表
        if(attrEntity.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode() && attr.getAttrGroupId() != null){
            //如果属性不存在分组那就新增关系表
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            relationEntity.setAttrId(attr.getAttrId());
            relationEntity.setAttrGroupId(attr.getAttrGroupId());
            //判断是否存在
            int count = attrAttrgroupRelationService.count(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attr.getAttrId()));
            if (count == 0) {
                //新增
                attrAttrgroupRelationService.save(relationEntity);
            }else{
                //修改分组关联
                attrAttrgroupRelationService.update(relationEntity, new UpdateWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attr.getAttrId()));
            }
        }
    }

    @Override
    public PageUtils queryNoRelationAttrPage(Map<String, Object> params, Long attrgroupId) {
        //先查询分组id对应的关系表
        QueryWrapper<AttrAttrgroupRelationEntity> wrapper = new QueryWrapper<AttrAttrgroupRelationEntity>();
        wrapper.eq("attr_group_id", attrgroupId);
        List<AttrAttrgroupRelationEntity> attrAttrgroupRelationEntities = relationDao.selectList(wrapper);

        //通过关系表获取属性实体
        List<Long> attrIds = attrAttrgroupRelationEntities.stream().map(AttrAttrgroupRelationEntity::getAttrId).collect(Collectors.toList());
        QueryWrapper<AttrEntity> attrId = new QueryWrapper<AttrEntity>();
        //只查出基础类型的属性
        attrId.eq("attr_type", ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode());
        //保证attrids有值
        if(attrIds.size() > 0){
            attrId.notIn("attr_id", attrIds);
        }
        String key = (String) params.get("key");
        if(!StringUtils.isEmpty(key)){
            attrId.like("attr_name", key).or().eq("attr_id", key);
        }
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                attrId
        );

        return new PageUtils(page);
    }

    //获取当前分组关联的所有属性
    @Override
    public List<AttrEntity> attrRelation(Long attrgroupId) {
        List<AttrAttrgroupRelationEntity> attrAttrgroupRelationEntities = relationDao.selectList(
                new QueryWrapper<AttrAttrgroupRelationEntity>()
                        .eq("attr_group_id", attrgroupId)
        );
        //查询属性id对应的实体
        List<Long> attrIds = attrAttrgroupRelationEntities.stream().map(AttrAttrgroupRelationEntity::getAttrId).collect(Collectors.toList());
        if(attrIds == null || attrIds.size() == 0){
            return null;
        }
        Collection<AttrEntity> attrEntities = this.listByIds(attrIds);

        return (List<AttrEntity>) attrEntities;
    }

    @Override
    public List<ProductAttrValueEntity> baseAttrListForSpu(Long spuId) {
        List<ProductAttrValueEntity> baseAttrListForSpu = productAttrValueService.AttrListForSpu(spuId);

        return baseAttrListForSpu;
    }

    @Override
    public List<Long> selectSearchAttrIds(List<Long> attrIds) {
        return baseMapper.selectSearchAttrIds(attrIds);
    }


}
