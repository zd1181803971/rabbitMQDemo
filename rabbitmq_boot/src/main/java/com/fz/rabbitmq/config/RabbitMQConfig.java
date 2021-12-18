package com.fz.rabbitmq.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

/**
 * @author Administrator
 * @date 2021/12/18 17:19
 * @description rabbitmq_boot
 */

@Configuration
public class RabbitMQConfig {

    @Bean
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange("fanout_order_exchange", true, false);
    }

    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange("direct_order_exchange", true, false);
    }

    public TopicExchange topicExchange() {
        return new TopicExchange("topic_order_exchange", true, false);
    }

    @Bean
    public Queue emailQueue() {
        return new Queue("email.fanout.queue", true, false, false);
    }

    @Bean
    public Queue smsQueue() {
        HashMap<String, Object> args = new HashMap<>();
        // 对整个队列设置TTL（消息过期时间，               5000毫秒 （一般使用死信队列来接受）
        args.put("x-message-ttl", 5000);
        // 设置队列消息的最大个数
        args.put("x-max-length", 5);
        // 队列创建了之后，再修改其参数，会报错，需要删除队列重新创建
        // 设置死信队列的交换机
        args.put("x-dead-letter-exchange-message-ttl", "");
        // 设置路由key，fanout模式不需要设置
        args.put("x-dead-letter-routing-key", "");

        return new Queue("sms.fanout.queue", true, false, false, args);
    }

    @Bean
    public Binding fanoutBindingEmail() {
        return BindingBuilder.bind(emailQueue()).to(fanoutExchange());
    }

    @Bean
    public Binding fanoutBindingSms() {
        return BindingBuilder.bind(smsQueue()).to(fanoutExchange());
    }

    @Bean
    public Binding directBindingSms() {
        return BindingBuilder.bind(smsQueue()).to(directExchange()).with("sms");
    }

    public Binding topicBindingEmail() {
        return BindingBuilder.bind(emailQueue()).to(topicExchange()).with("#.email");
    }
}
