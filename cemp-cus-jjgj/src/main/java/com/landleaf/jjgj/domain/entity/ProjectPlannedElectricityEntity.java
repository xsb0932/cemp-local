package com.landleaf.jjgj.domain.entity;

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
 * 计划用电实体类
 *
 * @author hebin
 * @since 2023-09-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(name = "ProjectPlannedElectricityEntity", description = "计划用电")
@TableName("tb_project_planned_electricity")
public class ProjectPlannedElectricityEntity extends BaseEntity {

    /**
     * id
     */
    @Schema(description = "id")
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 项目业务ID
     */
    @Schema(description = "项目业务ID")
    private String projectBizId;

    /**
     * 计划用电年份
     */
    @Schema(description = "计划用电年份")
    private String year;

    /**
     * 计划用电月份
     */
    @Schema(description = "计划用电月份")
    private String month;

    /**
     * 计划用电量
     */
    @Schema(description = "计划用电量")
    private BigDecimal planElectricityConsumption;

    /**
     * 租户id
     */
    @Schema(description = "租户id")
    private Long tenantId;
}
