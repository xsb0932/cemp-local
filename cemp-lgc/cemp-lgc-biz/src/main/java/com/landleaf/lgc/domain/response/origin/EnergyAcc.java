package com.landleaf.lgc.domain.response.origin;

import com.landleaf.lgc.domain.response.ChargeResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 累计能耗
 *
 * @author xushibai
 * @since 2023/09/05
 **/
@Data
public class EnergyAcc {

    /**
     * 年度累计能耗
     */
    @Schema(description = "年度累计能耗")
    private BigDecimal yearEnergyAcc;

    /**
     * 年度累计光伏发电量
     */
    @Schema(description = "年度累计光伏发电量")
    private BigDecimal yearGfAcc;

    /**
     * 年度累计碳排量
     */
    @Schema(description = "年度累计碳排量")
    private BigDecimal yearCarbonAcc;
}
