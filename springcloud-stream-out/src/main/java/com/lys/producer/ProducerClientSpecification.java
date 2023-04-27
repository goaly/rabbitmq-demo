/**
 * Copyright (C), 2022-2032
 */
package com.lys.producer;

import lombok.Data;
import org.springframework.cloud.context.named.NamedContextFactory;

/**
 * 定制ProducerClient子容器的接口
 *
 * @author: lys
 * @date: 2022/5/30 9:18
 */
@Data
public class ProducerClientSpecification implements NamedContextFactory.Specification {

    private final String name;
    private final Class<?>[] configuration;

    public ProducerClientSpecification(String name, Class<?>[] configuration) {
        this.name = name;
        this.configuration = configuration;
    }
}
