/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package com.landleaf.job.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.EscapeUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.job.domain.entity.ScheduleJobLogEntity;
import com.landleaf.job.domain.request.ScheduleJobLogPageRequest;
import com.landleaf.job.domain.response.ScheduleJobLogResponse;
import com.landleaf.job.service.ScheduleJobLogService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 定时任务日志
 */
@RestController
@RequestMapping("/schedule-log")
@RequiredArgsConstructor
@Tag(name = "Job日志接口")
public class ScheduleJobLogController {
    private final ScheduleJobLogService scheduleJobLogService;

    /**
     * 定时任务日志列表
     */
    @GetMapping("/list")
    public Response<IPage<ScheduleJobLogResponse>> list(ScheduleJobLogPageRequest pageRequest) {
        return Response.success(scheduleJobLogService.queryPage(pageRequest));
    }

    /**
     * 定时任务日志信息
     */
    @GetMapping("/info/{logId}")
    public Response<ScheduleJobLogResponse> info(@PathVariable("logId") Long logId) {
        ScheduleJobLogEntity log = scheduleJobLogService.getById(logId);
        ScheduleJobLogResponse target = new ScheduleJobLogResponse();
        BeanUtil.copyProperties(log, target);
        target.setError(EscapeUtil.unescape(target.getError()));
        return Response.success(target);
    }
}
