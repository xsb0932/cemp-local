package com.landleaf.job.domain.request;

import com.landleaf.comm.base.pojo.PageParam;
import lombok.Data;

@Data
public class ScheduleJobLogPageRequest extends PageParam {
    private Long jobId;
}
