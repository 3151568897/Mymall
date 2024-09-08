package com.example.mymall.order.feign;

import com.example.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

@FeignClient(name = "myMall-product")
public interface ProductFeignService {

    /**
     * 获取当时商品的价格
     */
    @RequestMapping("/product/skuinfo/{skuId}/price")
    public BigDecimal getPrice(@PathVariable("skuId") Long skuId);

    @GetMapping("/product/spuinfo/skuId/{skuId}")
    public R getSpuInfoBySkuId(@RequestParam("skuId") Long skuId);

    @RequestMapping("/product/brand/info/{brandId}")
    public R getBrandInfo(@PathVariable("brandId") Long brandId);
}
