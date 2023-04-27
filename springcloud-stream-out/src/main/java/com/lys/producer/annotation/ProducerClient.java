package com.lys.producer.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 声明创建 MQ生产者客户端的注解
 * @author lys
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ProducerClient {

    @AliasFor("name")
    String value() default "";

    /**
     * The service id with optional protocol prefix. Synonym for {@link #value() value}.
     *
     * @deprecated use {@link #name() name} instead
     */
    @Deprecated
    String serviceId() default "";


    /**
     * The service id with optional protocol prefix. Synonym for {@link #value() value}.
     */
    @AliasFor("value")
    String name() default "";

    /**
     * Whether to mark the feign proxy as a primary bean. Defaults to true.
     */
    boolean primary() default true;

    /**
     * @return producer客户端的 @Qualifiers 值。除非 qualifiers() 返回的数组为空或仅包含空值或空白值，
     * 在这种情况下则默认为 = contextId + "ProducerClient"。
     */
    String[] qualifiers() default {};

    /**
     * mq binder 名称
     */
    String binder() default "";
}
