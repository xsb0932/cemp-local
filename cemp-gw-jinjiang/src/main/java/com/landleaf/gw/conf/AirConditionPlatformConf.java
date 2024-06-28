package com.landleaf.gw.conf;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "cemp.air-condition")
@Validated
@Data
public class AirConditionPlatformConf {

    /**
     * 是否可用
     */
    private boolean enabled = false;

    /**
     * token请求地址
     */
    private String tokenUrl;

    /**
     * device请求地址
     */
    private String deviceUrl;

    /**
     * 设备写入url
     */
    private String deviceWriteUrl;

    /**
     * 用户名
     */
    private String appKey;

    /**
     * 密码
     */
    private String appSecret;

    /**
     * 授权类型
     */
    private String grantType;

    /**
     * 超时，单位毫秒
     */
    private int timeout = 60 * 1000;

    /**
     * 是否发送写指令
     */
    private int sendCmd = 0;
}
