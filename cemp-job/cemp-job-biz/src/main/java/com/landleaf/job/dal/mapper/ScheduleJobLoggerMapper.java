package com.landleaf.job.dal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.landleaf.job.domain.entity.ScheduleJobLoggerEntity;
import com.landleaf.job.domain.request.ScheduleLoggerPageRequest;
import com.landleaf.job.domain.response.ScheduleLoggerPageResponse;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;

/**
 * 定时任务日志的数据库操作句柄
 *
 * @author hebin
 * @since 2023-11-10
 */
public interface ScheduleJobLoggerMapper extends BaseMapper<ScheduleJobLoggerEntity> {

    Page<ScheduleLoggerPageResponse> queryPage(@Param("page") Page<Object> page, @Param("request") ScheduleLoggerPageRequest request);

    @Delete("DELETE FROM tb_schedule_job_logger WHERE CURRENT_DATE - INTERVAL '30 day' > create_time")
    void cleanLog();
}