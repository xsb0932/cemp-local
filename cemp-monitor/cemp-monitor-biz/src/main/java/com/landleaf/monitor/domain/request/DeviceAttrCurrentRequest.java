package com.landleaf.monitor.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Schema(name = "DeviceAttrCurrentRequest", description = "设备-属性 当前状态查询VO")
public class DeviceAttrCurrentRequest {
    @Schema(description = "key-设备bizId value-属性code集合")
    Map<String, List<String>> deviceAttrs;
}
