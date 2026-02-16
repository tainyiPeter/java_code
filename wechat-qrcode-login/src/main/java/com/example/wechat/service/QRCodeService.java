package com.example.wechat.service;

import com.example.wechat.util.QRCodeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class QRCodeService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private QRCodeUtil qrCodeUtil;

    @Value("${qrcode.expire-minutes:5}")
    private int expireMinutes;

    /**
     * 生成二维码并返回登录token
     */
    public String generateLoginToken() {
        String loginToken = UUID.randomUUID().toString().replace("-", "");

        // 存储到Redis，初始状态为pending
        Map<String, String> tokenInfo = new HashMap<>();
        tokenInfo.put("status", "pending");
        tokenInfo.put("createTime", String.valueOf(System.currentTimeMillis()));

        redisTemplate.opsForHash().putAll("qrcode:" + loginToken, tokenInfo);
        redisTemplate.expire("qrcode:" + loginToken, expireMinutes, TimeUnit.MINUTES);

        return loginToken;
    }

    /**
     * 生成二维码图片字节数组
     */
    public byte[] generateQRCodeImage(String loginToken) {
        // 二维码内容：小程序的页面路径，带上loginToken
        String pagePath = "pages/scan/scan";
        String qrContent = String.format("pages/scan/scan?token=%s", loginToken);

        // 如果是微信小程序码，可以使用微信官方接口生成
        // 这里简化处理，使用普通二维码
        return qrCodeUtil.createQRCode(qrContent);
    }

    /**
     * 验证二维码是否有效
     */
    public boolean validateQRCode(String loginToken) {
        String key = "qrcode:" + loginToken;
        Boolean hasKey = redisTemplate.hasKey(key);
        return Boolean.TRUE.equals(hasKey);
    }
}