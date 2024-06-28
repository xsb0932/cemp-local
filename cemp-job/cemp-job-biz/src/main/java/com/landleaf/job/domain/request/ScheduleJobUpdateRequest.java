package com.landleaf.job.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "Job - 任务更新 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class ScheduleJobUpdateRequest extends ScheduleJobBaseRequest {
    /**
     * 任务id
     */
    private Long id;
}
