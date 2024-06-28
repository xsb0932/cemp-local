package com.landleaf.jjgj.domain.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 计划用电列表
 *
 * @author Tycoon
 * @since 2023/8/10 16:24
 **/
@Data
@Schema(description = "计划用电列表")
public class PlanElectricityTabulationResponse {

    /**
     * id
     */
    @Schema(description = "id")
    private Long id;

    /**
     * 年份
     */
    @Schema(description = "年份")
    private String year;

    /**
     * 月份
     */
    @Schema(description = "年份")
    private String month;

    /**
     * 计划用电
     */
    @Schema(description = "计划用电")
    private BigDecimal planElectricityConsumption;

    /**
     * 去年计划用电
     */
    @Schema(description = "去年计划用电")
    private BigDecimal lastPlanElectricityConsumption;

    /**
     * 去年实际用电
     */
    @Schema(description = "去年实际用电")
    private BigDecimal lastElectricityConsumption;

}
