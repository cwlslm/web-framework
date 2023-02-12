package com.jfeng.framework.security.config;

import com.jfeng.framework.mvc.DataContext;
import com.jfeng.framework.mvc.annotation.ActionException;
import com.jfeng.framework.mvc.annotation.Configuration;
import com.jfeng.framework.security.fault.AuthcException;
import com.jfeng.framework.security.fault.AuthzException;
import com.jfeng.framework.util.WebUtil;

import javax.servlet.http.HttpServletResponse;

/**
 * 配置Action异常处理
 */
@Configuration
public class ActionExceptionConfig {

    // 认证异常（没登陆）
    @ActionException(AuthcException.class)
    public void authcExceptionHandler(AuthcException e) {
        HttpServletResponse response = DataContext.getResponse();
        WebUtil.sendError(HttpServletResponse.SC_FORBIDDEN, "请登录", response);
    }

    // 授权异常（权限不足）
    @ActionException(AuthzException.class)
    public void authzExceptionHandler(AuthzException e) {
        HttpServletResponse response = DataContext.getResponse();
        WebUtil.sendError(HttpServletResponse.SC_UNAUTHORIZED, "无权限", response);
    }
}
