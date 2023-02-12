package com.jfeng.framework.mvc.bean;

import com.jfeng.framework.core.bean.BaseBean;

/**
 * 封装 Action 信息
 *
 * @author jfeng
 * @since 1.0.0
 */
public class Request extends BaseBean {
    /**
     * 请求方法
     */
    private String requestType;

    /**
     * 请求路径
     */
    private String requestPath;

    public Request(String requestType, String requestPath) {
        this.requestType = requestType;
        this.requestPath = requestPath;
    }

    public String getRequestType() {
        return requestType;
    }

    public String getRequestPath() {
        return requestPath;
    }
}
