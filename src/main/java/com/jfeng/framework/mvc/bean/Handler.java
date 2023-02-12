package com.jfeng.framework.mvc.bean;

import com.jfeng.framework.core.bean.BaseBean;

import java.lang.reflect.Method;
import java.util.List;
import java.util.regex.Matcher;

/**
 * 封装 Action 信息
 *
 * @author jfeng
 * @since 1.0.0
 */
public class Handler extends BaseBean {

    /**
     * Controller 类
     */
    private Class<?> controllerClass;

    /**
     * Action 方法
     */
    private Method actionMethod;

    /**
     * 请求路径参数名称数组
     */
    private List<String> requestPathParamNames;

    /**
     * 请求路径正则匹配器
     */
    private Matcher requestPathMatcher;

    public Handler(Class<?> controllerClass, Method actionMethod, List<String> requestPathParamNames) {
        this.controllerClass = controllerClass;
        this.actionMethod = actionMethod;
        this.requestPathParamNames = requestPathParamNames;
    }

    public Class<?> getControllerClass() {
        return controllerClass;
    }

    public Method getActionMethod() {
        return actionMethod;
    }

    public Matcher getRequestPathMatcher() {
        return requestPathMatcher;
    }

    public void setRequestPathMatcher(Matcher requestPathMatcher) {
        this.requestPathMatcher = requestPathMatcher;
    }

    public List<String> getRequestPathParamNames() {
        return requestPathParamNames;
    }
}
