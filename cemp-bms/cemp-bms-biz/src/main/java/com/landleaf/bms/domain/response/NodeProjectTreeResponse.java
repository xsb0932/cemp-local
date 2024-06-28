package com.landleaf.bms.domain.response;

import com.landleaf.bms.domain.entity.ManagementNodeEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 管理节点项目树结构
 *
 * @author 张力方
 * @since 2023/6/5
 **/
@Data
@Schema(name = "管理节点项目树结构返回参数", description = "管理节点项目树结构返回参数")
public class NodeProjectTreeResponse {
    /**
     * 节点id
     */
    @Schema(description = "节点id", example = "1")
    private Long nodeId;
    /**
     * 节点业务id
     */
    @Schema(description = "节点业务id", example = "1")
    private String bizNodeId;
    /**
     * 父节点业务id
     */
    @Schema(description = "父节点业务id", example = "1")
    private String parentBizNodeId;
    /**
     * 节点名称
     */
    @Schema(description = "节点名称", example = "酒店")
    private String name;
    /**
     * 节点类型
     */
    @Schema(description = "节点类型", example = "1")
    private String type;
    /**
     * 项目id
     * <p>
     * 只有 {@link com.landleaf.bms.domain.enums.ManagementNodeTypeEnum#PROJECT} 才有值
     */
    @Schema(description = "项目id", example = "1")
    private Long projectId;
    /**
     * 项目业务id
     * <p>
     * 只有 {@link com.landleaf.bms.domain.enums.ManagementNodeTypeEnum#PROJECT} 才有值
     */
    @Schema(description = "项目业务id", example = "1")
    private String projectBizId;

    @Schema(description = "前端可勾选判断", example = "true")
    private Boolean selectable;

    @Schema(description = "前端节点唯一标识", example = "N001")
    private String selectId;
    /**
     * 子节点
     */
    @Schema(description = "子节点")
    private List<NodeProjectTreeResponse> children;

    public static NodeProjectTreeResponse convertFrom(ManagementNodeEntity managementNodeEntity) {
        NodeProjectTreeResponse nodeProjectTreeResponse = new NodeProjectTreeResponse();
        nodeProjectTreeResponse.setBizNodeId(managementNodeEntity.getBizNodeId());
        nodeProjectTreeResponse.setParentBizNodeId(managementNodeEntity.getParentBizNodeId());
        nodeProjectTreeResponse.setName(managementNodeEntity.getName());
        nodeProjectTreeResponse.setType(managementNodeEntity.getType());
        return nodeProjectTreeResponse;
    }

    public static List<NodeProjectTreeResponse> convertFrom(List<ManagementNodeEntity> managementNodeEntities) {
        List<NodeProjectTreeResponse> nodeProjectTreeResponses = new ArrayList<>();
        for (ManagementNodeEntity managementNodeEntity : managementNodeEntities) {
            NodeProjectTreeResponse nodeProjectTreeResponse = convertFrom(managementNodeEntity);
            nodeProjectTreeResponses.add(nodeProjectTreeResponse);
        }
        return nodeProjectTreeResponses;
    }
}
