package com.jfeng.framework.mvc;

import com.jfeng.framework.mvc.bean.RequestQueryOrFormParam;
import com.jfeng.framework.mvc.bean.RequestParam;
import com.jfeng.framework.util.ArrayUtil;
import com.jfeng.framework.util.CodecUtil;
import com.jfeng.framework.util.StreamUtil;
import com.jfeng.framework.util.StringUtil;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * 请求参数助手类
 *
 * @author jfeng
 * @since 1.0.0
 */
public class RequestParamHelper {

    /**
     * 创建请求对象
     */
    public static RequestParam createParam(HttpServletRequest request) throws IOException {
        return new RequestParam(parseParameterNames(request), parseInputStream(request));
    }

    /**
     * 获取 URL 中的 query 参数
     */
    public static List<RequestQueryOrFormParam> parseParameterNames(HttpServletRequest request) throws UnsupportedEncodingException {
        List<RequestQueryOrFormParam> formParamList = new ArrayList<>();

        request.setCharacterEncoding("UTF-8");
        Enumeration<String> paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String fieldName = paramNames.nextElement();
            String[] fieldValues = request.getParameterValues(fieldName);
            if (ArrayUtil.isNotEmpty(fieldValues)) {
                Object fieldValue;
                if (fieldValues.length == 1) {
                    fieldValue = fieldValues[0];
                } else {
                    StringBuilder sb = new StringBuilder("");
                    for (int i = 0; i < fieldValues.length; i++) {
                        sb.append(fieldValues[i]);
                        if (i != fieldValues.length - 1) {
                            sb.append(StringUtil.SEPARATOR);
                        }
                    }
                    fieldValue = sb.toString();
                }
                formParamList.add(new RequestQueryOrFormParam(fieldName, fieldValue));
            }
        }
        return formParamList;
    }

    /**
     * 获取 Form 表单参数
     */
    public static List<RequestQueryOrFormParam> parseInputStream(HttpServletRequest request) throws IOException {
        List<RequestQueryOrFormParam> formParamList = new ArrayList<>();
        String body = CodecUtil.decodeURL(StreamUtil.getString(request.getInputStream()));
        if (StringUtil.isNotEmpty(body)) {
            String[] kvs = StringUtil.splitString(body, "&");
            if (ArrayUtil.isNotEmpty(kvs)) {
                for (String kv: kvs) {
                    String[] array = StringUtil.splitString(kv, "=");
                    if (ArrayUtil.isNotEmpty(array) && array.length == 2) {
                        String fieldName = array[0];
                        String fieldValue = array[1];
                        formParamList.add(new RequestQueryOrFormParam(fieldName, fieldValue));
                    }
                }
            }
        }
        return formParamList;
    }
}
