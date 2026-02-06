package com.wechat.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

@Slf4j
public class SignUtil {

    /**
     * 验证微信服务器签名
     */
    public static boolean checkSignature(String token, String timestamp,
                                         String nonce, String signature) {
        if (!StringUtils.hasText(token) || !StringUtils.hasText(timestamp)
                || !StringUtils.hasText(nonce) || !StringUtils.hasText(signature)) {
            return false;
        }

        String[] arr = new String[]{token, timestamp, nonce};
        Arrays.sort(arr);

        StringBuilder content = new StringBuilder();
        for (String str : arr) {
            content.append(str);
        }

        String computedSignature = sha1(content.toString());
        return computedSignature != null && computedSignature.equals(signature);
    }

    /**
     * 生成JS-SDK签名
     */
    public static String generateJsSdkSignature(String jsapiTicket, String nonceStr,
                                                long timestamp, String url) {
        String string1 = "jsapi_ticket=" + jsapiTicket
                + "&noncestr=" + nonceStr
                + "&timestamp=" + timestamp
                + "&url=" + url;

        return sha1(string1);
    }

    /**
     * SHA1加密
     */
    private static String sha1(String str) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(str.getBytes());
            byte[] digest = md.digest();

            StringBuilder hexString = new StringBuilder();
            for (byte b : digest) {
                String shaHex = Integer.toHexString(b & 0xFF);
                if (shaHex.length() < 2) {
                    hexString.append(0);
                }
                hexString.append(shaHex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            log.error("SHA1加密失败", e);
            return null;
        }
    }
}