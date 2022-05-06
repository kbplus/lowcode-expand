package com.kbplus.dynamic.demo.controller;

import com.kbplus.dynamic.demo.config.ClassloaderFactory;
import com.kbplus.dynamic.demo.core.ModuleApplication;
import com.sun.webkit.network.URLs;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.MethodParameter;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author kbplus
 * @date 2022-03-28
 * @blog https://blog.csdn.net/cyy9487
 */
@Api(value = "ReloadController", tags = "刷新扩展包Api")
@RestController
@RequestMapping("/reload")
public class ReloadController implements ApplicationContextAware {

    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    @Value("${dynamic.jar}")
    private String dynamicJar;

    @Value("${dynamic.jar2}")
    private String dynamicJar2;

    private ApplicationContext applicationContext;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @RequestMapping(value = "/getUrl", method = RequestMethod.POST)
    public String getMappings() throws ClassNotFoundException {
        List<String> defaultUrl = new ArrayList<>();
        defaultUrl.add("/reload/getAllURL");
        defaultUrl.add("/reload/getUrl");
        defaultUrl.add("/reload2/reload2");
        defaultUrl.add("/reload/reload2");
        defaultUrl.add("/reload/remove");
        defaultUrl.add("/user/get");
        defaultUrl.add("/swagger-resources/configuration/security");
        defaultUrl.add("/swagger-resources/configuration/ui");
        defaultUrl.add("/swagger-resources");
        defaultUrl.add("/v2/api-docs");
        defaultUrl.add("/v3/api-docs");
        defaultUrl.add("/error");
        RequestMappingHandlerMapping mapping = applicationContext.getBean(RequestMappingHandlerMapping.class);
        // 拿到Handler适配器中的全部方法
        Map<RequestMappingInfo, HandlerMethod> methodMap = mapping.getHandlerMethods();

        for (Map.Entry<RequestMappingInfo, HandlerMethod> mappingInfoHandlerMethodEntry : methodMap.entrySet()) {
            Map<String, String> resultMap = new LinkedHashMap<>();

            RequestMappingInfo requestMappingInfo = mappingInfoHandlerMethodEntry.getKey();
            HandlerMethod handlerMethod = mappingInfoHandlerMethodEntry.getValue();

            String myUrl = String.join(",",requestMappingInfo.getPatternsCondition().getPatterns());

            if(!defaultUrl.contains(myUrl)) {
                MethodParameter[] methodParameters = handlerMethod.getMethodParameters();
                for (MethodParameter methodParameter : methodParameters) {
                    Class<?> declaringClass = methodParameter.getParameterType();
                    Field[] declaredFields = declaringClass.getDeclaredFields();
                    for (Field declaredField : declaredFields) {
                        System.out.println("声明的字段：" + declaredField.getName());
                        System.out.println("声明的字段类型：" + declaredField.getType().getTypeName());
                        ApiModelProperty annotation = declaredField.getAnnotation(ApiModelProperty.class);
                        if (annotation != null) {
                            System.out.println(declaredField.getName() + "字段的含义:" + annotation.value());
                        }
                    }
                }

                MethodParameter returnType = handlerMethod.getReturnType();
                Class<?> declaringClassBody = returnType.getParameterType();
                Field[] declaredFields = declaringClassBody.getDeclaredFields();
                for (Field declaredField : declaredFields) {
                    System.out.println("返回对象声明的字段：" + declaredField.getName());
                    System.out.println("声明的字段类型：" + declaredField.getType().getTypeName());
                    ApiModelProperty annotation = declaredField.getAnnotation(ApiModelProperty.class);
                    if (annotation != null) {
                        System.out.println(declaredField.getName() + "返回对象字段的含义:" + annotation.value());
                    }
                }

                String pathFix = "";
                String classDesc = "";
                String methodMessage = "";
                String requestType = "";
                String methodURL = "";

                Annotation[] parentAnnotations = handlerMethod.getBeanType().getAnnotations();
                for (Annotation annotation : parentAnnotations) {
                    if (annotation instanceof Api) {
                        Api api = (Api) annotation;
                        classDesc = api.value();
                        resultMap.put("classDesc", api.value());
                    } else if (annotation instanceof RequestMapping) {
                        RequestMapping requestMapping = (RequestMapping) annotation;
                        if (requestMapping.value().length > 0) {
                            pathFix = requestMapping.value()[0];
                            resultMap.put("classURL", requestMapping.value()[0]);//类URL
                        }
                    }
                }

                Annotation[] annotations = handlerMethod.getMethod().getDeclaredAnnotations();
                // 处理具体的方法信息
                for (Annotation annotation : annotations) {
                    if (annotation instanceof ApiOperation) {
                        ApiOperation methodDesc = (ApiOperation) annotation;
                        String desc = methodDesc.value();
                        resultMap.put("methodDesc", desc);//接口描述
                        methodMessage = desc;
                    }
                }


                PatternsRequestCondition p = requestMappingInfo.getPatternsCondition();
                for (String url : p.getPatterns()) {
                    resultMap.put("methodURL", url);//请求URL
                    methodURL = url;
                }
                RequestMethodsRequestCondition methodsCondition = requestMappingInfo.getMethodsCondition();
                for (RequestMethod requestMethod : methodsCondition.getMethods()) {
                    resultMap.put("requestType", requestMethod.toString());//请求方式：POST/PUT/GET/DELETE
                    if (!StringUtils.isEmpty(requestMethod.toString())) {
                        requestType = requestMethod.toString();
                    }
                }


                System.out.println("pathFix:" + pathFix);
                System.out.println("classDesc:" + classDesc);
                System.out.println("methodMessage:" + methodMessage);
                System.out.println("requestType:" + requestType);
                System.out.println("methodURL:" + methodURL);

                String url = String.join(",", requestMappingInfo.getPatternsCondition().getPatterns());
                System.out.println(requestMappingInfo.getMethodsCondition().toString().replace("[", "").replace("]", ""));
                System.out.println(url);
            }
        }
        return "ok";
    }


    @ApiOperation(nickname = "reload", value = "刷新容器")
    @PostMapping("/reload")
    public String get() throws Exception {

        File file = null;
        try {
            ModuleApplication test = ClassloaderFactory.getInstance().getClassLoader("test");
            if(test==null){
                test = new ModuleApplication();
            }
            test.reloadJar(URLs.newURL(dynamicJar),applicationContext,sqlSessionFactory);
            ClassloaderFactory.getInstance().addModuleApplication("test", test);
        } finally {
            if(file!=null){
                boolean delete = file.delete();
                System.out.println(delete);
            }
        }
        return "ok";
    }

    @PostMapping("/remove")
    public String remove() throws Exception {
        ModuleApplication test = ClassloaderFactory.getInstance().getClassLoader("test");
        if(test!=null){
            test.removeBeans(applicationContext,sqlSessionFactory);
        }
        return "ok";
    }

    @ApiOperation(nickname = "reload2", value = "刷新容器")
    @PostMapping("/reload2")
    public String get2() throws Exception {
        ModuleApplication test2 = ClassloaderFactory.getInstance().getClassLoader("test2");
        if(test2==null){
            test2 = new ModuleApplication();
        }
        test2.reloadJar(URLs.newURL(dynamicJar2),applicationContext,sqlSessionFactory);
        return "ok";
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public static List<String> getParameterNameJava8(Class<?> clazz, String methodName) {
        List<String> paramterList = new ArrayList<>();
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            if (methodName.equals(method.getName())) {
                //直接通过method就能拿到所有的参数
                Parameter[] params = method.getParameters();
                for (Parameter parameter : params) {
                    paramterList.add(parameter.getName());
                    Class<?> type = parameter.getType();
                    Field[] declaredFields = type.getDeclaredFields();
                    ArrayList<String> objects = new ArrayList<>();
                    for (Field declaredField : declaredFields) {
                        objects.add(declaredField.getName());
                        objects.add(declaredField.getType().toString());
                    }

                    paramterList.add(objects.toString());
                }
            }
        }
        return paramterList;
    }
}
