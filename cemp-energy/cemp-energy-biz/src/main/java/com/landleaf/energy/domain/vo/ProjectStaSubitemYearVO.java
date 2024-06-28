package com.landleaf.energy.domain.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

import java.util.Date;

/**
 * ProjectStaSubitemYearEntity对象的展示信息封装
 *
 * @author hebin
 * @since 2023-06-25
 */
@Data
@Schema(name = "ProjectStaSubitemYearVO对象", description = "ProjectStaSubitemYearEntity对象的展示信息封装")
public class ProjectStaSubitemYearVO {

	/**
	 * id
	 */
		@Schema(description = "id")
		private Long id;

	/**
	 * 项目ID
	 */
		@Schema(description = "项目ID")
		private String bizProjectId;

	/**
	 * 项目CODE
	 */
		@Schema(description = "项目CODE")
		private String projectCode;

	/**
	 * 租户ID
	 */
		@Schema(description = "租户ID")
		private Long tenantId;

	/**
	 * 租户CODE
	 */
		@Schema(description = "租户CODE")
		private String tenantCode;

	/**
	 * 项目名称
	 */
		@Schema(description = "项目名称")
		private String projectName;

	/**
	 * 统计-年
	 */
		@Schema(description = "统计-年")
		private String year;

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

	/**
	 * 统计时间
	 */
		@Schema(description = "统计时间")
		private Timestamp staTime;

	/**
	 * 全部负荷平电费
	 */
	@Schema(description = "全部负荷平电费")
	private BigDecimal projectElectricityEnergyusagefeeFlat;
	/**
	 * 全部负荷尖电费
	 */
	@Schema(description = "全部负荷尖电费")
	private BigDecimal projectElectricityEnergyusagefeeTip;

	/**
	 * 全部负荷谷电费
	 */
	@Schema(description = "全部负荷谷电费")
	private BigDecimal projectElectricityEnergyusagefeeValley;

	/**
	 * 全部负荷峰电费
	 */
	@Schema(description = "全部负荷峰电费")
	private BigDecimal projectElectricityEnergyusagefeePeak;

	/**
	 * 空调补水
	 * project.water.HVACUsage.total
	 */
	private BigDecimal projectWaterHvacusageTotal;

	/**
	 * 热水补水
	 * project.water.HeatingWaterUsage.total
	 */
	private BigDecimal projectWaterHeatingwaterusageTotal;

	/**
	 * 空调单方用电量
	 * project.electricity.subHAVCEnergy.avgSq
	 */
	@Schema(description = "空调单方用电量")
	private BigDecimal projectElectricitySubhavcenergyAvgsq;

	/**
	 * 热水单立方用电量
	 * project.electricity.subHeatingWaterEnergy.avgCube
	 */
	@Schema(description = "热水单立方用电量")
	private BigDecimal projectElectricitySubheatingwaterenergyAvgcube;
}
