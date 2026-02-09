package com.wechat.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wechat.dto.JsSdkConfigDTO;
import com.wechat.dto.WechatUserDTO;
import com.wechat.properties.WechatProperties;
import com.wechat.service.WechatService;
import com.wechat.service.cache.TokenCacheService;
import com.wechat.util.HttpUtil;
import com.wechat.util.SignUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class WechatServiceImpl implements WechatService {

    private final WechatProperties wechatProperties;
    private final TokenCacheService tokenCacheService;
    private final RestTemplate restTemplate;
    private final HttpUtil httpUtil;

    @Override
    public boolean checkSignature(String signature, String timestamp, String nonce) {
        String token = wechatProperties.getToken();
        return SignUtil.checkSignature(token, timestamp, nonce, signature);
    }

    @Override
    public String getAccessToken() {
        // 先从缓存获取
        String cachedToken = tokenCacheService.getAccessToken();
        if (StringUtils.hasText(cachedToken)) {
            return cachedToken;
        }

        // 缓存没有，从微信服务器获取
        String appId = wechatProperties.getAppId();
        String appSecret = wechatProperties.getAppSecret();
        String url = wechatProperties.getApi().getAccessToken()
                + "?grant_type=client_credential&appid=" + appId + "&secret=" + appSecret;

        try {
            String response = restTemplate.getForObject(url, String.class);
            JSONObject json = JSON.parseObject(response);

            if (json.containsKey("access_token")) {
                String accessToken = json.getString("access_token");
                int expiresIn = json.getIntValue("expires_in");

                // 缓存token，提前5分钟过期
                tokenCacheService.cacheAccessToken(accessToken, expiresIn - 300);

                log.info("获取access_token成功: {}", accessToken.substring(0, 20) + "...");
                return accessToken;
            } else {
                log.error("获取access_token失败: {}", response);
                throw new RuntimeException("获取access_token失败: " + json.getString("errmsg"));
            }
        } catch (Exception e) {
            log.error("调用微信接口失败", e);
            throw new RuntimeException("获取access_token异常", e);
        }
    }

    @Override
    public Map<String, Object> getOAuthAccessToken(String code) {
        if (!StringUtils.hasText(code)) {
            throw new IllegalArgumentException("授权码code不能为空");
        }

        String appId = wechatProperties.getAppId();
        String appSecret = wechatProperties.getAppSecret();
        String url = wechatProperties.getApi().getOauthAccessToken()
                + "?appid=" + appId + "&secret=" + appSecret
                + "&code=" + code + "&grant_type=authorization_code";

        log.info("getOAuthAccessToken, url: {}", url);
        try {
            String response = restTemplate.getForObject(url, String.class);
            JSONObject json = JSON.parseObject(response);

            if (json.containsKey("errcode")) {
                log.error("获取网页授权token失败: {}", response);
                throw new RuntimeException("授权失败: " + json.getString("errmsg"));
            }

            Map<String, Object> result = new HashMap<>();
            result.put("accessToken", json.getString("access_token"));
            result.put("openId", json.getString("openid"));
            result.put("expiresIn", json.getIntValue("expires_in"));
            result.put("scope", json.getString("scope"));
            result.put("refreshToken", json.getString("refresh_token"));

            log.info("获取网页授权token成功, openid: {}", json.getString("openid"));
            return result;
        } catch (Exception e) {
            log.error("调用微信网页授权接口失败", e);
            throw new RuntimeException("网页授权失败", e);
        }
    }

    @Override
    public WechatUserDTO getUserInfo(String accessToken, String openId) {
        String url = wechatProperties.getApi().getUserInfo()
                + "?access_token=" + accessToken + "&openid=" + openId + "&lang=zh_CN";

        try {
            String response = restTemplate.getForObject(url, String.class);
            JSONObject json = JSON.parseObject(response);

            if (json.containsKey("errcode")) {
                log.error("获取用户信息失败: {}", response);
                throw new RuntimeException("获取用户信息失败: " + json.getString("errmsg"));
            }

            WechatUserDTO user = new WechatUserDTO();
            user.setOpenId(json.getString("openid"));
            user.setNickname(json.getString("nickname"));
            user.setGender(json.getIntValue("sex"));
            user.setProvince(json.getString("province"));
            user.setCity(json.getString("city"));
            user.setCountry(json.getString("country"));
            user.setHeadImgUrl(json.getString("headimgurl"));
            user.setPrivilege(JSON.parseArray(json.getString("privilege"), String.class));
            user.setUnionId(json.getString("unionid"));

            log.info("获取用户信息成功, nickname: {}", user.getNickname());
            return user;
        } catch (Exception e) {
            log.error("获取用户信息失败", e);
            throw new RuntimeException("获取用户信息失败", e);
        }
    }

    @Override
    public String generateOAuthUrl(String redirectUri, String scope, String state) {
        String appId = wechatProperties.getAppId();
        String encodedUri = java.net.URLEncoder.encode(redirectUri);

        return wechatProperties.getApi().getOauthRedirect()
                + "?appid=" + appId
                + "&redirect_uri=" + encodedUri
                + "&response_type=code"
                + "&scope=" + scope
                + "&state=" + state
                + "#wechat_redirect";
    }

    @Override
    public String getJsapiTicket() {
        // 先从缓存获取
        String cachedTicket = tokenCacheService.getJsapiTicket();
        if (StringUtils.hasText(cachedTicket)) {
            return cachedTicket;
        }

        // 缓存没有，从微信服务器获取
        String accessToken = getAccessToken();
        String url = wechatProperties.getApi().getJsapiTicket()
                + "?access_token=" + accessToken + "&type=jsapi";

        try {
            String response = restTemplate.getForObject(url, String.class);
            JSONObject json = JSON.parseObject(response);

            if (json.getIntValue("errcode") == 0) {
                String ticket = json.getString("ticket");
                int expiresIn = json.getIntValue("expires_in");

                // 缓存ticket
                tokenCacheService.cacheJsapiTicket(ticket, expiresIn - 300);

                log.info("获取jsapi_ticket成功");
                return ticket;
            } else {
                log.error("获取jsapi_ticket失败: {}", response);
                throw new RuntimeException("获取jsapi_ticket失败: " + json.getString("errmsg"));
            }
        } catch (Exception e) {
            log.error("获取jsapi_ticket异常", e);
            throw new RuntimeException("获取jsapi_ticket失败", e);
        }
    }

    @Override
    public JsSdkConfigDTO getJsSdkConfig(String url) {
        String jsapiTicket = getJsapiTicket();
        String nonceStr = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        long timestamp = System.currentTimeMillis() / 1000;

        // 生成签名
        String signature = SignUtil.generateJsSdkSignature(jsapiTicket, nonceStr, timestamp, url);

        JsSdkConfigDTO config = new JsSdkConfigDTO();
        config.setAppId(wechatProperties.getAppId());
        config.setTimestamp(timestamp);
        config.setNonceStr(nonceStr);
        config.setSignature(signature);
        config.setJsApiList(new String[]{
                "updateAppMessageShareData",
                "updateTimelineShareData",
                "onMenuShareTimeline",
                "onMenuShareAppMessage",
                "chooseImage",
                "previewImage",
                "getLocation"
        });

        return config;
    }
}