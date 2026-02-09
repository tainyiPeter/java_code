// AccessTokenResponse.java
package com.wechat.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AccessTokenResponse {
    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("expires_in")
    private Integer expiresIn;

    @JsonProperty("refresh_token")
    private String refreshToken;

    private String openid;
    private String scope;

    @JsonProperty("is_snapshotuser")
    private Integer isSnapshotUser;

    @JsonProperty("unionid")
    private String unionId;

    @JsonProperty("errcode")
    private Integer errCode;

    @JsonProperty("errmsg")
    private String errMsg;

    public boolean isSuccess() {
        return errCode == null || errCode == 0;
    }
}