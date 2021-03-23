package org.zhu.httpclientspringbootstarter.annotation;

import org.springframework.lang.NonNull;

import java.lang.annotation.*;

/**
 * 请求参数名称
 *
 * @author zhuru
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Param {
    /**
     * 参数名称
     */
    String value() default "";
}
