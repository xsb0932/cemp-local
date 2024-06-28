package com.landleaf.energy.controller;

import com.landleaf.comm.base.pojo.Response;
import com.landleaf.energy.domain.request.PlanElectricityRequest;
import com.landleaf.energy.domain.response.PlanElectricityTabulationResponse;
import com.landleaf.energy.service.PlannedElectricityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.Year;
import java.util.List;

/**
 * 计划用电接口
 *
 * @author Tycoon
 * @since 2023/8/11 9:49
 **/
@Tag(name = "计划用电接口", description = "计划用电接口")
@RestController
@RequestMapping("/planned/electricity")
@RequiredArgsConstructor
public class PlannedElectricityController {

    private final PlannedElectricityService plannedElectricityService;

    /**
     * 初始化年份计划用电
     *
     * @param request 初始化参数
     */
    @Operation(summary = "初始化年份计划用电")
    @PostMapping("/initialize")
    public Response<Void> initPlanElectricity(@Validated @RequestBody PlanElectricityRequest.Initialize request) {
        plannedElectricityService.initPlanElectricity(request);
        return Response.success();    }

    /**
     * 变更计划用电量
     *
     * @param request 变更参数
     */
    @Operation(summary = "变更计划用电量")
    @PutMapping("/change")
    public Response<Void> updatePlanElectricity(@Validated @RequestBody PlanElectricityRequest.Change request) {
        plannedElectricityService.updatePlanElectricity(request);
        return Response.success();
    }

    /**
     * 获取该年份的用电列表
     *
     * @param projectBizId 项目业务ID
     * @param year 年份
     * @return 结果
     */
    @Parameter(name = "projectBizId", description = "项目业务ID", required = true)
    @Parameter(name = "year", description = "年份", required = true)
    @Operation(summary = "获取该年份的用电列表")
    @GetMapping("/plans")
    public Response<List<PlanElectricityTabulationResponse>> searchElectricityTabulation(@RequestParam("projectBizId") String projectBizId,
                                                                                         @RequestParam("year") Year year) {
        return Response.success(plannedElectricityService.searchElectricityTabulation(projectBizId, year));
    }

}
