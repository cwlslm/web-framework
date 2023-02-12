package com.jfeng.framework.core;

import com.jfeng.framework.util.PropsUtil;

import java.util.List;
import java.util.Properties;

/**
 * 属性文件助手类
 *
 * @author jfeng
 * @since 1.0.0
 */
public class ConfigHelper {

    public static final Properties CONFIG_PROPS = PropsUtil.loadProps(ConfigConstant.CONFIG_FILE);

    /**
     * 获取 JDBC 驱动
     */
    public static String getJdbcDriver() {
        return PropsUtil.getString(CONFIG_PROPS, ConfigConstant.JDBC_DRIVER);
    }

    /**
     * 获取 JDBC URL
     */
    public static String getJdbcUrl() {
        return PropsUtil.getString(CONFIG_PROPS, ConfigConstant.JDBC_URL);
    }

    /**
     * 获取 JDBC 用户名
     */
    public static String getJdbcUsername() {
        return PropsUtil.getString(CONFIG_PROPS, ConfigConstant.JDBC_USERNAME);
    }

    /**
     * 获取 JDBC 密码
     */
    public static String getJdbcPassword() {
        return PropsUtil.getString(CONFIG_PROPS, ConfigConstant.JDBC_PASSWORD);
    }

    /**
     * 获取数据库类型
     */
    public static String getJdbcType() {
        return PropsUtil.getString(CONFIG_PROPS, ConfigConstant.JDBC_TYPE);
    }

    /**
     * 获取数据库连接池初始容量
     */
    public static int getJdbcInitSize() {
        return PropsUtil.getInt(CONFIG_PROPS, ConfigConstant.JDBC_INIT_SIZE);
    }

    /**
     * 获取数据库连接池最大容量
     */
    public static int getJdbcMaxTotal() {
        return PropsUtil.getInt(CONFIG_PROPS, ConfigConstant.JDBC_MAX_TOTAL);
    }

    /**
     * 获取数据库连接池最小空闲连接数
     */
    public static int getJdbcMinIdle() {
        return PropsUtil.getInt(CONFIG_PROPS, ConfigConstant.JDBC_MIN_IDLE);
    }

    /**
     * 获取数据库连接池最大空闲连接数
     */
    public static int getJdbcMaxIdle() {
        return PropsUtil.getInt(CONFIG_PROPS, ConfigConstant.JDBC_MAX_IDLE);
    }

    /**
     * 获取数据库连接池最大等待时间（毫秒）
     */
    public static int getJdbcMaxWait() {
        return PropsUtil.getInt(CONFIG_PROPS, ConfigConstant.JDBC_MAX_WAIT);
    }

    /**
     * 验证连接有效性的SQL语句
     */
    public static String getJdbcValidationQuery() {
        return PropsUtil.getString(CONFIG_PROPS, ConfigConstant.JDBC_VALIDATION_QUERY, null);
    }

    /**
     * 获取一个连接时，是否需要验证连接的有效性
     * 如果需要验证，就使用validationQuery设置的语句验证连接的有效性
     */
    public static Boolean getJdbcTestOnBorrow() {
        return PropsUtil.getBoolean(CONFIG_PROPS, ConfigConstant.JDBC_TEST_ON_BORROW, false);
    }

    /**
     * 连接重新归还到池子中时，是否需要校验连接的有效性
     * 如果需要验证，就使用validationQuery设置的语句验证连接的有效性
     */
    public static Boolean getJdbcTestOnReturn() {
        return PropsUtil.getBoolean(CONFIG_PROPS, ConfigConstant.JDBC_TEST_ON_RETURN, false);
    }

    /**
     * 驱逐线程是否要验证连接的有效性，如果有效性无法验证通过，也会进行驱逐
     * 如果需要验证，就使用validationQuery设置的语句验证连接的有效性
     */
    public static Boolean getJdbcTestWhileIdle() {
        return PropsUtil.getBoolean(CONFIG_PROPS, ConfigConstant.JDBC_TEST_WHILE_IDLE, false);
    }

    /**
     * 连接最大持续空闲时间，超过后会尝试驱逐
     */
    public static int getJdbcMinEvictableIdleTimeMillis() {
        return PropsUtil.getInt(CONFIG_PROPS, ConfigConstant.JDBC_MIN_EVICTABLE_IDLE_TIME_MILLIS);
    }

    /**
     * 驱逐线程每隔多久运行一次
     */
    public static int getJdbcTimeBetweenEvictionRunsMillis() {
        return PropsUtil.getInt(CONFIG_PROPS, ConfigConstant.JDBC_TIME_BETWEEN_EVICTION_RUNS_MILLIS);
    }

    /**
     * 当一个连接创建后，执行的一些sql语句的集合
     */
    public static List<String> getJdbcConnectInitSqls() {
        return PropsUtil.getStringList(CONFIG_PROPS, ConfigConstant.JDBC_CONNECTION_INIT_SQLS, null);
    }

    /**
     * 获取应用基础包名
     */
    public static String getAppBasePackage() {
        return PropsUtil.getString(CONFIG_PROPS, ConfigConstant.APP_BASE_PACKAGE);
    }

    /**
     * 获取应用 JSP 路径
     */
    public static String getAppJspPath() {
        return PropsUtil.getString(CONFIG_PROPS, ConfigConstant.APP_JSP_PATH, "/WEB-INF/view/");
    }

    /**
     * 获取应用静态资源路径
     */
    public static String getAppAssetPath() {
        return PropsUtil.getString(CONFIG_PROPS, ConfigConstant.APP_ASSET_PATH, "/asset/");
    }

    /**
     * 获取文件上传大小限制（MB）
     */
    public static int getAppUploadLimit() {
        return PropsUtil.getInt(CONFIG_PROPS, ConfigConstant.APP_UPLOAD_LIMIT, 10);
    }

    /**
     * 获取首页地址
     */
    public static String getAppHomePage() {
        return PropsUtil.getString(CONFIG_PROPS, ConfigConstant.APP_HOME_PAGE, "/index.html");
    }

    /**
     * 获取Security模块开关
     */
    public static boolean getSecurityEnable() {
        return PropsUtil.getBoolean(CONFIG_PROPS, ConfigConstant.SECURITY_ENABLE, false);
    }

    /**
     * 获取Security模块授权有效时长（秒）
     */
    public static int getSecurityExpires() {
        return PropsUtil.getInt(CONFIG_PROPS, ConfigConstant.SECURITY_EXPIRES);
    }
}
