/**
 * Copyright (C), 2022-2032 FileName: TestMainProducer Author:   lys Date:     2022/5/8 22:32 Description: History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.lys.producer;

import static org.apache.commons.lang3.StringUtils.EMPTY;

import com.lys.util.RabbitMqUtil;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import java.util.Scanner;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

/**
 * Producer01 简单队列模型 - 生产者01
 *
 * @author lys
 * @date 2022/5/8
 */
@Slf4j
public class RabbitTemplateDemo01 {

    /**
     * 队列名称
     */
    private static final String QUEUE = "QUEUE01_HELLO_WORLD";

    private CachingConnectionFactory connectionFactory;

    private RabbitTemplate rabbitTemplate;

    public RabbitTemplateDemo01() {
        init();
    }

    public void init() {
        if (connectionFactory == null) {
            connectionFactory = RabbitMqUtil.getConnectionFactory(
                    RabbitMqUtil.RABBITMQ_HOST,
                    RabbitMqUtil.RABBITMQ_PORT,
                    RabbitMqUtil.RABBITMQ_USERNAME,
                    RabbitMqUtil.RABBITMQ_PASSWORD
            );
        }
        rabbitTemplate = RabbitMqUtil.getRabbitTemplate(connectionFactory);
    }

    public static void main(String[] args) {
        RabbitTemplateDemo01 demo = new RabbitTemplateDemo01();
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("输入要发送的消息：");
            String message = scanner.next();
            demo.sendMessage(message);
        }

    }

    private void sendMessage(String message) {
        rabbitTemplate.convertAndSend(EMPTY, QUEUE, message);
    }
}
