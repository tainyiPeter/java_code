package com.wechat.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 微信工具类
 * 包含常用的微信API调用方法
 */
@Slf4j
@Component
public class WechatUtil {

    private final RestTemplate restTemplate;
    private final HttpUtil httpUtil;

    public WechatUtil(RestTemplate restTemplate, HttpUtil httpUtil) {
        this.restTemplate = restTemplate;
        this.httpUtil = httpUtil;
    }

    /**
     * 发送模板消息
     * @param accessToken 基础access_token
     * @param openId 用户openid
     * @param templateId 模板ID
     * @param data 模板数据
     * @param url 跳转链接（可选）
     * @param miniProgram 小程序信息（可选）
     * @return 是否发送成功
     */
    public boolean sendTemplateMessage(String accessToken, String openId,
                                       String templateId, Map<String, Object> data,
                                       String url, Map<String, Object> miniProgram) {
        String apiUrl = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=" + accessToken;

        Map<String, Object> params = new HashMap<>();
        params.put("touser", openId);
        params.put("template_id", templateId);
        params.put("data", data);

        if (StringUtils.hasText(url)) {
            params.put("url", url);
        }

        if (miniProgram != null && !miniProgram.isEmpty()) {
            params.put("miniprogram", miniProgram);
        }

        try {
            String jsonParams = JSON.toJSONString(params);
            String response = httpUtil.doPost(apiUrl, jsonParams);
            JSONObject result = JSON.parseObject(response);

            if (result.getIntValue("errcode") == 0) {
                log.info("模板消息发送成功，消息ID: {}", result.getString("msgid"));
                return true;
            } else {
                log.error("模板消息发送失败: {}", response);
                return false;
            }
        } catch (Exception e) {
            log.error("发送模板消息异常", e);
            return false;
        }
    }

    /**
     * 创建自定义菜单
     * @param accessToken 基础access_token
     * @param menuJson 菜单JSON
     * @return 是否创建成功
     */
    public boolean createMenu(String accessToken, String menuJson) {
        String apiUrl = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=" + accessToken;

        try {
            String response = httpUtil.doPost(apiUrl, menuJson);
            JSONObject result = JSON.parseObject(response);

            if (result.getIntValue("errcode") == 0) {
                log.info("菜单创建成功");
                return true;
            } else {
                log.error("菜单创建失败: {}", response);
                return false;
            }
        } catch (Exception e) {
            log.error("创建菜单异常", e);
            return false;
        }
    }

    /**
     * 获取关注者列表
     * @param accessToken 基础access_token
     * @param nextOpenId 下一个openid（第一次传空）
     * @return 关注者列表
     */
    public JSONObject getUserList(String accessToken, String nextOpenId) {
        String apiUrl = "https://api.weixin.qq.com/cgi-bin/user/get?access_token=" + accessToken;

        if (StringUtils.hasText(nextOpenId)) {
            apiUrl += "&next_openid=" + nextOpenId;
        }

        try {
            String response = httpUtil.doGet(apiUrl);
            return JSON.parseObject(response);
        } catch (Exception e) {
            log.error("获取用户列表异常", e);
            return null;
        }
    }

    /**
     * 获取用户基本信息（通过基础access_token）
     * @param accessToken 基础access_token
     * @param openId 用户openid
     * @param lang 语言（zh_CN, zh_TW, en）
     * @return 用户信息
     */
    public JSONObject getUserInfo(String accessToken, String openId, String lang) {
        String apiUrl = "https://api.weixin.qq.com/cgi-bin/user/info" +
                "?access_token=" + accessToken +
                "&openid=" + openId +
                "&lang=" + lang;

        try {
            String response = httpUtil.doGet(apiUrl);
            return JSON.parseObject(response);
        } catch (Exception e) {
            log.error("获取用户信息异常", e);
            return null;
        }
    }

    /**
     * 生成带参数的二维码
     * @param accessToken 基础access_token
     * @param sceneStr 场景值（字符串形式）
     * @param expireSeconds 过期时间（秒），0表示永久
     * @return 二维码ticket
     */
    public String createQrCode(String accessToken, String sceneStr, int expireSeconds) {
        String apiUrl = "https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=" + accessToken;

        Map<String, Object> params = new HashMap<>();

        if (expireSeconds > 0) {
            // 临时二维码
            params.put("expire_seconds", expireSeconds);
            params.put("action_name", "QR_STR_SCENE");

            Map<String, Object> scene = new HashMap<>();
            Map<String, Object> sceneInfo = new HashMap<>();
            sceneInfo.put("scene_str", sceneStr);
            scene.put("scene", sceneInfo);

            params.put("action_info", scene);
        } else {
            // 永久二维码
            params.put("action_name", "QR_LIMIT_STR_SCENE");

            Map<String, Object> scene = new HashMap<>();
            Map<String, Object> sceneInfo = new HashMap<>();
            sceneInfo.put("scene_str", sceneStr);
            scene.put("scene", sceneInfo);

            params.put("action_info", scene);
        }

        try {
            String jsonParams = JSON.toJSONString(params);
            String response = httpUtil.doPost(apiUrl, jsonParams);
            JSONObject result = JSON.parseObject(response);

            if (result.containsKey("ticket")) {
                return result.getString("ticket");
            } else {
                log.error("生成二维码失败: {}", response);
                return null;
            }
        } catch (Exception e) {
            log.error("生成二维码异常", e);
            return null;
        }
    }

    /**
     * 根据ticket获取二维码图片URL
     * @param ticket 二维码ticket
     * @return 二维码图片URL
     */
    public String getQrCodeUrl(String ticket) {
        return "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=" + ticket;
    }

    /**
     * 获取微信服务器IP地址
     * @param accessToken 基础access_token
     * @return IP地址列表
     */
    public JSONObject getCallbackIp(String accessToken) {
        String apiUrl = "https://api.weixin.qq.com/cgi-bin/getcallbackip?access_token=" + accessToken;

        try {
            String response = httpUtil.doGet(apiUrl);
            return JSON.parseObject(response);
        } catch (Exception e) {
            log.error("获取微信服务器IP异常", e);
            return null;
        }
    }

    /**
     * 长链接转短链接
     * @param accessToken 基础access_token
     * @param longUrl 长链接
     * @return 短链接
     */
    public String shortUrl(String accessToken, String longUrl) {
        String apiUrl = "https://api.weixin.qq.com/cgi-bin/shorturl?access_token=" + accessToken;

        Map<String, Object> params = new HashMap<>();
        params.put("action", "long2short");
        params.put("long_url", longUrl);

        try {
            String jsonParams = JSON.toJSONString(params);
            String response = httpUtil.doPost(apiUrl, jsonParams);
            JSONObject result = JSON.parseObject(response);

            if (result.getIntValue("errcode") == 0) {
                return result.getString("short_url");
            } else {
                log.error("长链接转短链接失败: {}", response);
                return null;
            }
        } catch (Exception e) {
            log.error("长链接转短链接异常", e);
            return null;
        }
    }

    /**
     * 获取素材列表
     * @param accessToken 基础access_token
     * @param type 素材类型（image, voice, video, news）
     * @param offset 从全部素材的该偏移位置开始返回
     * @param count 返回素材的数量
     * @return 素材列表
     */
    public JSONObject getMaterialList(String accessToken, String type, int offset, int count) {
        String apiUrl = "https://api.weixin.qq.com/cgi-bin/material/batchget_material?access_token=" + accessToken;

        Map<String, Object> params = new HashMap<>();
        params.put("type", type);
        params.put("offset", offset);
        params.put("count", count);

        try {
            String jsonParams = JSON.toJSONString(params);
            String response = httpUtil.doPost(apiUrl, jsonParams);
            return JSON.parseObject(response);
        } catch (Exception e) {
            log.error("获取素材列表异常", e);
            return null;
        }
    }

    /**
     * 验证消息是否来自微信服务器（用于消息推送）
     * @param signature 微信加密签名
     * @param timestamp 时间戳
     * @param nonce 随机数
     * @param token 令牌（需要和微信后台配置一致）
     * @return 是否验证成功
     */
    public boolean verifyMessageSignature(String signature, String timestamp,
                                          String nonce, String token) {
        return SignUtil.checkSignature(token, timestamp, nonce, signature);
    }

    /**
     * 解析XML消息（简单示例）
     * 实际使用时可能需要更复杂的XML解析
     */
    public Map<String, String> parseXmlMessage(String xmlContent) {
        // 这里只是一个示例，实际应该使用DOM或SAX解析XML
        // 或者使用第三方库如dom4j、XStream等

        Map<String, String> result = new HashMap<>();
        // 简单的字符串解析（仅适用于简单情况）
        String[] lines = xmlContent.split("\n");
        for (String line : lines) {
            if (line.contains("<") && line.contains(">")) {
                String tag = line.substring(line.indexOf("<") + 1, line.indexOf(">"));
                String content = line.substring(line.indexOf(">") + 1, line.lastIndexOf("<"));
                result.put(tag, content);
            }
        }
        return result;
    }

    /**
     * 生成回复消息XML
     * @param toUserName 接收方帐号
     * @param fromUserName 发送方帐号
     * @param createTime 消息创建时间
     * @param msgType 消息类型
     * @param content 消息内容
     * @return XML格式的回复消息
     */
    public String generateReplyXml(String toUserName, String fromUserName,
                                   long createTime, String msgType, String content) {
        return String.format(
                "<xml>\n" +
                        "<ToUserName><![CDATA[%s]]></ToUserName>\n" +
                        "<FromUserName><![CDATA[%s]]></FromUserName>\n" +
                        "<CreateTime>%d</CreateTime>\n" +
                        "<MsgType><![CDATA[%s]]></MsgType>\n" +
                        "<Content><![CDATA[%s]]></Content>\n" +
                        "</xml>",
                toUserName, fromUserName, createTime, msgType, content
        );
    }

    /**
     * 处理接收到的消息（示例方法）
     * @param xmlMessage 接收到的XML消息
     * @return 回复的XML消息
     */
    public String handleMessage(String xmlMessage) {
        Map<String, String> message = parseXmlMessage(xmlMessage);

        String msgType = message.get("MsgType");
        String fromUserName = message.get("FromUserName");
        String toUserName = message.get("ToUserName");

        long createTime = System.currentTimeMillis() / 1000;

        // 根据消息类型处理
        switch (msgType) {
            case "text":
                String content = message.get("Content");
                // 示例：回复收到的文本
                return generateReplyXml(fromUserName, toUserName,
                        createTime, "text", "收到消息：" + content);

            case "event":
                String event = message.get("Event");
                if ("subscribe".equals(event)) {
                    // 关注事件
                    return generateReplyXml(fromUserName, toUserName,
                            createTime, "text", "欢迎关注！");
                }
                break;

            default:
                // 其他消息类型
                return generateReplyXml(fromUserName, toUserName,
                        createTime, "text", "暂不支持此类型消息");
        }

        return "success"; // 不回复任何内容
    }
}