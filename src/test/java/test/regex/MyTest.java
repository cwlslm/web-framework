package test.regex;

import org.junit.Test;

import java.util.regex.Pattern;

public class MyTest {

    @Test
    public void compileTest() throws Exception {
        String url = "/hello/(.*)";
        String path = "/hello/22";

        Pattern pattern = Pattern.compile(url);
        boolean res = pattern.matcher(path).matches();
        System.out.println(res);
    }
}
