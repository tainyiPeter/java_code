package com.qrapp.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class QRCodeService {

    @Value("${qrcode.width}")
    private int width;

    @Value("${qrcode.height}")
    private int height;

    @Value("${qrcode.image-format}")
    private String imageFormat;

    /**
     * 生成二维码图片字节数组
     */
    public byte[] generateQRCodeImage(String content) throws WriterException, IOException {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("二维码内容不能为空");
        }

        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.MARGIN, 1);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height, hints);

        BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(qrImage, imageFormat, baos);

        return baos.toByteArray();
    }

    /**
     * 生成二维码Base64字符串
     */
    public String generateQRCodeBase64(String content) throws IOException, WriterException {
        byte[] imageBytes = generateQRCodeImage(content);
        return Base64.getEncoder().encodeToString(imageBytes);
    }

    /**
     * 验证URL是否合法
     */
    public boolean isValidUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            return false;
        }
        // 简单验证，实际项目中可能需要更复杂的验证
        return url.startsWith("http://") || url.startsWith("https://");
    }

    /**
     * 获取二维码内容类型
     */
    public String getContentType() {
        return "image/" + imageFormat.toLowerCase();
    }
}