/**
 * Copyright (C), 2022-2032
 */
package com.lys.controller;

import static com.lys.constant.MqBindingConsts.FUNC_MQ_IN_0;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.lys.config.PropertiesConfig;
import com.lys.model.MessageWrapper;
import com.lys.producer.demo.ProducerClientTest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.UUID;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * SendController
 *
 * @author: lys
 * @date: 2022/5/15 20:15
 */
@Api(tags = "MQ发送控制器")
@RestController
@RequestMapping("/send")
@Slf4j
public class SendController {

    /**
     * 装配一个桥 用来连接rabbit或者kafka
     */
    @Autowired
    private StreamBridge streamBridge;

    @Resource
    private ProducerClientTest adjustBillProducerClient;

    @Autowired
    private PropertiesConfig propertiesConfig;

    /**
     * 流式发布MQ
     * @param message 消息
     * @return 操作结果
     */
    @GetMapping("/stream/{message}")
    @ApiOperation("流式发布MQ")
    public String stream(@PathVariable("message") String message) {

        MessageWrapper<String> messageWrapper = MessageWrapper.<String>builder()
                .messageId(UUID.randomUUID().toString())
                .data(message)
                .createTime(DateUtil.date().toTimestamp()).build();

        // 这里说明一下这个 streamBridge.send 方法的参数 第一个参数是exchange或者topic 就是主题名称
        // 默认的主题名称是通过
        // 输出:    <方法名> + -out- + <index>
        // 输入:    <方法名> + -in- + <index>
        // 消费端接收的时候就要用<方法名> 参数是consumer<MessageIdempotent<String>>接收
        streamBridge.send(FUNC_MQ_IN_0, messageWrapper);
        log.info("streamBridge send MQ to {}, message：{}", FUNC_MQ_IN_0,
                JSON.toJSONString(messageWrapper));

        return "direct send user ok, message: " + message;
    }

    /**
     * 流式发布确认调帐MQ
     * @param message 消息
     * @return 操作结果
     */
    @GetMapping("/confirmAdjust/{message}")
    @ApiOperation("流式发布确认调帐MQ")
    public String confirmAdjust(@PathVariable("message") String message) {

        MessageWrapper<String> messageWrapper = MessageWrapper.<String>builder()
                .messageId(UUID.randomUUID().toString())
                .data(message)
                .createTime(DateUtil.date().toTimestamp()).build();

        String bindingName = "confirmAdjust-in-0";
        streamBridge.send(bindingName, messageWrapper);
        log.info("streamBridge send MQ to {}, message：{}", bindingName,
                JSON.toJSONString(messageWrapper));

        return "direct send user ok, message: " + message;
    }

    /**
     * 流式发布大小写转换MQ
     * @param message 消息
     * @return 操作结果
     */
    @GetMapping("/uppercase/{message}")
    @ApiOperation("流式发布大小写转换MQ")
    public String uppercase(@PathVariable("message") String message) {

        String bindingName = "uppercase.exchange";

//        streamBridge.send(bindingName, "scm", message, MimeTypeUtils.TEXT_PLAIN);

        MessageWrapper<String> messageWrapper = MessageWrapper.<String>builder()
                .messageId(UUID.randomUUID().toString())
                .data(message)
                .createTime(DateUtil.date().toTimestamp()).build();
        streamBridge.send(bindingName, "scm", messageWrapper, MimeTypeUtils.APPLICATION_JSON);

        log.info("streamBridge send MQ to {}, message：{}", bindingName, message);

        return "direct send user ok, message: " + message;
    }

    /**
     * 测试MQ生产者客户端 - 确认调帐
     */
    @ApiOperation("测试MQ生产者客户端 - 确认调帐")
    @GetMapping("/confirmByProducerClient/{message}")
    public void confirmByProducerClient(@PathVariable("message") String message) {
        adjustBillProducerClient.confirm(message);
    }

    /**
     * 测试MQ生产者客户端 - 大小写转换
     */
    @GetMapping("/uppercaseByProducerClient/{text}")
    @ApiOperation("测试MQ生产者客户端 - 大小写转换")
    public void uppercaseByProducerClient(@PathVariable("text") String text) {
        adjustBillProducerClient.uppercase(text);
    }

}
