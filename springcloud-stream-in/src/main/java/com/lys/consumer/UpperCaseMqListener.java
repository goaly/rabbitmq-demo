/**
 * Copyright (C), 2023-2033
 */
package com.lys.consumer;

import com.rabbitmq.client.Channel;
import java.io.IOException;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * UpperCaseMqListener
 *
 * @author: lys
 * @date: 2023/1/3 17:22
 */
//@Component
@RabbitListener(queues = "queue.uppercase-01", containerFactory = "rabbitListenerContainerFactory")
@Slf4j
public class UpperCaseMqListener {

    @RabbitHandler
    public void process(@Payload Message message, @Headers Map<String, Object> headers, Channel channel)
            throws Exception {

        Long deliveryTag = (Long) headers.get(AmqpHeaders.DELIVERY_TAG);
        String queueName = (String) headers.get(AmqpHeaders.CONSUMER_QUEUE);
        try {
            log.info("upperCase consumed by spring amqp: {}", new String(message.getBody(), "utf-8"));
            channel.basicAck(deliveryTag, false);
        } catch (IOException e) {
            log.error("##MQ##" + queueName + "消费失败", e);
            //拒绝消息
            // requeue为true该消息重新回到队列，并发到该队列的其他消费者，如没有其他消费者，则会一直发到该消费者，为false则直接丢掉该消息
            channel.basicReject(deliveryTag, false);
        }
    }

}
