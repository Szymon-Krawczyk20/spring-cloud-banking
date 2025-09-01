package com.banking.model.repository;

import com.banking.model.entity.FundTransferEntity;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FundTransferRepository extends JpaRepository<FundTransferEntity, Long> {
}
