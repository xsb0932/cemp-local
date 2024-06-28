package com.landleaf.bms.domain.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "编辑设备时设备详情参数对象")
public class DeviceIotInfoResponse {
    @Schema(description = "设备id")
    private Long id;

    @Schema(description = "项目id")
    private Long projectId;

    @Schema(description = "项目id（全局唯一id）")
    private String bizProjectId;

    @Schema(description = "项目名称")
    private String projectName;

    @Schema(description = "分区id（全局唯一id）")
    private String bizAreaId;

    @Schema(description = "设备id（全局唯一id）")
    private String bizDeviceId;

    @Schema(description = "设备名称")
    private String name;

    @Schema(description = "产品id")
    private Long productId;

    @Schema(description = "产品id（全局唯一id）")
    private String bizProductId;

    @Schema(description = "品类id（全局唯一id）")
    private String bizCategoryId;

    @Schema(description = "设备编码（校验唯一）")
    private String code;

    @Schema(description = "设备位置")
    private String locationDesc;

    @Schema(description = "设备描述")
    private String deviceDesc;

    @Schema(description = "外部设备id")
    private String sourceDeviceId;

    @Schema(description = "设备参数")
    private List<DeviceParameterDetailResponse> deviceParameters;

}
