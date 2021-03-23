package org.zhu.httpclientspringbootstarter.annotation;

import org.springframework.lang.NonNull;

import java.lang.annotation.*;

/**
 * Http请求注解
 *
 * @author zhuru
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ResponseLine {
    /**
     * 请求路径
     */
    @NonNull
    String value() default "";
}
