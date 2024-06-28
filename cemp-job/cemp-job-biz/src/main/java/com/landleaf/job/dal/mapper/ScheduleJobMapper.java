package com.landleaf.job.dal.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.landleaf.job.domain.entity.ScheduleJobEntity;
import com.landleaf.job.domain.response.ScheduleJobResponse;
import com.landleaf.pgsql.extension.ExtensionMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ScheduleJobMapper extends ExtensionMapper<ScheduleJobEntity> {

    Page<ScheduleJobResponse> queryPage(@Param("page") Page<Object> page,
                                        @Param("tenantId") Long tenantId,
                                        @Param("jobName") String jobName,
                                        @Param("lastStatus") Integer lastStatus,
                                        @Param("statisticType") Integer statisticType);
}