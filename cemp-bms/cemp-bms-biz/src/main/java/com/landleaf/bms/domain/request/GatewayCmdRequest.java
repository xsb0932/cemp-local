package com.landleaf.bms.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * GatewayCmdRequest
 *
 * @author xushibai
 * @since 2023/8/29
 **/
@Data
public class GatewayCmdRequest {

    @Schema(description = "网关名称")
    private String name;

    @Schema(description = "shell脚本")
    private String cmd;


}
