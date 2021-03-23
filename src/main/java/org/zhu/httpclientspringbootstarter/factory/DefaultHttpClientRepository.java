package org.zhu.httpclientspringbootstarter.factory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.zhu.httpclientspringbootstarter.annotation.HttpClient;
import org.zhu.httpclientspringbootstarter.annotation.Param;
import org.zhu.httpclientspringbootstarter.annotation.PathVariable;
import org.zhu.httpclientspringbootstarter.annotation.ResponseLine;
import org.zhu.httpclientspringbootstarter.bean.ResponseBean;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.io.IOException;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 创建Http动态代理类,接口的方法实现
 *
 * @author zhuru
 */
public class DefaultHttpClientRepository implements InvocationHandler {

    private final RestTemplate restTemplate;
    private ObjectMapper objectMapper;

    public DefaultHttpClientRepository(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * 目标类，也就是被代理对象
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //如果传进来是一个已实现的具体类（本次演示略过此逻辑)
        if (Object.class.equals(method.getDeclaringClass())) {
            try {
                return method.invoke(this, args);
            } catch (Throwable t) {
                t.printStackTrace();
            }
            //如果传进来的是一个接口（核心)
        } else {
            return run(method, args);
        }
        return null;
    }


    /**
     * 实现接口的核心方法
     *
     * @param method 代理方法
     * @param args   参数
     * @return
     */
    public Object run(Method method, Object[] args) {
        ResponseBean responseBean = this.getResponseBean(method, args);
        Class<?> returnType = method.getReturnType();
        ResponseEntity<Map> forEntity = null;
        if (responseBean.getMethod().toLowerCase().equals("get")) {
            // get请求
            forEntity = restTemplate.getForEntity(
                    responseBean.getRequestUrl(),
                    Map.class
            );
        } else if (responseBean.getMethod().toLowerCase().equals("post")) {
            // post请求
            forEntity = restTemplate.postForEntity(
                    responseBean.getRequestUrl(),
                    responseBean.getRequest(),
                    Map.class
            );
        } else if (responseBean.getMethod().toLowerCase().equals("put")) {
            // put请求
            restTemplate.put(responseBean.getRequestUrl(), responseBean.getRequest());
        } else if (responseBean.getMethod().toLowerCase().equals("delele")) {
            // delete请求
            restTemplate.delete(responseBean.getRequestUrl());
        }

        // 判断请求是否成功
        if (!forEntity.getStatusCode().equals(HttpStatus.OK)) {
            throw new RuntimeException("网络请求异常: " + forEntity.getBody());
        }

        // 原始结果集
        Map body = Objects.requireNonNull(forEntity).getBody();

        // 判断请求是否成功
        if (!Objects.requireNonNull(body).get("code").equals(0)) {
            throw new RuntimeException(body.get("msg").toString());
        }

        // 结果为null不需要处理
        if (body.get("data") == null) {
            return null;
        }

        if (isWrapClass(returnType)) {
            // 处理基本类型
            return body.get("data");
        }

        Object result;

        if (!(body.get("data") instanceof String)) {
            try {
                result = objectMapper.writeValueAsString(body.get("data"));
            } catch (JsonProcessingException e) {
                throw new RuntimeException("JSON解析失败");
            }
        } else {
            result = body.get("data").toString();
        }

        if (List.class.isAssignableFrom(returnType)) {
            // 处理集合
            Type genericReturnType = method.getGenericReturnType();
            Class clazz = null;
            if (genericReturnType instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) genericReturnType;
                Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                Type actualTypeArgument = actualTypeArguments[0];
                // 获取泛型类型
                if (actualTypeArgument instanceof ParameterizedTypeImpl) {
                    ParameterizedTypeImpl parameterizedType1 = (ParameterizedTypeImpl) actualTypeArgument;
                    clazz = parameterizedType1.getRawType();
                } else {
                    clazz = (Class) actualTypeArgument;
                }
            }
            try {
                return objectMapper.readValue(
                        result.toString(),
                        objectMapper.getTypeFactory().constructCollectionType(ArrayList.class, clazz)
                );
            } catch (IOException e) {
                throw new RuntimeException("JSON解析失败");
            }
        } else {
            // 处理其他类型
            try {
                return objectMapper.readValue(result.toString(), returnType);
            } catch (IOException e) {
                throw new RuntimeException("JSON解析失败");
            }
        }
    }

    /**
     * 解析注解生成Bean
     *
     * @param method 代理方法
     * @param args   参数
     */
    protected ResponseBean getResponseBean(Method method, Object[] args) {
        // 请求方式
        ResponseBean responseBean = new ResponseBean();

        // 解析method上的注解
        ResponseLine annotation = method.getAnnotation(ResponseLine.class);
        String responseText = (String) annotation.value();
        if (responseText.length() <= 0) {
            throw new RuntimeException("ResponseLine 参数错误。 请按照 'GET /xxxx/xxxx' 格式");
        }
        String[] strings = responseText.split(" ");
        if (strings.length != 2) {
            throw new RuntimeException("ResponseLine 参数错误。 请按照 'GET /xxxx/xxxx' 格式");
        }
        if (StringUtils.isEmpty(strings[0])) {
            throw new RuntimeException("请求方式不能为空");
        }
        responseBean.setMethod(strings[0]);
        responseBean.setPath(strings[1]);
        // 标记get和delete方法
        boolean hasStringParam = responseBean.getMethod().toLowerCase().equals("get") || responseBean.getMethod().toLowerCase().equals("delete");

        // 解析接口注解
        Class<?> declaringClass = method.getDeclaringClass();
        HttpClient httpClient = declaringClass.getAnnotation(HttpClient.class);
        responseBean.setBaseUri(httpClient.remote());
        responseBean.setModule(httpClient.path());

        // 解析参数
        Parameter[] parameters = method.getParameters();
        // 保存post和put参数的对象
        MultiValueMap<String, Object> request = new LinkedMultiValueMap<>();
        // 保存get和delete参数
        String pathVariableParams = null;
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];

            if (!isWrapClass(parameter.getType())) {
                responseBean.setRequest(args[i]);
                break;
            }

            // 解析路径参数
            PathVariable pathVariable = parameter.getAnnotation(PathVariable.class);
            if (pathVariable != null) {
                String key = pathVariable.value();
                if (StringUtils.isEmpty(key)) {
                    key = parameter.getName();
                }
                String path = responseBean.getPath();
                String replacePath = path.replace("{" + key + "}", args[i].toString());
                responseBean.setPath(replacePath);
            } else {
                // 解析普通参数
                Param parameterAnnotation = parameter.getAnnotation(Param.class);
                String key;
                if (parameterAnnotation != null && !StringUtils.isEmpty(parameterAnnotation.value())) {
                    key = parameterAnnotation.value();
                } else {
                    key = parameter.getName();
                }
                if (hasStringParam) {
                    // 如果式get或者delete请求，则需要字符串类型参数
                    if (pathVariableParams == null) {
                        pathVariableParams = "?" + key + "=" + args[i];
                    } else {
                        pathVariableParams += "&" + key + "=" + args[i];
                    }
                } else {
                    // 如果式post或者put请求，则需要对象类型参数
                    request.add(key, args[i]);
                }
            }
        }

        // 保存解析的参数
        if (hasStringParam) {
            responseBean.setPath(responseBean.getPath() + pathVariableParams);
        } else {
            if (responseBean.getRequest() == null) {
                responseBean.setRequest(request);
            }
        }
        return responseBean;
    }

    /**
     * 判断是否是基本类型
     *
     * @param clz 判断是否是基本类型
     * @return
     */
    protected static boolean isWrapClass(Class clz) {
        try {
            return ((Class) clz.getField("TYPE").get(null)).isPrimitive();
        } catch (Exception e) {
            return false;
        }
    }
}
