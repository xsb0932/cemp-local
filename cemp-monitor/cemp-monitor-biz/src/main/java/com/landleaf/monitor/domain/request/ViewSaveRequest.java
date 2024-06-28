package com.landleaf.monitor.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author Yang
 */
@Data
@Schema(name = "ViewSaveRequest", description = "新增视图VO")
public class ViewSaveRequest {
    @Schema(description = "项目id（全局唯一id）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String bizProjectId;

    @Schema(description = "视图名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String name;

    @Schema(description = "视图类型（字典配置）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private Integer viewType;

    @Schema(description = "视图排序序号")
    private Integer sort;

}
