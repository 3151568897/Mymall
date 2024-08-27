package com.example.mymall.product.feign;

import com.example.common.to.SkuReductionTO;
import com.example.common.to.SpuBoundTO;
import com.example.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("mymall-coupon")
public interface CouponFeignService {

    @PostMapping("/coupon/spubounds/save")
    R saveSpuBounds(@RequestBody SpuBoundTO spuBoundTO);

    @PostMapping("/coupon/skufullreduction/saveinfo")
    R saveSkuReduction(@RequestBody SkuReductionTO skuReductionTO);
}
