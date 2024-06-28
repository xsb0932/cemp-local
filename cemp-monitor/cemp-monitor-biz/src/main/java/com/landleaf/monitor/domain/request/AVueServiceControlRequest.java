package com.landleaf.monitor.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "AVue视图服务控制参数")
public class AVueServiceControlRequest {
    @Schema(description = "用户密码")
    @NotBlank(message = "密码不能为空")
    private String password;

    @Schema(description = "设备业务id")
    @NotBlank(message = "设备业务id不能为空")
    private String bizDeviceId;

    @Schema(description = "功能标识符")
    @NotBlank(message = "功能标识符不能为空")
    private String identifier;

    @Schema(description = "服务参数<参数identifier,参数值value>")
    private List<AVueFunctionParameterRequest> functionParameters;

}
