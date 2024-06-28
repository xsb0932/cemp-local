package com.landleaf.energy.domain.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

import java.util.Date;

/**
 * ProjectStaSubareaDayEntity对象的展示信息封装
 *
 * @author hebin
 * @since 2023-06-25
 */
@Data
@Schema(name = "ProjectStaSubareaDayVO对象", description = "ProjectStaSubareaDayEntity对象的展示信息封装")
public class ProjectStaSubareaDayVO {

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
	 * 统计-天
	 */
		@Schema(description = "统计-天")
		private String day;

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
