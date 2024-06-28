package com.landleaf.job.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.landleaf.job.domain.entity.ScheduleJobLogEntity;
import com.landleaf.job.domain.request.ScheduleJobLogPageRequest;
import com.landleaf.job.domain.response.ScheduleJobLogResponse;

public interface ScheduleJobLogService extends IService<ScheduleJobLogEntity> {

    IPage<ScheduleJobLogResponse> queryPage(ScheduleJobLogPageRequest pageRequest);
}