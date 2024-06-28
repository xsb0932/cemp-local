package com.landleaf.energy.service;

import com.landleaf.energy.domain.request.PlanElectricityRequest;
import com.landleaf.energy.domain.request.PlanWaterRequest;
import com.landleaf.energy.domain.response.PlanElectricityTabulationResponse;
import com.landleaf.energy.domain.response.PlanWaterTabulationResponse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Year;
import java.util.List;

/**
 * 计划用水
 *
 * @author Tycoon
 * @since 2023/8/10 15:31
 **/
public interface PlannedWaterService {

    /**
     * 初始化年份计划用水
     *
     * @param request 初始化参数
     */
    void initPlanWater(@NotNull PlanWaterRequest.Initialize request);

    /**
     * 变更计划用水量
     *
     * @param request 变更参数
     */
    void updatePlanWater(@NotNull PlanWaterRequest.Change request);

    /**
     * 获取该年份的用水列表
     *
     * @param projectBizId 项目业务ID
     * @param year 年份
     * @return 结果
     */
    List<PlanWaterTabulationResponse> searchWaterTabulation(@NotBlank String projectBizId, @NotBlank Year year);

}
