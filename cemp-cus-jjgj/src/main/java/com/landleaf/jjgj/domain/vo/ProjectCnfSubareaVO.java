package com.landleaf.jjgj.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * ProjectCnfSubareaEntity对象的展示信息封装
 *
 * @author hebin
 * @since 2023-06-25
 */
@Data
@Schema(name = "ProjectCnfSubareaVO对象", description = "ProjectCnfSubareaEntity对象的展示信息封装")
public class ProjectCnfSubareaVO {

    /**
     * 分区id
     */
    @Schema(description = "分区id")
    private Long id;

    /**
     * 分区名称
     */
    @Schema(description = "分区名称")
    private String name;

    /**
     * 分区类型
     */
    @Schema(description = "分区类型")
    private String type;

    /**
     * 父ID
     */
    @Schema(description = "父ID")
    private String parentId;

    /**
     * 租户ID
     */
    @Schema(description = "租户ID")
    private String path;

    /**
     * 租户ID
     */
    @Schema(description = "租户ID")
    private Long tenantId;

    /**
     * 项目ID
     */
    @Schema(description = "项目ID")
    private String projectId;

    /**
     * 指标大类
     */
    @Schema(description = "指标大类")
    private String kpiType;

    /**
     * 分区指标代码
     */
    @Schema(description = "分区指标代码")
    private String kpiSubtype;

    /**
     * 指标大类代码
     */
    @Schema(description = "指标大类代码")
    private String kpiTypeCode;

    /**
     * 分区类型代码
     */
    @Schema(description = "分区类型代码")
    private String typeCode;

    @Schema(description = "子分区")
    private List<ProjectCnfSubareaVO> children;

    @Schema(description = "分区绑定的设备")
    private List<DeviceMonitorVO> devices;

    @Schema(description = "分区绑定的设备描述")
    private String devicesDesc;


}
