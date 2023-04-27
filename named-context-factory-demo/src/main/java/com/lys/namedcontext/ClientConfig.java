package com.lys.namedcontext;

import org.apache.http.client.config.RequestConfig;
import org.springframework.core.env.Environment;

/**
 * 客户端配置模型 - 用于实现不同服务场景的配置差异化
 * @author lys
 */
public class ClientConfig {

    /**
     * 服务名称
     */
    private final String serviceName;

    /**
     * 运行环境
     */
    private final Environment env;

    public ClientConfig(String serviceName, Environment env) {
        this.serviceName = serviceName;
        this.env = env;
    }

    public RequestConfig getRequestConfig() {
        Integer socketTimeout = env.getProperty(serviceName + "." + "socketTimeout", Integer.class);
        if (socketTimeout != null) {
            return RequestConfig.custom().setSocketTimeout(socketTimeout).build();
        }

        return RequestConfig.custom().build();
    }

}
