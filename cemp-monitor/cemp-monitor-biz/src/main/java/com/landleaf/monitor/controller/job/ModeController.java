package com.landleaf.monitor.controller.job;

import com.landleaf.comm.base.pojo.Response;
import com.landleaf.job.api.JobLogApi;
import com.landleaf.job.api.dto.JobLogSaveDTO;
import com.landleaf.job.api.dto.JobRpcRequest;
import com.landleaf.monitor.service.impl.ModeJobService;
import com.landleaf.oauth.api.TenantApi;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

import static com.landleaf.comm.constance.CommonConstant.JOB_EXEC_ERROR;
import static com.landleaf.comm.constance.CommonConstant.JOB_EXEC_SUCCESS;


/**
 * 模式下发Job 接口定义
 *
 * @author xushibai
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/job/mode")
@Tag(name = "模式下发Job 接口定义", description = "模式下发Job 接口定义")
public class ModeController {
    private JobLogApi jobLogApi;
    private TenantApi tenantApi;
    private ModeJobService modeJobService;

    @PostMapping("/sync/{type}")
    @Operation(summary = "模式下发")
    public Response<Void> sync1(@PathVariable Integer type, @RequestBody JobRpcRequest request) throws InterruptedException {
        JobLogSaveDTO jobLog = new JobLogSaveDTO();
//        Long tenantId;
//        try {
//            tenantId = tenantApi.getTenantIdByCode("JJGJ").getCheckedData();
//        } catch (Exception e) {
//            log.error("rpc error", e);
//            tenantId = 2L;
//        }

        jobLog.setJobId(request.getJobId())
                // 这种定制的懒得搞了 开发生产id目前一致 写死得了
                .setTenantId(2L)
                .setProjectIds("PJ00000001")
                .setProjectNames("锦江体验中心酒店")
                .setExecTime(LocalDateTime.now())
                .setExecType(request.getExecType())
                .setExecUser(request.getExecUser());
        try {
            modeJobService.sync(type);
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
