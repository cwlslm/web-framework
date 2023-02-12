package com.jfeng.framework.aop.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * 切面抽象类
 * 所有的切面类都要继承它
 *
 * @author jfeng
 * @since 1.0.0
 */
public abstract class AbstractAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractAspect.class);

    /**
     * 拦截发生的匹配条件（切点）
     */
    public boolean intercept(Class<?> cls, Method method, Object[] params) throws Throwable {
        return true;
    }

    /**
     * 开始前增强
     * 无需匹配条件运行，一定会运行
     */
    public void begin(Class<?> cls, Method method, Object[] params) {
    }

    /**
     * 前置增强
     * 需要匹配条件运行
     */
    public void before(Class<?> cls, Method method, Object[] params) throws Throwable {
    }

    /**
     * 后置增强
     * 需要匹配条件运行
     */
    public void after(Class<?> cls, Method method, Object[] params) throws Throwable {
    }

    /**
     * 抛出增强
     * 需要匹配条件并且发生异常才会运行
     */
    public void error(Class<?> cls, Method method, Object[] params) throws Throwable {
    }

    /**
     * 返回后增强
     * 需要匹配条件运行
     */
    public void afterReturn(Class<?> cls, Method method, Object[] params) {
    }

    /**
     * 结束增强
     * 无需匹配条件运行，一定会运行
     */
    public void end(Class<?> cls, Method method, Object[] params) {
    }
}
