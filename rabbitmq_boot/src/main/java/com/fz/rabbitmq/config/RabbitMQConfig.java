package com.fz.rabbitmq.config;

import com.rabbitmq.client.ConfirmCallback;
import com.rabbitmq.client.Return;
import com.rabbitmq.client.ReturnCallback;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.HashMap;

/**
 * @author Administrator
 * @date 2021/12/18 17:19
 * @description rabbitmq_boot
 */

@Configuration
public class RabbitMQConfig implements RabbitTemplate.ConfirmCallback,RabbitTemplate.ReturnCallback {

    @Autowired
    private RabbitTemplate rabbitTemplate;

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
//        // 对整个队列设置TTL（消息过期时间，               5000毫秒 （一般使用死信队列来接受）
//        args.put("x-message-ttl", 5000);
//        // 设置队列消息的最大个数
//        args.put("x-max-length", 5);
//        // 队列创建了之后，再修改其参数，会报错，需要删除队列重新创建
//        // 设置死信队列的交换机
//        args.put("x-dead-letter-exchange-message-ttl", "");
//        // 设置路由key，fanout模式不需要设置
//        args.put("x-dead-letter-routing-key", "");

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

    @Bean
    public Binding topicBindingEmail() {
        return BindingBuilder.bind(emailQueue()).to(topicExchange()).with("#.email");
    }


    @PostConstruct
    public void init(){
        rabbitTemplate.setConfirmCallback(this);            //指定 ConfirmCallback
        /**
         * true：交换机无法将消息进行路由时，会将该消息返回给生产者
         * false：如果发现消息无法进行路由，则直接丢弃
         */
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setReturnCallback(this);             //指定 ReturnCallback
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());


    }

    @Bean
    public MessageConverter messageConverter() {
        // 可以序列化为json
        return new Jackson2JsonMessageConverter();
    }


    /**
     * 交换机不管是否收到消息的一个回调方法
     *
     * @param correlationData 消息相关数据
     * @param ack             交换机是否收到消息
     * @param cause           为收到消息的原因
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        System.out.println("消息唯一标识："+correlationData);
        System.out.println("确认结果："+ack);
        System.out.println("失败原因："+cause);
    }


    /**
     * 当消息无法路由的时候的回调方法
     * @param message 消息主体
     * @param replyCode 消息代码
     * @param replyText 描述
     * @param exchange 消息使用的交换器
     * @param routingKey 消息使用的路由键
     */
    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
        System.out.println("消息主体 message : "+message);
        System.out.println("消息代码 message : "+replyCode);
        System.out.println("描述："+replyText);
        System.out.println("消息使用的交换器 exchange : "+exchange);
        System.out.println("消息使用的路由键 routing : "+routingKey);

    }
}
