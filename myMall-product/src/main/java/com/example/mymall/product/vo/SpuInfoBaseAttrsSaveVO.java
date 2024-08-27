package com.example.mymall.product.vo;


import lombok.Data;

@Data
public class SpuInfoBaseAttrsSaveVO {
    Long attrId;
    String attrValues;
    /**
     * 是否快速展示
     */
    int showDesc;
}
