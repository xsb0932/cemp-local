package com.landleaf.jjgj.controller.job;

import com.landleaf.comm.base.pojo.Response;
import com.landleaf.jjgj.service.JjgjReportPushService;
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
@RequestMapping("/job/report")
@Tag(name = "报表推送Job接口定义", description = "报表推送Job接口定义")
public class JobReportPushController {
    private JobLogApi jobLogApi;
    private JjgjReportPushService jjgjReportPushService;

    @PostMapping("/push")
    @Operation(summary = "报表推送定时任务接口")
    public Response<Void> reportPush(@RequestBody JobRpcRequest request) {
        JobLogSaveDTO jobLog = new JobLogSaveDTO();
        jobLog.setJobId(request.getJobId())
                .setTenantId(2L)
                .setExecTime(LocalDateTime.now())
                .setExecType(request.getExecType())
                .setExecUser(request.getExecUser());
        try {
            jjgjReportPushService.reportPush(request, jobLog);
            jobLog.setStatus(JOB_EXEC_SUCCESS);
        } catch (Exception e) {
            log.error("报表推送异常", e);
            jobLog.setStatus(JOB_EXEC_ERROR);
        }
        jobLogApi.saveLog(jobLog);
        return Response.success();
    }
}
