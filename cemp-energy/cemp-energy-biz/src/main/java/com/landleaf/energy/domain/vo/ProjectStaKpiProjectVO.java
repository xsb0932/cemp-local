package com.landleaf.energy.domain.vo;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Schema(name = "项目统计VO", description = "项目统计VO")
public class ProjectStaKpiProjectVO extends ProjectStaKpiVO{

    /**
     * 总用气费
     */
    @Schema(description = "总用气费")
    private BigDecimal projectGasFeeTotal;

    /**
     * 总用气量
     */
    @Schema(description = "总用气量")
    private BigDecimal projectGasUsageTotal;

    /**
     * 总用水量
     */
    @Schema(description = "总用水量")
    private BigDecimal projectWaterUsageTotal;

    /**
     * 总计用水费
     */
    @Schema(description = "总计用水费")
    private BigDecimal projectWaterFeeWater;

    /**
     * 总计污水处理费
     */
    @Schema(description = "总计污水处理费")
    private BigDecimal projectWaterFeeSewerage;

    /**
     * 总计水费
     */
    @Schema(description = "总计水费")
    private BigDecimal projectWaterFeeTotal;

    /**
     * 全部负荷平用电量
     */
    @Schema(description = "全部负荷平用电量")
    private BigDecimal projectElectricityEnergyusageFlat;

    /**
     * 全部负荷总用电量
     */
    @Schema(description = "全部负荷总用电量")
    private BigDecimal projectElectricityEnergyusageTotal;

    /**
     * 全部负荷电度电费
     */
    @Schema(description = "全部负荷电度电费")
    private BigDecimal projectElectricityEnergyusagefeeTotal;

    /**
     * 全部负荷尖用电量
     */
    @Schema(description = "全部负荷尖用电量")
    private BigDecimal projectElectricityEnergyusageTip;

    /**
     * 全部负荷谷用电量
     */
    @Schema(description = "全部负荷谷用电量")
    private BigDecimal projectElectricityEnergyusageValley;

    /**
     * 全部负荷峰用电量
     */
    @Schema(description = "全部负荷峰用电量")
    private BigDecimal projectElectricityEnergyusagePeak;

    /**
     * 电梯总用电量
     */
    @Schema(description = "电梯总用电量")
    private BigDecimal projectElectricitySubelevatorenergyTotal;

    /**
     * 客房总用电量
     */
    @Schema(description = "客房总用电量")
    private BigDecimal projectElectricitySubguestroomenergyTotal;

    /**
     * 热水总用电量
     */
    @Schema(description = "热水总用电量")
    private BigDecimal projectElectricitySubheatingwaterenergyTotal;

    /**
     * 空调总用电量
     */
    @Schema(description = "空调总用电量")
    private BigDecimal projectElectricitySubhavcenergyTotal;

    /**
     * 其他总用电量
     */
    @Schema(description = "其他总用电量")
    private BigDecimal projectElectricitySubothertypeTotal;

    /**
     * 供水总用电量
     */
    @Schema(description = "供水总用电量")
    private BigDecimal projectElectricitySubwatersupplyenergyTotal;

    /**
     * 全部能耗标准煤当量
     */
    @Schema(description = "全部能耗标准煤当量")
    private BigDecimal projectCarbonTotalcoalTotal;

    /**
     * 全部能耗粉尘当量
     */
    @Schema(description = "全部能耗粉尘当量")
    private BigDecimal projectCarbonTotaldustTotal;

    /**
     * 全部能耗CO2当量
     */
    @Schema(description = "全部能耗CO2当量")
    private BigDecimal projectCarbonTotalco2Total;

    /**
     * 全部能耗SO2当量
     */
    @Schema(description = "全部能耗SO2当量")
    private BigDecimal projectCarbonTotalso2Total;

    /**
     * 气耗CO2当量
     */
    @Schema(description = "气耗CO2当量")
    private BigDecimal projectCarbonGasusageco2Total;

    /**
     * 气耗标准煤当量
     */
    @Schema(description = "气耗标准煤当量")
    private BigDecimal projectCarbonGasusagecoalTotal;

    /**
     * 气耗粉尘当量
     */
    @Schema(description = "气耗粉尘当量")
    private BigDecimal projectCarbonGasusagedustTotal;

    /**
     * 气耗SO2当量
     */
    @Schema(description = "气耗SO2当量")
    private BigDecimal projectCarbonGasusageso2Total;

    /**
     * 电耗SO2当量
     */
    @Schema(description = "电耗SO2当量")
    private BigDecimal projectCarbonElectricityusageso2Total;

    /**
     * 电耗粉尘当量
     */
    @Schema(description = "电耗粉尘当量")
    private BigDecimal projectCarbonElectricityusagedustTotal;

    /**
     * 电耗CO2当量
     */
    @Schema(description = "电耗CO2当量")
    private BigDecimal projectCarbonElectricityusageco2Total;

    /**
     * 电耗标准煤当量
     */
    @Schema(description = "电耗标准煤当量")
    private BigDecimal projectCarbonElectricityusagecoalTotal;

    /**
     * 水耗粉尘当量
     */
    @Schema(description = "水耗粉尘当量")
    private BigDecimal projectCarbonWaterusagedustTotal;

    /**
     * 水耗标煤当量
     */
    @Schema(description = "水耗标煤当量")
    private BigDecimal projectCarbonWaterusagecoalTotal;

    /**
     * 水耗CO2当量
     */
    @Schema(description = "水耗CO2当量")
    private BigDecimal projectCarbonWaterusageco2Total;

    /**
     * 水耗SO2当量
     */
    @Schema(description = "水耗SO2当量")
    private BigDecimal projectCarbonWaterusageso2Total;

}
