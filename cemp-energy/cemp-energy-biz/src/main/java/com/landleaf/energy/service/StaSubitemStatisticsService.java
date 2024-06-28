package com.landleaf.energy.service;

import com.landleaf.job.api.dto.JobRpcRequest;

/**
 * 分项统计逻辑定义
 */
public interface StaSubitemStatisticsService {
    /**
     * 按小时统计分项
     *
     * @param times
     * @param request
     * @return
     */
    boolean statisticsByHour(String[] times, JobRpcRequest request);

    /**
     * 按天统计分项
     *
     * @param times
     * @param request
     * @return
     */
    boolean statisticsByDay(String[] times, JobRpcRequest request);

    /**
     * 按月统计分项
     *
     * @param times
     * @param request
     * @return
     */
    boolean statisticsByMonth(String[] times, JobRpcRequest request);

    /**
     * 按年统计分项
     *
     * @param times
     * @param request
     * @return
     */
    boolean statisticsByYear(String[] times, JobRpcRequest request);
}
