package com.landleaf.energy.domain.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

import java.util.Date;

/**
 * ProjectSubitemDeviceEntity对象的展示信息封装
 *
 * @author hebin
 * @since 2023-06-25
 */
@Data
@Schema(name = "ProjectSubitemDeviceVO对象", description = "ProjectSubitemDeviceEntity对象的展示信息封装")
public class ProjectSubitemDeviceVO {

	/**
	 * id
	 */
		@Schema(description = "id")
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
}
