package com.jfeng.framework.mvc.annotation;

import java.lang.annotation.*;

/**
 * 定义Action异常处理函数
 *
 * @author jfeng
 * @since 1.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ActionException {

    /**
     * 设置对应的异常类型
     */
    Class<? extends Throwable> value();
}
