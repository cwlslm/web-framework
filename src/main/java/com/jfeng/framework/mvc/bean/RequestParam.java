package com.jfeng.framework.mvc.bean;

import com.jfeng.framework.core.bean.BaseBean;
import com.jfeng.framework.util.CastUtil;
import com.jfeng.framework.util.CollectionUtil;
import com.jfeng.framework.util.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 封装请求参数对象
 *
 * @author jfeng
 * @since 1.0.0
 */
public class RequestParam extends BaseBean {

    private List<RequestQueryOrFormParam> queryParamList;
    private List<RequestQueryOrFormParam> formParamList;
    private Map<String, Object> queryFieldMap;
    private Map<String, Object> formFieldMap;
    private Map<String, Object> fieldMap;
    private List<RequestFileParam> fileParamList;

    public RequestParam(List<RequestQueryOrFormParam> queryParamList, List<RequestQueryOrFormParam> formParamList) {
        this.queryParamList = queryParamList;
        this.formParamList = formParamList;
        this.queryFieldMap = _getQueryFieldMap();
        this.formFieldMap = _getFormFieldMap();
        this.fieldMap = _getFieldMap();
    }

    public RequestParam(List<RequestQueryOrFormParam> queryParamList, List<RequestQueryOrFormParam> formParamList,
                        List<RequestFileParam> fileParamList) {
        this.queryParamList = queryParamList;
        this.formParamList = formParamList;
        this.queryFieldMap = _getQueryFieldMap();
        this.formFieldMap = _getFormFieldMap();
        this.fieldMap = _getFieldMap();
        this.fileParamList = fileParamList;
    }

    /**
     * 获取请求参数映射  query 参数部分
     */
    private Map<String, Object> _getQueryFieldMap() {
        Map<String, Object> fieldMap = new HashMap<>();
        if (CollectionUtil.isNotEmpty(queryParamList)) {
            for (RequestQueryOrFormParam queryParam : queryParamList) {
                String fieldName = queryParam.getFieldName();
                Object fieldValue = queryParam.getFieldValue();
                if (fieldMap.containsKey(fieldName)) {
                    fieldValue = fieldMap.get(fieldName) + StringUtil.SEPARATOR + fieldValue;
                }
                fieldMap.put(fieldName, fieldValue);
            }
        }
        return fieldMap;
    }

    /**
     * 获取请求参数映射  form 参数部分
     */
    private Map<String, Object> _getFormFieldMap() {
        Map<String, Object> fieldMap = new HashMap<>();
        if (CollectionUtil.isNotEmpty(formParamList)) {
            for (RequestQueryOrFormParam queryParam : formParamList) {
                String fieldName = queryParam.getFieldName();
                Object fieldValue = queryParam.getFieldValue();
                if (fieldMap.containsKey(fieldName)) {
                    fieldValue = fieldMap.get(fieldName) + StringUtil.SEPARATOR + fieldValue;
                }
                fieldMap.put(fieldName, fieldValue);
            }
        }
        return fieldMap;
    }

    /**
     * 获取所有请求参数映射
     */
    private Map<String, Object> _getFieldMap() {
        Map<String, Object> fieldMap = new HashMap<>();
        fieldMap.putAll(queryFieldMap);
        fieldMap.putAll(formFieldMap);
        return fieldMap;
    }

    public Map<String, Object> getQueryFieldMap() {
        return queryFieldMap;
    }

    public Map<String, Object> getFormFieldMap() {
        return formFieldMap;
    }

    public Map<String, Object> getFieldMap() {
        return fieldMap;
    }

    /**
     * 根据 fieldName 判断对应数据是否存在
     */
    public boolean exists(String fieldName) {
        return fieldMap.containsKey(fieldName);
    }

    /**
     * 根据 fieldName 获取 fieldValue
     */
    public Object getFieldValue(String fieldName) {
        return fieldMap.get(fieldName);
    }

    /**
     * 获取上传文件映射
     */
    public Map<String, List<RequestFileParam>> getFileMap() {
        Map<String, List<RequestFileParam>> fieldMap = new HashMap<>();
        if (CollectionUtil.isNotEmpty(fileParamList)) {
            for (RequestFileParam requestFileParam : fileParamList) {
                String fieldName = requestFileParam.getFieldName();
                if (!fieldMap.containsKey(fieldName)) {
                    fieldMap.put(fieldName, new ArrayList<>());
                }
                fieldMap.get(fieldName).add(requestFileParam);
            }
        }
        return fieldMap;
    }

    /**
     * 根据 fieldName 获取所有上传文件
     */
    public List<RequestFileParam> getFileList(String fieldName) {
        return getFileMap().get(fieldName);
    }

    /**
     * 根据 fieldName 获取唯一上传文件
     */
    public RequestFileParam getFile(String fieldName) {
        List<RequestFileParam> fileParamList = getFileList(fieldName);
        if (CollectionUtil.isNotEmpty(fileParamList) && fileParamList.size() == 1) {
            return fileParamList.get(0);
        }
        else {
            return null;
        }
    }

    /**
     * 验证参数是否为空
     */
    public boolean isEmpty() {
        return CollectionUtil.isEmpty(formParamList) && CollectionUtil.isEmpty(fileParamList);
    }

    /**
     * 根据参数名获取 String 型参数值
     */
    public String getString(String name) {
        return CastUtil.castString(_getFieldMap().get(name));
    }

    /**
     * 根据参数名获取 double 型参数值
     */
    public double getDouble(String name) {
        return CastUtil.castDouble(_getFieldMap().get(name));
    }

    /**
     * 根据参数名获取 long 型参数值
     */
    public long getLong(String name) {
        return CastUtil.castLong(_getFieldMap().get(name));
    }

    /**
     * 根据参数名获取 int 型参数值
     */
    public int getInt(String name) {
        return CastUtil.castInt(_getFieldMap().get(name));
    }

    /**
     * 根据参数名获取 boolean 型参数值
     */
    public boolean getBoolean(String name) {
        return CastUtil.castBoolean(_getFieldMap().get(name));
    }
}
