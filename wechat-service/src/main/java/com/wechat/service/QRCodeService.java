package com.wechat.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

@Service
public class QRCodeService {

    /**
     * 生成二维码并返回Base64字符串
     */
    public String generateQRCodeBase64(String content, int width, int height) {
        try {
            // 设置编码参数
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(EncodeHintType.MARGIN, 1);

            // 生成二维码矩阵
            BitMatrix bitMatrix = new MultiFormatWriter().encode(
                    content,
                    BarcodeFormat.QR_CODE,
                    width,
                    height,
                    hints
            );

            // 转换为BufferedImage
            BufferedImage image = MatrixToImageWriter.toBufferedImage(bitMatrix);

            // 转换为Base64
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "PNG", baos);
            byte[] bytes = baos.toByteArray();

            return "data:image/png;base64," + Base64Utils.encodeToString(bytes);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 生成微信公众号关注二维码
     */
    public String generateWeChatQRCode(String appId, String secret) {
        try {
            // 1. 获取微信access_token
            String token = getWeChatAccessToken(appId, secret);

            // 2. 创建带参二维码（示例）
            String sceneStr = "user_" + System.currentTimeMillis();
            String qrContent = generateWeChatQRContent(token, sceneStr);

            // 3. 生成二维码图片
            return generateQRCodeBase64(qrContent, 300, 300);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getWeChatAccessToken(String appId, String secret) {
        // 调用微信API获取token
        // 这里需要实际实现HTTP请求
        return "mock_access_token";
    }

    private String generateWeChatQRContent(String token, String sceneStr) {
        // 生成微信公众号二维码内容
        return "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=" + sceneStr;
    }
}