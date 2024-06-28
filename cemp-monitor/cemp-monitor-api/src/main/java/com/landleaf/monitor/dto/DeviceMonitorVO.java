package com.landleaf.monitor.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 设备-监测平台的展示信息封装
 *
 * @author hebin
 * @since 2023-06-05
 */
@Data
@Schema(name = "DeviceMonitorVO对象", description = "设备-监测平台的展示信息封装")
public class DeviceMonitorVO {

    /**
     * 设备id
     */
    @Schema(description = "设备id")
    private Long id;

    /**
     * 项目id（全局唯一id）
     */
    @Schema(description = "项目id（全局唯一id）")
    private String bizProjectId;

    /**
     * 项目名称
     */
    @Schema(description = "项目名称")
    private String bizProjectName;

    /**
     * 分区id（全局唯一id）
     */
    @Schema(description = "分区id（全局唯一id）")
    private String bizAreaId;

    /**
     * 分区路径path
     */
    @Schema(description = "分区路径path")
    private String areaPath;

    /**
     * 设备id（全局唯一id）
     */
    @Schema(description = "设备id（全局唯一id）")
    private String bizDeviceId;

    /**
     * 设备名称
     */
    @Schema(description = "设备名称")
    private String name;

    /**
     * 产品id（全局唯一id）
     */
    @Schema(description = "产品id（全局唯一id）")
    private String bizProductId;

    /**
     * 品类id（全局唯一id）
     */
    @Schema(description = "品类id（全局唯一id）")
    private String bizCategoryId;

    /**
     * 设备编码（校验唯一）
     */
    @Schema(description = "设备编码（校验唯一）")
    private String code;

    /**
     * 租户id
     */
    @Schema(description = "租户id")
    private Long tenantId;

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

    /**
     * 设备位置
     */
    @Schema(description = "设备位置")
    private String locationDesc;

    /**
     * 设备描述
     */
    @Schema(description = "设备描述")
    private String deviceDesc;


}
