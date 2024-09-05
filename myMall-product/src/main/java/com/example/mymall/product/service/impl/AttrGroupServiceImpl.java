package com.example.mymall.product.service.impl;

import com.example.mymall.product.dao.AttrAttrgroupRelationDao;
import com.example.mymall.product.dao.AttrDao;
import com.example.mymall.product.entity.AttrAttrgroupRelationEntity;
import com.example.mymall.product.entity.AttrEntity;
import com.example.mymall.product.entity.ProductAttrValueEntity;
import com.example.mymall.product.service.AttrAttrgroupRelationService;
import com.example.mymall.product.service.AttrService;
import com.example.mymall.product.service.ProductAttrValueService;
import com.example.mymall.product.vo.AttrGroupWithAttrsVO;
import com.example.mymall.product.vo.SkuItemVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    @Autowired
    private ProductAttrValueService productAttrValueService;

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

    @Override
    public List<SkuItemVO.SpuItemAttrGroupVO> getAttrGroupWithAttrsBySpuId(Long spuId, Long catalogId) {
        //1.查出当前spu对应的所有属性组，以及当前属性组的属性对应的值
        List<SkuItemVO.SpuItemAttrGroupVO> attrGroupVOS = new ArrayList<>();
        //1.1获取当前spu有多少对应的属性分组
        List<AttrGroupWithAttrsVO> attrGroupByCatelogId = this.getAttrGroupWithAttrsByCatelogId(catalogId);
        //1.2获取spu对应的所有属性 并且将id封装为list
        List<ProductAttrValueEntity> attrValueBySpuId = productAttrValueService.list(new QueryWrapper<ProductAttrValueEntity>().eq("spu_id", spuId));
        List<Long> attrIds = attrValueBySpuId.stream().map(ProductAttrValueEntity::getAttrId).collect(Collectors.toList());
        //1.3遍历每个属性分组 根据attrIds获取对应的分组和属性信息
        for (AttrGroupWithAttrsVO attrGroupWithAttrsVO : attrGroupByCatelogId) {
            SkuItemVO.SpuItemAttrGroupVO attrGroupVO = new SkuItemVO.SpuItemAttrGroupVO();
            //分组名
            attrGroupVO.setGroupName(attrGroupWithAttrsVO.getAttrGroupName());
            List<SkuItemVO.SpuBaseAttrsVO> attrs = new ArrayList<>();
            //分组对应的属性
            if(attrGroupWithAttrsVO.getAttrs() != null && attrGroupWithAttrsVO.getAttrs().size() > 0){
                for(AttrEntity attrEntity : attrGroupWithAttrsVO.getAttrs()){
                    //属性
                    SkuItemVO.SpuBaseAttrsVO spuBaseAttrsVO = new SkuItemVO.SpuBaseAttrsVO();
                    if(attrIds.contains(attrEntity.getAttrId())){
                        spuBaseAttrsVO.setAttrName(attrEntity.getAttrName());
                        //获取属性值
                        for (ProductAttrValueEntity productAttrValueEntity : attrValueBySpuId) {
                            if(attrEntity.getAttrId().equals(productAttrValueEntity.getAttrId())){
                                spuBaseAttrsVO.setAttrValues(productAttrValueEntity.getAttrValue());
                                attrs.add(spuBaseAttrsVO);
                                break;
                            }
                        }
                    }
                }
            }
            if(attrs.size() > 0){
                attrGroupVO.setAttrs(attrs);
                attrGroupVOS.add(attrGroupVO);
            }
        }

        return attrGroupVOS;
    }

}
