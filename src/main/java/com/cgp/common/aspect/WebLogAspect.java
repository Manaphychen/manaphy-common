package com.cgp.common.aspect;

import com.cgp.common.entity.WebLog;
import com.cgp.common.utils.WebUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

/**
 * 统一日志处理切面
 *
 * @author Manaphy
 * @version 1.2
 * @date 2020-06-21
 */
@Aspect
@Component
@Order(1)
@Slf4j
@ConditionalOnProperty(prefix = "manaphy", name = "weblog", havingValue = "true", matchIfMissing = true)
public class WebLogAspect {

    /**
     * execution() 表达式的主体
     * 第一个`*`号 表示返回值的类型任意
     * 第二个`*`号 表示任意包名
     * `..` 表示当前包及子包
     * `*Controller` 表示任意的Controller方法
     * `.*(..)` 表示任何方法名，括号表示参数，两个点表示任何参数类型
     */
    @Pointcut("execution(public * *..*Controller.*(..))")
    public void webLog() {
    }

    @Before("webLog()")
    public void doBefore() {
    }

    @AfterReturning(value = "webLog()")
    public void doAfterReturning() {
    }

    @Around("webLog()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        //获取当前请求对象
        HttpServletRequest request = WebUtil.getRequest();

        String urlStr = request.getRequestURL().toString();
        String uriStr = request.getRequestURI();
        //获取方法名和类名
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        String clazz = joinPoint.getTarget().getClass().toString();
        String className = clazz.substring(clazz.lastIndexOf(".") + 1);
        ObjectMapper mapper = new ObjectMapper();
        //记录请求信息
        WebLog webLog = new WebLog();
        webLog.setStartTime(new Date());
        webLog.setUrl(request.getRequestURL().toString());
        webLog.setUri(uriStr);
        webLog.setIp(WebUtil.getIpAddress());
        webLog.setBasePath(urlStr.substring(0, urlStr.length() - uriStr.length()));
        webLog.setMethod(request.getMethod());
        webLog.setClassMethod(className + "." + method.getName());
        Object parameter = getParameter(method, joinPoint.getArgs());
        try {
            // 防止某些对象无法转json导致程序报错
            mapper.writeValueAsString(parameter);
            webLog.setParameter(parameter);
        } catch (JsonProcessingException ignored) {
        }
        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long endTime = System.currentTimeMillis();
        webLog.setSpendTime((int) (endTime - startTime));
        webLog.setResult(result);
        log.info(mapper.writeValueAsString(webLog));
        return result;
    }

    /**
     * 根据方法和传入的参数获取请求参数
     */
    private Object getParameter(Method method, Object[] args) {
        List<Object> argList = new ArrayList<>();
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            String typeName = parameters[i].getParameterizedType().getTypeName();
            String paramName = parameters[i].getName();
            if (!classType().contains(typeName)) {
                //将RequestBody注解修饰的参数作为请求参数
                RequestBody requestBody = parameters[i].getAnnotation(RequestBody.class);
                if (requestBody != null) {
                    argList.add(convertToMap(args[i]));
                }
                Map<String, Object> map = new TreeMap<>();
                //将RequestParam注解修饰的参数作为请求参数
                RequestParam requestParam = parameters[i].getAnnotation(RequestParam.class);
                if (requestParam != null) {
                    map.put("@type", "RequestParam");
                    map.put(paramName, args[i]);
                    argList.add(map);
                }
                //将PathVariable注解修饰的参数作为请求参数
                PathVariable pathVariable = parameters[i].getAnnotation(PathVariable.class);
                if (pathVariable != null) {
                    map.put("@type", "PathVariable");
                    map.put(paramName, args[i]);
                    argList.add(map);
                }
                Annotation[] annotations = parameters[i].getDeclaredAnnotations();
                List<Annotation> annotationsList = Arrays.asList(annotations);
                if (annotationsList.size() == 0) {
                    map.put("@type", "null");
                    map.put(paramName, args[i]);
                    argList.add(map);
                }
            }
        }
        if (argList.size() == 0) {
            return null;
        } else if (argList.size() == 1) {
            return argList.get(0);
        } else {
            return argList;
        }
    }

    /**
     * 将对象转化为Map对象
     *
     * @param obj obj
     * @return {@link Map<>}
     */
    private static Map<String, Object> convertToMap(Object obj) {
        Map<String, Object> map = null;
        try {
            map = new TreeMap<>();
            map.put("@type", "RequestBody");
            Field[] fields = obj.getClass().getDeclaredFields();
            for (Field field : fields) {
                String varName = field.getName();
                boolean accessFlag = field.isAccessible();
                field.setAccessible(true);

                Object o = field.get(obj);
                if (o != null && !"serialVersionUID".equals(varName)) {
                    map.put(varName, o.toString());
                }
                field.setAccessible(accessFlag);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 包含了不获取参数的参数类型的集合
     *
     * @return {@link List<String>}
     */
    private static List<String> classType() {
        return new ArrayList<String>() {{
            add(Model.class.getName());
            add(HttpServletRequest.class.getName());
            add(HttpServletResponse.class.getName());
            add(ServletResponse.class.getName());
            add(ServletResponse.class.getName());
        }};
    }

}
