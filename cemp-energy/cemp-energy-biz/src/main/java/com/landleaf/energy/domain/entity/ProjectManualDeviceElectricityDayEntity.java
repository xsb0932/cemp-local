package com.landleaf.energy.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.landleaf.pgsql.base.TenantBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.sql.Timestamp;


/**
 * 手抄表-月
 *
 * @author tycoon
 * @since 2023-08-16
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tb_project_manual_device_electricity_day")
public class ProjectManualDeviceElectricityDayEntity extends TenantBaseEntity {

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 设备ID
     */
    @TableField("biz_device_id")
    private String bizDeviceId;

    /**
     * 产品ID
     */
    @TableField("biz_product_id")
    private String bizProductId;

    /**
     * 品类ID
     */
    @TableField("biz_category_id")
    private String bizCategoryId;

    /**
     * 项目ID
     */
    @TableField("biz_project_id")
    private String bizProjectId;

    /**
     * 统计-年
     */
    @TableField("year")
    private String year;

    /**
     * 统计-月
     */
    @TableField("month")
    private String month;

    /**
     * 统计-日
     */
    @TableField("day")
    private String day;

    /**
     * 有功用电量
     */
    @TableField("energymeter_epimport_total")
    private BigDecimal energymeterEpimportTotal;

    /**
     * 统计时间
     */
    @TableField("sta_time")
    private Timestamp staTime;

    /**
     * 期初表显
     */
    @TableField("open_displays_value")
    private BigDecimal openDisplaysValue;

    /**
     * 期末表显
     */
    @TableField("close_displays_value")
    private BigDecimal closeDisplaysValue;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

}
