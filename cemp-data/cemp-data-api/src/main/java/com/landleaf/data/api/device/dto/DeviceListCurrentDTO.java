package com.landleaf.data.api.device.dto;

import lombok.Data;

import java.util.Map;

/**
 * 设备监测列表 同一品类查询设备当前状态
 *
 * @author Yang
 */
@Data
public class DeviceListCurrentDTO {
    /**
     * 设备唯一id
     */
    private String bizDeviceId;
    /**
     * 当前属性值
     */
    private Map<String, Object> current;
}
