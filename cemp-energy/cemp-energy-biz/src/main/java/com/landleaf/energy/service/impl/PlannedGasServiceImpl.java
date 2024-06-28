package com.landleaf.energy.service.impl;

import cn.hutool.core.lang.Assert;
import com.landleaf.comm.exception.ServiceException;
import com.landleaf.comm.tenant.TenantContext;
import com.landleaf.energy.dal.mapper.PlannedGasMapper;
import com.landleaf.energy.dal.mapper.PlannedWaterMapper;
import com.landleaf.energy.dal.mapper.ProjectMapper;
import com.landleaf.energy.dal.mapper.ProjectStaSubitemMonthMapper;
import com.landleaf.energy.domain.entity.PlannedGasEntity;
import com.landleaf.energy.domain.entity.PlannedWaterEntity;
import com.landleaf.energy.domain.request.PlanGasRequest;
import com.landleaf.energy.domain.request.PlanWaterRequest;
import com.landleaf.energy.domain.response.PlanGasTabulationResponse;
import com.landleaf.energy.domain.response.PlanWaterTabulationResponse;
import com.landleaf.energy.service.PlannedGasService;
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
 * 计划用气
 *
 * @author Tycoon
 * @since 2023/8/10 15:31
 **/
@Service
@RequiredArgsConstructor
public class PlannedGasServiceImpl implements PlannedGasService {

    private final PlannedGasMapper plannedGasMapper;
    private final ProjectStaSubitemMonthMapper projectStaSubitemMonthMapper;
    private final ProjectMapper projectMapper;

    @Override
    public void initPlanGas(PlanGasRequest.Initialize request) {
        TenantContext.setIgnore(false);
        Assert.isTrue(projectMapper.existsProjectBizId(request.getProjectBizId()), () -> new ServiceException(PROJECT_NOT_EXIST));
        List<PlannedGasEntity> gasEntities = plannedGasMapper.searchProjectYearPlans(request.getProjectBizId(), request.getYear());
        Assert.isTrue(gasEntities.isEmpty(), () -> new ServiceException(YEAR_PLANNED_GAS_EXISTED));
        List<PlannedGasEntity> entities = IntStream.range(1, 13)
                .mapToObj(it -> {
                    PlannedGasEntity entity = new PlannedGasEntity();
                    entity.setProjectBizId(request.getProjectBizId());
                    entity.setYear(request.getYear());
                    entity.setMonth(String.valueOf(it));
                    entity.setTenantId(TenantContext.getTenantId());
                    return entity;
                }).toList();
        plannedGasMapper.insertBatchSomeColumn(entities);
    }

    @Override
    public void updatePlanGas(PlanGasRequest.Change request) {
        TenantContext.setIgnore(false);
        Assert.notNull(plannedGasMapper.selectById(request.getId()), () -> new ServiceException(PLANNED_GAS_NOT_EXIST));
        PlannedGasEntity gasEntity = new PlannedGasEntity();
        gasEntity.setPlanGasConsumption(request.getPlanGasConsumption());
        gasEntity.setId(request.getId());
        plannedGasMapper.updateById(gasEntity);
    }

    @Override
    public List<PlanGasTabulationResponse> searchGasTabulation(String projectBizId, Year year) {
        TenantContext.setIgnore(false);
        String yearString = String.valueOf(year);
        String lastYearString = String.valueOf(year.minusYears(1));
        Map<String, PlannedGasEntity> yearMonthMap = plannedGasMapper.searchProjectYearPlans(projectBizId, yearString)
                .stream()
                .collect(Collectors.toMap(it -> it.getYear() + it.getMonth(), Function.identity()));
        Map<String, BigDecimal> lastYearMonthPlanMap = plannedGasMapper.searchProjectYearPlans(projectBizId, lastYearString)
                .stream()
                .collect(HashMap::new, (map, item) -> map.put(item.getYear() + item.getMonth(), item.getPlanGasConsumption()), HashMap::putAll);
        Map<String, BigDecimal> lastYearMonthMap = projectStaSubitemMonthMapper.getEleYearData(projectBizId, lastYearString)
                .stream()
                .collect(HashMap::new, (map, item) -> map.put(item.getYear() + item.getMonth(), item.getProjectGasUsageTotal()), HashMap::putAll);
        if (yearMonthMap.isEmpty()) {
            return List.of();
        }
        return IntStream.range(1, 13)
                .mapToObj(String::valueOf)
                .map(it -> {
                    PlannedGasEntity water = yearMonthMap.get(yearString + it);
                    PlanGasTabulationResponse tabulationResponse = new PlanGasTabulationResponse();
                    tabulationResponse.setId(water.getId());
                    tabulationResponse.setYear(yearString);
                    tabulationResponse.setMonth(it);
                    tabulationResponse.setPlanGasConsumption(water.getPlanGasConsumption());
                    tabulationResponse.setLastPlanGasConsumption(lastYearMonthPlanMap.get(lastYearString + it));
                    tabulationResponse.setLastGasConsumption(lastYearMonthMap.get(lastYearString + it));
                    return tabulationResponse;
                }).toList();
    }
}
