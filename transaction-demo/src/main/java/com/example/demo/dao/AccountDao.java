package com.example.demo.dao;

import com.example.demo.entity.Account;
import com.example.demo.entity.TransferRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public class AccountDao {

    private static final Logger logger = LoggerFactory.getLogger(AccountDao.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Account findByAccountNumber(String accountNumber) {
        logger.debug("查询账户: {}", accountNumber);
        String sql = "SELECT * FROM account WHERE account_number = ?";
        try {
            return jdbcTemplate.queryForObject(sql,
                    new BeanPropertyRowMapper<>(Account.class), accountNumber);
        } catch (Exception e) {
            logger.warn("账户不存在: {}", accountNumber);
            return null;
        }
    }

    public int updateBalance(String accountNumber, BigDecimal newBalance) {
        logger.debug("更新账户余额: {} -> ¥{}", accountNumber, newBalance);
        String sql = "UPDATE account SET balance = ? WHERE account_number = ?";
        int rows = jdbcTemplate.update(sql, newBalance, accountNumber);
        logger.debug("更新影响行数: {}", rows);
        return rows;
    }

    public int insertTransferRecord(TransferRecord record) {
        logger.debug("插入转账记录: {} -> {} ¥{}",
                record.getFromAccount(), record.getToAccount(), record.getAmount());
        String sql = "INSERT INTO transfer_record(from_account, to_account, amount, status, create_time) " +
                "VALUES(?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql,
                record.getFromAccount(),
                record.getToAccount(),
                record.getAmount(),
                record.getStatus(),
                record.getCreateTime());
    }

    public BigDecimal getBalance(String accountNumber) {
        logger.debug("查询余额: {}", accountNumber);
        String sql = "SELECT balance FROM account WHERE account_number = ?";
        try {
            return jdbcTemplate.queryForObject(sql, BigDecimal.class, accountNumber);
        } catch (Exception e) {
            logger.warn("查询余额失败，账户可能不存在: {}", accountNumber);
            return BigDecimal.ZERO;
        }
    }

    public List<Account> findAllAccounts() {
        logger.debug("查询所有账户");
        String sql = "SELECT * FROM account ORDER BY id";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Account.class));
    }

    public List<TransferRecord> findTransferRecords(String accountNumber) {
        logger.debug("查询转账记录: {}", accountNumber);
        String sql = "SELECT * FROM transfer_record WHERE from_account = ? OR to_account = ? ORDER BY create_time DESC";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(TransferRecord.class), accountNumber, accountNumber);
    }
}