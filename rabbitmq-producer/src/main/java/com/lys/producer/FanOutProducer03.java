/**
 * Copyright (C), 2022-2032
 */
package com.lys.producer;

import com.lys.util.RabbitMqUtil;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import lombok.extern.slf4j.Slf4j;

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;

import static org.apache.commons.lang3.StringUtils.EMPTY;

/**
 * FanOutProducer03 发布订阅模型 - Fanout生产者03
 *
 * @author lys
 * @date 2022/5/8
 */
@Slf4j
public class FanOutProducer03 {

    /**
     * 发布订阅交换机名称
     */
    private static final String PUBLISH_SUBSCRIBE_EXCHANGE_NAME = "publish_subscribe_exchange_fanout";
    /**
     * 交换机类型：分发 广播
     */
    private static final String PUBLISH_SUBSCRIBE_EXCHANGE_TYPE = "fanout";

    private static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss SSS");

    public static void main(String[] args) {

        // 发送count条消息
        int count = 3;
        for (int i = 0; i < count; i++) {
            String message = String.format("测试消息%d ===> %s", i + 1,
                    new Timestamp(System.currentTimeMillis()).toLocalDateTime().format(dateTimeFormatter)
            );
            sendMessage(message);
        }
        log.info("消息发送完毕！");

    }

    private static void sendMessage(String message) {
        Connection connection = null;
        Channel channel = null;
        try {
            // 1.连接工厂
            // 2.创建与RabbitMQ服务的TCP连接
            connection = RabbitMqUtil.getConnection();

            // 3.创建与Exchange的通道，每个连接可以创建多个通道，每个通道代表一个会话任务
            channel = connection.createChannel();
            // 创建交换机对象 publish_subscribe_exchange_fanout
            channel.exchangeDeclare(PUBLISH_SUBSCRIBE_EXCHANGE_NAME, PUBLISH_SUBSCRIBE_EXCHANGE_TYPE);
            // 发送消息到交换机exchange
            channel.basicPublish(PUBLISH_SUBSCRIBE_EXCHANGE_NAME, EMPTY, null, message.getBytes());
            log.info("Send Message is:'{}'", message);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            RabbitMqUtil.closeConnection(channel, connection);
        }
    }
}
