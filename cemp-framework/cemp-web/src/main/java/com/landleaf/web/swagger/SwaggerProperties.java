package com.landleaf.web.swagger;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "swagger")
public class SwaggerProperties {
    private String title;

    private String description;

    private String version;

    private String groupName;

    private String packageName;
}
