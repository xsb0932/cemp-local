package com.landleaf.jjgj.domain.dto;

import com.landleaf.comm.base.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * ProjectSubareaDeviceEntity对象的查询时的参数封装
 *
 * @author hebin
 * @since 2023-06-25
 */
@Data
@EqualsAndHashCode(callSuper=false)
@Schema(name = "ProjectSubareaDeviceQueryDTO对象", description = "ProjectSubareaDeviceEntity对象的查询时的参数封装")
public class ProjectSubareaDeviceQueryDTO extends PageParam {

	/**
	 * id
	 */
		@Schema(name = "id")
		private Long id;

	/**
	 * 分区ID
	 */
		@Schema(name = "分区ID")
		private Long subareadId;

	/**
	 * 设备ID
	 */
		@Schema(name = "设备ID")
		private String deviceId;

	/**
	 * 计算标志位1,-1
	 */
		@Schema(name = "计算标志位1,-1")
		private String computeTag;

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
