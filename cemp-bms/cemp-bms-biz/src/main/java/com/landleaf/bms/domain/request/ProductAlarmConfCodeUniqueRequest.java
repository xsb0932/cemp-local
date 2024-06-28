package com.landleaf.bms.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * ProductAlarmConfCodeUniqueRequest
 *
 * @author 张力方
 * @since 2023/8/11
 **/
@Data
public class ProductAlarmConfCodeUniqueRequest {
    /**
     * 告警配置id
     * <p>
     * 编辑时不能为空
     */
    @Schema(name = "告警配置id", description = "编辑时不能为空", example = "01")
    private Long id;

    /**
     * 告警码（校验唯一）
     */
    @Schema(description = "告警码（校验唯一）", requiredMode = Schema.RequiredMode.REQUIRED, example = "01")
    @NotBlank(message = "告警码不能为空")
    private String code;

    /**
     * 产品id
     */
    @Schema(description = "产品id", example = "1")
    @NotNull(message = "产品id不能为空")
    private Long productId;

}
