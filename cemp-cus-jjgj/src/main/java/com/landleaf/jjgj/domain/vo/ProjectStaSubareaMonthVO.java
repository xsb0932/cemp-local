package com.landleaf.jjgj.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * ProjectStaSubareaMonthEntity对象的展示信息封装
 *
 * @author hebin
 * @since 2023-06-25
 */
@Data
@Schema(name = "ProjectStaSubareaMonthVO对象", description = "ProjectStaSubareaMonthEntity对象的展示信息封装")
public class ProjectStaSubareaMonthVO {

	/**
	 * id
	 */
		@Schema(description = "id")
		private Long id;

	/**
	 * 指标CODE
	 */
		@Schema(description = "指标CODE")
		private String kpiCode;

	/**
	 * 分区代码
	 */
		@Schema(description = "分区代码")
		private String subareaCode;

	/**
	 * 分区名字
	 */
		@Schema(description = "分区名字")
		private String subareaName;

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
	 * 统计-月
	 */
		@Schema(description = "统计-月")
		private String month;

	/**
	 * 统计值
	 */
		@Schema(description = "统计值")
		private BigDecimal staValue;

	/**
	 * 统计时间
	 */
		@Schema(description = "统计时间")
		private Timestamp staTime;
}
