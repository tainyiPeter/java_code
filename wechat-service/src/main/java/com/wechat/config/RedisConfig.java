package com.wechat.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * 简化的Redis配置
 */
@Configuration
@ConditionalOnProperty(name = "spring.redis.enabled", havingValue = "true")
public class RedisConfig {

    // Spring Boot会自动配置RedisTemplate和StringRedisTemplate
    // 只需要在application.yml中配置Redis连接信息即可
}