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
 * ProjectSubitemDeviceEntity对象的查询时的参数封装
 *
 * @author hebin
 * @since 2023-06-25
 */
@Data
@EqualsAndHashCode(callSuper=false)
@Schema(name = "ProjectSubitemDeviceQueryDTO对象", description = "ProjectSubitemDeviceEntity对象的查询时的参数封装")
public class ProjectSubitemDeviceQueryDTO extends PageParam {

	/**
	 * id
	 */
		@Schema(name = "id")
		private Long id;

	/**
	 * 分项ID
	 */
		@Schema(name = "分项ID")
		private Long subitemId;

	/**
	 * 设备ID
	 */
		@Schema(name = "设备ID")
		private String deviceId;

	/**
	 * 设备名称
	 */
		@Schema(name = "设备名称")
		private String deviceName;

	/**
	 * 计算标志位1,-1
	 */
		@Schema(name = "计算标志位1,-1")
		private String computeTag;

	/**
	 * 租户id
	 */
		@Schema(name = "租户id")
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