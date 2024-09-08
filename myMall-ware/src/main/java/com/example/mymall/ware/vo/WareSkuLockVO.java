package com.example.mymall.ware.vo;

import lombok.Data;

import java.util.List;

@Data
public class WareSkuLockVO {

    private String orderSn;//订单好

    private List<OrderItemVO> locks;//需要锁定的库存


}
