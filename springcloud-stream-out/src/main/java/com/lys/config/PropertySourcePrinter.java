/**
 * Copyright (C), 2022-2032
 */
package com.lys.config;

import cn.hutool.core.text.StrFormatter;
import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.StrUtil;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.annotation.Resource;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.stereotype.Component;

/**
 * PropertySourcePrinter
 *
 * @author: lys
 * @date: 2022/12/29 17:48
 */
@Component
public class PropertySourcePrinter implements CommandLineRunner {

    @Resource
    private ConfigurableEnvironment springEnv;

    /**
     * Callback used to run the bean.
     *
     * @param args incoming main method arguments
     * @throws Exception on error
     */
    @Override
    public void run(String... args) throws Exception {

        MutablePropertySources propertySources = springEnv.getPropertySources();

        // 获取所有配置
        Map<String, String> props = StreamSupport.stream(propertySources.spliterator(), false)
                .filter(ps -> ps instanceof EnumerablePropertySource)
                .map(ps -> ((EnumerablePropertySource<?>) ps).getPropertyNames())
                .flatMap(Arrays::stream)
                .distinct()
                .collect(Collectors.toMap(Function.identity(), springEnv::getProperty));

        // key 和 value 之间的最小间隙
        int interval = 20;
        // 找到最长的key
        int max = props.keySet().stream().max(Comparator.comparingInt(String::length)).orElse("").length();

        // 打印
        String separators = String.join(StrUtil.EMPTY, Collections.nCopies(max, StrPool.DASHED));
        System.out.println(StrFormatter.format("{} 所有配置 Start{}", separators, separators));
        props.keySet().stream().sorted().forEach(k -> {
            int i = max - k.length() + interval;
            String join = String.join(StrUtil.EMPTY, Collections.nCopies(i, StrUtil.SPACE));
            System.out.printf("%s%s%s%n", k, join, props.get(k));
        });
        System.out.println(StrFormatter.format("{} 所有配置 End{}", separators, separators));
    }
}
