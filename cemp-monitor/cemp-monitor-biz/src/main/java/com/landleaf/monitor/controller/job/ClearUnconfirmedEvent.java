package com.landleaf.monitor.controller.job;

import com.landleaf.comm.base.pojo.Response;
import com.landleaf.job.api.JobLogApi;
import com.landleaf.job.api.dto.JobLogSaveDTO;
import com.landleaf.job.api.dto.JobRpcRequest;
import com.landleaf.monitor.service.AlarmConfirmService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

import static com.landleaf.comm.constance.CommonConstant.JOB_EXEC_ERROR;
import static com.landleaf.comm.constance.CommonConstant.JOB_EXEC_SUCCESS;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/job/event/sys-confirm")
@Tag(name = "系统确认告警下发Job 接口定义", description = "系统确认告警下发Job 接口定义")
public class ClearUnconfirmedEvent {
    @Resource
    private JobLogApi jobLogApi;

    @Resource
    private AlarmConfirmService alarmConfirmService;

    @PostMapping("/confirm")
    @Operation(summary = "系统自动确认超过30天的event")
    public Response<Void> confirm(@RequestBody JobRpcRequest request) {
        JobLogSaveDTO jobLog = new JobLogSaveDTO();
        jobLog.setJobId(request.getJobId())
                .setTenantId(0L)
                .setProjectIds("")
                .setProjectNames("")
                .setExecTime(LocalDateTime.now())
                .setExecType(request.getExecType())
                .setExecUser(request.getExecUser());
        try {
            alarmConfirmService.sysConfirm();
            jobLog.setStatus(JOB_EXEC_SUCCESS);
        } catch (Exception e) {
            jobLog.setStatus(JOB_EXEC_ERROR);
            throw e;
        } finally {
            jobLogApi.saveLog(jobLog);
        }
        return Response.success();
    }
}
