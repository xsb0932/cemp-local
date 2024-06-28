package com.landleaf.redis.dao.dto;

import lombok.Data;

@Data
public class DeviceParameterValueCacheDTO {
    /**
     * 功能标识符
     */
    private String identifier;

    /**
     * 参数值
     */
    private String value;
}
