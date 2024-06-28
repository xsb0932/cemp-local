package com.landleaf.engine.domain.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

import java.util.Date;

/**
 * RuleTriggerEntity对象的展示信息封装
 *
 * @author hebin
 * @since 2024-04-23
 */
@Data
@Schema(name = "RuleTriggerVO", description = "RuleTriggerEntity对象的展示信息封装")
public class RuleTriggerVO {

    /**
     * 规则编号
     */
    @Schema(description = "规则编号")
    private Long id;

    /**
     * 触发类型：见字典RULE_TRIGGER_TYPE
     */
    @Schema(description = "触发类型：见字典RULE_TRIGGER_TYPE")
    private String triggerType;

    /**
     * 触发类型描述
     */
    @Schema(description = "触发类型描述")
    private String triggerTypeDesc;

    /**
     * 触发报文类型：见字典TARGET_MESSAGE_TYPE
     */
    @Schema(description = "触发报文类型：见字典TARGET_MESSAGE_TYPE")
    private String messageType;

    /**
     * 触发报文类型描述
     */
    @Schema(description = "触发报文类型描述")
    private String messageTypeDesc;

    /**
     * 产品业务id
     */
    @Schema(description = "产品业务id")
    private String targetBizProdId;

    /**
     * 产品业务名
     */
    @Schema(description = "产品业务名")
    private String targetBizProdName;

    /**
     * 触发设备，多个以逗号分隔
     */
    @Schema(description = "触发设备，多个以逗号分隔")
    private String targetBizDeviceIds;

    /**
     * 触发设备名称，多个以逗号分隔，与id一一对应
     */
    @Schema(description = "触发设备名称，多个以逗号分隔，与id一一对应")
    private String targetBizDeviceNames;

    /**
     * 规则的业务编码
     */
    @Schema(description = "规则的业务编码")
    private String bizRuleId;

    /**
     * 重复时间，逗号分隔
     */
    @Schema(description = "重复时间，逗号分隔")
    private String repeatTime;

    /**
     * 重复类型
     */
    @Schema(description = "重复类型")
    private String repeatType;

    /**
     * 触发时间
     */
    @Schema(description = "触发时间")
    private String targetTime;

    @Schema(description = "给前端冗余的产品id")
    private Long targetProdId;
}