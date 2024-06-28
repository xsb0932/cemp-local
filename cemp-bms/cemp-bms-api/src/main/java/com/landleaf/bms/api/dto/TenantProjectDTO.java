package com.landleaf.bms.api.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TenantProjectDTO {
    private String bizProjectId;
    private String name;
    private String bizType;
    private BigDecimal area;
}
