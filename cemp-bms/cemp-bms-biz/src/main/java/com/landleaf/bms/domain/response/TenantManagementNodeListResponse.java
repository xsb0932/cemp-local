package com.landleaf.bms.domain.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * ManagementNodeListResponse
 *
 * @author 张力方
 * @since 2023/6/9
 **/
@Data
public class TenantManagementNodeListResponse {
    /**
     * 节点id
     */
    @Schema(description = "节点id")
    private Long id;
    /**
     * 节点业务id
     */
    @Schema(description = "节点业务id")
    private String bizNodeId;
    /**
     * 节点名称
     */
    @Schema(description = "节点名称")
    private String name;

    /**
     * 节点code（校验唯一）
     */
    @Schema(description = "节点code（校验唯一）")
    private String code;

    /**
     * 父节点业务id
     */
    @Schema(description = "父节点业务id")
    private String parentBizNodeId;

    /**
     * 显示顺序
     */
    @Schema(description = "显示顺序")
    private Integer sort;

    /**
     * 节点类型（字典类型-管理节点）
     * <p>
     * 企业 - 01
     * 项目 - 00
     * 城市 - 02
     */
    @Schema(description = "节点类型（字典类型-管理节点）")
    private String type;

    @Schema(description = "子节点")
    private List<TenantManagementNodeListResponse> children;

}
