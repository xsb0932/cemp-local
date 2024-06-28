package com.landleaf.file.config;

import com.landleaf.file.minio.properties.MinioProperties;
import io.minio.MinioClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MinioConifg
 *
 * @author 张力方
 * @since 2023/6/12
 **/
@Configuration
@EnableConfigurationProperties(MinioProperties.class)
public class MinioConfig {
    private final MinioProperties minioProperties;

    public MinioConfig(MinioProperties minioProperties) {
        this.minioProperties = minioProperties;
    }

    @Bean
    @ConditionalOnMissingBean
    public MinioClient minioClient() {
        MinioClient minioClient = null;
        try {
            minioClient = MinioClient.builder()
                    .endpoint(minioProperties.getEndpoint(), minioProperties.getPort(), false)
                    .credentials(minioProperties.getAccesskey(), minioProperties.getSecretKey())
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return minioClient;
    }
}
