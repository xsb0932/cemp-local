package com.landleaf.bms.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * GatewayJsDownRunRequest
 *
 * @author 张力方
 * @since 2023/8/21
 **/
@Data
public class GatewayJsDownRunRequest {
    /**
     * 网关业务id
     */
    @NotNull(message = "网关业务id不能为空")
    @Schema(description = "网关业务id", requiredMode = Schema.RequiredMode.REQUIRED, example = "GW123")
    private String gateWayBizId;
    /**
     * cmd
     */
    @Schema(description = "cmd", example = "xxxx")
    private String cmd;

}
