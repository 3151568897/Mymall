package com.example.mymall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.common.utils.PageUtils;
import com.example.mymall.order.entity.OrderEntity;
import com.example.mymall.order.vo.OrderConfirmVO;
import com.example.mymall.order.vo.OrderSubmitResponseVO;
import com.example.mymall.order.vo.OrderSubmitVO;

import java.util.Map;

/**
 * 订单
 *
 * @author wupeng
 * @email wupeng@gmail.com
 * @date 2024-08-10 16:07:09
 */
public interface OrderService extends IService<OrderEntity> {

    PageUtils queryPage(Map<String, Object> params);

    OrderConfirmVO confirmOrder();

    /**
     * 下单提交
     * @param vo
     * @return
     */
    OrderSubmitResponseVO submitOrder(OrderSubmitVO vo);

    OrderEntity getOrderByOrderSn(String orderSn);
}

