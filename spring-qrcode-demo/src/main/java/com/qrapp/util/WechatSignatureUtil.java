package com.qrapp.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class WechatSignatureUtil {

    /**
     * 验证微信签名
     */
    public static boolean checkSignature(String token, String signature,
                                         String timestamp, String nonce) {
        try {
            // 1. 字典序排序
            String[] arr = new String[]{token, timestamp, nonce};
            Arrays.sort(arr);

            // 2. 拼接字符串
            StringBuilder content = new StringBuilder();
            for (String s : arr) {
                content.append(s);
            }

            // 3. SHA1加密
            String temp = sha1(content.toString());

            // 4. 对比签名
            return temp != null && temp.equals(signature);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * SHA1加密
     */
    private static String sha1(String str) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] digest = md.digest(str.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : digest) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}