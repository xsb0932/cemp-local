package com.landleaf.monitor.controller.job;

import com.landleaf.comm.base.pojo.Response;
import com.landleaf.job.api.JobLogApi;
import com.landleaf.job.api.dto.JobLogSaveDTO;
import com.landleaf.job.api.dto.JobRpcRequest;
import com.landleaf.monitor.domain.dto.WeatherDTO;
import com.landleaf.monitor.service.impl.WeatherJobService;
import com.landleaf.oauth.api.TenantApi;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

import static com.landleaf.comm.constance.CommonConstant.JOB_EXEC_ERROR;
import static com.landleaf.comm.constance.CommonConstant.JOB_EXEC_SUCCESS;


/**
 * 天气 Job接口定义
 *
 * @author Yang
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/job/weather")
@Tag(name = "天气 Job接口定义", description = "天气 Job接口定义")
public class WeatherJobController {
    private WeatherJobService weatherJobService;
    private JobLogApi jobLogApi;
    private TenantApi tenantApi;

    @PostMapping("/sync")
    @Operation(summary = "同步项目的天气信息接口")
    public Response<Void> sync(@RequestBody JobRpcRequest request) {
        JobLogSaveDTO jobLog = new JobLogSaveDTO();
        jobLog.setJobId(request.getJobId())
                .setExecTime(LocalDateTime.now())
                .setExecType(request.getExecType())
                .setExecUser(request.getExecUser());
        try {
            Long tenantId = tenantApi.getTenantAdmin().getCheckedData();
            jobLog.setTenantId(tenantId);
            Map<String, WeatherDTO> data = weatherJobService.sync();
            log.info("同步天气结果-{}", data);
            jobLog.setStatus(JOB_EXEC_SUCCESS);
        } catch (Exception e) {
            log.error("同步天气异常", e);
            jobLog.setStatus(JOB_EXEC_ERROR);
        }
        jobLogApi.saveLog(jobLog);
        return Response.success();
    }

}