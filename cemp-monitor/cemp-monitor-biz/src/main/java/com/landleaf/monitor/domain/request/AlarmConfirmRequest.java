package com.landleaf.monitor.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 告警确认请求
 *
 * @author 张力方
 * @since 2023/8/14
 **/
@Data
@Schema(name = "告警确认请求", description = "告警确认请求")
public class AlarmConfirmRequest {
    /**
     * 事件id
     */
    @NotNull(message = "事件id不允许为空")
    @Schema(description = "事件id")
    private String eventId;
    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remark;
}
