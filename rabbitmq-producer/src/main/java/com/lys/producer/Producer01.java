/**
 * Copyright (C), 2022-2032
 * FileName: TestMainProducer
 * Author:   lys
 * Date:     2022/5/8 22:32
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.lys.producer;

import com.lys.util.RabbitMqUtil;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import lombok.extern.slf4j.Slf4j;

import java.util.Scanner;

import static org.apache.commons.lang3.StringUtils.EMPTY;

/**
 * Producer01 简单队列模型 - 生产者01
 *
 * @author lys
 * @date 2022/5/8
 */
@Slf4j
public class Producer01 {

    /**
     * 队列名称
     */
    private static final String QUEUE = "QUEUE01_HELLO_WORLD";

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("输入要发送的消息：");
            String message = scanner.next();
            sendMessage(message);
        }

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

            /** 4.声明队列，如果Rabbit中没有此队列将自动创建
             * param1:队列名称
             * param2:是否持久化
             * param3:队列是否独占此连接
             * param4:队列不再使用时是否自动删除此队列
             * param5:队列参数
             ***/
            channel.queueDeclare(QUEUE, true, false, false, null);

            /***
             * 消息发布方法
             * param1：Exchange的名称，如果没有指定，则使用Default Exchange
             * 这里没有指定交换机，消息将发送给默认交换机，每个队列也会绑定那个默认的交换机，但是不能显 示绑定或解除绑定
             * param2:routingKey,消息的路由Key，是用于Exchange（交换机）将消息转发到指定的消息队列
             * param3:消息包含的属性
             * param4：消息体
             */
            channel.basicPublish(EMPTY, QUEUE, null, message.getBytes());
            log.info("Send Message is:'{}'", message);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            RabbitMqUtil.closeConnection(channel, connection);
        }
    }
}
