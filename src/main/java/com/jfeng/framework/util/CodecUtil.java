package com.jfeng.framework.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * 编码与解码操作工具类
 *
 * @author jfeng
 * @since 1.0.0
 */
public class CodecUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(CodecUtil.class);

    /**
     * 将 URL 编码
     */
    public static String encodeURL(String source) {
        String target;
        try {
            target = URLEncoder.encode(source, StandardCharsets.UTF_8);
        } catch (Exception e) {
            LOGGER.error("encodeURL 异常", e);
            throw new RuntimeException(e);
        }
        return target;
    }

    /**
     * 将 URL 解码
     */
    public static String decodeURL(String source) {
        String target;
        try {
            target = URLDecoder.decode(source, StandardCharsets.UTF_8);
        } catch (Exception e) {
            LOGGER.error("decodeURL 异常", e);
            throw new RuntimeException(e);
        }
        return target;
    }
}
