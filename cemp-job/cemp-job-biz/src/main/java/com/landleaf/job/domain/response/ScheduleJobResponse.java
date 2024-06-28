package com.landleaf.job.domain.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "定时任务分页对象")
public class ScheduleJobResponse {
    /**
     * 任务id
     */
    @Schema(description = "任务id")
    private Long id;

    @Schema(description = "租户id")
    private Long tenantId;

    @Schema(description = "租户名称")
    private String tenantName;

    @Schema(description = "任务名称")
    private String jobName;

    @Schema(description = "统计类型（0时 1日 2月 3年）")
    private Integer statisticType;

    @Schema(description = "统计类型名称")
    private String statisticTypeName;

    @Schema(description = "上次运行状态（0成功 1失败)")
    private Integer lastStatus;

    @Schema(description = "上次运行状态名称")
    private String lastStatusName;

    @Schema(description = "上次运行时间")
    private LocalDateTime lastTime;
}
