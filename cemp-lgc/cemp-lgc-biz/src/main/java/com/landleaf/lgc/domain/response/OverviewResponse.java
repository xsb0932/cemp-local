package com.landleaf.lgc.domain.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 总览
 *
 * @author 张力方
 * @since 2023/7/28
 **/
@Data
public class OverviewResponse {
    /**
     * 当年购网电量
     */
    @Schema(description = "当年购网电量")
    private BigDecimal electricityPccEnergyUsageCurrentYear;
    /**
     * 当年上网电量
     */
    @Schema(description = "当年上网电量")
    private BigDecimal electricityPccEnergyProductionCurrentYear;
    /**
     * 当年光伏发电量
     */
    @Schema(description = "当年光伏发电量")
    private BigDecimal electricityPvEnergyProductionCurrentYear;
    /**
     * 累计光伏发电量
     */
    @Schema(description = "累计光伏发电量")
    private BigDecimal electricityPvEnergyProductionTotal;
    /**
     * 当年储能放电量
     */
    @Schema(description = "当年储能放电量")
    private BigDecimal electricityStorageEnergyUsageCurrentYear;
    /**
     * 累计储能放电量
     */
    @Schema(description = "累计储能放电量")
    private BigDecimal electricityStorageEnergyUsageTotal;
    /**
     * 当年充电桩充电量
     */
    @Schema(description = "当年充电桩充电量")
    private BigDecimal electricitySubChargeEnergyCurrentYear;
    /**
     * 累计充电桩充电量
     */
    @Schema(description = "累计充电桩充电量")
    private BigDecimal electricitySubChargeEnergyTotal;
    /**
     * 当年二氧化碳排放量
     */
    @Schema(description = "当年二氧化碳排放量")
    private BigDecimal carbonTotalCurrentYear;
    /**
     * 累计二氧化碳减少量
     */
    @Schema(description = "累计二氧化碳减少量")
    private BigDecimal carbonPvReductionTotal;
    /**
     * 光伏累计收益
     */
    @Schema(description = "光伏累计收益")
    private BigDecimal electricityPvEnergyProductionFeeTotal;
    /**
     * 储能累计收益
     */
    @Schema(description = "储能累计收益")
    private BigDecimal electricityStorageEnergyNetFeeTotal;
    /**
     * 充电桩累计收益
     */
    @Schema(description = "充电桩累计收益")
    private BigDecimal electricitySubEnergyNetFeeTotal;
    /**
     * 当年光伏绿电占总用电比例 %
     */
    @Schema(description = "当年光伏绿电占总用电比例 %")
    private BigDecimal pvVsTotalPercentageCurrentYear;
    /**
     * 当年光伏发电消纳比例 %
     */
    @Schema(description = "当年光伏发电消纳比例 %")
    private BigDecimal pvAbsorptivePercentageCurrentYear;
    /**
     * 月度电源结构趋势
     */
    @Schema(description = "月度电源结构趋势")
    private ElectricityStructure monthElectricityStructure;
    /**
     * 上海办公建筑平均能耗
     */
    @Schema(description = "上海办公建筑平均能耗")
    private BigDecimal buildingAvgEnergyUsage;
    /**
     * LGC能耗
     */
    @Schema(description = "LGC能耗")
    private BigDecimal lgcEnergyUsage;
    /**
     * 当年负荷使用结构
     */
    @Schema(description = "当年负荷使用结构")
    private ElectricityLoadStructure electricityLoadStructureCurrentYear;
    /**
     * 月度负荷使用结构
     */
    @Schema(description = "月度负荷使用结构")
    private ElectricityLoadStructureDataSet monthElectricityLoadStructure;
    /**
     * 中间对象
     */
    private Middle middle = new Middle();

    @Data
    public static class Middle {

        /**
         * 光伏实时有功功率
         */
        @Schema(description = "光伏实时有功功率")
        private BigDecimal pvCurrentActiveP;
        /**
         * 光伏当日发电量
         */
        @Schema(description = "光伏当日发电量")
        private BigDecimal electricityPvEnergyProductionCurrentDay;
        /**
         * 市电实时有功功率
         */
        @Schema(description = "市电实时有功功率")
        private BigDecimal pccCurrentActiveP;
        /**
         * 市电实时无功功率
         */
        @Schema(description = "市电实时无功功率")
        private BigDecimal pccCurrentReactiveP;
        /**
         * 当日购网电量
         */
        @Schema(description = "当日购网电量")
        private BigDecimal electricityPccEnergyUsageCurrentDay;
        /**
         * 当日上网电量
         */
        @Schema(description = "当日上网电量")
        private BigDecimal electricityPvEnergyProductionGridCurrentDay;
        /**
         * 建筑用电负荷有功功率
         */
        @Schema(description = "建筑用电负荷有功功率")
        private BigDecimal buildingUsageElectricityActiveP;
        /**
         * 建筑用电负荷当日用电量
         */
        @Schema(description = "建筑用电负荷当日用电量")
        private BigDecimal buildingUsageElectricityCurrentDay;
        /**
         * 储能系统实时运行状态
         */
        @Schema(description = "储能系统实时运行状态")
        private String storageCurrentRunningStatus;
        /**
         * 储能系统实时有功功率
         */
        @Schema(description = "储能系统实时有功功率")
        private BigDecimal storageCurrentActiveP;
        /**
         * 储能系统实时SOC %
         */
        @Schema(description = "储能系统实时SOC %")
        private BigDecimal storageCurrentSoc;
        /**
         * 充电桩实时充电中数量
         */
        @Schema(description = "充电桩实时充电中数量")
        private BigDecimal stationChargeCurrentNum;
        /**
         * 充电桩实时有功功率
         */
        @Schema(description = "充电桩实时有功功率")
        private BigDecimal stationCurrentActiveP;
        /**
         * 充电桩当日充电量
         */
        @Schema(description = "充电桩当日充电量")
        private BigDecimal stationChargeCurrentDay;
    }

    @Data
    public static class ElectricityStructure {
        /**
         * 月份
         */
        @Schema(description = "月份")
        private List<Integer> x = new ArrayList<>();
        /**
         * 光伏发电
         */
        @Schema(description = "光伏发电")
        private List<BigDecimal> pv = new ArrayList<>();
        /**
         * 市电
         */
        @Schema(description = "市电")
        private List<BigDecimal> pcc = new ArrayList<>();
    }

    @Data
    public static class ElectricityLoadStructure {
        /**
         * 充电桩
         */
        @Schema(description = "充电桩")
        private BigDecimal charge;
        /**
         * 暖通
         */
        @Schema(description = "暖通")
        private BigDecimal hvac;
        /**
         * 照明
         */
        @Schema(description = "照明")
        private BigDecimal light;
        /**
         * 插座
         */
        @Schema(description = "插座")
        private BigDecimal socket;
        /**
         * 其他
         */
        @Schema(description = "其他")
        private BigDecimal other;
    }

    @Data
    public static class ElectricityLoadStructureDataSet {
        /**
         * 月份
         */
        @Schema(description = "月份")
        private List<Integer> x = new ArrayList<>();
        /**
         * 充电桩
         */
        @Schema(description = "充电桩")
        private List<BigDecimal> charge = new ArrayList<>();
        /**
         * 暖通
         */
        @Schema(description = "暖通")
        private List<BigDecimal> hvac = new ArrayList<>();
        /**
         * 照明
         */
        @Schema(description = "照明")
        private List<BigDecimal> light = new ArrayList<>();
        /**
         * 插座
         */
        @Schema(description = "插座")
        private List<BigDecimal> socket = new ArrayList<>();
        /**
         * 其他
         */
        @Schema(description = "其他")
        private List<BigDecimal> other = new ArrayList<>();
    }
}
