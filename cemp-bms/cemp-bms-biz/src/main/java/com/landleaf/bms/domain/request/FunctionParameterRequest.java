package com.landleaf.bms.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "服务参数")
public class FunctionParameterRequest {
    @Schema(description = "参数标识符")
    private String identifier;
    @Schema(description = "参数值")
    private Object value;
}