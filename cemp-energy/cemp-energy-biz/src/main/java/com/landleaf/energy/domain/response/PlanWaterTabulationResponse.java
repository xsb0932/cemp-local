package com.landleaf.energy.domain.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 计划用水列表
 *
 * @author Tycoon
 * @since 2023/8/10 16:24
 **/
@Data
@Schema(description = "计划用水列表")
public class PlanWaterTabulationResponse {

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
     * 计划用水
     */
    @Schema(description = "计划用水")
    private BigDecimal planWaterConsumption;

    /**
     * 去年计划用水
     */
    @Schema(description = "去年计划用水")
    private BigDecimal lastPlanWaterConsumption;

    /**
     * 去年实际用水
     */
    @Schema(description = "去年实际用水")
    private BigDecimal lastWaterConsumption;

}
