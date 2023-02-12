package com.jfeng.framework.mvc.bean;

import com.jfeng.framework.core.bean.BaseBean;

/**
 * 封装 Query 参数或 Form 参数
 *
 * @author jfeng
 * @since 1.0.0
 */
public class RequestQueryOrFormParam extends BaseBean {

    private String fieldName;
    private Object fieldValue;

    public RequestQueryOrFormParam(String fieldName, Object fieldValue) {
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    public String getFieldName() {
        return fieldName;
    }

    public Object getFieldValue() {
        return fieldValue;
    }
}
