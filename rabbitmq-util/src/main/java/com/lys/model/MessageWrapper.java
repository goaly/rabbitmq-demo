/**
 * Copyright (C), 2022-2032
 */
package com.lys.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * FileName: MessageIdempotent
 * Description: 消息幂等对象
 *
 * @author: lys
 * @date: 2022/5/12 18:58
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value="MessageIdempotent", description = "消息幂等对象")
public class MessageWrapper<T> implements Serializable {

    private static final long serialVersionUID = -1436237514857905208L;

    @ApiModelProperty("消息ID")
    private String messageId;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS", timezone = "GMT+8")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss.SSS")
    @ApiModelProperty(value = "消息创建时间", example = "2022-05-12 16:12:01.122")
    private Timestamp createTime;

    @ApiModelProperty("消息承载数据体")
    private T data;

    @ApiModelProperty("是否为重复回调")
    private Boolean returnCallback;

}
