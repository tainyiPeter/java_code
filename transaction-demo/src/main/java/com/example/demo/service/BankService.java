package com.example.demo.service;

import com.example.demo.dao.AccountDao;
import com.example.demo.entity.Account;
import com.example.demo.entity.TransferRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class BankService {

    // 使用Logger
    private static final Logger logger = LoggerFactory.getLogger(BankService.class);

    @Autowired
    private AccountDao accountDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 转账业务方法 - 演示事务
     */
    @Transactional
    public boolean transfer(String fromAccount, String toAccount, BigDecimal amount) {
        logger.info("[事务开始] 转账: {} → {} 金额: ¥{}", fromAccount, toAccount, amount);

        try {
            // 1. 检查转出账户
            Account from = accountDao.findByAccountNumber(fromAccount);
            if (from == null) {
                logger.error("转出账户不存在: {}", fromAccount);
                throw new RuntimeException("转出账户不存在: " + fromAccount);
            }

            // 2. 检查转入账户
            Account to = accountDao.findByAccountNumber(toAccount);
            if (to == null) {
                logger.error("转入账户不存在: {}", toAccount);
                throw new RuntimeException("转入账户不存在: " + toAccount);
            }

            // 3. 检查余额
            BigDecimal fromBalance = accountDao.getBalance(fromAccount);
            if (fromBalance.compareTo(amount) < 0) {
                logger.warn("余额不足! 账户: {}, 当前余额: ¥{}, 需要: ¥{}",
                        fromAccount, fromBalance, amount);
                throw new RuntimeException("余额不足! 账户: " + fromAccount +
                        ", 当前余额: ¥" + fromBalance);
            }

            // 4. 扣款
            BigDecimal newFromBalance = fromBalance.subtract(amount);
            accountDao.updateBalance(fromAccount, newFromBalance);
            logger.info("[扣款成功] {} 新余额: ¥{}", fromAccount, newFromBalance);

            // 5. 存款
            BigDecimal toBalance = accountDao.getBalance(toAccount);
            BigDecimal newToBalance = toBalance.add(amount);
            accountDao.updateBalance(toAccount, newToBalance);
            logger.info("[存款成功] {} 新余额: ¥{}", toAccount, newToBalance);

            // 6. 记录
            TransferRecord record = new TransferRecord(fromAccount, toAccount, amount);
            accountDao.insertTransferRecord(record);
            logger.info("[记录保存] 转账完成，记录ID: {}", record.getId());

            return true;

        } catch (Exception e) {
            logger.error("[事务异常] 转账失败", e);
            throw e;
        }
    }

    /**
     * 查询余额 - 只读事务
     */
    @Transactional(readOnly = true)
    public BigDecimal getBalance(String accountNumber) {
        logger.debug("查询账户余额: {}", accountNumber);
        return accountDao.getBalance(accountNumber);
    }

    /**
     * 查询所有账户 - 只读事务
     */
    @Transactional(readOnly = true)
    public List<Account> getAllAccounts() {
        logger.debug("查询所有账户");
        return accountDao.findAllAccounts();
    }

    /**
     * 查询转账记录 - 只读事务
     */
    @Transactional(readOnly = true)
    public List<TransferRecord> getTransferHistory(String accountNumber) {
        logger.debug("查询转账记录: {}", accountNumber);
        return accountDao.findTransferRecords(accountNumber);
    }

    /**
     * 创建账户
     */
    @Transactional
    public void createAccount(String accountNumber, String accountName, BigDecimal balance) {
        logger.info("[创建账户] {} - {} 初始金额: ¥{}", accountNumber, accountName, balance);

        // 检查是否已存在
        if (accountDao.findByAccountNumber(accountNumber) != null) {
            logger.error("账户已存在: {}", accountNumber);
            throw new RuntimeException("账户已存在: " + accountNumber);
        }

        // 插入新账户
        String sql = "INSERT INTO account(account_number, account_name, balance) VALUES(?, ?, ?)";
        jdbcTemplate.update(sql, accountNumber, accountName, balance);
        logger.info("[账户创建成功] {}", accountNumber);
    }

    /**
     * 演示事务回滚
     */
    @Transactional
    public void testTransactionRollback(String fromAccount, String toAccount, BigDecimal amount) {
        logger.info("[测试事务回滚] 从 {} 转账到 {} 金额: ¥{}", fromAccount, toAccount, amount);

        // 正常扣款
        BigDecimal fromBalance = accountDao.getBalance(fromAccount);
        accountDao.updateBalance(fromAccount, fromBalance.subtract(amount));
        logger.info("[扣款成功]");

        // 模拟异常
        if (amount.compareTo(new BigDecimal("10000")) > 0) {
            logger.error("触发测试异常: 转账金额超过限制!");
            throw new RuntimeException("测试异常: 转账金额超过限制!");
        }

        // 这行不会执行
        BigDecimal toBalance = accountDao.getBalance(toAccount);
        accountDao.updateBalance(toAccount, toBalance.add(amount));
    }
}