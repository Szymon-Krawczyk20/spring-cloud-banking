package com.banking.repository;

import com.banking.model.entity.TransactionEntity;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {
}
