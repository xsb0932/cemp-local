package com.landleaf.energy.controller.job;

import com.landleaf.comm.base.pojo.Response;
import com.landleaf.energy.service.PlannedElectricityService;
import com.landleaf.job.api.dto.JobRpcRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/job/plan")
@Tag(name = "计划录入 Job接口定义", description = "计划录入 Job接口定义")
public class JobPlanController {
    private PlannedElectricityService plannedElectricityService;

    @PostMapping("/electricity-reminder")
    @Operation(summary = "电价录入提醒")
    public Response<Void> electricityReminder(@RequestBody JobRpcRequest request) {
        log.info("电价录入提醒任务执行 {}", request);
        LocalDateTime now;
        if (null == request.getExecTime()) {
            // 定时执行
            now = LocalDateTime.now();
        } else {
            // 手动执行
            now = request.getExecTime();
        }
        plannedElectricityService.electricityReminder(now, request);
        return Response.success();
    }

    @PostMapping("/year-reminder")
    @Operation(summary = "每年计划录入提醒")
    public Response<Void> yearReminder(@RequestBody JobRpcRequest request) {
        log.info("每年计划录入提醒任务执行 {}", request);
        LocalDateTime now;
        if (null == request.getExecTime()) {
            // 定时执行
            now = LocalDateTime.now();
        } else {
            // 手动执行
            now = request.getExecTime();
        }
        plannedElectricityService.yearReminder(now, request);
        return Response.success();
    }
}
