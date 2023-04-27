/**
 * Copyright (C), 2022-2032
 */
package com.lys.consumer03;

import com.lys.util.RabbitMqUtil;
import com.rabbitmq.client.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.apache.commons.lang3.StringUtils.EMPTY;

/**
 * FanoutConsumer03B  发布订阅模型 - 消费者03B
 *
 * @author lys
 * @date 2022/5/14
 */
@Slf4j
public class FanoutConsumer03B {

    /**
     * 发布订阅交换机名称
     */
    private static final String PUBLISH_SUBSCRIBE_EXCHANGE_NAME = "publish_subscribe_exchange_fanout";

    /**
     * 队列名称
     */
    private static final String PUBLISH_SUBSCRIBE_QUEUE_NAME = "publish_subscribe_queue_b";

    public static void main(String[] args) throws IOException {

        // 建立连接
        Connection connection = RabbitMqUtil.getConnection();
        // 打开通道，建立会话
        Channel channel = connection.createChannel();
        // 声明队列
        channel.queueDeclare(PUBLISH_SUBSCRIBE_QUEUE_NAME, true, false, false, null);
        // 将队列绑定到交换机上
        channel.queueBind(PUBLISH_SUBSCRIBE_QUEUE_NAME, PUBLISH_SUBSCRIBE_EXCHANGE_NAME, EMPTY);

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
        channel.basicConsume(PUBLISH_SUBSCRIBE_QUEUE_NAME, autoAck, consumer);
    }

}
