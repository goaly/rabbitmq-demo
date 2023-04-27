package com.lys.producer.demo;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

/**
 * 协同调帐单MQ消息输出源定义接口
 * @author lys
 * @projectName scm-sp
 * @date 2022/05/18
 */
public interface AdjustBillSource {

    String ADJUST_BILL_CONFIRM_OUTPUT = "adjust-bill-confirm-out";

    /**
     * 供应商同意调帐
     * @return
     */
    @Output("adjust-bill-confirm-out")
    MessageChannel adjustBillConfirmSync();
}
