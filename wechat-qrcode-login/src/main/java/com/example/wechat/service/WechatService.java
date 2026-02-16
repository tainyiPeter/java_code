package com.example.wechat.service;

import com.alibaba.fastjson.JSONObject;
import com.example.wechat.model.ScanCallbackRequest;
import com.example.wechat.util.WechatUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.Map;           // 添加这个 import

@Service
public class WechatService {

    private static final Logger logger = LoggerFactory.getLogger(WechatService.class);

    @Autowired
    private WechatUtil wechatUtil;

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 处理扫码回调
     */
    public String processScanCallback(ScanCallbackRequest request) {
        String loginToken = request.getLoginToken();
        String code = request.getCode();

        // 验证loginToken是否存在
        String key = "qrcode:" + loginToken;
        Boolean hasKey = redisTemplate.hasKey(key);
        if (Boolean.FALSE.equals(hasKey)) {
            throw new RuntimeException("二维码已过期");
        }

        // 使用code换取openid
        JSONObject wechatResult = wechatUtil.getOpenIdByCode(code);
        if (wechatResult.containsKey("errcode") && wechatResult.getInteger("errcode") != 0) {
            logger.error("微信登录失败: {}", wechatResult);
            throw new RuntimeException("微信登录失败: " + wechatResult.getString("errmsg"));
        }

        String openid = wechatResult.getString("openid");
        String sessionKey = wechatResult.getString("session_key");

        logger.info("获取到用户openid: {}, session_key: {}", openid, sessionKey);

        // 生成自定义用户token
        String userToken = UUID.randomUUID().toString();

        // 更新Redis状态
        redisTemplate.opsForHash().put(key, "status", "confirmed");
        redisTemplate.opsForHash().put(key, "openid", openid);
        redisTemplate.opsForHash().put(key, "userToken", userToken);
        redisTemplate.opsForHash().put(key, "sessionKey", sessionKey);

        // 也可以将用户信息存储到单独的key中
        String userKey = "user:" + openid;
        redisTemplate.opsForHash().put(userKey, "openid", openid);
        redisTemplate.opsForHash().put(userKey, "lastLoginTime", String.valueOf(System.currentTimeMillis()));
        redisTemplate.expire(userKey, 30, TimeUnit.DAYS);

        return openid;
    }

    /**
     * 检查二维码状态
     */
    public JSONObject checkQRCodeStatus(String token) {
        String key = "qrcode:" + token;
        Map<Object, Object> tokenInfo = redisTemplate.opsForHash().entries(key);

        JSONObject result = new JSONObject();

        if (tokenInfo.isEmpty()) {
            result.put("status", "expired");
            result.put("message", "二维码已过期");
            return result;
        }

        String status = (String) tokenInfo.get("status");
        result.put("status", status);

        if ("confirmed".equals(status)) {
            result.put("openid", tokenInfo.get("openid"));
            result.put("userToken", tokenInfo.get("userToken"));
        }

        return result;
    }
}