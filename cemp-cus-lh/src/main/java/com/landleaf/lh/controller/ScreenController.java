package com.landleaf.lh.controller;

import com.landleaf.comm.base.pojo.Response;
import com.landleaf.lh.domain.response.*;
import com.landleaf.lh.service.impl.ScreenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.YearMonth;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/screen")
@Tag(name = "大屏监测接口", description = "大屏监测接口")
public class ScreenController {
    private final ScreenService screenService;

    private YearMonth getMonth() {
        return YearMonth.now().minusMonths(1L);
    }

    @GetMapping("/end-date")
    @Operation(summary = "统计截止日期")
    public Response<LhEndDateResponse> endDate() {
        YearMonth yearMonth = getMonth();
        return Response.success(new LhEndDateResponse(yearMonth.getYear(), yearMonth.getMonthValue()));
    }

    @GetMapping("/overview")
    @Operation(summary = "总览地图")
    public Response<LhOverviewResponse> overview() {
        return Response.success(screenService.overview(getMonth()));
    }

    @GetMapping("/project")
    @Operation(summary = "项目汇总")
    public Response<LhProjectResponse> project() {
        return Response.success(screenService.project());
    }

    @GetMapping("/maintenance-average")
    @Operation(summary = "平均报修数量趋势")
    public Response<LhMaintenanceAverageResponse> maintenanceAverage() {
        return Response.success(screenService.maintenanceAverage(getMonth()));
    }

    @GetMapping("/maintenance-sort")
    @Operation(summary = "报修数量排名")
    public Response<List<LhMaintenanceSortResponse>> maintenanceSort() {
        return Response.success(screenService.maintenanceSort(getMonth()));
    }

    @GetMapping("/energy-month")
    @Operation(summary = "总能耗趋势")
    public Response<LhEnergyMonthResponse> energyMonth() {
        return Response.success(screenService.energyMonth(getMonth()));
    }

    @GetMapping("/production-schedule")
    @Operation(summary = "生产进度")
    public Response<LhProductionScheduleResponse> productionSchedule() {
        return Response.success(screenService.productionSchedule(getMonth()));
    }

    @GetMapping("/energy-efficiency-sort")
    @Operation(summary = "能效排名")
    public Response<LhEnergyEfficiencySortResponse> energyEfficiencySort() {
        return Response.success(screenService.energyEfficiencySort(getMonth()));
    }

    @GetMapping("/energy-efficiency-avg")
    @Operation(summary = "平均能效趋势")
    public Response<LhEnergyEfficiencyAvgResponse> energyEfficiencyAvg() {
        return Response.success(screenService.energyEfficiencyAvg(getMonth()));
    }
}
