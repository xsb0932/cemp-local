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
 * 设备品类和指标维度转换配置表实体类
 *
 * @author hebin
 * @since 2023-06-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(name = "ProjectCnfDeviceIndexTransEntity对象", description = "设备品类和指标维度转换配置表")
@TableName("tb_project_cnf_device_index_trans")
public class ProjectCnfDeviceIndexTransEntity extends BaseEntity {

    /**
     * id
     */
    @Schema(description = "id")
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 品类id
     */
    @Schema(description = "品类id")
    private String bizCategoryId;

    /**
     * 品类代码
     */
    @Schema(description = "品类代码")
    private String bizCategoryCode;

    /**
     * 转换后的维度代码
     */
    @Schema(description = "转换后的维度代码")
    private String transIndexCode;

    /**
     * 转换后的维度名称
     */
    @Schema(description = "转换后的维度名称")
    private String transIndexName;

    /**
     * 项目ID
     */
    @Schema(description = "项目ID")
    private String bizProjectId;

    /**
     * 租户ID
     */
    @Schema(description = "租户ID")
    private Long tenantId;
}
