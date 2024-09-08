package com.example.common.to;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class FareTO {

    private BigDecimal fare;
    /**
     * 收货人地址信息
     */
    private MemberAddressVO address;
}
