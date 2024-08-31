package com.example.mymall.product.service.impl;

import com.example.mymall.product.service.CategoryBrandRelationService;
import com.example.mymall.product.vo.Catalog2VO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.utils.PageUtils;
import com.example.common.utils.Query;

import com.example.mymall.product.dao.CategoryDao;
import com.example.mymall.product.entity.CategoryEntity;
import com.example.mymall.product.service.CategoryService;
import org.springframework.transaction.annotation.Transactional;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Autowired
    CategoryDao categoryDao;
    @Autowired
    CategoryBrandRelationService categoryBrandRelationService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree() {
        //查出所有
        List<CategoryEntity> entities = baseMapper.selectList(null);

        //2 组装
        //2.1 找出一级分类
        List<CategoryEntity> level1Menus = entities.stream().filter((categoryEntity) -> {
            return categoryEntity.getParentCid() == 0;
        }).map(menu -> {
            //找到子菜单
            menu.setChildren(getChildrens(menu, entities));
            return menu;
        }).sorted(Comparator.comparingInt(menu -> (menu.getSort() == null ? 0 : menu.getSort()))).collect(Collectors.toList());


        return level1Menus;
    }

    @Override
    public void removeMenuByIds(List<Long> list) {
        //TODO 1.检查该分类是否有被引用

        //逻辑删除
        baseMapper.deleteBatchIds(list);
    }

    @Override
    public Long[] findCatelogPath(Long catelogId) {
        List<Long> paths = new ArrayList<>();
        List<Long> parentPath = findParentPath(catelogId, paths);

        return parentPath.toArray(new Long[parentPath.size()]);
    }

    @Override
    @Transactional
    public void updateCascade(CategoryEntity category) {
        //更新其他冗余表
        this.updateById(category);
        if(!StringUtils.isEmpty(category.getName())){
            categoryBrandRelationService.updateCategory(category.getCatId(), category.getName());
        }
    }

    @Override
    public List<CategoryEntity> getLevel1Categorys() {
        return baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("cat_level", 1));
    }

    @Override
    public Map<String, List<Catalog2VO>> getCatalogJson() {

        //查出所有一级分类
        List<CategoryEntity> level1Categorys = getLevel1Categorys();

        //封装数据
        Map<String, List<Catalog2VO>> parentCid = level1Categorys.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            //查询二级分类
            List<CategoryEntity> categoryEntities = baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", v.getCatId()));

            //封装上面的结果
            List<Catalog2VO> catalog2VOS = null;
            if (categoryEntities != null && categoryEntities.size() > 0) {
                catalog2VOS = categoryEntities.stream().map(item -> {
                    Catalog2VO catalog2VO = new Catalog2VO(v.getCatId(), null, item.getName(), item.getCatId());
                    //查询三级分类
                    List<CategoryEntity> level3Entities = baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", item.getCatId()));
                    if(level3Entities != null && level3Entities.size() > 0){
                        //封装成指定格式
                        List<Catalog2VO.Catalog3VO> catalog3VOS = level3Entities.stream().map(level3Entity -> {

                            return new Catalog2VO.Catalog3VO(item.getCatId(), level3Entity.getName(), level3Entity.getCatId());
                        }).collect(Collectors.toList());

                        catalog2VO.setCatalog3List(catalog3VOS);
                    }
                    return catalog2VO;
                }).collect(Collectors.toList());
            }

            return catalog2VOS;
        }));

        return parentCid;
    }

    private List<Long> findParentPath(Long catelogId, List<Long> paths){
        CategoryEntity byId = this.getById(catelogId);
        paths.add(byId.getCatId());

        if(byId.getParentCid() != 0){
            findParentPath(byId.getParentCid(), paths);
        }
        Collections.reverse(paths);
        return paths;
    }

    private List<CategoryEntity> getChildrens(CategoryEntity root, List<CategoryEntity> all){
        List<CategoryEntity> childrens = all.stream().filter(categoryEntity -> {
            return Objects.equals(categoryEntity.getParentCid(), root.getCatId());
        }).map(menu ->{
            //找到子菜单的子菜单
            menu.setChildren(getChildrens(menu, all));
            return menu;
        }).sorted((menu1, menu2)->{
            return (menu1.getSort() == null?0:menu1.getSort()) - (menu2.getSort()==null?0:menu2.getSort());
        }).collect(Collectors.toList());

        return childrens;

    }

}
