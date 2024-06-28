package com.landleaf.energy.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.landleaf.pgsql.base.TenantBaseEntity;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 计划用电
 *
 * @author Truth
 * @since 2023/8/10 15:12
 **/
@Data
@TableName(value = "tb_project_planned_electricity")
public class PlannedElectricityEntity extends TenantBaseEntity {

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 项目业务ID
     */
    @TableField("project_biz_id")
    private String projectBizId;

    /**
     * 计划用电年份
     */
    @TableField("year")
    private String year;

    /**
     * 计划用电月份
     */
    @TableField("month")
    private String month;

    /**
     * 计划用电量
     */
    @TableField("plan_electricity_consumption")
    private BigDecimal planElectricityConsumption;

}
