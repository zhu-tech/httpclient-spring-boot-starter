package org.zhu.httpclientspringbootstarter.factory;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Proxy;

/**
 * http客户端接口代理工厂类
 *
 * @author zhuru
 */
public class HttpClientProxyFactory<T> implements FactoryBean<T> {

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 被代理的类
     */
    private Class<T> interfaceClass;

    public HttpClientProxyFactory(Class<T> interfaceClass) {
        this.interfaceClass = interfaceClass;
    }

    /**
     * 创建代理类
     *
     * @return
     */
    @Override
    public T getObject() {
        DefaultHttpClientRepository repository = new DefaultHttpClientRepository(restTemplate, objectMapper);
        Object newProxyInstance = Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class[]{interfaceClass},
                repository);
        return (T) newProxyInstance;
    }

    @Override
    public Class<?> getObjectType() {
        return interfaceClass;
    }
}
