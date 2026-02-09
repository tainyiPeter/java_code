// JsSdkConfigDTO.java - 改进版
package com.wechat.dto;

import lombok.Data;

@Data
public class JsSdkConfigDTO {
    private String appId;
    private long timestamp;
    private String nonceStr;
    private String signature;
    private String[] jsApiList;

    // 新增：验证签名的方法
    public boolean isValid() {
        return appId != null && !appId.isEmpty()
                && timestamp > 0
                && nonceStr != null && !nonceStr.isEmpty()
                && signature != null && !signature.isEmpty();
    }

    // 新增：生成配置JSON字符串（前端直接使用）
    public String toJsonConfig() {
        return String.format(
                "wx.config({" +
                        "appId: '%s'," +
                        "timestamp: %d," +
                        "nonceStr: '%s'," +
                        "signature: '%s'," +
                        "jsApiList: %s," +
                        "debug: false" +
                        "});",
                appId, timestamp, nonceStr, signature,
                jsApiList == null ? "[]" : arrayToJson(jsApiList));
    }

    private String arrayToJson(String[] array) {
        if (array == null || array.length == 0) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < array.length; i++) {
            sb.append("'").append(array[i]).append("'");
            if (i < array.length - 1) {
                sb.append(",");
            }
        }
        sb.append("]");
        return sb.toString();
    }
}