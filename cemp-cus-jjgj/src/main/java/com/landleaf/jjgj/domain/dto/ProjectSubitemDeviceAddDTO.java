package com.landleaf.jjgj.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * ProjectSubitemDeviceEntity对象的新增时的参数封装
 *
 * @author hebin
 * @since 2023-06-25
 */
@Data
@Schema(name = "ProjectSubitemDeviceAddDTO对象", description = "ProjectSubitemDeviceEntity对象的新增时的参数封装")
public class ProjectSubitemDeviceAddDTO {

	/**
	 * id
	 */
		@Schema(description = "id")
				@NotNull(groups = {UpdateGroup.class},message = "id不能为空")
		private Long id;

	/**
	 * 分项ID
	 */
		@Schema(description = "分项ID")
			private Long subitemId;

	/**
	 * 设备ID
	 */
		@Schema(description = "设备ID")
			private String deviceId;

	/**
	 * 设备名称
	 */
		@Schema(description = "设备名称")
			private String deviceName;

	/**
	 * 计算标志位1,-1
	 */
		@Schema(description = "计算标志位1,-1")
			private String computeTag;

	/**
	 * 租户id
	 */
		@Schema(description = "租户id")
			private Long tenantId;

	public interface AddGroup {
	}

	public interface UpdateGroup {
	}
}
