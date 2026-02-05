// 文件名：SimpleQRCodeTest.java
package com.qrapp;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import org.junit.Test;

import java.io.File;
import java.nio.file.Paths;

public class SimpleQRCodeTest {

    @Test
    public void generateQRCode() {
        try {
            System.out.println("开始生成二维码...");

            // 二维码内容
            String content = "https://lianchenglaoli.com";
            String filePath = "d:/tmp/simple_qr.png";

            // 创建目录
            File dir = new File("d:/tmp");
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // 生成二维码
            BitMatrix bitMatrix = new MultiFormatWriter()
                    .encode(content, BarcodeFormat.QR_CODE, 300, 300);

            // 保存文件
            MatrixToImageWriter.writeToPath(bitMatrix, "PNG", Paths.get(filePath));

            System.out.println("二维码生成成功: " + filePath);

        } catch (Exception e) {
            System.err.println("生成失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}