package com.landleaf.jjgj.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.landleaf.pgsql.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;


/**
 * 配置表-分区实体类
 *
 * @author hebin
 * @since 2023-07-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(name = "ProjectCnfSubareaEntity对象", description = "配置表-分区")
@TableName("tb_project_cnf_subarea")
public class ProjectCnfSubareaEntity extends BaseEntity {

    /**
     * 分区id
     */
    @Schema(description = "分区id")
    @TableId(type = IdType.AUTO)
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
}
