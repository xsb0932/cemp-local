package com.landleaf.lh.domain.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author xusihbai
 * @since 2024/06/11
 **/
@Data
@Schema(description = "概览")
@AllArgsConstructor
public class LhAreaOverviewResponse {

    @Schema(description = "当月报修记录")
    private String maintenanceNum;
    @Schema(description = "报修同比")
    private String maintenanceYOY;

    @Schema(description = "当月总用电")
    private String eleMonth;
    @Schema(description = "用电-累计全年计划使用")
    private String eleTotalRatio;

    @Schema(description = "当月总用水")
    private String waterMonth;
    @Schema(description = "用水-累计全年计划使用")
    private String waterTotalRatio;

    @Schema(description = "当月平均气温")
    private String temEverMonth;
    @Schema(description = "气温同比")
    private String tempYOY;

    @Schema(description = "统计年份")
    private String year;
    @Schema(description = "统计月份")
    private String month;


}
