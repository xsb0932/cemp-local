package com.landleaf.bms.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author Yang
 */
@Data
@Schema(description = "网关详情-模拟运行下行js请求参数")
public class SimulateDownJsRequest {
    @NotBlank(message = "消息体不能为空")
    @Schema(description = "消息体")
    private String cmd;
}
