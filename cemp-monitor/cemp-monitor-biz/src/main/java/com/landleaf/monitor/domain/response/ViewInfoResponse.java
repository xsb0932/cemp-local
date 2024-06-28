package com.landleaf.monitor.domain.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(name = "ViewInfoResponse", description = "视图管理 - 视图详情 VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class ViewInfoResponse extends ViewBaseResponse {
    @Schema(description = "视图id")
    private Long id;

    @Schema(description = "项目名称）")
    private String projectName;

    @Schema(description = "视图类型名称（字典值）")
    private String typeName;
}
