package com.landleaf.energy.domain.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

import java.util.Date;

/**
 * 指标库表的展示信息封装
 *
 * @author hebin
 * @since 2023-07-06
 */
@Data
@Schema(name = "ProjectKpiConfigVO对象", description = "指标库表的展示信息封装")
public class ProjectKpiConfigVO {

	/**
	 * 指标ID
	 */
		@Schema(description = "指标ID")
		private Long id;

	/**
	 * 指标code
	 */
		@Schema(description = "指标code")
		private String code;

	/**
	 * 指标名称
	 */
		@Schema(description = "指标名称")
		private String name;

	/**
	 * 分项大类 水 气 电 ...
	 */
		@Schema(description = "分项大类 水 气 电 ...")
		private String kpiType;

	/**
	 * 分项类型代码
	 */
		@Schema(description = "分项类型代码")
		private String kpiSubtype;

	/**
	 * 统计间隔-小时-1:是 0 否
	 */
		@Schema(description = "统计间隔-小时-1:是 0 否")
		private Integer staIntervalHour;

	/**
	 * 统计间隔-日月年-1:是 0 否
	 */
		@Schema(description = "统计间隔-日月年-1:是 0 否")
		private Integer staIntervalYmd;

	/**
	 * 租户id
	 */
		@Schema(description = "租户id")
		private Long tenantId;

	/**
	 * 单位
	 */
		@Schema(description = "单位")
		private String unit;

	/**
	 * 分项大类代码
	 */
		@Schema(description = "分项大类代码")
		private String kpiTypeCode;
}
