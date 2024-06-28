package com.landleaf.jjgj.controller;

import com.landleaf.comm.base.pojo.Response;
import com.landleaf.jjgj.domain.request.ReportPushConfigSaveRequest;
import com.landleaf.jjgj.domain.response.ReportPushConfigResponse;
import com.landleaf.jjgj.service.JjgjReportPushService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * 锦江报表推送配置的控制层接口定义
 *
 * @author hebin
 * @since 2023-11-21
 */
@RestController
@AllArgsConstructor
@RequestMapping("/jjgj-report-push")
@Tag(name = "锦江报表推送配置的控制层接口定义", description = "锦江报表推送配置的控制层接口定义")
public class JjgjReportPushController {
    /**
     * 锦江报表推送配置的相关逻辑操作句柄
     */
    private final JjgjReportPushService jjgjReportPushService;


    @GetMapping("/project-config")
    @Operation(summary = "查询项目报表推送配置")
    public Response<ReportPushConfigResponse> projectConfig(@RequestParam("bizProjectId") String bizProjectId) {
        return Response.success(jjgjReportPushService.projectConfig(bizProjectId));
    }

    @PutMapping("/save")
    @Operation(summary = "保存or更新项目报表推送配置")
    public Response<Void> save(@Validated @RequestBody ReportPushConfigSaveRequest request) {
        jjgjReportPushService.save(request);
        return Response.success();
    }
}