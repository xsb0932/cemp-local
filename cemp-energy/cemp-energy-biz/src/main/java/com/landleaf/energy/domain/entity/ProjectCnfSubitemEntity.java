package com.landleaf.energy.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.landleaf.pgsql.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;


/**
 * 配置表-分项实体类
 *
 * @author hebin
 * @since 2023-07-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(name = "ProjectCnfSubitemEntity对象", description = "配置表-分项")
@TableName("tb_project_cnf_subitem")
public class ProjectCnfSubitemEntity extends BaseEntity {

    /**
     * 分项id
     */
    @Schema(description = "分项id")
    @TableId(type = IdType.AUTO)
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
}
