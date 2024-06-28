package com.landleaf.job.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "Job - 任务创建 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class ScheduleJobSaveRequest extends ScheduleJobBaseRequest {

}
