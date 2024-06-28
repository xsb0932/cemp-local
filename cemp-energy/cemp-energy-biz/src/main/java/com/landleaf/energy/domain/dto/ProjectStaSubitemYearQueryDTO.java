package com.landleaf.energy.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.landleaf.comm.base.pojo.PageParam;
import java.math.BigDecimal;
import java.sql.Timestamp;

import java.util.Date;

/**
 * ProjectStaSubitemYearEntity对象的查询时的参数封装
 *
 * @author hebin
 * @since 2023-06-25
 */
@Data
@EqualsAndHashCode(callSuper=false)
@Schema(name = "ProjectStaSubitemYearQueryDTO对象", description = "ProjectStaSubitemYearEntity对象的查询时的参数封装")
public class ProjectStaSubitemYearQueryDTO extends PageParam {

	/**
	 * id
	 */
		@Schema(name = "id")
		private Long id;

	/**
	 * 项目ID
	 */
		@Schema(name = "项目ID")
		private String bizProjectId;

	/**
	 * 项目CODE
	 */
		@Schema(name = "项目CODE")
		private String projectCode;

	/**
	 * 租户ID
	 */
		@Schema(name = "租户ID")
		private Long tenantId;

	/**
	 * 租户CODE
	 */
		@Schema(name = "租户CODE")
		private String tenantCode;

	/**
	 * 项目名称
	 */
		@Schema(name = "项目名称")
		private String projectName;

	/**
	 * 统计-年
	 */
		@Schema(name = "统计-年")
		private String year;

	/**
	 * 总用气费
	 */
		@Schema(name = "总用气费")
		private BigDecimal projectGasFeeTotal;

	/**
	 * 总用气量
	 */
		@Schema(name = "总用气量")
		private BigDecimal projectGasUsageTotal;

	/**
	 * 总用水量
	 */
		@Schema(name = "总用水量")
		private BigDecimal projectWaterUsageTotal;

	/**
	 * 总计用水费
	 */
		@Schema(name = "总计用水费")
		private BigDecimal projectWaterFeeWater;

	/**
	 * 总计污水处理费
	 */
		@Schema(name = "总计污水处理费")
		private BigDecimal projectWaterFeeSewerage;

	/**
	 * 总计水费
	 */
		@Schema(name = "总计水费")
		private BigDecimal projectWaterFeeTotal;

	/**
	 * 全部负荷平用电量
	 */
		@Schema(name = "全部负荷平用电量")
		private BigDecimal projectElectricityEnergyusageFlat;

	/**
	 * 全部负荷总用电量
	 */
		@Schema(name = "全部负荷总用电量")
		private BigDecimal projectElectricityEnergyusageTotal;

	/**
	 * 全部负荷电度电费
	 */
		@Schema(name = "全部负荷电度电费")
		private BigDecimal projectElectricityEnergyusagefeeTotal;

	/**
	 * 全部负荷尖用电量
	 */
		@Schema(name = "全部负荷尖用电量")
		private BigDecimal projectElectricityEnergyusageTip;

	/**
	 * 全部负荷谷用电量
	 */
		@Schema(name = "全部负荷谷用电量")
		private BigDecimal projectElectricityEnergyusageValley;

	/**
	 * 全部负荷峰用电量
	 */
		@Schema(name = "全部负荷峰用电量")
		private BigDecimal projectElectricityEnergyusagePeak;

	/**
	 * 电梯总用电量
	 */
		@Schema(name = "电梯总用电量")
		private BigDecimal projectElectricitySubelevatorenergyTotal;

	/**
	 * 动力用电量
	 */
	@Schema(name = "动力总用电量")
	private BigDecimal projectElectricitySubpowersupplyenergyTotal;

	/**
	 * 客房总用电量
	 */
		@Schema(name = "客房总用电量")
		private BigDecimal projectElectricitySubguestroomenergyTotal;

	/**
	 * 热水总用电量
	 */
		@Schema(name = "热水总用电量")
		private BigDecimal projectElectricitySubheatingwaterenergyTotal;

	/**
	 * 空调总用电量
	 */
		@Schema(name = "空调总用电量")
		private BigDecimal projectElectricitySubhavcenergyTotal;

	/**
	 * 其他总用电量
	 */
		@Schema(name = "其他总用电量")
		private BigDecimal projectElectricitySubothertypeTotal;

	/**
	 * 供水总用电量
	 */
		@Schema(name = "供水总用电量")
		private BigDecimal projectElectricitySubwatersupplyenergyTotal;

	/**
	 * 全部能耗标准煤当量
	 */
		@Schema(name = "全部能耗标准煤当量")
		private BigDecimal projectCarbonTotalcoalTotal;

	/**
	 * 全部能耗粉尘当量
	 */
		@Schema(name = "全部能耗粉尘当量")
		private BigDecimal projectCarbonTotaldustTotal;

	/**
	 * 全部能耗CO2当量
	 */
		@Schema(name = "全部能耗CO2当量")
		private BigDecimal projectCarbonTotalco2Total;

	/**
	 * 全部能耗SO2当量
	 */
		@Schema(name = "全部能耗SO2当量")
		private BigDecimal projectCarbonTotalso2Total;

	/**
	 * 气耗CO2当量
	 */
		@Schema(name = "气耗CO2当量")
		private BigDecimal projectCarbonGasusageco2Total;

	/**
	 * 气耗标准煤当量
	 */
		@Schema(name = "气耗标准煤当量")
		private BigDecimal projectCarbonGasusagecoalTotal;

	/**
	 * 气耗粉尘当量
	 */
		@Schema(name = "气耗粉尘当量")
		private BigDecimal projectCarbonGasusagedustTotal;

	/**
	 * 气耗SO2当量
	 */
		@Schema(name = "气耗SO2当量")
		private BigDecimal projectCarbonGasusageso2Total;

	/**
	 * 电耗SO2当量
	 */
		@Schema(name = "电耗SO2当量")
		private BigDecimal projectCarbonElectricityusageso2Total;

	/**
	 * 电耗粉尘当量
	 */
		@Schema(name = "电耗粉尘当量")
		private BigDecimal projectCarbonElectricityusagedustTotal;

	/**
	 * 电耗CO2当量
	 */
		@Schema(name = "电耗CO2当量")
		private BigDecimal projectCarbonElectricityusageco2Total;

	/**
	 * 电耗标准煤当量
	 */
		@Schema(name = "电耗标准煤当量")
		private BigDecimal projectCarbonElectricityusagecoalTotal;

	/**
	 * 水耗粉尘当量
	 */
		@Schema(name = "水耗粉尘当量")
		private BigDecimal projectCarbonWaterusagedustTotal;

	/**
	 * 水耗标煤当量
	 */
		@Schema(name = "水耗标煤当量")
		private BigDecimal projectCarbonWaterusagecoalTotal;

	/**
	 * 水耗CO2当量
	 */
		@Schema(name = "水耗CO2当量")
		private BigDecimal projectCarbonWaterusageco2Total;

	/**
	 * 水耗SO2当量
	 */
		@Schema(name = "水耗SO2当量")
		private BigDecimal projectCarbonWaterusageso2Total;

	/**
	 * 统计时间
	 */
		@Schema(name = "统计时间")
		private Timestamp staTime;

	/**
	 * 开始时间
	 */
	@Schema(name = "开始时间,格式为yyyy-MM-dd")
	private String startTime;

	/**
	 * 结束时间
	 */
	@Schema(name = "结束时间,格式为yyyy-MM-dd")
	private String endTime;
}
