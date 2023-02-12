package com.jfeng.framework.core;

import com.jfeng.framework.mvc.FileUploadHelper;
import com.jfeng.framework.util.StringUtil;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRegistration;
import javax.servlet.annotation.WebListener;

/**
 * Servlet 容器监听器
 *
 * @author jfeng
 * @since 1.0.0
 */
@WebListener
public class ServletContainerListener implements ServletContextListener {

    /**
     * 当容器初始化时调用
     */
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // 获取 ServletContext
        ServletContext servletContext = sce.getServletContext();
        // 初始化相关 Helper 类
        InitHelper.init();
        // 初始化文件上传 Helper 类
        FileUploadHelper.init(servletContext);
        // 添加 Servlet 映射
        addServletMapping(servletContext);
    }

    /**
     * 当容器销毁时调用
     */
    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }

    private void addServletMapping(ServletContext context) {
        // 用 DefaultServlet 映射所有静态资源
        registerDefaultServlet(context);
        // 用 JspServlet 映射所有 JSP 请求
        registerJspServlet(context);
    }

    private void registerDefaultServlet(ServletContext context) {
        ServletRegistration defaultServlet = context.getServletRegistration("default");
        defaultServlet.addMapping("/index.html");
        defaultServlet.addMapping("/favicon.ico");
        String wwwPath = ConfigHelper.getAppAssetPath();
        if (StringUtil.isNotEmpty(wwwPath)) {
            defaultServlet.addMapping(wwwPath + "*");
        }
    }

    private void registerJspServlet(ServletContext context) {
        ServletRegistration jspServlet = context.getServletRegistration("jsp");
        jspServlet.addMapping("/index.jsp");
        String jspPath = ConfigHelper.getAppJspPath();
        if (StringUtil.isNotEmpty(jspPath)) {
            jspServlet.addMapping(jspPath + "*");
        }
    }
}
