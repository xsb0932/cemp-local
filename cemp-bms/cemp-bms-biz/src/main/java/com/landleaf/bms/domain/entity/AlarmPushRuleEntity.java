package com.landleaf.bms.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.landleaf.pgsql.base.TenantBaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;


/**
 * 告警推送规则实体类
 *
 * @author hebin
 * @since 2024-05-31
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(name = "AlarmPushRuleEntity", description = "告警推送规则")
@TableName("tb_alarm_push_rule")
public class AlarmPushRuleEntity extends TenantBaseEntity {

    /**
     * id
     */
    @Schema(description = "id")
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 推送规则名称
     */
    @Schema(description = "推送规则名称")
    private String ruleName;

    /**
     * 推送状态（系统字典:01启用 02停用）
     */
    @Schema(description = "推送状态（系统字典:01启用 02停用）")
    private String ruleStatus;

    /**
     * 推送说明
     */
    @Schema(description = "推送说明")
    private String description;
}