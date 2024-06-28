package com.landleaf.bms.domain.dto;

import lombok.Data;

@Data
public class GatewayDeviceIdRelationDTO {
    private String bizDeviceId;
    private String bizProductId;
    private String bizGatewayId;
    private String sourceDeviceId;
}
