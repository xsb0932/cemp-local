package com.landleaf.influx.conf;

import lombok.Data;
import org.influxdb.BatchOptions;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author Lokiy
 * @date 2021/11/17
 * @description influxdb配置
 **/
@Data
@ConfigurationProperties(prefix = "spring.influx")
@Component
public class InfluxdbConfig {

    private String url;
    private String user;
    private String password;
    private String database;
    private String retentionPolicy;

    private final int coreNum = Runtime.getRuntime().availableProcessors();

    @Bean
    public InfluxDB init() {
        InfluxDB client = InfluxDBFactory.connect(url, user, password)
                .setDatabase(database)
                .setRetentionPolicy(retentionPolicy)
                .enableBatch(BatchOptions.DEFAULTS
                        .actions(1000)
                        .flushDuration(1000)
                        .jitterDuration(3)
                        .bufferLimit(10000)
                        .threadFactory(influxdbExecutor()));
        //服务关闭时，关闭连接
        Runtime.getRuntime().addShutdownHook(new Thread(client::close));
        return client;
    }

    /**
     * influxdb client thread pool
     *
     * @return threadPoolTaskExecutor
     */
    public ThreadPoolTaskExecutor influxdbExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(coreNum);
        executor.setMaxPoolSize(coreNum * 2);
        executor.setQueueCapacity(100);
        executor.setKeepAliveSeconds(60);
        executor.setDaemon(true);
        executor.setThreadNamePrefix("influxdb-client-thread");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        return executor;
    }
}
