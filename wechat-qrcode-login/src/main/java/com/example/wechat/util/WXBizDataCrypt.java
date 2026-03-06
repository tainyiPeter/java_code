package com.example.wechat.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.AlgorithmParameters;
import java.security.Security;
import java.util.Base64;
import com.alibaba.fastjson.JSONObject;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

@Component
public class WXBizDataCrypt {

    private static final Logger logger = LoggerFactory.getLogger(WXBizDataCrypt.class);

    static {
        // 添加BouncyCastle支持 - 这里需要捕获异常
        try {
            Security.addProvider(new BouncyCastleProvider());
            logger.info("BouncyCastleProvider 初始化成功");
        } catch (Exception e) {
            logger.error("BouncyCastleProvider 初始化失败", e);
        }
    }

    /**
     * 解密微信加密数据
     * @param sessionKey 会话密钥
     * @param encryptedData 加密数据
     * @param iv 初始向量
     * @param appId 小程序appId（用于验证水印）
     * @return 解密后的数据，失败返回null
     */
    public JSONObject decrypt(String sessionKey, String encryptedData, String iv, String appId) {
        try {
            logger.debug("开始解密数据，appId: {}", appId);

            // Base64解码
            byte[] sessionKeyBytes = Base64.getDecoder().decode(sessionKey);
            byte[] encryptedDataBytes = Base64.getDecoder().decode(encryptedData);
            byte[] ivBytes = Base64.getDecoder().decode(iv);

            // 创建AES解密器
            SecretKeySpec keySpec = new SecretKeySpec(sessionKeyBytes, "AES");
            AlgorithmParameters params = AlgorithmParameters.getInstance("AES");
            params.init(new IvParameterSpec(ivBytes));

            // 创建密码器（使用CBC模式，PKCS7Padding）
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");
            cipher.init(Cipher.DECRYPT_MODE, keySpec, params);

            // 解密
            byte[] decryptedBytes = cipher.doFinal(encryptedDataBytes);
            String decryptedText = new String(decryptedBytes, StandardCharsets.UTF_8);

            logger.debug("解密结果: {}", decryptedText);

            // 解析JSON
            JSONObject result = JSONObject.parseObject(decryptedText);

            // 验证水印中的appid
            JSONObject watermark = result.getJSONObject("watermark");
            if (watermark != null) {
                String watermarkAppId = watermark.getString("appid");
                if (!appId.equals(watermarkAppId)) {
                    logger.error("appid不匹配: 解密结果中的appid={}, 期望={}", watermarkAppId, appId);
                    return null;
                }
            }

            return result;

        } catch (Exception e) {
            logger.error("解密失败", e);
            return null;
        }
    }

    /**
     * 解密手机号（便捷方法）
     */
    public JSONObject decryptPhoneNumber(String sessionKey, String encryptedData, String iv, String appId) {
        return decrypt(sessionKey, encryptedData, iv, appId);
    }
}