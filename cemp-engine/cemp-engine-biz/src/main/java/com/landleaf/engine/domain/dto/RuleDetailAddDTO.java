package com.landleaf.engine.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 新增详情
 *
 * @author hebin
 * @since 2024-04-23
 */
@Data
@Schema(name = "RuleDetailAddDTO", description = "新增详情")
public class RuleDetailAddDTO {

    /**
     * 规则编号
     */
    @Schema(description = "规则编号")
    private Long id;

    /**
     * 执行节点
     */
    @Schema(description = "执行节点")
    private RuleActionAddDTO actionAddDTO;

    /**
     * 触发节点
     */
    @Schema(description = "触发节点")
    private RuleTriggerAddDTO triggerAddDTO;

    /**
     * 条件节点
     */
    @Schema(description = "条件节点")
    private List<RuleConditionAddDTO> conditionAddDTOList;

    public interface AddGroup {
    }

    public interface UpdateGroup {
    }
}