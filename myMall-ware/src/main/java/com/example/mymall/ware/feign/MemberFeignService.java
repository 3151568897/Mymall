package com.example.mymall.ware.feign;

import com.example.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@FeignClient("myMall-member")
public interface MemberFeignService {

    /**
     * 获取用户地址
     * @return
     */
    @RequestMapping("/member/memberreceiveaddress/info/{id}")
    public R getMemberAddressInfo(@PathVariable("id") Long id);
}
