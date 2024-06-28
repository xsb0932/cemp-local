package com.landleaf.monitor.domain.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Schema(name = "ViewPageResponse", description = "视图管理 - 视图分页查询列表 VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class ViewPageResponse extends ViewBaseResponse {
    @Schema(description = "视图id")
    private Long id;

    @Schema(description = "项目名称）")
    private String projectName;

    @Schema(description = "视图类型名称（字典值）")
    private String typeName;

    @Schema(description = "创建者名称")
    private String creatorName;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
