package com.landleaf.energy.domain.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

import java.util.Date;

/**
 * 统计表-设备指标-气类-统计月的展示信息封装
 *
 * @author hebin
 * @since 2023-06-25
 */
@Data
@Schema(name = "ProjectStaDeviceGasMonthVO对象", description = "统计表-设备指标-气类-统计月的展示信息封装")
public class ProjectStaDeviceGasMonthVO {

	/**
	 * id
	 */
		@Schema(description = "id")
		private Long id;

	/**
	 * 设备ID
	 */
		@Schema(description = "设备ID")
		private String bizDeviceId;

	/**
	 * 产品ID
	 */
		@Schema(description = "产品ID")
		private String bizProductId;

	/**
	 * 品类ID
	 */
		@Schema(description = "品类ID")
		private String bizCategoryId;

	/**
	 * 项目ID
	 */
		@Schema(description = "项目ID")
		private String bizProjectId;

	/**
	 * 项目代码
	 */
		@Schema(description = "项目代码")
		private String projectCode;

	/**
	 * 租户ID
	 */
		@Schema(description = "租户ID")
		private Long tenantId;

	/**
	 * 租户代码
	 */
		@Schema(description = "租户代码")
		private String tenantCode;

	/**
	 * 统计-年
	 */
		@Schema(description = "统计-年")
		private String year;

	/**
	 * 统计-月
	 */
		@Schema(description = "统计-月")
		private String month;

	/**
	 * 用气量
	 */
		@Schema(description = "用气量")
		private BigDecimal gasmeterUsageTotal;

	/**
	 * 统计时间
	 */
		@Schema(description = "统计时间")
		private Timestamp staTime;
}
