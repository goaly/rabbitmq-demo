package com.lys.producer.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * MQ生产者客户端的信息绑定
 *
 * @author lys
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ProducerBinding {

    /**
     * mq binding 名称
     */
    String bindingName() default "";

    /**
     * 绑定名称格式化，为true时自动将非空的bindingName格式化成bindingName-in-0
     */
    boolean nameFormat() default true;
}
