package com.example.common.to;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SpuBoundTO {

    private Long spuId;
    /**
     * 成长值奖励
     */
    private BigDecimal growBounds;
    /**
     * 购买值奖励
     */
    private BigDecimal buyBounds;
}
