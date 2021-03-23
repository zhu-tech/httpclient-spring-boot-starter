package org.zhu.httpclientspringbootstarter.registrar;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.zhu.httpclientspringbootstarter.annotation.EnableHttpClient;

/**
 * HttpClient扫描器
 *
 * @author zhuru
 */
public class HttpClientAutoConfigureRegistrar implements ImportBeanDefinitionRegistrar {


    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry beanDefinitionRegistry) {
        // 获取MapperScan注解属性信息
        AnnotationAttributes annotationAttributes = AnnotationAttributes.fromMap(annotationMetadata.getAnnotationAttributes(EnableHttpClient.class.getName()));
        // 获取注解的属性值,拿到定义的扫描路径
        String[] basePackages = annotationAttributes.getStringArray("basePackage");
        // 使用自定义扫描器扫描
        HttpClientBeanDefinitionScanner scanner = new HttpClientBeanDefinitionScanner(beanDefinitionRegistry, false);
        scanner.doScan(basePackages);
    }

}
