package org.zhu.httpclientspringbootstarter.registrar;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.zhu.httpclientspringbootstarter.annotation.HttpClient;
import org.zhu.httpclientspringbootstarter.factory.HttpClientProxyFactory;

import java.util.Set;

/**
 * HttpClient注解扫描器
 *
 * @author zhuru
 */
public class HttpClientBeanDefinitionScanner extends ClassPathBeanDefinitionScanner {

    public HttpClientBeanDefinitionScanner(BeanDefinitionRegistry registry, boolean useDefaultFilters) {
        super(registry, useDefaultFilters);
    }

    @Override
    protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
        addIncludeFilter(new AnnotationTypeFilter(HttpClient.class));
        Set<BeanDefinitionHolder> beanDefinitionHolders = super.doScan(basePackages);
        // 对扫描到的数据进行代理处理
        processBeanDefinitions(beanDefinitionHolders);
        return beanDefinitionHolders;
    }

    private void processBeanDefinitions(Set<BeanDefinitionHolder> beanDefinitionHolderSet) {
        beanDefinitionHolderSet.forEach(e -> {
            // 设置工厂等操作需要基于GenericBeanDefinition，BeanDefinitionHolder是其子类
            GenericBeanDefinition definition = (GenericBeanDefinition) e.getBeanDefinition();
            // 获取接口的全路径名称
            String beanClassName = definition.getBeanClassName();
            // 设置构造函数参数
            definition.getConstructorArgumentValues().addGenericArgumentValue(beanClassName);
            // 设置工厂
            definition.setBeanClass(HttpClientProxyFactory.class);
            definition.setAutowireMode(GenericBeanDefinition.AUTOWIRE_BY_TYPE);
        });
    }

    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        return beanDefinition.getMetadata().isInterface() && beanDefinition.getMetadata().isIndependent();
    }
}
