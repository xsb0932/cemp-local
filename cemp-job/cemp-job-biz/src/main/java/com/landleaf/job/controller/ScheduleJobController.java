/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package com.landleaf.job.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.comm.exception.BusinessException;
import com.landleaf.job.api.JobLogApi;
import com.landleaf.job.api.dto.JobLogSaveDTO;
import com.landleaf.job.api.dto.JobRpcRequest;
import com.landleaf.job.domain.entity.ScheduleJobEntity;
import com.landleaf.job.domain.request.ScheduleJobPageRequest;
import com.landleaf.job.domain.request.ScheduleJobSaveRequest;
import com.landleaf.job.domain.request.ScheduleJobUpdateRequest;
import com.landleaf.job.domain.request.ScheduleManualRunRequest;
import com.landleaf.job.domain.response.ScheduleJobResponse;
import com.landleaf.job.service.ScheduleJobLoggerService;
import com.landleaf.job.service.ScheduleJobService;
import com.landleaf.oauth.api.TenantApi;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

import static com.landleaf.comm.constance.CommonConstant.JOB_EXEC_ERROR;
import static com.landleaf.comm.constance.CommonConstant.JOB_EXEC_SUCCESS;

/**
 * 定时任务
 */
@Slf4j
@RestController
@RequestMapping("/schedule")
@RequiredArgsConstructor
@Tag(name = "Job接口")
public class ScheduleJobController {
    private final ScheduleJobService scheduleJobService;
    private final ScheduleJobLoggerService scheduleJobLoggerService;
    private final TenantApi tenantApi;
    private final JobLogApi jobLogApi;

    @GetMapping("/list")
    @Operation(description = "定时任务列表")
    public Response<IPage<ScheduleJobResponse>> list(ScheduleJobPageRequest request) {
        return Response.success(scheduleJobService.queryPage(request));
    }

    /**
     * 定时任务信息
     */
    @GetMapping("/info/{jobId}")
    public Response<ScheduleJobResponse> info(@PathVariable("jobId") Long jobId) {
        ScheduleJobEntity schedule = scheduleJobService.getById(jobId);
        ScheduleJobResponse target = new ScheduleJobResponse();
        BeanUtil.copyProperties(schedule, target);
        return Response.success(target);
    }

    /**
     * 保存定时任务
     */
    @PostMapping("/save")
    public Response<ScheduleJobResponse> save(@RequestBody ScheduleJobSaveRequest saveRequest) {
        ScheduleJobEntity schedule = scheduleJobService.saveJob(saveRequest);
        ScheduleJobResponse target = new ScheduleJobResponse();
        BeanUtil.copyProperties(schedule, target);
        return Response.success(target);
    }

    /**
     * 修改定时任务
     */
    @PutMapping("/update")
    public Response<ScheduleJobResponse> update(@RequestBody ScheduleJobUpdateRequest updateRequest) {
        ScheduleJobEntity schedule = scheduleJobService.update(updateRequest);
        ScheduleJobResponse target = new ScheduleJobResponse();
        BeanUtil.copyProperties(schedule, target);
        return Response.success(target);
    }

    /**
     * 删除定时任务
     */
    @DeleteMapping("/delete")
    public Response<Boolean> delete(@RequestBody List<Long> jobIds) {
        scheduleJobService.deleteBatch(jobIds);
        return Response.success(Boolean.TRUE);
    }

    /**
     * 立即执行任务
     */
    @PutMapping("/run")
    public Response<Boolean> run(@RequestBody List<Long> jobIds) {
        scheduleJobService.run(jobIds);
        return Response.success(Boolean.TRUE);
    }

    @PutMapping("/manual-run")
    @Operation(description = "手动执行定时任务")
    public Response<Boolean> manualRun(@Validated @RequestBody ScheduleManualRunRequest request) {
        if (StrUtil.isBlank(request.getStartTime())) {
            throw new BusinessException("开始时间不能为空");
        }
        if (StrUtil.isBlank(request.getEndTime())) {
            throw new BusinessException("结束时间不能为空");
        }
        scheduleJobService.manualRunV2(request);
        return Response.success(Boolean.TRUE);
    }

    /**
     * 暂停定时任务
     */
    @PutMapping("/pause")
    public Response<Boolean> pause(@RequestBody List<Long> jobIds) {
        scheduleJobService.pause(jobIds);
        return Response.success(Boolean.TRUE);
    }

    /**
     * 恢复定时任务
     */
    @PutMapping("/resume")
    public Response<Boolean> resume(@RequestBody List<Long> jobIds) {
        scheduleJobService.resume(jobIds);
        return Response.success(Boolean.TRUE);
    }

    @PostMapping("/clean-log")
    @Operation(summary = "清理日志")
    public Response<Void> cleanLog(@RequestBody JobRpcRequest request) {
        JobLogSaveDTO jobLog = new JobLogSaveDTO();
        jobLog.setJobId(request.getJobId())
                .setExecTime(LocalDateTime.now())
                .setExecType(request.getExecType())
                .setExecUser(request.getExecUser());
        try {
            Long tenantId = tenantApi.getTenantAdmin().getCheckedData();
            jobLog.setTenantId(tenantId);
            scheduleJobLoggerService.cleanLog();
            log.info("清理日志");
            jobLog.setStatus(JOB_EXEC_SUCCESS);
        } catch (Exception e) {
            log.error("清理日志", e);
            jobLog.setStatus(JOB_EXEC_ERROR);
        }
        jobLogApi.saveLog(jobLog);
        return Response.success();
    }

    /**
     * 测试定时任务
     */
    @GetMapping("/test-job")
    public Response<String> testJob() {
        log.info("测试定时任务执行：{}", LocalDateTimeUtil.formatNormal(LocalDateTime.now()));
        return Response.success("success");
    }
}
