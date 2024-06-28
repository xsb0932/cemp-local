package com.landleaf.lh.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.google.common.collect.Maps;
import com.landleaf.bms.api.DictApi;
import com.landleaf.bms.api.ProjectApi;
import com.landleaf.bms.api.dto.ProjectCityDTO;
import com.landleaf.bms.api.dto.TenantProjectDTO;
import com.landleaf.comm.tenant.TenantContext;
import com.landleaf.energy.api.PlanedElectricityApi;
import com.landleaf.energy.api.PlannedWaterApi;
import com.landleaf.energy.api.SubitemApi;
import com.landleaf.energy.enums.SubitemIndexEnum;
import com.landleaf.energy.request.IndexMonthOnlyBatchRequest;
import com.landleaf.energy.request.MonthPlanBatchRequest;
import com.landleaf.lh.dal.mapper.MaintenanceSheetMapper;
import com.landleaf.lh.domain.dto.EnergyEfficiencyAvgDTO;
import com.landleaf.lh.domain.response.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScreenService {
    private final ProjectApi projectApi;
    private final SubitemApi subitemApi;
    private final DictApi dictApi;
    private final PlanedElectricityApi planedElectricityApi;
    private final PlannedWaterApi plannedWaterApi;
    private final MaintenanceSheetMapper maintenanceSheetMapper;

    private List<YearMonth> getQueryMonth(YearMonth yearMonth) {
        List<YearMonth> result = new ArrayList<>();
        for (int i = 1; i <= yearMonth.getMonthValue(); i++) {
            result.add(YearMonth.of(yearMonth.getYear(), i));
        }
        return result;
    }

    private List<YearMonth> getPlanQueryMonth(YearMonth yearMonth) {
        List<YearMonth> result = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            result.add(YearMonth.of(yearMonth.getYear(), i));
        }
        return result;
    }

    private IndexMonthOnlyBatchRequest buildOverviewIndexQueryRequest(List<String> bizProjectIdList, YearMonth yearMonth) {
        return new IndexMonthOnlyBatchRequest()
                .setBizProjectIdList(bizProjectIdList)
                .setMonths(getQueryMonth(yearMonth))
                .setIndices(CollUtil.newArrayList(
                        SubitemIndexEnum.TOTAL_ELECTRICITY_CONSUMPTION_OF_ALL_LOADS,
                        SubitemIndexEnum.WATER_USAGE_TOTAL,
                        SubitemIndexEnum.CARBON_DIOXIDE_EMISSIONS
                ));
    }

    private void dealOverviewIndexData(LhOverviewResponse result, Map<String, Map<YearMonth, Map<SubitemIndexEnum, BigDecimal>>> data) {
        List<BigDecimal> electricity = new ArrayList<>();
        List<BigDecimal> water = new ArrayList<>();
        List<BigDecimal> co2 = new ArrayList<>();
        data.forEach((k, v) -> v.forEach((k1, v1) -> {
            electricity.add(v1.get(SubitemIndexEnum.TOTAL_ELECTRICITY_CONSUMPTION_OF_ALL_LOADS));
            water.add(v1.get(SubitemIndexEnum.WATER_USAGE_TOTAL));
            co2.add(v1.get(SubitemIndexEnum.CARBON_DIOXIDE_EMISSIONS));
        }));
        // 理论上没数据 和 0 是有区别的 至于要不要默认给0 到时候看产品怎么要求（取整）
        result.setElectricity(electricity.stream().filter(Objects::nonNull).reduce(BigDecimal::add)
                .map(bigDecimal -> bigDecimal.setScale(0, RoundingMode.DOWN)).orElse(null));
        result.setWater(water.stream().filter(Objects::nonNull).reduce(BigDecimal::add)
                .map(bigDecimal -> bigDecimal.setScale(0, RoundingMode.DOWN)).orElse(null));
        result.setCo2(co2.stream().filter(Objects::nonNull).reduce(BigDecimal::add)
                .map(bigDecimal -> bigDecimal.setScale(0, RoundingMode.DOWN)).orElse(null));
    }

    private void dealOverviewCityData(LhOverviewResponse result, List<ProjectCityDTO> projectCityList, List<TenantProjectDTO> projectList) {
        Map<String, BigDecimal> projectAreaMap = projectList.stream().collect(Collectors.toMap(TenantProjectDTO::getBizProjectId, TenantProjectDTO::getArea));
        HashMap<String, ProjectCityDTO> lngLatMap = projectCityList.stream().collect(HashMap::new, (map, item) -> map.put(item.getCity(), item), HashMap::putAll);
        Map<String, List<BigDecimal>> cityMap = projectCityList.stream()
                .collect(Collectors.groupingBy(ProjectCityDTO::getCity, Collectors.mapping(o -> projectAreaMap.get(o.getBizProjectId()), Collectors.toList())));
        List<LhOverviewCityResponse> cityList = new ArrayList<>();
        for (Map.Entry<String, List<BigDecimal>> entry : cityMap.entrySet()) {
            LhOverviewCityResponse data = new LhOverviewCityResponse();
            List<BigDecimal> areaList = entry.getValue();
            BigDecimal area = areaList.stream()
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            data.setName(entry.getKey());
            ProjectCityDTO cityDTO = lngLatMap.get(entry.getKey());
            List<Object> value = data.getValue();
            value.add(cityDTO.getLng());
            value.add(cityDTO.getLat());
            value.add(area);
            cityList.add(data);
        }
        result.setCityList(cityList);
    }

    private HashMap<Integer, BigDecimal> getMonthAvgMap(LocalDate start, LocalDate end) {
        return maintenanceSheetMapper.selectMonthMaintenanceAverage(start, end)
                .stream()
                .collect(HashMap::new, (map, item) -> map.put(item.getMaintenanceMonth(), item.getAvgNum()), HashMap::putAll);
    }

    private IndexMonthOnlyBatchRequest buildEnergyMonthIndexQueryRequest(List<String> bizProjectIdList, YearMonth yearMonth) {
        return new IndexMonthOnlyBatchRequest()
                .setBizProjectIdList(bizProjectIdList)
                .setMonths(getQueryMonth(yearMonth))
                .setIndices(CollUtil.newArrayList(
                        SubitemIndexEnum.TOTAL_ELECTRICITY_CONSUMPTION_OF_ALL_LOADS,
                        SubitemIndexEnum.WATER_USAGE_TOTAL
                ));
    }

    private MonthPlanBatchRequest buildMonthPlanBatchQueryRequest(List<String> bizProjectIdList, YearMonth yearMonth) {
        return new MonthPlanBatchRequest()
                .setBizProjectIdList(bizProjectIdList)
                .setMonths(getPlanQueryMonth(yearMonth));
    }

    private Map<Integer, BigDecimal> aggregatePlansByMonthHandlingNulls(Map<String, Map<YearMonth, BigDecimal>> projectPlans) {
        Map<Integer, BigDecimal> aggregatedPlans = new HashMap<>();

        for (Map.Entry<String, Map<YearMonth, BigDecimal>> projectEntry : projectPlans.entrySet()) {
            Map<YearMonth, BigDecimal> monthPlans = projectEntry.getValue();
            for (Map.Entry<YearMonth, BigDecimal> monthPlanEntry : monthPlans.entrySet()) {
                Integer month = monthPlanEntry.getKey().getMonthValue();
                BigDecimal planValue = monthPlanEntry.getValue();

                if (!aggregatedPlans.containsKey(month)) {
                    aggregatedPlans.put(month, planValue);
                    continue;
                }
                if (null == planValue) {
                    continue;
                }
                BigDecimal total = aggregatedPlans.get(month);
                if (null == total) {
                    aggregatedPlans.put(month, planValue);
                    continue;
                }
                aggregatedPlans.put(month, total.add(planValue));
            }
        }
        return aggregatedPlans;
    }

    private void dealMonthIndexMap(Map<Integer, BigDecimal> monthMap, Integer month, BigDecimal data) {
        if (!monthMap.containsKey(month)) {
            monthMap.put(month, data);
        } else {
            if (null != data) {
                BigDecimal total = monthMap.get(month);
                if (null == total) {
                    monthMap.put(month, data);
                } else {
                    monthMap.put(month, total.add(data));
                }
            }
        }
    }

    private Map<SubitemIndexEnum, Map<Integer, BigDecimal>> aggregateIndexByMonthHandlingNulls(Map<String, Map<YearMonth, Map<SubitemIndexEnum, BigDecimal>>> data) {
        Map<SubitemIndexEnum, Map<Integer, BigDecimal>> indexData = Maps.newEnumMap(SubitemIndexEnum.class);
        Map<Integer, BigDecimal> electricityMonthMap = new HashMap<>();
        Map<Integer, BigDecimal> waterMonthMap = new HashMap<>();
        indexData.put(SubitemIndexEnum.TOTAL_ELECTRICITY_CONSUMPTION_OF_ALL_LOADS, electricityMonthMap);
        indexData.put(SubitemIndexEnum.WATER_USAGE_TOTAL, waterMonthMap);

        for (Map.Entry<String, Map<YearMonth, Map<SubitemIndexEnum, BigDecimal>>> projectEntry : data.entrySet()) {
            Map<YearMonth, Map<SubitemIndexEnum, BigDecimal>> monthData = projectEntry.getValue();
            for (Map.Entry<YearMonth, Map<SubitemIndexEnum, BigDecimal>> monthDataEntry : monthData.entrySet()) {
                Integer month = monthDataEntry.getKey().getMonthValue();
                Map<SubitemIndexEnum, BigDecimal> indexValue = monthDataEntry.getValue();
                BigDecimal electricity = indexValue.get(SubitemIndexEnum.TOTAL_ELECTRICITY_CONSUMPTION_OF_ALL_LOADS);
                BigDecimal water = indexValue.get(SubitemIndexEnum.WATER_USAGE_TOTAL);
                dealMonthIndexMap(electricityMonthMap, month, electricity);
                dealMonthIndexMap(waterMonthMap, month, water);
            }
        }
        return indexData;
    }

    private void dealEnergyMonthData(LhEnergyMonthResponse result,
                                     Map<String, Map<YearMonth, Map<SubitemIndexEnum, BigDecimal>>> data,
                                     Map<String, Map<YearMonth, BigDecimal>> electricityPlan,
                                     Map<String, Map<YearMonth, BigDecimal>> waterPlan) {
        Map<SubitemIndexEnum, Map<Integer, BigDecimal>> indexMap = aggregateIndexByMonthHandlingNulls(data);
        Map<Integer, BigDecimal> electricityMonthPlan = aggregatePlansByMonthHandlingNulls(electricityPlan);
        Map<Integer, BigDecimal> waterMonthPlan = aggregatePlansByMonthHandlingNulls(waterPlan);
        for (int i = 1; i <= 12; i++) {
            BigDecimal plan1 = electricityMonthPlan.get(i);
            result.getData1().add(null == plan1 ? null : plan1.setScale(0, RoundingMode.DOWN).toString());

            BigDecimal plan3 = waterMonthPlan.get(i);
            result.getData3().add(null == plan3 ? null : plan3.setScale(0, RoundingMode.DOWN).toString());

            BigDecimal electricity = indexMap.get(SubitemIndexEnum.TOTAL_ELECTRICITY_CONSUMPTION_OF_ALL_LOADS).get(i);
            result.getData2().add(null == electricity ? null : electricity.setScale(0, RoundingMode.DOWN).toString());

            BigDecimal water = indexMap.get(SubitemIndexEnum.WATER_USAGE_TOTAL).get(i);
            result.getData4().add(null == water ? null : water.setScale(0, RoundingMode.DOWN).toString());
        }
    }

    private void dealProductionScheduleData(LhProductionScheduleResponse result,
                                            Map<String, Map<YearMonth, Map<SubitemIndexEnum, BigDecimal>>> data,
                                            Map<String, Map<YearMonth, BigDecimal>> electricityPlan,
                                            Map<String, Map<YearMonth, BigDecimal>> waterPlan) {
        Map<SubitemIndexEnum, Map<Integer, BigDecimal>> indexMap = aggregateIndexByMonthHandlingNulls(data);
        Map<Integer, BigDecimal> electricityMonthPlan = aggregatePlansByMonthHandlingNulls(electricityPlan);
        Map<Integer, BigDecimal> waterMonthPlan = aggregatePlansByMonthHandlingNulls(waterPlan);
        Optional<BigDecimal> etOpl = indexMap.get(SubitemIndexEnum.TOTAL_ELECTRICITY_CONSUMPTION_OF_ALL_LOADS)
                .values()
                .stream()
                .filter(Objects::nonNull)
                .reduce(BigDecimal::add);
        Optional<BigDecimal> epOpl = electricityMonthPlan.values()
                .stream()
                .filter(Objects::nonNull)
                .reduce(BigDecimal::add);
        Optional<BigDecimal> wtOpl = indexMap.get(SubitemIndexEnum.WATER_USAGE_TOTAL)
                .values()
                .stream()
                .filter(Objects::nonNull)
                .reduce(BigDecimal::add);
        Optional<BigDecimal> wpOpl = waterMonthPlan.values()
                .stream()
                .filter(Objects::nonNull)
                .reduce(BigDecimal::add);
        result.setElectricityPlan(epOpl.map(value -> value.setScale(0, RoundingMode.DOWN).toString()).orElse(null))
                .setElectricityTotal(etOpl.map(value -> value.setScale(0, RoundingMode.DOWN).toString()).orElse(null))
                .setWaterPlan(wpOpl.map(value -> value.setScale(0, RoundingMode.DOWN).toString()).orElse(null))
                .setWaterTotal(wtOpl.map(value -> value.setScale(0, RoundingMode.DOWN).toString()).orElse(null));
    }

    private IndexMonthOnlyBatchRequest buildEnergyEfficiencySortQueryRequest(List<String> bizProjectIdList, YearMonth yearMonth) {
        return new IndexMonthOnlyBatchRequest()
                .setBizProjectIdList(bizProjectIdList)
                .setMonths(CollUtil.newArrayList(yearMonth))
                .setIndices(CollUtil.newArrayList(
                        SubitemIndexEnum.PROJECT_ELECTRICITY_SUBHAVCENERGY_AVGSQ,
                        SubitemIndexEnum.PROJECT_ELECTRICITY_SUBHEATINGWATERENERGY_AVGCUBE
                ));
    }

    private void dealEnergyEfficiencySortData(LhEnergyEfficiencySortResponse result,
                                              YearMonth yearMonth,
                                              List<TenantProjectDTO> projectList,
                                              Map<String, Map<YearMonth, Map<SubitemIndexEnum, BigDecimal>>> data,
                                              Map<String, Map<YearMonth, Map<SubitemIndexEnum, BigDecimal>>> lastYearData) {
        YearMonth lastYear = yearMonth.minusYears(1L);
        for (TenantProjectDTO projectDTO : projectList) {
            String bizProjectId = projectDTO.getBizProjectId();
            String name = projectDTO.getName();

            Map<SubitemIndexEnum, BigDecimal> indexMap = data.get(bizProjectId).get(yearMonth);
            Map<SubitemIndexEnum, BigDecimal> lastYearIndexMap = lastYearData.get(bizProjectId).get(lastYear);
            BigDecimal e1 = indexMap.get(SubitemIndexEnum.PROJECT_ELECTRICITY_SUBHAVCENERGY_AVGSQ);
            BigDecimal e2 = lastYearIndexMap.get(SubitemIndexEnum.PROJECT_ELECTRICITY_SUBHAVCENERGY_AVGSQ);
            BigDecimal h1 = indexMap.get(SubitemIndexEnum.PROJECT_ELECTRICITY_SUBHEATINGWATERENERGY_AVGCUBE);
            BigDecimal h2 = lastYearIndexMap.get(SubitemIndexEnum.PROJECT_ELECTRICITY_SUBHEATINGWATERENERGY_AVGCUBE);

            LhEnergyEfficiencySortData e = new LhEnergyEfficiencySortData().setName(name);
            LhEnergyEfficiencySortData h = new LhEnergyEfficiencySortData().setName(name);

            e.setAvgSQ(null == e1 ? null : e1.setScale(1, RoundingMode.DOWN).toString())
                    .setSort(null == e1 ? BigDecimal.ZERO : e1.setScale(1, RoundingMode.DOWN));
            h.setAvgSQ(null == h1 ? null : h1.setScale(1, RoundingMode.DOWN).toString())
                    .setSort(null == h1 ? BigDecimal.ZERO : h1.setScale(1, RoundingMode.DOWN));

            if (null != e1 && null != e2) {
                e.setAvgSQ2(e1.subtract(e2).divide(e2, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).setScale(1, RoundingMode.DOWN).toString());
            }
            if (null != h1 && null != h2) {
                h.setAvgSQ2(h1.subtract(h2).divide(h2, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).setScale(1, RoundingMode.DOWN).toString());
            }

            result.getElectricity().add(e);
            result.getHeatingWater().add(h);
        }
        result.getElectricity().sort(Comparator.comparing(LhEnergyEfficiencySortData::getSort).reversed());
        if (result.getElectricity().size() > 6) {
            result.setElectricity(new ArrayList<>(result.getElectricity().subList(0, 6)));
        }
        result.getHeatingWater().sort(Comparator.comparing(LhEnergyEfficiencySortData::getSort).reversed());
        if (result.getHeatingWater().size() > 6) {
            result.setHeatingWater(new ArrayList<>(result.getHeatingWater().subList(0, 6)));
        }
    }

    private IndexMonthOnlyBatchRequest buildEnergyEfficiencyAvgQueryRequest(List<String> bizProjectIdList, YearMonth yearMonth) {
        return new IndexMonthOnlyBatchRequest()
                .setBizProjectIdList(bizProjectIdList)
                .setMonths(getQueryMonth(yearMonth))
                .setIndices(CollUtil.newArrayList(
                        SubitemIndexEnum.PROJECT_ELECTRICITY_SUBHAVCENERGY_AVGSQ,
                        SubitemIndexEnum.PROJECT_ELECTRICITY_SUBHEATINGWATERENERGY_AVGCUBE,
                        SubitemIndexEnum.PROJECT_ENVIRONMENT_OUTTEMP_AVG
                ));
    }

    private void dealEnergyEfficiencyMonthData(Map<Integer, EnergyEfficiencyAvgDTO> dataMap, Integer month, BigDecimal data, BigDecimal projectArea) {
        if (null != data) {
            BigDecimal total = data.multiply(projectArea);
            if (!dataMap.containsKey(month)) {
                EnergyEfficiencyAvgDTO dto = new EnergyEfficiencyAvgDTO()
                        .setTotal(total)
                        .setArea(projectArea);
                dataMap.put(month, dto);
            } else {
                EnergyEfficiencyAvgDTO dto = dataMap.get(month);
                dto.setTotal(dto.getTotal().add(total));
                dto.setArea(dto.getArea().add(projectArea));
            }
        }
    }

    private void dealEnergyEfficiencyAvgData(LhEnergyEfficiencyAvgResponse result,
                                             List<TenantProjectDTO> projectList,
                                             Map<String, Map<YearMonth, Map<SubitemIndexEnum, BigDecimal>>> data) {
        Map<SubitemIndexEnum, Map<Integer, EnergyEfficiencyAvgDTO>> indexData = Maps.newEnumMap(SubitemIndexEnum.class);
        HashMap<String, BigDecimal> projectAreaMap = projectList.stream().collect(HashMap::new, (map, item) -> map.put(item.getBizProjectId(), item.getArea()), HashMap::putAll);
        Map<Integer, EnergyEfficiencyAvgDTO> data1Map = new HashMap<>();
        Map<Integer, EnergyEfficiencyAvgDTO> data2Map = new HashMap<>();
        Map<Integer, EnergyEfficiencyAvgDTO> data3Map = new HashMap<>();
        indexData.put(SubitemIndexEnum.PROJECT_ELECTRICITY_SUBHEATINGWATERENERGY_AVGCUBE, data1Map);
        indexData.put(SubitemIndexEnum.PROJECT_ELECTRICITY_SUBHAVCENERGY_AVGSQ, data2Map);
        indexData.put(SubitemIndexEnum.PROJECT_ENVIRONMENT_OUTTEMP_AVG, data3Map);

        for (Map.Entry<String, Map<YearMonth, Map<SubitemIndexEnum, BigDecimal>>> projectEntry : data.entrySet()) {
            Map<YearMonth, Map<SubitemIndexEnum, BigDecimal>> monthData = projectEntry.getValue();
            for (Map.Entry<YearMonth, Map<SubitemIndexEnum, BigDecimal>> monthDataEntry : monthData.entrySet()) {
                Integer month = monthDataEntry.getKey().getMonthValue();
                Map<SubitemIndexEnum, BigDecimal> indexValue = monthDataEntry.getValue();
                BigDecimal data1 = indexValue.get(SubitemIndexEnum.PROJECT_ELECTRICITY_SUBHEATINGWATERENERGY_AVGCUBE);
                BigDecimal data2 = indexValue.get(SubitemIndexEnum.PROJECT_ELECTRICITY_SUBHAVCENERGY_AVGSQ);
                BigDecimal data3 = indexValue.get(SubitemIndexEnum.PROJECT_ENVIRONMENT_OUTTEMP_AVG);

                BigDecimal projectArea = projectAreaMap.get(projectEntry.getKey());
                dealEnergyEfficiencyMonthData(data1Map, month, data1, projectArea);
                dealEnergyEfficiencyMonthData(data2Map, month, data2, projectArea);

                if (null != data3) {
                    if (!data3Map.containsKey(month)) {
                        EnergyEfficiencyAvgDTO dto = new EnergyEfficiencyAvgDTO()
                                .setTotal(data3)
                                .setCount(BigDecimal.ONE);
                        data3Map.put(month, dto);
                    } else {
                        EnergyEfficiencyAvgDTO dto = data3Map.get(month);
                        dto.setTotal(dto.getTotal().add(data3));
                        dto.setCount(dto.getCount().add(BigDecimal.ONE));
                    }
                }
            }
        }

        for (int i = 1; i <= 12; i++) {
            EnergyEfficiencyAvgDTO data1 = indexData.get(SubitemIndexEnum.PROJECT_ELECTRICITY_SUBHEATINGWATERENERGY_AVGCUBE).get(i);
            EnergyEfficiencyAvgDTO data2 = indexData.get(SubitemIndexEnum.PROJECT_ELECTRICITY_SUBHAVCENERGY_AVGSQ).get(i);
            EnergyEfficiencyAvgDTO data3 = indexData.get(SubitemIndexEnum.PROJECT_ENVIRONMENT_OUTTEMP_AVG).get(i);

            // 加权平均
            result.getData1().add(null != data1 ? data1.getTotal().divide(data1.getAreaIfNull(), 1, RoundingMode.DOWN).toString() : null);
            result.getData2().add(null != data2 ? data2.getTotal().divide(data2.getAreaIfNull(), 1, RoundingMode.DOWN).toString() : null);
            // 算数平均
            result.getData3().add(null != data3 ? data3.getTotal().divide(data3.getCount(), 1, RoundingMode.DOWN).toString() : null);
        }
    }

    /**
     * 总览地图
     *
     * @param yearMonth 截止年月
     * @return LhOverviewResponse
     */
    public LhOverviewResponse overview(YearMonth yearMonth) {
        LhOverviewResponse result = new LhOverviewResponse();
        List<TenantProjectDTO> projectList = projectApi.getTenantProjects(TenantContext.getTenantId()).getCheckedData();
        if (CollUtil.isEmpty(projectList)) {
            return result;
        }
        List<String> bizProjectIdList = projectList.stream().map(TenantProjectDTO::getBizProjectId).toList();
        Map<String, Map<YearMonth, Map<SubitemIndexEnum, BigDecimal>>> data = subitemApi.batchSearchDataOnlyByMonth(buildOverviewIndexQueryRequest(bizProjectIdList, yearMonth)).getCheckedData();
        dealOverviewIndexData(result, data);

        List<ProjectCityDTO> projectCityList = projectApi.getProjectsCity(bizProjectIdList).getCheckedData();
        dealOverviewCityData(result, projectCityList, projectList);
        return result;
    }

    /**
     * 项目汇总
     *
     * @return LhProjectResponse
     */
    public LhProjectResponse project() {
        LhProjectResponse result = new LhProjectResponse();
        List<TenantProjectDTO> projectList = projectApi.getTenantProjects(TenantContext.getTenantId()).getCheckedData();
        if (CollUtil.isEmpty(projectList)) {
            return result;
        }
        HashMap<String, String> projectBizTypeMap = dictApi.getDictDataList("PROJECT_BIZ_TYPE").getCheckedData()
                .stream()
                .collect(HashMap::new, (map, item) -> map.put(item.getValue(), item.getLabel()), HashMap::putAll);
        result.setTotal(projectList.size());

        Map<String, LhProjectBizTypeAreaResponse> map = new HashMap<>();
        BigDecimal area = BigDecimal.ZERO;
        for (TenantProjectDTO o : projectList) {
            area = area.add(o.getArea());
            String bizType = o.getBizType();
            LhProjectBizTypeAreaResponse data = map.get(bizType);
            if (null == data) {
                data = new LhProjectBizTypeAreaResponse()
                        .setBizTypeName(projectBizTypeMap.getOrDefault(bizType, bizType))
                        .setArea(BigDecimal.ZERO);
                map.put(bizType, data);
            }
            data.setArea(data.getArea().add(o.getArea()));
        }
        result.setArea(area.divide(new BigDecimal("10000"), RoundingMode.HALF_UP).setScale(1, RoundingMode.HALF_UP))
                .setBizTypeArea(map.values());
        return result;
    }

    /**
     * 平均报修数量趋势
     *
     * @param yearMonth 截止年月
     * @return LhMaintenanceAverageResponse
     */
    public LhMaintenanceAverageResponse maintenanceAverage(YearMonth yearMonth) {
        LhMaintenanceAverageResponse result = new LhMaintenanceAverageResponse();
        HashMap<Integer, BigDecimal> monthAvgMap = getMonthAvgMap(
                LocalDate.of(yearMonth.getYear(), 1, 1),
                LocalDate.of(yearMonth.getYear(), yearMonth.getMonthValue(), 1)
        );
        int lastYear = yearMonth.minusYears(1L).getYear();
        HashMap<Integer, BigDecimal> lastMonthAvgMap = getMonthAvgMap(
                LocalDate.of(lastYear, 1, 1),
                LocalDate.of(lastYear, 12, 1)
        );

        for (int i = 1; i <= 12; i++) {
            BigDecimal avg = monthAvgMap.get(i);
            result.getData1().add(null == avg ? null : avg.setScale(1, RoundingMode.HALF_UP).toString());
            BigDecimal lastAvg = lastMonthAvgMap.get(i);
            if (null == avg || null == lastAvg) {
                result.getData2().add(null);
                continue;
            }
            BigDecimal ratio = avg.subtract(lastAvg)
                    .divide(lastAvg, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal(100))
                    .setScale(1, RoundingMode.HALF_UP);
            result.getData2().add(ratio.toString());
        }
        return result;
    }

    /**
     * 报修数量排名
     *
     * @param yearMonth 截止年月
     * @return List<LhMaintenanceSortResponse>
     */
    public List<LhMaintenanceSortResponse> maintenanceSort(YearMonth yearMonth) {
        return maintenanceSheetMapper.listMaintenanceSort(LocalDate.of(yearMonth.getYear(), yearMonth.getMonthValue(), 1));
    }

    /**
     * 总能耗趋势
     *
     * @param yearMonth 截止年月
     * @return LhEnergyMonthResponse
     */
    public LhEnergyMonthResponse energyMonth(YearMonth yearMonth) {
        LhEnergyMonthResponse result = new LhEnergyMonthResponse();
        List<TenantProjectDTO> projectList = projectApi.getTenantProjects(TenantContext.getTenantId()).getCheckedData();
        if (CollUtil.isEmpty(projectList)) {
            return result.empty();
        }
        List<String> bizProjectIdList = projectList.stream().map(TenantProjectDTO::getBizProjectId).toList();
        Map<String, Map<YearMonth, Map<SubitemIndexEnum, BigDecimal>>> data = subitemApi.batchSearchDataOnlyByMonth(buildEnergyMonthIndexQueryRequest(bizProjectIdList, yearMonth)).getCheckedData();
        MonthPlanBatchRequest queryRequest = buildMonthPlanBatchQueryRequest(bizProjectIdList, yearMonth);
        Map<String, Map<YearMonth, BigDecimal>> electricityPlan = planedElectricityApi.batchMonthPlan(queryRequest).getCheckedData();
        Map<String, Map<YearMonth, BigDecimal>> waterPlan = plannedWaterApi.batchMonthPlan(queryRequest).getCheckedData();
        dealEnergyMonthData(result, data, electricityPlan, waterPlan);
        return result;
    }

    /**
     * 生产进度
     *
     * @param yearMonth 截止年月
     * @return LhProductionScheduleResponse
     */
    public LhProductionScheduleResponse productionSchedule(YearMonth yearMonth) {
        LhProductionScheduleResponse result = new LhProductionScheduleResponse();
        List<TenantProjectDTO> projectList = projectApi.getTenantProjects(TenantContext.getTenantId()).getCheckedData();
        if (CollUtil.isEmpty(projectList)) {
            return result;
        }
        List<String> bizProjectIdList = projectList.stream().map(TenantProjectDTO::getBizProjectId).toList();
        Map<String, Map<YearMonth, Map<SubitemIndexEnum, BigDecimal>>> data = subitemApi.batchSearchDataOnlyByMonth(buildEnergyMonthIndexQueryRequest(bizProjectIdList, yearMonth)).getCheckedData();
        MonthPlanBatchRequest queryRequest = buildMonthPlanBatchQueryRequest(bizProjectIdList, yearMonth);
        Map<String, Map<YearMonth, BigDecimal>> electricityPlan = planedElectricityApi.batchMonthPlan(queryRequest).getCheckedData();
        Map<String, Map<YearMonth, BigDecimal>> waterPlan = plannedWaterApi.batchMonthPlan(queryRequest).getCheckedData();
        dealProductionScheduleData(result, data, electricityPlan, waterPlan);
        return result;
    }

    /**
     * 能效排名
     *
     * @param yearMonth 截止年月
     * @return LhEnergyEfficiencySortResponse
     */
    public LhEnergyEfficiencySortResponse energyEfficiencySort(YearMonth yearMonth) {
        LhEnergyEfficiencySortResponse result = new LhEnergyEfficiencySortResponse();
        List<TenantProjectDTO> projectList = projectApi.getTenantProjects(TenantContext.getTenantId()).getCheckedData();
        if (CollUtil.isEmpty(projectList)) {
            return result;
        }
        List<String> bizProjectIdList = projectList.stream().map(TenantProjectDTO::getBizProjectId).toList();
        Map<String, Map<YearMonth, Map<SubitemIndexEnum, BigDecimal>>> data = subitemApi.batchSearchDataOnlyByMonth(
                buildEnergyEfficiencySortQueryRequest(bizProjectIdList, yearMonth)).getCheckedData();
        Map<String, Map<YearMonth, Map<SubitemIndexEnum, BigDecimal>>> lastYearData = subitemApi.batchSearchDataOnlyByMonth(
                buildEnergyEfficiencySortQueryRequest(bizProjectIdList, yearMonth.minusYears(1L))).getCheckedData();
        dealEnergyEfficiencySortData(result, yearMonth, projectList, data, lastYearData);
        return result;
    }

    /**
     * 平均能效趋势
     *
     * @param yearMonth 截止年月
     * @return LhEnergyEfficiencyAvgResponse
     */
    public LhEnergyEfficiencyAvgResponse energyEfficiencyAvg(YearMonth yearMonth) {
        LhEnergyEfficiencyAvgResponse result = new LhEnergyEfficiencyAvgResponse();
        List<TenantProjectDTO> projectList = projectApi.getTenantProjects(TenantContext.getTenantId()).getCheckedData();
        if (CollUtil.isEmpty(projectList)) {
            return result;
        }
        List<String> bizProjectIdList = projectList.stream().map(TenantProjectDTO::getBizProjectId).toList();
        Map<String, Map<YearMonth, Map<SubitemIndexEnum, BigDecimal>>> data = subitemApi.batchSearchDataOnlyByMonth(
                buildEnergyEfficiencyAvgQueryRequest(bizProjectIdList, yearMonth)).getCheckedData();
        dealEnergyEfficiencyAvgData(result, projectList, data);
        return result;
    }

}
