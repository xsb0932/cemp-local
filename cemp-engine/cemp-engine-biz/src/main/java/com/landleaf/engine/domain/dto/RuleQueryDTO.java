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
 * RuleEntity对象的查询时的参数封装
 *
 * @author hebin
 * @since 2024-04-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "RuleQueryDTO", description = "RuleEntity对象的查询时的参数封装")
public class RuleQueryDTO extends PageParam {

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
     * 项目名称
     */
    @Schema(description = "项目名称")
    private String projectName;

}