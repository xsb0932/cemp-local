package com.landleaf.energy.domain.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
@Schema(name = "统计通用对象", description = "统计通用对象")
public class ProjectStaKpiVO {

    /**
     * 设备id
     */
    @Schema(description = "设备id")
    private String bizDeviceId;

    /**
     * 设备名称
     */
    @Schema(description = "设备名称")
    private String deviceName;

    /**
     * 设备编码
     */
    @Schema(description = "设备编码")
    private String deviceCode;

    /**
     * 产品ID
     */
    @Schema(description = "产品ID")
    private String bizProductId;

    /**
     * 品类ID
     */
    @Schema(description = "品类ID")
    private String bizCategoryId;

    /**
     * 项目ID
     */
    @Schema(description = "项目ID")
    private String bizProjectId;

    /**
     * 项目代码
     */
    @Schema(description = "项目代码")
    private String projectCode;

    /**
     * 项目名称
     */
    @Schema(description = "项目名称")
    private String projectName;

    /**
     * 租户ID
     */
    @Schema(description = "租户ID")
    private Long tenantId;

    /**
     * 租户代码
     */
    @Schema(description = "租户代码")
    private String tenantCode;

    /**
     * 统计-年
     */
    @Schema(description = "统计-年")
    private String year;

    /**
     * 统计-月
     */
    @Schema(description = "统计-月")
    private String month;

    /**
     * 统计-日
     */
    @Schema(description = "统计-日")
    private String day;

    /**
     * 统计-小时
     */
    @Schema(description = "统计-小时")
    private String hour;

    /**
     * 统计时间
     */
    @Schema(description = "统计时间")
    private String staTime;
}
