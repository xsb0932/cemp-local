package com.landleaf.bms.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * GatewayJsSaveRequest
 *
 * @author 张力方
 * @since 2023/8/17
 **/
@Data
public class GatewayJsSaveRequest {
    /**
     * 网关业务id
     */
    @NotNull(message = "网关业务id不能为空")
    @Schema(description = "网关业务id", requiredMode = Schema.RequiredMode.REQUIRED, example = "GW123")
    private String gateWayBizId;
    /**
     * 上行js脚本
     */
    @Schema(description = "上行js脚本", example = "xxxx")
    private String uploadJs;
    /**
     * 下行js脚本
     */
    @Schema(description = "下行js脚本", example = "xxxx")
    private String downloadJs;

}
