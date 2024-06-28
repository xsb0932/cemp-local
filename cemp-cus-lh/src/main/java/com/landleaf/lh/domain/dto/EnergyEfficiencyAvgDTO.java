package com.landleaf.lh.domain.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class EnergyEfficiencyAvgDTO {
    private BigDecimal total;
    private BigDecimal area;
    private BigDecimal count;

    public BigDecimal getAreaIfNull() {
        if (null == area || BigDecimal.ZERO.compareTo(area) == 0) {
            return BigDecimal.ONE;
        } else {
            return area;
        }
    }
}
