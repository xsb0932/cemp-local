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
 * 告警推送方式实体类
 *
 * @author hebin
 * @since 2024-05-31
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(name = "AlarmPushStatusEntity", description = "告警推送方式")
@TableName("tb_alarm_push_status")
public class AlarmPushStatusEntity extends TenantBaseEntity {

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
     * 邮件推送状态（系统字典：01启用 02停用）
     */
    @Schema(description = "邮件推送状态（系统字典：01启用 02停用）")
    private String emailStatus;

    /**
     * 短信推送状态（系统字典：01启用 02停用）
     */
    @Schema(description = "短信推送状态（系统字典：01启用 02停用）")
    private String messageStatus;

    /**
     * 钉钉机器人推送状态（系统字典：01启用 02停用）
     */
    @Schema(description = "钉钉机器人推送状态（系统字典：01启用 02停用）")
    private String dingStatus;

    /**
     * 邮件配置顺序
     */
    @Schema(description = "邮件配置顺序")
    private Integer emailSort;

    /**
     * 短信配置顺序
     */
    @Schema(description = "短信配置顺序")
    private Integer messageSort;

    /**
     * 钉钉配置顺序
     */
    @Schema(description = "钉钉配置顺序")
    private Integer dingSort;
}