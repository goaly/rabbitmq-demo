package com.lys.namedcontext;

import org.springframework.cloud.context.named.NamedContextFactory;

/**
 * 自定义http客户端 - 名称的配置规范
 * @author lys
 */
public class NamedHttpClientSpec implements NamedContextFactory.Specification {

    private final String name;
    private final Class<?>[] configuration;

    public NamedHttpClientSpec(String name, Class<?>[] configuration) {
        this.name = name;
        this.configuration = configuration;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Class<?>[] getConfiguration() {
        return configuration;
    }
}
