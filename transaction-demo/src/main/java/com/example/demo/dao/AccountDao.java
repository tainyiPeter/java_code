package com.example.demo.dao;

import com.example.demo.entity.Account;
import com.example.demo.entity.TransferRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public class AccountDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // 根据账号查询账户
    public Account findByAccountNumber(String accountNumber) {
        String sql = "SELECT * FROM account WHERE account_number = ?";
        try {
            return jdbcTemplate.queryForObject(sql,
                    new BeanPropertyRowMapper<>(Account.class), accountNumber);
        } catch (Exception e) {
            return null;
        }
    }

    // 更新账户余额
    public int updateBalance(String accountNumber, BigDecimal newBalance) {
        String sql = "UPDATE account SET balance = ? WHERE account_number = ?";
        return jdbcTemplate.update(sql, newBalance, accountNumber);
    }

    // 插入转账记录
    public int insertTransferRecord(TransferRecord record) {
        String sql = "INSERT INTO transfer_record(from_account, to_account, amount, status, create_time) " +
                "VALUES(?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql,
                record.getFromAccount(),
                record.getToAccount(),
                record.getAmount(),
                record.getStatus(),
                record.getCreateTime());
    }

    // 获取账户余额
    public BigDecimal getBalance(String accountNumber) {
        String sql = "SELECT balance FROM account WHERE account_number = ?";
        try {
            return jdbcTemplate.queryForObject(sql, BigDecimal.class, accountNumber);
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    // 查询所有账户
    public List<Account> findAllAccounts() {
        String sql = "SELECT * FROM account ORDER BY id";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Account.class));
    }

    // 查询转账记录
    public List<TransferRecord> findTransferRecords(String accountNumber) {
        String sql = "SELECT * FROM transfer_record WHERE from_account = ? OR to_account = ? ORDER BY create_time DESC";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(TransferRecord.class), accountNumber, accountNumber);
    }
}