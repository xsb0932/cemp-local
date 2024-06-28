package com.landleaf.bms.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.landleaf.bms.domain.request.AlarmPushRuleAddRequest;
import com.landleaf.bms.domain.request.AlarmPushRuleConfigSaveRequest;
import com.landleaf.bms.domain.request.AlarmPushRuleEditRequest;
import com.landleaf.bms.domain.request.AlarmPushRulePageRequest;
import com.landleaf.bms.domain.response.AlarmPushRuleConfigResponse;
import com.landleaf.bms.domain.response.AlarmPushRulePageResponse;
import com.landleaf.bms.service.AlarmPushRuleService;
import com.landleaf.bms.service.impl.AlarmPushService;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.comm.tenant.TenantContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * 告警推送规则的控制层接口定义
 *
 * @author hebin
 * @since 2024-05-31
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/alarm-push-rule")
@Tag(name = "告警推送规则的控制层接口定义", description = "告警推送规则的控制层接口定义")
public class AlarmPushRuleController {
    private final AlarmPushRuleService alarmPushRuleService;
    private final AlarmPushService alarmPushService;

    @PostMapping("/page")
    @Operation(summary = "分页列表", description = "分页列表")
    public Response<Page<AlarmPushRulePageResponse>> page(@RequestBody @Validated AlarmPushRulePageRequest request) {
        return Response.success(alarmPushRuleService.selectPage(request));
    }

    @PostMapping("/add")
    @Operation(summary = "添加", description = "添加")
    public Response<Void> add(@RequestBody @Validated AlarmPushRuleAddRequest request) {
        alarmPushRuleService.add(request);
        return Response.success();
    }

    @PutMapping("/edit")
    @Operation(summary = "修改", description = "修改")
    public Response<Void> edit(@RequestBody @Validated AlarmPushRuleEditRequest request) {
        alarmPushRuleService.edit(request);
        return Response.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除", description = "删除")
    public Response<Void> delete(@PathVariable("id") Long id) {
        alarmPushRuleService.delete(id);
        return Response.success();
    }

    @PostMapping("/enable/{id}")
    @Operation(summary = "启用", description = "启用")
    public Response<Void> enable(@PathVariable("id") Long id) {
        alarmPushRuleService.enable(id);
        alarmPushService.refresh(TenantContext.getTenantId());
        return Response.success();
    }

    @PostMapping("/disable/{id}")
    @Operation(summary = "停用", description = "停用")
    public Response<Void> disable(@PathVariable("id") Long id) {
        alarmPushRuleService.disable(id);
        alarmPushService.refresh(TenantContext.getTenantId());
        return Response.success();
    }

    @GetMapping("/config/{id}")
    @Operation(summary = "配置详情", description = "配置详情")
    public Response<AlarmPushRuleConfigResponse> config(@PathVariable("id") Long id) {
        return Response.success(alarmPushRuleService.config(id));
    }

    @PostMapping("/config/save")
    @Operation(summary = "配置保存", description = "配置保存")
    public Response<Void> configSave(@RequestBody @Validated AlarmPushRuleConfigSaveRequest request) {
        alarmPushRuleService.configSave(request);
        return Response.success();
    }

    @GetMapping("/cache/refresh")
    @Operation(summary = "刷新配置缓存", description = "刷新配置缓存")
    public Response<Void> configSave() {
        alarmPushService.refresh(null);
        return Response.success();
    }

}