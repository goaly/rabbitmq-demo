/**
 * Copyright (C), 2022-2032
 */
package com.lys.config;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.Opt;
import cn.hutool.core.text.NamingCase;
import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.StrUtil;
import com.lys.model.StreamBinder;
import com.lys.model.StreamBinderChangeEvent;
import com.lys.model.StreamBinderEnvironment;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import lombok.Getter;
import org.nutz.lang.Mirror;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.MutablePropertySources;

/**
 * CustomPropertiesConfig
 *
 * @author: lys
 * @date: 2022/12/29 17:09
 */
@Configuration("streamBindersConfig")
@ConditionalOnClass({ConfigurableEnvironment.class, ApplicationEventPublisher.class})
public class StreamBindersConfig {

    public final static String DEFAULT_BINDER_PROP_NAME = "spring.cloud.stream.default-binder";

    public final static String DEFAULT_BINDER_NAME = "defaultRabbit";

    public final static String PREFIX_STREAM_BINDERS = "spring.cloud.stream.binders";

    public final static String PREFIX_ENVIRONMENT_RABBITMQ = "environment.spring.rabbitmq";

    private final static Mirror<StreamBinder> MIRROR_STREAM_BINDER = Mirror.me(StreamBinder.class);

    private final static Mirror<StreamBinderEnvironment> MIRROR_STREAM_BINDER_ENVIRONMENT = Mirror.me(
            StreamBinderEnvironment.class);

    private final static Field[] STREAM_BINDER_FIELDS = MIRROR_STREAM_BINDER.getFields();

    private final static Field[] STREAM_BINDER_ENVIRONMENT_FIELDS = MIRROR_STREAM_BINDER_ENVIRONMENT.getFields();

    @Getter
    public final Map<String, StreamBinder> streamBinderMap = new ConcurrentHashMap<>();

    @Getter
    public StreamBinder defaultBinder;

    @Resource
    private ConfigurableEnvironment springEnv;

    @Resource
    private ApplicationEventPublisher eventPublisher;

    public StreamBindersConfig(ConfigurableEnvironment springEnv) {
        this.springEnv = springEnv;
    }

    @PostConstruct
    public void init() {

        MutablePropertySources propertySources = springEnv.getPropertySources();
        // 获取所有binders配置
        Map<String, String> binderProps = StreamSupport.stream(propertySources.spliterator(), false)
                .filter(ps -> ps instanceof EnumerablePropertySource)
                .map(ps -> ((EnumerablePropertySource<?>) ps).getPropertyNames())
                .flatMap(Arrays::stream)
                .distinct()
                .filter(propName -> StrUtil.startWith(propName, PREFIX_STREAM_BINDERS))
                .collect(Collectors.toMap(Function.identity(), springEnv::getProperty));

        String defaultBinderName = DEFAULT_BINDER_NAME;
        for (Entry<String, String> entry : binderProps.entrySet()) {
            String propName = entry.getKey(), propValue = entry.getValue();
            if (StrUtil.equals(propName, DEFAULT_BINDER_PROP_NAME)) {
                // 默认binder名称
                defaultBinderName  = propValue;
                continue;
            }
            retrieveStreamBinder(propName, propValue);
        }
        defaultBinder = streamBinderMap.get(defaultBinderName);
    }

    /**
     * 找回StreamBinder配置
     *
     * @param propName  属性名
     * @param propValue 属性值
     */
    private StreamBinder retrieveStreamBinder(String propName, String propValue) {
        if (!StrUtil.startWith(propName, PREFIX_STREAM_BINDERS)) {
            return null;
        }
        // 去除前缀
        propName = StrUtil.strip(propName, PREFIX_STREAM_BINDERS + StrPool.DOT, null);
        String binderName = StrUtil.subBefore(propName, StrPool.DOT, false);
        streamBinderMap.putIfAbsent(binderName, StreamBinder.builder()
                .name(binderName)
                .build());
        StreamBinder streamBinder = streamBinderMap.get(binderName);

        for (Field streamBinderField : STREAM_BINDER_FIELDS) {
            String binderFieldName = streamBinderField.getName();
            if (ifPropNameMatched(propName, binderFieldName, binderName + StrPool.DOT)) {
                MIRROR_STREAM_BINDER.setValue(streamBinder, streamBinderField,
                        Convert.convert(streamBinderField.getType(), propValue));
            } else if (StrUtil.startWith(propName, binderName + StrPool.DOT + binderFieldName)) {
                if (StrUtil.equals(binderFieldName, "environment")) {
                    if (streamBinder.getEnvironment() == null) {
                        streamBinder.setEnvironment(new StreamBinderEnvironment());
                    }
                    StreamBinderEnvironment binderEnvironment = streamBinder.getEnvironment();
                    for (Field binderEnvField : STREAM_BINDER_ENVIRONMENT_FIELDS) {
                        String binderEnvFieldName = binderEnvField.getName();
                        if (ifPropNameMatched(propName, binderEnvFieldName,
                                binderName + StrPool.DOT + PREFIX_ENVIRONMENT_RABBITMQ + StrPool.DOT)) {
                            MIRROR_STREAM_BINDER_ENVIRONMENT.setValue(binderEnvironment, binderEnvField,
                                    Convert.convert(binderEnvField.getType(), propValue));
                        }
                    }
                }
            }
        }
        return streamBinder;
    }

    @EventListener
    public void onEnvironmentChange(EnvironmentChangeEvent envChangEvent) {
        Set<String> keys = envChangEvent.getKeys();
        Set<String> binderNames = new HashSet<>(CollUtil.size(keys));
        for (String key : keys) {
            Opt.ofNullable(retrieveStreamBinder(key, this.springEnv.getProperty(key)))
                    .map(StreamBinder::getName)
                    .ifPresent(binderNames::add);
        }
        if (binderNames.size() > 0) {
            eventPublisher.publishEvent(new StreamBinderChangeEvent(binderNames));
        }
    }

    private boolean ifPropNameMatched(String propName, String fieldName, String prefix) {
        return ifPropNameMatched(propName, fieldName, prefix, null);
    }

    private boolean ifPropNameMatched(String propName, String fieldName, String prefix, String suffix) {

        boolean ifMatched = StrUtil.equals(propName,
                StrUtil.addSuffixIfNot(StrUtil.addPrefixIfNot(fieldName, prefix), suffix));
        if (!ifMatched) {
            fieldName = NamingCase.toKebabCase(fieldName);
            ifMatched = StrUtil.equals(propName,
                    StrUtil.addSuffixIfNot(StrUtil.addPrefixIfNot(fieldName, prefix), suffix));
        }
        return ifMatched;
    }

}