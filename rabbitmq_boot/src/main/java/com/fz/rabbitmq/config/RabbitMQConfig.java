package com.fz.rabbitmq.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
        return new Queue("sms.fanout.queue", true, false, false);
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
