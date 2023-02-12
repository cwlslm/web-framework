package com.jfeng.framework.aop.core;

import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 切面链
 *
 * @author jfeng
 * @since 1.0.0
 */
public class AspectChain {

    private final Class<?> targetClass;
    private final Object targetObject;
    private final Method targetMethod;
    private final MethodProxy methodProxy;
    private final Object[] methodParams;
    private final List<AbstractAspect> aspectList;
    private int aspectIndex = 0;
    
    public AspectChain(Class<?> targetClass, Object targetObject, Method targetMethod,
                       MethodProxy methodProxy, Object[] methodParams,
                       List<AbstractAspect> aspectList) {
        this.targetClass = targetClass;
        this.targetObject = targetObject;
        this.targetMethod = targetMethod;
        this.methodProxy = methodProxy;
        this.methodParams = methodParams;
        this.aspectList = aspectList;
    }

    public Object[] getMethodParams() {
        return methodParams;
    }

    public Class<?> getTargetClass() {
        return targetClass;
    }

    public Method getTargetMethod() {
        return targetMethod;
    }

    /**
     * 内部递归调用
     * 每次调用返回下一层递归的执行结果，最终返回原函数执行结果
     */
    public Object run() throws Throwable {
        Object methodResults = null;
        if (aspectIndex < aspectList.size()) {
            AbstractAspect aspect = aspectList.get(aspectIndex++);

            Exception exception = null;

            aspect.begin(targetClass, targetMethod, methodParams);

            if (aspect.intercept(targetClass, targetMethod, methodParams)) {
                try {
                    aspect.before(targetClass, targetMethod, methodParams);
                    methodResults = this.run();
                    aspect.after(targetClass, targetMethod, methodParams);
                }  catch (Exception e) {
                    exception = e;
                    aspect.error(targetClass, targetMethod, methodParams);
                } finally {
                    aspect.afterReturn(targetClass, targetMethod, methodParams);
                }
            } else {
                methodResults = this.run();
            }

            aspect.end(targetClass, targetMethod, methodParams);

            if (exception != null) {
                throw exception;
            }
        } else {
            methodResults = methodProxy.invokeSuper(targetObject, methodParams);
        }
        return methodResults;
    }
}
