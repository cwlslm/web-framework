package test.reflect;

import com.jfeng.framework.security.bean.Rule;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ReturnTypeTest {

    public List<Rule> returnTypeTest0() {
        return new ArrayList<>();
    }

    public List<Rule> returnTypeTest1() {
        return new ArrayList<>();
    }

    @Test
    public void returnTypeTest() throws Exception {
        Class<?> cls = ReturnTypeTest.class;

        Method methodTest0 = cls.getDeclaredMethod("returnTypeTest0");
        Type genericReturnType = methodTest0.getGenericReturnType();
        Class<?> returnTypeCls = methodTest0.getReturnType();
        AnnotatedType annotatedReturnType = methodTest0.getAnnotatedReturnType();

        Method methodTest1 = cls.getDeclaredMethod("returnTypeTest1");
        Type genericReturnType1 = methodTest1.getGenericReturnType();
        Class<?> returnTypeCls1 = methodTest1.getReturnType();
        AnnotatedType annotatedReturnType1 = methodTest1.getAnnotatedReturnType();

        System.out.println(genericReturnType);
        System.out.println(returnTypeCls);
        System.out.println(annotatedReturnType);

        System.out.println(genericReturnType.equals(genericReturnType1));
    }
}
