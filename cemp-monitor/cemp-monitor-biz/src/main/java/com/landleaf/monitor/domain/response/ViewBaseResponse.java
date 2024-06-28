package com.landleaf.monitor.domain.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ViewBaseResponse {
    @Schema(description = "项目id（全局唯一id）")
    private String bizProjectId;

    @Schema(description = "视图名称")
    private String name;

    @Schema(description = "视图类型（字典配置）")
    private Integer viewType;

    @Schema(description = "发布状态（0未发布 1已发布）")
    private Integer status;

    @Schema(description = "avue项目编辑地址")
    private String url;

    @Schema(description = "项目展示地址")
    private String viewUrl;

    @Schema(description = "类型（0avue 1项目定制）")
    private Integer customType;

    @Schema(description = "视图排序序号")
    private Integer sort;
}
