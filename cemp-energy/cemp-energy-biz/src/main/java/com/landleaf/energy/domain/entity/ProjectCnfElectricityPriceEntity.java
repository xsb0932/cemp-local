package com.landleaf.energy.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Value;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.landleaf.pgsql.base.BaseEntity;

import java.math.BigDecimal;

import java.util.Date;
import java.sql.Timestamp;

import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;


/**
 * 电费配置表实体类
 *
 * @author hebin
 * @since 2024-03-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(name = "ProjectCnfElectricityPriceEntity", description = "电费配置表")
@TableName("tb_project_cnf_electricity_price")
public class ProjectCnfElectricityPriceEntity extends BaseEntity {

    /**
     * 分时配置id
     */
    @Schema(description = "分时配置id")
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 项目ID
     */
    @Schema(description = "项目ID")
    private String projectId;

    /**
     * 电费类型，见字典electricity_price_type
     */
    @Schema(description = "电费类型，见字典electricity_price_type")
    private String type;

    /**
     * 电价
     */
    @Schema(description = "电价")
    private BigDecimal price;

    /**
     * 租户ID
     */
    @Schema(description = " 租户ID")
    private Long tenantId;
}