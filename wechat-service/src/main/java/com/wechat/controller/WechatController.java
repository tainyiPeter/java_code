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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Controller  // 改为 @Controller，支持页面跳转
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

    // ========== 新增：授权页面引导功能 ==========

    /**
     * 方式1：直接重定向到微信授权页面（最常用）
     * 访问示例：/wechat/auth/redirect?redirectUri=xxx&scope=snsapi_userinfo
     */
    @GetMapping("/auth/redirect")
    public String redirectToWechatAuth(
            @RequestParam(value = "redirectUri", required = false) String redirectUri,
            @RequestParam(value = "scope", defaultValue = "snsapi_userinfo") String scope,
            @RequestParam(value = "state", required = false) String state) {

        try {
            // 1. 参数处理
            if (redirectUri == null || redirectUri.isEmpty()) {
                // 默认回调到 /wechat/auth-callback
                redirectUri = "https://91qj1470uc04.vicp.fun/wechat/auth-callback";
            }

            // 如果没有传入state，生成一个随机state（用于防CSRF攻击）
            if (state == null || state.isEmpty()) {
                state = generateRandomState();
            }

            // 2. 构建授权URL
            String encodedRedirectUri = URLEncoder.encode(redirectUri, StandardCharsets.UTF_8.name());
            String authUrl = String.format("%s?appid=%s&redirect_uri=%s&response_type=code&scope=%s&state=%s#wechat_redirect",
                    AUTH_URL, appId, encodedRedirectUri, scope, state);

            log.info("重定向到微信授权页面，scope: {}, state: {}, redirectUri: {}", scope, state, redirectUri);

            // 3. 重定向到微信授权页面
            return "redirect:" + authUrl;

        } catch (UnsupportedEncodingException e) {
            log.error("URL编码失败", e);
            throw new RuntimeException("URL编码失败", e);
        }
    }

    /**
     * 方式2：生成授权页面链接（返回HTML页面，让用户点击）
     * 访问示例：/wechat/auth/page
     */
    @GetMapping("/auth/page")
    public String authPage(
            @RequestParam(value = "redirectUri", required = false) String redirectUri,
            @RequestParam(value = "scope", defaultValue = "snsapi_userinfo") String scope,
            Map<String, Object> model) {

        try {
            // 参数处理
            if (redirectUri == null || redirectUri.isEmpty()) {
                redirectUri = "https://91qj1470uc04.vicp.fun/wechat/auth-callback";
            }

            String state = generateRandomState();
            String encodedRedirectUri = URLEncoder.encode(redirectUri, StandardCharsets.UTF_8.name());
            String authUrl = String.format("%s?appid=%s&redirect_uri=%s&response_type=code&scope=%s&state=%s#wechat_redirect",
                    AUTH_URL, appId, encodedRedirectUri, scope, state);

            // 将数据传递给模板
            model.put("authUrl", authUrl);
            model.put("appId", appId);
            model.put("scope", scope);
            model.put("state", state);
            model.put("redirectUri", redirectUri);

            log.info("生成授权页面，授权URL: {}", authUrl);

            // 返回模板页面（需要创建相应的HTML模板）
            return "wechat/auth-page";

        } catch (Exception e) {
            log.error("生成授权页面失败", e);
            model.put("error", "生成授权页面失败: " + e.getMessage());
            return "error";
        }
    }

    /**
     * 方式3：静默授权（只获取openid，不弹出授权页面）
     * 访问示例：/wechat/auth/silent
     */
    @GetMapping("/auth/silent")
    public String silentAuth(
            @RequestParam(value = "redirectUri", required = false) String redirectUri,
            @RequestParam(value = "state", required = false) String state) {

        try {
            if (redirectUri == null || redirectUri.isEmpty()) {
                redirectUri = "https://91qj1470uc04.vicp.fun/wechat/auth-callback";
            }

            if (state == null || state.isEmpty()) {
                state = generateRandomState();
            }

            String encodedRedirectUri = URLEncoder.encode(redirectUri, StandardCharsets.UTF_8.name());
            String authUrl = String.format("%s?appid=%s&redirect_uri=%s&response_type=code&scope=snsapi_base&state=%s#wechat_redirect",
                    AUTH_URL, appId, encodedRedirectUri, state);

            log.info("静默授权，重定向URL: {}", authUrl);

            return "redirect:" + authUrl;

        } catch (UnsupportedEncodingException e) {
            log.error("URL编码失败", e);
            throw new RuntimeException("URL编码失败", e);
        }
    }

    /**
     * 方式4：带参数的授权（支持更多参数）
     * 访问示例：/wechat/auth/full?redirectUri=xxx&scope=xxx&state=xxx&forcePopup=true
     */
    @GetMapping("/auth/full")
    public String fullAuth(
            @RequestParam(value = "redirectUri", required = false) String redirectUri,
            @RequestParam(value = "scope", defaultValue = "snsapi_userinfo") String scope,
            @RequestParam(value = "state", required = false) String state,
            @RequestParam(value = "forcePopup", defaultValue = "false") boolean forcePopup) {

        try {
            if (redirectUri == null || redirectUri.isEmpty()) {
                redirectUri = "https://91qj1470uc04.vicp.fun/wechat/auth-callback";
            }

            if (state == null || state.isEmpty()) {
                state = generateRandomState();
            }

            String encodedRedirectUri = URLEncoder.encode(redirectUri, StandardCharsets.UTF_8.name());
            StringBuilder authUrl = new StringBuilder();
            authUrl.append(AUTH_URL)
                    .append("?appid=").append(appId)
                    .append("&redirect_uri=").append(encodedRedirectUri)
                    .append("&response_type=code")
                    .append("&scope=").append(scope)
                    .append("&state=").append(state);

            // 强制弹出授权页面（即使用户已经授权过）
            if (forcePopup) {
                authUrl.append("&forcePopup=true");
            }

            authUrl.append("#wechat_redirect");

            log.info("完整授权流程，URL: {}", authUrl);

            return "redirect:" + authUrl.toString();

        } catch (UnsupportedEncodingException e) {
            log.error("URL编码失败", e);
            throw new RuntimeException("URL编码失败", e);
        }
    }

    /**
     * 方式5：授权页面（返回JSON，供前端使用）
     * 访问示例：/wechat/auth/json
     */
    @GetMapping("/auth/json")
    @ResponseBody
    public ResponseEntity<?> authJson(
            @RequestParam(value = "redirectUri", required = false) String redirectUri,
            @RequestParam(value = "scope", defaultValue = "snsapi_userinfo") String scope,
            @RequestParam(value = "state", required = false) String state) {

        try {
            if (redirectUri == null || redirectUri.isEmpty()) {
                redirectUri = "https://91qj1470uc04.vicp.fun/wechat/auth-callback";
            }

            if (state == null || state.isEmpty()) {
                state = generateRandomState();
            }

            String encodedRedirectUri = URLEncoder.encode(redirectUri, StandardCharsets.UTF_8.name());
            String authUrl = String.format("%s?appid=%s&redirect_uri=%s&response_type=code&scope=%s&state=%s#wechat_redirect",
                    AUTH_URL, appId, encodedRedirectUri, scope, state);

            Map<String, Object> result = new HashMap<>();
            result.put("code", 0);
            result.put("message", "success");
            result.put("data", Map.of(
                    "authUrl", authUrl,
                    "appId", appId,
                    "scope", scope,
                    "state", state,
                    "redirectUri", redirectUri,
                    "description", "请将用户重定向到此URL进行微信授权"
            ));

            log.info("生成授权JSON，授权URL: {}", authUrl);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("生成授权JSON失败", e);
            return ResponseEntity.badRequest().body(
                    Map.of("code", -1, "message", "生成授权URL失败: " + e.getMessage())
            );
        }
    }

    /**
     * 方式6：一步登录（页面直接调用，自动重定向）
     * 访问示例：/wechat/login/auto
     * 这是最常用的方式，用户访问这个页面会自动跳转到微信授权
     */
    @GetMapping("/login/auto")
    public String autoLogin(
            @RequestParam(value = "returnUrl", required = false) String returnUrl,
            @RequestParam(value = "scope", defaultValue = "snsapi_userinfo") String scope) {

        try {
            // 生成回调URL，将returnUrl作为state传递
            String state = returnUrl != null ? returnUrl : "";

            // 构建回调URL（授权后回到这个接口处理）
            String callbackUrl = "https://91qj1470uc04.vicp.fun/wechat/login/callback";
            if (returnUrl != null && !returnUrl.isEmpty()) {
                // 将returnUrl编码后作为参数传递给callback
                callbackUrl += "?returnUrl=" + URLEncoder.encode(returnUrl, StandardCharsets.UTF_8.name());
            }

            String encodedCallbackUrl = URLEncoder.encode(callbackUrl, StandardCharsets.UTF_8.name());
            String authUrl = String.format("%s?appid=%s&redirect_uri=%s&response_type=code&scope=%s&state=%s#wechat_redirect",
                    AUTH_URL, appId, encodedCallbackUrl, scope, state);

            log.info("自动登录，跳转到授权页面，returnUrl: {}", returnUrl);

            return "redirect:" + authUrl;

        } catch (Exception e) {
            log.error("自动登录失败", e);
            throw new RuntimeException("自动登录失败", e);
        }
    }

    /**
     * 自动登录的回调处理
     */
    @GetMapping("/login/callback")
    public String loginCallback(
            @RequestParam("code") String code,
            @RequestParam(value = "state", required = false) String state,
            @RequestParam(value = "returnUrl", required = false) String returnUrl) {

        try {
            log.info("自动登录回调，code: {}, state: {}, returnUrl: {}", code, state, returnUrl);

            // 1. 获取access_token
            String accessTokenUrl = String.format("%s?appid=%s&secret=%s&code=%s&grant_type=authorization_code",
                    ACCESS_TOKEN_URL, appId, appSecret, code);

            String response = httpUtil.doGet(accessTokenUrl);
            JSONObject tokenJson = JSON.parseObject(response);

            if (tokenJson.containsKey("errcode")) {
                log.error("获取access_token失败: {}", tokenJson);
                return "redirect:/error?message=授权失败";
            }

            String accessToken = tokenJson.getString("access_token");
            String openId = tokenJson.getString("openid");
            String scope = tokenJson.getString("scope");

            // 2. 获取用户信息
            WechatUserDTO userInfo = null;
            if ("snsapi_userinfo".equals(scope)) {
                userInfo = getUserInfo(accessToken, openId);
            }

            // 3. 这里应该将用户信息保存到session或数据库
            // 示例：保存到session
            // HttpSession session = request.getSession();
            // session.setAttribute("wechatUser", userInfo);
            // session.setAttribute("openId", openId);
            // session.setAttribute("accessToken", accessToken);

            log.info("自动登录成功，openid: {}, nickname: {}",
                    openId, userInfo != null ? userInfo.getNickname() : "N/A");

            // 4. 重定向到原始页面或首页
            if (returnUrl != null && !returnUrl.isEmpty()) {
                return "redirect:" + returnUrl;
            } else if (state != null && !state.isEmpty()) {
                // state中可能包含了returnUrl
                return "redirect:" + state;
            } else {
                return "redirect:/index";
            }

        } catch (Exception e) {
            log.error("自动登录回调处理失败", e);
            return "redirect:/error?message=登录失败";
        }
    }

    /**
     * 生成随机的state参数（用于防CSRF攻击）
     */
    private String generateRandomState() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }

    /**
     * 工具方法：构建授权URL
     */
    public String buildAuthUrl(String redirectUri, String scope, String state) {
        try {
            String encodedRedirectUri = URLEncoder.encode(redirectUri, StandardCharsets.UTF_8.name());
            return String.format("%s?appid=%s&redirect_uri=%s&response_type=code&scope=%s&state=%s#wechat_redirect",
                    AUTH_URL, appId, encodedRedirectUri, scope, state);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("URL编码失败", e);
        }
    }

    // ========== 原有代码保持不变 ==========

    /**
     * 验证微信服务器（GET请求）
     * 在微信公众平台配置服务器时调用
     */
    @GetMapping("/verify")
    @ResponseBody
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
    @ResponseBody
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
     * 网页授权回调接口 - 修正版
     */
    @GetMapping("/auth-callback")
    @ResponseBody
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
    @ResponseBody
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
            userInfo.setOpenId(userJson.getString("openid"));

            // 处理昵称乱码问题
            String nickname = userJson.getString("nickname");
            if (nickname != null) {
                // 尝试修复编码
                nickname = fixNicknameEncoding(nickname);
            }
            userInfo.setNickname(nickname);

            userInfo.setGender(userJson.getInteger("sex"));
            userInfo.setProvince(userJson.getString("province"));
            userInfo.setCity(userJson.getString("city"));
            userInfo.setCountry(userJson.getString("country"));
            userInfo.setHeadImgUrl(userJson.getString("headimgurl"));
            userInfo.setUnionId(userJson.getString("unionid"));
            userInfo.setErrCode(userJson.getInteger("errcode"));
            userInfo.setErrMsg(userJson.getString("errmsg"));

            return userInfo;
        } catch (Exception e) {
            log.error("获取用户信息异常", e);
            return null;
        }
    }

    /**
     * 修复昵称编码问题
     */
    private String fixNicknameEncoding(String nickname) {
        if (nickname == null || nickname.isEmpty()) {
            return nickname;
        }

        try {
            // 如果是乱码，尝试从ISO-8859-1转换到UTF-8
            if (isGarbled(nickname)) {
                byte[] isoBytes = nickname.getBytes(StandardCharsets.ISO_8859_1);
                String utf8String = new String(isoBytes, StandardCharsets.UTF_8);

                if (containsChinese(utf8String) && !isGarbled(utf8String)) {
                    log.debug("修复昵称编码: {} -> {}", nickname, utf8String);
                    return utf8String;
                }
            }
        } catch (Exception e) {
            log.warn("修复昵称编码失败: {}", e.getMessage());
        }

        return nickname;
    }

    /**
     * 判断是否乱码
     */
    private boolean isGarbled(String str) {
        if (str == null) return false;
        for (char c : str.toCharArray()) {
            if (c == '?' || c == '�') {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否包含中文
     */
    private boolean containsChinese(String str) {
        if (str == null) return false;
        for (char c : str.toCharArray()) {
            if (Character.UnicodeScript.of(c) == Character.UnicodeScript.HAN) {
                return true;
            }
        }
        return false;
    }

    /**
     * 刷新access_token - 修正版
     */
    @GetMapping("/refresh")
    @ResponseBody
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
    @ResponseBody
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
    @ResponseBody
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
    @ResponseBody
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

    /**
     * 智能授权 - 先检查用户是否已关注
     */
    @GetMapping("/auth/smart")
    public String smartAuth(
            @RequestParam(value = "redirectUri", required = false) String redirectUri,
            @RequestParam(value = "state", required = false) String state,
            HttpServletRequest request) {

        try {
            // 1. 检查是否有openid（用户可能已经授权过）
            String openid = getOpenidFromCookie(request);

            if (openid != null) {
                // 2. 检查用户是否已关注
                if (isUserSubscribed(openid)) {
                    // 已关注且已授权，直接跳转
                    log.info("用户已关注且已授权，直接跳转到目标页面");
                    return buildRedirectUrl(redirectUri, state);
                } else {
                    // 有openid但未关注，提示关注
                    log.info("用户有openid但未关注，跳转到关注页面");
                    return "redirect:/wechat/follow-guide";
                }
            }

            // 3. 用户既没有openid，也没有关注，进行授权
            if (redirectUri == null || redirectUri.isEmpty()) {
                redirectUri = "https://91qj1470uc04.vicp.fun/user/center";
            }

            if (state == null || state.isEmpty()) {
                state = generateRandomState();
            }

            String encodedRedirectUri = URLEncoder.encode(redirectUri, StandardCharsets.UTF_8.name());
            String authUrl = String.format("%s?appid=%s&redirect_uri=%s&response_type=code&scope=snsapi_base&state=%s#wechat_redirect",
                    AUTH_URL, appId, encodedRedirectUri, state);

            log.info("用户未关注，使用静默授权获取openid");
            return "redirect:" + authUrl;

        } catch (Exception e) {
            log.error("智能授权失败", e);
            return "redirect:/error?message=授权失败";
        }
    }

    /**
     * 检查用户是否已关注
     */
    private boolean isUserSubscribed(String openid) {
        try {
            // 获取基础access_token（需要缓存）
            String accessToken = getCachedAccessToken();
            if (accessToken == null) {
                log.warn("无法获取基础access_token");
                return false;
            }

            // 调用微信API检查用户信息
            String userInfoUrl = String.format("https://api.weixin.qq.com/cgi-bin/user/info?access_token=%s&openid=%s&lang=zh_CN",
                    accessToken, openid);

            String response = httpUtil.doGet(userInfoUrl);
            JSONObject json = JSON.parseObject(response);

            // 如果用户关注，subscribe字段为1
            if (json.containsKey("subscribe") && json.getIntValue("subscribe") == 1) {
                return true;
            }

            // 错误处理
            if (json.containsKey("errcode")) {
                log.warn("检查用户关注状态失败: {}", json.getString("errmsg"));
            }

            return false;

        } catch (Exception e) {
            log.error("检查用户关注状态异常", e);
            return false;
        }
    }

    /**
     * 获取基础access_token（需要实现缓存机制）
     */
    private String getCachedAccessToken() {
        // 这里应该从缓存或数据库获取
        // 简单实现：每次重新获取
        try {
            String url = String.format("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s",
                    appId, appSecret);

            String response = httpUtil.doGet(url);
            JSONObject json = JSON.parseObject(response);

            if (json.containsKey("access_token")) {
                return json.getString("access_token");
            }

            log.error("获取基础access_token失败: {}", response);
            return null;

        } catch (Exception e) {
            log.error("获取基础access_token异常", e);
            return null;
        }
    }

    /**
     * 新版授权流程（处理未关注用户）
     */
    @GetMapping("/auth/new")
    public String newAuthFlow(
            @RequestParam(value = "redirectUri", required = false) String redirectUri,
            @RequestParam(value = "state", required = false) String state,
            HttpServletRequest request,
            HttpServletResponse response) {

        try {
            // 1. 检查Cookie中是否有openid
            String openid = getOpenidFromCookie(request);

            if (openid != null) {
                // 2. 检查用户是否已关注
                if (isUserSubscribed(openid)) {
                    // 已关注，直接跳转到目标页面
                    return buildRedirectUrl(redirectUri, state);
                } else {
                    // 未关注，保存redirectUri到session，跳转到关注页面
                    HttpSession session = request.getSession();
                    session.setAttribute("pendingRedirectUri", redirectUri);
                    session.setAttribute("pendingState", state);
                    return "redirect:/wechat/follow-guide";
                }
            }

            // 3. 没有openid，先使用静默授权获取openid
            String callbackUrl = "https://91qj1470uc04.vicp.fun/wechat/auth/callback";
            if (redirectUri != null) {
                callbackUrl += "?pendingRedirectUri=" + URLEncoder.encode(redirectUri, StandardCharsets.UTF_8.name());
            }
            if (state != null) {
                callbackUrl += "&pendingState=" + state;
            }

            String encodedCallbackUrl = URLEncoder.encode(callbackUrl, StandardCharsets.UTF_8.name());
            String authUrl = String.format("%s?appid=%s&redirect_uri=%s&response_type=code&scope=snsapi_base&state=auth_new#wechat_redirect",
                    AUTH_URL, appId, encodedCallbackUrl);

            return "redirect:" + authUrl;

        } catch (Exception e) {
            log.error("新版授权流程失败", e);
            return "redirect:/error?message=授权失败";
        }
    }

    /**
     * 授权回调处理
     */
    @GetMapping("/auth/callback")
    public String authCallback(
            @RequestParam("code") String code,
            @RequestParam(value = "state", required = false) String state,
            @RequestParam(value = "pendingRedirectUri", required = false) String pendingRedirectUri,
            @RequestParam(value = "pendingState", required = false) String pendingState,
            HttpServletRequest request,
            HttpServletResponse response) {

        try {
            // 1. 使用code获取openid
            String accessTokenUrl = String.format("%s?appid=%s&secret=%s&code=%s&grant_type=authorization_code",
                    ACCESS_TOKEN_URL, appId, appSecret, code);

            String tokenResponse = httpUtil.doGet(accessTokenUrl);
            JSONObject tokenJson = JSON.parseObject(tokenResponse);

            if (tokenJson.containsKey("errcode")) {
                log.error("获取access_token失败: {}", tokenJson);
                return "redirect:/error?message=授权失败";
            }

            String openid = tokenJson.getString("openid");

            // 2. 保存openid到Cookie
            Cookie openidCookie = new Cookie("wechat_openid", openid);
            openidCookie.setMaxAge(30 * 24 * 60 * 60); // 30天
            openidCookie.setPath("/");
            openidCookie.setHttpOnly(true);
            response.addCookie(openidCookie);

            // 3. 检查是否已关注
            if (isUserSubscribed(openid)) {
                // 已关注，跳转到目标页面
                String redirectUri = pendingRedirectUri != null ? pendingRedirectUri : "/user/center";
                String finalState = pendingState != null ? pendingState : state;
                return buildRedirectUrl(redirectUri, finalState);
            } else {
                // 未关注，保存到session，跳转到关注页面
                HttpSession session = request.getSession();
                session.setAttribute("pendingRedirectUri", pendingRedirectUri);
                session.setAttribute("pendingState", pendingState);
                session.setAttribute("userOpenid", openid);
                return "redirect:/wechat/follow-guide";
            }

        } catch (Exception e) {
            log.error("授权回调处理失败", e);
            return "redirect:/error?message=回调处理失败";
        }
    }

    /**
     * 关注后回调
     */
    @GetMapping("/follow/callback")
    public String followCallback(HttpServletRequest request) {
        try {
            HttpSession session = request.getSession();
            String openid = (String) session.getAttribute("userOpenid");
            String pendingRedirectUri = (String) session.getAttribute("pendingRedirectUri");
            String pendingState = (String) session.getAttribute("pendingState");

            if (openid == null) {
                return "redirect:/error?message=未找到用户信息";
            }

            // 检查是否已关注
            if (isUserSubscribed(openid)) {
                // 清除session数据
                session.removeAttribute("userOpenid");
                session.removeAttribute("pendingRedirectUri");
                session.removeAttribute("pendingState");

                // 跳转到目标页面
                String redirectUri = pendingRedirectUri != null ? pendingRedirectUri : "/user/center";
                return "redirect:" + redirectUri;
            } else {
                return "redirect:/wechat/follow-guide?message=请先关注公众号";
            }

        } catch (Exception e) {
            log.error("关注回调处理失败", e);
            return "redirect:/error?message=处理失败";
        }
    }

    /**
     * 从Cookie获取openid
     */
    private String getOpenidFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("wechat_openid".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    /**
     * 构建重定向URL
     */
    private String buildRedirectUrl(String redirectUri, String state) {
        if (redirectUri == null || redirectUri.isEmpty()) {
            redirectUri = "/user/center";
        }

        if (state != null && !state.isEmpty()) {
            // 如果redirectUri已经包含参数，添加&，否则添加?
            if (redirectUri.contains("?")) {
                redirectUri += "&state=" + state;
            } else {
                redirectUri += "?state=" + state;
            }
        }

        return "redirect:" + redirectUri;
    }

    /**
     * 接口测试
     */
    @GetMapping("/mytest")
    @ResponseBody  // 表明返回的是数据，而不是视图模板
    public String mytest() {
        log.info("this is mytest appid:{}, appSecret:{}", appId, appSecret);
        return "测试成功，appId: " + appId;
    }
}