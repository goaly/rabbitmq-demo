/**
 * Copyright (C), 2022-2032
 */
package com.lys;

import com.lys.producer.annotation.EnableProducerClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * StreamOutApplication Stream生产者SpringCloud应用
 * @author: lys
 * @date: 2022/5/15 17:29
 */
@SpringBootApplication
@EnableProducerClients
@EnableSwagger2
public class StreamOutApplication {

    public static void main(String[] args) {
        SpringApplication.run(StreamOutApplication.class, args);
    }

}
