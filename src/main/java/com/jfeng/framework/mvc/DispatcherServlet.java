package com.jfeng.framework.mvc;

import com.jfeng.framework.dao.DatabaseHelper;
import com.jfeng.framework.mvc.bean.Handler;
import com.jfeng.framework.mvc.bean.ResultView;
import com.jfeng.framework.ioc.BeanHelper;
import com.jfeng.framework.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.List;

/**
 * 请求转发器
 *
 * @author jfeng
 * @since 1.0.0
 */
@WebServlet(urlPatterns = "/*", loadOnStartup = 0)
public class DispatcherServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(DispatcherServlet.class);

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {}

    @Override
    public void service(HttpServletRequest request, HttpServletResponse response) {
        String requestMethod = request.getMethod().toLowerCase();
        String requestPath = request.getPathInfo();

        // 去掉当前请求路径末尾的 "/"
        if (requestPath.endsWith("/") && requestPath.length() > 1) {
            requestPath = requestPath.substring(0, requestPath.length() - 1);
        }

        Handler handler = ControllerHelper.getHandler(requestMethod, requestPath);
        // 若未找到 Action，则跳转到 404 页面
        if (handler == null) {
            WebUtil.sendError(HttpServletResponse.SC_NOT_FOUND, "", response);
            return;
        }

        // 初始化 DataContext
        DataContext.init(request, response, handler);

        try {
            Class<?> controllerClass = handler.getControllerClass();
            Object controllerBean = BeanHelper.getBean(controllerClass);
            Method actionMethod = handler.getActionMethod();

            // 构建 Action 方法参数列表
            List<Object> paramList = ControllerHelper.createActionMethodParamList(handler);

            // 调用 Action 方法
            Object result = ReflectionUtil.invokeMethod(controllerBean, actionMethod, paramList.toArray());

            // 处理 Action 方法返回值
            if (result instanceof ResultView) {
                ControllerHelper.handleViewResult((ResultView) result, request, response);
            } else {
                ControllerHelper.handleDataResult(result, request, response);
            }
        }
        catch (Throwable e) {
            LOGGER.error("service Action函数运行异常", e);
            ControllerHelper.resolveHandlerException(request, response, e);
        }
        finally {
            // 释放数据库连接池的所有连接
            try {
                DatabaseHelper.closeAllConnection();
            }
            catch (Exception e) {
                LOGGER.error("service DatabaseHelper.closeAllConnection()异常", e);
            }

            // 销毁 DataContext
            DataContext.destroy();
        }
    }
}
