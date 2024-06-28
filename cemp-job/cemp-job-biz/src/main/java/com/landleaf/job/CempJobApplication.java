package com.landleaf.job;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author Yang
 */
@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan("com.landleaf.*")
@MapperScan("com.landleaf.job.dal.mapper")
@ConfigurationPropertiesScan
public class CempJobApplication {
    public static void main(String[] args) {
        SpringApplication.run(CempJobApplication.class, args);
    }
}
