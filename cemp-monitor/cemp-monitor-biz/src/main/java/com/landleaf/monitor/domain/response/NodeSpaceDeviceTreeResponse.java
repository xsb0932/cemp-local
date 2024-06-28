package com.landleaf.monitor.domain.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Schema(name = "设备节点树结构")
public class NodeSpaceDeviceTreeResponse {
    @Schema(description = "应前端要求，给个id字段，方便其控件展示", example = "1")
    private String id;

    @Schema(description = "节点对象的业务id")
    private String bizId;

    @Schema(description = "名称")
    private String name;

    @Schema(name = "是否可点击，非设备，均不可选")
    private Boolean checkable = false;

    @Schema(description = "子节点")
    private List<NodeSpaceDeviceTreeResponse> children = new ArrayList<>();

}
