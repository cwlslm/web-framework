package com.jfeng.framework.core;

import com.jfeng.framework.mvc.annotation.Configuration;
import com.jfeng.framework.mvc.annotation.Controller;
import com.jfeng.framework.mvc.annotation.Service;
import com.jfeng.framework.security.SecurityHelper;
import com.jfeng.framework.transaction.TransactionHelper;
import com.jfeng.framework.util.ClassUtil;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

/**
 * 类操作助手类
 *
 * @author jfeng
 * @since 1.0.0
 */
public class ClassHelper {

    /**
     * 定义类集合（用于存放被加载的类）
     */
    private static final Set<Class<?>> CLASS_SET = new HashSet<>();

    static {
        // 添加应用包内的所有类
        String basePackage = ConfigHelper.getAppBasePackage();
        CLASS_SET.addAll(ClassUtil.getClassSet(basePackage));
    }

    /**
     * 获取应用包名下的所有类
     */
    public static Set<Class<?>> getClassSet() {
        return CLASS_SET;
    }

    /**
     * 手动添加类到 CLASS_SET
     */
    public static void addClass(Class<?> cls) {
        CLASS_SET.add(cls);
    }

    /**
     * 手动添加类到 CLASS_SET
     */
    public static void addClass(Set<Class<?>> clsSet) {
        CLASS_SET.addAll(clsSet);
    }

    /**
     * 获取应用包名下带有某注解的所有类
     */
    public static Set<Class<?>> getClassSetByAnnotation(Class<? extends Annotation> annotationClass) {
        Set<Class<?>> classSet = new HashSet<>();
        for (Class<?> cls: CLASS_SET) {
            if (cls.isAnnotationPresent(annotationClass)) {
                classSet.add(cls);
            }
        }
        return classSet;
    }

    /**
     * 获取应用包名下某父类（或接口）的所有子类（或实现类）
     */
    public static Set<Class<?>> getClassSetBySuper(Class<?> superClass) {
        Set<Class<?>> classSet = new HashSet<>();
        for (Class<?> cls: CLASS_SET) {
            if (superClass.isAssignableFrom(cls) && !superClass.equals(cls)) {
                classSet.add(cls);
            }
        }
        return classSet;
    }

    /**
     * 获取应用包名下的所有Service类
     */
    public static Set<Class<?>> getServiceClassSet() {
        return getClassSetByAnnotation(Service.class);
    }

    /**
     * 获取应用包名下的所有Controller类
     */
    public static Set<Class<?>> getControllerClassSet() {
        return getClassSetByAnnotation(Controller.class);
    }

    /**
     * 获取应用包名下的所有Configuration类
     */
    public static Set<Class<?>> getConfigurationClassSet() {
        return getClassSetByAnnotation(Configuration.class);
    }

    /**
     * 获取应用包名下的所有Bean类
     */
    public static Set<Class<?>> getBeanClassSet() {
        Set<Class<?>> classSet = new HashSet<>();
        classSet.addAll(getServiceClassSet());
        classSet.addAll(getControllerClassSet());
        classSet.addAll(getConfigurationClassSet());
        return classSet;
    }
}
