package com.landleaf.job.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.EscapeUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.landleaf.job.dal.mapper.ScheduleJobLogMapper;
import com.landleaf.job.domain.entity.ScheduleJobLogEntity;
import com.landleaf.job.domain.request.ScheduleJobLogPageRequest;
import com.landleaf.job.domain.response.ScheduleJobLogResponse;
import com.landleaf.job.service.ScheduleJobLogService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service("scheduleJobLogService")
@AllArgsConstructor
@Slf4j
public class ScheduleJobLogServiceImpl extends ServiceImpl<ScheduleJobLogMapper, ScheduleJobLogEntity> implements ScheduleJobLogService {

    @Override
    public IPage<ScheduleJobLogResponse> queryPage(ScheduleJobLogPageRequest pageRequest) {
        LambdaQueryWrapper<ScheduleJobLogEntity> wrapper = new LambdaQueryWrapper<>();
        if (null != pageRequest.getJobId()) {
            wrapper.eq(ScheduleJobLogEntity::getJobId, pageRequest.getJobId());
        }
        return baseMapper.selectPage(Page.of(pageRequest.getPageNo(), pageRequest.getPageSize()), wrapper)
                .convert(o -> {
                    ScheduleJobLogResponse target = new ScheduleJobLogResponse();
                    BeanUtil.copyProperties(o, target);
                    target.setError(EscapeUtil.unescape(target.getError()));
                    return target;
                });
    }
}
