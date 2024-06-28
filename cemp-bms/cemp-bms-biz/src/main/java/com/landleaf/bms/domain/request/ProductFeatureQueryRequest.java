package com.landleaf.bms.domain.request;

import com.landleaf.comm.base.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 产品-功能列表查询请求参数
 *
 * @author 张力方
 * @since 2023/6/25
 **/
@Data
@Schema(name = "产品-功能列表查询请求参数", description = "产品-功能列表查询请求参数")
public class ProductFeatureQueryRequest extends PageParam {
    /**
     * 产品id
     */
    @NotNull(message = "产品id不能为空")
    @Schema(description = "产品id", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long productId;

    /**
     * 功能标识符
     */
    @Schema(description = "功能标识符", example = "wxxxx001")
    private String identifier;

    /**
     * 功能名称
     */
    @Schema(description = "功能名称", example = "xxx")
    private String functionName;

}
