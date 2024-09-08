package com.example.mymall.ware.vo;

import lombok.Data;

@Data
public class LockStockResult {

    private Long skuId;//商品id

    private Integer num;//锁定了几件

    private Boolean locked;//锁定是否成功
}
