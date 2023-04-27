package com.lys.producer.demo;

import com.alibaba.fastjson.JSONObject;
import com.lys.model.MessageWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;

/**
 * 协同-->鸿鹄 调账单同意、拒绝调帐MQ生产者
 *
 * @author lys
 * @projectName scm-sp
 * @date 2022/5/17
 */
@Slf4j
//@EnableBinding(AdjustBillSource.class)
public class AdjustBillSyncProducer {

    @Autowired
    private AdjustBillSource adjustBillSource;

    /**
     * 供应商同意调帐MQ
     *
     * @param confirmMessage
     */
    public void adjustBillConfirmSync(MessageWrapper<String> confirmMessage) {
        log.info("【send】供应商同意调帐mq同步：{}", JSONObject.toJSONString(confirmMessage));
        adjustBillSource.adjustBillConfirmSync().send(
            MessageBuilder.withPayload(confirmMessage).build()
        );
    }
}
