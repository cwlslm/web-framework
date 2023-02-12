package com.jfeng.framework.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * 属性文件（.properties）工具类
 *
 * @author jfeng
 * @since 1.0.0
 */
public class PropsUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(PropsUtil.class);

    /**
     * 加载属性文件
     */
    public static Properties loadProps(String fileName) {
        Properties props = null;
        InputStream is = null;
        try {
            is = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
            if (is == null) {
                throw new FileNotFoundException(fileName + "file is not found");
            }
            props = new Properties();
            props.load(is);
        } catch (IOException e) {
            LOGGER.error("loadProps IOException异常", e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    LOGGER.error("loadProps IOException异常，流关闭异常", e);
                }
            }
        }
        return props;
    }

    /**
     * 获取字符型属性（可指定默认值）
     */
    public static String getString(Properties props, String key, String defaultValue) {
        if (props.containsKey(key)) {
            defaultValue = props.getProperty(key);
        }
        return defaultValue;
    }

    /**
     * 获取字符型属性（默认值为空字符串）
     */
    public static String getString(Properties props, String key) {
        return getString(props, key, "");
    }

    /**
     * 获取字符型列表（List<String>）属性（可指定默认值）
     */
    public static List<String> getStringList(Properties props, String key, List<String> defaultValue) {
        if (props.containsKey(key)) {
            defaultValue = CastUtil.castStringList(props.getProperty(key));
        }
        return defaultValue;
    }

    /**
     * 获取字符型列表（List<String>）属性（默认值为空列表）
     */
    public static List<String> getStringList(Properties props, String key) {
        return getStringList(props, key, new ArrayList<>());
    }

    /**
     * 获取数值型属性（可指定默认值）
     */
    public static int getInt(Properties props, String key, int defaultValue) {
        if (props.containsKey(key)) {
            defaultValue = CastUtil.castInt(props.getProperty(key));
        }
        return defaultValue;
    }

    /**
     * 获取数值型属性（默认值为 0）
     */
    public static int getInt(Properties props, String key) {
        return getInt(props, key, 0);
    }

    /**
     * 获取布尔型属性（可指定默认值）
     */
    public static boolean getBoolean(Properties props, String key, boolean defaultValue) {
        if (props.containsKey(key)) {
            defaultValue = CastUtil.castBoolean(props.getProperty(key));
        }
        return defaultValue;
    }

    /**
     * 获取数值型属性（默认值为 false）
     */
    public static boolean getBoolean(Properties props, String key) {
        return getBoolean(props, key, false);
    }
}
