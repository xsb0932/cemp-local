package com.landleaf.energy.service.impl;

import cn.hutool.core.lang.Assert;
import com.landleaf.bms.api.ProjectApi;
import com.landleaf.bms.api.dto.ProjectDetailsResponse;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.comm.exception.ServiceException;
import com.landleaf.energy.dal.mapper.ProjectCnfChargeStationMapper;
import com.landleaf.energy.dal.mapper.ProjectCnfPvMapper;
import com.landleaf.energy.dal.mapper.ProjectCnfStorageMapper;
import com.landleaf.energy.domain.entity.ProjectCnfChargeStationEntity;
import com.landleaf.energy.domain.entity.ProjectCnfPvEntity;
import com.landleaf.energy.domain.entity.ProjectCnfStorageEntity;
import com.landleaf.energy.domain.enums.EnergySubSystemEnum;
import com.landleaf.energy.domain.request.EnergySubSystemCnfRequest;
import com.landleaf.energy.domain.response.EnergySubSystemCnfResponse;
import com.landleaf.energy.service.ProjectCnfEnergySubSystemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

import static com.landleaf.energy.domain.enums.EnergySubSystemEnum.*;

/**
 * 能源子系统配置Service
 *
 * @author Tycoon
 * @since 2023/8/14 13:10
 **/
@Service
@RequiredArgsConstructor
public class ProjectCnfEnergySubSystemServiceImpl implements ProjectCnfEnergySubSystemService {

    private final ProjectCnfChargeStationMapper projectCnfChargeStationMapper;
    private final ProjectCnfStorageMapper projectCnfStorageMapper;
    private final ProjectCnfPvMapper projectCnfPvMapper;
    private final ProjectApi projectApi;

    @Override
    public void changePv(EnergySubSystemCnfRequest.Pv request) {
        Assert.isTrue(judgement(PHOTOVOLTAIC, request.getProjectBizId()), () -> new ServiceException("1", "项目不存在光伏能源子系统类型"));
        ProjectCnfPvEntity entity = projectCnfPvMapper.selectOneByProject(request.getProjectBizId());
        ProjectCnfPvEntity pvEntity = request.toEntity();
        if (Objects.isNull(entity)) {
            projectCnfPvMapper.insert(pvEntity);
        } else {
            pvEntity.setId(entity.getId());
            projectCnfPvMapper.updateById(pvEntity);
        }
    }

    @Override
    public void changeStorage(EnergySubSystemCnfRequest.Storage request) {
        Assert.isTrue(judgement(ENERGY_STORAGE, request.getProjectBizId()), () -> new ServiceException("1", "项目不存在储能能源子系统类型"));
        ProjectCnfStorageEntity entity = projectCnfStorageMapper.selectOneByProject(request.getProjectBizId());
        ProjectCnfStorageEntity storageEntity = request.toEntity();
        if (Objects.isNull(entity)) {
            projectCnfStorageMapper.insert(storageEntity);
        } else {
            storageEntity.setId(entity.getId());
            projectCnfStorageMapper.updateById(storageEntity);
        }
    }

    @Override
    public void changeChargeStation(EnergySubSystemCnfRequest.ChargeStation request) {
        Assert.isTrue(judgement(CHARGING_STATION, request.getProjectBizId()), () -> new ServiceException("1", "项目不存在充电站能源子系统类型"));
        ProjectCnfChargeStationEntity entity = projectCnfChargeStationMapper.selectOneByProject(request.getProjectBizId());
        ProjectCnfChargeStationEntity chargeStationEntity = request.toEntity();
        if (Objects.isNull(entity)) {
            projectCnfChargeStationMapper.insert(chargeStationEntity);
        } else {
            chargeStationEntity.setId(entity.getId());
            projectCnfChargeStationMapper.updateById(chargeStationEntity);
        }
    }

    @Override
    public EnergySubSystemCnfResponse configs(String projectBizId) {
        Response<ProjectDetailsResponse> response = projectApi.getProjectDetails(projectBizId);
        Assert.isTrue(response.isSuccess(), () -> new ServiceException(response.getErrorCode(), response.getMessage()));
        List<String> energySubSystem = response.getResult().getEnergySubSystem();

        boolean hasPv = energySubSystem.contains(PHOTOVOLTAIC.getCode());
        boolean hasStorage = energySubSystem.contains(ENERGY_STORAGE.getCode());
        boolean hasChargeStation = energySubSystem.contains(CHARGING_STATION.getCode());

        ProjectCnfStorageEntity storageEntity = null;
        ProjectCnfChargeStationEntity chargeStationEntity = null;
        ProjectCnfPvEntity pvEntity = null;
        if (hasPv) {
            pvEntity = projectCnfPvMapper.selectOneByProject(projectBizId);
        }
        if (hasStorage) {
            storageEntity = projectCnfStorageMapper.selectOneByProject(projectBizId);
        }
        if (hasChargeStation) {
            chargeStationEntity = projectCnfChargeStationMapper.selectOneByProject(projectBizId);
        }
        return EnergySubSystemCnfResponse.from(projectBizId, hasPv, pvEntity, hasStorage, storageEntity, hasChargeStation, chargeStationEntity);
    }

    private boolean judgement(EnergySubSystemEnum systemEnum, String projectBizId) {
        Response<ProjectDetailsResponse> response = projectApi.getProjectDetails(projectBizId);
        Assert.isTrue(response.isSuccess(), () -> new ServiceException(response.getErrorCode(), response.getMessage()));
        return response.getResult().getEnergySubSystem().contains(systemEnum.getCode());
    }

}
