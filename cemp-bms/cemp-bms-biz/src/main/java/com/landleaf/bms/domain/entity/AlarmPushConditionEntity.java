package com.landleaf.bms.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.landleaf.pgsql.base.TenantBaseEntity;
import com.landleaf.pgsql.handler.type.StringListTypeHandler;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;


/**
 * 告警推送条件实体类
 *
 * @author hebin
 * @since 2024-05-31
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(name = "AlarmPushConditionEntity", description = "告警推送条件")
@TableName(value = "tb_alarm_push_condition", autoResultMap = true)
public class AlarmPushConditionEntity extends TenantBaseEntity {

    /**
     * id
     */
    @Schema(description = "id")
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 推送规则id
     */
    @Schema(description = "推送规则id")
    private Long ruleId;

    /**
     * 项目列表（0全部）
     */
    @Schema(description = "项目列表（0全部）")
    @TableField(typeHandler = StringListTypeHandler.class, updateStrategy = FieldStrategy.IGNORED)
    private List<String> bizProjectIdList;

    /**
     * 告警类型（系统字典，为空表示全部）
     */
    @Schema(description = "告警类型（系统字典，为空表示全部）")
    @TableField(typeHandler = StringListTypeHandler.class, updateStrategy = FieldStrategy.IGNORED)
    private List<String> alarmTypeList;

    /**
     * 告警等级（系统字典，为空表示全部）
     */
    @Schema(description = "告警等级（系统字典，为空表示全部）")
    @TableField(typeHandler = StringListTypeHandler.class, updateStrategy = FieldStrategy.IGNORED)
    private List<String> alarmLevelList;

    /**
     * 告警状态（系统字典，为空表示全部）
     */
    @Schema(description = "告警状态（系统字典，为空表示全部）")
    @TableField(typeHandler = StringListTypeHandler.class, updateStrategy = FieldStrategy.IGNORED)
    private List<String> alarmStatusList;

    /**
     * 告警类型启用状态（系统字典:01启用 02停用）
     */
    @Schema(description = "告警类型启用状态（系统字典:01启用 02停用）")
    private String alarmTypeFlag;

    /**
     * 告警等级启用状态（系统字典:01启用 02停用）
     */
    @Schema(description = "告警等级启用状态（系统字典:01启用 02停用）")
    private String alarmLevelFlag;

    /**
     * 告警状态启用状态（系统字典:01启用 02停用）
     */
    @Schema(description = "告警状态启用状态（系统字典:01启用 02停用）")
    private String alarmStatusFlag;

    /**
     * 告警类型配置顺序
     */
    @Schema(description = "告警类型配置顺序")
    private Integer alarmTypeSort;

    /**
     * 告警等级配置顺序
     */
    @Schema(description = "告警等级配置顺序")
    private Integer alarmLevelSort;

    /**
     * 告警状态配置顺序
     */
    @Schema(description = "告警状态配置顺序")
    private Integer alarmStatusSort;

}