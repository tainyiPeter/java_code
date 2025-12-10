package com.example.demo.entity;

import java.math.BigDecimal;
import java.util.Date;

public class TransferRecord {
    private Long id;
    private String fromAccount;
    private String toAccount;
    private BigDecimal amount;
    private Integer status; // 1:成功, 0:失败
    private Date createTime;

    public TransferRecord() {
    }

    public TransferRecord(String fromAccount, String toAccount, BigDecimal amount) {
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.amount = amount;
        this.status = 1;
        this.createTime = new Date();
    }

    // Getter和Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFromAccount() { return fromAccount; }
    public void setFromAccount(String fromAccount) { this.fromAccount = fromAccount; }

    public String getToAccount() { return toAccount; }
    public void setToAccount(String toAccount) { this.toAccount = toAccount; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }

    @Override
    public String toString() {
        return fromAccount + " → " + toAccount + ": ¥" + amount + " (" + createTime + ")";
    }
}