package com.fz.rabbitmq;

import com.fz.rabbitmq.entity.Student;
import com.fz.rabbitmq.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;

@SpringBootTest
class RabbitmqBootApplicationTests {

    @Autowired
    private OrderService orderService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    void contextLoads() {
        ArrayList<Student> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
//            orderService.makeOrderByFanout((long) i, (long) i, i);
//            orderService.makeOrderByDirect();
//            orderService.makeOrderByTopic();
            Student student = new Student(123, "张三", 14);
            list.add(student);
        }
        rabbitTemplate.convertAndSend("direct_order_exchange","sms",list);
    }

}
