package com.fz.rabbitmq;

import com.fz.rabbitmq.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class RabbitmqBootApplicationTests {

    @Autowired
    private OrderService orderService;

    @Test
    void contextLoads() {
        for (int i = 0; i < 10; i++) {
//            orderService.makeOrderByFanout((long) i, (long) i, i);
//            orderService.makeOrderByDirect();
            orderService.makeOrderByTopic();
        }
    }

}
