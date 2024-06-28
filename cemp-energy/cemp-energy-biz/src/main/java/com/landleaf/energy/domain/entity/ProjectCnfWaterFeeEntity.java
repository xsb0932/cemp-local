package com.landleaf.energy.domain.entity;

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
 * 用水费用配置表实体类
 *
 * @author hebin
 * @since 2023-07-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(name = "ProjectCnfWaterFeeEntity", description = "用水费用配置表")
@TableName("tb_project_cnf_water_fee")
public class ProjectCnfWaterFeeEntity extends TenantBaseEntity {

    /**
     * 用水配置id
     */
    @Schema(description = "用水配置id")
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

    /**
     * 污水比例
     */
    @Schema(description = "污水比例")
    private BigDecimal sewageRatio;

    /**
     * 污水处理价格
     */
    @Schema(description = "污水处理价格")
    private BigDecimal sewagePrice;
}