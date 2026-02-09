package com.wechat.config;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate(httpRequestFactory());

        // 获取当前所有的消息转换器
        List<org.springframework.http.converter.HttpMessageConverter<?>> converters =
                new ArrayList<>();

        // 1. 添加 UTF-8 编码的字符串转换器
        converters.add(new StringHttpMessageConverter(StandardCharsets.UTF_8));

        // 2. 添加 Jackson JSON 转换器
        MappingJackson2HttpMessageConverter jacksonConverter =
                new MappingJackson2HttpMessageConverter();
        jacksonConverter.setDefaultCharset(StandardCharsets.UTF_8);
        converters.add(jacksonConverter);

        // 3. 添加其他转换器
        converters.add(new org.springframework.http.converter.FormHttpMessageConverter());
        converters.add(new org.springframework.http.converter.ByteArrayHttpMessageConverter());

        // 设置消息转换器
        restTemplate.setMessageConverters(converters);

        return restTemplate;
    }

    private ClientHttpRequestFactory httpRequestFactory() {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectionRequestTimeout(5000);
        factory.setConnectTimeout(5000);
        factory.setReadTimeout(15000);

        HttpClient httpClient = HttpClientBuilder.create()
                .setMaxConnTotal(50)
                .setMaxConnPerRoute(20)
                .build();
        factory.setHttpClient(httpClient);

        return factory;
    }
}