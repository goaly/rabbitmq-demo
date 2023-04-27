package com.lys.namedcontext;

import lombok.Getter;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

/**
 * 自定义Http客户端
 * @author lys
 */
public class NamedHttpClient {

    /**
     * 服务名称
     */
    @Getter
    private final String serviceName;

    /**
     * 代理的apache Http客户端
     */
    @Getter
    private final CloseableHttpClient httpClient;

    /**
     * 代理的apache 请求配置
     */
    @Getter
    private final RequestConfig requestConfig;

    public NamedHttpClient(String serviceName, ClientConfig clientConfig) {
        this.serviceName = serviceName;
        this.requestConfig = clientConfig.getRequestConfig();
        this.httpClient = HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .build();
    }

}
