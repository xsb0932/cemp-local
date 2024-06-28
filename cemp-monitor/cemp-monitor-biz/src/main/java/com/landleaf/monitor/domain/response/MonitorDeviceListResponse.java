package com.landleaf.monitor.domain.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 监控设备列表
 *
 * @author 张力方
 * @since 2023/7/20
 **/
@Data
@Schema(name = "MonitorDeviceListResponse", description = "监控设备列表")
public class MonitorDeviceListResponse {
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
     * 项目名称
     */
    @Schema(name = "项目名称")
    private String projectName;

    /**
     * 空间id
     */
    @Schema(name = "空间id")
    private String bizAreaId;

    /**
     * 空间名称
     */
    @Schema(name = "空间名称")
    private String areaName;

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
     * 产品名称
     */
    @Schema(name = "产品名称")
    private String productName;

    /**
     * 品类id（全局唯一id）
     */
    @Schema(name = "品类id（全局唯一id）")
    private String bizCategoryId;

    /**
     * 品类名称
     */
    @Schema(name = "品类名称")
    private String categoryName;

    /**
     * 设备编码（校验唯一）
     */
    @Schema(name = "设备编码（校验唯一）")
    private String code;

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

    /**
     * 外部设备id
     */
    @Schema(description = "外部设备id")
    private String sourceDeviceId;


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
