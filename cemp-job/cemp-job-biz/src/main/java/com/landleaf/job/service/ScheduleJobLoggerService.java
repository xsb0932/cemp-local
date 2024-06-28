package com.landleaf.job.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.landleaf.job.domain.entity.ScheduleJobLoggerEntity;
import com.landleaf.job.domain.request.ScheduleLoggerPageRequest;
import com.landleaf.job.domain.response.ScheduleLoggerPageResponse;

/**
 * 定时任务日志的业务逻辑接口定义
 *
 * @author hebin
 * @since 2023-11-10
 */
public interface ScheduleJobLoggerService extends IService<ScheduleJobLoggerEntity> {

    /**
     * 查询定时任务日志分页列表
     *
     * @param request 请求参数
     * @return IPage<ScheduleLoggerPageResponse>
     */
    IPage<ScheduleLoggerPageResponse> queryPage(ScheduleLoggerPageRequest request);

    /**
     * 清理日志
     */
    void cleanLog();
}