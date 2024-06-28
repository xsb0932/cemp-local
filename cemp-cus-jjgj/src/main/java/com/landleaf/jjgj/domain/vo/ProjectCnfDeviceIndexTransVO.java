package com.landleaf.jjgj.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 设备品类和指标维度转换配置表的展示信息封装
 *
 * @author hebin
 * @since 2023-06-25
 */
@Data
@Schema(name = "ProjectCnfDeviceIndexTransVO对象", description = "设备品类和指标维度转换配置表的展示信息封装")
public class ProjectCnfDeviceIndexTransVO {

	/**
	 * id
	 */
		@Schema(description = "id")
		private Long id;

	/**
	 * 品类id
	 */
		@Schema(description = "品类id")
		private String bizCategoryId;

	/**
	 * 品类代码
	 */
		@Schema(description = "品类代码")
		private String bizCategoryCode;

	/**
	 * 转换后的维度代码
	 */
		@Schema(description = "转换后的维度代码")
		private String transIndexCode;

	/**
	 * 转换后的维度名称
	 */
		@Schema(description = "转换后的维度名称")
		private String transIndexName;

	/**
	 * 项目ID
	 */
		@Schema(description = "项目ID")
		private String bizProjectId;

	/**
	 * 租户ID
	 */
		@Schema(description = "租户ID")
		private Long tenantId;
}
