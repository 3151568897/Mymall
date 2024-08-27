package com.example.mymall.product.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.mymall.product.entity.BrandEntity;
import com.example.mymall.product.service.BrandService;
import com.example.mymall.product.service.CategoryService;
import com.example.mymall.product.vo.BrandRelationVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.mymall.product.entity.CategoryBrandRelationEntity;
import com.example.mymall.product.service.CategoryBrandRelationService;
import com.example.common.utils.PageUtils;
import com.example.common.utils.R;



/**
 * 品牌分类关联
 *
 * @author wupeng
 * @email wupeng@gmail.com
 * @date 2024-08-09 18:21:33
 */
@RestController
@RequestMapping("product/categorybrandrelation")
public class CategoryBrandRelationController {
    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = categoryBrandRelationService.queryPage(params);

        return R.ok().put("page", page);
    }

    /**
     * 根据品牌查询列表
     */
    @GetMapping("/catelog/list")
    public R listByBrandId(@RequestParam(value = "brandId", required = true) Long brandId){
        List<CategoryBrandRelationEntity> categoryBrandRelationEntities = categoryBrandRelationService.getBaseMapper()
                .selectList(new QueryWrapper<CategoryBrandRelationEntity>().eq("brand_id", brandId));

        return R.ok().put("data", categoryBrandRelationEntities);
    }
    /**
     * 获取分类关联的品牌
     */
    @GetMapping("/brands/list")
    public R listByCategoryId(@RequestParam(value = "catId", required = true) Long catId){
        List<BrandEntity> brands = categoryBrandRelationService.getBrandsByCatId(catId);
        List<BrandRelationVO> brandRelationVOList = new ArrayList<>();

        for (BrandEntity brandEntity: brands) {
            BrandRelationVO brandRelationVO = new BrandRelationVO();
            brandRelationVO.setBrandId(brandEntity.getBrandId());
            brandRelationVO.setBrandName(brandEntity.getName());
            brandRelationVOList.add(brandRelationVO);
        }

        return R.ok().put("data", brandRelationVOList);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		CategoryBrandRelationEntity categoryBrandRelation = categoryBrandRelationService.getById(id);

        return R.ok().put("categoryBrandRelation", categoryBrandRelation);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody CategoryBrandRelationEntity categoryBrandRelation){
        categoryBrandRelationService.saveDetail(categoryBrandRelation);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody CategoryBrandRelationEntity categoryBrandRelation){
		categoryBrandRelationService.updateById(categoryBrandRelation);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		categoryBrandRelationService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
