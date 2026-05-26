package com.code5150.meshgrouptest.service;

import com.code5150.meshgrouptest.dto.TransferRequest;
import com.code5150.meshgrouptest.entity.Account;
import com.code5150.meshgrouptest.exception.InsufficientFundsException;
import com.code5150.meshgrouptest.exception.ResourceNotFoundException;
import com.code5150.meshgrouptest.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    private static Account buildAccount(Long id, String balance, String initialBalance) {
        return Account.builder()
                .id(id)
                .balance(new BigDecimal(balance))
                .initialBalance(new BigDecimal(initialBalance))
                .build();
    }

    @Test
    void shouldTransferSuccessfully() {
        Account from = buildAccount(1L, "200.00", "100.00");
        Account to = buildAccount(2L, "100.00", "50.00");
        TransferRequest request = new TransferRequest(2L, new BigDecimal("50.00"));

        when(accountRepository.findByUserIdWithLock(1L)).thenReturn(Optional.of(from));
        when(accountRepository.findByUserIdWithLock(2L)).thenReturn(Optional.of(to));

        accountService.transfer(1L, request);

        assertEquals(new BigDecimal("150.00"), from.getBalance());
        assertEquals(new BigDecimal("150.00"), to.getBalance());
        verify(accountRepository).save(from);
        verify(accountRepository).save(to);
    }

    @Test
    void shouldThrowWhenTransferToSelf() {
        TransferRequest request = new TransferRequest(1L, new BigDecimal("50.00"));

        assertThrows(IllegalArgumentException.class, () -> accountService.transfer(1L, request));
        verify(accountRepository, never()).findByUserIdWithLock(any());
    }

    @Test
    void shouldThrowWhenInsufficientFunds() {
        Account from = buildAccount(1L, "30.00", "100.00");
        Account to = buildAccount(2L, "50.00", "50.00");
        TransferRequest request = new TransferRequest(2L, new BigDecimal("50.00"));

        when(accountRepository.findByUserIdWithLock(1L)).thenReturn(Optional.of(from));
        when(accountRepository.findByUserIdWithLock(2L)).thenReturn(Optional.of(to));

        assertThrows(InsufficientFundsException.class, () -> accountService.transfer(1L, request));
        verify(accountRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenSenderNotFound() {
        TransferRequest request = new TransferRequest(2L, new BigDecimal("50.00"));

        when(accountRepository.findByUserIdWithLock(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> accountService.transfer(1L, request));
    }

    @Test
    void shouldThrowWhenReceiverNotFound() {
        Account from = buildAccount(1L, "200.00", "100.00");
        TransferRequest request = new TransferRequest(2L, new BigDecimal("50.00"));

        when(accountRepository.findByUserIdWithLock(1L)).thenReturn(Optional.of(from));
        when(accountRepository.findByUserIdWithLock(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> accountService.transfer(1L, request));
    }
}
