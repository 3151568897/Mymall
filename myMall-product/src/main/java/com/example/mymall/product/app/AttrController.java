package com.example.mymall.product.app;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.example.mymall.product.entity.ProductAttrValueEntity;
import com.example.mymall.product.service.ProductAttrValueService;
import com.example.mymall.product.vo.AttrVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    private ProductAttrValueService productAttrValueService;

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
     * /product/attr/base/listforspu/{spuId}
     * 获取spu规格
     */
    @RequestMapping("/base/listforspu/{spuId}")
    public R attrListForSpu(@PathVariable("spuId") Long spuId){
        List<ProductAttrValueEntity> attrEntities = attrService.baseAttrListForSpu(spuId);

        return R.ok().put("data", attrEntities);
    }

    /**
     * /product/attr/update/{spuId}
     * 修改商品规格
     */
    @PostMapping("/update/{spuId}")
    public R updateSpuAttr(@RequestBody List<ProductAttrValueEntity> productAttr, @PathVariable("spuId") Long spuId){
        productAttrValueService.updateSpuAttr(spuId, productAttr);

        return R.ok();
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
