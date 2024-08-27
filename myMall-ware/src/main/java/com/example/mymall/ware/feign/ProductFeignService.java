package com.example.mymall.ware.feign;

import com.example.common.to.SkuReductionTO;
import com.example.common.to.SpuBoundTO;
import com.example.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("mymall-product")
@Component
public interface ProductFeignService {

    @PostMapping("/product/skuinfo/info/{skuId}")
    R getSkuInfo(@PathVariable("skuId") Long skuId);
}
