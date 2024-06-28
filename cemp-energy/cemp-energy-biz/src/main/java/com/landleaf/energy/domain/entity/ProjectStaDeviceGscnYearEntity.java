package com.landleaf.energy.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.landleaf.pgsql.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.sql.Timestamp;


/**
 * 统计表-设备指标-储能系统-统计年实体类
 *
 * @author hebin
 * @since 2023-06-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(name = "ProjectStaDeviceGscnYearEntity", description = "统计表-设备指标-储能系统-统计年")
@TableName("tb_project_sta_device_gscn_year")
public class ProjectStaDeviceGscnYearEntity extends BaseEntity {

    /**
     * id
     */
    @Schema(description = "id")
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 设备ID
     */
    @Schema(description = "设备ID")
    private String bizDeviceId;

    /**
     * 产品ID
     */
    @Schema(description = "产品ID")
    private String bizProductId;

    /**
     * 品类ID
     */
    @Schema(description = "品类ID")
    private String bizCategoryId;

    /**
     * 项目ID
     */
    @Schema(description = "项目ID")
    private String bizProjectId;

    /**
     * 项目代码
     */
    @Schema(description = "项目代码")
    private String projectCode;

    /**
     * 租户ID
     */
    @Schema(description = "租户ID")
    private Long tenantId;

    /**
     * 租户代码
     */
    @Schema(description = "租户代码")
    private String tenantCode;

    /**
     * 统计-年
     */
    @Schema(description = "统计-年")
    private String year;

    /**
     * 在线时长
     */
    @Schema(description = "在线时长")
    @TableField("gscn_online_time_total")
    private BigDecimal gscnOnlineTimeTotal;

    /**
     * 充电时长
     */
    @Schema(description = "充电时长")
    @TableField("gscn_charge_time_total")
    private BigDecimal gscnChargeTimeTotal;

    /**
     * 放电时长
     */
    @Schema(description = "放电时长")
    @TableField("gscn_discharge_time_total")
    private BigDecimal gscnDischargeTimeTotal;

    /**
     * 待机时长
     */
    @Schema(description = "待机时长")
    @TableField("gscn_standby_time_total")
    private BigDecimal gscnStandbyTimeTotal;

    /**
     * 充电电量
     */
    @Schema(description = "充电电量")
    @TableField("gscn_epimport_total")
    private BigDecimal gscnEpimportTotal;

    /**
     * 放电电量
     */
    @Schema(description = "放电电量")
    @TableField("gscn_epexport_total")
    private BigDecimal gscnEpexportTotal;
    /**
     * 统计时间
     */
    @Schema(description = "统计时间")
    private Timestamp staTime;

    /**
     * 手工维护标识(0-系统 1手工)
     */
    @Schema(description = "手工维护标识(0-系统 1手工)")
    private Integer manualFlag;
}
