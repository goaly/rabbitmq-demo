/**
 * Copyright (C), 2022-2032
 */
package com.lys.producer;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.cloud.context.named.NamedContextFactory;
import org.springframework.lang.Nullable;

import java.util.Map;

/**
 * 创建 Producer 类实例的工厂。它为每个客户端名称创建一个 Spring ApplicationContext，并从那里提取它需要的 bean。
 *
 * @author: lys
 * @date: 2022/5/30 9:41
 */
public class ProducerContext extends NamedContextFactory<ProducerClientSpecification> {

    public ProducerContext() {
        super(ProducerClientsConfiguration.class, "producer", "producer.client.name");
    }

    @Nullable
    public <T> T getInstanceWithoutAncestors(String name, Class<T> type) {
        try {
            return BeanFactoryUtils.beanOfType(getContext(name), type);
        }
        catch (BeansException ex) {
            return null;
        }
    }

    @Nullable
    public <T> Map<String, T> getInstancesWithoutAncestors(String name, Class<T> type) {
        return getContext(name).getBeansOfType(type);
    }

    public <T> T getInstance(String contextName, String beanName, Class<T> type) {
        return getContext(contextName).getBean(beanName, type);
    }
}
