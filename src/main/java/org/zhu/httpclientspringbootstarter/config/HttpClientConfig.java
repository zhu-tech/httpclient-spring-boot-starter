package org.zhu.httpclientspringbootstarter.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 客户端配置
 *
 * @author zhuru
 */
@ConfigurationProperties(prefix = "httpclient")
public class HttpClientConfig {

    /**
     * 超时时间
     */
    private int timeout = -1;

    /**
     * 最大重试次数
     */
    private int maxRedirects = -1;

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getMaxRedirects() {
        return maxRedirects;
    }

    public void setMaxRedirects(int maxRedirects) {
        this.maxRedirects = maxRedirects;
    }
}
