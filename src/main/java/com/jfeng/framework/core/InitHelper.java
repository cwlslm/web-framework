package com.jfeng.framework.core;

import com.jfeng.framework.aop.AopHelper;
import com.jfeng.framework.core.ClassHelper;
import com.jfeng.framework.ioc.BeanHelper;
import com.jfeng.framework.ioc.IocHelper;
import com.jfeng.framework.mvc.ControllerHelper;
import com.jfeng.framework.orm.EntityHelper;
import com.jfeng.framework.security.SecurityHelper;
import com.jfeng.framework.transaction.TransactionHelper;
import com.jfeng.framework.util.ClassUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 系统初始化助手类
 *
 * @author jfeng
 * @since 1.0.0
 */
public class InitHelper {

    public static void init() {
        // 先加载ClassHelper（类管理，扫描应用包内的所有类）
        ClassUtil.loadClass(ClassHelper.class.getName(), true);

        // 手动添加框架内的部分扩展功能
        // 事务
        if (TransactionHelper.isEnable()) {
            ClassHelper.addClass(ClassUtil.getClassSet("com.jfeng.framework.transaction"));
        }
        // Security安全模块
        if (SecurityHelper.isEnable()) {
            ClassHelper.addClass(ClassUtil.getClassSet("com.jfeng.framework.security"));
        }

        Class<?>[] classList = {
                EntityHelper.class,
                BeanHelper.class,
                AopHelper.class,
                IocHelper.class,
                ControllerHelper.class
        };
        for (Class<?> cls: classList) {
            ClassUtil.loadClass(cls.getName(), true);
        }
    }
}
