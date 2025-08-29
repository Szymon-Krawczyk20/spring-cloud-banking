package com.banking.service;

import com.banking.exception.EntityNotFoundException;
import com.banking.model.dto.BankAccount;
import com.banking.model.dto.UtilityAccount;
import com.banking.model.entity.BankAccountEntity;
import com.banking.model.entity.UtilityAccountEntity;
import com.banking.model.mapper.BankAccountMapper;
import com.banking.model.mapper.UtilityAccountMapper;
import com.banking.repository.BankAccountRepository;
import com.banking.repository.UtilityAccountRepository;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountService {

    private BankAccountMapper bankAccountMapper = new BankAccountMapper();
    private UtilityAccountMapper utilityAccountMapper = new UtilityAccountMapper();

    private final BankAccountRepository bankAccountRepository;
    private final UtilityAccountRepository utilityAccountRepository;

    public BankAccount readBankAccount(String accountNumber) {
        BankAccountEntity entity = bankAccountRepository.findByNumber(accountNumber).orElseThrow(EntityNotFoundException::new);
        return bankAccountMapper.convertToDto(entity);
    }

    public UtilityAccount readUtilityAccount(String provider) {
        UtilityAccountEntity utilityAccountEntity = utilityAccountRepository.findByProviderName(provider).orElseThrow(EntityNotFoundException::new);
        return utilityAccountMapper.convertToDto(utilityAccountEntity);
    }

    public UtilityAccount readUtilityAccount(Long id) {
        return utilityAccountMapper.convertToDto(utilityAccountRepository.findById(id).orElseThrow(EntityNotFoundException::new));
    }

}
