/**
 * Copyright (C), 2022-2032 FileName: RabbitMqUtil Author:   lys Date:     2022/5/14 11:36 Description: RabbitMq工具类
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.lys.util;

import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.StrUtil;
import com.lys.model.StreamBinder;
import com.lys.model.StreamBinderEnvironment;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.SneakyThrows;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory.ConfirmType;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;

/**
 * RabbitMqUtil RabbitMq工具类
 *
 * @author lys
 * @date 2022/5/14
 */
public class RabbitMqUtil {

    public static final String RABBITMQ_HOST = "127.0.0.1";
    public static final Integer RABBITMQ_PORT = 5672;
    public static final String RABBITMQ_VHOST = "/";
    public static final String RABBITMQ_USERNAME = "admin";
    public static final String RABBITMQ_PASSWORD = "admin";

    /**
     * 获取连接
     */
    public static Connection getConnection() {
        //定义MQ连接对象
        Connection connection = null;
        //创建MQ连接工厂对象
        ConnectionFactory connectionFactory = new ConnectionFactory();
        // 设置MQ主机名称
        connectionFactory.setHost(RABBITMQ_HOST);
        // 设置MQ AMQP端口号
        connectionFactory.setPort(RABBITMQ_PORT);
        // 设置MQ 连接的virtual host
        connectionFactory.setVirtualHost(RABBITMQ_VHOST);
        // 设置MQ 用户名称
        connectionFactory.setUsername(RABBITMQ_USERNAME);
        // 设置MQ 用户密码
        connectionFactory.setPassword(RABBITMQ_PASSWORD);
        try {
            connection = connectionFactory.newConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //返回连接对象
        return connection;
    }

    /**
     * 关闭连接
     */
    @SneakyThrows
    public static void closeConnection(Channel channel, Connection connection) {
        if (channel != null) {
            channel.close();
        }
        if (connection != null) {
            connection.close();
        }
    }

    /**
     * 获取Rabbit连接工厂
     *
     * @param host     主机
     * @param port     端口
     * @param username 用户名
     * @param password 密码
     * @return Rabbit连接工厂
     */
    public static CachingConnectionFactory getConnectionFactory(String host, int port, String username,
            String password) {
        return getConnectionFactory(host, port, username, password, StrPool.SLASH);
    }

    /**
     * 获取Rabbit连接工厂
     *
     * @param addresses   主机地址
     * @param username    用户名
     * @param password    密码
     * @param virtualHost 虚拟主机
     * @return Rabbit连接工厂
     */
    public static CachingConnectionFactory getConnectionFactory(String addresses, String username, String password,
            String virtualHost) {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setAddresses(StrUtil.strip(addresses, "amqp://", null));
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        connectionFactory.setVirtualHost(virtualHost);
        //需要进行消息回调
        connectionFactory.setPublisherConfirmType(ConfirmType.CORRELATED);
        return connectionFactory;
    }

    /**
     * 获取Rabbit连接工厂
     *
     * @param host        主机
     * @param port        端口
     * @param username    用户名
     * @param password    密码
     * @param virtualHost 虚拟主机
     * @return Rabbit连接工厂
     */
    public static CachingConnectionFactory getConnectionFactory(String host, int port, String username, String password,
            String virtualHost) {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost(host);
        connectionFactory.setPort(port);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        connectionFactory.setVirtualHost(virtualHost);
        //需要进行消息回调
        connectionFactory.setPublisherConfirmType(ConfirmType.CORRELATED);
        return connectionFactory;
    }

    public static CachingConnectionFactory getConnectionFactory(StreamBinder streamBinder) {
        CachingConnectionFactory connectionFactory = null;
        if (StrUtil.equals(streamBinder.getType(), "rabbit")) {
            StreamBinderEnvironment environment = streamBinder.getEnvironment();
            String addresses = environment.getAddresses();
            String host = environment.getHost();
            Integer port = environment.getPort();

            if (StrUtil.isNotBlank(addresses)) {
                connectionFactory = RabbitMqUtil.getConnectionFactory(
                        addresses,
                        environment.getUsername(),
                        environment.getPassword(),
                        environment.getVirtualHost()
                );
            } else {
                connectionFactory = RabbitMqUtil.getConnectionFactory(
                        host, port,
                        environment.getUsername(),
                        environment.getPassword(),
                        environment.getVirtualHost()
                );
            }
        }
        return connectionFactory;
    }

    public static RabbitTemplate getRabbitTemplate(
            org.springframework.amqp.rabbit.connection.ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
//        rabbitTemplate.setMessageConverter(new SimpleMessageConverter());
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        //设置发送消息失败重试
        rabbitTemplate.setMandatory(true);
        return rabbitTemplate;
    }

    public static RabbitTemplate getRabbitTemplate(StreamBinder streamBinder) {
        RabbitTemplate rabbitTemplate = null;
        CachingConnectionFactory connectionFactory = getConnectionFactory(streamBinder);
        if (connectionFactory != null) {
            rabbitTemplate = RabbitMqUtil.getRabbitTemplate(connectionFactory);
        }
        return rabbitTemplate;
    }

}
