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
@Schema(name = "RuleEntity", description = "RuleEntity对象")
@TableName("tb_rule")
public class RuleEntity extends BaseEntity{

/**
 * 规则编号
 */
        @Schema(description = "规则编号")
            @TableId(type = IdType.AUTO)
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
}