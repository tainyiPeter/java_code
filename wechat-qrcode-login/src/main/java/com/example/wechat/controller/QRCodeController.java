package com.example.wechat.controller;

import com.example.wechat.model.ApiResponse;
import com.example.wechat.model.ScanCallbackRequest;
import com.example.wechat.service.QRCodeService;
import com.example.wechat.service.WechatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/qrcode")
public class QRCodeController {

    private static final Logger logger = LoggerFactory.getLogger(QRCodeController.class);

    @Autowired
    private QRCodeService qrCodeService;

    @Autowired
    private WechatService wechatService;

    /**
     * 1. 生成二维码（返回图片）
     */
    @GetMapping(value = "/generate", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> generateQRCode() {
        try {
            // 生成登录token
            String loginToken = qrCodeService.generateLoginToken();
            logger.info("生成二维码登录token: {}", loginToken);

            // 生成二维码图片
            byte[] qrCodeBytes = qrCodeService.generateQRCodeImage(loginToken);

            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .body(qrCodeBytes);

        } catch (Exception e) {
            logger.error("生成二维码失败", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 2. 获取二维码状态（供前端轮询）
     */
    @GetMapping("/status")
    public ResponseEntity<ApiResponse<?>> checkStatus(@RequestParam String token) {
        try {
            Object status = wechatService.checkQRCodeStatus(token);
            return ResponseEntity.ok(ApiResponse.success(status));
        } catch (Exception e) {
            logger.error("检查二维码状态失败", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("检查失败: " + e.getMessage()));
        }
    }

    /**
     * 3. 小程序扫码回调
     */
    @PostMapping("/scan-callback")
    public ResponseEntity<ApiResponse<String>> scanCallback(@RequestBody ScanCallbackRequest request) {
        try {
            logger.info("收到扫码回调: loginToken={}, code={}",
                    request.getLoginToken(), request.getCode());

            String openid = wechatService.processScanCallback(request);

            return ResponseEntity.ok(ApiResponse.success("扫码成功", openid));

        } catch (Exception e) {
            logger.error("处理扫码回调失败", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 4. 生成二维码并返回token（JSON格式）
     */
    @GetMapping("/generate-token")
    public ResponseEntity<ApiResponse<String>> generateToken() {
        try {
            String loginToken = qrCodeService.generateLoginToken();
            return ResponseEntity.ok(ApiResponse.success("生成成功", loginToken));
        } catch (Exception e) {
            logger.error("生成token失败", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("生成失败: " + e.getMessage()));
        }
    }
}