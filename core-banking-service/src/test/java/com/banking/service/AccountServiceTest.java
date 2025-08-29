package com.banking.service;

import com.banking.exception.EntityNotFoundException;
import com.banking.model.dto.BankAccount;
import com.banking.model.dto.UtilityAccount;
import com.banking.model.entity.BankAccountEntity;
import com.banking.model.entity.UtilityAccountEntity;
import com.banking.repository.BankAccountRepository;
import com.banking.repository.UtilityAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccountServiceTest {

    private BankAccountRepository bankAccountRepository;
    private UtilityAccountRepository utilityAccountRepository;
    private AccountService accountService;

    @BeforeEach
    void setUp() {
        bankAccountRepository = mock(BankAccountRepository.class);
        utilityAccountRepository = mock(UtilityAccountRepository.class);
        accountService = new AccountService(bankAccountRepository, utilityAccountRepository);
    }

    @Test
    void readBankAccount_found() {
        BankAccountEntity entity = new BankAccountEntity();
        entity.setNumber("123");
        when(bankAccountRepository.findByNumber("123")).thenReturn(java.util.Optional.of(entity));

        BankAccount dto = accountService.readBankAccount("123");
        assertNotNull(dto);
    }

    @Test
    void readBankAccount_notFound() {
        when(bankAccountRepository.findByNumber("123")).thenReturn(java.util.Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> accountService.readBankAccount("123"));
    }

    @Test
    void readUtilityAccount_byProvider_found() {
        UtilityAccountEntity entity = new UtilityAccountEntity();
        entity.setProviderName("ProviderA");
        when(utilityAccountRepository.findByProviderName("ProviderA")).thenReturn(java.util.Optional.of(entity));

        UtilityAccount dto = accountService.readUtilityAccount("ProviderA");
        assertNotNull(dto);
    }

    @Test
    void readUtilityAccount_byProvider_notFound() {
        when(utilityAccountRepository.findByProviderName("ProviderA")).thenReturn(java.util.Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> accountService.readUtilityAccount("ProviderA"));
    }

    @Test
    void readUtilityAccount_byId_found() {
        UtilityAccountEntity entity = new UtilityAccountEntity();
        entity.setId(1L);
        when(utilityAccountRepository.findById(1L)).thenReturn(java.util.Optional.of(entity));

        UtilityAccount dto = accountService.readUtilityAccount(1L);
        assertNotNull(dto);
    }

    @Test
    void readUtilityAccount_byId_notFound() {
        when(utilityAccountRepository.findById(1L)).thenReturn(java.util.Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> accountService.readUtilityAccount(1L));
    }
}

