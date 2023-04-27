package com.lys.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

/**
 * 描述: 获取实时刷新的Nacos配置
 *
 * @author lys
 * @create 2022-09-27
 */
@Getter
@Configuration
@RefreshScope
public class PropertiesConfig {

    @Value("${mq.producer.bindingName}")
    private String producerBindingName;
}
