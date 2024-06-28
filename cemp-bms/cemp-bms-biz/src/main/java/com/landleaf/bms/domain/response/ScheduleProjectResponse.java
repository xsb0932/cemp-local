package com.landleaf.bms.domain.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author Yang
 */
@Data
@Schema(description = "定时任务项目对象")
public class ScheduleProjectResponse {
    @Schema(description = "项目id")
    private String bizProjectId;

    @Schema(description = "项目名称")
    private String name;
}
