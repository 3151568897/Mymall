package com.example.mymall.ware.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 整个购物车的内容
 * 需要计算属性,所有获取属性都要计算
 */
public class CartVO {

    List<CartItemVO> items;

    /**
     * 购物车商品数量
     */
    private Integer countNum;
    /**
     * 商品类型
     */
    private Integer countType;
    /**
     * 总价
     */
    private BigDecimal totalAmount;
    /**
     * 减免价
     */
    private BigDecimal reduce = new BigDecimal("0");

    public List<CartItemVO> getItems() {
        return items;
    }

    public void setItems(List<CartItemVO> items) {
        this.items = items;
    }

    public Integer getCountNum() {
        if(this.items != null && this.items.size() > 0){
            return this.items.stream().mapToInt(CartItemVO::getCount).sum();
        }

        return 0;
    }

    public Integer getCountType() {
        if(this.items != null && this.items.size() > 0){
            return this.items.size();
        }

        return 0;
    }

    public BigDecimal getTotalAmount() {
        if(this.items != null && this.items.size() > 0){
            BigDecimal decimal = this.items.stream().map(CartItemVO::getTotalPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
            //减去减免价格
            return decimal.subtract(this.getReduce());
        }

        return BigDecimal.ZERO;
    }

    public BigDecimal getReduce() {
        return reduce;
    }

    public void setReduce(BigDecimal reduce) {
        this.reduce = reduce;
    }
}
