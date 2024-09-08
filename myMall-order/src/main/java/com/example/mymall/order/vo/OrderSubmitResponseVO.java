package com.example.mymall.order.vo;

import com.example.mymall.order.entity.OrderEntity;
import lombok.Data;

@Data
public class OrderSubmitResponseVO {

    private OrderEntity order;
    private Integer code; //错误状态码,0表示成功
    private String msg; //错误信息
}
