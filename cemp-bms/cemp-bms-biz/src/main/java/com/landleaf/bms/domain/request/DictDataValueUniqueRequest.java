package com.landleaf.bms.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 数据字典数据-码值唯一性校验
 *
 * @author 张力方
 * @since 2023/6/25
 **/
@Data
@Schema(name = "数据字典数据-码值唯一性校验", description = "数据字典数据-码值唯一性校验")
public class DictDataValueUniqueRequest {

    /**
     * 数据字典id
     */
    @Schema(name = "数据字典id", example = "1")
    @NotNull(message = "数据字典id")
    private Long dictId;

    /**
     * 数据字典码值id
     * <p>
     * 编辑时不能为空
     */
    @Schema(name = "数据字典码值id", description = "编辑时不能为空", example = "1")
    private Long dictDataId;

    /**
     * 数据字典码值
     */
    @NotBlank(message = "数据字典码值不能为空")
    @Schema(description = "数据字典码值", requiredMode = Schema.RequiredMode.REQUIRED, example = "XXX")
    private String value;
}
