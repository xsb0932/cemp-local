package com.landleaf.jjgj.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 配置表-分项的展示信息封装
 *
 * @author hebin
 * @since 2023-07-06
 */
@Data
@Schema(name = "ProjectCnfSubitemVO对象", description = "配置表-分项的展示信息封装")
public class ProjectCnfSubitemVO {

    /**
     * 分项id
     */
    @Schema(description = "分项id")
    private Long id;

    /**
     * 分项名称
     */
    @Schema(description = "分项名称")
    private String name;

    /**
     * 父项ID
     */
    @Schema(description = "父项ID")
    private String parentId;

    /**
     * 路径
     */
    @Schema(description = "路径")
    private String path;

    /**
     * 项目ID
     */
    @Schema(description = "项目ID")
    private String projectId;

    /**
     * 租户ID
     */
    @Schema(description = "租户ID")
    private Long tenantId;

    /**
     * 分项指标代码
     */
    @Schema(description = "分项指标代码")
    private String kpiSubtype;

    /**
     * 分项指标名称
     */
    @Schema(description = "分项指标名称")
    private String kpiSubtypeStr;

    /**
     * 指标大类
     */
    @Schema(description = "指标大类")
    private String kpiType;

    /**
     * 指标大类代码
     */
    @Schema(description = "指标大类代码")
    private String kpiTypeCode;

    @Schema(description = "子分区")
    private List<ProjectCnfSubitemVO> children;

    @Schema(description = "分项绑定的设备")
    private List<DeviceMonitorVO> devices;

    @Schema(description = "分项绑定的设备描述")
    private String devicesDesc;
}
