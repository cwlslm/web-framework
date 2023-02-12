package com.jfeng.framework.mvc.bean;

import com.jfeng.framework.core.bean.BaseBean;

import java.io.InputStream;

/**
 * 封装请求参数中的上传文件参数
 *
 * @author jfeng
 * @since 1.0.0
 */
public class RequestFileParam extends BaseBean {

    private String fieldName;
    private String fileName;
    private long fileSize;
    private String contentType;
    private InputStream inputStream;

    public RequestFileParam(String fieldName, String fileName, long fileSize,
                            String contentType, InputStream inputStream) {
        this.fieldName = fieldName;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.contentType = contentType;
        this.inputStream = inputStream;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getFileName() {
        return fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public String getContentType() {
        return contentType;
    }

    public InputStream getInputStream() {
        return inputStream;
    }
}
