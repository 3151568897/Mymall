package com.example.mymall.order.config;

import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.amqp.RabbitTemplateConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import javax.annotation.PostConstruct;

@Configuration
public class MyRabbitConfig{

    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }

    /**
     * 定制rabbitTemplate
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, RabbitTemplateConfigurer configurer) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        // 使用 RabbitTemplateConfigurer 来应用 Spring Boot 的默认配置
        configurer.configure(template, connectionFactory);
        // 自定义配置
        //rabbitmq发送消息失败的回调函数
        template.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            /**
             *
             * @param correlationData 消息的唯一id
             * @param ack 是否成功收到
             * @param cause 失败原因
             */
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                System.out.println(correlationData+" "+ack+" "+cause);
            }
        });
        //rabbitmq发送消息抵达队列的回调函数
        template.setReturnsCallback(new RabbitTemplate.ReturnsCallback() {
            /**
             * 成功不会触发
             * @param returnedMessage
             * 这个message封装的回调内容
             */
            @Override
            public void returnedMessage(ReturnedMessage returnedMessage) {
                System.out.println(returnedMessage);
            }
        });
        return template;
    }
}
