package com.landleaf.messaging.domain.bo;

import lombok.Data;

import java.util.Map;

@Data
public class DeviceServiceBO {
    private String gateId;
    private String pkId;
    private String sourceDevId;
    private Long time;
    private Map<String, Map<String, Object>> services;
}
