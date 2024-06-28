package com.landleaf.gw.domain.screen.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * HvacResponse
 *
 * @author 张力方
 * @since 2023/8/2
 **/
@Data
public class HvacResponse {
    /**
     * 室内平均温度
     */
    @Schema(description = "室内平均温度")
    private BigDecimal indoorAvgTemp;
    /**
     * 室内平均湿度
     */
    @Schema(description = "室内平均湿度")
    private BigDecimal indoorAvgHumidity;
    /**
     * 室内平均甲醛
     */
    @Schema(description = "室内平均甲醛")
    private BigDecimal indoorAvgFormaldehyde;
    /**
     * 室内平均CO2
     */
    @Schema(description = "室内平均CO2")
    private BigDecimal indoorAvgCO2;
    /**
     * 室内平均PM2.5
     */
    @Schema(description = "室内平均PM2.5")
    private BigDecimal indoorAvgPM25;
    /**
     * 当日用电量
     */
    @Schema(description = "当日用电量")
    private BigDecimal electricityEnergyUsageCurrentDay;
    /**
     * 当月用电量
     */
    @Schema(description = "当月用电量")
    private BigDecimal electricityEnergyUsageCurrentMonth;
    /**
     * 当年用电量
     */
    @Schema(description = "当年用电量")
    private BigDecimal electricityEnergyUsageCurrentYear;
    /**
     * 毛细辐射末端开启数量
     */
    @Schema(description = "毛细辐射末端开启数量")
    private Integer crdOpenNum;
    /**
     * 毛细辐射末端关闭数量
     */
    @Schema(description = "毛细辐射末端关闭数量")
    private Integer crdCloseNum;
    /**
     * 风机盘末端开启数量
     */
    @Schema(description = "风机盘末端开启数量")
    private Integer fdeOpenNum;
    /**
     * 风机盘末端关闭数量
     */
    @Schema(description = "风机盘末端关闭数量")
    private Integer fdeCloseNum;
    /**
     * 日用电趋势 key 天
     */
    @Schema(description = "日用电趋势")
    private DayTrend electricityEnergyUsageDayTrend;
    /**
     * 月用电趋势 key 月
     */
    @Schema(description = "月用电趋势")
    private Usage electricityEnergyUsageMonthTrend;

    @Data
    public static class Usage {
        /**
         * 月
         */
        @Schema(description = "月")
        private List<Integer> x = new ArrayList<>();
        /**
         * 当年用电量
         */
        @Schema(description = "当年用电量")
        private List<BigDecimal> currentYear = new ArrayList<>();
        /**
         * 上年用电量
         */
        @Schema(description = "上年用电量")
        private List<BigDecimal> lastYear = new ArrayList<>();
        /**
         * 同比
         */
        @Schema(description = "同比")
        private List<BigDecimal> yoy = new ArrayList<>();
    }

    @Data
    public static class DayTrend {
        /**
         * 天
         */
        @Schema(description = "天")
        private List<Integer> x = new ArrayList<>();
        /**
         * 值
         */
        @Schema(description = "值")
        private List<BigDecimal> value = new ArrayList<>();
    }

}
