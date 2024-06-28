package com.landleaf.energy.controller;

import com.landleaf.comm.base.pojo.Response;
import com.landleaf.energy.domain.request.EnergySubSystemCnfRequest;
import com.landleaf.energy.domain.response.EnergySubSystemCnfResponse;
import com.landleaf.energy.service.ProjectCnfEnergySubSystemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 能源子系统配置接口
 *
 * @author Tycoon
 * @since 2023/8/11 17:07
 **/
@RestController
@RequiredArgsConstructor
@RequestMapping("/project-cnf-energy-sub-system")
@Tag(name = "能源子系统配置接口", description = "能源子系统配置接口")
public class ProjectCnfEnergySubSystemController {

    private final ProjectCnfEnergySubSystemService projectCnfEnergySubSystemService;

    /**
     * 光伏配置变更
     * @param request 参数
     * @return  结果
     */
    @Operation(summary = "光伏配置变更")
    @PostMapping("/change/pv")
    public Response<Void> changePv(@Validated @RequestBody EnergySubSystemCnfRequest.Pv request) {
        projectCnfEnergySubSystemService.changePv(request);
        return Response.success();
    }

    /**
     * 储能配置变更
     * @param request 参数
     * @return  结果
     */
    @Operation(summary = "储能配置变更")
    @PostMapping("/change/storage")
    public Response<Void> changeStorage(@Validated @RequestBody EnergySubSystemCnfRequest.Storage request) {
        projectCnfEnergySubSystemService.changeStorage(request);
        return Response.success();
    }

    /**
     * 充电桩配置变更
     * @param request 参数
     * @return  结果
     */
    @Operation(summary = "充电桩配置变更")
    @PostMapping("/change/chargeStation")
    public Response<Void> changeChargeStation(@Validated @RequestBody EnergySubSystemCnfRequest.ChargeStation request) {
        projectCnfEnergySubSystemService.changeChargeStation(request);
        return Response.success();
    }

    /**
     * 获取能源子系统配置
     *
     * @param projectBizId 项目业务ID
     * @return  结果
     */
    @Parameter(name = "projectBizId", description = "项目业务ID", in = ParameterIn.PATH, required = true)
    @Operation(summary = "获取能源子系统配置")
    @GetMapping("/configs/{projectBizId}")
    public Response<EnergySubSystemCnfResponse> configs(@PathVariable("projectBizId") String projectBizId) {
        return Response.success(projectCnfEnergySubSystemService.configs(projectBizId));
    }
    
}
