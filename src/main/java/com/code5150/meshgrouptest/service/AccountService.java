package com.code5150.meshgrouptest.service;

import com.code5150.meshgrouptest.dto.TransferRequest;
import com.code5150.meshgrouptest.entity.Account;
import com.code5150.meshgrouptest.exception.InsufficientFundsException;
import com.code5150.meshgrouptest.exception.ResourceNotFoundException;
import com.code5150.meshgrouptest.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    @Transactional
    public void transfer(Long fromUserId, TransferRequest request) {
        Long toUserId = request.getToUserId();
        BigDecimal amount = request.getValue();

        if (fromUserId.equals(toUserId)) {
            throw new IllegalArgumentException("Cannot transfer to self");
        }

        Account fromAccount = accountRepository.findByUserIdWithLock(fromUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Sender account not found"));
        Account toAccount = accountRepository.findByUserIdWithLock(toUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Receiver account not found"));

        if (fromAccount.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient funds: balance="
                    + fromAccount.getBalance() + ", requested=" + amount);
        }

        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
        toAccount.setBalance(toAccount.getBalance().add(amount));

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        log.info("Transfer completed: {} -> {}, amount: {}", fromUserId, toUserId, amount);
    }

    @Transactional(readOnly = true)
    public List<Long> getAllAccountIds() {
        return accountRepository.findAllIds();
    }

    @Transactional
    public void increaseBalances(List<Long> accountIds, BigDecimal rate, BigDecimal maxMultiplier) {
        if (accountIds != null && !accountIds.isEmpty()) {
            for (Long accountId : accountIds) {
                accountRepository.findByIdWithLock(accountId).ifPresent(account -> {
                    BigDecimal balance = account.getBalance();
                    BigDecimal initial = account.getInitialBalance();
                    BigDecimal cap = initial.multiply(maxMultiplier).setScale(2, RoundingMode.HALF_UP);

                    if (balance.compareTo(cap) > 0) {
                        return;
                    }

                    BigDecimal newBalance = balance.multiply(rate).setScale(2, RoundingMode.HALF_UP);
                    if (newBalance.compareTo(cap) > 0) {
                        newBalance = cap;
                    }

                    account.setBalance(newBalance);
                    accountRepository.save(account);
                });
            }
        }

    }
}
