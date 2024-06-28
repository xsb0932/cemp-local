package com.landleaf.bms.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 产品库-引用产品请求参数
 *
 * @author 张力方
 * @since 2023/7/3
 **/
@Data
@Schema(name = "产品库-引用产品请求参数", description = "产品库-引用产品请求参数")
public class RepoProductRefRequest {

    /**
     * 产品id
     */
    @NotNull(message = "产品id不能为空")
    @Schema(description = "产品id", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long productId;

}
