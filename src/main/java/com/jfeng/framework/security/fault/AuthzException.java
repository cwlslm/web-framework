package com.jfeng.framework.security.fault;

/**
 * 授权异常（权限不足）
 *
 * @author jfeng
 * @since 1.0.0
 */
public class AuthzException extends RuntimeException {

    public AuthzException() {
        super();
    }

    public AuthzException(String message) {
        super(message);
    }

    public AuthzException(String message, Throwable cause) {
        super(message, cause);
    }

    public AuthzException(Throwable cause) {
        super(cause);
    }
}
