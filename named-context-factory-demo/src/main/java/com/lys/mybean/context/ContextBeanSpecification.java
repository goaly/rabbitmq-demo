package com.lys.mybean.context;

import lombok.Setter;
import org.springframework.cloud.context.named.NamedContextFactory;

/**
 * 自定义上下文Bean - 名称的配置规范
 *
 * @author lys
 */
public class ContextBeanSpecification implements NamedContextFactory.Specification {

    @Setter
    private final String name;

    @Setter
    private final Class<?>[] configuration;

    public ContextBeanSpecification(String name, Class<?>[] configuration) {
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
