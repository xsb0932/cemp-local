package com.landleaf.energy.api;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.energy.enums.ApiConstants;
import com.landleaf.energy.request.MonthPlanBatchRequest;
import com.landleaf.energy.response.PlannedAreaMonthsDataResponse;
import com.landleaf.energy.response.PlannedWaterResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

@Tag(name = "Feign 服务 - 计划用水查询")
@FeignClient(name = ApiConstants.NAME)
public interface PlannedWaterApi {

    @Operation(summary = "获取时间范围内的项目计划用水合计")
    @GetMapping(ApiConstants.PREFIX + "/water/get-project-duration-total-plan")
    Response<BigDecimal> getProjectDurationTotalPlan(@RequestParam("bizProjectId") String bizProjectId,
                                                     @RequestParam("begin")
                                                     @JsonFormat(pattern = "yyyy-MM-dd")
                                                     @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                                                     @RequestParam("end")
                                                     @JsonFormat(pattern = "yyyy-MM-dd")
                                                     @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end);

    @Operation(summary = "年用水")
    @GetMapping(ApiConstants.PREFIX + "/water/plan/year")
    Response<List<PlannedWaterResponse>> getWaterPlanYear(@RequestParam("bizProjectId") String bizProjectId,
                                                          @RequestParam("year") String year,
                                                          @RequestParam("month") String month);

    @Operation(summary = "查询多个项目某一月的计划用水")
    @PostMapping(ApiConstants.PREFIX + "/water/plan/month-batch")
    Response<Map<String, Map<YearMonth, BigDecimal>>> batchMonthPlan(@RequestBody MonthPlanBatchRequest request);

    @Operation(summary = "区域-年分月用水指标")
    @GetMapping(ApiConstants.PREFIX + "/water/area/months")
    Response<List<PlannedAreaMonthsDataResponse>> getAreaMonthsData(@RequestParam("nodeId") String nodeId,
                                                                    @RequestParam("year") String year);
}
