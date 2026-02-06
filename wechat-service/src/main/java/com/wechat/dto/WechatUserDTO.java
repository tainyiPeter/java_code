package com.wechat.dto;

import lombok.Data;
import java.util.List;

@Data
public class WechatUserDTO {
    private String openId;
    private String nickname;
    private Integer sex;
    private String province;
    private String city;
    private String country;
    private String headImgUrl;
    private List<String> privilege;
    private String unionId;
}