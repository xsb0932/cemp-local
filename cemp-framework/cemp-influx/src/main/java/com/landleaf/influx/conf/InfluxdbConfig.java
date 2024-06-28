package com.landleaf.influx.conf;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.InfluxDBClientOptions;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @author eason
 */
@Data
@Configuration
@EnableConfigurationProperties(InfluxdbProperties.class)
public class InfluxdbConfig {
    @Autowired
    private InfluxdbProperties influxdbProperties;

    @Bean
    public InfluxDBClient influxdbClient() {
        InfluxDBClient client = InfluxDBClientFactory.create(influxdbClientOptions());
        // 服务关闭时关闭连接
        Runtime.getRuntime().addShutdownHook(new Thread(client::close));
        return client;
    }

    private InfluxDBClientOptions influxdbClientOptions() {
        return InfluxDBClientOptions.builder()
                .url(influxdbProperties.getUrl())
                .org(influxdbProperties.getOrg())
                .bucket(influxdbProperties.getBucket())
                .authenticateToken(influxdbProperties.getToken().toCharArray())
                .build();
    }
}
