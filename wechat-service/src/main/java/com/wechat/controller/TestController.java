package com.wechat.controller;

import com.wechat.service.WechatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {

    private final WechatService wechatService;

    @GetMapping("/encoding")
    public String testEncoding() {
        try {
            log.info("start test encoding");
            // 使用反射调用 testEncoding 方法（如果不在接口中）
            wechatService.getClass().getMethod("testEncoding").invoke(wechatService);
            return "编码测试完成，请查看日志";
        } catch (Exception e) {
            return "测试失败: " + e.getMessage();
        }
    }
}