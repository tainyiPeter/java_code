package com.qrapp.controller;

import com.qrapp.service.QRCodeService;
import com.google.zxing.WriterException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Controller
public class QRCodeController {

    @Autowired
    private QRCodeService qrCodeService;

    @Value("${qrcode.default-url}")
    private String defaultUrl;

    /**
     * 首页 - 显示二维码生成页面
     */
    @GetMapping("/")
    public String index(Model model) throws IOException, WriterException {
        model.addAttribute("title", "微信扫码跳转测试");
        model.addAttribute("defaultUrl", defaultUrl);
        model.addAttribute("qrCodeBase64", qrCodeService.generateQRCodeBase64(defaultUrl));
        model.addAttribute("apiInfo", getApiInfo());
        return "index";
    }

    /**
     * 生成二维码页面
     */
    @GetMapping("/generate")
    public String generateQRCodePage(
            @RequestParam(value = "url", required = false) String url,
            Model model) throws IOException, WriterException {

        String targetUrl = (url != null && !url.trim().isEmpty()) ? url : defaultUrl;

        if (!qrCodeService.isValidUrl(targetUrl)) {
            model.addAttribute("error", "请输入有效的URL（以http://或https://开头）");
            targetUrl = defaultUrl;
        }

        model.addAttribute("title", "二维码生成结果");
        model.addAttribute("url", targetUrl);
        model.addAttribute("qrCodeBase64", qrCodeService.generateQRCodeBase64(targetUrl));
        model.addAttribute("isValid", qrCodeService.isValidUrl(targetUrl));

        return "qr-page";
    }

    /**
     * 获取二维码图片 - 直接返回图片
     */
    @GetMapping(value = "/qrcode/image", produces = MediaType.IMAGE_PNG_VALUE)
    @ResponseBody
    public ResponseEntity<byte[]> getQRCodeImage(
            @RequestParam(value = "content", required = false) String content,
            HttpServletResponse response) throws IOException, WriterException {

        String targetContent = (content != null && !content.trim().isEmpty()) ? content : defaultUrl;

        byte[] imageBytes = qrCodeService.generateQRCodeImage(targetContent);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf(qrCodeService.getContentType()));
        headers.setContentLength(imageBytes.length);
        headers.set("X-QR-Content", targetContent);

        return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
    }

    /**
     * 获取二维码Base64数据（JSON格式）
     */
    @GetMapping("/qrcode/base64")
    @ResponseBody
    public Map<String, Object> getQRCodeBase64(
            @RequestParam(value = "content", required = false) String content) throws IOException, WriterException {

        String targetContent = (content != null && !content.trim().isEmpty()) ? content : defaultUrl;

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("content", targetContent);
        result.put("qrCodeBase64", qrCodeService.generateQRCodeBase64(targetContent));
        result.put("timestamp", System.currentTimeMillis());

        return result;
    }

    /**
     * 健康检查
     */
    @GetMapping("/health")
    @ResponseBody
    public Map<String, Object> health() {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "UP");
        result.put("service", "QR Code Generator");
        result.put("timestamp", System.currentTimeMillis());
        return result;
    }

    /**
     * 获取API信息
     */
    private Map<String, String> getApiInfo() {
        Map<String, String> apiInfo = new HashMap<>();
        apiInfo.put("首页", "GET /");
        apiInfo.put("生成页面", "GET /generate?url={your_url}");
        apiInfo.put("获取图片", "GET /qrcode/image?content={your_content}");
        apiInfo.put("获取Base64", "GET /qrcode/base64?content={your_content}");
        apiInfo.put("健康检查", "GET /health");
        return apiInfo;
    }
}