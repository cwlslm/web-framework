package com.jfeng.framework.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * JSON 工具类
 *
 * @author jfeng
 * @since 1.0.0
 */
public class JsonUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonUtil.class);

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * 将 POJO 转为 JSON
     */
    public static <T> String toJson(T obj) {
        String json;
        try {
            json = OBJECT_MAPPER.writeValueAsString(obj);
        } catch (Exception e) {
            LOGGER.error("toJson 异常", e);
            throw new RuntimeException(e);
        }
        return json;
    }

    /**
     * 将 JSON 转为 POJO
     */
    @SuppressWarnings("unchecked")
    public static <T> T fromJson(String json, Class<T> type) {
        try {
            // 如果此函数参数类型是字符串，无需转换
            if (type == String.class) {
                return (T) json;
            }
            else {
                return OBJECT_MAPPER.readValue(json, type);
            }
        } catch (Exception e) {
            LOGGER.error("fromJson 异常", e);
            throw new RuntimeException(e);
        }
    }

    public static JsonNode toJsonNode(String json) {
        if (json == null) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readTree(json);
        } catch (IOException e) {
            LOGGER.error("toJsonNode 异常", e);
            throw new RuntimeException(e);
        }
    }

    private static <T> T getFieldValue(String json, String fieldName, TypeReference<T> valueTypeRef) {
        try {
            JsonNode leaf = OBJECT_MAPPER.readTree(json).get(fieldName);
            if (leaf != null) {
                return OBJECT_MAPPER.convertValue(leaf, valueTypeRef);
            }
        } catch (IOException e) {
            LOGGER.error("getFieldValue IOException异常", e);
        }
        return null;
    }

    public static String getFieldToStr(String json, String fieldName) {
        return getFieldValue(json, fieldName, new TypeReference<String>() {});
    }

    public static List<String> getFieldToStrList(String json, String fieldName) {
        return getFieldValue(json, fieldName, new TypeReference<List<String>>() {});
    }

    public static Integer getFieldToInt(String json, String fieldName) {
        return getFieldValue(json, fieldName, new TypeReference<Integer>() {});
    }

    public static List<Integer> getFieldToIntList(String json, String fieldName) {
        return getFieldValue(json, fieldName, new TypeReference<List<Integer>>() {});
    }

    public static Short getFieldToShort(String json, String fieldName) {
        return getFieldValue(json, fieldName, new TypeReference<Short>() {});
    }

    public static Byte getFieldToByte(String json, String fieldName) {
        return getFieldValue(json, fieldName, new TypeReference<Byte>() {});
    }

    public static Boolean getFieldToBool(String json, String fieldName) {
        return getFieldValue(json, fieldName, new TypeReference<Boolean>() {});
    }

    public static <T> T getFieldToObj(String json, String fieldName, Class<T> cls) {
        try {
            JsonNode leaf = OBJECT_MAPPER.readTree(json).get(fieldName);
            if (leaf != null) {
                return OBJECT_MAPPER.treeToValue(leaf, cls);
            }
        } catch (IOException e) {
            LOGGER.error("getFieldToObj IOException异常", e);
        }
        return null;
    }
}
