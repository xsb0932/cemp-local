package com.landleaf.bms.domain.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * GatewayRunJsResponse
 *
 * @author 张力方
 * @since 2023/8/21
 **/
@Data
public class GatewayRunJsResponse {
    /**
     * 响应参数对象
     */
    @Schema(description = "响应参数对象")
    private String responseObj;
    /**
     * 提示
     */
    @Schema(description = "提示")
    private String tip;

}
