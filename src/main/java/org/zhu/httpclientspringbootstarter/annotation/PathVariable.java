package org.zhu.httpclientspringbootstarter.annotation;

import org.springframework.lang.NonNull;

import java.lang.annotation.*;

/**
 * 支持参数路径
 *
 * @author zhuru
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PathVariable {

    @NonNull
    String value() default "";
}
