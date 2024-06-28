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
 * ProjectStaSubareaYearEntity对象的查询时的参数封装
 *
 * @author hebin
 * @since 2023-06-25
 */
@Data
@EqualsAndHashCode(callSuper=false)
@Schema(name = "ProjectStaSubareaYearQueryDTO对象", description = "ProjectStaSubareaYearEntity对象的查询时的参数封装")
public class ProjectStaSubareaYearQueryDTO extends PageParam {

	/**
	 * id
	 */
		@Schema(name = "id")
		private Long id;

	/**
	 * 指标CODE
	 */
		@Schema(name = "指标CODE")
		private String kpiCode;

	/**
	 * 分区代码
	 */
		@Schema(name = "分区代码")
		private String subareaCode;

	/**
	 * 分区名字
	 */
		@Schema(name = "分区名字")
		private String subareaName;

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
	 * 统计值
	 */
		@Schema(name = "统计值")
		private BigDecimal staValue;

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