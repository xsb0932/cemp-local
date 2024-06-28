package com.landleaf.engine.domain.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

import java.util.Date;

/**
 * RuleConditionEntity对象的展示信息封装
 *
 * @author hebin
 * @since 2024-04-23
 */
@Data
@Schema(name = "RuleConditionVO", description = "RuleConditionEntity对象的展示信息封装")
public class RuleConditionVO {

    /**
     * 规则编号
     */
    @Schema(description = "规则编号")
    private Long id;

    /**
     * 条件类型：见字典RULE_CONDITION_TYPE
     */
    @Schema(description = "条件类型：见字典RULE_CONDITION_TYPE")
    private String conditionType;

    /**
     * 条件类型描述
     */
    @Schema(description = "条件类型描述")
    private String conditionTypeDesc;

    /**
     * 关联产品业务id
     */
    @Schema(description = "关联产品业务id")
    private String corBizProdId;

    /**
     * 关联产品名
     */
    @Schema(description = "关联产品名")
    private String corBizProdName;

    /**
     * 关联设备
     */
    @Schema(description = "关联设备")
    private String corBizDeviceId;

    /**
     * 关联设备名
     */
    @Schema(description = "关联设备名")
    private String corBizDeviceName;

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
     * 重复类型描述
     */
    @Schema(description = "重复类型描述")
    private String repeatTypeDesc;

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
     * 比较符号，见字典COMPARATOR
     */
    @Schema(description = "比较符号，见字典COMPARATOR")
    private String comparatorDesc;

    /**
     * 属性code,同identifier
     */
    @Schema(description = "属性code,同identifier")
    private String attrCode;

    /**
     * 属性code
     */
    @Schema(description = "属性codeDesc")
    private String attrCodeDesc;

    /**
     * 触发时间结束
     */
    @Schema(description = "触发时间结束")
    private String judgeTimeEnd;

    @Schema(description = "给前端冗余的产品id")
    private Long corProdId;
}