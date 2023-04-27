/**
 * Copyright (C), 2022-2032
 */
package com.lys.producer;

import com.lys.producer.annotation.EnableProducerClients;
import com.lys.producer.annotation.ProducerClient;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.*;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AbstractClassTestingTypeFilter;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;

/**
 * ProducerClientsRegistrar MQ生产者客户端注册器
 *
 * @author: lys
 * @date: 2022/5/29 22:27
 */
public class ProducerClientsRegistrar implements ImportBeanDefinitionRegistrar,
        ResourceLoaderAware, EnvironmentAware, BeanFactoryAware {

    private ResourceLoader resourceLoader;

    private Environment environment;

    private BeanFactory beanFactory;

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
        registerProducerClients(metadata, registry);
    }

    public void registerProducerClients(AnnotationMetadata metadata,
                                        BeanDefinitionRegistry registry) {
        ClassPathScanningCandidateComponentProvider scanner = getScanner();
        scanner.setResourceLoader(this.resourceLoader);

        Set<String> basePackages;

        Map<String, Object> attrs = metadata
                .getAnnotationAttributes(EnableProducerClients.class.getName());
        AnnotationTypeFilter annotationTypeFilter = new AnnotationTypeFilter(
                ProducerClient.class);
        final Class<?>[] clients = attrs == null ? null
                : (Class<?>[]) attrs.get("clients");
        if (clients == null || clients.length == 0) {
            scanner.addIncludeFilter(annotationTypeFilter);
            basePackages = getBasePackages(metadata);
        } else {
            final Set<String> clientClasses = new HashSet<>();
            basePackages = new HashSet<>();
            for (Class<?> clazz : clients) {
                basePackages.add(ClassUtils.getPackageName(clazz));
                clientClasses.add(clazz.getCanonicalName());
            }
            AbstractClassTestingTypeFilter filter = new AbstractClassTestingTypeFilter() {
                @Override
                protected boolean match(ClassMetadata metadata) {
                    String cleaned = metadata.getClassName().replaceAll("\\$", ".");
                    return clientClasses.contains(cleaned);
                }
            };
            scanner.addIncludeFilter(
                    new AllTypeFilter(Arrays.asList(filter, annotationTypeFilter)));
        }

        for (String basePackage : basePackages) {
            Set<BeanDefinition> candidateComponents = scanner
                    .findCandidateComponents(basePackage);
            for (BeanDefinition candidateComponent : candidateComponents) {
                if (candidateComponent instanceof AnnotatedBeanDefinition) {
                    // verify annotated class is an interface
                    AnnotatedBeanDefinition beanDefinition = (AnnotatedBeanDefinition) candidateComponent;
                    AnnotationMetadata annotationMetadata = beanDefinition.getMetadata();
                    Assert.isTrue(annotationMetadata.isInterface(),
                            "@ProducerClient can only be specified on an interface");

                    Map<String, Object> attributes = annotationMetadata
                            .getAnnotationAttributes(
                                    ProducerClient.class.getCanonicalName());

                    String name = getClientName(attributes);

                    registerProducerClient(registry, annotationMetadata, attributes);
                }
            }
        }
    }

    /**
     * 注册ProducerClient实例
     * @see ProducerClientsRegistrar#registerProducerClient
     * @param registry
     * @param annotationMetadata
     * @param attributes
     */
    private void registerProducerClient(BeanDefinitionRegistry registry, AnnotationMetadata annotationMetadata,
                                        Map<String, Object> attributes) {
        String className = annotationMetadata.getClassName();
        Class clazz = ClassUtils.resolveClassName(className, null);
        ConfigurableBeanFactory beanFactory = registry instanceof ConfigurableBeanFactory
                ? (ConfigurableBeanFactory) registry : null;
        String contextId = getContextId(beanFactory, attributes);
        String name = getName(attributes);
        ProducerClientFactoryBean factoryBean = new ProducerClientFactoryBean();
        factoryBean.setBeanFactory(beanFactory);
        factoryBean.setName(name);
        factoryBean.setContextId(contextId);
        factoryBean.setType(clazz);

        BeanDefinitionBuilder definition = BeanDefinitionBuilder.genericBeanDefinition(clazz, () -> {

            return factoryBean.getObject();
        });

        AbstractBeanDefinition beanDefinition = definition.getBeanDefinition();

        boolean primary = (Boolean)attributes.get("primary"); // has a default, won't be null
        beanDefinition.setPrimary(primary);

        String[] qualifiers = getQualifiers(attributes);
        if (ObjectUtils.isEmpty(qualifiers)) {
            qualifiers = new String[] { contextId + "ProducerClient" };
        }

        BeanDefinitionHolder holder = new BeanDefinitionHolder(beanDefinition, className,
                qualifiers);
        BeanDefinitionReaderUtils.registerBeanDefinition(holder, registry);

    }

    String getName(Map<String, Object> attributes) {
        return getName(null, attributes);
    }

    String getName(ConfigurableBeanFactory beanFactory, Map<String, Object> attributes) {
        String name = (String) attributes.get("name");
        if (!StringUtils.hasText(name)) {
            name = (String) attributes.get("value");
        }
        name = resolve(beanFactory, name);
        return getName(name);
    }

    static String getName(String name) {
        if (!StringUtils.hasText(name)) {
            return "";
        }
        return name;
    }

    protected ClassPathScanningCandidateComponentProvider getScanner() {
        return new ClassPathScanningCandidateComponentProvider(false, this.environment) {
            @Override
            protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
                boolean isCandidate = false;
                if (beanDefinition.getMetadata().isIndependent()) {
                    if (!beanDefinition.getMetadata().isAnnotation()) {
                        isCandidate = true;
                    }
                }
                return isCandidate;
            }
        };
    }

    protected Set<String> getBasePackages(AnnotationMetadata importingClassMetadata) {
        Map<String, Object> attributes = importingClassMetadata
                .getAnnotationAttributes(EnableProducerClients.class.getCanonicalName());

        Set<String> basePackages = new HashSet<>();
        for (String pkg : (String[]) attributes.get("value")) {
            if (StringUtils.hasText(pkg)) {
                basePackages.add(pkg);
            }
        }
        for (String pkg : (String[]) attributes.get("basePackages")) {
            if (StringUtils.hasText(pkg)) {
                basePackages.add(pkg);
            }
        }
        for (Class<?> clazz : (Class[]) attributes.get("basePackageClasses")) {
            basePackages.add(ClassUtils.getPackageName(clazz));
        }

        if (basePackages.isEmpty()) {
            basePackages.add(
                    ClassUtils.getPackageName(importingClassMetadata.getClassName()));
        }
        return basePackages;
    }

    private String getContextId(ConfigurableBeanFactory beanFactory, Map<String, Object> attributes) {
        String contextId = (String) attributes.get("contextId");
        if (!StringUtils.hasText(contextId)) {
            return getName(beanFactory, attributes);
        }

        contextId = resolve(beanFactory, contextId);
        return getName(contextId);
    }

    private String getClientName(Map<String, Object> client) {
        if (client == null) {
            return null;
        }
        String value = (String) client.get("value");
        if (!StringUtils.hasText(value)) {
            value = (String) client.get("name");
        }
        if (!StringUtils.hasText(value)) {
            value = (String) client.get("serviceId");
        }
        if (StringUtils.hasText(value)) {
            return value;
        }

        throw new IllegalStateException("Either 'name' or 'value' must be provided in @"
                + ProducerClient.class.getSimpleName());
    }

    /**
     * resolve 解析value中的表达式
     *
     * @param beanFactory bean工厂
     * @param value       注解属性值
     * @return value解析结果
     */
    private String resolve(ConfigurableBeanFactory beanFactory, String value) {
        if (StringUtils.hasText(value)) {
            if (beanFactory == null) {
                return this.environment.resolvePlaceholders(value);
            }
            BeanExpressionResolver resolver = beanFactory.getBeanExpressionResolver();
            String resolved = beanFactory.resolveEmbeddedValue(value);
            if (resolver == null) {
                return resolved;
            }
            Object evaluateValue = resolver.evaluate(resolved, new BeanExpressionContext(beanFactory, null));
            if (evaluateValue != null) {
                return String.valueOf(evaluateValue);
            }
            return null;
        }
        return value;
    }

    /**
     * Helper class to create a {@link TypeFilter} that matches if all the delegates
     * match.
     *
     * @author Oliver Gierke
     */
    private static class AllTypeFilter implements TypeFilter {

        private final List<TypeFilter> delegates;

        /**
         * Creates a new {@link AllTypeFilter} to match if all the given delegates match.
         *
         * @param delegates must not be {@literal null}.
         */
        public AllTypeFilter(List<TypeFilter> delegates) {
            Assert.notNull(delegates, "This argument is required, it must not be null");
            this.delegates = delegates;
        }

        @Override
        public boolean match(MetadataReader metadataReader,
                             MetadataReaderFactory metadataReaderFactory) throws IOException {

            for (TypeFilter filter : this.delegates) {
                if (!filter.match(metadataReader, metadataReaderFactory)) {
                    return false;
                }
            }

            return true;
        }
    }

    private String[] getQualifiers(Map<String, Object> client) {
        if (client == null) {
            return null;
        }
        List<String> qualifierList = new ArrayList<>(Arrays.asList((String[]) client.get("qualifiers")));
        return !qualifierList.isEmpty() ? qualifierList.toArray(new String[0]) : null;
    }
}
