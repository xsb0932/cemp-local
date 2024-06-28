package com.landleaf.bms.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 管理节点排序请求
 *
 * @author 张力方
 * @since 2023/6/5
 **/
@Data
@Schema(name = "管理节点排序请求参数", description = "管理节点排序请求参数")
public class ManagementNodeSortRequest {
    /**
     * 当前拖动管理节点
     */
    @NotNull(message = "当前拖动管理节点不能为空")
    @Schema(description = "当前拖动管理节点", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long nodeId;
    /**
     * 当前管理节点下一个管理节点
     * <p>
     * 为空则表示拖到了最后
     */
    @Schema(description = "当前管理节点下一个管理节点", example = "1")
    private Long nextNodeId;
    /**
     * 租户id
     */
    @Schema(description = "租户id", example = "1")
    private Long tenantId;
}
