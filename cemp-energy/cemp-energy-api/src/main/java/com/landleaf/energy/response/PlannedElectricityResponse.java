package com.landleaf.energy.response;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 计划用电返回
 *
 * @author xshibai
 * @since 2024/01/29
 **/
@Data
public class PlannedElectricityResponse {

    private Long id;
    private String projectBizId;
    private String year;
    private String month;
    private BigDecimal planElectricityConsumption;

}
