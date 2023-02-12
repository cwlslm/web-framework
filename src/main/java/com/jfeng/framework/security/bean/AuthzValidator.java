package com.jfeng.framework.security.bean;

import com.jfeng.framework.core.bean.BaseBean;

import java.lang.reflect.Method;

/**
 * 保存权限验证器的相关信息（类和函数）
 */
public class AuthzValidator extends BaseBean {

    private Class<?> cls;

    private Method method;

    public AuthzValidator(Class<?> cls, Method method) {
        this.cls = cls;
        this.method = method;
    }

    public Class<?> getCls() {
        return cls;
    }

    public void setCls(Class<?> cls) {
        this.cls = cls;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }
}
