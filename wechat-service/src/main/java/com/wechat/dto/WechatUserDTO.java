// WechatUserDTO.java - 改进版
package com.wechat.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class WechatUserDTO {
    @JsonProperty("openid")
    private String openId;

    private String nickname;

    @JsonProperty("sex")
    private Integer gender; // 建议改名，避免歧义

    private String province;
    private String city;
    private String country;

    @JsonProperty("headimgurl")
    private String headImgUrl;

    @JsonProperty("privilege")
    private List<String> privilege;

    @JsonProperty("unionid")
    private String unionId;

    // 新增错误处理字段
    @JsonProperty("errcode")
    private Integer errCode;

    @JsonProperty("errmsg")
    private String errMsg;

    // 新增判断成功的方法
    public boolean isSuccess() {
        return errCode == null || errCode == 0;
    }

    // 新增：获取高清头像URL
    public String getHighQualityAvatar() {
        if (headImgUrl != null && headImgUrl.contains("/0")) {
            return headImgUrl;
        }
        if (headImgUrl != null) {
            return headImgUrl.replaceAll("/\\d+$", "/0");
        }
        return null;
    }
}