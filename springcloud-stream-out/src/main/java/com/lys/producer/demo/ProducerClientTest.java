package com.lys.producer.demo;

import com.lys.producer.annotation.ProducerBinding;
import com.lys.producer.annotation.ProducerClient;

/**
 * MQ生产者客户端注解测试
 *
 * @author lys
 */
@ProducerClient(value = "producerClientTest", binder = "scm")
public interface ProducerClientTest {

    /**
     * 确认消息
     *
     * @param message 消息内容
     */
    @ProducerBinding(bindingName = "${mq.producer.bindingName}")
    void confirm(String message);

    /**
     * 大写转换
     *
     * @param text 文本内容
     */
//    @ProducerBinding(bindingName = "uppercase.exchange", nameFormat = false)
    @ProducerBinding
    void uppercase(String text);
}
