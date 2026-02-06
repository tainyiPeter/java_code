package com.wechat.controller;

import com.wechat.dto.JsSdkConfigDTO;
import com.wechat.dto.WechatUserDTO;
import com.wechat.service.WechatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/wechat")
@RequiredArgsConstructor
public class WechatController {

    private final WechatService wechatService;

    /**
     * 验证微信服务器（GET请求）
     * 在微信公众平台配置服务器时调用
     */
    @GetMapping("/callback")
    public String verifyServer(
            @RequestParam("signature") String signature,
            @RequestParam("timestamp") String timestamp,
            @RequestParam("nonce") String nonce,
            @RequestParam("echostr") String echostr) {

        log.info("微信服务器验证: signature={}, timestamp={}, nonce={}",
                signature, timestamp, nonce);

        if (wechatService.checkSignature(signature, timestamp, nonce)) {
            log.info("服务器验证成功");
            return echostr;
        } else {
            log.warn("服务器验证失败");
            return "验证失败";
        }
    }

    /**
     * 接收微信消息（POST请求）
     * 用于接收用户发送的消息
     */
    @PostMapping("/callback")
    public String handleMessage(
            @RequestParam("signature") String signature,
            @RequestParam("timestamp") String timestamp,
            @RequestParam("nonce") String nonce,
            @RequestBody String requestBody) {

        log.info("收到微信消息: {}", requestBody);

        // 验证签名
        if (!wechatService.checkSignature(signature, timestamp, nonce)) {
            return "验证失败";
        }

        // TODO: 处理消息逻辑
        // 这里可以解析XML，根据消息类型进行回复

        return "success";
    }

    /**
     * 获取网页授权URL
     */
    @GetMapping("/oauth/url")
    public ResponseEntity<?> getOAuthUrl(
            @RequestParam("redirectUri") String redirectUri,
            @RequestParam(value = "scope", defaultValue = "snsapi_userinfo") String scope,
            @RequestParam(value = "state", defaultValue = "STATE") String state) {

        try {
            String authUrl = wechatService.generateOAuthUrl(redirectUri, scope, state);

            Map<String, Object> result = new HashMap<>();
            result.put("code", 0);
            result.put("data", authUrl);
            result.put("message", "success");

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("生成授权URL失败", e);
            return ResponseEntity.badRequest().body(
                    Map.of("code", -1, "message", e.getMessage())
            );
        }
    }

    /**
     * 网页授权回调
     */
    @GetMapping("/oauth/callback")
    public ResponseEntity<?> oauthCallback(
            @RequestParam("code") String code,
            @RequestParam(value = "state", required = false) String state) {

        try {
            // 获取access_token和openid
            Map<String, Object> tokenInfo = wechatService.getOAuthAccessToken(code);
            String accessToken = (String) tokenInfo.get("accessToken");
            String openId = (String) tokenInfo.get("openId");

            // 获取用户信息（scope为snsapi_userinfo时）
            WechatUserDTO userInfo = wechatService.getUserInfo(accessToken, openId);

            Map<String, Object> result = new HashMap<>();
            result.put("code", 0);
            result.put("data", Map.of(
                    "userInfo", userInfo,
                    "openId", openId,
                    "accessToken", accessToken,
                    "expiresIn", tokenInfo.get("expiresIn")
            ));
            result.put("message", "授权成功");

            // 这里可以：
            // 1. 将用户信息保存到数据库
            // 2. 创建自己的用户session/token
            // 3. 重定向到前端页面

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("网页授权失败", e);
            return ResponseEntity.badRequest().body(
                    Map.of("code", -1, "message", "授权失败: " + e.getMessage())
            );
        }
    }

    /**
     * 获取JS-SDK配置
     */
    @GetMapping("/js-sdk/config")
    public ResponseEntity<?> getJsSdkConfig(@RequestParam("url") String url) {
        try {
            JsSdkConfigDTO config = wechatService.getJsSdkConfig(url);

            Map<String, Object> result = new HashMap<>();
            result.put("code", 0);
            result.put("data", config);
            result.put("message", "success");

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("获取JS-SDK配置失败", e);
            return ResponseEntity.badRequest().body(
                    Map.of("code", -1, "message", "获取配置失败")
            );
        }
    }

    /**
     * 获取基础access_token（测试用）
     */
    @GetMapping("/access-token")
    public ResponseEntity<?> getAccessToken() {
        try {
            String token = wechatService.getAccessToken();

            Map<String, Object> result = new HashMap<>();
            result.put("code", 0);
            result.put("data", Map.of("access_token", token));
            result.put("message", "success");

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("获取access_token失败", e);
            return ResponseEntity.badRequest().body(
                    Map.of("code", -1, "message", e.getMessage())
            );
        }
    }
}