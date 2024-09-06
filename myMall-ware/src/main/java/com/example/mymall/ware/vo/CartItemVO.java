package com.example.mymall.ware.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 购物项的内容
 */
@Data
public class CartItemVO {
    private Long skuId;

    private Integer count;

    private Boolean check = true;

    private String title;

    private String defaultImage;

    private BigDecimal price;

    private BigDecimal totalPrice;

    private List<String> skuSaleVO;

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Boolean getCheck() {
        return check;
    }

    public void setCheck(Boolean check) {
        this.check = check;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDefaultImage() {
        return defaultImage;
    }

    public void setDefaultImage(String defaultImage) {
        this.defaultImage = defaultImage;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    /**
     * 计算当前总价
     * @return
     */
    public BigDecimal getTotalPrice() {
        return this.price.multiply(new BigDecimal(""+this.count));
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public List<String> getSkuSaleVO() {
        return skuSaleVO;
    }

    public void setSkuSaleVO(List<String> skuSaleVO) {
        this.skuSaleVO = skuSaleVO;
    }
}
