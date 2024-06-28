package com.landleaf.jjgj.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.landleaf.pgsql.base.TenantBaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;


/**
 * 燃气费用配置表实体类
 *
 * @author hebin
 * @since 2023-07-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(name = "ProjectCnfGasFeeEntity", description = "燃气费用配置表")
@TableName("tb_project_cnf_gas_fee")
public class ProjectCnfGasFeeEntity extends TenantBaseEntity {

    /**
     * 燃气配置id
     */
    @Schema(description = "燃气配置id")
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 项目ID
     */
    @Schema(description = "项目ID")
    private String projectId;

    /**
     * 收费模式，0=>单一价格
     */
    @Schema(description = "收费模式，0=>单一价格")
    private Integer chargingMode;

    /**
     * 燃气单价
     */
    @Schema(description = "燃气单价")
    private BigDecimal price;
}
