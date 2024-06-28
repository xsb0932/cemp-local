package com.landleaf.redis.dao.dto;

import lombok.Data;

@Data
public class DeviceInfoCacheDTO {
    /**
     * 设备id
     */
    private Long id;

    /**
     * 项目id（全局唯一id）
     */
    private String bizProjectId;

    /**
     * 分区id（全局唯一id）
     */
    private String bizAreaId;

    /**
     * 分区路径path
     */
    private String areaPath;

    /**
     * 设备id（全局唯一id）
     */
    private String bizDeviceId;

    /**
     * 设备名称
     */
    private String name;

    /**
     * 产品id（全局唯一id）
     */
    private String bizProductId;

    /**
     * 品类id（全局唯一id）
     */
    private String bizCategoryId;

    /**
     * 设备编码（校验唯一）
     */
    private String code;

    /**
     * 租户id
     */
    private Long tenantId;

    /**
     * 设备位置
     */
    private String locationDesc;

    /**
     * 设备描述
     */
    private String deviceDesc;

    /**
     * 外部设备id
     */
    private String sourceDeviceId;

    /**
     * 项目名称
     */
    private String projectName;

    /**
     * 产品id
     */
    private Long productId;

    /**
     * 项目id
     */
    private Long projectId;
}
