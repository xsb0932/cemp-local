package com.landleaf.bms.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 编辑产品状态请求参数
 *
 * @author 张力方
 * @since 2023/7/3
 **/
@Data
@Schema(name = "编辑产品状态请求参数", description = "编辑产品状态请求参数")
public class ProductChangeStatusRequest {

    /**
     * 产品id
     */
    @NotNull(message = "产品id不能为空")
    @Schema(description = "产品id", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    /**
     * 产品状态 - 数据字典 PRODUCT_STATUS
     */
    @NotNull(message = "产品状态不能为空")
    @Schema(description = "产品状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "XXX")
    private Integer status;
}
