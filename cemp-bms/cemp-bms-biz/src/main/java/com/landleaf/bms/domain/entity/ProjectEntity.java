package com.landleaf.bms.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.landleaf.pgsql.base.TenantBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;

/**
 * 项目 entity
 *
 * @author 张力方
 * @since 2023/06/05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "tb_project", autoResultMap = true)
public class ProjectEntity extends TenantBaseEntity {
    /**
     * 项目id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 项目业务id（全局唯一id）
     */
    private String bizProjectId;

    /**
     * 项目名称
     */
    private String name;

    /**
     * 项目编码
     */
    private String code;

    /**
     * 项目业态code（字典配置）
     */
    private String bizType;

    /**
     * 面积
     */
    private BigDecimal area;

    /**
     * 能源类型
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> energyType;

    /**
     * 项目状态（字典配置 0规划 1建设 2运维）
     */
    private String status;

    /**
     * 能源子系统类型
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> energySubSystem;

    /**
     * 负责人
     */
    private String director;

    /**
     * 负责人电话
     */
    private String mobile;

    /**
     * 项目地址
     */
    private String address;

    /**
     * 项目归属管理节点业务id
     */
    private String parentBizNodeId;

    /**
     * 项目行政区域（tb_address）
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> addressCode;

    /**
     * 纬度
     */
    private String gdLatitude;

    /**
     * 经度
     */
    private String gdLongitude;

    /**
     * 项目描述
     */
    private String description;

    /**
     * 租户id
     */
    private Long tenantId;

    /**
     * 权限路径path（冗余）
     */
    private String path;

    /**
     * 项目对应的管理节点业务id
     */
    private String bizNodeId;

    /**
     * 负责人用户id
     */
    private Long directorUserId;
}

