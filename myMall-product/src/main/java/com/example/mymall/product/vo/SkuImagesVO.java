package com.example.mymall.product.vo;

import lombok.Data;

@Data
public class SkuImagesVO {

    private String imgUrl;
    /**
     * 是不是默认图片 （0 - 不是，1 - 是）
     */
    private int defaultImg;
}
