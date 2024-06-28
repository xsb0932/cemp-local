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
 * DeviceMonitorEntity对象的查询时的参数封装
 *
 * @author hebin
 * @since 2023-06-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "DeviceMonitorQueryDTO对象", description = "DeviceMonitorEntity对象的查询时的参数封装")
public class DeviceMonitorQueryDTO extends PageParam {

    /**
     * 设备id
     */
    @Schema(name = "设备id")
    private Long id;

    /**
     * 项目id（全局唯一id）
     */
    @Schema(name = "项目id（全局唯一id）")
    private String bizProjectId;

    /**
     * 分区id（全局唯一id）
     */
    @Schema(name = "分区id（全局唯一id）")
    private String bizAreaId;

    /**
     * 分区路径path
     */
    @Schema(name = "分区路径path")
    private String areaPath;

    /**
     * 设备id（全局唯一id）
     */
    @Schema(name = "设备id（全局唯一id）")
    private String bizDeviceId;

    /**
     * 设备名称
     */
    @Schema(name = "设备名称")
    private String name;

    /**
     * 产品id（全局唯一id）
     */
    @Schema(name = "产品id（全局唯一id）")
    private String bizProductId;

    /**
     * 品类id（全局唯一id）
     */
    @Schema(name = "品类id（全局唯一id）")
    private String bizCategoryId;

    /**
     * 设备编码（校验唯一）
     */
    @Schema(name = "设备编码（校验唯一）")
    private String code;

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

	/**
	 * 设备描述
	 */
	@Schema(description = "设备描述")
	private String deviceDesc;

	/**
	 * 外部设备id
	 */
	@Schema(description = "外部设备id")
	private String sourceDeviceId;

	/**
	 * 项目名称
	 */
	@Schema(description = "项目名称")
	private String projectName;

	/**
	 * 产品id
	 */
	@Schema(description = "产品id")
	private Long productId;

	/**
	 * 项目id
	 */
	@Schema(description = "项目id")
	private Long projectId;
}
