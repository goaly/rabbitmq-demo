package com.lys.namedcontext;

import org.springframework.cloud.context.named.NamedContextFactory;
import org.springframework.stereotype.Component;

/**
 * NamedHttpClient 命名上下文工厂
 * <pre>创建一组子上下文，允许一组specification定义每个子上下文中的bean。
 * 移植自spring-cloud-netflix的FeignClientFactory和SpringClientFactory </pre>
 *
 * @author lys
 */
@Component
public class NamedHttpContext extends NamedContextFactory<NamedHttpClientSpec> {

    public NamedHttpContext() {
        super(NamedHttpClientConfiguration.class, "namedHttpClient", "http.client.name");
    }

}
