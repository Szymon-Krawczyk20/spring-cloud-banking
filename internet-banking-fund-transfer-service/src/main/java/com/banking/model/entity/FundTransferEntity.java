package com.banking.model.entity;

import com.banking.model.TransactionStatus;
import com.banking.model.dto.AuditAware;

import java.math.BigDecimal;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "fund_transfer")
public class FundTransferEntity extends AuditAware {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String transactionReference;
    private String fromAccount;
    private String toAccount;
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

}
