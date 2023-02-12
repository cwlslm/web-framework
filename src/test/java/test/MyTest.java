package test;

import com.jfeng.framework.mvc.annotation.Controller;
import com.jfeng.framework.mvc.bean.Handler;
import com.jfeng.framework.mvc.bean.Request;
import com.jfeng.framework.security.AuthzType;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyTest {

    @Before
    public void init() throws Exception {

    }

    @Test
    public void test() throws Exception {
        Controller controller = MyTest.class.getAnnotation(Controller.class);
        System.out.println(controller);
    }
}
