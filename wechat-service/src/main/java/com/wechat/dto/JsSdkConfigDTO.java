package com.wechat.dto;

import lombok.Data;

@Data
public class JsSdkConfigDTO {
    private String appId;
    private long timestamp;
    private String nonceStr;
    private String signature;
    private String[] jsApiList;
}