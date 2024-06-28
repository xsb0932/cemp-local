package com.landleaf.jjgj.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.landleaf.pgsql.base.TenantBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 光伏配置表
 *
 * @author yue lin
 * @since 2023/7/26 13:19
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "tb_project_cnf_pv")
public class ProjectCnfPvEntity extends TenantBaseEntity {
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
     * 光伏上网模式
     */
    @TableField(value = "online_mode")
    private String onlineMode;

    /**
     * 总额定功率
     */
    @TableField(value = "total_rp")
    private BigDecimal totalRp;

    /**
     * 上网电价
     */
    @TableField(value = "price")
    private BigDecimal price;
}
