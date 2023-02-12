package com.jfeng.framework.transaction;

import com.jfeng.framework.aop.annotation.Aspect;
import com.jfeng.framework.aop.core.AbstractAspect;
import com.jfeng.framework.core.annotation.Order;
import com.jfeng.framework.mvc.annotation.Service;
import com.jfeng.framework.transaction.annotation.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * 事务切面类
 *
 * @author jfeng
 * @since 1.0.0
 */
@Aspect(Service.class)
@Order(100)
public class TransactionAspect extends AbstractAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionAspect.class);

    @Override
    public boolean intercept(Class<?> cls, Method method, Object[] params) throws Throwable {
        return method.isAnnotationPresent(Transaction.class);
    }

    @Override
    public void before(Class<?> cls, Method method, Object[] params) throws Throwable {
        TransactionManager.begin();
    }

    @Override
    public void after(Class<?> cls, Method method, Object[] params) throws Throwable {
        TransactionManager.commit();
    }

    @Override
    public void error(Class<?> cls, Method method, Object[] params) throws Throwable {
        TransactionManager.rollback();
    }
}
