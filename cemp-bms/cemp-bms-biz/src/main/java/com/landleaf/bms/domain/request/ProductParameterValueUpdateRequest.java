package com.landleaf.bms.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 产品参数值更新请求
 *
 * @author 张力方
 * @since 2023/7/12
 **/
@Data
@Schema(name = "产品-产品参数值更新请求", description = "产品-产品参数值更新请求")
public class ProductParameterValueUpdateRequest {
    /**
     * 产品参数id
     */
    @NotNull(message = "产品参数id不能为空")
    @Schema(description = "产品参数id不能为空", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    /**
     * 产品参数值
     */
    @NotNull(message = "产品参数值不能为空")
    @Schema(description = "产品参数值不能为空", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private String value;

}
