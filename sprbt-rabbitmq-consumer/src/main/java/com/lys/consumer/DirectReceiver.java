/**
 * Copyright (C), 2022-2032
 */
package com.lys.consumer;

import com.alibaba.fastjson.JSON;
import com.lys.model.User;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * DirectReceiver
 *
 * @author: lys
 * @date: 2022/5/15 21:01
 */
@Component
public class DirectReceiver {

    @RabbitListener(queues = "direct-queue-001")
    @RabbitHandler
    public void process1(User user) {
        System.out.println("direct-queue-001消费者收到消息：" + JSON.toJSONString(user));
    }

}
