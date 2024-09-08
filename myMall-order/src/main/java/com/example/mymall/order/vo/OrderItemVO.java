package com.example.mymall.order.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderItemVO {
    private Long skuId;

    private Integer count;

    private Boolean check;

    private String title;

    private String defaultImage;

    private BigDecimal price;

    private BigDecimal totalPrice;
    //TODO 查询库存状态
    private Boolean hasStock = true;
    //TODO 查询商品重量
    private BigDecimal weight = BigDecimal.valueOf(0.095);

    private List<String> skuSaleVO;
}
