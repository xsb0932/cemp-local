package com.landleaf.bms.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 管理节点项目树结构
 *
 * @author 张力方
 * @since 2023/6/5
 **/
@Data
@Schema(name = "管理节点项目树结构返回参数", description = "管理节点项目树结构返回参数")
public class NodeProjectTreeDTO {
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
     */
    @Schema(description = "项目id", example = "1")
    private Long projectId;
    /**
     * 项目业务id
     */
    @Schema(description = "项目业务id", example = "1")
    private String projectBizId;
    /**
     * 子节点
     */
    @Schema(description = "子节点")
    private List<NodeProjectTreeDTO> children;
}