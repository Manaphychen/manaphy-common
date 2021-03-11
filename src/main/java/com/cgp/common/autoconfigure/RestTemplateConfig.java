package com.cgp.common.autoconfigure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * RestTemplate配置类
 *
 * @author Manaphy
 * @date 2020-04-29
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = "manaphy", name = "rest", havingValue = "true", matchIfMissing = true)
public class RestTemplateConfig {

    /**
     * 不设置超时时间的话直接返回new RestTemplate();
     *
     * @param factory 工厂
     * @return {@link RestTemplate}
     */
    @Bean
    public RestTemplate restTemplate(ClientHttpRequestFactory factory) {
        return new RestTemplate(factory);
    }

    /**
     * 简单的客户端http请求工厂
     * 设置超时时间
     *
     * @return {@link ClientHttpRequestFactory}
     */
    @Bean
    public ClientHttpRequestFactory simpleClientHttpRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setReadTimeout(5000);//单位为ms
        factory.setConnectTimeout(5000);//单位为ms
        return factory;
    }
}
