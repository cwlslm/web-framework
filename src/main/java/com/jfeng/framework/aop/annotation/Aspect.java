package com.jfeng.framework.aop.annotation;

import java.lang.annotation.*;

/**
 * 切面注解
 * 指定一个切面类的作用范围
 * 作用在所有加了 [value] 注解的类上
 *
 * @author jfeng
 * @since 1.0.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Aspect {

    /**
     * 注解
     */
    Class<? extends Annotation> value();
}
