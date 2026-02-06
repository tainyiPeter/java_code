package com.wechat.controller;

import com.wechat.util.WechatUtil;
import com.wechat.service.WechatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/wechat/api")
@RequiredArgsConstructor
public class WechatApiController {

    private final WechatUtil wechatUtil;
    private final WechatService wechatService;

    /**
     * 发送模板消息示例
     */
    @PostMapping("/send-template")
    public String sendTemplateMessage(@RequestParam String openId) {
        String accessToken = wechatService.getAccessToken();

        Map<String, Object> data = new HashMap<>();

        // 第一个字段
        Map<String, String> first = new HashMap<>();
        first.put("value", "您好，您有一个新的订单");
        first.put("color", "#173177");
        data.put("first", first);

        // 订单号
        Map<String, String> orderNo = new HashMap<>();
        orderNo.put("value", "123456789");
        orderNo.put("color", "#173177");
        data.put("orderNo", orderNo);

        // 订单金额
        Map<String, String> amount = new HashMap<>();
        amount.put("value", "99.99元");
        amount.put("color", "#173177");
        data.put("amount", amount);

        // 备注
        Map<String, String> remark = new HashMap<>();
        remark.put("value", "感谢您的使用！");
        remark.put("color", "#173177");
        data.put("remark", remark);

        boolean success = wechatUtil.sendTemplateMessage(
                accessToken,
                openId,
                "你的模板ID", // 实际模板ID
                data,
                "https://example.com/order/123", // 跳转链接
                null // 小程序信息（可选）
        );

        return success ? "发送成功" : "发送失败";
    }

    /**
     * 创建二维码示例
     */
    @GetMapping("/create-qrcode")
    public String createQrCode(@RequestParam String sceneStr) {
        String accessToken = wechatService.getAccessToken();
        String ticket = wechatUtil.createQrCode(accessToken, sceneStr, 0); // 永久二维码

        if (ticket != null) {
            String qrCodeUrl = wechatUtil.getQrCodeUrl(ticket);
            return "二维码生成成功:<br>" +
                    "Ticket: " + ticket + "<br>" +
                    "URL: <a href=\"" + qrCodeUrl + "\">" + qrCodeUrl + "</a>";
        } else {
            return "二维码生成失败";
        }
    }
}