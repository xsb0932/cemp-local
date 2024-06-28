package com.landleaf.energy.service;

import com.landleaf.energy.domain.request.PlanElectricityRequest;
import com.landleaf.energy.domain.response.PlanElectricityTabulationResponse;
import com.landleaf.job.api.dto.JobRpcRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;

/**
 * 计划用电
 *
 * @author Tycoon
 * @since 2023/8/10 15:31
 **/
public interface PlannedElectricityService {

    /**
     * 初始化年份计划用电
     *
     * @param request 初始化参数
     */
    void initPlanElectricity(@NotNull PlanElectricityRequest.Initialize request);

    /**
     * 变更计划用电量
     *
     * @param request 变更参数
     */
    void updatePlanElectricity(@NotNull PlanElectricityRequest.Change request);

    /**
     * 获取该年份的用电列表
     *
     * @param projectBizId 项目业务ID
     * @param year         年份
     * @return 结果
     */
    List<PlanElectricityTabulationResponse> searchElectricityTabulation(@NotBlank String projectBizId, @NotBlank Year year);

    void electricityReminder(LocalDateTime now, JobRpcRequest request);

    void yearReminder(LocalDateTime now, JobRpcRequest request);
}
