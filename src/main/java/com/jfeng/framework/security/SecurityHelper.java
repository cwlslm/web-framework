package com.jfeng.framework.security;

import com.jfeng.framework.core.ClassHelper;
import com.jfeng.framework.core.ConfigHelper;
import com.jfeng.framework.ioc.BeanHelper;
import com.jfeng.framework.mvc.DataContext;
import com.jfeng.framework.mvc.annotation.Configuration;
import com.jfeng.framework.mvc.bean.Handler;
import com.jfeng.framework.security.annotation.Permission;
import com.jfeng.framework.security.annotation.SecurityAuthzValidator;
import com.jfeng.framework.security.annotation.SecurityRules;
import com.jfeng.framework.security.bean.AuthzValidator;
import com.jfeng.framework.security.bean.Rule;
import com.jfeng.framework.util.ReflectionUtil;
import com.jfeng.framework.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Security助手类
 */
public class SecurityHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityHelper.class);

    // 规则列表
    private static final List<Rule> RULE_LIST = new ArrayList<>();

    // 权限验证器列表
    private static final List<AuthzValidator> AUTHZ_VALIDATOR_LIST = new ArrayList<>();

    static {
        // 找到加了@Configuration注解的配置类
        Set<Class<?>> configurationClassSet = ClassHelper.getClassSetByAnnotation(Configuration.class);
        for (Class<?> configCls: configurationClassSet) {
            for (Method method : configCls.getDeclaredMethods()) {
                // 找到加了@SecurityRules注解的函数
                if (method.isAnnotationPresent(SecurityRules.class)) {
                    try {
                        // 验证返回值类型是否为List<Map<String, AuthzType>>
                        Type returnType = method.getGenericReturnType();
                        Class<?> thisClass = SecurityHelper.class;
                        Method validator = thisClass.getDeclaredMethod("securityRulesReturnTypeValidator");
                        Type validatorReturnType = validator.getGenericReturnType();
                        if (!returnType.equals(validatorReturnType)) {
                            LOGGER.warn(String.format("SecurityHelper 初始化异常，%s-%s返回值类型错误",
                                    configCls.getName(), method.getName()));
                            continue;
                        }

                        Object configurationIns = BeanHelper.getBean(configCls);
                        @SuppressWarnings("unchecked")
                        List<Rule> ruleList = (List<Rule>) ReflectionUtil.invokeMethod(configurationIns, method);
                        for (Rule rule: ruleList) {
                            try {
                                rule.compile();
                            }
                            catch (Exception e) {
                                LOGGER.warn(String.format("SecurityHelper 初始化异常，%s-%s rule compile失败:%s",
                                        configCls.getName(), method.getName(), rule), e);
                                continue;
                            }
                            RULE_LIST.add(rule);
                        }
                    }
                    catch (Exception e) {
                        LOGGER.error("SecurityHelper异常 初始化异常", e);
                    }
                }

                // 找到加了@SecurityAuthzValidator注解的函数
                if (method.isAnnotationPresent(SecurityAuthzValidator.class)) {
                    // 获取函数参数信息
                    Parameter[] parameters = method.getParameters();
                    // 参数只能有1个，且类型要为String
                    if (parameters.length != 1 || !String.class.isAssignableFrom(parameters[0].getType())) {
                        LOGGER.warn(String.format("SecurityHelper 初始化异常，%s-%s函数参数类型或数量错误",
                                configCls.getName(), method.getName()));
                        continue;
                    }

                    AUTHZ_VALIDATOR_LIST.add(new AuthzValidator(configCls, method));
                }
            }
        }
    }

    private static List<Rule> securityRulesReturnTypeValidator() {
        return new ArrayList<>();
    }

    public static boolean isEnable() {
        return ConfigHelper.getSecurityEnable();
    }

    public static List<Rule> getRules() {
        return RULE_LIST;
    }

    public static List<AuthzValidator> getAuthzValidatorList() {
        return AUTHZ_VALIDATOR_LIST;
    }

    public static boolean authzVerify() {
        Handler handler = DataContext.getHandler();
        Method method = handler.getActionMethod();
        Permission permission = method.getAnnotation(Permission.class);
        if (permission == null || StringUtil.isEmpty(permission.value())) {
            return true;
        }

        for (AuthzValidator authzValidator: AUTHZ_VALIDATOR_LIST) {
            Object configurationIns = BeanHelper.getBean(authzValidator.getCls());
            Object verifyRes = ReflectionUtil.invokeMethod(configurationIns, authzValidator.getMethod(), permission.value());
            try {
                if ((boolean) verifyRes) {
                    return true;
                }
            }
            catch (Exception e) {
                LOGGER.warn(String.format("SecurityHelper authzVerify异常，%s-%s返回值类型错误",
                        authzValidator.getCls().getName(), authzValidator.getMethod().getName()));
            }
        }

        return false;
    }
}
