package com.banking.model.dto;

import com.banking.model.AccountStatus;
import com.banking.model.AccountType;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class BankAccount {

    private Long id;
    private String number;
    private AccountType type;
    private AccountStatus status;
    private BigDecimal availableBalance;
    private BigDecimal actualBalance;
    private User user;

}
