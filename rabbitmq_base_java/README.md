# rabbitMQ

## 概念

- Server：又称Broker，接收客户端的连接，实现AMQP实体服务

- Connection：连接，应用程序与Broker的网络连接

- Channel：网络信道，几乎所有的操作都在Channel中进行，包括定义Queue、定义Exchange、绑定Queue与Exchange、发布消息等。Channel是进行消息读写的通道。客户端可以建立多个Channel，每个Channel代表一个会话任务。

- Message：消息，服务器和应用程序之间传送的数据，由Properties和Body组成。Properties可以对消息进行修饰，比如消息的优先级、延迟等高级特性；Body就是消息体内容。

- Virtual host：虚拟地址，用于进行逻辑隔离，最上层的消息路由。一个Virtual host可以有若干个Exchange和Queue，同一个Virtual host里面不能有相同的Exchange和Queue

- Exchange：交换机，接收消息，根据路由键转发消息到绑定的队列
  
  RabbitMQ中有三种常用的交换机类型
  
  - direct: 如果路由键匹配，消息就投递到对应的队列
  - fanout：投递消息给所有绑定在当前交换机上面的队列
  - topic：允许实现有趣的消息通信场景，使得5不同源头的消息能够达到同一个队列。topic队列名称有两个特殊的关键字。

- Binding：Exchange和Queue之间的虚拟连接，binding中可以包含routing key

- Routing key：一个路由规则，虚拟机可用它来确定如何路由一个特定消息

- Queue：也称为Message Queue，消息队列，保存消息并将它们转发给消费者，多个消费者可以订阅同一个Queue，这时Queue中的消息会被平均分摊给多个消费者进行处理，而不是每个消费者都收到所有的消息并处理。

- Prefetch count：如果有多个消费者同时订阅同一个Queue中的消息，Queue中的消息会被平摊给多个消费者。这时如果每个消息的处理时间不同，就有可能会导致某些消费者一直在忙，而另外一些消费者很快就处理完手头工作并一直空闲的情况。我们可以通过设置prefetchCount来限制Queue每次发送给每个消费者的消息数，比如我们设置prefetchCount=1，则Queue每次给每个消费者发送一条消息；消费者处理完这条消息后Queue会再给该消费者发送一条消息。

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

### 2、高内聚、低耦合

## 异常

交换机和队列不存在都会抛出异常

## 高级

TTL

- 对整个队列属性设置
  
  - 会被投递到死信队列

- 对消息单独设置
  
  - 直接丢失

死信队列：（需要交换机）

- 消息被拒绝

- 消息过期

- 队列达到最大长度

## 发布确认

```
spring.rabbitmq.publisher-confirm-type=correlated
```

- `NONE` 值是禁用发布确认模式，是默认值

- `CORRELATED` 值是发布消息成功到交换器后会触发回调方法

- `SIMPLE` 值经测试有两种效果，其一效果和 CORRELATED 值一样会触发回调方法，其二在发布消息成功后使用 rabbitTemplate 调用 waitForConfirms 或 waitForConfirmsOrDie 方法等待 broker 节点返回发送结果，根据返回结果来判定下一步的逻辑，要注意的点是 waitForConfirmsOrDie 方法如果返回 false 则会关闭 channel，则接下来无法发送消息到 broker;

```java
public class MyCallBack implements RabbitTemplate.ConfirmCallback
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
        System.out.println("失败原
因："+cause);
    }
  rabbitTemplate.setConfirmCallback(MyCallBack);


```

## 

## 回退消息

**Mandatory** 参数

```
rabbitTemplate.setReturnsCallback(myCallBack);
```

在仅开启了生产者确认机制的情况下，交换机接收到消息后，会直接给消息生产者发送确认消息，如果发现该消息不可路由，那么消息会被直接丢弃，此时生产者是不知道消息被丢弃这个事件的。

那么如何让无法被路由的消息帮我想办法处理一下？最起码通知我一声，我好自己处理啊。通过设置 mandatory 参数可以在当消息传递过程中不可达目的地时将消息返回给生产者。



**修改配置**

```
#消息退回
spring.rabbitmq.publisher-returns=true
```

```java
public class MyCallBack implements RabbitTemplate.ReturnCallback
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
        System.out.println("消息使用的路由键 routing : "
+routingKey);
    }
rabbitTemplate.setMandatory(true);
        rabbitTemplate.setReturnCallback(MycallBack);             //指定 ReturnCallback
```
