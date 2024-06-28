package com.landleaf.lh.domain.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MonthMaintenanceAverageDTO {
    private Integer maintenanceMonth;
    private BigDecimal avgNum;
}
