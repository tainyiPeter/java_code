package com.example.wechat.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

@Component
public class QRCodeUtil {

    private static final Logger logger = LoggerFactory.getLogger(QRCodeUtil.class);

    @Value("${qrcode.width:300}")
    private int width;

    @Value("${qrcode.height:300}")
    private int height;

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
}