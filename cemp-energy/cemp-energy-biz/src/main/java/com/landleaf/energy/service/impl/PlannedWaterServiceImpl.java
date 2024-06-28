package com.landleaf.energy.service.impl;

import cn.hutool.core.lang.Assert;
import com.landleaf.comm.exception.ServiceException;
import com.landleaf.comm.tenant.TenantContext;
import com.landleaf.energy.dal.mapper.PlannedElectricityMapper;
import com.landleaf.energy.dal.mapper.PlannedWaterMapper;
import com.landleaf.energy.dal.mapper.ProjectMapper;
import com.landleaf.energy.dal.mapper.ProjectStaSubitemMonthMapper;
import com.landleaf.energy.domain.entity.PlannedElectricityEntity;
import com.landleaf.energy.domain.entity.PlannedWaterEntity;
import com.landleaf.energy.domain.request.PlanElectricityRequest;
import com.landleaf.energy.domain.request.PlanWaterRequest;
import com.landleaf.energy.domain.response.PlanElectricityTabulationResponse;
import com.landleaf.energy.domain.response.PlanWaterTabulationResponse;
import com.landleaf.energy.service.PlannedElectricityService;
import com.landleaf.energy.service.PlannedWaterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Year;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.landleaf.energy.domain.enums.ErrorCodeConstants.*;


/**
 * 计划用水
 *
 * @author Tycoon
 * @since 2023/8/10 15:31
 **/
@Service
@RequiredArgsConstructor
public class PlannedWaterServiceImpl implements PlannedWaterService {

    private final PlannedWaterMapper plannedWaterMapper;
    private final ProjectStaSubitemMonthMapper projectStaSubitemMonthMapper;
    private final ProjectMapper projectMapper;

    @Override
    public void initPlanWater(PlanWaterRequest.Initialize request) {
        TenantContext.setIgnore(false);
        Assert.isTrue(projectMapper.existsProjectBizId(request.getProjectBizId()), () -> new ServiceException(PROJECT_NOT_EXIST));
        List<PlannedWaterEntity> waterEntities = plannedWaterMapper.searchProjectYearPlans(request.getProjectBizId(), request.getYear());
        Assert.isTrue(waterEntities.isEmpty(), () -> new ServiceException(YEAR_PLANNED_WATER_EXISTED));
        List<PlannedWaterEntity> entities = IntStream.range(1, 13)
                .mapToObj(it -> {
                    PlannedWaterEntity entity = new PlannedWaterEntity();
                    entity.setProjectBizId(request.getProjectBizId());
                    entity.setYear(request.getYear());
                    entity.setMonth(String.valueOf(it));
                    entity.setTenantId(TenantContext.getTenantId());
                    return entity;
                }).toList();
        plannedWaterMapper.insertBatchSomeColumn(entities);
    }

    @Override
    public void updatePlanWater(PlanWaterRequest.Change request) {
        TenantContext.setIgnore(false);
        Assert.notNull(plannedWaterMapper.selectById(request.getId()), () -> new ServiceException(PLANNED_WATER_NOT_EXIST));
        PlannedWaterEntity waterEntity = new PlannedWaterEntity();
        waterEntity.setPlanWaterConsumption(request.getPlanWaterConsumption());
        waterEntity.setId(request.getId());
        plannedWaterMapper.updateById(waterEntity);
    }

    @Override
    public List<PlanWaterTabulationResponse> searchWaterTabulation(String projectBizId, Year year) {
        TenantContext.setIgnore(false);
        String yearString = String.valueOf(year);
        String lastYearString = String.valueOf(year.minusYears(1));
        Map<String, PlannedWaterEntity> yearMonthMap = plannedWaterMapper.searchProjectYearPlans(projectBizId, yearString)
                .stream()
                .collect(Collectors.toMap(it -> it.getYear() + it.getMonth(), Function.identity()));
        Map<String, BigDecimal> lastYearMonthPlanMap = plannedWaterMapper.searchProjectYearPlans(projectBizId, lastYearString)
                .stream()
                .collect(HashMap::new, (map, item) -> map.put(item.getYear() + item.getMonth(), item.getPlanWaterConsumption()), HashMap::putAll);
        Map<String, BigDecimal> lastYearMonthMap = projectStaSubitemMonthMapper.getEleYearData(projectBizId, lastYearString)
                .stream()
                .collect(HashMap::new, (map, item) -> map.put(item.getYear() + item.getMonth(), item.getProjectWaterUsageTotal()), HashMap::putAll);
        if (yearMonthMap.isEmpty()) {
            return List.of();
        }
        return IntStream.range(1, 13)
                .mapToObj(String::valueOf)
                .map(it -> {
                    PlannedWaterEntity water = yearMonthMap.get(yearString + it);
                    PlanWaterTabulationResponse tabulationResponse = new PlanWaterTabulationResponse();
                    tabulationResponse.setId(water.getId());
                    tabulationResponse.setYear(yearString);
                    tabulationResponse.setMonth(it);
                    tabulationResponse.setPlanWaterConsumption(water.getPlanWaterConsumption());
                    tabulationResponse.setLastPlanWaterConsumption(lastYearMonthPlanMap.get(lastYearString + it));
                    tabulationResponse.setLastWaterConsumption(lastYearMonthMap.get(lastYearString + it));
                    return tabulationResponse;
                }).toList();
    }
}
