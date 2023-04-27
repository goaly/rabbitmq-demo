/**
 * Copyright (C), 2022-2032
 */
package com.lys.consumer;

import com.alibaba.fastjson.JSON;
import com.lys.model.MessageWrapper;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

/**
 * FuncMqConsumer 函数式的MQ消费者
 *
 * @author: lys
 * @date: 2022/5/19 20:21
 */
@Component
@Slf4j
public class FuncMqConsumer {

    @Value("${server.port}")
    private String serverPort;

    /**
     * 这里接收rabbitmq的条件是参数为Consumer 并且 方法名和supplier方法名相同 这里的返回值是一个匿名函数 返回类型是consumer 类型和提供者的类型一致 supplier发送的exchange是
     * funcMq-in-0 这里只需要用funcMq方法名即可
     */
    @Bean
    public Consumer<MessageWrapper<String>> funcMq() {
        return msgWrapper -> {
            log.info("received message content: {}", JSON.toJSONString(msgWrapper));
            log.info("我是消费者" + serverPort + "，收到了消息：" + msgWrapper.getData());
        };
    }

    /**
     * 这里接收rabbitmq的条件是参数为Consumer 并且 方法名和supplier方法名相同 这里的返回值是一个匿名函数 返回类型是consumer 类型和提供者的类型一致 supplier发送的exchange是
     * confirmAdjust-in-0 这里只需要用confirmAdjust方法名即可
     */
    @Bean
    public Consumer<MessageWrapper<String>> confirmAdjust() {
        return msgWrapper -> {
            log.info("received message content: {}", JSON.toJSONString(msgWrapper));
            log.info("我是调帐确认消费者{}，收到了消息：{}，messageId: {}, createTime: {}", serverPort, msgWrapper.getData(),
                    msgWrapper.getMessageId(), msgWrapper.getCreateTime());
        };
    }

    @Bean
    public Consumer<MessageWrapper<String>> uppercase() {
        return data -> {
//            String result = new String(data, StandardCharsets.UTF_8);
            log.info("uppercase consumed by spring stream: " + JSON.toJSONString(data));
        };
    }
}
