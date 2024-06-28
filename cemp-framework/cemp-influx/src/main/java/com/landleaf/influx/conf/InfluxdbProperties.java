package com.landleaf.influx.conf;

import lombok.Data;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author eason
 */
@Data
@ConfigurationProperties(prefix = "spring.influx")
public class InfluxdbProperties {
    private String url;
    private String token;
    private String bucket;
    private String org;
}
