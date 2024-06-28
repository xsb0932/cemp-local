package com.landleaf.data.api.device.dto;

import lombok.Data;

import java.util.Map;

/**
 * 设备当前属性值
 *
 * @author Yang
 */
@Data
public class DeviceCurrentDTO {
    /**
     * 设备唯一id
     */
    private String bizDeviceId;
    /**
     * 当前属性值
     */
    private Map<String, Object> current;
}
