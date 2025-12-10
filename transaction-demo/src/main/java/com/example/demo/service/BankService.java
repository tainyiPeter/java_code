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
     * 转账业务方法
     * 这个方法将演示事务的完整生命周期
     */
    public void transfer(String fromAccount, String toAccount, BigDecimal amount) {
        System.out.println("开始转账业务...");

        // 1. 查询转出账户
        Account from = accountDao.findByAccountNumber(fromAccount);
        System.out.println("查询到转出账户: " + from.getAccountName());

        // 2. 检查余额是否充足
        if (from.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("余额不足");
        }

        // 3. 扣减转出账户余额
        BigDecimal newFromBalance = from.getBalance().subtract(amount);
        accountDao.updateBalance(fromAccount, newFromBalance);
        System.out.println("扣减转出账户余额成功");

        // 4. 增加转入账户余额
        BigDecimal toBalance = accountDao.getBalance(toAccount);
        BigDecimal newToBalance = toBalance.add(amount);
        accountDao.updateBalance(toAccount, newToBalance);
        System.out.println("增加转入账户余额成功");

        // 5. 记录转账记录
        TransferRecord record = new TransferRecord();
        record.setFromAccount(fromAccount);
        record.setToAccount(toAccount);
        record.setAmount(amount);
        record.setStatus(1); // 成功
        record.setRemark("正常转账");
        accountDao.insertTransferRecord(record);
        System.out.println("保存转账记录成功");

        System.out.println("转账业务完成");
    }

    /**
     * 只读查询方法
     * 演示只读事务
     */
    public List<TransferRecord> queryTransferHistory(String accountNumber) {
        System.out.println("查询转账历史...");
        return accountDao.findTransferRecords(accountNumber);
    }

    /**
     * 嵌套事务示例
     * 演示事务传播行为
     */
    public void batchTransfer(List<TransferRequest> requests) {
        System.out.println("开始批量转账...");

        for (TransferRequest request : requests) {
            try {
                // 每个转账操作在独立的事务中
                transferWithNewTransaction(request.getFromAccount(),
                        request.getToAccount(),
                        request.getAmount());
            } catch (Exception e) {
                System.err.println("单笔转账失败: " + e.getMessage());
                // 记录失败，但继续执行其他转账
            }
        }

        System.out.println("批量转账完成");
    }

    /**
     * 使用REQUIRES_NEW传播行为
     * 每次调用都开启新事务
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void transferWithNewTransaction(String fromAccount, String toAccount, BigDecimal amount) {
        transfer(fromAccount, toAccount, amount);
    }

    /**
     * 模拟异常情况
     * 演示事务回滚
     */
    public void transferWithException(String fromAccount, String toAccount, BigDecimal amount) {
        System.out.println("开始带异常的转账...");

        // 正常扣款
        BigDecimal fromBalance = accountDao.getBalance(fromAccount);
        accountDao.updateBalance(fromAccount, fromBalance.subtract(amount));
        System.out.println("扣款成功");

        // 模拟异常
        if (amount.compareTo(new BigDecimal("10000")) > 0) {
            throw new RuntimeException("转账金额过大，触发异常！");
        }

        // 增加收款方余额（如果上面抛出异常，这行不会执行）
        BigDecimal toBalance = accountDao.getBalance(toAccount);
        accountDao.updateBalance(toAccount, toBalance.add(amount));

        System.out.println("转账完成");
    }
}

// 辅助类
class TransferRequest {
    private String fromAccount;
    private String toAccount;
    private BigDecimal amount;

    // 构造器、getter、setter省略
}