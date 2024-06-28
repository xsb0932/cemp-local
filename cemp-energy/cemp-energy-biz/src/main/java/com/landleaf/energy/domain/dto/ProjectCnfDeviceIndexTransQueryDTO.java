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
 * 设备品类和指标维度转换配置表的查询时的参数封装
 *
 * @author hebin
 * @since 2023-06-25
 */
@Data
@EqualsAndHashCode(callSuper=false)
@Schema(name = "ProjectCnfDeviceIndexTransQueryDTO对象", description = "设备品类和指标维度转换配置表的查询时的参数封装")
public class ProjectCnfDeviceIndexTransQueryDTO extends PageParam {

	/**
	 * id
	 */
		@Schema(name = "id")
		private Long id;

	/**
	 * 品类id
	 */
		@Schema(name = "品类id")
		private String bizCategoryId;

	/**
	 * 品类代码
	 */
		@Schema(name = "品类代码")
		private String bizCategoryCode;

	/**
	 * 转换后的维度代码
	 */
		@Schema(name = "转换后的维度代码")
		private String transIndexCode;

	/**
	 * 转换后的维度名称
	 */
		@Schema(name = "转换后的维度名称")
		private String transIndexName;

	/**
	 * 项目ID
	 */
		@Schema(name = "项目ID")
		private String bizProjectId;

	/**
	 * 租户ID
	 */
		@Schema(name = "租户ID")
		private Long tenantId;

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