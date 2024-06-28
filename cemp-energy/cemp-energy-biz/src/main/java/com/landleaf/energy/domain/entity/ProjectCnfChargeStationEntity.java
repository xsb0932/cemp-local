package com.landleaf.energy.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.landleaf.pgsql.base.TenantBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 充电桩配置表
 *
 * @author yue lin
 * @since 2023/7/26 13:17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "tb_project_cnf_charge_station")
public class ProjectCnfChargeStationEntity extends TenantBaseEntity {
    /**
     * 燃气配置id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 项目ID
     */
    @TableField(value = "project_id")
    private String projectId;

    /**
     * 充电桩计费模式 01：服务费+电费
     */
    @TableField(value = "billing_mode")
    private String billingMode;

    /**
     * 服务费单价
     */
    @TableField(value = "price")
    private BigDecimal price;

    /**
     * 额定功率
     */
    @TableField(value = "rp")
    private BigDecimal rp;

    /**
     * 交流桩数量
     */
    @TableField(value = "ac_station_num")
    private Long acStationNum;

    /**
     * 直流桩数量
     */
    @TableField(value = "dc_station_num")
    private Long dcStationNum;
}