package test.json;

import com.jfeng.framework.util.JsonUtil;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;

/**
 * Json 相关测试
 */
public class JsonTest {

    @Before
    public void init() throws Exception {

    }

    @Test
    public void fromJson() throws Exception {
        String json = "{\"a\":\"a\",\"c\":\"1\"}";
        TestPojo testPojo = JsonUtil.fromJson(json, TestPojo.class);
        System.out.println("json:\n");
        System.out.println(json);
        System.out.println("pojo:\n");
        System.out.println(testPojo);
    }

    @Test
    public void getFieldToStr1() throws Exception {
        String json0 = "{\"a\":\"a\"}";
        String fieldValue0 = JsonUtil.getFieldToStr(json0, "a");
        System.out.println("toStr:");
        System.out.println(fieldValue0);
        System.out.println();

        String json1 = "{\"a\":[\"a1\",\"a2\"]}";
        List<String> fieldValue1 = JsonUtil.getFieldToStrList(json1, "a");
        System.out.println("toStrList:");
        System.out.println(fieldValue1);
        System.out.println();

        String json2 = "{\"a\":1}";
        Integer fieldValue2 = JsonUtil.getFieldToInt(json2, "a");
        System.out.println("toInt:");
        System.out.println(fieldValue2);
        System.out.println();

        String json3 = "{\"a\":[1,2]}";
        List<Integer> fieldValue3 = JsonUtil.getFieldToIntList(json3, "a");
        System.out.println("toIntList:");
        System.out.println(fieldValue0);
        System.out.println();

        String json4 = "{\"a\":1}";
        Short fieldValue4 = JsonUtil.getFieldToShort(json4, "a");
        System.out.println("toShort:");
        System.out.println(fieldValue4);
        System.out.println();

        String json5 = "{\"a\":1}";
        Byte fieldValue5 = JsonUtil.getFieldToByte(json5, "a");
        System.out.println("toByte:");
        System.out.println(fieldValue5);
        System.out.println();

        String json6 = "{\"a\":true}";
        Boolean fieldValue6 = JsonUtil.getFieldToBool(json6, "a");
        System.out.println("toBool:");
        System.out.println(fieldValue6);
        System.out.println();

        String json7 = "{\"a\":{\"a\":\"a\",\"c\":\"1\"}}";
        TestPojo fieldValue7 = JsonUtil.getFieldToObj(json7, "a", TestPojo.class);
        System.out.println("toObj:");
        System.out.println(fieldValue7);
        System.out.println();
    }
}
