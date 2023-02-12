package com.jfeng.framework.security.fault;

/**
 * 认证异常（没登陆）
 *
 * @author jfeng
 * @since 1.0.0
 */
public class AuthcException extends RuntimeException {

    public AuthcException() {
        super();
    }

    public AuthcException(String message) {
        super(message);
    }

    public AuthcException(String message, Throwable cause) {
        super(message, cause);
    }

    public AuthcException(Throwable cause) {
        super(cause);
    }
}
