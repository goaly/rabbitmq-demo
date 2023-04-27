/**
 * Copyright (C), 2022-2032
 */
package com.lys.producer;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.lys.config.StreamBindersConfig;
import com.lys.model.MessageWrapper;
import com.lys.producer.annotation.ProducerBinding;
import com.lys.producer.annotation.ProducerClient;
import com.lys.producer.demo.AdjustBillSource;
import com.lys.util.RabbitMqUtil;
import com.lys.util.UUIDGenerateUtil;
import com.rabbitmq.client.BuiltinExchangeType;
import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.sql.Timestamp;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanExpressionContext;
import org.springframework.beans.factory.config.BeanExpressionResolver;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.ApplicationContext;
import org.springframework.util.MimeTypeUtils;

/**
 * ProducerClientProxy 消息生产者的动态代理
 *
 * @author: lys
 * @date: 2022/5/31 8:10
 */
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
public class ProducerClientProxy implements InvocationHandler, Serializable {

    private static final long serialVersionUID = 1470316318623017876L;

    @Setter
    private ApplicationContext applicationContext;

    @Setter
    private BeanFactory beanFactory;

    @Getter
    private RabbitTemplate rabbitTemplate;

    @Getter
    private StreamBridge streamBridge;

    @Getter
    private StreamBindersConfig bindersConfig;

    private String binderName;

    public ProducerClientProxy(BeanFactory beanFactory,
            ApplicationContext applicationContext) {
        this.beanFactory = beanFactory;
        this.applicationContext = applicationContext;
    }

    /**
     * 获得目标接口的代理
     *
     * @param <T>                目标接口泛型
     * @param targetInterface    目标接口
     * @param beanFactory
     * @param applicationContext
     * @return 目标接口的代理
     */
    public static <T> T newTargetProxy(Class<T> targetInterface, BeanFactory beanFactory,
            ApplicationContext applicationContext) {
        ClassLoader classLoader = targetInterface.getClassLoader();
        Class<?>[] interfaces = new Class[]{targetInterface};
        ProducerClientProxy proxy = new ProducerClientProxy(beanFactory, applicationContext);
        proxy.init(targetInterface);
        return (T) Proxy.newProxyInstance(classLoader, interfaces, proxy);
    }

    public static void main(String[] args) {
        AdjustBillSource adjustBillSource = newTargetProxy(AdjustBillSource.class, null, null);
        adjustBillSource.adjustBillConfirmSync();
    }

    @Override
    @SneakyThrows
    public Object invoke(Object proxy, Method method, Object[] args) {

        if (showDebugOnInfo()) {
            log.info("ProducerClient接口方法调用开始");
            //执行方法
            log.info("method toGenericString:{}", method.toGenericString());
        }
        String methodName = method.getName();
        Class<?> targetClass = method.getDeclaringClass();
        log.info("method args:{}", JSON.toJSONString(args));
        if (Object.class.equals(targetClass)) {
            return method.invoke(this, args);
        } else if (isDefaultMethod(method)) {
            return invokeDefaultMethod(proxy, method, args);
        }

        if (ArrayUtil.isNotEmpty(args)) {

            String bindingName = dealBindingName(method);
            log.info("bindingName:{}", methodName);
            String messageStr = Objects.toString(args[0], StringUtils.EMPTY);
            Timestamp timestamp = DateUtil.date().toTimestamp();
            MessageWrapper<Object> messageWrapper = MessageWrapper.builder()
                    .messageId(UUIDGenerateUtil.randomUuid())
                    .createTime(timestamp)
                    .returnCallback(false)
                    .data(messageStr)
                    .build();

            /*if (this.streamBridge != null) {
                this.streamBridge.send(bindingName, this.binderName, messageWrapper, MimeTypeUtils.APPLICATION_JSON);
            } else */
            if (this.rabbitTemplate != null) {
                if (!checkExchangeExisted(bindingName)) {
                    rabbitTemplate.execute(channel ->
                            channel.exchangeDeclare(bindingName, BuiltinExchangeType.TOPIC, true, false, null));
                }
                this.rabbitTemplate.convertAndSend(bindingName, null, messageWrapper,
                        ProducerClientProxy::processAsStreamMessage);
            }
        }
        log.info("接口方法调用结束");

        return null;
    }

    /**
     * 是否在info级别日志中显示自定义的调试信息
     *
     * @return 开关标识
     */
    public boolean showDebugOnInfo() {
        return BooleanUtil.toBoolean(resolve("${debug.info.show:false}"));
    }

    /**
     * 从上下文中获取Bean对象
     *
     * @param tClass Bean的Class类
     * @param <T>    Bean类型
     * @return Bean对象
     */
    private <T> T getBeanFromContext(Class<T> tClass) {
        T t = null;
        if (beanFactory != null) {
            t = beanFactory.getBean(tClass);
        }
        if (this.applicationContext != null && this.applicationContext != beanFactory) {
            t = t == null ? applicationContext.getBean(tClass) :
                    t;
        }
        return t;
    }

    /**
     * 检查rabbitmq中是否存在指定名称的Exchange
     *
     * @param exchangeName 交换机名称
     * @return 是否存在标识
     */
    private boolean checkExchangeExisted(String exchangeName) {
        return rabbitTemplate.execute(channel -> {
            try {
                return channel.exchangeDeclarePassive(exchangeName);
            } catch (Exception e) {
                log.info("Exchange '" + exchangeName + "' does not exist");
                return null;
            }
        }) != null;
    }

    /**
     * RabbitTemplate按Stream方式处理消息
     *
     * @param originalMessage 原消息
     * @return 处理后的消息
     */
    private static Message processAsStreamMessage(Message originalMessage) {
        MessageProperties msgProperties = originalMessage.getMessageProperties();
        msgProperties.setHeader("target-protocol", "streamBridge");
        msgProperties.setMessageId(IdUtil.randomUUID());
        msgProperties.setTimestamp(DateUtil.date().toTimestamp());
        msgProperties.setContentType(MimeTypeUtils.APPLICATION_JSON_VALUE);
        return originalMessage;
    }

    private <T> void init(Class<T> targetInterface) {

        this.bindersConfig = getBeanFromContext(StreamBindersConfig.class);
        this.streamBridge = getBeanFromContext(StreamBridge.class);

        ProducerClient producerClient = targetInterface.getAnnotation(ProducerClient.class);
        this.binderName = StrUtil.blankToDefault(producerClient.binder(), "defaultRabbit");
        this.rabbitTemplate = RabbitMqUtil.getRabbitTemplate(this.bindersConfig
                .getStreamBinderMap().get(binderName));

    }

    /**
     * resolve 解析value中的表达式
     *
     * @param value 注解属性值
     * @return value解析结果
     */
    private String resolve(String value) {
        if (org.springframework.util.StringUtils.hasText(value)) {
            ConfigurableBeanFactory configurableBeanFactory =
                    (this.beanFactory != null && this.beanFactory instanceof ConfigurableBeanFactory)
                            ? (ConfigurableBeanFactory) beanFactory : null;
            if (configurableBeanFactory == null) {
                return this.applicationContext.getEnvironment().resolvePlaceholders(value);
            }
            BeanExpressionResolver resolver = configurableBeanFactory.getBeanExpressionResolver();
            String resolved = configurableBeanFactory.resolveEmbeddedValue(value);
            if (resolver == null) {
                return resolved;
            }
            Object evaluateValue = resolver.evaluate(resolved,
                    new BeanExpressionContext(configurableBeanFactory, null));
            if (evaluateValue != null) {
                return String.valueOf(evaluateValue);
            }
            return null;
        }
        return value;
    }

    /**
     * 处理消息绑定名称
     *
     * @param method 方法对象
     * @return 消息绑定名称
     */
    private String dealBindingName(Method method) {
        // bindingName默认为方法名
        String bindingName = method.getName();
        // bindingName默认格式化
        boolean nameFormat = true;
        ProducerBinding annotation = method.getAnnotation(ProducerBinding.class);
        if (annotation != null) {
            if (StrUtil.isNotBlank(annotation.bindingName())) {
                // 有ProducerBinding注解，取注解的bindingName属性
                bindingName = resolve(annotation.bindingName());
            }
            nameFormat = annotation.nameFormat();
        }
        if (nameFormat) {
            bindingName = String.format("%s-in-0", bindingName);
        }
        return bindingName;
    }

    private Object invokeDefaultMethod(Object proxy, Method method, Object[] args)
            throws Throwable {
        final Constructor<MethodHandles.Lookup> constructor = MethodHandles.Lookup.class
                .getDeclaredConstructor(Class.class, int.class);
        if (!constructor.isAccessible()) {
            constructor.setAccessible(true);
        }
        final Class<?> declaringClass = method.getDeclaringClass();
        return constructor
                .newInstance(declaringClass,
                        MethodHandles.Lookup.PRIVATE | MethodHandles.Lookup.PROTECTED
                                | MethodHandles.Lookup.PACKAGE | MethodHandles.Lookup.PUBLIC)
                .unreflectSpecial(method, declaringClass).bindTo(proxy).invokeWithArguments(args);
    }

    /**
     * Backport of java.lang.reflect.Method#isDefault()
     */
    private boolean isDefaultMethod(Method method) {
        return (method.getModifiers()
                & (Modifier.ABSTRACT | Modifier.PUBLIC | Modifier.STATIC)) == Modifier.PUBLIC
                && method.getDeclaringClass().isInterface();
    }
}
