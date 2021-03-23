package org.zhu.httpclientspringbootstarter.annotation;

import org.springframework.lang.NonNull;

import java.lang.annotation.*;

/**
 * 标注该类为http客户端
 *
 * @author zhuru
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface HttpClient {
    /**
     * 远程服务器地址
     */
    @NonNull
    String remote() default "";

    /**
     * 请求路径
     */
    @NonNull
    String path() default "";
}
