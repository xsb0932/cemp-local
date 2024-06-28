package com.landleaf.job.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author Yang
 */
@Data
@Schema(description = "手动执行定时任务入参")
public class ScheduleManualRunRequest {
    @Schema(description = "定时任务id")
    @NotNull(message = "定时任务id不能为空")
    private Long jobId;

    @Schema(description = "租户id")
    @NotNull(message = "租户id不能为空")
    private Long tenantId;

    @Schema(description = "项目bizId")
    private String bizProjectId;

    /**
     * 时：yyyy-MM-dd HH:mm:ss
     * 日：yyyy-MM-dd
     * 月：yyyy-MM
     * 年：yyyy
     */
    @Schema(description = "开始时间")
    private String startTime;

    @Schema(description = "结束时间")
    private String endTime;
}
