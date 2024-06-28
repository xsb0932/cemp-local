package com.landleaf.job.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.job.domain.request.ScheduleLoggerPageRequest;
import com.landleaf.job.domain.response.ScheduleLoggerPageResponse;
import com.landleaf.job.service.ScheduleJobLoggerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 定时任务日志的控制层接口定义
 *
 * @author hebin
 * @since 2023-11-10
 */
@RestController
@AllArgsConstructor
@RequestMapping("/schedule-logger")
@Tag(name = "定时任务日志的控制层接口定义", description = "定时任务日志的控制层接口定义")
public class ScheduleJobLoggerController {
    /**
     * 定时任务日志的相关逻辑操作句柄
     */
    private final ScheduleJobLoggerService scheduleJobLoggerServiceImpl;

    @GetMapping("/list")
    @Operation(description = "查询定时任务日志分页列表")
    public Response<IPage<ScheduleLoggerPageResponse>> list(@Validated ScheduleLoggerPageRequest request) {
        return Response.success(scheduleJobLoggerServiceImpl.queryPage(request));
    }

}