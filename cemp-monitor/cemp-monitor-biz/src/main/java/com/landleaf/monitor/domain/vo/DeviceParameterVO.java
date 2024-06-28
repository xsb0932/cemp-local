package com.landleaf.monitor.domain.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

import java.util.Date;

/**
 * 设备参数明细表的展示信息封装
 *
 * @author hebin
 * @since 2023-07-27
 */
@Data
@Schema(name = "DeviceParameterVO对象", description = "设备参数明细表的展示信息封装")
public class DeviceParameterVO {

	/**
	 * 租户ID
	 */
		@Schema(description = "租户ID")
		private Long tenantId;

	/**
	 * ID
	 */
		@Schema(description = "ID")
		private Long id;

	/**
	 * 产品ID
	 */
		@Schema(description = "产品ID")
		private Long productId;

	/**
	 * 功能标识符
	 */
		@Schema(description = "功能标识符")
		private String identifier;

	/**
	 * 功能类型
     * 系统默认功能、系统可选功能、标准可选功能
	 */
		@Schema(description = "功能类型  * 系统默认功能、系统可选功能、标准可选功能")
		private String functionName;

	/**
	 * 参数值
	 */
		@Schema(description = "参数值")
		private String value;

	/**
	 * 设备id
	 */
		@Schema(description = "设备id")
		private String bizDeviceId;
}
