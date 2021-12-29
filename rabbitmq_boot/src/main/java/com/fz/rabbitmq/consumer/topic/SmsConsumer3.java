package com.fz.rabbitmq.consumer.topic;

import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Component;

/**
 * @author Administrator
 * @date 2021/12/18 17:35
 * @description rabbitmq_boot
 */

//@Component
public class SmsConsumer3 {

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "sms.topic.queue",durable = "true",exclusive = "false",autoDelete = "false"),
            exchange = @Exchange(value = "topic_order_exchange",type = ExchangeTypes.TOPIC),
            key = "#.sms.*"
    ))
    @RabbitHandler
    public void reMessage(String message) {
        System.out.println("SmsConsumer::");
        System.out.println(message);
    }
}
