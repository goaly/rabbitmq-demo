/**
 * Copyright (C), 2023-2033
 */
package com.lys.mybean;

import lombok.Getter;

/**
 *
 * @author: lys
 * @date: 2023/4/3 14:37
 */
public class MyShowContextBean implements ITestContext {

    /**
     * 服务名称
     */
    @Getter
    private final String serviceName;

    public MyShowContextBean(String serviceName) {
        this.serviceName = serviceName;
    }

    /**
     * 显示服务名称
     */
    @Override
    public void show() {
        System.out.println("MyShowContextBean -> serviceName: " + serviceName);
    }
}
