package com.example.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    private Long id;
    private String accountNumber;
    private String accountName;
    private BigDecimal balance;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class TransferRecord {
    private Long id;
    private String fromAccount;
    private String toAccount;
    private BigDecimal amount;
    private Integer status; // 1:成功, 0:失败
    private String remark;
    private LocalDateTime createTime;
}