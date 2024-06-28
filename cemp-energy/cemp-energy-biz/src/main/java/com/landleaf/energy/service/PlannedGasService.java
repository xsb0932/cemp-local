package com.landleaf.energy.service;

import com.landleaf.energy.domain.request.PlanElectricityRequest;
import com.landleaf.energy.domain.request.PlanGasRequest;
import com.landleaf.energy.domain.response.PlanElectricityTabulationResponse;
import com.landleaf.energy.domain.response.PlanGasTabulationResponse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Year;
import java.util.List;

/**
 * 计划用气
 *
 * @author Tycoon
 * @since 2023/8/10 15:31
 **/
public interface PlannedGasService {

    /**
     * 初始化年份计划用气
     *
     * @param request 初始化参数
     */
    void initPlanGas(@NotNull PlanGasRequest.Initialize request);

    /**
     * 变更计划用气量
     *
     * @param request 变更参数
     */
    void updatePlanGas(@NotNull PlanGasRequest.Change request);

    /**
     * 获取该年份的用气列表
     *
     * @param projectBizId 项目业务ID
     * @param year 年份
     * @return 结果
     */
    List<PlanGasTabulationResponse> searchGasTabulation(@NotBlank String projectBizId, @NotBlank Year year);

}
