package com.example.mymall.order.feign;

import com.example.common.utils.R;
import com.example.mymall.order.vo.OrderItemVO;
import com.example.mymall.order.vo.WareSkuLockVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "myMall-ware")
public interface WareFeignService {

    /**
     * 获取用户购物车
     * @return
     */
    @GetMapping("/currentUserCartItems")
    public List<OrderItemVO> currentUserCartItems();
    /**
     * 获取用户选中的购物车
     * @return
     */
    @GetMapping("/currentUserCheckedCartItems")
    public List<OrderItemVO> currentUserCheckedCartItems();
    /**
     * 查询sku是否有库存
     */
    @PostMapping("/ware/waresku/hasstock")
    public R getSkusHasStock(@RequestBody List<Long> skuIds);

    @GetMapping("/ware/wareinfo/fare")
    public R getFare(@RequestParam("addrId") Long addrId);

    @PostMapping("/ware/waresku/lock/order")
    public R orderLockStock(@RequestBody WareSkuLockVO locks);
}
