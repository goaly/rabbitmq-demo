/**
 * Copyright (C), 2022-2032
 */
package com.lys.producer;

import lombok.Data;
import lombok.ToString;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;

/**
 * ProducerClientFactoryBean 用来代理@ProducerClient注解的接口，对该接口的所有方法做一个拦截
 *
 * @author: lys
 * @date: 2022/5/30 9:13
 */
@Data
@ToString(callSuper = false)
public class ProducerClientFactoryBean
        implements FactoryBean<Object>, InitializingBean, ApplicationContextAware, BeanFactoryAware {

    private Class<?> type;

    private String name;

    private String contextId;

    private ApplicationContext applicationContext;

    private BeanFactory beanFactory;

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        applicationContext = context;
        beanFactory = context;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.hasText(this.name, "Name must be set");
    }

    @Override
    public Object getObject() {
        return ProducerClientProxy.newTargetProxy(this.type, this.beanFactory, this.applicationContext);
    }

    @Override
    public Class<?> getObjectType() {
        return this.type;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

}
