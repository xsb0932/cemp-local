package com.landleaf.bms.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 功能标识符唯一性校验
 *
 * @author 张力方
 * @since 2023/6/25
 **/
@Data
@Schema(name = "功能标识符唯一性校验", description = "功能标识符唯一性校验")
public class FunctionIdentifierUniqueRequest {

    /**
     * 功能参数id
     * <p>
     * 编辑时不能为空
     */
    @Schema(name = "功能参数id", description = "编辑时不能为空", example = "1")
    private Long id;

    /**
     * 功能标识符
     * <p>
     * 1、功能管理中唯一
     * 2、英文开头，可包含英文何数字的字符串
     */
    @NotBlank(message = "功能标识符不能为空")
    @Schema(description = "功能标识符，英文开头，可包含英文何数字的字符串", requiredMode = Schema.RequiredMode.REQUIRED, example = "wxxxx001")
    private String identifier;

}
