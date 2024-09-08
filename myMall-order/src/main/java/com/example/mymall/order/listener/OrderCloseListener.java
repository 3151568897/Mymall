package com.example.mymall.order.listener;

import com.example.common.constant.OrderStatusEnum;
import com.example.common.to.mq.OrderTO;
import com.example.mymall.order.entity.OrderEntity;
import com.example.mymall.order.service.OrderService;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Objects;

@RabbitListener(queues = "order.release.order.queue")
@Service
public class OrderCloseListener {

    @Autowired
    OrderService orderService;
    @Autowired
    RabbitTemplate rabbitTemplate;

    /**
     * 处理过期的订单
     */
    @RabbitHandler
    public void listener(OrderEntity order, Channel channel, Message message) throws IOException {
        System.out.println("收到过期的订单，订单：" + order);
        //查询当前订单的最新状态
        OrderEntity orderEntity = orderService.getById(order.getId());
        if(Objects.equals(orderEntity.getStatus(), OrderStatusEnum.CREATE_NEW.getCode())) {
            //如果订单状态是未支付,那么进行关闭
            OrderEntity update = new OrderEntity();
            update.setId(order.getId());
            update.setStatus(OrderStatusEnum.CANCLED.getCode());
            orderService.updateById(update);
            //订单关闭后,发给mq的库存关闭队列,进行库存解锁
            OrderTO orderTO = new OrderTO();
            BeanUtils.copyProperties(orderEntity, orderTO);
            rabbitTemplate.convertAndSend("order-event-exchange", "order.release.other.#", orderTO);
        }

        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }
}
