package com.dzu.rabbitmq.driect;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;


/**
 * @author Administrator
 * @date 2021/12/17 13:35
 * @description 路由模式 生产者
 */
public class Produce {
    public static void main(String[] args) {

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

            // 4、准备消息内容
            String message = "Hello rabbitMQ!";

            // 5 准备交换机
            String exchange = "amq.direct";
            // 5.1 准备 routingKey
            String routingKey = "user";

            // 6、发送消息给队列queue
            /**
             * 1、交换机  ==》可以存在没有交换机的队列吗？？ 不可能，虽然没有指定，但是存在一个默认的交换机。 消息一定是通过交换机传递给队列的
             * 2、队列、路由key
             * 3、消息的状态控制，其他属性
             * 4、消息主体
             */
            channel.basicPublish(exchange, routingKey, null, message.getBytes());

            System.out.println("direct schemas send success");
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
