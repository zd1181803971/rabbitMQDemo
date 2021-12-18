# rabbitMQ

# 安装

port：

- 程序通信端口：5672

- 图形化管理界面：15672

command：

```bash
rabbitmqctl add_user 账号  密码 # 新建用户
rabbitmqctl set_user_tags 账号  权限 # 赋予权限
rabbitmqctl change_password 账号 新密码  # 修改密码
rabbitmqctl delete_user 账户 # 删除用户
rabbitmqctl list_users # 查看用户清单
rabbitmqctl set_permissions -p / 账户 ".*" ".*" ".*" # 添加资源权限?
```

privilege level：

- administrator  登录控制台、查看所有信息、对rabbitmq进行管理
  
  - 最高权限
  
  - 可以创建和删除virtual hosts
  
  - 可以查看，创建和删除users
  
  - 查看和创建permissions

- monitoring  监控者，登录控制台，查看所有信息
  
  - 包含management所有权限
  
  - 罗列出所有的virtual hosts，包含不能登陆的virtual hosts
  
  - 查看其他用户的connections和channels信息
  
  - 查看节点级别的数据，如clustering和memory使用情况
  
  - 查看所有的virtual hosts的全局统计信息

- policymaker 策略制定，登录控制台，指定策略
  
  - 包含management所有权限
  
  - 查看和创建和删除自己的virtual hosts 所属的policies和parameters信息

- management 普通管理员，登陆控制台。查看自己相关的节点信息
  
  - 列出自己可以通过AMQP登入的虚拟机
  
  - 查看自己的虚拟机节点virtual hosts的queues,exchanges和bindings信息
  
  - 查看和关闭自己的channels和connections
  
  - 查看有关自己的虚拟机节点virtual hosts 的统计信息。包括其他用户在这个节点virtual hosts中的活动信息

- none 不能访问management plugin

## 快速入门

### 1、 Java原生依赖

```xml
        <!-- https://mvnrepository.com/artifact/com.rabbitmq/amqp-client -->
        <dependency>
            <groupId>com.rabbitmq</groupId>
            <artifactId>amqp-client</artifactId>
            <version>5.10.0</version>
        </dependency>
    </dependencies>
```

Produce

```java
package com.dzu.rabbitmq.simple;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;


/**
 * @author Administrator
 * @date 2021/12/17 13:35
 * @description 简单模式 生产者
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

            System.out.println("123123123");
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
```

```java
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
            channel.basicConsume(queueName, true, (consumer, qwe) -> {
                System.out.println("接收到的消息：");
                System.out.println(consumer);
                System.out.println(new String(qwe.getBody()));
            }, (consumerTag) -> {
                System.out.println("接受失败");
                System.out.println(consumerTag);
            });

            System.out.println("123123123");
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
```

##### 代码形式声明交换机和队列并进行绑定：

```java
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
            String message = "Hello rabbitMQ!";

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
            channel.basicPublish(exchangeName, "order", null, message.getBytes());


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

```

## AMQP

AMQP，即Advanced Message Queuing Protocol，一个提供统一消息服务的应用层标准高级消息队列协议，是应用层协议的一个开放标准，为面向消息的中间件设计。基于此协议的客户端与消息中间件可传递消息，并不受客户端/中间件不同产品，不同的开发语言等条件的限制。Erlang中的实现有RabbitMQ等。

## Exchanges Type

- fanout
  
  - publish/Subscribe
  
  - 广播模式，就是把交换机（Exchange）里的消息发送给所有绑定该交换机的队列，忽略routingKey
  
  - ![](https://img2018.cnblogs.com/blog/1310329/201811/1310329-20181108214617899-986752509.png)

- direct
  
  - Routing
  
  - 通过routingKey和exchange决定的那个唯一的queue可以接收消息
  
  - ![](https://img2018.cnblogs.com/blog/1310329/201811/1310329-20181108223926207-1625168350.png)

- topic
  
  - Topics
  
  - 所有符合routingKey(此时可以是一个表达式)的routingKey所bind的queue可以接收消息
    
    - \# 代表0个或者多级，可以有多级，也可以没有
    
    - \* 代表1级，必须要有。
  
  - ![](https://img2018.cnblogs.com/blog/1310329/201811/1310329-20181109011551628-29724992.png)

- headers
  
  - 通过headers来决定把消息发给哪些queue，用的比较少

## 分发机制

- 简单模式

- 工作模式
  
  - 轮询 RoundRobin 
  
  - 公平
    
    - autoAck：false ->> 需要手动ack 可以确保消息真正成成功被消费
    
    - channel.basicQos(1);  同一时刻，broke只会推送一条消息给消费者 

- 发布|订阅模式

- 路由模式

- 主题模式

- RPC

## 使用场景

### 1、解耦、异步、晓峰

- 同步异步的问题（串行）
  
  - 串行：将订单信息写入数据库成功后，发送注册邮件，再发送注册短信……所有任务执行完成后，返回给客户端。所需时间是所有服务总和。

## 异常

交换机和队列不存在都会抛出异常
