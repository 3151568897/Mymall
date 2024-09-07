package com.example.mymall.order;

import com.example.mymall.order.entity.OrderItemEntity;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MyMallOrderApplicationTests {

    @Autowired
    AmqpAdmin amqpAdmin;
    @Autowired
    RabbitTemplate rabbitTemplate;

    @Test
    void contextLoads() {
    }

    @Test
    void sendMessageTest() {
        OrderItemEntity itemEntity = new OrderItemEntity();
        itemEntity.setOrderSn("sdafsadf");
        rabbitTemplate.convertAndSend("Hello-java-exchange", "hello.java", itemEntity);
    }

    @Test
    void createExchange() {
        System.out.println(rabbitTemplate);
    }


}
