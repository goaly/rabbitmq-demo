package com.lys.consumer.config;

import com.lys.config.StreamBindersConfig;
import com.lys.util.RabbitMqUtil;
import java.util.ArrayList;
import java.util.Map;
import javax.annotation.Resource;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.ClassMapper;
import org.springframework.amqp.support.converter.ContentTypeDelegatingMessageConverter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

/**
 * MQ相关配置
 *
 * @author meng.yao
 */
//@Configuration
public class CommonRabbitMqConfig {

    @Resource
    private StreamBindersConfig streamBindersConfig;

    /**
     * esb_serviceResult交换机
     *
     * @return 交换机
     */
    @Bean
    TopicExchange uppercaseExchange() {
        return new TopicExchange("uppercase.exchange");
    }

    /**
     * esb返回结果队列
     *
     * @return 队列
     */
    @Bean
    public Queue uppercaseQueue() {
        return new Queue("queue.uppercase-01");
    }

    /**
     * esb返回结果队列绑定交换机
     *
     * @return 绑定
     */
    @Bean
    Binding uppercaseBind(Queue uppercaseQueue,
                                 TopicExchange uppercaseExchange) {
        return BindingBuilder.bind(uppercaseQueue).to(uppercaseExchange)
                .with("#");
    }

    /**
     * rabbitMq消息连接配置
     *
     * @return 连接工厂
     */
    @Bean
    @DependsOn("streamBindersConfig")
    public ConnectionFactory connectionFactory() {
        return RabbitMqUtil.getConnectionFactory(streamBindersConfig.getStreamBinderMap().get("scm"));
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        Jackson2JsonMessageConverter jackson2JsonMessageConverter = new Jackson2JsonMessageConverter();
        jackson2JsonMessageConverter.setClassMapper(new ClassMapper() {
            @Override
            public Class<?> toClass(MessageProperties properties) {
                Map<String, Object> mapHead = properties.getHeaders();
                String typeIdHeadKey = "__TypeId__";
                String specialClassType = "ArrayList$SubList";
                //特殊类型处理
                if (mapHead.containsKey(typeIdHeadKey)
                        && mapHead.get(typeIdHeadKey).toString().indexOf(specialClassType) >= 0) {
                    return ArrayList.class;
                }
                return Object.class;
            }

            @Override
            public void fromClass(Class<?> clazz, MessageProperties properties) {
            }
        });
        factory.setMessageConverter(new ContentTypeDelegatingMessageConverter(jackson2JsonMessageConverter));
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        factory.setPrefetchCount(1);
        factory.setConcurrentConsumers(1);
        factory.setMaxConcurrentConsumers(1);
        return factory;
    }
}
