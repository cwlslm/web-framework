package com.jfeng.framework.mvc.bean;

import com.jfeng.framework.core.bean.BaseBean;

import java.lang.reflect.Method;
import java.util.List;
import java.util.regex.Matcher;

/**
 * 封装Action异常处理函数的信息
 *
 * @author jfeng
 * @since 1.0.0
 */
public class ActionExceptionHandler extends BaseBean {

    /**
     * Configuration类
     */
    private Class<?> configurationClass;

    /**
     * Action异常处理函数
     */
    private Method actionExceptionMethod;

    public ActionExceptionHandler (Class<?> configurationClass, Method actionExceptionMethod) {
        this.configurationClass = configurationClass;
        this.actionExceptionMethod = actionExceptionMethod;
    }

    public Class<?> getConfigurationClass() {
        return configurationClass;
    }

    public void setConfigurationClass(Class<?> configurationClass) {
        this.configurationClass = configurationClass;
    }

    public Method getActionExceptionMethod() {
        return actionExceptionMethod;
    }

    public void setActionExceptionMethod(Method actionExceptionMethod) {
        this.actionExceptionMethod = actionExceptionMethod;
    }
}
