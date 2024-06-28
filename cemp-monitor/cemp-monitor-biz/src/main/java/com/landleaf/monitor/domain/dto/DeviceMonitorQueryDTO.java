package com.landleaf.monitor.domain.dto;

import com.landleaf.comm.base.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 设备-监测平台的查询时的参数封装
 *
 * @author hebin
 * @since 2023-06-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "DeviceMonitorQueryDTO对象", description = "设备-监测平台的查询时的参数封装")
public class DeviceMonitorQueryDTO extends PageParam {

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
     * 开始时间
     */
    @Schema(description = "开始时间,格式为yyyy-MM-dd")
    private String startTime;

    /**
     * 结束时间
     */
    @Schema(description = "结束时间,格式为yyyy-MM-dd")
    private String endTime;

    /**
     * 项目id
     */
    @Schema(description = "项目id")
    private Long projectId;

    /**
     * 项目id 集合
     */
    @Schema(description = "项目id集合")
    private List<String> projectIds;
}
