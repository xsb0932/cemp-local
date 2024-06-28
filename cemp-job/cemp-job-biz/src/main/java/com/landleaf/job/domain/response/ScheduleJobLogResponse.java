package com.landleaf.job.domain.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ScheduleJobLogResponse {
    /**
     * 日志id
     */
    @Schema(description = "日志id")
    private Long id;
    /**
     * 任务id
     */
    @Schema(description = "任务id")
    private Long jobId;

    /**
     * 接口地址
     */
    @Schema(description = "接口地址")
    private String apiUrl;

    /**
     * 任务状态（0成功 1失败）
     */
    @Schema(description = "任务状态（0成功 1失败）")
    private Integer status;

    /**
     * 日志信息
     */
    @Schema(description = "日志信息")
    private String logInfo;

    /**
     * 失败信息
     */
    @Schema(description = "失败信息")
    private String error;

    /**
     * 耗时(单位：毫秒)
     */
    @Schema(description = "耗时(单位：毫秒)")
    private Integer times;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
