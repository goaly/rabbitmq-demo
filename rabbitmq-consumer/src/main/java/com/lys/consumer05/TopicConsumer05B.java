/**
 * Copyright (C), 2022-2032
 */
package com.lys.consumer05;

import com.lys.util.RabbitMqUtil;
import com.rabbitmq.client.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * TopicConsumer05B  主题模式 - 消费者05B
 *
 * @author lys
 * @date 2022/5/14
 */
@Slf4j
public class TopicConsumer05B {

    /**
     * 发布订阅交换机名称
     */
    private static final String EXCHANGE_NAME = "publish_subscribe_exchange_topic";

    /**
     * 队列名称
     */
    private static final String QUEUE_NAME = "topic_queue_b";

    /**
     * 路由key, #匹配任意数量（0个或多个）单词
     */
    private static final String ROUTING_KEY = "news.#";

    public static void main(String[] args) throws IOException {

        // 建立连接
        Connection connection = RabbitMqUtil.getConnection();
        // 打开通道，建立会话
        Channel channel = connection.createChannel();
        // 声明队列
        channel.queueDeclare(QUEUE_NAME, true, false, false, null);
        // 将队列绑定到交换机上
        channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, ROUTING_KEY);

        // 定义消费方法
        DefaultConsumer consumer = new DefaultConsumer(channel) {

            @Override
            @SneakyThrows
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
                                       byte[] body) {
                // 交换机
                String exchange = envelope.getExchange();
                // 路由key
                String routingKey = envelope.getRoutingKey();
                // 投递标签，消息唯一标识
                long deliveryTag = envelope.getDeliveryTag();
                try {
                    String msg = new String(body, StandardCharsets.UTF_8);
                    log.info("received MQ message: {exchange:\"{}\", routingKey:\"{}\", deliveryTag:\"{}\", body:\"{}\"}",
                            exchange, routingKey, deliveryTag, msg);
                    Thread.sleep(500);
                } finally {
                    // 消费完一条消息，手工确认收到消息
                    channel.basicAck(deliveryTag, false);
                }
            }
        };
        // 使用公平分发必须关闭自动应答(autoAck：true自动返回结果，false手动返回)
        boolean autoAck = false;
        channel.basicConsume(QUEUE_NAME, autoAck, consumer);
    }

}
