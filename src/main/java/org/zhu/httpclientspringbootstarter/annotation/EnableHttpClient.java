package org.zhu.httpclientspringbootstarter.annotation;

import org.springframework.context.annotation.Import;
import org.zhu.httpclientspringbootstarter.registrar.HttpClientAutoConfigureRegistrar;

import java.lang.annotation.*;

/**
 * 扫描http客户端
 *
 * @author zhuru
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(HttpClientAutoConfigureRegistrar.class)
public @interface EnableHttpClient {
    /**
     * 配置扫描路径
     */
    String[] basePackage();
}
