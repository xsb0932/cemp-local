package com.landleaf.bms.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 品类列表查询
 *
 * @author yue lin
 * @since 2023/7/7 15:16
 */
@Data
@Schema(description = "品类列表查询")
public class CategoryFeatureQueryRequest extends FeatureQueryRequest {

    /**
     * 功能类别(不需要传递)
     * @see  [01， 02， 03， 04, 05]
     */
    @Schema(description = "功能类别")
    private String functionCategory;

    /**
     * 业务ID
     */
    @NotNull(message = "id不能为空")
    @Schema(description = "ID")
    private String categoryBizId;

    /**
     * id（非前端查询条件）
     */
    @Schema(description = "ID")
    private Long categoryId;

}
