package com.landleaf.lgc.controller;

import com.alibaba.fastjson2.JSONObject;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.comm.tenant.TenantContext;
import com.landleaf.lgc.domain.response.*;
import com.landleaf.lgc.domain.response.origin.*;
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
import java.util.Map;

/**
 * LGC 旧大屏
 *
 * @author xushibai
 * @since 2023/09/05
 **/
@RestController
@RequiredArgsConstructor
@RequestMapping("/screen/origin")
@Tag(name = "旧大屏相关接口", description = "旧大屏相关接口")
public class LgcOriginScreenController {
    private final ScreenService screenService;

    /**
     * 空气质量-室内多参
     *
     */
    @GetMapping("/sensor")
    @Operation(summary = "多参")
    public Response<Map<String, EnergySensor>> storage() {
        TenantContext.setTenantId(1004L);
        Map<String, EnergySensor> sensorMap  = screenService.getSensor();
        return Response.success(sensorMap);
    }

    /**
     * 年度累计
     *
     */
    @GetMapping("/energy-acc")
    @Operation(summary = "年度累计")
    public Response<EnergyAcc> acc() {
        TenantContext.setTenantId(1004L);
        EnergyAcc energyAcc  = screenService.getEnergyAcc();
        return Response.success(energyAcc);
    }

    /**
     * 年统计
     *
     */
    @GetMapping("/year-subitem")
    @Operation(summary = "年统计")
    public Response<List<EnergyItem>> subitemYear() {
        TenantContext.setTenantId(1004L);
        return Response.success(screenService.getSubitemYear());
    }

    /**
     * 月统计
     *
     */
    @GetMapping("/month-subitem")
    @Operation(summary = "月统计")
    public Response<List<EnergyItem>> subitemMonth() {
        TenantContext.setTenantId(1004L);
        return Response.success(screenService.getSubitemMonth());
    }

    /**
     * 月累计光伏发电量
     *
     */
    @GetMapping("/gf-line-chart")
    @Operation(summary = "月累计光伏发电量")
    public Response<EnergyLineChart> gfChart() {
        TenantContext.setTenantId(1004L);
        EnergyLineChart chart  = screenService.getGfChartMonth();
        return Response.success(chart);
    }

    /**
     * 月累计能耗
     *
     */
    @GetMapping("/energy-line-chart")
    @Operation(summary = "月累计能耗")
    public Response<EnergyLineChart> energyChart() {
        TenantContext.setTenantId(1004L);
        EnergyLineChart chart  = screenService.getEnergyChartMonth();
        return Response.success(chart);
    }

    /**
     * 月累计能耗
     *
     */
    @GetMapping("/weather")
    @Operation(summary = "月累计能耗")
    public Response<EnergyWeather> energyWeather() {
        TenantContext.setTenantId(1004L);
        EnergyWeather weather  = screenService.getEnergyEnergyWeather();
        return Response.success(weather);
    }
}
