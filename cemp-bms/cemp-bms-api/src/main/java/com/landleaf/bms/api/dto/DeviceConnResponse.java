package com.landleaf.bms.api.dto;

import lombok.Data;

@Data
public class DeviceConnResponse {

    /**
     * bizDeviceId
     */
    private String bizDeviceId;

    /**
     * 产品编号
     */
    private Long prodId;

    /**
     * 连接超时时间
     */
    private Double timeout;
}
