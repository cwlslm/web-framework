package com.jfeng.framework.mvc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 定义某方法参数为请求参数，并设置对应的参数名称和默认值
 *
 * @author jfeng
 * @since 1.0.0
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestVariable {

    /**
     * 请求参数对应的名称
     */
    String value();

    /**
     * 默认值
     */
    String defaultValue() default "";
}
