package com.landleaf.energy.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.landleaf.pgsql.base.TenantBaseEntity;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 计划用水
 *
 * @author Truth
 * @since 2023/8/10 15:12
 **/
@Data
@TableName(value = "tb_project_planned_water")
public class PlannedWaterEntity extends TenantBaseEntity {

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
     * 计划用水年份
     */
    @TableField("year")
    private String year;

    /**
     * 计划用水月份
     */
    @TableField("month")
    private String month;

    /**
     * 计划用水量
     */
    @TableField("plan_water_consumption")
    private BigDecimal planWaterConsumption;

}
