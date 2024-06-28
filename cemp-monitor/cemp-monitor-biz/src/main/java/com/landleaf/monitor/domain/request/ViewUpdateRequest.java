package com.landleaf.monitor.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author Yang
 */
@Data
@Schema(name = "ViewUpdateRequest", description = "修改视图VO")
public class ViewUpdateRequest {
    @Schema(description = "视图id", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private Long id;

    @Schema(description = "视图名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String name;

    @Schema(description = "视图排序序号")
    private Integer sort;

}
