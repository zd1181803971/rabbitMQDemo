package com.fz.rabbitmq.consumer.fanout;

import com.fz.rabbitmq.entity.Student;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;


/**
 * @author Administrator
 * @date 2021/12/18 17:35
 * @description rabbitmq_boot
 */

@RabbitListener(queues = "sms.fanout.queue")
@Component
public class EmailConsumer {

    @RabbitHandler
    public void reMessage(Message message, List<Student> student, Channel channel) {
        System.out.println("EmailConsumer::");
        System.out.println(student.size());
        System.out.println(student);
        System.out.println(message);
        try {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
