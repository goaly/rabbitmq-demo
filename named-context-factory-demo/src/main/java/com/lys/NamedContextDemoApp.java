package com.lys;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author lys
 */
@SpringBootApplication
@EnableSwagger2
public class NamedContextDemoApp {

    public static void main(String[] args) {
        SpringApplication.run(NamedContextDemoApp.class, args);
    }

}
