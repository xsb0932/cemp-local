package com.landleaf.job.domain.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.sql.Timestamp;

/**
 * @author Yang
 */
@Data
@Schema(description = "定时任务日志分页对象")
public class ScheduleLoggerPageResponse {
    @Schema(description = "日志id")
    private Long id;

    @Schema(description = "执行时间")
    private Timestamp execTime;

    @Schema(description = "任务名称")
    private String jobName;

    @Schema(description = "项目名称列表")
    private String projectNames;

    @Schema(description = "运行状态（0自动 1人工）")
    private Integer execType;

    @Schema(description = "运行状态名称")
    private String execTypeName;

    @Schema(description = "运行状态（0成功 1失败）")
    private Integer status;

    @Schema(description = "运行状态名称")
    private String statusName;
}
