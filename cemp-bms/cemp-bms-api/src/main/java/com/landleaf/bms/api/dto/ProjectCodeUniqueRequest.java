package com.landleaf.bms.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 项目-编码唯一性校验
 *
 * @author 张力方
 * @since 2023/6/25
 **/
@Data
@Schema(name = "项目-编码唯一性校验", description = "项目-编码唯一性校验")
public class ProjectCodeUniqueRequest {

    /**
     * 项目id
     * <p>
     * 编辑时不能为空
     */
    @Schema(name = "项目id", description = "编辑时不能为空", example = "1")
    private Long id;

    /**
     * 项目编码
     * 租户内唯一
     */
    @NotBlank(message = "项目编码不能为空")
    @Size(min = 1, max = 50, message = "项目编码长度区间{min}-{max}")
    @Schema(description = "项目编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "XXX")
    private String code;
}
