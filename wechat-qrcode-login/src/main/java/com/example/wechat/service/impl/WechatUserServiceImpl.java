package com.example.wechat.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.example.wechat.config.WechatConfig;
import com.example.wechat.model.*;
import com.example.wechat.service.QRCodeService;
import com.example.wechat.service.WechatUserService;
import com.example.wechat.util.WXBizDataCrypt;
import com.example.wechat.util.WechatUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class WechatUserServiceImpl implements WechatUserService {

    private static final Logger logger = LoggerFactory.getLogger(WechatUserServiceImpl.class);

    @Autowired
    private WechatConfig wechatConfig;

    @Autowired
    private WechatUtil wechatUtil;

    @Autowired
    private WXBizDataCrypt wxBizDataCrypt;

    @Autowired(required = false)
    private StringRedisTemplate redisTemplate;

    @Autowired
    private QRCodeService qrCodeService;  // 复用现有的内存存储服务

    @Value("${wechat.mini.session-expire-days:7}")
    private int sessionExpireDays;

    // 内存存储（用于没有Redis的场景）
    private final Map<String, Map<String, Object>> sessionStore = new HashMap<>();
    private final Map<String, String> tokenStore = new HashMap<>();

    @Override
    public LoginResponse login(LoginRequest request) {
        logger.info("收到登录请求: code={}, scanId={}, scanType={}",
                request.getCode(), request.getScanId(), request.getScanType());

        // 1. 参数校验
        if (request.getCode() == null || request.getCode().isEmpty()) {
            return LoginResponse.builder()
                    .success(false)
                    .message("code不能为空")
                    .build();
        }

        // 2. 调用微信接口换取openid和session_key
        JSONObject wxResult = wechatUtil.getOpenIdByCode(request.getCode());

        // 3. 检查微信接口是否返回错误
        if (wxResult.containsKey("errcode") && wxResult.getInteger("errcode") != 0) {
            int errcode = wxResult.getInteger("errcode");
            String errmsg = wxResult.getString("errmsg");
            logger.error("微信登录失败: errcode={}, errmsg={}", errcode, errmsg);

            // 针对不同错误码给出友好提示
            String friendlyMsg = getFriendlyErrorMessage(errcode, errmsg);

            return LoginResponse.builder()
                    .success(false)
                    .message(friendlyMsg)
                    .errcode(errcode)
                    .build();
        }

        // 4. 获取openid和session_key
        String openid = wxResult.getString("openid");
        String sessionKey = wxResult.getString("session_key");
        String unionid = wxResult.getString("unionid");

        if (openid == null || sessionKey == null) {
            logger.error("微信返回数据不完整: {}", wxResult);
            return LoginResponse.builder()
                    .success(false)
                    .message("微信返回数据异常")
                    .build();
        }

        logger.info("微信登录成功: openid={}", openid);

        // 5. 保存session_key并生成token
        String token = saveUserSession(openid, sessionKey);

        // 6. 处理扫码业务逻辑（如果有）
        if (request.getScanId() != null) {
            handleScanBusiness(openid, request.getScanId(), request.getScanType());
        }

        // 7. 构建返回结果
        LoginResponse.Body body = LoginResponse.Body.builder()
                .openId(openid)
                .token(token)
                .scanId(request.getScanId())
                .scanType(request.getScanType())
                .build();

        // 开发环境返回更多调试信息
        if (logger.isDebugEnabled()) {
            body.setSessionKey(sessionKey);
            if (unionid != null) {
                body.setUnionId(unionid);
            }
        }

        return LoginResponse.builder()
                .success(true)
                .message("登录成功")
                .body(body)
                .build();
    }

    @Override
    public DecryptPhoneResponse decryptPhone(DecryptPhoneRequest request) {
        logger.info("收到解密请求: openid={}, scanId={}", request.getOpenId(), request.getScanId());

        try {
            // 1. 参数校验
            if (request.getEncryptedData() == null || request.getIv() == null) {
                return DecryptPhoneResponse.builder()
                        .success(false)
                        .message("加密数据不能为空")
                        .build();
            }

            if (request.getOpenId() == null) {
                return DecryptPhoneResponse.builder()
                        .success(false)
                        .message("openId不能为空")
                        .build();
            }

            // 2. 获取session_key
            String sessionKey = getUserSessionKey(request.getOpenId(), request.getToken());

            if (sessionKey == null) {
                logger.error("未找到session_key: openid={}", request.getOpenId());
                return DecryptPhoneResponse.builder()
                        .success(false)
                        .message("会话已过期，请重新登录")
                        .build();
            }

            logger.info("获取到session_key: {}...", sessionKey.substring(0, 10));

            // 3. 解密手机号
            JSONObject decryptedData = wxBizDataCrypt.decryptPhoneNumber(
                    sessionKey,
                    request.getEncryptedData(),
                    request.getIv(),
                    wechatConfig.getAppId()
            );

            if (decryptedData == null) {
                return DecryptPhoneResponse.builder()
                        .success(false)
                        .message("解密失败，请重试")
                        .build();
            }

            // 4. 提取手机号信息
            String phoneNumber = decryptedData.getString("phoneNumber");
            String purePhoneNumber = decryptedData.getString("purePhoneNumber");
            String countryCode = decryptedData.getString("countryCode");

            if (phoneNumber == null) {
                return DecryptPhoneResponse.builder()
                        .success(false)
                        .message("解密结果中不包含手机号")
                        .build();
            }

            // 5. 更新用户手机号
            updateUserPhone(request.getOpenId(), phoneNumber);

            // 6. 处理扫码业务（如果需要）
            if (request.getScanId() != null) {
                handleScanBusinessWithPhone(request.getOpenId(), request.getScanId(),
                        request.getScanType(), phoneNumber);
            }

            logger.info("手机号解密成功: openid={}, phone={}", request.getOpenId(), phoneNumber);

            // 7. 返回成功响应
            return DecryptPhoneResponse.builder()
                    .success(true)
                    .message("解密成功")
                    .body(DecryptPhoneResponse.Body.builder()
                            .phoneNumber(phoneNumber)
                            .purePhoneNumber(purePhoneNumber != null ? purePhoneNumber : phoneNumber)
                            .countryCode(countryCode != null ? countryCode : "86")
                            .openId(request.getOpenId())
                            .scanId(request.getScanId())
                            .scanType(request.getScanType())
                            .build())
                    .build();

        } catch (Exception e) {
            logger.error("解密处理异常", e);
            return DecryptPhoneResponse.builder()
                    .success(false)
                    .message("系统异常: " + e.getMessage())
                    .build();
        }
    }

    /**
     * 保存用户会话信息
     */
    private String saveUserSession(String openid, String sessionKey) {
        String token;

        if (redisTemplate != null) {
            // 使用Redis存储
            token = UUID.randomUUID().toString().replace("-", "");

            // 存储token到openid的映射
            redisTemplate.opsForValue().set("token:" + token, openid,
                    sessionExpireDays, TimeUnit.DAYS);

            // 存储session_key
            Map<String, String> sessionMap = new HashMap<>();
            sessionMap.put("sessionKey", sessionKey);
            sessionMap.put("openid", openid);
            redisTemplate.opsForHash().putAll("session:" + openid, sessionMap);
            redisTemplate.expire("session:" + openid, sessionExpireDays, TimeUnit.DAYS);

        } else {
            // 使用内存存储
            token = generateToken(openid, sessionKey);

            Map<String, Object> sessionInfo = new HashMap<>();
            sessionInfo.put("sessionKey", sessionKey);
            sessionInfo.put("openid", openid);
            sessionInfo.put("createTime", System.currentTimeMillis());

            sessionStore.put(openid, sessionInfo);
            tokenStore.put(token, openid);
        }

        return token;
    }

    /**
     * 获取用户session_key
     */
    private String getUserSessionKey(String openid, String token) {
        if (redisTemplate != null) {
            // 从Redis获取
            String storedOpenid = null;

            // 优先使用token获取
            if (token != null) {
                storedOpenid = redisTemplate.opsForValue().get("token:" + token);
            }

            // 如果token无效，直接使用openid
            if (storedOpenid == null) {
                storedOpenid = openid;
            }

            Object sessionKey = redisTemplate.opsForHash().get("session:" + storedOpenid, "sessionKey");
            return sessionKey != null ? sessionKey.toString() : null;

        } else {
            // 从内存获取
            String storedOpenid = tokenStore.get(token);
            if (storedOpenid == null) {
                storedOpenid = openid;
            }

            Map<String, Object> sessionInfo = sessionStore.get(storedOpenid);
            return sessionInfo != null ? (String) sessionInfo.get("sessionKey") : null;
        }
    }

    /**
     * 更新用户手机号
     */
    private void updateUserPhone(String openid, String phone) {
        if (redisTemplate != null) {
            redisTemplate.opsForHash().put("user:" + openid, "phone", phone);
            redisTemplate.expire("user:" + openid, sessionExpireDays, TimeUnit.DAYS);
        } else {
            // 这里可以调用现有的UserModel逻辑
            // 或者暂时不实现，由业务方处理
        }
    }

    /**
     * 生成内存版token
     */
    private String generateToken(String openid, String sessionKey) {
        try {
            String raw = openid + ":" + sessionKey + ":" + System.currentTimeMillis();
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(raw.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            return UUID.randomUUID().toString().replace("-", "");
        }
    }

    /**
     * 处理扫码业务
     */
    private void handleScanBusiness(String openid, String scanId, String scanType) {
        logger.info("处理扫码业务: scanId={}, scanType={}, openid={}", scanId, scanType, openid);

        // 使用现有的QRCodeService更新状态
        Map<String, Object> tokenInfo = qrCodeService.getTokenInfo(scanId);
        if (tokenInfo != null) {
            tokenInfo.put("openid", openid);
            tokenInfo.put("status", "confirmed");
            qrCodeService.updateTokenInfo(scanId, tokenInfo);
        }
    }

    /**
     * 处理带手机号的扫码业务
     */
    private void handleScanBusinessWithPhone(String openid, String scanId, String scanType, String phone) {
        logger.info("处理扫码业务(带手机号): scanId={}, scanType={}, openid={}, phone={}",
                scanId, scanType, openid, phone);

        Map<String, Object> tokenInfo = qrCodeService.getTokenInfo(scanId);
        if (tokenInfo != null) {
            tokenInfo.put("openid", openid);
            tokenInfo.put("phone", phone);
            tokenInfo.put("status", "completed");
            qrCodeService.updateTokenInfo(scanId, tokenInfo);
        }
    }

    /**
     * 获取友好的错误提示
     */
    private String getFriendlyErrorMessage(int errcode, String errmsg) {
        switch (errcode) {
            case 40029:
                return "登录凭证无效，请重试";
            case 45011:
                return "操作太频繁，请稍后重试";
            case 40013:
                return "AppID无效，请联系管理员";
            case 40125:
                return "AppSecret无效，请联系管理员";
            case -1:
                return "微信服务繁忙，请稍后重试";
            default:
                return errmsg;
        }
    }
}