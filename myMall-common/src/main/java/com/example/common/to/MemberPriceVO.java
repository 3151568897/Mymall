package com.example.common.to;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MemberPriceVO {
    private Long id;

    /**
     * 会员等级名
     */
    private String name;

    private BigDecimal price;
}
