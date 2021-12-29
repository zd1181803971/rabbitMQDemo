package com.fz.rabbitmq.consumer.direct;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @author Administrator
 * @date 2021/12/18 17:35
 * @description rabbitmq_boot
 */

//@RabbitListener(queues = "email.fanout.queue")
@Component
public class EmailConsumer2 {

    @RabbitHandler
    public void reMessage(byte[] message) {
        System.out.println("EmailConsumer::::::::::::::");
        System.out.println(message);
    }
}
