package org.zhu.httpclientspringbootstarter.bean;

/**
 * 发送请求对象
 *
 * @author zhuru
 */
public class ResponseBean {

    /**
     * 根路由，一般是网址
     */
    private String baseUri;

    /**
     * 业务模块 默认""
     */
    private String module;

    /**
     * 具体业务路径
     */
    private String path;

    /**
     * 请求体，请求参数
     */
    private Object request;

    /**
     * 请求方式
     */
    private String method;

    public String getBaseUri() {
        return baseUri;
    }

    public void setBaseUri(String baseUri) {
        this.baseUri = baseUri;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Object getRequest() {
        return request;
    }

    public void setRequest(Object request) {
        this.request = request;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public ResponseBean() {
    }

    public String getRequestUrl() {
        return this.baseUri + this.module + this.path;
    }
}
