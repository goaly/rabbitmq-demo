/**
 * Copyright (C), 2023-2033
 */
package com.lys.mybean;

/**
 * ITestContext 子容器上下文测试接口
 *
 * @author: lys
 * @date: 2023/4/3 14:34
 */
public interface ITestContext {


    /**
     * 显示服务名称
     */
    void show();

    /**
     * 返回服务名称
     * @return 服务名称
     */
    String getServiceName();

}
