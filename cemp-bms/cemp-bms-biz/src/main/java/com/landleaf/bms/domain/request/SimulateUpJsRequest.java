package com.landleaf.bms.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author Yang
 */
@Data
@Schema(description = "网关详情-模拟运行上行js请求参数")
public class SimulateUpJsRequest {
    @NotBlank(message = "topic不能为空")
    @Schema(description = "topic", example = "/test/001")
    private String topic;

    @NotBlank(message = "消息体不能为空")
    @Schema(description = "消息体", example = "{\"msg\":\"Hello World!\"}")
    private String payload;
}
