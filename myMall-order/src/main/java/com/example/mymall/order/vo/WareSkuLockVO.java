package com.example.mymall.order.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class WareSkuLockVO {

    private String orderSn;//订单好

    private List<OrderItemVO> locks;//需要锁定的库存


}
