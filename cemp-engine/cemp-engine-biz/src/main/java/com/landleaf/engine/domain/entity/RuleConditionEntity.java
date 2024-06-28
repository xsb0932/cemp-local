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
@Schema(name = "RuleConditionEntity", description = "RuleConditionEntity对象")
@TableName("tb_rule_condition")
public class RuleConditionEntity extends BaseEntity {

    /**
     * 规则编号
     */
    @Schema(description = "规则编号")
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 条件类型：见字典RULE_CONDITION_TYPE
     */
    @Schema(description = "条件类型：见字典RULE_CONDITION_TYPE")
    private String conditionType;

    /**
     * 关联产品业务id
     */
    @Schema(description = "关联产品业务id")
    private String corBizProdId;

    /**
     * 关联设备
     */
    @Schema(description = "关联设备")
    private String corBizDeviceId;

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
     * 触发时间开始
     */
    @Schema(description = "触发时间开始")
    private String judgeTimeStart;

    /**
     * 比较值
     */
    @Schema(description = "比较值")
    private String compareVal;

    /**
     * 比较符号，见字典COMPARATOR
     */
    @Schema(description = "比较符号，见字典COMPARATOR")
    private String comparator;

    /**
     * 属性code
     */
    @Schema(description = "属性code")
    private String attrCode;

    /**
     * 触发时间结束
     */
    @Schema(description = "触发时间结束")
    private String judgeTimeEnd;


    @Schema(description = "给前端冗余的产品id")
    private Long corProdId;
}