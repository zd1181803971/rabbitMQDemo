package com.dzu.rabbitmq.all;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;


/**
 * @author Administrator
 * @date 2021/12/17 13:35
 * @description 代码声明交换机和队列
 */
public class ProduceDeclare {
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
            StringBuilder message = new StringBuilder("Hello rabbitMQ!");

            // 5、用代码声明一个交换机 ， 名称、类型、是否持久化（ broke重启交换机会不会丢失
            String exchangeName = "direct_order_exchange";
            channel.exchangeDeclare(exchangeName, "direct", true);

            // 6、声明队列
            channel.queueDeclare("queue5", true, false, false, null);
            channel.queueDeclare("queue6", true, false, false, null);
            channel.queueDeclare("queue7", true, false, false, null);

            // 7、 绑定
            channel.queueBind("queue5", exchangeName, "order");
            channel.queueBind("queue6", exchangeName, "order");
            channel.queueBind("queue7", exchangeName, "message");

            // 8、发布消息
            for (int i = 0; i < 20; i++) {
                message.append(i);
                channel.basicPublish(exchangeName, "order", null, message.toString().getBytes());

            }


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
