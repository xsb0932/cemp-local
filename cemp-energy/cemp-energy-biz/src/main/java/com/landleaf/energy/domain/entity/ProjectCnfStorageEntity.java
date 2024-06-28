package com.landleaf.energy.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.landleaf.pgsql.base.TenantBaseEntity;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 储能配置
 *
 * @author Tycoon
 * @since 2023/8/14 13:20
 **/
@Data
@TableName(value = "tb_project_cnf_storage")
public class ProjectCnfStorageEntity extends TenantBaseEntity {
    /**
     * 燃气配置id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 项目ID
     */
    @TableField(value = "project_id")
    private String projectId;

    /**
     * 总额定功率
     */
    @TableField(value = "total_rp")
    private BigDecimal totalRp;

    /**
     * 总储电量
     */
    @TableField(value = "total_storage")
    private BigDecimal totalStorage;

}