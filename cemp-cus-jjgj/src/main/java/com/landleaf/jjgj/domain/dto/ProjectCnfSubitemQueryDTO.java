package com.landleaf.jjgj.domain.dto;

import com.landleaf.comm.base.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 配置表-分项的查询时的参数封装
 *
 * @author hebin
 * @since 2023-07-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "ProjectCnfSubitemQueryDTO", description = "配置表-分项的查询时的参数封装")
public class ProjectCnfSubitemQueryDTO extends PageParam{

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
 * 指标大类
 */
        @Schema(description = "指标大类")
    private String kpiType;

/**
 * 指标大类代码
 */
        @Schema(description = "指标大类代码")
    private String kpiTypeCode;

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
