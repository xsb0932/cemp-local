package com.landleaf.lgc.controller;

import com.landleaf.comm.base.pojo.Response;
import com.landleaf.comm.tenant.TenantContext;
import com.landleaf.lgc.domain.response.*;
import com.landleaf.lgc.service.ScreenService;
import com.landleaf.monitor.dto.AlarmResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 大屏相关接口
 *
 * @author 张力方
 * @since 2023/7/28
 **/
@RestController
@RequiredArgsConstructor
@RequestMapping("/screen")
@Tag(name = "大屏相关接口", description = "大屏相关接口")
public class ScreenController {
    private final ScreenService screenService;

    /**
     * 总览
     *
     * @param projectBizId 项目业务id
     */
    @GetMapping("/overview")
    @Operation(summary = "总览")
    public Response<OverviewResponse> overview(@RequestParam String projectBizId) {
        TenantContext.setTenantId(1004L);
        OverviewResponse overview = screenService.overview(projectBizId);
        return Response.success(overview);
    }

    /**
     * 光伏
     *
     * @param projectBizId 项目业务id
     */
    @GetMapping("/pv")
    @Operation(summary = "光伏")
    public Response<PvResponse> pv(@RequestParam String projectBizId) {
        TenantContext.setTenantId(1004L);
        PvResponse pv = screenService.pv(projectBizId);
        return Response.success(pv);
    }

    /**
     * 储能
     *
     * @param projectBizId 项目业务id
     */
    @GetMapping("/storage")
    @Operation(summary = "储能")
    public Response<StorageResponse> storage(@RequestParam String projectBizId) {
        TenantContext.setTenantId(1004L);
        StorageResponse storage = screenService.storage(projectBizId);
        return Response.success(storage);
    }

    /**
     * 充电桩
     *
     * @param projectBizId 项目业务id
     */
    @GetMapping("/charge")
    @Operation(summary = "充电桩")
    public Response<ChargeResponse> charge(@RequestParam String projectBizId) {
        TenantContext.setTenantId(1004L);
        ChargeResponse charge = screenService.charge(projectBizId);
        return Response.success(charge);
    }

    /**
     * 暖通
     *
     * @param projectBizId 项目业务id
     */
    @GetMapping("/hvac")
    @Operation(summary = "暖通")
    public Response<HvacResponse> hvac(@RequestParam String projectBizId) {
        TenantContext.setTenantId(1004L);
        HvacResponse hvac = screenService.hvac(projectBizId);
        return Response.success(hvac);
    }

    /**
     * 获取光伏告警列表
     *
     * @param projectBizId 项目id
     * @return 光伏告警列表
     */
    @GetMapping("/pv/alarm")
    @Operation(summary = "获取光伏告警列表")
    public Response<List<AlarmResponse>> getPvAlarm(@RequestParam String projectBizId) {
        TenantContext.setTenantId(1004L);
        List<AlarmResponse> pvAlarm = screenService.getPvAlarm(projectBizId);
        return Response.success(pvAlarm);
    }

    /**
     * 获取储能告警列表
     *
     * @param projectBizId 项目id
     * @return 储能告警列表
     */
    @GetMapping("/storage/alarm")
    @Operation(summary = "获取储能告警列表")
    public Response<List<AlarmResponse>> getStorageAlarm(@RequestParam String projectBizId) {
        TenantContext.setTenantId(1004L);
        List<AlarmResponse> alarm = screenService.getStorageAlarm(projectBizId);
        return Response.success(alarm);
    }

    /**
     * 获取充电桩告警列表
     *
     * @param projectBizId 项目id
     * @return 充电桩告警列表
     */
    @GetMapping("/charge/alarm")
    @Operation(summary = "获取充电桩告警列表")
    public Response<List<AlarmResponse>> getChargeAlarm(@RequestParam String projectBizId) {
        TenantContext.setTenantId(1004L);
        List<AlarmResponse> alarm = screenService.getChargeAlarm(projectBizId);
        return Response.success(alarm);
    }

    /**
     * 获取暖通告警列表
     *
     * @param projectBizId 项目id
     * @return 暖通告警列表
     */
    @GetMapping("/hvac/alarm")
    @Operation(summary = "获取暖通告警列表")
    public Response<List<AlarmResponse>> getHvacAlarm(@RequestParam String projectBizId) {
        List<AlarmResponse> alarm = screenService.getHvacAlarm(projectBizId);
        return Response.success(alarm);
    }

}
