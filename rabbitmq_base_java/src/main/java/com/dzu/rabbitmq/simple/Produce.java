package com.dzu.rabbitmq.simple;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;


/**
 * @author Administrator
 * @date 2021/12/17 13:35
 * @description 简单模式生产者
 */
public class Produce {
    public static void main(String[] args) {


        // 为什么rabbitmq基于channel去处理 而不是 连接？？  长连接——》》 信道
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

            // 4、通过创建交换机，生命队列，绑定关系，路由key，发送消息和接收消息
            String queueName = "queue1";
            /**
             * 1、 队列名称
             * 2、 是否要持久化，
             * 3、 排它性，是否独占
             * 4、 是否自动删除，随着最后一个消费者消费完毕后是否要把队列删除
             * 5、 携带附加参数
             */
            channel.queueDeclare(queueName, false, false, false, null);

            // 5、准备消息内容
            String message = "Hello rabbitMQ!";


            // 6、发送消息给队列queue
            /**
             * 1、交换机  ==》可以存在没有交换机的队列吗？？ 不可能，虽然没有指定，但是存在一个默认的交换机。 消息一定是通过交换机传递给队列的
             * 2、队列、路由key
             * 3、消息的状态控制，其他属性
             * 4、消息主体
             */
            channel.basicPublish("", queueName, null, message.getBytes());

            System.out.println("publish message success");
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
