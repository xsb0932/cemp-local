package com.landleaf.engine.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 规则相请封装
 *
 * @author hebin
 * @since 2024-04-23
 */
@Data
@Schema(name = "RuleDetailVO", description = "规则相请封装")
public class RuleDetailVO {

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

    /**
     * 规则状态，见字典RULE_STATUS
     */
    @Schema(description = "规则状态，见字典RULE_STATUS")
    private String ruleStatus;

    /**
     * 项目id（全局唯一id）
     */
    @Schema(description = "项目id（全局唯一id）")
    private String bizProjectId;

    /**
     * 执行节点
     */
    @Schema(description = "执行节点")
    private RuleActionVO actionVO;

    /**
     * 触发节点
     */
    @Schema(description = "触发节点")
    private RuleTriggerVO triggerVO;

    /**
     * 条件节点
     */
    @Schema(description = "条件节点")
    private List<RuleConditionVO> conditionVOList;

    @Schema(description = "规则类型描述")
    private String ruleTypeDesc;


    @Schema(description = "规则状态描述")
    private String ruleStatusDesc;

    /**
     * 项目名
     */
    @Schema(description = "项目名")
    private String projectName;
}