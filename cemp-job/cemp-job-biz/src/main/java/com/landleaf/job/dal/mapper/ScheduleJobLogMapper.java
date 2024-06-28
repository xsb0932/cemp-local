package com.landleaf.job.dal.mapper;

import com.landleaf.job.domain.entity.ScheduleJobLogEntity;
import com.landleaf.pgsql.extension.ExtensionMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ScheduleJobLogMapper extends ExtensionMapper<ScheduleJobLogEntity> {

    @Delete("DELETE FROM tb_schedule_job_log WHERE CURRENT_DATE - INTERVAL '30 day' > create_time")
    void cleanLog();
}