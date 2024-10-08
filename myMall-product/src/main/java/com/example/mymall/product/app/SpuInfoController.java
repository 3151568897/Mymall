package com.example.mymall.product.app;

import java.util.Arrays;
import java.util.Map;

import com.example.mymall.product.vo.SpuInfoSaveVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.mymall.product.entity.SpuInfoEntity;
import com.example.mymall.product.service.SpuInfoService;
import com.example.common.utils.PageUtils;
import com.example.common.utils.R;



/**
 * spu信息
 *
 * @author wupeng
 * @email wupeng@gmail.com
 * @date 2024-08-09 18:21:35
 */
@RestController
@RequestMapping("product/spuinfo")
public class SpuInfoController {
    @Autowired
    private SpuInfoService spuInfoService;

    /**
     * 根据skuid获取spu信息
     */
    @GetMapping("/skuId/{skuId}")
    public R getSpuInfoBySkuId(@RequestParam("skuId") Long skuId){
        SpuInfoEntity spuInfo = spuInfoService.getSpuInfoBySkuId(skuId);

        return R.ok().setData(spuInfo);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = spuInfoService.queryPageByCondition(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		SpuInfoEntity spuInfo = spuInfoService.getById(id);

        return R.ok().put("spuInfo", spuInfo);
    }

    /**
     * 新增商品
     */
    @RequestMapping("/save")
    public R save(@RequestBody SpuInfoSaveVO spuInfoSaveVO){
		spuInfoService.saveSpuInfo(spuInfoSaveVO);

        return R.ok();
    }

    /**
     * /product/spuinfo/{spuId}/up
     * 商品上架(要修改es的数据和商品状态)
     */
    @RequestMapping("/{spuId}/up")
    public R up(@PathVariable("spuId") Long spuId){
        spuInfoService.up(spuId);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody SpuInfoEntity spuInfo){
		spuInfoService.updateById(spuInfo);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		spuInfoService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
