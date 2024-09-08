package com.example.mymall.order.feign;

import com.example.common.to.MemberAddressVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "myMall-member")
public interface MemberFeignService {

    /**
     * 获取用户地址
     * @param memberId
     * @return
     */
    @GetMapping("/member/memberreceiveaddress/{memberId}/address")
    public List<MemberAddressVO> getAddress(@PathVariable("memberId") Long memberId);
}
