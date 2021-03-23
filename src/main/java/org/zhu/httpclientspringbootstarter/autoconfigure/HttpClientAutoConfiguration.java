package org.zhu.httpclientspringbootstarter.autoconfigure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.zhu.httpclientspringbootstarter.config.HttpClientConfig;

/**
 * http客户端自动配置类
 *
 * @author zhuru
 */
@Configuration
@EnableConfigurationProperties({HttpClientConfig.class})
public class HttpClientAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(ClientHttpRequestFactory.class)
    public ClientHttpRequestFactory requestFactory(HttpClientConfig httpClientConfig) {
        SimpleClientHttpRequestFactory clientHttpRequestFactory = new SimpleClientHttpRequestFactory();
        clientHttpRequestFactory.setConnectTimeout(httpClientConfig.getTimeout());
        clientHttpRequestFactory.setReadTimeout(httpClientConfig.getTimeout());
        return clientHttpRequestFactory;
    }

    @Bean
    @ConditionalOnMissingBean(RestTemplate.class)
    public RestTemplate restTemplate(ClientHttpRequestFactory requestFactory) {
        RestTemplate template = new RestTemplate();
        template.setRequestFactory(requestFactory);
        return template;
    }
}
