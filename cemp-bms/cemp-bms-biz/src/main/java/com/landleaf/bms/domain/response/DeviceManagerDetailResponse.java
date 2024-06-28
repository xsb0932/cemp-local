package com.landleaf.bms.domain.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "设备详情页参数返回对象")
public class DeviceManagerDetailResponse {
    @Schema(description = "设备id")
    private String id;
    @Schema(description = "设备名称")
    private String name;
    @Schema(description = "设备业务id")
    private String bizDeviceId;
    @Schema(description = "品类图标")
    private String image;
    @Schema(description = "设备描述")
    private String deviceDesc;
    @Schema(description = "通讯状态：0-离线 1-在线")
    private Integer cst;

    @Schema(description = "品类名称")
    private String categoryName;
    @Schema(description = "产品名称")
    private String productName;
    @Schema(description = "产品型号")
    private String model;
    @Schema(description = "外部设备id")
    private String sourceDeviceId;
    @Schema(description = "设备编码（校验唯一）")
    private String code;

    @Schema(description = "项目名称")
    private String projectName;
    @Schema(description = "设备位置")
    private String locationDesc;

}
