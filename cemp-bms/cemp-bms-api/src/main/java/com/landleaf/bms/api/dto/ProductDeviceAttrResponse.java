package com.landleaf.bms.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * ProductDeviceAttr
 *
 * @author 张力方
 * @since 2023/7/26
 **/
@Data
public class ProductDeviceAttrResponse {
    @Schema(name = "属性code")
    private String attrCode;

    @Schema(name = "属性名")
    private String attrName;

    @Schema(name = "单位")
    private String unit;

}
