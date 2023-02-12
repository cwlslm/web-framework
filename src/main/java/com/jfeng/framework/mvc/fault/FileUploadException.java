package com.jfeng.framework.mvc.fault;

/**
 * 文件上传异常（当文件上传失败时抛出）
 *
 * @author jfeng
 * @since 1.0.0
 */
public class FileUploadException extends RuntimeException {

    public FileUploadException() {
        super();
    }

    public FileUploadException(String message) {
        super(message);
    }

    public FileUploadException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileUploadException(Throwable cause) {
        super(cause);
    }
}
