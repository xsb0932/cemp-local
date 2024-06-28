package com.landleaf.data.api.device.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "RPC 服务 - 设备监测列表 同一品类查询设备当前状态 Request DTO")
@Data
public class DeviceListCurrentReqDTO {
    @Schema(description = "设备id")
    private List<String> deviceIds;
    @Schema(description = "属性code")
    private List<String> attrCodes;
    //TODO 后期需要添加统计类型的数据查询
}
