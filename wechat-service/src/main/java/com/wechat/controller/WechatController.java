package com.wechat.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wechat.dto.AccessTokenResponse;
import com.wechat.dto.WechatUserDTO;
import com.wechat.util.HttpUtil;
import com.wechat.util.SignUtil;
import com.wechat.util.WechatUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/wechat")
@RequiredArgsConstructor
public class WechatController {

    private final HttpUtil httpUtil;
    private final WechatUtil wechatUtil;

    @Value("${wechat.app-id:wx54d7f0c5bd8d51d1}")
    private String appId;

    @Value("${wechat.app-secret:}")
    private String appSecret;

    @Value("${wechat.token:}")
    private String wechatToken;

    // 微信API地址常量
    private static final String ACCESS_TOKEN_URL = "https://api.weixin.qq.com/sns/oauth2/access_token";
    private static final String USER_INFO_URL = "https://api.weixin.qq.com/sns/userinfo";
    private static final String AUTH_URL = "https://open.weixin.qq.com/connect/oauth2/authorize";

    /**
     * 验证微信服务器（GET请求）
     * 在微信公众平台配置服务器时调用
     */
    @GetMapping("/verify")
    public String verifyServer(
            @RequestParam("signature") String signature,
            @RequestParam("timestamp") String timestamp,
            @RequestParam("nonce") String nonce,
            @RequestParam("echostr") String echostr) {

        log.info("微信服务器验证: signature={}, timestamp={}, nonce={}", signature, timestamp, nonce);

        if (SignUtil.checkSignature(wechatToken, timestamp, nonce, signature)) {
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
    @PostMapping("/verify")
    public String handleMessage(
            @RequestParam("signature") String signature,
            @RequestParam("timestamp") String timestamp,
            @RequestParam("nonce") String nonce,
            @RequestBody String requestBody) {

        log.info("收到微信消息: {}", requestBody);

        // 验证签名
        if (!SignUtil.checkSignature(wechatToken, timestamp, nonce, signature)) {
            return "验证失败";
        }

        // 使用WechatUtil处理消息
        String response = wechatUtil.handleMessage(requestBody);
        return response;
    }

    /**
     * 获取网页授权URL
     */
    @GetMapping("/auth-url")
    public ResponseEntity<?> getAuthUrl(
            @RequestParam(value = "redirectUri", required = false) String redirectUri,
            @RequestParam(value = "scope", defaultValue = "snsapi_userinfo") String scope,
            @RequestParam(value = "state", defaultValue = "STATE") String state) {

        try {
            // 如果没有传入redirectUri，使用默认值
            if (redirectUri == null || redirectUri.isEmpty()) {
                redirectUri = "https://91qj1470uc04.vicp.fun/wechat/auth-callback";
            }

            // 构建授权URL
            String encodedRedirectUri = java.net.URLEncoder.encode(redirectUri, "UTF-8");
            String authUrl = String.format("%s?appid=%s&redirect_uri=%s&response_type=code&scope=%s&state=%s#wechat_redirect",
                    AUTH_URL, appId, encodedRedirectUri, scope, state);

            Map<String, Object> result = new HashMap<>();
            result.put("code", 0);
            result.put("data", Map.of("authUrl", authUrl));
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
     * 网页授权回调接口 - 修正版
     */
    @GetMapping("/auth-callback")
    public ResponseEntity<?> oauthCallback(
            @RequestParam("code") String code,
            @RequestParam(value = "state", required = false) String state) {

        try {
            log.info("收到微信回调！code={}, state={}", code, state);

            // 1. 获取access_token
            String accessTokenUrl = String.format("%s?appid=%s&secret=%s&code=%s&grant_type=authorization_code",
                    ACCESS_TOKEN_URL, appId, appSecret, code);

            String response = httpUtil.doGet(accessTokenUrl);
            JSONObject tokenJson = JSON.parseObject(response);

            // 检查错误
            if (tokenJson.containsKey("errcode")) {
                log.error("获取access_token失败: {}", tokenJson);
                return ResponseEntity.badRequest().body(
                        Map.of("code", -1, "message", "授权失败: " + tokenJson.getString("errmsg"))
                );
            }

            String accessToken = tokenJson.getString("access_token");
            String openId = tokenJson.getString("openid");
            String refreshToken = tokenJson.getString("refresh_token");
            Integer expiresIn = tokenJson.getInteger("expires_in");
            String scope = tokenJson.getString("scope");

            // 2. 获取用户信息（如果scope是snsapi_userinfo）
            WechatUserDTO userInfo = null;
            if ("snsapi_userinfo".equals(scope)) {
                userInfo = getUserInfo(accessToken, openId);
            }

            // 3. 构建Token响应 - 使用 AccessTokenResponse
            AccessTokenResponse tokenResponse = new AccessTokenResponse();
            tokenResponse.setAccessToken(accessToken);
            tokenResponse.setExpiresIn(expiresIn);
            tokenResponse.setRefreshToken(refreshToken);
            tokenResponse.setOpenid(openId);
            tokenResponse.setScope(scope);
            tokenResponse.setUnionId(tokenJson.getString("unionid"));
            tokenResponse.setIsSnapshotUser(tokenJson.getInteger("is_snapshotuser"));

            Map<String, Object> result = new HashMap<>();
            result.put("code", 0);
            result.put("data", Map.of(
                    "tokenInfo", tokenResponse,
                    "userInfo", userInfo
            ));
            result.put("message", "授权成功");

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("网页授权失败", e);
            return ResponseEntity.badRequest().body(
                    Map.of("code", -1, "message", "授权失败: " + e.getMessage())
            );
        }
    }

    /**
     * 完善后的login接口 - 修正版
     */
    @GetMapping("/login")
    public ResponseEntity<?> login(
            @RequestParam("code") String code,
            @RequestParam(value = "state", required = false) String state) {

        log.info("微信授权登录 - code: {}, state: {}", code, state);

        try {
            // 1. 构建获取access_token的URL
            String accessTokenUrl = String.format("%s?appid=%s&secret=%s&code=%s&grant_type=authorization_code",
                    ACCESS_TOKEN_URL, appId, appSecret, code);

            log.info("请求access_token URL: {}", accessTokenUrl);

            // 2. 调用微信API
            String response = httpUtil.doGet(accessTokenUrl);
            JSONObject tokenJson = JSON.parseObject(response);

            // 3. 检查响应
            if (tokenJson.containsKey("errcode")) {
                int errCode = tokenJson.getIntValue("errcode");
                String errMsg = tokenJson.getString("errmsg");
                log.error("获取access_token失败: errcode={}, errmsg={}", errCode, errMsg);
                return ResponseEntity.badRequest().body(
                        Map.of("code", errCode, "message", "微信授权失败: " + errMsg)
                );
            }

            // 4. 构建返回对象
            AccessTokenResponse tokenResponse = new AccessTokenResponse();
            tokenResponse.setAccessToken(tokenJson.getString("access_token"));
            tokenResponse.setExpiresIn(tokenJson.getInteger("expires_in"));
            tokenResponse.setRefreshToken(tokenJson.getString("refresh_token"));
            tokenResponse.setOpenid(tokenJson.getString("openid"));
            tokenResponse.setScope(tokenJson.getString("scope"));
            tokenResponse.setIsSnapshotUser(tokenJson.getInteger("is_snapshotuser"));
            tokenResponse.setUnionId(tokenJson.getString("unionid"));

            log.info("获取access_token成功: openid={}, unionid={}",
                    tokenResponse.getOpenid(), tokenResponse.getUnionId());

            // 5. 可选：获取用户详细信息
            if ("snsapi_userinfo".equals(tokenResponse.getScope())) {
                // 使用 WechatUserDTO，不是 UserInfoResponse
                WechatUserDTO userInfo = getUserInfo(tokenResponse.getAccessToken(), tokenResponse.getOpenid());
                if (userInfo != null) {
                    log.info("用户信息: nickname={}, city={}, province={}",
                            userInfo.getNickname(), userInfo.getCity(), userInfo.getProvince());
                    // 可以将userInfo也返回
                    Map<String, Object> data = new HashMap<>();
                    data.put("tokenInfo", tokenResponse);
                    data.put("userInfo", userInfo);
                    return ResponseEntity.ok(Map.of("code", 0, "data", data, "message", "success"));
                }
            }

            return ResponseEntity.ok(Map.of("code", 0, "data", tokenResponse, "message", "success"));
        } catch (Exception e) {
            log.error("登录接口异常", e);
            return ResponseEntity.internalServerError().body(
                    Map.of("code", -1, "message", "系统异常: " + e.getMessage())
            );
        }
    }

    /**
     * 获取用户详细信息 - 修正版
     */
    private WechatUserDTO getUserInfo(String accessToken, String openid) {
        try {
            String userInfoUrl = String.format("%s?access_token=%s&openid=%s&lang=zh_CN",
                    USER_INFO_URL, accessToken, openid);

            String response = httpUtil.doGet(userInfoUrl);
            JSONObject userJson = JSON.parseObject(response);

            if (userJson.containsKey("errcode")) {
                log.warn("获取用户信息失败: errcode={}, errmsg={}",
                        userJson.getInteger("errcode"), userJson.getString("errmsg"));
                return null;
            }

            WechatUserDTO userInfo = new WechatUserDTO();
            // 注意：根据你的 WechatUserDTO，应该是 setOpenId 而不是 setOpenid
            userInfo.setOpenId(userJson.getString("openid"));
            userInfo.setNickname(userJson.getString("nickname"));
            userInfo.setGender(userJson.getInteger("sex"));  // 注意：是 setGender 而不是 setSex
            userInfo.setProvince(userJson.getString("province"));
            userInfo.setCity(userJson.getString("city"));
            userInfo.setCountry(userJson.getString("country"));
            userInfo.setHeadImgUrl(userJson.getString("headimgurl"));
            userInfo.setUnionId(userJson.getString("unionid"));
            // 设置错误信息字段
            userInfo.setErrCode(userJson.getInteger("errcode"));
            userInfo.setErrMsg(userJson.getString("errmsg"));

            return userInfo;
        } catch (Exception e) {
            log.error("获取用户信息异常", e);
            return null;
        }
    }

    /**
     * 刷新access_token - 修正版
     */
    @GetMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestParam("refresh_token") String refreshToken) {
        try {
            String refreshUrl = String.format("https://api.weixin.qq.com/sns/oauth2/refresh_token?" +
                            "appid=%s&grant_type=refresh_token&refresh_token=%s",
                    appId, refreshToken);

            String response = httpUtil.doGet(refreshUrl);
            JSONObject tokenJson = JSON.parseObject(response);

            if (tokenJson.containsKey("errcode")) {
                return ResponseEntity.badRequest().body(
                        Map.of("code", tokenJson.getIntValue("errcode"),
                                "message", tokenJson.getString("errmsg"))
                );
            }

            AccessTokenResponse tokenResponse = new AccessTokenResponse();
            tokenResponse.setAccessToken(tokenJson.getString("access_token"));
            tokenResponse.setExpiresIn(tokenJson.getInteger("expires_in"));
            tokenResponse.setRefreshToken(tokenJson.getString("refresh_token"));
            tokenResponse.setOpenid(tokenJson.getString("openid"));
            tokenResponse.setScope(tokenJson.getString("scope"));
            tokenResponse.setUnionId(tokenJson.getString("unionid"));
            tokenResponse.setIsSnapshotUser(tokenJson.getInteger("is_snapshotuser"));

            return ResponseEntity.ok(Map.of("code", 0, "data", tokenResponse, "message", "success"));
        } catch (Exception e) {
            log.error("刷新token异常", e);
            return ResponseEntity.internalServerError().body(
                    Map.of("code", -1, "message", "系统异常")
            );
        }
    }

    /**
     * 检查access_token是否有效
     */
    @GetMapping("/check")
    public ResponseEntity<?> checkToken(
            @RequestParam("access_token") String accessToken,
            @RequestParam("openid") String openid) {

        try {
            String checkUrl = String.format("https://api.weixin.qq.com/sns/auth?access_token=%s&openid=%s",
                    accessToken, openid);

            String response = httpUtil.doGet(checkUrl);
            JSONObject result = JSON.parseObject(response);

            if (result.getIntValue("errcode") == 0) {
                return ResponseEntity.ok(Map.of("code", 0, "data", "有效", "message", "success"));
            } else {
                return ResponseEntity.ok(Map.of("code", 0, "data", "无效", "message", result.getString("errmsg")));
            }
        } catch (Exception e) {
            log.error("检查token异常", e);
            return ResponseEntity.internalServerError().body(
                    Map.of("code", -1, "message", "系统异常")
            );
        }
    }

    /**
     * 获取JS-SDK配置（示例，需要基础access_token）
     */
    @GetMapping("/js-sdk/config")
    public ResponseEntity<?> getJsSdkConfig(@RequestParam("url") String url) {
        try {
            // 注意：这里需要基础access_token，不是网页授权的access_token
            // 需要先获取基础access_token（可以通过定时任务获取并缓存）
            String jsapiTicket = ""; // 需要从缓存或重新获取

            long timestamp = System.currentTimeMillis() / 1000;
            String nonceStr = "random123";
            String signature = SignUtil.generateJsSdkSignature(jsapiTicket, nonceStr, timestamp, url);

            Map<String, Object> config = new HashMap<>();
            config.put("appId", appId);
            config.put("timestamp", timestamp);
            config.put("nonceStr", nonceStr);
            config.put("signature", signature);

            return ResponseEntity.ok(Map.of("code", 0, "data", config, "message", "success"));
        } catch (Exception e) {
            log.error("获取JS-SDK配置失败", e);
            return ResponseEntity.badRequest().body(
                    Map.of("code", -1, "message", "获取配置失败")
            );
        }
    }

    /**
     * 获取基础access_token（测试用，实际应该定时获取并缓存）
     */
    @GetMapping("/access-token")
    public ResponseEntity<?> getAccessToken() {
        try {
            String url = String.format("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s",
                    appId, appSecret);

            String response = httpUtil.doGet(url);
            JSONObject result = JSON.parseObject(response);

            if (result.containsKey("access_token")) {
                return ResponseEntity.ok(Map.of("code", 0,
                        "data", Map.of("access_token", result.getString("access_token")),
                        "message", "success"));
            } else {
                return ResponseEntity.badRequest().body(
                        Map.of("code", -1, "message", result.getString("errmsg"))
                );
            }
        } catch (Exception e) {
            log.error("获取access_token失败", e);
            return ResponseEntity.internalServerError().body(
                    Map.of("code", -1, "message", "系统异常")
            );
        }
    }
}