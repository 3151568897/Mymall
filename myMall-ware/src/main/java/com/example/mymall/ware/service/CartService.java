package com.example.mymall.ware.service;

import com.example.mymall.ware.vo.CartItemVO;
import com.example.mymall.ware.vo.CartVO;

public interface CartService {
    /**
     * 添加商品到购物车
     * @param skuId
     * @param num
     * @return
     */
    CartItemVO addToCart(Long skuId, Integer num);

    //获取购物车中的某一个项
    CartItemVO getCartItem(Long skuId);

    CartVO getCart();

    /**
     * 清空购物车
     * @param cartKey
     */
    void clearCart(String cartKey);

    /**
     * 改变商品是否选中的状态
     * @param skuId
     * @param check
     */
    void checkItem(Long skuId, boolean check);

    /**
     * 改变购物车数量
     * @param skuId
     * @param num
     */
    void countItem(Long skuId, Integer num);

    /**
     * 删除购物车中商品
     * @param skuId
     */
    void deleteItem(Long skuId);
}
