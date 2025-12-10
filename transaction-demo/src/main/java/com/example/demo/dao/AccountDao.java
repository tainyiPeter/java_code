package com.example.demo.dao;

import com.example.demo.entity.Account;
import com.example.demo.entity.TransferRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class AccountDao {

    private static final Logger logger = LoggerFactory.getLogger(AccountDao.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Account findByAccountNumber(String accountNumber) {
        String sql = "SELECT * FROM account WHERE account_number = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new RowMapper<Account>() {
                @Override
                public Account mapRow(ResultSet rs, int rowNum) throws SQLException {
                    Account account = new Account();
                    account.setId(rs.getLong("id"));
                    account.setAccountNumber(rs.getString("account_number"));
                    account.setAccountName(rs.getString("account_name"));
                    account.setBalance(rs.getBigDecimal("balance"));
                    account.setCreateTime(rs.getTimestamp("create_time"));
                    account.setUpdateTime(rs.getTimestamp("update_time"));
                    return account;
                }
            }, accountNumber);
        } catch (Exception e) {
            logger.debug("账户不存在: {}", accountNumber);
            return null;
        }
    }

    public int updateBalance(String accountNumber, BigDecimal newBalance) {
        String sql = "UPDATE account SET balance = ? WHERE account_number = ?";
        return jdbcTemplate.update(sql, newBalance, accountNumber);
    }

    public int insertTransferRecord(TransferRecord record) {
        // MySQL使用NOW()函数
        String sql = "INSERT INTO transfer_record(from_account, to_account, amount, status, remark, create_time) " +
                "VALUES(?, ?, ?, ?, ?, NOW())";
        return jdbcTemplate.update(sql,
                record.getFromAccount(),
                record.getToAccount(),
                record.getAmount(),
                record.getStatus(),
                record.getRemark());
    }

    public BigDecimal getBalance(String accountNumber) {
        String sql = "SELECT balance FROM account WHERE account_number = ?";
        try {
            return jdbcTemplate.queryForObject(sql, BigDecimal.class, accountNumber);
        } catch (Exception e) {
            logger.debug("查询余额失败，账户可能不存在: {}", accountNumber);
            return BigDecimal.ZERO;
        }
    }

    public List<Account> findAllAccounts() {
        String sql = "SELECT * FROM account ORDER BY id";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Account.class));
    }

    public List<TransferRecord> findTransferRecords(String accountNumber) {
        String sql = "SELECT * FROM transfer_record WHERE from_account = ? OR to_account = ? ORDER BY create_time DESC";
        return jdbcTemplate.query(sql, new RowMapper<TransferRecord>() {
            @Override
            public TransferRecord mapRow(ResultSet rs, int rowNum) throws SQLException {
                TransferRecord record = new TransferRecord();
                record.setId(rs.getLong("id"));
                record.setFromAccount(rs.getString("from_account"));
                record.setToAccount(rs.getString("to_account"));
                record.setAmount(rs.getBigDecimal("amount"));
                record.setStatus(rs.getInt("status"));
                record.setRemark(rs.getString("remark"));
                record.setCreateTime(rs.getTimestamp("create_time"));
                return record;
            }
        }, accountNumber, accountNumber);
    }

    // 新增：获取最后插入的ID（MySQL特定）
    public Long getLastInsertId() {
        return jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
    }
}