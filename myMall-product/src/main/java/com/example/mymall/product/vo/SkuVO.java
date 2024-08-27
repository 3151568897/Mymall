package com.example.mymall.product.vo;

import com.example.common.to.MemberPriceVO;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SkuVO {
    /**
     * 销售属性集合
     */
    private List<SkuAttrsVO> attr;

    private String skuName;

    private BigDecimal price;
    /**
     * sku的标题
     */
    private String skuTitle;

    private String skuSubtitle;

    private List<SkuImagesVO> images;

    private List<String> descar;
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
