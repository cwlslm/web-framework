package com.jfeng.framework.orm.fault;

/**
 * 生成 SQL 语句错误
 *
 * @author jfeng
 * @since 1.0.0
 */
public class SqlGenerateError extends Error {

    public SqlGenerateError() {
        super();
    }

    public SqlGenerateError(String message) {
        super(message);
    }

    public SqlGenerateError(String message, Throwable cause) {
        super(message, cause);
    }

    public SqlGenerateError(Throwable cause) {
        super(cause);
    }
}
