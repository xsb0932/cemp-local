package com.landleaf.energy.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.sql.Timestamp;

import java.util.Date;

/**
 * ProjectStaSubareaHourEntity对象的新增时的参数封装
 *
 * @author hebin
 * @since 2023-06-25
 */
@Data
@Schema(name = "ProjectStaSubareaHourAddDTO对象", description = "ProjectStaSubareaHourEntity对象的新增时的参数封装")
public class ProjectStaSubareaHourAddDTO {

	/**
	 * id
	 */
		@Schema(description = "id")
				@NotNull(groups = {UpdateGroup.class},message = "id不能为空")
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
	 * 统计-小时
	 */
		@Schema(description = "统计-小时")
			private String hour;

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

	public interface AddGroup {
	}

	public interface UpdateGroup {
	}
}
