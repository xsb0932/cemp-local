package com.landleaf.lgc.domain.response.origin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 月累计数据
 *
 * @author xushibai
 * @since 2023/09/05
 **/
@Data
public class EnergyLineChart {

    /**
     * 月x轴
     */
    @Schema(description = "月x轴")
    private List<String> xs;

    /**
     * 月统计值
     */
    @Schema(description = "月统计值")
    private List<BigDecimal> ys;




}
