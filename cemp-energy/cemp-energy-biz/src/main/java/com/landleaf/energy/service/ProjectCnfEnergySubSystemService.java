package com.landleaf.energy.service;

import com.landleaf.energy.domain.request.EnergySubSystemCnfRequest;
import com.landleaf.energy.domain.response.EnergySubSystemCnfResponse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 能源子系统配置service
 *
 * @author Tycoon
 * @since 2023/8/14 13:09
 **/
public interface ProjectCnfEnergySubSystemService {

    /**
     * 光伏配置变更
     *
     * @param request 参数
     */
    void changePv(@NotNull EnergySubSystemCnfRequest.Pv request);

    /**
     * 储能配置变更
     *
     * @param request 参数
     */
    void changeStorage(@NotNull EnergySubSystemCnfRequest.Storage request);

    /**
     * 充电桩配置变更
     *
     * @param request 参数
     */
    void changeChargeStation(@NotNull EnergySubSystemCnfRequest.ChargeStation request);

    /**
     * 获取能源子系统配置
     *
     * @param projectBizId 项目业务ID
     * @return  结果
     */
    EnergySubSystemCnfResponse configs(@NotBlank String projectBizId);
}
