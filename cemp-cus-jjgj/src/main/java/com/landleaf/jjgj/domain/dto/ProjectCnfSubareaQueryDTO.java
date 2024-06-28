package com.landleaf.jjgj.domain.dto;

import com.landleaf.comm.base.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * ProjectCnfSubareaEntity对象的查询时的参数封装
 *
 * @author hebin
 * @since 2023-06-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "ProjectCnfSubareaQueryDTO对象", description = "ProjectCnfSubareaEntity对象的查询时的参数封装")
public class ProjectCnfSubareaQueryDTO extends PageParam{

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
 * 开始时间
 */
@Schema(name = "开始时间,格式为yyyy-MM-dd")
private String startTime;

/**
 * 结束时间
 */
@Schema(name = "结束时间,格式为yyyy-MM-dd")
private String endTime;
        }
