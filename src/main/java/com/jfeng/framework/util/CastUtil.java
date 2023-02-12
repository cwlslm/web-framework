package com.jfeng.framework.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 转型操作工具类
 *
 * @author jfeng
 * @since 1.0.0
 */
public class CastUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(CastUtil.class);

    /**
     * 转为 String 型（提供默认值）
     */
    public static String castString(Object obj, String defaultValue) {
        return obj !=null ? String.valueOf(obj) : defaultValue;
    }

    /**
     * 转为 String 型
     */
    public static String castString(Object obj) {
        return castString(obj, "");
    }

    /**
     * 转为 List<String> 型（提供默认值）
     */
    public static List<String> castStringList(Object obj, List<String> defaultValue) {
        return obj !=null ? List.of(String.valueOf(obj).split(",")) : defaultValue;
    }

    /**
     * 转为 List<String> 型
     */
    public static List<String> castStringList(Object obj) {
        return castStringList(obj, new ArrayList<>());
    }

    /**
     * 转为 double 型（提供默认值）
     */
    public static double castDouble(Object obj, double defaultValue) {
        if (obj != null) {
            String strValue = castString(obj);
            if (StringUtil.isNotEmpty(strValue)) {
                try {
                    defaultValue = Double.parseDouble(strValue);
                } catch (NumberFormatException ignored) {

                }
            }
        }
        return defaultValue;
    }

    /**
     * 转为 double 型
     */
    public static double castDouble(Object obj) {
        return castDouble(obj, 0);
    }

    /**
     * 转为 long 型（提供默认值）
     */
    public static long castLong(Object obj, long defaultValue) {
        if (obj != null) {
            String strValue = castString(obj);
            if (StringUtil.isNotEmpty(strValue)) {
                try {
                    defaultValue = Long.parseLong(strValue);
                } catch (NumberFormatException ignored) {

                }
            }
        }
        return defaultValue;
    }

    /**
     * 转为 long 型
     */
    public static long castLong(Object obj) {
        return castLong(obj, 0);
    }

    /**
     * 转为 int 型（提供默认值）
     */
    public static int castInt(Object obj, int defaultValue) {
        if (obj != null) {
            String strValue = castString(obj);
            if (StringUtil.isNotEmpty(strValue)) {
                try {
                    defaultValue = Integer.parseInt(strValue);
                } catch (NumberFormatException ignored) {

                }
            }
        }
        return defaultValue;
    }

    /**
     * 转为 int 型
     */
    public static int castInt(Object obj) {
        return castInt(obj, 0);
    }

    /**
     * 转为 boolean 型（提供默认值）
     */
    public static boolean castBoolean(Object obj, boolean defaultValue) {
        if (obj != null) {
            String strValue = castString(obj);
            if (StringUtil.isNotEmpty(strValue)) {
                try {
                    defaultValue = Boolean.parseBoolean(strValue);
                } catch (NumberFormatException ignored) {

                }
            }
        }
        return defaultValue;
    }

    /**
     * 转为 boolean 型
     */
    public static boolean castBoolean(Object obj) {
        return castBoolean(obj, false);
    }
}
