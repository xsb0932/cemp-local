package com.landleaf.monitor.domain.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author Yang
 */
@Data
@Schema(name = "ViewTabResponse", description = "视图TabVO")
public class ViewTabResponse {
    @Schema(description = "ID")
    private Long id;
    @Schema(description = "视图名称")
    private String name;
    @Schema(description = "展示url地址")
    private String viewUrl;
    @Schema(description = "视图类型")
    private Integer viewType;
}
