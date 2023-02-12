package com.jfeng.framework.mvc;

import com.jfeng.framework.core.ConfigHelper;
import com.jfeng.framework.ioc.BeanHelper;
import com.jfeng.framework.mvc.annotation.*;
import com.jfeng.framework.mvc.bean.*;
import com.jfeng.framework.core.ClassHelper;
import com.jfeng.framework.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 控制器助手类
 *
 * @author jfeng
 * @since 1.0.0
 */
public class ControllerHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(ControllerHelper.class);

    /**
     * 定义Action映射（用于存放请求与处理器的映射关系）
     */
    private static final Map<Request, Handler> ACTION_MAP = new HashMap<>();

    /**
     * 定义Action异常处理函数映射（用于存放Action异常类型与异常处理函数的映射关系）
     */
    private static final Map<Class<?>, List<ActionExceptionHandler>> ACTION_EXCEPTION_MAP = new HashMap<>();

    static {
        Set<Class<?>> controllerClassSet = ClassHelper.getControllerClassSet();
        if (CollectionUtil.isNotEmpty(controllerClassSet)) {
            for (Class<?> controllerClass: controllerClassSet) {
                Method[] methods = controllerClass.getDeclaredMethods();
                if (ArrayUtil.isNotEmpty(methods)) {
                    Controller controllerAnnotation = controllerClass.getAnnotation(Controller.class);
                    String basePath = controllerAnnotation.value();
                    for (Method method: methods) {
                        if (method.isAnnotationPresent(Action.Get.class)) {
                            String requestPath = method.getAnnotation(Action.Get.class).value();
                            putActionMap("GET", requestPathConcat(basePath, requestPath), controllerClass, method);
                        }
                        if (method.isAnnotationPresent(Action.Post.class)) {
                            String requestPath = method.getAnnotation(Action.Post.class).value();
                            putActionMap("POST", requestPathConcat(basePath, requestPath), controllerClass, method);
                        }
                        if (method.isAnnotationPresent(Action.Put.class)) {
                            String requestPath = method.getAnnotation(Action.Put.class).value();
                            putActionMap("PUT", requestPathConcat(basePath, requestPath), controllerClass, method);
                        }
                        if (method.isAnnotationPresent(Action.Delete.class)) {
                            String requestPath = method.getAnnotation(Action.Delete.class).value();
                            putActionMap("DELETE", requestPathConcat(basePath, requestPath), controllerClass, method);
                        }
                    }
                }
            }
        }

        // 获取所有配置类
        Set<Class<?>> configurationClassSet = ClassHelper.getClassSetByAnnotation(Configuration.class);
        for (Class<?> configCls: configurationClassSet) {
            for (Method method: configCls.getDeclaredMethods()) {
                // 找到加了@ActionException注解的函数
                // （加了@ActionException注解说明其为定义的一个异常处理函数，将对应信息添加到ACTION_EXCEPTION_MAP）
                if (method.isAnnotationPresent(ActionException.class)) {
                    ActionExceptionHandler actionExceptionHandler = new ActionExceptionHandler(configCls, method);
                    Class<?> exceptionCls = method.getAnnotation(ActionException.class).value();
                    if (!ACTION_EXCEPTION_MAP.containsKey(exceptionCls)) {
                        List<ActionExceptionHandler> actionExceptionHandlerList = new ArrayList<>();
                        actionExceptionHandlerList.add(actionExceptionHandler);
                        ACTION_EXCEPTION_MAP.put(exceptionCls, actionExceptionHandlerList);
                    }
                    else {
                        ACTION_EXCEPTION_MAP.get(exceptionCls).add(actionExceptionHandler);
                    }
                }
            }
        }
    }

    private static String requestPathConcat(String basePath, String requestPath) {
        if (basePath.length() != 0  && requestPath.length() != 0) {
            if (!basePath.endsWith("/") && !requestPath.startsWith("/")) {
                requestPath = "/" + requestPath;
            }
            else if (basePath.endsWith("/") && requestPath.startsWith("/")) {
                requestPath = requestPath.substring(1);
            }
        }
        requestPath = basePath + requestPath;
        return requestPath;
    }

    private static void putActionMap(String requestType, String requestPath, Class<?> controllerClass, Method method) {
        // 请求路径参数名称数组
        List<String> requestPathParamNames = new ArrayList<>();
        // 判断 Request Path 中是否带有占位符  regex: .+\{.+?}.*
        if (requestPath.matches(".+\\{.+?}.*")) {
            // 将请求路径中的占位符 {(.+?)} 转换为正则表达式 (.+?)  regex: \{(.+?)}  replacement: (.+?)
            String regex = "\\{(.+?)}";
            String replacement = "(.+?)";
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(requestPath);
            StringBuffer sb = new StringBuffer();
            while (m.find()) {
                requestPathParamNames.add(m.group(1));
                m.appendReplacement(sb, replacement);
            }
            m.appendTail(sb);
            requestPath = sb.toString();
        }
        // 判断有无重复定义Request
        Request request = new Request(requestType, requestPath);
        if (ACTION_MAP.containsKey(request)) {
            LOGGER.warn(String.format("Request重复定义：\r\n%s", request));
            return;
        }
        ACTION_MAP.put(request, new Handler(controllerClass, method, requestPathParamNames));
    }

    /**
     * 获取 Handler
     */
    public static Handler getHandler(String currentRequestType, String currentRequestPath) {
        Handler handler = null;
        for (Map.Entry<Request, Handler> actionEntry : ACTION_MAP.entrySet()) {
            Request request = actionEntry.getKey();
            String requestType = request.getRequestType();
            String requestPath = request.getRequestPath();
            // 获取请求路径匹配器（使用正则表达式匹配请求路径并从中获取相应的请求参数）
            Matcher requestPathMatcher = Pattern.compile(requestPath).matcher(currentRequestPath);
            // 判断请求方法与请求路径是否同时匹配
            if (requestType.equalsIgnoreCase(currentRequestType) && requestPathMatcher.matches()) {
                handler = actionEntry.getValue();
                if (handler != null) {
                    handler.setRequestPathMatcher(requestPathMatcher);
                }
                break;
            }
        }
        return handler;
    }

    /**
     * 处理 Action 方法返回值 （返回 ResultView 对象时）
     */
    public static void handleViewResult(ResultView resultView, HttpServletRequest request,
                                        HttpServletResponse response)
            throws IOException, ServletException {
        String path = resultView.getPath();
        if (StringUtil.isNotEmpty(path)) {
            if (resultView.isRedirect()) {
                // 重定向请求
                WebUtil.redirectRequest(path, request, response);
            } else {
                Map<String, Object> data = resultView.getData();
                for (Map.Entry<String, Object> entry: data.entrySet()) {
                    request.setAttribute(entry.getKey(), entry.getValue());
                }
                // 转发请求
                WebUtil.forwardRequest(ConfigHelper.getAppJspPath() + path, request, response);
            }
        }
    }

    /**
     * 处理 Action 方法返回值 （返回非 ResultView 对象时）
     */
    public static void handleDataResult(Object data, HttpServletRequest request,
                                  HttpServletResponse response) throws IOException {
        // 对于 multipart 类型，说明是文件上传，需要转换为 HTML 格式并写入响应中
        if (FileUploadHelper.isMultiPart(request)) {
            WebUtil.writeHTML(response, data);
        }
        // 对于其它类型，统一转换为 JSON 格式并写入响应中
        else {
            WebUtil.writeJSON(response, data);
        }
    }

    /**
     * 处理Action异常
     */
    public static void resolveHandlerException(HttpServletRequest request, HttpServletResponse response, Throwable e) {
        // 判断异常原因
        if (ACTION_EXCEPTION_MAP.containsKey(e.getClass())) {
            try {
                List<ActionExceptionHandler> actionExceptionHandlerList = ACTION_EXCEPTION_MAP.get(e.getClass());
                for (ActionExceptionHandler actionExceptionHandler: actionExceptionHandlerList) {
                    Class<?> configurationCls = actionExceptionHandler.getConfigurationClass();
                    Method method = actionExceptionHandler.getActionExceptionMethod();
                    // 构建参数列表
                    List<Object> paramList = new ArrayList<>();
                    // 获取函数参数信息
                    Parameter[] parameters = method.getParameters();
                    // 函数参数只能为0或1个，且参数类型必须继承自Throwable
                    if (parameters.length > 1 ||
                            (parameters.length == 1 && !Throwable.class.isAssignableFrom(parameters[0].getType()))) {
                        throw new RuntimeException(String.format("Action异常处理函数'%s'参数异常", method.getName()));
                    }
                    if (parameters.length == 1) {
                        paramList.add(e);
                    }
                    Object configurationIns = BeanHelper.getBean(configurationCls);
                    ReflectionUtil.invokeMethod(configurationIns, method, paramList.toArray());
                }
            }
            catch (Exception e1) {
                WebUtil.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "服务器错误", response);
            }
        }
        // 未定义的异常
        else {
            WebUtil.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "服务器错误", response);
        }
    }

    /**
     * 构建 Action 方法参数列表
     */
    public static List<Object> createActionMethodParamList(Handler handler) throws Exception {
        List<Object> paramList = new ArrayList<>();

        // 获取上下文对象
        HttpServletRequest request = DataContext.getRequest();
        HttpServletResponse response = DataContext.getResponse();

        // 创建 RequestParam
        RequestParam requestParam;
        if (FileUploadHelper.isMultiPart(request)) {
            // 如果是上传文件的请求
            requestParam = FileUploadHelper.createParam(request);
        } else {
            requestParam = RequestParamHelper.createParam(request);
        }

        Method actionMethod = handler.getActionMethod();
        // 获取 Action 方法参数
        Parameter[] parameters = actionMethod.getParameters();
        for (Parameter parameter: parameters) {
            // 参数类型
            Class<?> parameterType = parameter.getType();
            // 参数名
            String parameterName = parameter.getName();
            // 参数值
            Object val;
            // 成功获取到 val 的标志
            boolean success = true;

            // 首先通过参数类型匹配
            // HttpServletRequest 类型
            if (HttpServletRequest.class.isAssignableFrom(parameterType)) {
                val = request;
            }
            // HttpServletResponse 类型
            else if (HttpServletResponse.class.isAssignableFrom(parameterType)) {
                val = response;
            }
            // RequestParam 类型
            else if (RequestParam.class.isAssignableFrom(parameterType)) {
                val = requestParam;
            }
            // 如果是其他类型且存在
            else if (requestParam.exists(parameterName)) {
                Object[] res = getFieldValue(parameterName, parameterType, requestParam);
                val = res[0];
                success = (boolean) res[1];
            }
            // 如果找不到则按照不同参数类型赋值默认值
            else {
                val = getDefaultByClass(parameterType);
                success = false;
            }

            // 对有注解的参数做额外处理
            // PathVariable 获取路径中的参数
            if (parameter.isAnnotationPresent(PathVariable.class)) {
                success = false;
                PathVariable pathVariable = parameter.getAnnotation(PathVariable.class);

                // 获得此路径中的所有占位符名称
                List<String> requestPathParamNames = handler.getRequestPathParamNames();
                Matcher matcher = handler.getRequestPathMatcher();

                // 默认搜索名称为此方法参数名，若注解设置了名称则用注解内的名称
                String targetParamName = parameter.getName();
                if (!pathVariable.value().equals("")) {
                    targetParamName = pathVariable.value();
                }

                // 搜索占位符名称中有无对应的 targetParamName
                int i = 0;
                for (; i < requestPathParamNames.size(); i++) {
                    if (requestPathParamNames.get(i).equals(targetParamName)) {
                        break;
                    }
                }
                if (i < requestPathParamNames.size()) {
                    String paramValue = matcher.group(i+1);

                    Object result = null;
                    try {
                        result = JsonUtil.fromJson(paramValue, parameterType);
                    } catch (Exception ignore) {}
                    if (result != null) {
                        success = true;
                        val = result;
                    }
                }
            }
            // RequestVariable 设置请求参数对应的名称和默认值
            if (parameter.isAnnotationPresent(RequestVariable.class)) {
                RequestVariable requestVariable = parameter.getAnnotation(RequestVariable.class);

                // 注解参数名称与此函数参数名不同（否则上面已进行赋值） && 注解参数名称不为空 && 请求体中存在与参数名称相同的字段
                if (!requestVariable.value().equals(parameterName)
                        && !requestVariable.value().equals("") && requestParam.exists(requestVariable.value())) {
                    Object[] res = getFieldValue(requestVariable.value(), parameterType, requestParam);
                    val = res[0];
                    success = (boolean) res[1];
                }

                if (!success && !requestVariable.defaultValue().equals("")) {
                    Object result = null;
                    try {
                        result = JsonUtil.fromJson(requestVariable.defaultValue(), parameterType);
                    } catch (Exception ignore) {}
                    if (result != null) {
                        success = true;
                        val = result;
                    }
                }
            }
            // RequestBody 获取请求体 request 的 body
            if (parameter.isAnnotationPresent(RequestBody.class)) {
                success = false;
                // 如果不是基本类型
                if (Object.class.isAssignableFrom(parameterType)) {
                    val = fromJson(request, parameterType);
                    if (val != null) {
                        success = true;
                    }
                }
            }

            // 如果找不到则按照不同参数类型赋值默认值
            if (!success) {
                val = getDefaultByClass(parameterType);
            }

            paramList.add(val);
        }
        return paramList;
    }

    /**
     * 根据不同的类型返回特定的默认值
     */
    private static Object getDefaultByClass(Class<?> cls) {
        Object val;
        if (cls.equals(byte.class)) {
            val = 0;
        }
        else if (cls.equals(short.class)) {
            val = 0;
        }
        else if (cls.equals(int.class)) {
            val = 0;
        }
        else if (cls.equals(long.class)) {
            val = 0;
        }
        else if (cls.equals(float.class)) {
            val = 0;
        }
        else if (cls.equals(double.class)) {
            val = 0;
        }
        else if (cls.equals(boolean.class)) {
            val = false;
        }
        else if (cls.equals(char.class)) {
            val = (char) 0;
        }
        else {
            val = null;
        }
        return val;
    }

    /**
     * JSON 转 POJO
     */
    private static Object fromJson(HttpServletRequest request, Class<?> parameterType) {
        Object object = null;
        // 尝试用 Json 转 POJO 的方式
        try {
            String body = CodecUtil.decodeURL(StreamUtil.getString(request.getInputStream()));
            object = JsonUtil.fromJson(body, parameterType);
        } catch (Exception ignore) {}
        return object;
    }

    /**
     * 根据 parameterName 从 requestParam 中获取参数值，并转换为 parameterType 类型
     * 若转换失败，则调用 getDefaultByClass 获取对应类型的默认值
     * 返回值数组的第一个元素是数据，第二个元素是成功标志（成功为 true，失败为 false）
     */
    private static Object[] getFieldValue(String parameterName,
                                        Class<?> parameterType,
                                        RequestParam requestParam) {
        Object result;
        boolean success = true;
        String valStr = (String) requestParam.getFieldValue(parameterName);
        try {
            result = JsonUtil.fromJson(valStr, parameterType);
        } catch (Exception e) {
            result = getDefaultByClass(parameterType);
            success = false;
        }
        return new Object[] { result, success };
    }
}
