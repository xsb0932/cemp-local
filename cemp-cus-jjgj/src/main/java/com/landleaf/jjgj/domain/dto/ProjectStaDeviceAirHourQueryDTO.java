package com.landleaf.jjgj.domain.dto;

import com.landleaf.comm.base.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * 统计表-设备指标-空调-统计小时的查询时的参数封装
 *
 * @author hebin
 * @since 2023-06-25
 */
@Data
@EqualsAndHashCode(callSuper=false)
@Schema(name = "ProjectStaDeviceAirHourQueryDTO对象", description = "统计表-设备指标-空调-统计小时的查询时的参数封装")
public class ProjectStaDeviceAirHourQueryDTO extends PageParam {

	/**
	 * id
	 */
		@Schema(name = "id")
		private Long id;

	/**
	 * 设备ID
	 */
		@Schema(name = "设备ID")
		private String bizDeviceId;

	/**
	 * 产品ID
	 */
		@Schema(name = "产品ID")
		private String bizProductId;

	/**
	 * 品类ID
	 */
		@Schema(name = "品类ID")
		private String bizCategoryId;

	/**
	 * 项目ID
	 */
		@Schema(name = "项目ID")
		private String bizProjectId;

	/**
	 * 项目代码
	 */
		@Schema(name = "项目代码")
		private String projectCode;

	/**
	 * 租户ID
	 */
		@Schema(name = "租户ID")
		private Long tenantId;

	/**
	 * 租户代码
	 */
		@Schema(name = "租户代码")
		private String tenantCode;

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
	 * 统计-日
	 */
		@Schema(name = "统计-日")
		private String day;

	/**
	 * 统计-小时
	 */
		@Schema(name = "统计-小时")
		private String hour;

	/**
	 * 在线时长
	 */
		@Schema(name = "在线时长")
		private BigDecimal airconditionercontrollerOnlinetimeTotal;

	/**
	 * 运行时长
	 */
		@Schema(name = "运行时长")
		private BigDecimal airconditionercontrollerRunningtimeTotal;

	/**
	 * 测得平均温度
	 */
		@Schema(name = "测得平均温度")
		private BigDecimal airconditionercontrollerActualtempAvg;

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
