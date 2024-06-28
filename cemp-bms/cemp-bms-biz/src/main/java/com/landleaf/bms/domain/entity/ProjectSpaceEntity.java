package com.landleaf.bms.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.landleaf.pgsql.base.TenantBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 项目-空间管理
 *
 * @author yue lin
 * @since 2023/7/12 14:51
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "tb_project_space")
public class ProjectSpaceEntity extends TenantBaseEntity {

    /**
     * 区域ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 区域名称
     */
    @TableField("name")
    private String name;

    /**
     * 区域父级ID
     */
    @TableField("parent_id")
    private Long parentId;

    /**
     * 区域类型
     */
    @TableField("type")
    private String type;

    /**
     * 区域面积
     */
    @TableField("proportion")
    private BigDecimal proportion;

    /**
     * 区域备注
     */
    @TableField("description")
    private String description;

    /**
     * 项目ID
     */
    @TableField("project_id")
    private Long projectId;

    /**
     * 业务ID
     */
    @TableField("biz_id")
    private String bizId;

}
