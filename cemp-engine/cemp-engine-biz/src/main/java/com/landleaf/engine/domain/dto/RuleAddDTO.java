package com.landleaf.engine.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.sql.Timestamp;

import java.util.Date;

/**
 * RuleEntity对象的新增时的参数封装
 *
 * @author hebin
 * @since 2024-04-23
 */
@Data
@Schema(name = "RuleAddDTO", description = "RuleEntity对象的新增时的参数封装")
public class RuleAddDTO {

    /**
     * 规则编号
     */
    @Schema(description = "规则编号")
    @NotNull(groups = {UpdateGroup.class}, message = "规则编号不能为空")
    private Long id;

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
     * 项目id（全局唯一id）
     */
    @Schema(description = "项目id（全局唯一id）")
    private String bizProjectId;

    public interface AddGroup {
    }

    public interface UpdateGroup {
    }
}