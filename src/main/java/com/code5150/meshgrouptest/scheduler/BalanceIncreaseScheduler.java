package com.code5150.meshgrouptest.scheduler;

import com.code5150.meshgrouptest.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.stream.Gatherers;

@Slf4j
@Component
@RequiredArgsConstructor
public class BalanceIncreaseScheduler {

    private static final BigDecimal INCREASE_RATE = new BigDecimal("1.10");
    private static final BigDecimal MAX_MULTIPLIER = new BigDecimal("2.07");

    private final AccountService accountService;
    private final ExecutorService balanceIncreasePool;

    @Value("${scheduler.batchSize}")
    private int batchSize;

    @Scheduled(fixedRateString = "${scheduler.rate}")
    public void increaseBalances() {
        accountService.getAllAccountIds().stream().gather(Gatherers.windowFixed(batchSize))
                .forEach(accountIds -> CompletableFuture.runAsync(
                        () -> accountService.increaseBalances(accountIds, INCREASE_RATE, MAX_MULTIPLIER),
                        balanceIncreasePool
                ).exceptionally(e -> {
                    log.warn("Failed to increase balance", e);
                    return null;
                }));
    }
}