package com.jfeng.framework.mvc.bean;

import com.jfeng.framework.core.bean.BaseBean;

import java.util.HashMap;
import java.util.Map;

/**
 * 封装返回的视图对象（JSP）
 *
 * @author jfeng
 * @since 1.0.0
 */
public class ResultView extends BaseBean {

    /**
     * 视图路径
     */
    private String path;

    /**
     * 视图数据
     */
    private Map<String, Object> data;

    public ResultView(String path) {
        this.path = path;
        data = new HashMap<>();
    }

    public ResultView addData(String key, Object value) {
        data.put(key, value);
        return this;
    }

    public boolean isRedirect() {
        return path.startsWith("/");
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }
}
