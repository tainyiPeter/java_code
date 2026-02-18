package com.example.wechat.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.google.zxing.WriterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.io.OutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;

@Component
public class QRCodeUtil {

    private static final Logger logger = LoggerFactory.getLogger(QRCodeUtil.class);

    @Value("${qrcode.width:300}")
    private int width;

    @Value("${qrcode.height:300}")
    private int height;

    @Value("${miniprogram.qrcode-rule}")
    private String qrcodeRule;

    @Value("${miniprogram.page-path}")
    private String pagePath;

    /**
     * 生成二维码字节数组
     * @param content 二维码内容
     * @return 二维码图片字节数组
     */
    public byte[] createQRCode(String content) {
        return createQRCode(content, width, height);
    }

    /**
     * 生成二维码字节数组（可指定宽高）
     */
    public byte[] createQRCode(String content, int width, int height) {
        try {
            logger.info("[test] create QRCode:{}", content);
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
            hints.put(EncodeHintType.MARGIN, 1);

            BitMatrix bitMatrix = new MultiFormatWriter().encode(
                    content, BarcodeFormat.QR_CODE, width, height, hints
            );

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);

            return outputStream.toByteArray();

        } catch (Exception e) {
            logger.error("生成二维码失败", e);
            throw new RuntimeException("生成二维码失败", e);
        }
    }
    /*
    * ------------------------------------------新的应用---------------------------------------------
    */
    /**
     * 生成跳转到小程序的二维码
     * @param deviceId 设备ID（用于追踪）
     * @param outputStream 输出流
     */
    public void generateMiniProgramQr(String deviceId, OutputStream outputStream) {
        try {
            // 1. 构建完整的URL（基于已配置的规则）
            String qrContent = buildQrContent(deviceId);
            logger.info("生成二维码内容：{}", qrContent);

            // 2. 生成二维码图片
            BufferedImage qrImage = createQrImage(qrContent, 300, 300);

            // 3. 可选：添加底部文字说明
            BufferedImage finalImage = addBottomText(qrImage, "微信扫码绑定设备");

            // 4. 输出到响应流
            ImageIO.write(finalImage, "PNG", outputStream);

        } catch (Exception e) {
            logger.error("生成二维码失败", e);
            throw new RuntimeException("二维码生成失败", e);
        }
    }

    /**
     * 构建二维码内容
     * 使用已配置的规则地址，可以附加参数
     */
    private String buildQrContent(String deviceId) {
        StringBuilder url = new StringBuilder(qrcodeRule);

        // 不要 移除末尾可能的斜杠
//        // 移除末尾可能的斜杠
//        if (url.charAt(url.length() - 1) == '/') {
//            url.deleteCharAt(url.length() - 1);
//        }

        // 添加查询参数（可选）
        url.append("?deviceId=").append(deviceId);
        url.append("&timestamp=").append(System.currentTimeMillis());
        url.append("&source=springboot");

        return url.toString();
    }

    /**
     * 创建二维码图片
     */
    private BufferedImage createQrImage(String content, int width, int height)
            throws WriterException {
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
        hints.put(EncodeHintType.MARGIN, 1);

        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, width, height, hints);

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
            }
        }

        return image;
    }

    /**
     * 添加底部文字说明
     */
    private BufferedImage addBottomText(BufferedImage qrImage, String text) {
        int qrWidth = qrImage.getWidth();
        int qrHeight = qrImage.getHeight();
        int textHeight = 30;

        // 创建新图片（二维码 + 底部文字区域）
        BufferedImage newImage = new BufferedImage(
                qrWidth,
                qrHeight + textHeight,
                BufferedImage.TYPE_INT_RGB
        );

        Graphics2D g = newImage.createGraphics();

        // 设置背景色
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, qrWidth, qrHeight + textHeight);

        // 绘制二维码
        g.drawImage(qrImage, 0, 0, null);

        // 绘制文字
        g.setColor(Color.BLACK);
        g.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));

        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int x = (qrWidth - textWidth) / 2;
        int y = qrHeight + (textHeight + fm.getAscent()) / 2;

        g.drawString(text, x, y);
        g.dispose();

        return newImage;
    }

    /**
     * 将二维码转换为字节数组
     */
    public byte[] generateQrAsBytes(String deviceId) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        generateMiniProgramQr(deviceId, baos);
        return baos.toByteArray();
    }
}