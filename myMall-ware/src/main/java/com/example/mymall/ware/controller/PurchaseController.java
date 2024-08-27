package com.example.mymall.ware.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.example.mymall.ware.vo.PurchaseDoneVO;
import com.example.mymall.ware.vo.PurchaseMergeVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.mymall.ware.entity.PurchaseEntity;
import com.example.mymall.ware.service.PurchaseService;
import com.example.common.utils.PageUtils;
import com.example.common.utils.R;



/**
 * 采购信息
 *
 * @author wupeng
 * @email wupeng@gmail.com
 * @date 2024-08-10 16:10:48
 */
@RestController
@RequestMapping("ware/purchase")
public class PurchaseController {
    @Autowired
    private PurchaseService purchaseService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = purchaseService.queryPage(params);

        return R.ok().put("page", page);
    }

    /**
     * 查询未领取的采购单
     */
    @RequestMapping("/unreceive/list")
    public R listWithUnreceive(@RequestParam Map<String, Object> params){
        PageUtils page = purchaseService.queryPageWithUnreceive(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		PurchaseEntity purchase = purchaseService.getById(id);

        return R.ok().put("purchase", purchase);
    }

    /**
     * /ware/purchase/merge
     */
    @PostMapping("/merge")
    public R merge(@RequestBody PurchaseMergeVO purchaseMergeVO){
        purchaseService.purchaseMerge(purchaseMergeVO);

        return R.ok();
    }

    /**
     * /ware/purchase/received
     * 领取采购单
     */
    @PostMapping("/received")
    public R received(@RequestBody List<Long> ids){
        purchaseService.received(ids);

        return R.ok();
    }

    /**
     * /ware/purchase/done
     * 完成采购
     */
    @PostMapping("/done")
    public R done(@RequestBody PurchaseDoneVO doneVO){
        purchaseService.done(doneVO);

        return R.ok();
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody PurchaseEntity purchase){
        purchase.setCreateTime(new Date());
        purchase.setUpdateTime(new Date());
		purchaseService.save(purchase);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody PurchaseEntity purchase){
		purchaseService.updateById(purchase);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		purchaseService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
