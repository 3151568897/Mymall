package com.example.mymall.order.to;

import com.example.mymall.order.entity.OrderEntity;
import com.example.mymall.order.entity.OrderItemEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderCreateTO {
    private OrderEntity order;

    private List<OrderItemEntity> orderItems;

    private BigDecimal payPrice;//应付价格

    private BigDecimal fare;//运费
}
