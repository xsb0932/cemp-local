package com.landleaf.bms.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 品类-设备参数-表格头
 *
 * @author yue lin
 * @since 2023/7/20 10:19
 */
@Data
@Schema(description = "品类-设备参数-表格头")
public class CategoryDeviceParameterResponse {

    /**
     * 标识符
     */
    @Schema(description = "标识符")
    private String identifier;
    /**
     * 功能名称
     */
    @Schema(description = "功能名称")
    private String functionName;

}
