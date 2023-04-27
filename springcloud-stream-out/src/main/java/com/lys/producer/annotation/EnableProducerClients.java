package com.lys.producer.annotation;

import com.lys.producer.ProducerClientsRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 扫描声明它们是  MQ生产者客户端的接口（通过 @ProducerClient）。
 * 配置组件扫描指令以与 @Configuration 类一起使用
 *
 * @author lys
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(ProducerClientsRegistrar.class)
public @interface EnableProducerClients {

    /**
     * basePackages() 属性的别名。允许更简洁的注释声明，
     * 例如：@ComponentScan("org.my.pkg") 而不是 @ComponentScan(basePackages="org.my.pkg")。
     *
     * @return 'basePackages' 的数组
     */
    String[] value() default {};

    /**
     * 用于扫描带注释组件的基础包。 value() 是此属性的别名（并且与此属性互斥）。
     * 使用 basePackageClasses() 作为基于字符串的包名称的类型安全替代方案
     *
     * @return 'basePackages' 的数组
     */
    String[] basePackages() default {};

    /**
     * {@link #basePackages()} 的类型安全替代方案，用于指定要扫描带注释组件的包。将扫描指定的每个类的包。 <p>
     * 考虑在每个包中创建一个特殊的无操作标记类或接口，除了被此属性引用之外没有其他用途
     *
     * @return the array of 'basePackageClasses'.
     */
    Class<?>[] basePackageClasses() default {};

    /**
     * 所有 ProducerClient 客户端的自定义 @Configuration。可以包含构成客户端的部分的覆盖 @Bean 定义
     *
     * @return
     */
    Class<?>[] defaultConfiguration() default {};

    /**
     * 使用 @ProducerClient 注释的类列表。如果不为空，则禁用类路径扫描。
     *
     * @return ProducerClient 类的列表
     */
    Class<?>[] clients() default {};
}
