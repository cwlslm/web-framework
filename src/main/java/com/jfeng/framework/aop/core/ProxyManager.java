package com.jfeng.framework.aop.core;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 代理管理器
 *
 * @author jfeng
 * @since 1.0.0
 */
@SuppressWarnings("unchecked")
public class ProxyManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProxyManager.class);

    /**
     * 创建代理类实例
     * @param targetClass 目标类
     * @param aspectInterfaceList 切面类列表
     * @param <T> 目标类 targetClass
     * @return 代理类实例
     */
    public static <T> T createProxy(final Class<?> targetClass,
                                    final List<AbstractAspect> aspectInterfaceList) {
        return (T) Enhancer.create(targetClass, new MethodInterceptor() {
            @Override
            public Object intercept(Object targetObject, Method targetMethod, Object[] methodParams,
                                    MethodProxy methodProxy) throws Throwable {
                return new AspectChain(targetClass, targetObject, targetMethod, methodProxy,
                        methodParams, aspectInterfaceList).run();
            }
        });
    }
}
