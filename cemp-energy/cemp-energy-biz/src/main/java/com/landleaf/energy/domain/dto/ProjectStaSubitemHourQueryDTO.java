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
 * ProjectStaSubitemHourEntity对象的查询时的参数封装
 *
 * @author hebin
 * @since 2023-06-25
 */
@Data
@EqualsAndHashCode(callSuper=false)
@Schema(name = "ProjectStaSubitemHourQueryDTO对象", description = "ProjectStaSubitemHourEntity对象的查询时的参数封装")
public class ProjectStaSubitemHourQueryDTO extends PageParam {

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
	 * 统计-月
	 */
		@Schema(name = "统计-月")
		private String month;

	/**
	 * 统计-天
	 */
		@Schema(name = "统计-天")
		private String day;

	/**
	 * 统计小时
	 */
		@Schema(name = "统计小时")
		private String hour;

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
	 * 全部负荷总用电量
	 */
		@Schema(name = "全部负荷总用电量")
		private BigDecimal projectElectricityEnergyusageTotal;

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
