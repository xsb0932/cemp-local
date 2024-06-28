package com.landleaf.engine.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Value;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.landleaf.pgsql.base.BaseEntity;

import java.math.BigDecimal;

import java.util.Date;
import java.sql.Timestamp;

import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;


/**
 * 实体类
 *
 * @author hebin
 * @since 2024-04-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(name = "RuleTriggerEntity", description = "RuleTriggerEntity对象")
@TableName("tb_rule_trigger")
public class RuleTriggerEntity extends BaseEntity {

    /**
     * 规则编号
     */
    @Schema(description = "规则编号")
    @TableId(type = IdType.AUTO)
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

    @Schema(description = "给前端冗余的产品id")
    private Long targetProdId;
}