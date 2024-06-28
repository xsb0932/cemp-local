package com.landleaf.bms.domain.response;

import com.landleaf.bms.domain.dto.ManagementNodeTreeDTO;
import com.landleaf.bms.domain.entity.ProjectSpaceEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "物联平台-设备管理分组节点树")
public class DeviceManagerNodeTreeResponse {
    @Schema(description = "节点id")
    private String nodeId;
    @Schema(description = "父节点id")
    private String parentNodeId;
    @Schema(description = "节点名称")
    private String name;
    @Schema(description = "节点类型: 0-管理节点 1-项目 2-项目空间")
    private Integer type;
    @Schema(description = "子节点")
    private List<DeviceManagerNodeTreeResponse> children;

    public static DeviceManagerNodeTreeResponse convertToNodeFromNode(ManagementNodeTreeDTO dto) {
        return new DeviceManagerNodeTreeResponse()
                .setNodeId(dto.getBizNodeId())
                .setParentNodeId(dto.getParentBizNodeId())
                .setName(dto.getName())
                .setType(0);
    }

    public static DeviceManagerNodeTreeResponse convertToProjectFromNode(ManagementNodeTreeDTO dto) {
        return new DeviceManagerNodeTreeResponse()
                .setNodeId(dto.getBizProjectId())
                .setParentNodeId(dto.getParentBizNodeId())
                .setName(dto.getName())
                .setType(1);
    }

    public static DeviceManagerNodeTreeResponse convertToProjectFromSpace(ProjectSpaceEntity dto, String parentNodeId) {
        return new DeviceManagerNodeTreeResponse()
                .setNodeId(dto.getId().toString())
                .setParentNodeId(parentNodeId)
                .setName(dto.getName())
                .setType(2);
    }
}
