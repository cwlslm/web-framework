package com.jfeng.framework.security;

import com.jfeng.framework.aop.annotation.Aspect;
import com.jfeng.framework.aop.core.AbstractAspect;
import com.jfeng.framework.core.annotation.Order;
import com.jfeng.framework.mvc.DataContext;
import com.jfeng.framework.mvc.annotation.Controller;
import com.jfeng.framework.security.bean.Rule;
import com.jfeng.framework.security.fault.AuthcException;
import com.jfeng.framework.security.fault.AuthzException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Security切面
 * 作用在所有Controller上
 */
@Aspect(Controller.class)
@Order(90)
public class SecurityAspect extends AbstractAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityAspect.class);

    @Override
    public void before(Class<?> cls, Method method, Object[] params) throws Throwable {
        HttpServletRequest request = DataContext.getRequest();
        String requestPath = request.getPathInfo();

        // 规则列表
        List<Rule> ruleList = SecurityHelper.getRules();

        Rule rule = null;
        for (Rule r: ruleList) {
            if (r.getPattern().matcher(requestPath).matches()) {
                rule = r;
                break;
            }
        }

        if (rule != null) {
            switch (rule.getAuthzType()) {
                case ANON:
                    break;

                case AUTHC:
                    if (!SecurityManager.isLogin()) {
                        throw new AuthcException("请登录");
                    }
                    break;

                case PERMS:
                    if (!SecurityManager.isLogin()) {
                        throw new AuthcException("请登录");
                    }
                    if (!SecurityHelper.authzVerify()) {
                        throw new AuthzException("无权限");
                    }
                    break;
            }
        }
    }
}
