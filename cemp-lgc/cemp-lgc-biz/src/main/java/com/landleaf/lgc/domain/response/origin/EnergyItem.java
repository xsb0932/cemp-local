package com.landleaf.lgc.domain.response.origin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 统计项
 *
 * @author xushibai
 * @since 2023/09/05
 **/
@Data
@AllArgsConstructor
public class EnergyItem {

    /**
     * 指标key
     */
    @Schema(description = "指标key")
    private String key;

    /**
     * 指标value
     */
    @Schema(description = "指标value")
    private BigDecimal value;


}
