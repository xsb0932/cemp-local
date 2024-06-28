package com.landleaf.lh.domain.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author xusihbai
 * @since 2024/01/26
 **/
@Data
@Schema(description = "看板-当年计划执行")
public class LhBoardTargetResponse {

    @Schema(description = "总用电量实际值")
    private BigDecimal eleEnergy;
    @Schema(description = "总用电量计划值")
    private BigDecimal eleEnergyTarget;

    @Schema(description = "总用水量实际值")
    private BigDecimal waterUse;
    @Schema(description = "总用水量计划值")
    private BigDecimal waterUseTarget;

}
