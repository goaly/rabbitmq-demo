/**
 * Copyright (C), 2022-2032
 */
package com.lys.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMqDirectConfig RabbitMq路由模式配置
 *
 * @author: lys
 * @date: 2022/5/15 17:46
 */
@Configuration
public class RabbitMqDirectConfig {

    @Bean
    public Queue directQueue1() {
        return new Queue("direct-queue-001");
    }

    @Bean
    public Queue directQueue2() {
        return new Queue("direct-queue-002");
    }

    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange("direct-exchange-001");
    }

    @Bean
    public Binding bindingDirect1(){
        // 将队列和交换机绑定
        return BindingBuilder.bind(directQueue1()).to(directExchange()).with("userInfo");
    }

    @Bean
    public Binding bindingDirect2(){
        // 将队列和交换机绑定
        return BindingBuilder.bind(directQueue2()).to(directExchange()).with("user002");
    }
}
