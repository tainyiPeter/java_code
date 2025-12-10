package com.example.demo;

import com.example.demo.config.AppConfig;
import com.example.demo.service.BankService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AppConfig.class)
@Transactional // 测试完成后自动回滚
public class BankServiceTest {

    @Autowired
    private BankService bankService;

    @Test
    public void testTransferSuccess() {
        // 正常转账测试
        BigDecimal initialBalance1 = bankService.getBalance("1001");
        BigDecimal initialBalance2 = bankService.getBalance("1002");

        bankService.transfer("1001", "1002", new BigDecimal("500.00"));

        BigDecimal finalBalance1 = bankService.getBalance("1001");
        BigDecimal finalBalance2 = bankService.getBalance("1002");

        assertEquals(initialBalance1.subtract(new BigDecimal("500.00")), finalBalance1);
        assertEquals(initialBalance2.add(new BigDecimal("500.00")), finalBalance2);
    }

    @Test(expected = RuntimeException.class)
    public void testTransferInsufficientBalance() {
        // 余额不足测试
        bankService.transfer("1001", "1002", new BigDecimal("99999.00"));
    }

    @Test(expected = RuntimeException.class)
    public void testTransferAccountNotExist() {
        // 账户不存在测试
        bankService.transfer("9999", "1002", new BigDecimal("100.00"));
    }
}