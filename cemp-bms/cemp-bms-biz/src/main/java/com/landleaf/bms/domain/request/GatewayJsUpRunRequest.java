package com.landleaf.bms.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * GatewayJsRunRequest
 *
 * @author 张力方
 * @since 2023/8/21
 **/
@Data
public class GatewayJsUpRunRequest {
    /**
     * 网关业务id
     */
    @NotNull(message = "网关业务id不能为空")
    @Schema(description = "网关业务id", requiredMode = Schema.RequiredMode.REQUIRED, example = "GW123")
    private String gateWayBizId;
    /**
     * topic
     */
    @Schema(description = "topic", example = "xxxx")
    private String topic;
    /**
     * payload
     */
    @Schema(description = "payload", example = "xxxx")
    private String payload;

}
