package com.landleaf.job.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.landleaf.job.domain.entity.ScheduleJobEntity;
import com.landleaf.job.domain.request.ScheduleJobPageRequest;
import com.landleaf.job.domain.request.ScheduleJobSaveRequest;
import com.landleaf.job.domain.request.ScheduleJobUpdateRequest;
import com.landleaf.job.domain.request.ScheduleManualRunRequest;
import com.landleaf.job.domain.response.ScheduleJobResponse;

import java.util.List;

public interface ScheduleJobService extends IService<ScheduleJobEntity> {

    IPage<ScheduleJobResponse> queryPage(ScheduleJobPageRequest request);

    ScheduleJobEntity saveJob(ScheduleJobSaveRequest saveRequest);

    ScheduleJobEntity update(ScheduleJobUpdateRequest updateRequest);

    void deleteBatch(List<Long> jobIds);

    void run(List<Long> jobIds);

    void pause(List<Long> jobIds);

    void resume(List<Long> jobIds);

    void manualRun(ScheduleManualRunRequest request);

    void manualRunV2(ScheduleManualRunRequest request);
}