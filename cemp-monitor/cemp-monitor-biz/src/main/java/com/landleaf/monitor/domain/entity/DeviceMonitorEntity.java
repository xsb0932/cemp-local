package com.landleaf.monitor.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.landleaf.pgsql.base.TenantBaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;


/**
 * 设备-监测平台实体类
 *
 * @author hebin
 * @since 2023-06-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(name = "DeviceMonitorEntity对象", description = "设备-监测平台")
@TableName("tb_device_monitor")
public class DeviceMonitorEntity extends TenantBaseEntity {

    /**
     * 设备id
     */
    @Schema(description = "设备id")
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 项目id（全局唯一id）
     */
    @Schema(description = "项目id（全局唯一id）")
    private String bizProjectId;

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


}
