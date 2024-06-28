package com.landleaf.engine.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.landleaf.comm.base.pojo.PageParam;

import java.math.BigDecimal;
import java.sql.Timestamp;

import java.util.Date;

/**
 * RuleTriggerEntity对象的查询时的参数封装
 *
 * @author hebin
 * @since 2024-04-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "RuleTriggerQueryDTO", description = "RuleTriggerEntity对象的查询时的参数封装")
public class RuleTriggerQueryDTO extends PageParam{

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
 * 触发报文类型：见字典TARGET_MESSAGE_TYPE
 */
        @Schema(description = "触发报文类型：见字典TARGET_MESSAGE_TYPE")
    private String messageType;

/**
 * 产品业务id
 */
        @Schema(description = "产品业务id")
    private String targetBizProdId;

/**
 * 触发设备，多个以逗号分隔
 */
        @Schema(description = "触发设备，多个以逗号分隔")
    private String targetBizDeviceIds;

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

/**
 * 开始时间
 */
@Schema(name = "开始时间,格式为yyyy-MM-dd")
private String startTime;

/**
 * 结束时间
 */
@Schema(name = "结束时间,格式为yyyy-MM-dd")
private String endTime;
        }