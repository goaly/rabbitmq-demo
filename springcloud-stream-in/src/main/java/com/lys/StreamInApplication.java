/**
 * Copyright (C), 2022-2032
 */
package com.lys;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * StreamInApplication Stream消费者SpringCloud应用
 *
 * @author: lys
 * @date: 2022/5/19 17:29
 */
@SpringBootApplication
@Slf4j
public class StreamInApplication {

    public static void main(String[] args) {
        SpringApplication.run(StreamInApplication.class, args);
    }

}
