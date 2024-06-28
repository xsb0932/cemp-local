package com.landleaf.engine.domain.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

import java.util.Date;

/**
 * RuleEntity对象的展示信息封装
 *
 * @author hebin
 * @since 2024-04-23
 */
@Data
@Schema(name = "RuleVO", description = "RuleEntity对象的展示信息封装")
public class RuleVO {

    /**
     * 规则编号
     */
    @Schema(description = "规则编号")
    private Long id;

    /**
     * 规则的业务编码
     */
    @Schema(description = "规则的业务编码")
    private String bizRuleId;

    /**
     * 规则名称
     */
    @Schema(description = "规则名称")
    private String name;

    /**
     * 规则描述
     */
    @Schema(description = "规则描述")
    private String ruleDesc;

    /**
     * 规则类型，见字典RULE_TYPE
     */
    @Schema(description = "规则类型，见字典RULE_TYPE")
    private String ruleType;

    @Schema(description = "规则类型描述")
    private String ruleTypeDesc;

    /**
     * 规则状态，见字典RULE_STATUS
     */
    @Schema(description = "规则状态，见字典RULE_STATUS")
    private String ruleStatus;

    @Schema(description = "规则状态描述")
    private String ruleStatusDesc;
    /**
     * 项目id（全局唯一id）
     */
    @Schema(description = "项目id（全局唯一id）")
    private String bizProjectId;

    /**
     * 项目名
     */
    @Schema(description = "项目名")
    private String projectName;
}