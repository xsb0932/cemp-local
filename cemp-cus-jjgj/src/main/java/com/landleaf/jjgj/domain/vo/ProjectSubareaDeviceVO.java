package com.landleaf.jjgj.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * ProjectSubareaDeviceEntity对象的展示信息封装
 *
 * @author hebin
 * @since 2023-06-25
 */
@Data
@Schema(name = "ProjectSubareaDeviceVO对象", description = "ProjectSubareaDeviceEntity对象的展示信息封装")
public class ProjectSubareaDeviceVO {

	/**
	 * id
	 */
		@Schema(description = "id")
		private Long id;

	/**
	 * 分区ID
	 */
		@Schema(description = "分区ID")
		private Long subareadId;

	/**
	 * 设备ID
	 */
		@Schema(description = "设备ID")
		private String deviceId;

	/**
	 * 计算标志位1,-1
	 */
		@Schema(description = "计算标志位1,-1")
		private String computeTag;

	/**
	 * 租户ID
	 */
		@Schema(description = "租户ID")
		private Long tenantId;
}
