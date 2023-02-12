package com.jfeng.framework.util;

import java.util.Random;

/**
 * 随机数工具类
 *
 * @author jfeng
 * @since 1.0.0
 */
public class RandomUtil {

    /**
     * 从 baseString 中随机选出 resultLen 个字符（baseString 中的每个字符都可重复被选出）
     */
    public static String getRandomString(String baseString, int resultLen) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < resultLen; i++) {
            int number = random.nextInt(baseString.length());
            sb.append(baseString.charAt(number));
        }
        return sb.toString();
    }
}
