package com.landleaf.bms.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 管理节点下项目列表请求参数
 *
 * @author 张力方
 * @since 2023/6/6
 **/
@Data
@Schema(name = "管理节点下项目列表请求参数", description = "管理节点下项目列表请求参数")
public class ProjectTreeListApiRequest {
    /**
     * 管理节点业务id
     */
    @NotBlank(message = "管理节点不能为空")
    @Schema(description = "管理节点业务id", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private String bizNodeId;
}
