package com.landleaf.comm.thread;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程池配置
 */
@Configuration
public class ExecutorConfig {
    /**
     * 告警处理线程池
     */
    @Bean
    public Executor warningExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(50);
        executor.setMaxPoolSize(200);
        executor.setQueueCapacity(500);
        executor.setKeepAliveSeconds(100);
        executor.setThreadNamePrefix("WarningExecutor-Thread");
        // 丢弃
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());
        executor.initialize();
        return executor;
    }

    /**
     * 消息处理线程池
     */
    @Bean
    public Executor receiveExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(50);
        executor.setMaxPoolSize(500);
        executor.setQueueCapacity(500);
        executor.setKeepAliveSeconds(100);
        executor.setThreadNamePrefix("WarningExecutor-Thread");
        // 丢弃
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }

    /**
     * 缓存处理线程池
     */
    @Bean
    public Executor cacheExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(9);
        executor.setMaxPoolSize(80);
        executor.setQueueCapacity(1000);
        executor.setKeepAliveSeconds(100);
        executor.setThreadNamePrefix("CacheExecutor-Thread");
        // 主线程执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }

    /**
     * 日志处理线程池
     */
    @Bean
    public Executor logExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(9);
        executor.setMaxPoolSize(80);
        executor.setQueueCapacity(1000);
        executor.setKeepAliveSeconds(100);
        executor.setThreadNamePrefix("LogExecutor-Thread");
        // 主线程执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }

    /**
     * 业务处理线程池
     */
    @Bean
    public Executor businessExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(9);
        executor.setMaxPoolSize(80);
        executor.setQueueCapacity(1000);
        executor.setKeepAliveSeconds(100);
        executor.setThreadNamePrefix("BusinessExecutor-Thread");
        // 主线程执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }
}
