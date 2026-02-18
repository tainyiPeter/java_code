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
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.net.URLEncoder;

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
            logger.info("call generate-token");
            String loginToken = qrCodeService.generateLoginToken();
            return ResponseEntity.ok(ApiResponse.success("生成成功", loginToken));
        } catch (Exception e) {
            logger.error("生成token失败", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("生成失败: " + e.getMessage()));
        }
    }

    /*
    * ----------------------------------------------------------------------------------
     */
    /**
     * 生成设备绑定二维码
     * 访问：/api/qrcode/device?deviceId=DEV001
     */
    @GetMapping(value = "/device", produces = MediaType.IMAGE_PNG_VALUE)
    public void generateDeviceQr(
            @RequestParam String deviceId,
            HttpServletResponse response) {

        try {
            // 设置响应头
            response.setContentType(MediaType.IMAGE_PNG_VALUE);
            response.setHeader("Content-Disposition",
                    "inline; filename=\"" + URLEncoder.encode(deviceId + "_qrcode.png", "UTF-8") + "\"");

            // 生成二维码
            OutputStream os = response.getOutputStream();
            qrCodeService.generateMiniProgramQr(deviceId, os);
            os.flush();
            os.close();

            logger.info("二维码生成成功，deviceId: {}", deviceId);

        } catch (Exception e) {
            logger.error("生成二维码失败", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}