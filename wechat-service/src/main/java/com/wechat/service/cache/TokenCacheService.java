package com.wechat.service.cache;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

@Service
public class TokenCacheService {

    private final StringRedisTemplate redisTemplate;

    // Redis key
    private static final String ACCESS_TOKEN_KEY = "wechat:access_token";
    private static final String JSAPI_TICKET_KEY = "wechat:jsapi_ticket";

    public TokenCacheService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 获取缓存的access_token
     */
    public String getAccessToken() {
        return redisTemplate.opsForValue().get(ACCESS_TOKEN_KEY);
    }

    /**
     * 缓存access_token
     */
    public void cacheAccessToken(String accessToken, int expiresIn) {
        if (StringUtils.hasText(accessToken)) {
            redisTemplate.opsForValue().set(
                    ACCESS_TOKEN_KEY,
                    accessToken,
                    expiresIn,
                    TimeUnit.SECONDS
            );
        }
    }

    /**
     * 获取缓存的jsapi_ticket
     */
    public String getJsapiTicket() {
        return redisTemplate.opsForValue().get(JSAPI_TICKET_KEY);
    }

    /**
     * 缓存jsapi_ticket
     */
    public void cacheJsapiTicket(String ticket, int expiresIn) {
        if (StringUtils.hasText(ticket)) {
            redisTemplate.opsForValue().set(
                    JSAPI_TICKET_KEY,
                    ticket,
                    expiresIn,
                    TimeUnit.SECONDS
            );
        }
    }

    /**
     * 清除所有缓存
     */
    public void clearCache() {
        redisTemplate.delete(ACCESS_TOKEN_KEY);
        redisTemplate.delete(JSAPI_TICKET_KEY);
    }
}