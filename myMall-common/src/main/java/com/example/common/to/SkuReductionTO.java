package com.example.common.to;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SkuReductionTO {
    private Long skuId;
    /**
     * 满fllcount个 就打discoutd折
     */
    private int fullCount;

    private BigDecimal discount;
    /**
     * 满减是否叠加其他优惠 1叠加 0不叠加
     */
    private int countStatus;
    /**
     * 满fullprice块钱,就减reducePrice块钱
     */
    private BigDecimal fullPrice;

    private BigDecimal reducePrice;
    /**
     * fullprice满减是否参加其他优惠 1参加 0不参加
     */
    private int priceStatus;
    /**
     * 会员价格信息
     */
    private List<MemberPriceVO> memberPrice;
}
