package com.landleaf.bms.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 项目-名称唯一性校验
 *
 * @author 张力方
 * @since 2023/6/25
 **/
@Data
@Schema(name = "项目-名称唯一性校验", description = "项目-名称唯一性校验")
public class ProjectNameUniqueRequest {

    /**
     * 项目id
     * <p>
     * 编辑时不能为空
     */
    @Schema(name = "项目id", description = "编辑时不能为空", example = "1")
    private Long id;

    /**
     * 项目名称
     * 租户内唯一
     */
    @NotBlank(message = "项目名称不能为空")
    @Schema(description = "项目名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "XXX")
    private String name;

}
