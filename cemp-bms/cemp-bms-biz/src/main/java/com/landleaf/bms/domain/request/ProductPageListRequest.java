package com.landleaf.bms.domain.request;

import com.landleaf.comm.base.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 产品列表分页查询请求参数
 *
 * @author 张力方
 * @since 2023/7/3
 **/
@Data
@Schema(name = "产品列表分页查询请求参数", description = "产品列表分页查询请求参数")
public class ProductPageListRequest extends PageParam {
    /**
     * 品类id
     */
    @NotBlank(message = "品类id不能为空")
    @Schema(description = "品类id", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private String categoryId;

    /**
     * 是否是目录
     */
    @NotNull(message = "是否是目录不能为空")
    @Schema(description = "是否是目录", requiredMode = Schema.RequiredMode.REQUIRED, example = "true")
    private Boolean isCatalogue;

    /**
     * 产品名称
     */
    @Schema(description = "产品名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "XXX")
    private String name;

    /**
     * 产品状态 - 数据字典 PRODUCT_STATUS
     */
    @Schema(description = "产品状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "XXX")
    private Integer status;

}
