package com.landleaf.lh.domain.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author xusihbai
 * @since 2024/01/26
 **/
@Data
@Schema(description = "看板概览数据")
public class LhBoardTitleResponse {

    @Schema(description = "当年空调用电强度")
    private BigDecimal hvacEleEnergyDensityYear;
    @Schema(description = "当年空调用电量")
    private BigDecimal hvacEleEnergyYear;

    @Schema(description = "当月热水用电强度")
    private BigDecimal waterEleEnergyDesityMonth;
    @Schema(description = "当年热水用电量")
    private BigDecimal waterEleEnergyYear;

    @Schema(description = "当月总用水量")
    private BigDecimal waterMonth;
    @Schema(description = "当年总用水量")
    private BigDecimal waterYear;

    @Schema(description = "当年总用电量")
    private BigDecimal eleEnergyYear;
    @Schema(description = "计划使用率")
    private BigDecimal useRatio;

}
