package com.jfeng.framework.aop;

import com.jfeng.framework.aop.annotation.Aspect;
import com.jfeng.framework.core.ClassHelper;
import com.jfeng.framework.core.annotation.Order;
import com.jfeng.framework.ioc.BeanHelper;
import com.jfeng.framework.aop.core.AbstractAspect;
import com.jfeng.framework.aop.core.ProxyManager;
import com.jfeng.framework.util.ObjectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.*;

/**
 * AOP 助手类
 */
public class AopHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(AopHelper.class);

    static {
        try {
            Map<Class<?>, List<AbstractAspect>> targetToAspectMap = getTargetToAspectMap();
            for (Map.Entry<Class<?>, List<AbstractAspect>> targetToAspect: targetToAspectMap.entrySet()) {
                Class<?> targetClass = targetToAspect.getKey();
                List<AbstractAspect> aspectList = targetToAspect.getValue();
                Object proxy = ProxyManager.createProxy(targetClass, aspectList);
                BeanHelper.setBean(targetClass, proxy);
            }
        } catch (Exception e) {
            LOGGER.error("AopHelper 初始化异常", e);
        }
    }

    /**
     * 获取 '切面类' 到 '目标类集合' 的映射
     * 切面类与目标类通过 '切面类自定义注解 @MyAspect' 建立了联系
     * 切面类通过 @Aspect(MyAspect.class) 建立联系
     * 目标类则通过 @MyAspect 建立联系
     */
    private static Map<Class<?>, Set<Class<?>>> getAspectToTargetMap() {
        // '切面类' 到 '目标类集合' 的映射
        Map<Class<?>, Set<Class<?>>> aspectToTargetMap = new HashMap<>();

        // 获取所有继承了切面抽象类 AbstractAspect 的类
        Set<Class<?>> aspectClassSet = ClassHelper.getClassSetBySuper(AbstractAspect.class);
        for (Class<?> aspectClass: aspectClassSet) {
            // 如果继承了切面抽象类 AbstractAspect 且带有 @Aspect 注解，则为切面类
            if (aspectClass.isAnnotationPresent(Aspect.class)) {
                // 获取切面类自定义注解
                Aspect aspect = aspectClass.getAnnotation(Aspect.class);
                Class<? extends Annotation> annotation = aspect.value();
                // 切面类自定义注解不能是 @Aspect
                if (!annotation.equals(Aspect.class)) {
                    // 获取所有带有切面类自定义注解的类，即为目标类集合
                    Set<Class<?>> targetClassSet = ClassHelper.getClassSetByAnnotation(annotation);
                    aspectToTargetMap.put(aspectClass, targetClassSet);
                }
            }
        }
        return aspectToTargetMap;
    }

    /**
     * 获取 '目标类' 到 '切面类实例列表' 的映射
     */
    private static Map<Class<?>, List<AbstractAspect>> getTargetToAspectMap() throws Exception {
        // '目标类' 到 '切面类实例列表' 的映射
        Map<Class<?>, List<AbstractAspect>> targetToAspectMap = new HashMap<>();

        // 获取 '切面类' 到 '目标类集合' 的映射
        Map<Class<?>, Set<Class<?>>> aspectToTargetMap = getAspectToTargetMap();

        // '切面类' 到 '优先级（由@Order定义）' 的映射
        Map<Class<?>, Integer> aspectToOrderMap = new HashMap<>();
        for (Map.Entry<Class<?>, Set<Class<?>>> aspectToTarget: aspectToTargetMap.entrySet()) {
            // 切面类
            Class<?> aspectClass = aspectToTarget.getKey();
            if (aspectClass.isAnnotationPresent(Order.class)) {
                aspectToOrderMap.put(aspectClass, aspectClass.getAnnotation(Order.class).value());
            }
        }

        for (Map.Entry<Class<?>, Set<Class<?>>> aspectToTarget: aspectToTargetMap.entrySet()) {
            // 切面类
            Class<?> aspectClass = aspectToTarget.getKey();
            // 目标类集合
            Set<Class<?>> targetClassSet = aspectToTarget.getValue();
            for (Class<?> targetClass: targetClassSet) {
                // 实例化切面类
                AbstractAspect aspectIns = ObjectUtil.newInstance(aspectClass);
                Integer order = aspectToOrderMap.getOrDefault(aspectIns.getClass(), null);

                if (targetToAspectMap.containsKey(targetClass)) {
                    List<AbstractAspect> aspectInsList = targetToAspectMap.get(targetClass);

                    if (order == null) {
                        aspectInsList.add(aspectIns);
                        continue;
                    }

                    int findIndex = 0;
                    for (; findIndex < aspectInsList.size(); findIndex++) {
                        AbstractAspect abstractAspect = aspectInsList.get(findIndex);
                        Integer orderLast = aspectToOrderMap.getOrDefault(abstractAspect.getClass(), null);
                        if (orderLast == null || orderLast > order) {
                            break;
                        }
                    }
                    aspectInsList.add(findIndex, aspectIns);
                }
                else {
                    List<AbstractAspect> aspectInsList = new ArrayList<>();
                    aspectInsList.add(aspectIns);
                    targetToAspectMap.put(targetClass, aspectInsList);
                }
            }
        }

        return targetToAspectMap;
    }
}
