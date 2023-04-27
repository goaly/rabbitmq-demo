/**
 * Copyright (C), 2023-2033
 */
package com.lys.mybean.context;

import com.lys.mybean.config.MyContextAutoConfiguration;
import org.springframework.cloud.context.named.NamedContextFactory;
import org.springframework.stereotype.Component;

/**
 * 创建MyContextBean的命名上下文工厂
 *
 * @author: lys
 * @date: 2023/4/3 14:54
 */
@Component
public class MyContextBeanFactory extends NamedContextFactory<ContextBeanSpecification> {

    /**
     * 通过命名获取子容器的ContextBean的工厂
     * <pre> 先定义一个ContextFactory对象, 通过ContextBeanSpecification定义不同命名的子context需要加载的configuration，
     * 当命名是以default开头的配置，所有的子context共用该ContextBeanSpecification定义的configuration。</pre>
     */
    public MyContextBeanFactory() {
        // 自定义自动配置类
        super(MyContextAutoConfiguration.class, "contextBean", "testcontext.name");
    }

}
