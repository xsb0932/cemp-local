package com.landleaf.jjgj.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * ProjectSubareaDeviceEntity对象的新增时的参数封装
 *
 * @author hebin
 * @since 2023-06-25
 */
@Data
@Schema(name = "ProjectSubareaDeviceAddDTO对象", description = "ProjectSubareaDeviceEntity对象的新增时的参数封装")
public class ProjectSubareaDeviceAddDTO {

	/**
	 * id
	 */
		@Schema(description = "id")
				@NotNull(groups = {UpdateGroup.class},message = "id不能为空")
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

	public interface AddGroup {
	}

	public interface UpdateGroup {
	}
}
