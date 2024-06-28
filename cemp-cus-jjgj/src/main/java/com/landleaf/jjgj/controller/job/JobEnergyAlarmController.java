package com.landleaf.jjgj.controller.job;

import com.landleaf.comm.base.pojo.Response;
import com.landleaf.jjgj.service.impl.EnergyAlarmService;
import com.landleaf.job.api.JobLogApi;
import com.landleaf.job.api.dto.JobLogSaveDTO;
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

import static com.landleaf.comm.constance.CommonConstant.JOB_EXEC_ERROR;
import static com.landleaf.comm.constance.CommonConstant.JOB_EXEC_SUCCESS;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/job/energy-alarm")
@Tag(name = "用能告警Job接口定义", description = "用能告警Job接口定义")
public class JobEnergyAlarmController {
    private JobLogApi jobLogApi;
    private EnergyAlarmService energyAlarmService;

    @PostMapping("/last-week")
    @Operation(summary = "上周用能告警任务")
    public Response<Void> lastWeek(@RequestBody JobRpcRequest request) {
        JobLogSaveDTO jobLog = new JobLogSaveDTO();
        jobLog.setJobId(request.getJobId())
                // 这种定制的懒得搞了 开发生产id目前一致 写死得了
                .setTenantId(2L)
                .setProjectIds("PJ00000001")
                .setProjectNames("锦江体验中心酒店")
                .setExecTime(LocalDateTime.now())
                .setExecType(request.getExecType())
                .setExecUser(request.getExecUser());
        try {
            energyAlarmService.lastWeek(request);
            jobLog.setStatus(JOB_EXEC_SUCCESS);
        } catch (Exception e) {
            log.error("上周用能告警任务异常", e);
            jobLog.setStatus(JOB_EXEC_ERROR);
        }
        jobLogApi.saveLog(jobLog);
        return Response.success();
    }
}
