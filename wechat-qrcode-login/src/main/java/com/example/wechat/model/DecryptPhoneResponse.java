package com.example.wechat.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DecryptPhoneResponse {
    private boolean success;
    private String message;
    private Body body;

    @Data
    @Builder
    public static class Body {
        private String phoneNumber;
        private String purePhoneNumber;
        private String countryCode;
        private String openId;
        private String scanId;
        private String scanType;
    }
}