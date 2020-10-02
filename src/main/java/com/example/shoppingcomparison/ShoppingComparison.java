package com.example.shoppingcomparison;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@SpringBootApplication
@EnableAsync
public class ShoppingComparison {
    public static void main(String[] args) {
        SpringApplication.run(ShoppingComparison.class, args);
    }

    int cpuCores = Runtime.getRuntime().availableProcessors();

    @Bean
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(cpuCores);
        executor.setQueueCapacity(1000);
        executor.initialize();
        return executor;
    }
}
