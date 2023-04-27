/**
 * Copyright (C), 2022-2032
 * FileName: Consumer01
 * Author:   lys
 * Date:     2022/5/14 12:01
 * Description: 消费者01
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.lys.consumer;

import com.lys.util.RabbitMqUtil;
import com.rabbitmq.client.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Consumer01  简单队列模型 - 消费者01
 *
 * @author lys
 * @date 2022/5/14
 */
@Slf4j
public class Consumer01 {

    /**
     * 队列名称
     */
    private static final String QUEUE = "QUEUE01_HELLO_WORLD";

    public static void main(String[] args) throws IOException {

        // 建立连接
        Connection connection = RabbitMqUtil.getConnection();
        // 打开通道，建立会话
        Channel channel = connection.createChannel();
        // 声明队列
        channel.queueDeclare(QUEUE, true, false, false, null);

        // 定义消费方法
        DefaultConsumer consumer = new DefaultConsumer(channel) {

            /**
             * 消费者接收消息调用此方法
             * @param consumerTag 消费者的标签，在channel.basicConsume()去指定
             * @param envelope 消息包的内容，可从中获取消息id，消息routingkey，交换机，消息和重传标志 (收到消息失败后是否需要重新发送)
             * @param properties
             * @param body
             */
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
                                       byte[] body) {
                // 交换机
                String exchange = envelope.getExchange();
                // 路由key
                String routingKey = envelope.getRoutingKey();
                // 投递标签，消息唯一标识
                long deliveryTag = envelope.getDeliveryTag();
                String msg = new String(body, StandardCharsets.UTF_8);
                log.info("received MQ message: {exchange:\"{}\", routingKey:\"{}\", deliveryTag:\"{}\", body:\"{}\"}",
                        exchange, routingKey, deliveryTag, msg);
            }
        };
        /**
         * 监听队列
         * String queue, boolean autoAck,Consumer callback
         * 参数明细
         * 1、队列名称
         * 2、是否自动回复，设置为true为表示消息接收到自动向mq回复接收到了，mq接收到回复会删除消息，设置 为false则需要手动回复
         * 3、消费消息的方法，消费者接收到消息后调用此方法
         */
        channel.basicConsume(QUEUE, true, consumer);
    }

}
