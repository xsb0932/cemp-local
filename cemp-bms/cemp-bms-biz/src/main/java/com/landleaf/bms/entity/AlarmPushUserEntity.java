package com.landleaf.bms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.landleaf.pgsql.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;


/**
 * 实体类
 *
 * @author hebin
 * @since 2024-05-31
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(name = "AlarmPushUserEntity", description = "AlarmPushUserEntity对象")
@TableName("tb_alarm_push_user")
public class AlarmPushUserEntity extends BaseEntity {

    /**
     * 自增id
     */
    @Schema(description = "自增id")
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 告警推送状态id
     */
    @Schema(description = "告警推送状态id")
    private Long statusId;

    /**
     * 推送规则id
     */
    @Schema(description = "推送规则id")
    private Long ruleId;

    /**
     * 用户id
     */
    @Schema(description = "用户id")
    private Long userId;

    /**
     * 租户id
     */
    @Schema(description = "租户id")
    private Long tenantId;

    /**
     * 钉钉机器人url
     */
    @Schema(description = "钉钉机器人url")
    private String dingUrl;

    /**
     * 推送方式（0邮件 1短信 2钉钉）
     */
    @Schema(description = "推送方式（0邮件 1短信 2钉钉）")
    private Integer pushType;
}