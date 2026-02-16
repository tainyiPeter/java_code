package com.example.wechat.service;

import com.alibaba.fastjson.JSONObject;
import com.example.wechat.model.ScanCallbackRequest;
import com.example.wechat.util.WechatUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class WechatService {

    private static final Logger logger = LoggerFactory.getLogger(WechatService.class);

    @Autowired
    private WechatUtil wechatUtil;

    @Autowired
    private QRCodeService qrCodeService;  // 注入内存版的 QRCodeService

    /**
     * 处理扫码回调
     */
    public String processScanCallback(ScanCallbackRequest request) {
        String loginToken = request.getLoginToken();
        String code = request.getCode();

        // 从内存中获取 token 信息
        Map<String, Object> tokenInfo = qrCodeService.getTokenInfo(loginToken);
        if (tokenInfo == null) {
            throw new RuntimeException("二维码已过期或不存在");
        }

        // 使用 code 换取 openid
        JSONObject wechatResult = wechatUtil.getOpenIdByCode(code);
        if (wechatResult.containsKey("errcode") && wechatResult.getInteger("errcode") != 0) {
            logger.error("微信登录失败: {}", wechatResult);
            throw new RuntimeException("微信登录失败: " + wechatResult.getString("errmsg"));
        }

        String openid = wechatResult.getString("openid");
        String sessionKey = wechatResult.getString("session_key");
        logger.info("获取到用户 openid: {}", openid);

        // 生成自定义用户 token
        String userToken = UUID.randomUUID().toString();

        // 更新内存中的状态
        tokenInfo.put("status", "confirmed");
        tokenInfo.put("openid", openid);
        tokenInfo.put("userToken", userToken);
        tokenInfo.put("sessionKey", sessionKey);
        qrCodeService.updateTokenInfo(loginToken, tokenInfo);

        return openid;
    }

    /**
     * 检查二维码状态
     */
    public JSONObject checkQRCodeStatus(String token) {
        Map<String, Object> tokenInfo = qrCodeService.getTokenInfo(token);

        JSONObject result = new JSONObject();
        if (tokenInfo == null) {
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