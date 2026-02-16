package com.example.wechat.util;

import com.alibaba.fastjson.JSONObject;
import com.example.wechat.config.WechatConfig;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class WechatUtil {

    private static final Logger logger = LoggerFactory.getLogger(WechatUtil.class);

    @Autowired
    private WechatConfig wechatConfig;

    /**
     * 通过code获取openid和session_key
     */
    public JSONObject getOpenIdByCode(String code) {
        String url = String.format("%s?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code",
                wechatConfig.getJscode2sessionUrl(),
                wechatConfig.getAppId(),
                wechatConfig.getAppSecret(),
                code);

        logger.debug("请求微信接口: {}", url);

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(url);
            httpGet.setHeader("Content-Type", "application/json");

            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                HttpEntity entity = response.getEntity();
                String result = EntityUtils.toString(entity, "UTF-8");
                logger.debug("微信接口返回: {}", result);

                JSONObject jsonResult = JSONObject.parseObject(result);

                // 检查是否有错误
                if (jsonResult.containsKey("errcode") && jsonResult.getInteger("errcode") != 0) {
                    logger.error("微信接口返回错误: {}", jsonResult);
                }

                return jsonResult;
            }
        } catch (IOException e) {
            logger.error("调用微信接口失败", e);
            JSONObject error = new JSONObject();
            error.put("errcode", -1);
            error.put("errmsg", "网络请求失败: " + e.getMessage());
            return error;
        }
    }

    /**
     * 验证签名（可选，用于数据解密）
     */
    public boolean checkSignature(String sessionKey, String rawData, String signature) {
        // 实现签名验证逻辑
        // 暂不实现，可根据需要添加
        return true;
    }
}