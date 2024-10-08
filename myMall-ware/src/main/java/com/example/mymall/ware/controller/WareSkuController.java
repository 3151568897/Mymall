package com.example.mymall.ware.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.example.common.exception.BaseCodeEnume;
import com.example.common.exception.NoStockException;
import com.example.common.to.SkuHasStockTO;
import com.example.mymall.ware.vo.LockStockResult;
import com.example.mymall.ware.vo.WareSkuLockVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.mymall.ware.entity.WareSkuEntity;
import com.example.mymall.ware.service.WareSkuService;
import com.example.common.utils.PageUtils;
import com.example.common.utils.R;



/**
 * 商品库存
 *
 * @author wupeng
 * @email wupeng@gmail.com
 * @date 2024-08-10 16:10:48
 */
@RestController
@RequestMapping("ware/waresku")
public class WareSkuController {
    @Autowired
    private WareSkuService wareSkuService;

    /**
     * 查询sku是否有库存
     */
    @PostMapping("/lock/order")
    public R orderLockStock(@RequestBody WareSkuLockVO locks){
        //skuId stock
        try {
            wareSkuService.orderLockStock(locks);
            return R.ok();
        }catch (NoStockException e){
            // 手动回滚事务
//            GlobalTransactionContext.reload(RootContext.getXID()).rollback();
            return R.error(BaseCodeEnume.NO_STOCK_EXCEPTION.getCode(), BaseCodeEnume.NO_STOCK_EXCEPTION.getMsg());
        }
    }

    /**
     * 查询sku是否有库存
     */
    @PostMapping("/hasstock")
    public R getSkuHasStock(@RequestBody List<Long> skuIds){
        //skuId stock
        List<SkuHasStockTO> vos = wareSkuService.getSkuHasStock(skuIds);

        return R.ok().setData(vos);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = wareSkuService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		WareSkuEntity wareSku = wareSkuService.getById(id);

        return R.ok().put("wareSku", wareSku);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody WareSkuEntity wareSku){
		wareSkuService.save(wareSku);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody WareSkuEntity wareSku){
		wareSkuService.updateById(wareSku);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		wareSkuService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
