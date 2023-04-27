/**
 * Copyright (C), 2023-2033
 */
package com.lys.mybean.config;

import cn.hutool.core.text.StrFormatter;
import com.lys.mybean.ITestContext;
import com.lys.mybean.MyContextBean;
import com.lys.mybean.MyShowContextBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * 上下文自动配置
 *
 * @author: lys
 * @date: 2023/4/3 14:44
 */
public class MyContextAutoConfiguration {

    private final String ENV_TEST = "test";

    /**
     * 默认为开发环境
     */
    @Value("${env.name:unknown}")
    String envName;

    @Value("${testcontext.name}")
    String name;

    @Bean
    @ConditionalOnMissingBean
    public ITestContext getTestContext() {

        String serviceName = StrFormatter.format("current environment is {}, hello {}", envName, name);
        if (envName.equalsIgnoreCase(ENV_TEST)) {
            return new MyShowContextBean(serviceName);
        } else {
            return new MyContextBean(serviceName);
        }
    }
}
