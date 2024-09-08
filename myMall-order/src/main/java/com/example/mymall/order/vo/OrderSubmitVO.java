package com.example.mymall.order.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 封装订单提交数据
 * 选中商品无需提交,直接从redis选中的获取即可
 * 用户相关信息,去session取就行了
 */
@Data
public class OrderSubmitVO {
    /**
     * 收货地址id
     */
    private Long addrId;
    /**
     * 支付方式
     */
    private Integer payType;
    //TODO 优惠,发票
    /**
     * 防重令牌
     */
    private String orderToken;
    /**
     * 应付价格
     * 用来对比,可以提醒用户价格变化了
     */
    private BigDecimal payPrice;
    /**
     * 订单备注
     */
    private String note;
}
