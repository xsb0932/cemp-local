package com.landleaf.job.domain.request;

import com.landleaf.comm.base.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * @author Yang
 */
@Data
@Schema(description = "定时任务日志分页查询参数")
public class ScheduleLoggerPageRequest extends PageParam {
    @Schema(description = "租户id")
    @NotNull(message = "租户id不能为空")
    private Long tenantId;

    @Schema(description = "任务id")
    @NotNull(message = "任务id不能为空")
    private Long jobId;

    @Schema(description = "项目名称")
    private String projectName;

    @Schema(description = "执行状态（0成功 1失败)")
    private Integer status;

    @Schema(description = "开始时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
}
