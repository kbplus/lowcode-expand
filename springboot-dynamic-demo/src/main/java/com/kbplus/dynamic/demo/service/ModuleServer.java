package com.kbplus.dynamic.demo.service;


import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author kbplus
 * @date 2022-03-28
 * @blog https://blog.csdn.net/cyy9487
 */
@Slf4j
@Component
public class ModuleServer {

    @Autowired
    WebApplicationContext applicationContext;

    public void getController2() throws ClassNotFoundException {
        List<String> defaultUrl = new ArrayList<>();
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

            RequestMappingInfo requestMappingInfo = mappingInfoHandlerMethodEntry.getKey();
            HandlerMethod handlerMethod = mappingInfoHandlerMethodEntry.getValue();

            String myUrl = String.join(",", requestMappingInfo.getPatternsCondition().getPatterns());

            if (!defaultUrl.contains(myUrl)) {
                MethodParameter[] methodParameters = handlerMethod.getMethodParameters();
                for (MethodParameter methodParameter : methodParameters) {
                    Class<?> declaringClass = methodParameter.getParameterType();
                    Field[] declaredFields = declaringClass.getDeclaredFields();
                    for (Field declaredField : declaredFields) {
                        ApiModelProperty annotation = declaredField.getAnnotation(ApiModelProperty.class);
                    }
                }

                MethodParameter returnType = handlerMethod.getReturnType();
                Class<?> declaringClassBody = returnType.getParameterType();
                Field[] declaredFields = declaringClassBody.getDeclaredFields();
                Type type = handlerMethod.getReturnType().getNestedGenericParameterType();
                if (type instanceof ParameterizedType) {
                    ParameterizedType pp = (ParameterizedType) type;
                    Type[] actualTypeArguments = pp.getActualTypeArguments();
                    if (actualTypeArguments.length != 0) {
                        Type actualType = actualTypeArguments[0];
                        String typeName = actualType.getTypeName();
                        Class<?> aClass = Class.forName(typeName);
                        for (Field declaredField : aClass.getDeclaredFields()) {
                            ApiModelProperty annotation = declaredField.getAnnotation(ApiModelProperty.class);
                        }
                    }
                }

                for (Field declaredField : declaredFields) {
                    ApiModelProperty annotation = declaredField.getAnnotation(ApiModelProperty.class);
                    Field[] fields = declaredField.getType().getDeclaredFields();
                    if(fields.length>0){
                        System.out.println("类名有里字段："+declaredField.getType().getName());
                        for (Field field : fields) {
                            System.out.println(field.getName());
                            ApiModelProperty annotation2 = field.getAnnotation(ApiModelProperty.class);
                            if(annotation2!=null){
                                System.out.println("里结果字段描述："+annotation2.value());
                            }
                        }
                    }
                    Type genericType = declaredField.getGenericType();
                    if(genericType instanceof ParameterizedType){
                        ParameterizedType pp = (ParameterizedType)genericType;
                        Type[] actualTypeArguments = pp.getActualTypeArguments();
                        if (actualTypeArguments.length != 0) {
                            Type actualType = actualTypeArguments[0];
                            String typeName = actualType.getTypeName();
                            Class<?> aClass = Class.forName(typeName);
                            for (Field declaredField2 : aClass.getDeclaredFields()) {
                                ApiModelProperty annotation3 = declaredField2.getAnnotation(ApiModelProperty.class);
                                if(annotation3!=null){
                                    System.out.println("里结果字段描述3："+annotation3.value());
                                }
                            }
                        }
                    }
                }

                String methodMessage = "";

                Annotation[] annotations = handlerMethod.getMethod().getDeclaredAnnotations();
                // 处理具体的方法信息
                for (Annotation annotation : annotations) {
                    if (annotation instanceof ApiOperation) {
                        ApiOperation methodDesc = (ApiOperation) annotation;
                        methodMessage = methodDesc.value();
                    }
                }
                System.out.println("methodMessage:" + methodMessage);
            }
        }
    }
}
