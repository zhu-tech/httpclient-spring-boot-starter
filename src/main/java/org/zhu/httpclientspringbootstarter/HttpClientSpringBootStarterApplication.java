package org.zhu.httpclientspringbootstarter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.zhu.httpclientspringbootstarter.annotation.EnableHttpClient;

@EnableHttpClient(basePackage = "org.zhu.httpclientspringbootstarter.test")
@SpringBootApplication
public class HttpClientSpringBootStarterApplication {

    public static void main(String[] args) {
        SpringApplication.run(HttpClientSpringBootStarterApplication.class, args);
    }

}
