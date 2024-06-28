package com.landleaf.monitor.domain.response;

import com.landleaf.bms.api.dto.NodeProjectTreeDTO;
import com.landleaf.monitor.domain.vo.DeviceTreeDisplayVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 设备节点树结构
 *
 * @author 张力方
 * @since 2023/6/5
 **/
@Data
@Schema(name="设备节点树结构")
public class NodeProjectDeviceTreeResponse {
    /**
     * 应前端要求，给个id字段，方便其控件展示
     */
    @Schema(description = "应前端要求，给个id字段，方便其控件展示", example = "1")
    private String id;

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
    private List<NodeProjectDeviceTreeResponse> children;

    /**
     * 是否可点击，非设备，均不可选
     */
    @Schema(name = "是否可点击，非设备，均不可选")
    private Boolean checkable = false;

    public String getId() {
        return bizNodeId;
    }
}
