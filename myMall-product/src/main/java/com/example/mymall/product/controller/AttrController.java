package com.example.mymall.product.controller;

import java.util.Arrays;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.mymall.product.entity.AttrAttrgroupRelationEntity;
import com.example.mymall.product.service.AttrAttrgroupRelationService;
import com.example.mymall.product.service.CategoryService;
import com.example.mymall.product.vo.AttrVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.mymall.product.entity.AttrEntity;
import com.example.mymall.product.service.AttrService;
import com.example.common.utils.PageUtils;
import com.example.common.utils.R;



/**
 * 商品属性
 *
 * @author wupeng
 * @email wupeng@gmail.com
 * @date 2024-08-09 18:21:35
 */
@RestController
@RequestMapping("product/attr")
public class AttrController {
    @Autowired
    private AttrService attrService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private AttrAttrgroupRelationService attrAttrgroupRelationService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = attrService.queryPage(params);

        return R.ok().put("page", page);
    }

    /**
     * 列表 同时用于sale和base
     */
    @RequestMapping("/{attrType}/list/{catelogId}")
    public R salelistByCatelogId(@RequestParam Map<String, Object> params,
                                 @PathVariable("catelogId") Long catelogId,
                                 @PathVariable("attrType") String attrType){
        PageUtils page = attrService.queryPage(params, catelogId, attrType);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrId}")
    public R info(@PathVariable("attrId") Long attrId){
		AttrVO attrVO = attrService.getInfo(attrId);


        return R.ok().put("attr", attrVO);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody AttrVO attr){
        attrService.saveAttr(attr);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody AttrVO attr){
		attrService.updateAttr(attr);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] attrIds){
		attrService.removeByIdsWithoutCascade(Arrays.asList(attrIds));

        return R.ok();
    }

}
