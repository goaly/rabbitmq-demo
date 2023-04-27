/**
 * Copyright (C), 2022-2032
 */
package com.lys.controller;

import com.alibaba.fastjson.JSON;
import com.lys.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * SendController
 *
 * @author: lys
 * @date: 2022/5/15 20:15
 */
@Slf4j
@RestController
@RequestMapping("/send")
public class SendController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @GetMapping("/direct/{routingKey}")
    public String sendDirectUser(@PathVariable("routingKey") String routingKey,
                                 String uname,
                                 String gender,
                                 String title) {
        User user = User.builder().id(UUID.randomUUID().toString())
                .userName(uname)
                .gender(gender)
                .title(title)
                .build();
        rabbitTemplate.convertAndSend("direct-exchange-001", routingKey, user);
        return "direct send user ok, message: " + JSON.toJSONString(user);
    }

}
