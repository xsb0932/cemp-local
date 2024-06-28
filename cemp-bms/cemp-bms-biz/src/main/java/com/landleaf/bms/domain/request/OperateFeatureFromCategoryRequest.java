package com.landleaf.bms.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 品类操作功能管理
 *
 * @author yue lin
 * @since 2023/7/6 15:49
 */
@Data
public class OperateFeatureFromCategoryRequest {

    /**
     * 目标品类业务ID
     */
    @NotNull(message = "品类业务ID不能为空")
    @Schema(description = "目标品类业务ID")
    private String categoryBizId;

    /**
     * 功能ID
     */
    @NotEmpty(message = "功能ID不能为空")
    @Schema(description = "功能ID")
    private List<Long> featureIds;

    /**
     * 功能类别
     * @see 01, 02, 03, 04, 05
     */
    @NotBlank(message = "功能类别不能为空")
    @Schema(description = "功能类别", allowableValues = {"01", "02", "03", "04", "05"})
    private String functionCategory;

}
