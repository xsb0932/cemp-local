package com.landleaf.monitor.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Schema(name = "设备属性信息封装")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceAttrDisplayVO {
    @Schema(name = "属性code")
    private String attrCode;

    @Schema(name = "属性名")
    private String attrName;

    @Schema(name = "单位")
    private String unit;
}
