package com.landleaf.bms.domain.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "设备管理分页列表返回对象")
public class DeviceManagerPageResponse {
    @Schema(description = "id")
    private Long id;

    @Schema(description = "设备id（全局唯一id）")
    private String bizDeviceId;

    @Schema(description = "设备名称")
    private String name;

    @Schema(description = "项目名称")
    private String projectName;

    @Schema(description = "通讯状态：0-离线 1-在线")
    private Integer cst;

    @Schema(description = "品类名称")
    private String categoryName;

    @Schema(description = "产品名称")
    private String productName;

    @Schema(description = "设备位置")
    private String locationDesc;

}
