package com.landleaf.monitor.domain.vo;

import com.landleaf.data.api.device.dto.DeviceCurrentDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

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
     * 空间名称
     */
    @Schema(description = "空间名称")
    private String areaName;

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
     * 产品名称
     */
    @Schema(description = "产品名称")
    private String productName;

    /**
     * 品类id（全局唯一id）
     */
    @Schema(description = "品类id（全局唯一id）")
    private String bizCategoryId;

    /**
     * 品类名称
     */
    @Schema(description = "品类名称")
    private String categoryName;

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

    @Schema(description = "设备当前状态")
    private DeviceCurrentDTO current;

    /**
     * 设备其他参数
     */
    @Schema(description = "设备参数")
    private List<DeviceParameterVO> deviceParameters;


}
