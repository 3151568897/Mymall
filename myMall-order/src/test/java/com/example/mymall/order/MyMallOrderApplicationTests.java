package com.example.mymall.order;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MyMallOrderApplicationTests {

    @Autowired
    AmqpAdmin amqpAdmin;

    @Test
    void contextLoads() {
    }

    @Test
    void createExchange() {
        System.out.println(amqpAdmin);
    }


}
