package com.banking.repository;

import com.banking.model.dto.UtilityPayment;
import com.banking.model.entity.UtilityPaymentEntity;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UtilityPaymentRepository extends JpaRepository<UtilityPaymentEntity, UtilityPayment> {
}
