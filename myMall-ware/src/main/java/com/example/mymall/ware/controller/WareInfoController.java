package com.example.mymall.ware.controller;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;

import com.example.common.to.FareTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.mymall.ware.entity.WareInfoEntity;
import com.example.mymall.ware.service.WareInfoService;
import com.example.common.utils.PageUtils;
import com.example.common.utils.R;



/**
 * 仓库信息
 *
 * @author wupeng
 * @email wupeng@gmail.com
 * @date 2024-08-10 16:10:48
 */
@RestController
@RequestMapping("ware/wareinfo")
public class WareInfoController {
    @Autowired
    private WareInfoService wareInfoService;

    /**
     * 根据用户地址计算运费
     * @param addrId
     * @return
     */
    @GetMapping("/fare")
    public R getFare(@RequestParam("addrId") Long addrId){
        FareTO fare = wareInfoService.getFare(addrId);

        return R.ok().setData(fare);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = wareInfoService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		WareInfoEntity wareInfo = wareInfoService.getById(id);

        return R.ok().put("wareInfo", wareInfo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody WareInfoEntity wareInfo){
		wareInfoService.save(wareInfo);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody WareInfoEntity wareInfo){
		wareInfoService.updateById(wareInfo);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		wareInfoService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
