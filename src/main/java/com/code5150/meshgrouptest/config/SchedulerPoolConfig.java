package com.code5150.meshgrouptest.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

@Configuration
public class SchedulerPoolConfig {
    @Bean(destroyMethod = "shutdown")
    public ExecutorService balanceIncreasePool() {
        var executor = new ThreadPoolExecutor(
                16, 16,
                60L, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(100),
                new ThreadPoolExecutor.CallerRunsPolicy());
        executor.allowCoreThreadTimeOut(true);
        return executor;
    }
}
