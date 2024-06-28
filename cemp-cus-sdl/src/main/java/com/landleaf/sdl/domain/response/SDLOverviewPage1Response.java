package com.landleaf.sdl.domain.response;

import com.landleaf.energy.response.SubitemYearRatioResoponse;
import com.landleaf.sdl.domain.vo.SDLEnergyMonthRatio;
import com.landleaf.sdl.domain.vo.SDLSubitemYearRatoVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 总览
 *
 * @author xusihbai
 * @since 2023/11/29
 **/
@Data
public class SDLOverviewPage1Response {

    /**
     * 光伏绿电占总用电比例
     */
    @Schema(description = "光伏绿电占总用电比例")
    private BigDecimal elePvRatio;

    /**
     * 直流用电比例
     */
    @Schema(description = "直流用电比例")
    private BigDecimal eleDirRatio;

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
     * 当年发电量
     */
    @Schema(description = "当年发电量")
    private BigDecimal electricityPvEnergyProductionCurrentYear;
    /**
     * 累计发电量
     */
    @Schema(description = "累计发电量")
    private BigDecimal electricityPvEnergyProductionTotal;
    /**
     * 当年储能放电量
     */
    @Schema(description = "当年储电量")
    private BigDecimal electricityStorageEnergyUsageCurrentYear;
    /**
     * 累计储能放电量
     */
    @Schema(description = "累计储电量")
    private BigDecimal electricityStorageEnergyUsageTotal;
    /**
     * 当年充电桩充电量
     */
    @Schema(description = "当年充电量")
    private BigDecimal electricitySubChargeEnergyCurrentYear;
    /**
     * 累计充电桩充电量
     */
    @Schema(description = "累计充电量")
    private BigDecimal electricitySubChargeEnergyTotal;
    /**
     * 当年二氧化碳排量
     */
    @Schema(description = "当年二氧化碳排量")
    private BigDecimal carbonTotalCurrentYear;
    /**
     * 累计减少二氧化碳
     */
    @Schema(description = "累计减少二氧化碳")
    private BigDecimal carbonPvReductionTotal;

    /**
     * PM25
     */
    @Schema(description = "PM25")
    private BigDecimal pm25;
    /**
     * CO2
     */
    @Schema(description = "CO2")
    private BigDecimal co2;

    /**
     * 温度
     */
    @Schema(description = "温度")
    private BigDecimal temp;

    /**
     * 湿度
     */
    @Schema(description = "湿度")
    private BigDecimal hum;
    /**
     * 甲醛
     */
    @Schema(description = "甲醛")
    private BigDecimal formaldehyde;

    /**
     * 建筑能耗强度-建筑标准
     */
    @Schema(description = "建筑能耗强度-建筑标准")
    private BigDecimal buildingEnergyStandard;

    /**
     * 建筑能耗强度-实际能耗强度
     */
    @Schema(description = "建筑能耗强度-实际能耗强度")
    private BigDecimal buildingEnergyActual;

    /**
     * 光伏用电占总用电比例
     */
    @Schema(description = "光伏用电占总用电比例")
    private BigDecimal pvPRatio;

    /**
     * 直流用电比例
     */
    @Schema(description = "直流用电比例")
    private BigDecimal pPRatio;

//    /**
//     * 当年负荷使用结构
//     */
//    @Schema(description = "当年负荷使用结构")
//    private SubitemYearRatioResoponse subitemYearRatioData;

    /**
     * 当年负荷使用结构
     */
    @Schema(description = "当年负荷使用结构")
    private ElectricityLoadStructure electricityLoadStructureCurrentYear;
    /**
     * 月度负荷使用结构-当年
     */
    @Schema(description = "月度负荷使用结构-当年")
    private ElectricityLoadStructureDataSet monthElectricityLoadStructure;

    /**
     * 月度负荷使用结构-去年
     */
    @Schema(description = "月度负荷使用结构-去年")
    private ElectricityLoadStructureDataSet lastYearMonthElectricityLoadStructure;

    /**
     * 月度负荷使用结构-同比
     */
    @Schema(description = "月度负荷使用结构-同比")
    private ElectricityLoadStructureDataSet yoyMonthElectricityLoadStructure;

    /**
     * 当年电源结构月趋势
     */
    @Schema(description = "当年电源结构月趋势")
    private SDLEnergyMonthRatio energyMonthRatioData;

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
