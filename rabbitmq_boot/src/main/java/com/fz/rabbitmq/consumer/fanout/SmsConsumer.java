package com.fz.rabbitmq.consumer.fanout;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * @author Administrator
 * @date 2021/12/18 17:35
 * @description rabbitmq_boot
 */

//@RabbitListener(queues = "sms.fanout.queue")
@Component
public class SmsConsumer {

    @RabbitHandler
    public void reMessage(String message) {
        System.out.println("SmsConsumer::");
        System.out.println(message);
    }
}
