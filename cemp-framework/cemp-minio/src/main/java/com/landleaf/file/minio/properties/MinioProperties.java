package com.landleaf.file.minio.properties;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * minio 属性值
 *
 * @author 张力方
 */
@Component
@Data
@ConfigurationProperties(prefix = "cemp.minio")
public class MinioProperties {

    /**
     * 连接url
     */
    private String endpoint;
    /**
     * 端口
     */
    private Integer port;
    /**
     * 用户名
     */
    private String accesskey;
    /**
     * 密码
     */
    private String secretKey;
    /**
     * 文件下载地址
     * <p>
     * 配置为服务地址
     * 因为目前的设计是的下载走自己的服务，不直接去minio下载。
     */
    private String downloadUrl;

}
