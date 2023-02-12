package com.jfeng.framework.security;

import com.jfeng.framework.core.ConfigHelper;
import com.jfeng.framework.mvc.DataContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Security管理器
 */
public class SecurityManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityManager.class);

    public static boolean isLogin() {
        Object userId = DataContext.getSession().getAttribute("securityUserId");
        return userId != null;
    }

    public static int getUserId() {
        Object userId = DataContext.getSession().getAttribute("securityUserId");
        if (userId == null) {
            throw new RuntimeException("没登陆");
        }
        return (int) userId;
    }

    public static void login(int userId) {
        if (isLogin()) {
            throw new RuntimeException("已登陆");
        }

        DataContext.getSession().setAttribute("securityUserId", userId);

        int expires = ConfigHelper.getSecurityExpires();
        if (expires != 0) {
            DataContext.getSession().setMaxInactiveInterval(expires);
        }
    }

    public static int logout() {
        if (!isLogin()) {
            throw new RuntimeException("没登陆");
        }
        int userId = getUserId();
        DataContext.getSession().invalidate();
        return userId;
    }
}
