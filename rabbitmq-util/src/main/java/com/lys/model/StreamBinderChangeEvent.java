/**
 * Copyright (C), 2023-2033
 */
package com.lys.model;

import java.util.Set;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * StreamBinderChangeEvent
 *
 * @author: lys
 * @date: 2023/1/4 16:50
 */
public class StreamBinderChangeEvent extends ApplicationEvent {

    @Getter
    private Set<String> binderNames;

    public StreamBinderChangeEvent(Set<String> binderNames) {
        this(binderNames, binderNames);
    }

    public StreamBinderChangeEvent(Object context, Set<String> binderNames) {
        super(context);
        this.binderNames = binderNames;
    }
}
