package com.landleaf.bms.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * UserManageNodeResponse
 *
 * @author 张力方
 * @since 2023/6/12
 **/
@Data
@Schema(description = "项目权限")
public class UserManageNodeResponse {
    /**
     * 节点id
     */
    @Schema(description = "节点id")
    private Long nodeId;
    /**
     * 业务节点id
     */
    @Schema(description = "业务节点id")
    private String bizNodeId;
    /**
     * 节点名称
     */
    @Schema(description = "节点名称")
    private String nodeName;
    /**
     * 父节点业务id
     */
    @Schema(description = "父节点业务id")
    private String parentBizNodeId;
    /**
     * 节点类型
     */
    @Schema(description = "节点类型")
    private Short nodeType;

    /**
     * 子集
     */
    @Schema(description = "子集")
    private List<UserManageNodeResponse> children;
}
