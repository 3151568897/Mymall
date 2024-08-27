package com.example.mymall.product.feign;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("mymall-member")
public interface memberFeignService {
}
