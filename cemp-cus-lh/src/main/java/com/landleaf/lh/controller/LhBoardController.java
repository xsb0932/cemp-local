package com.landleaf.lh.controller;

import com.landleaf.comm.base.pojo.Response;
import com.landleaf.comm.tenant.TenantContext;

import com.landleaf.lh.domain.enums.LhConstants;
import com.landleaf.lh.domain.response.*;
import com.landleaf.lh.service.LhBoardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 绿慧定制大看板
 *
 * @author xushibai
 * @since 2024/01/26
 **/
@RestController
@RequiredArgsConstructor
@RequestMapping("/board")
@Tag(name = "绿慧定制大看板接口", description = "绿慧定制大看板接口")
public class LhBoardController {

    private final LhBoardService lhBoardService;

    /**
     * 项目信息
     *
     */
    @GetMapping("/project/info")
    @Operation(summary = "项目信息")
    public Response<LhProjectInfoResponse> projectInfo(@RequestParam("projectId") String projectId) {
        TenantContext.setTenantId(LhConstants.LH_TENANT_ID);
        LhProjectInfoResponse response = lhBoardService.getCityInfo(projectId);
        return Response.success(response);
    }

    /**
     * 概览数据
     *
     */
    @GetMapping("/title")
    @Operation(summary = "概览数据")
    public Response<LhBoardTitleResponse> title(@RequestParam("projectId") String projectId) {
        TenantContext.setTenantId(LhConstants.LH_TENANT_ID);
        LhBoardTitleResponse resonse  = lhBoardService.getTitle(projectId);
        return Response.success(resonse);
    }

    /**
     * 当年计划执行
     *
     */
    @GetMapping("/target")
    @Operation(summary = "当年计划执行")
    public Response<LhBoardTargetResponse> target(@RequestParam("projectId") String projectId) {
        TenantContext.setTenantId(LhConstants.LH_TENANT_ID);
        LhBoardTargetResponse resonse  = lhBoardService.getTarget(projectId);
        return Response.success(resonse);
    }

    /**
     * 月能耗趋势
     *
     */
    @GetMapping("/trend/month")
    @Operation(summary = "月能耗趋势")
    public Response<LhBoardEnergyTrendMonthResponse> trendMonth(@RequestParam("projectId") String projectId,
                                                            @RequestParam("year") String year,
                                                            @RequestParam("month") String month,
                                                            @Parameter(description = "类型 1:用电 2:用水") @RequestParam("type") String type) {
        TenantContext.setTenantId(LhConstants.LH_TENANT_ID);
        LhBoardEnergyTrendMonthResponse resonse  = lhBoardService.getTrendMonth(projectId,year,month,type);
        return Response.success(resonse);
    }

    /**
     * 总能耗趋势
     *
     */
    @GetMapping("/trend/total")
    @Operation(summary = "总能耗趋势")
    public Response<LhBoardEnergyTrendTotalResponse> trendTotal(@RequestParam("projectId") String projectId,
                                                                @RequestParam("year") String year,
                                                                @Parameter(description = "类型 1:用电 2:用水") @RequestParam("type") String type) {
        TenantContext.setTenantId(LhConstants.LH_TENANT_ID);
        LhBoardEnergyTrendTotalResponse resonse  = lhBoardService.getTrendTotal(projectId,year,type);
        return Response.success(resonse);
    }

    /**
     * 空调单方趋势对比
     *
     */
    @GetMapping("/havc/compare")
    @Operation(summary = "空调单方趋势对比")
    public Response<LhBoardHavcEnergyCompareResponse> havcCompare(@RequestParam("projectId") String projectId,
                                                             @RequestParam("year") String year,
                                                             @Parameter(description = "类型 1:单平 2:单立") @RequestParam("type") String type) {
        TenantContext.setTenantId(LhConstants.LH_TENANT_ID);
        LhBoardHavcEnergyCompareResponse resonse  = lhBoardService.getHavcCompare(projectId,year,type);
        return Response.success(resonse);
    }

}
