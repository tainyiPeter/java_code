package com.example.wechat.controller;

import com.example.wechat.model.*;
import com.example.wechat.service.WechatUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/weixin/user")
public class WechatUserController {

    private static final Logger logger = LoggerFactory.getLogger(WechatUserController.class);

    @Autowired
    private WechatUserService wechatUserService;

    /**
     * 微信小程序登录接口
     * POST /weixin/user/loginCode
     */
    @PostMapping("/loginCode")
    public LoginResponse login(@RequestBody LoginRequest request) {
        logger.info("接收到登录请求: {}", request);
        return wechatUserService.login(request);
    }

    /**
     * 解密手机号接口
     * POST /weixin/user/decryptPhone
     */
    @PostMapping("/decryptPhone")
    public DecryptPhoneResponse decryptPhone(@RequestBody DecryptPhoneRequest request) {
        logger.info("接收到解密请求: openId={}, scanId={}",
                request.getOpenId(), request.getScanId());
        return wechatUserService.decryptPhone(request);
    }
}