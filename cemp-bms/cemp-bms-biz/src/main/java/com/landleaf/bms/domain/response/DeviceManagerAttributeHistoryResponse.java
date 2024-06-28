package com.landleaf.bms.domain.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "设备管理-历史数据")
public class DeviceManagerAttributeHistoryResponse {
    @Schema(description = "名称")
    private String name;
    @Schema(description = "单位")
    private String unit;
    @Schema(description = "属性code")
    private String code;
    @Schema(description = "是否数值类型")
    private Boolean numberFlag;
    @Schema(description = "数据")
    private List<String> data;
}
