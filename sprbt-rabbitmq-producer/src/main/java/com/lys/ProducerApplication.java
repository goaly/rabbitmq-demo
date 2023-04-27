/**
 * Copyright (C), 2022-2032
 */
package com.lys;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * ProducerApplication RabbitMq生产者SpringBoot应用
 * @author: lys
 * @date: 2022/5/15 17:29
 */
@SpringBootApplication
public class ProducerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProducerApplication.class, args);
    }

}
