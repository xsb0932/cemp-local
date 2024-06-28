package com.landleaf.job.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.landleaf.bms.api.DictApi;
import com.landleaf.bms.api.dto.DictDataResponse;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.job.dal.mapper.ScheduleJobLogMapper;
import com.landleaf.job.dal.mapper.ScheduleJobLoggerMapper;
import com.landleaf.job.domain.entity.ScheduleJobLoggerEntity;
import com.landleaf.job.domain.request.ScheduleLoggerPageRequest;
import com.landleaf.job.domain.response.ScheduleLoggerPageResponse;
import com.landleaf.job.service.ScheduleJobLoggerService;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

/**
 * 定时任务日志的业务逻辑接口实现
 *
 * @author hebin
 * @since 2023-11-10
 */
@Service
@AllArgsConstructor
@Slf4j
public class ScheduleJobLoggerServiceImpl extends ServiceImpl<ScheduleJobLoggerMapper, ScheduleJobLoggerEntity> implements ScheduleJobLoggerService {
    @Resource
    private DictApi dictApi;
    @Resource
    private ScheduleJobLogMapper scheduleJobLogMapper;

    @Override
    public IPage<ScheduleLoggerPageResponse> queryPage(ScheduleLoggerPageRequest request) {
        Page<ScheduleLoggerPageResponse> page = baseMapper.queryPage(Page.of(request.getPageNo(), request.getPageSize()), request);

        HashMap<String, String> execTypeMap = new HashMap<>(8);
        HashMap<String, String> statusMap = new HashMap<>(8);

        Response<List<DictDataResponse>> execTypeResponse = dictApi.getDictDataList("JOB_EXEC_TYPE");
        if (execTypeResponse.isSuccess()) {
            execTypeResponse.getResult().forEach(o -> execTypeMap.put(o.getValue(), o.getLabel()));
        }

        Response<List<DictDataResponse>> statusResponse = dictApi.getDictDataList("JOB_LAST_STATUS");
        if (statusResponse.isSuccess()) {
            statusResponse.getResult().forEach(o -> statusMap.put(o.getValue(), o.getLabel()));
        }

        page.getRecords().forEach(o -> {
            o.setExecTypeName(execTypeMap.get(String.valueOf(o.getExecType())));
            o.setStatusName(statusMap.get(String.valueOf(o.getStatus())));
        });
        return page;
    }

    @Override
    public void cleanLog() {
        scheduleJobLogMapper.cleanLog();
        baseMapper.cleanLog();
    }
}