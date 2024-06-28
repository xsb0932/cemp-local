package com.landleaf.job.api;

import cn.hutool.core.bean.BeanUtil;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.job.api.dto.JobLogSaveDTO;
import com.landleaf.job.domain.entity.ScheduleJobLoggerEntity;
import com.landleaf.job.service.ScheduleJobLoggerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Yang
 */
@RestController
@RequiredArgsConstructor
public class JobLogApiImpl implements JobLogApi {
    private final ScheduleJobLoggerService scheduleJobLoggerService;

    @Override
    public Response<Void> saveLog(JobLogSaveDTO request) {
        ScheduleJobLoggerEntity log = new ScheduleJobLoggerEntity();
        BeanUtil.copyProperties(request, log);
        scheduleJobLoggerService.save(log);
        return Response.success();
    }
}
