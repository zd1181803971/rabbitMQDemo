package com.fz.rabbitmq.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * @author Administrator
 * @date 2021/12/18 17:03
 * @description rabbitmq_boot
 */
@Service
public class OrderService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void makeOrderByFanout(Long userId, Long productId, int num) {
        // 1、模拟用户下单
        String orderId = UUID.randomUUID().toString();
        //………… 省略业务逻辑
        // 发送订单消息给 rabbitMQ
        rabbitTemplate.convertAndSend("fanout_order_exchange", "", orderId);
    }

    public void makeOrderByDirect() {
        String orderId = UUID.randomUUID().toString();
        rabbitTemplate.convertAndSend("direct_order_exchange", "sms", orderId);
    }

    public void makeOrderByTopic() {
        String orderId = UUID.randomUUID().toString();
        // 设置某条消息TTL
        rabbitTemplate.convertAndSend("topic_order_exchange", "sms.email", orderId, (message) -> {
            message.getMessageProperties().setExpiration("5000");
            message.getMessageProperties().setContentEncoding("UTF-8");
            return message;
        });
    }

}
