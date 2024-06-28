package com.landleaf.energy.domain.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MeterImportDataDTO {
    private String bizDeviceId;
    private String deviceName;
    private BigDecimal total;
    private BigDecimal start;
    private BigDecimal end;
}
