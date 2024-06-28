package com.landleaf.bms.domain.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author Yang
 */
@Data
@Schema(description = "模拟运行js返回对象")
public class SimulateJsResponse {
    @Schema(description = "转换后json对象")
    private Object result;

    @Schema(description = "运行结果 01:成功 02:失败")
    private String status;
}
