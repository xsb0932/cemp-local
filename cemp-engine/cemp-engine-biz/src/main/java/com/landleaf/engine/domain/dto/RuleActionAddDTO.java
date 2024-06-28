package com.landleaf.engine.domain.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.sql.Timestamp;

import java.util.Date;
import java.util.Map;

/**
 * RuleActionEntity对象的新增时的参数封装
 *
 * @author hebin
 * @since 2024-04-23
 */
@Data
@Schema(name = "RuleActionAddDTO", description = "RuleActionEntity对象的新增时的参数封装")
public class RuleActionAddDTO {

    /**
     * 规则编号
     */
    @Schema(description = "规则编号")
    @NotNull(groups = {UpdateGroup.class}, message = "规则编号不能为空")
    private Long id;

    /**
     * 动作类型：见字典RULE_ACTION_TYPE
     */
    @Schema(description = "动作类型：见字典RULE_ACTION_TYPE")
    private String actionType;

    /**
     * 执行动作的产品业务id
     */
    @Schema(description = "执行动作的产品业务id")
    private String actionBizProdId;

    /**
     * 执行动作的设备
     */
    @Schema(description = "执行动作的设备")
    private String actionBizDeviceId;

    /**
     * 规则的业务编码
     */
    @Schema(description = "规则的业务编码")
    private String bizRuleId;

    /**
     * 告警CODE
     */
    @Schema(description = "告警CODE")
    private String alarmCode;

    /**
     * 告警触发等级 数据字典（ALARM_LEVEL）
     */
    @Schema(description = "告警触发等级 数据字典（ALARM_LEVEL）")
    private String alarmTriggerLevel;

    /**
     * 告警复归等级 数据字典（ALARM_LEVEL）
     */
    @Schema(description = "告警复归等级 数据字典（ALARM_LEVEL）")
    private String alarmRelapseLevel;

    /**
     * 告警触发确认方式 数据字典（ALARM_CONFIRM_TYPE）
     */
    @Schema(description = "告警触发确认方式 数据字典（ALARM_CONFIRM_TYPE）")
    private String alarmTriggerConfirmType;

    /**
     * 告警触发描述
     */
    @Schema(description = "告警触发描述")
    private String alarmTriggerDesc;

    /**
     * 告警复归描述
     */
    @Schema(description = "告警复归描述")
    private String alarmRelapseDesc;

    /**
     * 告警复归确认方式 数据字典（ALARM_CONFIRM_TYPE）
     */
    @Schema(description = "告警复归确认方式 数据字典（ALARM_CONFIRM_TYPE）")
    private String alarmRelapseConfirmType;

    /**
     * 服务id
     */
    @Schema(description = "服务id")
    private Long serviceId;

    /**
     * 服务参数
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, String> serviceParameter;

    /**
     * 服务下发间隔时间
     */
    @Schema(description = "服务下发间隔时间")
    private Integer serviceSendingInterval;

    @Schema(description = "给前端冗余的产品id")
    private Long actionProdId;

    public interface AddGroup {
    }

    public interface UpdateGroup {
    }
}