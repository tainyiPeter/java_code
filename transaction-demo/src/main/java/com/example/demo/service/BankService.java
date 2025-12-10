package com.example.demo.service;

import com.example.demo.dao.AccountDao;
import com.example.demo.entity.Account;
import com.example.demo.entity.TransferRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class BankService {

    @Autowired
    private AccountDao accountDao;

    /**
     * 转账业务方法 - 演示事务
     * @Transactional 注解确保整个方法在事务中执行
     */
    @Transactional(rollbackFor = Exception.class)
    public void transfer(String fromAccount, String toAccount, BigDecimal amount) {
        System.out.println("\n[开始转账] " + fromAccount + " → " + toAccount + " 金额: ¥" + amount);

        // 1. 检查转出账户是否存在
        Account from = accountDao.findByAccountNumber(fromAccount);
        if (from == null) {
            throw new RuntimeException("转出账户不存在: " + fromAccount);
        }

        // 2. 检查转入账户是否存在
        Account to = accountDao.findByAccountNumber(toAccount);
        if (to == null) {
            throw new RuntimeException("转入账户不存在: " + toAccount);
        }

        // 3. 检查余额是否充足
        BigDecimal fromBalance = accountDao.getBalance(fromAccount);
        if (fromBalance.compareTo(amount) < 0) {
            throw new RuntimeException("余额不足! 账户: " + fromAccount +
                    ", 当前余额: ¥" + fromBalance +
                    ", 需要: ¥" + amount);
        }

        // 4. 扣减转出账户余额
        BigDecimal newFromBalance = fromBalance.subtract(amount);
        int rows1 = accountDao.updateBalance(fromAccount, newFromBalance);
        System.out.println("[扣款成功] " + fromAccount + " 新余额: ¥" + newFromBalance);

        // 5. 增加转入账户余额
        BigDecimal toBalance = accountDao.getBalance(toAccount);
        BigDecimal newToBalance = toBalance.add(amount);
        int rows2 = accountDao.updateBalance(toAccount, newToBalance);
        System.out.println("[入账成功] " + toAccount + " 新余额: ¥" + newToBalance);

        // 6. 记录转账记录
        TransferRecord record = new TransferRecord(fromAccount, toAccount, amount);
        record.setRemark("正常转账");
        int rows3 = accountDao.insertTransferRecord(record);
        System.out.println("[记录保存] 转账记录ID: " + record.getId());

        System.out.println("[转账完成] 成功!");
    }

    /**
     * 查询账户余额 - 只读事务
     */
    @Transactional(readOnly = true)
    public BigDecimal getBalance(String accountNumber) {
        return accountDao.getBalance(accountNumber);
    }

    /**
     * 查询转账历史 - 只读事务
     */
    @Transactional(readOnly = true)
    public List<TransferRecord> getTransferHistory(String accountNumber) {
        return accountDao.findTransferRecords(accountNumber);
    }

    /**
     * 查询所有账户 - 只读事务
     */
    @Transactional(readOnly = true)
    public List<Account> getAllAccounts() {
        return accountDao.findAllAccounts();
    }

    /**
     * 创建账户 - 需要事务
     */
    @Transactional(rollbackFor = Exception.class)
    public void createAccount(String accountNumber, String accountName, BigDecimal initialBalance) {
        System.out.println("\n[创建账户] " + accountNumber + " - " + accountName + " 初始金额: ¥" + initialBalance);

        // 检查账户是否已存在
        Account existing = accountDao.findByAccountNumber(accountNumber);
        if (existing != null) {
            throw new RuntimeException("账户已存在: " + accountNumber);
        }

        // 插入新账户
        Account newAccount = new Account(accountNumber, accountName, initialBalance);
        String sql = "INSERT INTO account(account_number, account_name, balance, create_time, update_time) " +
                "VALUES(?, ?, ?, NOW(), NOW())";
        jdbcTemplate.update(sql, newAccount.getAccountNumber(),
                newAccount.getAccountName(),
                newAccount.getBalance());

        System.out.println("[账户创建成功]");
    }

    /**
     * 演示事务回滚 - 模拟异常情况
     */
    @Transactional(rollbackFor = Exception.class)
    public void transferWithException(String fromAccount, String toAccount, BigDecimal amount) {
        System.out.println("\n[开始异常测试转账]");

        // 正常扣款
        BigDecimal fromBalance = accountDao.getBalance(fromAccount);
        accountDao.updateBalance(fromAccount, fromBalance.subtract(amount));
        System.out.println("[扣款成功]");

        // 模拟业务异常
        if (amount.compareTo(new BigDecimal("10000")) > 0) {
            throw new RuntimeException("模拟异常: 转账金额超过限制!");
        }

        // 这行代码不会执行，因为上面抛出了异常
        BigDecimal toBalance = accountDao.getBalance(toAccount);
        accountDao.updateBalance(toAccount, toBalance.add(amount));

        System.out.println("[转账完成]");
    }

    /**
     * 嵌套事务演示 - REQUIRES_NEW传播行为
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void transferWithNewTransaction(String fromAccount, String toAccount, BigDecimal amount) {
        transfer(fromAccount, toAccount, amount);
    }

    // 注入JdbcTemplate用于特殊操作
    @Autowired
    private org.springframework.jdbc.core.JdbcTemplate jdbcTemplate;
}