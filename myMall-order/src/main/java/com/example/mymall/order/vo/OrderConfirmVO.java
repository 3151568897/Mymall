package com.example.mymall.order.vo;

import com.example.common.to.MemberAddressVO;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.BitSet;
import java.util.List;

/**
 * 订单确认页需要的数据
 */
@ToString
public class OrderConfirmVO {
    //收货地址
    @Getter @Setter
    List<MemberAddressVO> address;
    //所有选中的购物项
    @Getter @Setter
    List<OrderItemVO> items;
    //优惠价信息
    @Getter @Setter
    Integer integration; //积分信息

    //防重令牌
    @Getter @Setter
    String orderToken;

    private BigDecimal total;//订单总额

    private BigDecimal payPrice; //应付价格

    private BigDecimal totalWeight; //商品总重量

    private Integer count; //商品总数量


    public Integer getCount() {
        if(items == null || items.size() == 0) {
            return 0;
        }
        return this.items.stream().mapToInt(OrderItemVO::getCount).sum();
    }

    public BigDecimal getTotal() {
        if(items == null || items.size() == 0) {
            return BigDecimal.ZERO;
        }
        return this.items.stream().map(OrderItemVO::getTotalPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getPayPrice() {
        if(items == null || items.size() == 0) {
            return BigDecimal.ZERO;
        }
        //TODO 减去减免价格
        return this.items.stream().map(OrderItemVO::getTotalPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalWeight() {
        if(items == null || items.size() == 0) {
            return BigDecimal.ZERO;
        }
        return this.items.stream().map(OrderItemVO::getWeight).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
