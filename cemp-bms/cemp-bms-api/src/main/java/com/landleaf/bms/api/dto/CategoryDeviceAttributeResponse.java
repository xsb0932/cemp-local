package com.landleaf.bms.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 品类-设备参数-表格头
 *
 * @author xushibai
 * @since 2023/8/16 10:19
 */
@Data
@Schema(description = "品类-设备参数-表格头")
public class CategoryDeviceAttributeResponse {

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
