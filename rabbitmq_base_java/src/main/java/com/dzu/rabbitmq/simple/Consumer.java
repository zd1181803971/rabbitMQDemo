package com.dzu.rabbitmq.simple;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;


/**
 * @author Administrator
 * @date 2021/12/17 13:35
 * @description 简答模式 消费者
 */
public class Consumer {
    public static void main(String[] args) {

        // 所有的中间件技术都是基于TCP/IP协议基础之上构建新型的协议规范，只不过rabbitMQ使用的AMQP协议

        // 1、 创建连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("121.5.224.141");
        factory.setPort(5672);
        factory.setUsername("admin");
        factory.setPassword("admin");
        factory.setVirtualHost("/");


        Connection connection = null;
        Channel channel = null;
        try {
            // 2、创建链接Connection
            connection = factory.newConnection("zdTest");
            // 3、通过链接获取通道Channel
            channel = connection.createChannel();

            // 4、定义消息列
            String queueName = "queue1";

            channel.basicConsume(queueName, true, (consumer, qwe) -> {
                System.out.println("接收到的消息：");
                System.out.println(consumer);
                System.out.println(new String(qwe.getBody()));
            }, (consumerTag) -> {
                System.out.println("接受失败");
                System.out.println(consumerTag);
            });

            System.out.println("consumer success");
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            // 7、关闭链接
            if (channel != null && channel.isOpen()) {
                try {
                    channel.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            // 8、关闭通道
            if (connection != null && connection.isOpen()) {
                try {
                    connection.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
