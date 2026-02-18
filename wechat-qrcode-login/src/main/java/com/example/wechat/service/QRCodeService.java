package com.example.wechat.service;

import com.example.wechat.util.QRCodeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.UUID;

import java.io.OutputStream;

@Service
public class QRCodeService {

    @Autowired
    private QRCodeUtil qrCodeUtil;

    @Value("${qrcode.expire-minutes:5}")
    private int expireMinutes;


    // 内存存储：token -> 状态信息
    private final Map<String, Map<String, Object>> tokenStore = new ConcurrentHashMap<>();

    // 定时清理过期 token 的线程池
    private final ScheduledExecutorService cleaner = Executors.newSingleThreadScheduledExecutor();

    @PostConstruct
    public void init() {
        // 每分钟执行一次清理
        cleaner.scheduleAtFixedRate(this::cleanExpiredTokens, 1, 1, TimeUnit.MINUTES);
    }

    @PreDestroy
    public void destroy() {
        cleaner.shutdown();
    }

    /**
     * 生成登录 token 并存储到内存
     */
    public String generateLoginToken() {
        String loginToken = UUID.randomUUID().toString().replace("-", "");

        Map<String, Object> tokenInfo = new ConcurrentHashMap<>();
        tokenInfo.put("status", "pending");
        tokenInfo.put("createTime", System.currentTimeMillis());

        tokenStore.put(loginToken, tokenInfo);

        return loginToken;
    }

    /**
     * 生成二维码图片字节数组
     */
    public byte[] generateQRCodeImage(String loginToken) {
        String qrContent = "pages/scan/scan?token=" + loginToken;
        //String qrContent = "https://91qj1470uc04.vicp.fun/api/qrcode/pages/scan/scan?token=" + loginToken;
        return qrCodeUtil.createQRCode(qrContent);
    }

    /**
     * 根据 token 获取存储的信息
     */
    public Map<String, Object> getTokenInfo(String loginToken) {
        return tokenStore.get(loginToken);
    }

    /**
     * 更新 token 信息
     */
    public void updateTokenInfo(String loginToken, Map<String, Object> info) {
        tokenStore.put(loginToken, info);
    }

    /**
     * 移除 token
     */
    public void removeToken(String loginToken) {
        tokenStore.remove(loginToken);
    }

    /**
     * 清理过期 token（超过 expireMinutes 分钟）
     */
    private void cleanExpiredTokens() {
        long now = System.currentTimeMillis();
        long expireMillis = expireMinutes * 60 * 1000L;

        tokenStore.entrySet().removeIf(entry -> {
            Map<String, Object> info = entry.getValue();
            Long createTime = (Long) info.get("createTime");
            return createTime != null && (now - createTime) > expireMillis;
        });
    }

    /*
    * --------------------------------------------新的位置-----------------------------------
    */
    public void generateMiniProgramQr(String deviceId, OutputStream outputStream) {
       qrCodeUtil.generateMiniProgramQr(deviceId, outputStream);
    }
}