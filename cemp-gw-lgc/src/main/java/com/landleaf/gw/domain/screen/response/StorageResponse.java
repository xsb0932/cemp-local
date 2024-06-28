package com.landleaf.gw.domain.screen.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 储能大屏
 *
 * @author 张力方
 * @since 2023/7/28
 **/
@Data
public class StorageResponse {
    /**
     * 所属省市名称
     */
    @Schema(description = "所属省市名称")
    private String cityName;
    /**
     * 当月电价 key 是小时
     */
    @Schema(description = "当月电价")
    private ElectricityPrice electricityPrice;
    /**
     * 日充放电功率 key 时间
     */
    @Schema(description = "日发电功率")
    private DayP dayP;
    /**
     * 当月收益
     */
    @Schema(description = "当月收益")
    private BigDecimal electricityStorageEnergyNetFeeCurrentMonth;
    /**
     * 当年收益
     */
    @Schema(description = "当年收益")
    private BigDecimal electricityStorageEnergyNetFeeCurrentYear;
    /**
     * 累计放电量
     */
    @Schema(description = "累计放电量")
    private BigDecimal electricityStorageEnergyUsageTotal;
    /**
     * 累计充电量
     */
    @Schema(description = "累计充电量")
    private BigDecimal electricityStorageEnergyProductionTotal;
    /**
     * 月充电趋势，key - 月份
     */
    @Schema(description = "月充电趋势")
    private Usage monthUsage;
    /**
     * 月放电趋势，key - 月份
     */
    @Schema(description = "月放电趋势")
    private Production monthProduction;
    /**
     * 实时功率
     */
    @Schema(description = "实时功率")
    private BigDecimal currentP;
    /**
     * SOC
     */
    @Schema(description = "SOC")
    private BigDecimal soc;
    /**
     * SOH
     */
    @Schema(description = "SOH")
    private BigDecimal soh;

    private Middle middle = new Middle();

    @Data
    public static class Middle {
        /**
         * 运行模式
         */
        private String runMode;
        /**
         * 控制方式
         */
        private String controlType;
        /**
         * PCS状态
         */
        private String pcsStatus;
        /**
         * 当日放电量
         */
        private BigDecimal productionCurrentDay;
        /**
         * 当日充电量
         */
        private BigDecimal usageCurrentDay;
        /**
         * 总压
         */
        private BigDecimal batteryU;
        /**
         * 电流
         */
        private BigDecimal batteryI;
        /**
         * 最高单体电压
         */
        private BigDecimal maxVoltage;
        /**
         * 最低单体电压
         */
        private BigDecimal minVoltage;
        /**
         * 最高单体温度
         */
        private BigDecimal maxTemp;
        /**
         * 最低单体温度
         */
        private BigDecimal minTemp;
        /**
         * ab线电压
         */
        private BigDecimal uab;
        /**
         * bc线电压
         */
        private BigDecimal ubc;
        /**
         * ca线电压
         */
        private BigDecimal uca;
        /**
         * a相电流
         */
        private BigDecimal ia;
        /**
         * b相电流
         */
        private BigDecimal ib;
        /**
         * c相电流
         */
        private BigDecimal ic;
        /**
         * 频率
         */
        private BigDecimal f;
        /**
         * 有功功率
         */
        private BigDecimal p;
        /**
         * 无功功率
         */
        private BigDecimal q;
        /**
         * 功率因素
         */
        private BigDecimal pf;

    }

    @Data
    public static class Usage {
        /**
         * 月
         */
        @Schema(description = "月")
        private List<Integer> x = new ArrayList<>();
        /**
         * 市电储量
         */
        @Schema(description = "市电储量")
        private List<BigDecimal> storageEnergyUsageGrid = new ArrayList<>();
        /**
         * 储光电量
         */
        @Schema(description = "储光电量")
        private List<BigDecimal> storageEnergyUsagePv = new ArrayList<>();
    }

    @Data
    public static class Production {
        /**
         * 月
         */
        @Schema(description = "月")
        private List<Integer> x = new ArrayList<>();
        /**
         * 尖放电
         */
        @Schema(description = "尖放电")
        private List<BigDecimal> storageEnergyProductionTip = new ArrayList<>();
        /**
         * 峰放电
         */
        @Schema(description = "峰放电")
        private List<BigDecimal> storageEnergyProductionPeak = new ArrayList<>();
    }

    @Data
    public static class ElectricityPrice {
        /**
         * 小时
         */
        @Schema(description = "小时")
        private List<Integer> x = new ArrayList<>();
        /**
         * 值
         */
        @Schema(description = "值")
        private List<BigDecimal> value = new ArrayList<>();
    }

    @Data
    public static class DayP {
        /**
         * 小时
         */
        @Schema(description = "小时")
        private List<String> x = new ArrayList<>();
        /**
         * 功率
         */
        @Schema(description = "功率")
        private List<BigDecimal> p = new ArrayList<>();
    }
}

