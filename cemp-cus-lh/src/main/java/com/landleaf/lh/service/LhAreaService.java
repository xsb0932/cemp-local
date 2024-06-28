package com.landleaf.lh.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.NumberUtil;
import com.google.common.collect.Maps;
import com.landleaf.bms.api.ManagementNodeApi;
import com.landleaf.bms.api.ProjectApi;
import com.landleaf.bms.api.dto.ProjectAreaProjectsDetailResponse;
import com.landleaf.bms.api.dto.ProjectAreaResponse;
import com.landleaf.bms.api.weather.ProjectWeatherApi;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.comm.sta.util.DateUtils;
import com.landleaf.comm.sta.util.KpiUtils;
import com.landleaf.comm.tenant.TenantContext;
import com.landleaf.comm.vo.CommonStaVO;
import com.landleaf.data.api.device.DeviceHistoryApi;
import com.landleaf.data.api.device.WeatherHistoryApi;
import com.landleaf.data.api.device.dto.WeatherHistoryDTO;
import com.landleaf.data.api.device.dto.WeatherStaQueryDTO;
import com.landleaf.energy.api.PlanedElectricityApi;
import com.landleaf.energy.api.PlannedWaterApi;
import com.landleaf.energy.api.SubitemApi;
import com.landleaf.energy.enums.SubitemIndexEnum;
import com.landleaf.energy.request.IndexMonthOnlyBatchRequest;
import com.landleaf.energy.request.IndexMonthRequest;
import com.landleaf.energy.response.PlannedAreaMonthsDataResponse;
import com.landleaf.energy.response.PlannedElectricityResponse;
import com.landleaf.energy.response.PlannedWaterResponse;
import com.landleaf.lh.dal.mapper.MaintenanceSheetMapper;
import com.landleaf.lh.domain.enums.MaintenanceTypeEnum;
import com.landleaf.lh.domain.response.*;
import com.landleaf.oauth.api.TenantApi;
import com.landleaf.oauth.api.dto.TenantInfoResponse;
import com.landleaf.oauth.api.enums.ReportingCycle;
import com.landleaf.redis.RedisUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * LhAreaService
 *
 * @author xushibai
 * @since 2024/06/12
 **/
@Service
@RequiredArgsConstructor
public class LhAreaService {

    private final WeatherHistoryApi weatherHistoryApi;
    private final PlanedElectricityApi planedElectricityApi;
    private final PlannedWaterApi plannedWaterApi;
    private final DeviceHistoryApi deviceHistoryApi;
    private final SubitemApi subitemApi;
    private final RedisUtils redisUtils;
    private final ProjectApi projectApi;
    private final ProjectWeatherApi projectWeatherApi;
    private final String[] X_LIST_YEAR = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"};
    private final String[] X_LIST_YEAR_DESC = new String[]{"1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月"};
    private final String[] X_LIST_MONTH_DESC = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30"};
    private final ManagementNodeApi managementNodeApi;
    private final MaintenanceSheetMapper maintenanceSheetMapper;
    private final TenantApi tenantApi;


    public List<LhAreaInfoResponse> getNodeList() {
        return managementNodeApi.getAreaNodes(TenantContext.getTenantId()).getResult().stream().map(a -> new LhAreaInfoResponse(a.getAreaId(), a.getAreaName())).collect(Collectors.toList());

    }

    public LhAreaProjectInfoResponse getAreaProjectInfo(String nodeId) {
        ProjectAreaResponse result = projectApi.getAreaProjectInfo(nodeId).getResult();
        return new LhAreaProjectInfoResponse(result.getProjectNum(), result.getProjectArea());
    }

    private List<String> getProjectIds(String nodeId) {
//        return projectApi.getAreaProjectIds(nodeId).getResult().stream().map(ProjectAreaProjectsDetailResponse::getProjectId).collect(Collectors.toList());
        return managementNodeApi.getAllProjectByNode(nodeId).getCheckedData();
    }

    private YearMonth getStaYearMonth() {
        TenantInfoResponse tenantInfo = tenantApi.getTenantInfo(TenantContext.getTenantId()).getResult();
        String reportCycle = tenantInfo.getReportingCycle();
        YearMonth staYM = null;
        LocalDate now = LocalDate.now();
        if (ReportingCycle.LABEL_0.getCode().equals(reportCycle)) {
            //自然月
            staYM = YearMonth.now().minusMonths(1L);
        } else {
            int i = Integer.parseInt(reportCycle);
            if (i >= 23) {
                if (now.getDayOfMonth() <= i) {
                    staYM = YearMonth.now().minusMonths(1L);
                } else {
                    staYM = YearMonth.now();
                }
            } else {
                if (now.getDayOfMonth() <= i) {
                    staYM = YearMonth.now().minusMonths(2L);
                } else {
                    staYM = YearMonth.now().minusMonths(1L);
                }
            }
        }
        return staYM;
    }

    public LhAreaOverviewResponse getOverview(String nodeId) {
        String maintenanceNum = "";
        String maintenanceYOY = "";


        //获取租户的报表统计周期->获取上一个统计月
        YearMonth staYM = this.getStaYearMonth();
//        else if(Arrays.asList(new String[]{ReportingCycle.LABEL_1.getCode(),ReportingCycle.LABEL_2.getCode(),ReportingCycle.LABEL_3.getCode(),ReportingCycle.LABEL_4.getCode(),ReportingCycle.LABEL_5.getCode(),ReportingCycle.LABEL_6.getCode()}).contains(reportCycle)){
//            //1-6
//            if (now.getDayOfMonth() > Integer.valueOf(reportCycle)){
//                staYM = YearMonth.now().minusMonths(1L);
//            }else{
//                staYM = YearMonth.now().minusMonths(2L);
//            }
//        }else{
//            //23-28
//            if (now.getDayOfMonth() > Integer.valueOf(reportCycle)){
//                staYM = YearMonth.now().minusMonths(1L);
//            }else{
//                staYM = YearMonth.now().minusMonths(2L);
//            }
//        }


        //报修记录
        BigDecimal mnum = maintenanceNum(nodeId, staYM);
        BigDecimal mnumLastYear = maintenanceNum(nodeId, staYM.minusYears(1L));
        maintenanceNum = BigDecimal.ZERO.compareTo(mnum) == 0 ? "--" : mnum.toPlainString();
        maintenanceYOY = BigDecimal.ZERO.compareTo(mnum) == 0 || BigDecimal.ZERO.compareTo(mnumLastYear) == 0 ?
                "--" : mnum.subtract(mnumLastYear).multiply(BigDecimal.valueOf(100)).divide(mnumLastYear, 1, RoundingMode.DOWN).toString();

        // 获取所有项目id
        List<String> projectIds = getProjectIds(nodeId);
        // 月度使用量查询


        BigDecimal eleTotal = BigDecimal.ZERO;
        BigDecimal waterTotal = BigDecimal.ZERO;
        IndexMonthRequest indexMonthRequest = new IndexMonthRequest();
        YearMonth[] currentYearMonth = new YearMonth[]{staYM};
        indexMonthRequest.setMonths(currentYearMonth);
        //indexMonthRequest.setProjectBizId(projectBizId);
        SubitemIndexEnum[] currentMonthIndices = new SubitemIndexEnum[]{

                // 用电量
                SubitemIndexEnum.TOTAL_ELECTRICITY_CONSUMPTION_OF_ALL_LOADS,
                // 用水量
                SubitemIndexEnum.WATER_USAGE_TOTAL,
        };
        indexMonthRequest.setIndices(currentMonthIndices);
        for (String s : projectIds) {
            indexMonthRequest.setProjectBizId(s);
            Response<Map<YearMonth, Map<SubitemIndexEnum, BigDecimal>>> mapMonthResponse = subitemApi.searchDataByMonth(indexMonthRequest);
            Map<YearMonth, Map<SubitemIndexEnum, BigDecimal>> currentMonths = mapMonthResponse.getResult();
            BigDecimal eleVal = getValue(SubitemIndexEnum.TOTAL_ELECTRICITY_CONSUMPTION_OF_ALL_LOADS, currentMonths.get(staYM));
            BigDecimal waterVal = getValue(SubitemIndexEnum.WATER_USAGE_TOTAL, currentMonths.get(staYM));
            eleTotal = NumberUtil.add(eleTotal, eleVal);
            waterTotal = NumberUtil.add(waterTotal, waterVal);
        }

        //用电
        String eleMonth = eleTotal.setScale(1, RoundingMode.DOWN).toPlainString();
        //用电计划
        BigDecimal elePlan = getElePlan(projectIds, staYM);
        String eleTotalRatio = eleTotal.compareTo(BigDecimal.ZERO) == 0 || elePlan.compareTo(BigDecimal.ZERO) == 0 ?
                "--" : eleTotal.multiply(BigDecimal.valueOf(100L)).divide(elePlan, 1, RoundingMode.DOWN).toString();
        //用水
        String waterMonth = waterTotal.setScale(1, RoundingMode.DOWN).toPlainString();
        BigDecimal waterPlan = getWaterPlan(projectIds, staYM);
        String waterTotalRatio = waterTotal.compareTo(BigDecimal.ZERO) == 0 || waterPlan.compareTo(BigDecimal.ZERO) == 0 ?
                "--" : waterTotal.multiply(BigDecimal.valueOf(100L)).divide(waterPlan, 1, RoundingMode.DOWN).toPlainString();
        ;
        //用水计划
        //气温
//        Map<String, List<WeatherHistoryDTO>> tempMap = getTmpMap(projectIds, String.valueOf(staYM.getYear()));
//        Map<String, List<WeatherHistoryDTO>> tempMapLastYear = getTmpMap(projectIds, String.valueOf(staYM.getYear() - 1));
//        CommonStaVO everHum = new CommonStaVO(
//                "平均温度",
//                Arrays.asList(X_LIST_YEAR_DESC),
//                Arrays.asList(X_LIST_YEAR).stream().map(month -> getTempAvg(tempMap, month).toPlainString()).collect(Collectors.toList()),
//                null
//        );
//        BigDecimal temEverMonth = getTempAvg(tempMap, String.valueOf(staYM.getMonthValue()));
//        BigDecimal temEverMonthLastYear = getTempAvg(tempMapLastYear, String.valueOf(staYM.getMonthValue()));

//        String tempYOY = temEverMonth.compareTo(BigDecimal.ZERO) == 0 || temEverMonthLastYear.compareTo(BigDecimal.ZERO) == 0 ? "--" : temEverMonth.subtract(temEverMonthLastYear).toPlainString();

        List<BigDecimal> tempData = projectIds.isEmpty() ? Collections.emptyList() : subitemApi.batchSearchDataOnlyByMonth(
                        new IndexMonthOnlyBatchRequest()
                                .setBizProjectIdList(projectIds)
                                .setMonths(CollUtil.newArrayList(staYM))
                                .setIndices(CollUtil.newArrayList(SubitemIndexEnum.PROJECT_ENVIRONMENT_OUTTEMP_AVG))
                ).getCheckedData()
                .values()
                .stream()
                .map(indexMap -> indexMap.get(staYM).get(SubitemIndexEnum.PROJECT_ENVIRONMENT_OUTTEMP_AVG))
                .filter(Objects::nonNull)
                .toList();
        List<BigDecimal> lastTempData = projectIds.isEmpty() ? Collections.emptyList() : subitemApi.batchSearchDataOnlyByMonth(
                        new IndexMonthOnlyBatchRequest()
                                .setBizProjectIdList(projectIds)
                                .setMonths(CollUtil.newArrayList(staYM.minusYears(1L)))
                                .setIndices(CollUtil.newArrayList(SubitemIndexEnum.PROJECT_ENVIRONMENT_OUTTEMP_AVG))
                ).getCheckedData()
                .values()
                .stream()
                .map(indexMap -> indexMap.get(staYM.minusYears(1L)).get(SubitemIndexEnum.PROJECT_ENVIRONMENT_OUTTEMP_AVG))
                .filter(Objects::nonNull)
                .toList();

        BigDecimal temEverMonth = tempData.isEmpty() ? null : tempData.stream().reduce(BigDecimal.ZERO, BigDecimal::add).divide(BigDecimal.valueOf(tempData.size()), 1, RoundingMode.DOWN);
        BigDecimal lastTemEverMonth = lastTempData.isEmpty() ? null : lastTempData.stream().reduce(BigDecimal.ZERO, BigDecimal::add).divide(BigDecimal.valueOf(lastTempData.size()), 1, RoundingMode.DOWN);

        String tempYOY = null == temEverMonth || null == lastTemEverMonth || BigDecimal.ZERO.compareTo(lastTemEverMonth) == 0 ?
                "--" : temEverMonth.subtract(lastTemEverMonth).multiply(BigDecimal.valueOf(100L)).divide(lastTemEverMonth, 1, RoundingMode.DOWN).toString();
        return new LhAreaOverviewResponse(maintenanceNum, maintenanceYOY, eleMonth, eleTotalRatio, waterMonth, waterTotalRatio, null == temEverMonth ? "--" : temEverMonth.toString(), tempYOY, String.valueOf(staYM.getYear()), String.valueOf(staYM.getMonthValue()));
    }

    private BigDecimal getElePlan(List<String> projectIds, YearMonth ym) {
        BigDecimal elePlan = BigDecimal.ZERO;
        for (String pid : projectIds) {
            List<PlannedElectricityResponse> response = planedElectricityApi.getElectricityPlanYear(pid, String.valueOf(ym.getYear()), "").getResult();
            elePlan = elePlan.add(response.stream().filter(ele -> Integer.valueOf(ele.getMonth()) <= ym.getMonthValue()).map(PlannedElectricityResponse::getPlanElectricityConsumption).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add));
        }
        return elePlan;

    }

    private BigDecimal getWaterPlan(List<String> projectIds, YearMonth ym) {
        BigDecimal waterPlan = BigDecimal.ZERO;
        for (String pid : projectIds) {
            List<PlannedWaterResponse> response = plannedWaterApi.getWaterPlanYear(pid, String.valueOf(ym.getYear()), "").getResult();
            waterPlan = waterPlan.add(response.stream().filter(ele -> Integer.valueOf(ele.getMonth()) <= ym.getMonthValue()).map(PlannedWaterResponse::getPlanWaterConsumption).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add));
        }
        return waterPlan;

    }


    /**
     * 获取上一个报表月的保修记录
     */
    private BigDecimal maintenanceNum(String nodeId, YearMonth ym) {

        Integer mnum = maintenanceSheetMapper.getMNum(nodeId, ym.getYear(), ym.getMonthValue());

        return mnum == null ? BigDecimal.ZERO : BigDecimal.valueOf(mnum);
    }

    private BigDecimal getValue(SubitemIndexEnum key, Map<SubitemIndexEnum, BigDecimal> values) {
        if (values == null) {
            return null;
        }
        return values.get(key);
    }

    public CommonStaVO getMaintenanceRatioList(String nodeId, YearMonth ym) {
        //报修类型
//        List<String> types = maintenanceSheetMapper.getAllType(nodeId, ym.getYear(), ym.getMonthValue());
        List<String> bizProjectIdList = managementNodeApi.getAllProjectByNode(nodeId).getCheckedData();
        if (bizProjectIdList.isEmpty()) {
            return new CommonStaVO("项目报修占比", Collections.emptyList(), Collections.emptyList(), null);
        }
        //报修数量
        List<LhMaintenanceGropDataResponse> typeNum = maintenanceSheetMapper.getAllTypeNum(bizProjectIdList, ym.getYear(), ym.getMonthValue());

        CommonStaVO response = new CommonStaVO(
                "项目报修占比",
                typeNum.stream().map(o -> MaintenanceTypeEnum.codeToName(o.getType())).collect(Collectors.toList()),
                typeNum.stream().map(LhMaintenanceGropDataResponse::getNum).toList(),
                null
        );
        return response;
    }

    public List<LhAreaMaintenanceOrderResponse> maintenanceOrder(String nodeId, YearMonth ym, String order) {
        TenantContext.setIgnore(true);
        List<LhAreaMaintenanceOrderResponse> response;
        List<String> bizProjectIdList = managementNodeApi.getAllProjectByNode(nodeId).getCheckedData();
        if (bizProjectIdList.isEmpty()) {
            return Collections.emptyList();
        }
        response = maintenanceSheetMapper.getMaintenanceOrder(bizProjectIdList, ym.getYear(), ym.getMonthValue());
//        Collections.sort(response, (o1, o2) -> {
//            if (StringUtils.equals("1", order)) {
//                return o1.getMaintenanceNum().compareTo(o2.getMaintenanceNum());
//            } else {
//                return o2.getMaintenanceNum().compareTo(o1.getMaintenanceNum());
//            }
//
//        });
        if (StringUtils.equals("1", order)) {
            response.sort(Comparator.comparing(LhAreaMaintenanceOrderResponse::getMaintenanceNumSort));
        } else {
            response.sort(Comparator.comparing(LhAreaMaintenanceOrderResponse::getMaintenanceNumSort).reversed());
        }
        return response;

    }

    private YearMonth[] getYM(String year) {
        return Arrays.stream(X_LIST_YEAR).map(month -> YearMonth.of(Integer.valueOf(year), Integer.valueOf(month))).collect(Collectors.toList()).toArray(new YearMonth[]{});
    }

    public List<CommonStaVO> getStaYear(String nodeId, String year, String type) {
        IndexMonthRequest indexMonthRequest = new IndexMonthRequest();
        YearMonth[] currentYearMonth = getYM(year);
        List<String> projectIds = getProjectIds(nodeId);
        indexMonthRequest.setMonths(currentYearMonth);
        SubitemIndexEnum[] currentMonthIndices = new SubitemIndexEnum[]{
                // 用电量
                SubitemIndexEnum.TOTAL_ELECTRICITY_CONSUMPTION_OF_ALL_LOADS,
                // 用水量
                SubitemIndexEnum.WATER_USAGE_TOTAL,
        };
        indexMonthRequest.setIndices(currentMonthIndices);
        List<Map<YearMonth, Map<SubitemIndexEnum, BigDecimal>>> allSubitemMonthData = new ArrayList<>();


        for (String s : projectIds) {
            indexMonthRequest.setProjectBizId(s);
            Response<Map<YearMonth, Map<SubitemIndexEnum, BigDecimal>>> mapMonthResponse = subitemApi.searchDataByMonth(indexMonthRequest);
            Map<YearMonth, Map<SubitemIndexEnum, BigDecimal>> currentMonths = mapMonthResponse.getResult();
            allSubitemMonthData.add(currentMonths);
        }

        //计划
        Map<String, List<PlannedAreaMonthsDataResponse>> areaMonthsDataResponses = new HashMap<>();
        if ("1".equals(type)) {
            areaMonthsDataResponses =
                    planedElectricityApi.getAreaMonthsData(nodeId, year).getResult().stream().collect(
                            Collectors.groupingBy(PlannedAreaMonthsDataResponse::getMonth)
                    );

        } else {
            areaMonthsDataResponses =
                    plannedWaterApi.getAreaMonthsData(nodeId, year).getResult().stream().collect(
                            Collectors.groupingBy(PlannedAreaMonthsDataResponse::getMonth)
                    );
        }

        List<CommonStaVO> response = new ArrayList<>();
        Map<String, List<PlannedAreaMonthsDataResponse>> finalAreaMonthsDataResponses = areaMonthsDataResponses;
        response.add(new CommonStaVO(
                "计划值",
                Arrays.asList(X_LIST_YEAR_DESC),
                Arrays.asList(X_LIST_YEAR).stream().map(new Function<String, String>() {
                    @Override
                    public String apply(String month) {
                        return finalAreaMonthsDataResponses.get(month) == null ? "--" :
                                (finalAreaMonthsDataResponses.get(month).isEmpty() || finalAreaMonthsDataResponses.get(month).get(0).getConsumption() == null) ? "--" :
                                        new BigDecimal(finalAreaMonthsDataResponses.get(month).get(0).getConsumption()).setScale(1, RoundingMode.DOWN).toString();
                    }
                }).collect(Collectors.toList()),
                null
        ));

        response.add(new CommonStaVO(
                "实际值",
                Arrays.asList(X_LIST_YEAR_DESC),
                Arrays.asList(X_LIST_YEAR).stream().map(new Function<String, String>() {
                    @Override
                    public String apply(String month) {
                        BigDecimal val = allSubitemMonthData.stream().map(new Function<Map<YearMonth, Map<SubitemIndexEnum, BigDecimal>>, BigDecimal>() {
                            @Override
                            public BigDecimal apply(Map<YearMonth, Map<SubitemIndexEnum, BigDecimal>> yearMonthMapMap) {
                                if ("1".equals(type)) {
                                    //用电
                                    return getValue(SubitemIndexEnum.TOTAL_ELECTRICITY_CONSUMPTION_OF_ALL_LOADS, yearMonthMapMap.get(YearMonth.of(Integer.valueOf(year), Integer.valueOf(month))));
                                } else {
                                    //用水
                                    return getValue(SubitemIndexEnum.WATER_USAGE_TOTAL, yearMonthMapMap.get(YearMonth.of(Integer.valueOf(year), Integer.valueOf(month))));
                                }
                            }
                        }).reduce(BigDecimal.ZERO, BigDecimal::add);

                        return val == null ? "--" : val.setScale(1, RoundingMode.DOWN).toPlainString();
                    }
                }).collect(Collectors.toList()),
                null
        ));

//        Map<String, List<WeatherHistoryDTO>> tempMap = getTmpMap(projectIds, year);
        Map<String, Map<YearMonth, Map<SubitemIndexEnum, BigDecimal>>> tempData = projectIds.isEmpty() ? Maps.newHashMap() : subitemApi.batchSearchDataOnlyByMonth(
                new IndexMonthOnlyBatchRequest()
                        .setBizProjectIdList(projectIds)
                        .setMonths(Arrays.asList(currentYearMonth))
                        .setIndices(CollUtil.newArrayList(SubitemIndexEnum.PROJECT_ENVIRONMENT_OUTTEMP_AVG))
        ).getCheckedData();
        Map<String, List<BigDecimal>> monthTemp = new HashMap<>();
        for (Map.Entry<String, Map<YearMonth, Map<SubitemIndexEnum, BigDecimal>>> projectIndexEntry : tempData.entrySet()) {
            for (Map.Entry<YearMonth, Map<SubitemIndexEnum, BigDecimal>> yearMonthMapEntry : projectIndexEntry.getValue().entrySet()) {
                int month = yearMonthMapEntry.getKey().getMonthValue();
                List<BigDecimal> monthTempData = monthTemp.computeIfAbsent(String.valueOf(month), k -> new ArrayList<>());
                BigDecimal temp = yearMonthMapEntry.getValue().get(SubitemIndexEnum.PROJECT_ENVIRONMENT_OUTTEMP_AVG);
                if (null != temp) {
                    monthTempData.add(temp);
                }
            }
        }

        CommonStaVO everHum = new CommonStaVO(
                "平均温度",
                Arrays.asList(X_LIST_YEAR_DESC),
//                Arrays.asList(X_LIST_YEAR).stream().map(month -> getTempAvg(tempMap, month).toPlainString()).collect(Collectors.toList()),
                Arrays.stream(X_LIST_YEAR).map(month -> {
                    List<BigDecimal> tempList = monthTemp.get(month);
                    if (CollUtil.isEmpty(tempList)) {
                        return "--";
                    } else {
                        return tempList.stream()
                                .reduce(BigDecimal.ZERO, BigDecimal::add)
                                .divide(BigDecimal.valueOf(tempList.size()), 1, RoundingMode.DOWN)
                                .toString();
                    }
                }).collect(Collectors.toList()),
                null
        );
        response.add(everHum);
        return response;
    }

    private List<YearMonth> getQueryMonth(YearMonth yearMonth) {
        List<YearMonth> result = new ArrayList<>();
        for (int i = 1; i <= yearMonth.getMonthValue(); i++) {
            result.add(YearMonth.of(yearMonth.getYear(), i));
        }
        return result;
    }

    private Map<String, List<WeatherHistoryDTO>> getTmpMap(List<String> projectIds, String year) {
        //温度
        WeatherStaQueryDTO queryDTO = new WeatherStaQueryDTO();
        List<YearMonth> queryMonths = new ArrayList<>();
        queryMonths = DateUtils.getMonthsBetween(YearMonth.of(Integer.valueOf(year), 1), YearMonth.of(Integer.valueOf(year) + 1, 1));//测试type2
        queryDTO.setYms(queryMonths);
        queryDTO.setType(2);
        List<WeatherHistoryDTO> tempResultTotal = new ArrayList<>();
        for (String projectId : projectIds) {
            queryDTO.setCityName(projectWeatherApi.getProjectWeatherName(projectId).getResult());
            List<WeatherHistoryDTO> humResult = weatherHistoryApi.getWeatherHistoryEverage(queryDTO).getResult();
            tempResultTotal.addAll(humResult);
        }
        Map<String, List<WeatherHistoryDTO>> tempMap = tempResultTotal.stream().collect(Collectors.groupingBy(WeatherHistoryDTO::getStaTime));
        return tempMap;
    }

    private BigDecimal getTempAvg(Map<String, List<WeatherHistoryDTO>> tempMap, String month) {
        return CollectionUtil.isEmpty(tempMap.get(month)) ? BigDecimal.ZERO : tempMap.get(month).stream().map(WeatherHistoryDTO::getTemperature).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add).divide(new BigDecimal(tempMap.get(month).size()), 2, RoundingMode.HALF_UP);
    }


    public List<String> showList(Collection<String> collection) {
        return collection.stream().map(s -> {
            return new BigDecimal(s).compareTo(BigDecimal.ZERO) == 0 ? "" : s;
        }).collect(Collectors.toList());
    }

    public List<CommonStaVO> getEleAirData(String nodeId, YearMonth ym, String order) {
        List<CommonStaVO> response = new ArrayList<>();
        //获取项目信息
        List<ProjectAreaProjectsDetailResponse> proAreas = projectApi.getAreaProjectIds(nodeId).getResult();
        List<String> projectIds = proAreas.stream().map(ProjectAreaProjectsDetailResponse::getProjectId).collect(Collectors.toList());
        //项目id:名称
        Map<String, String> nameMap = proAreas.stream().collect(Collectors.toMap(ProjectAreaProjectsDetailResponse::getProjectId, ProjectAreaProjectsDetailResponse::getProjectName));
        Map<String, BigDecimal> areaMap = proAreas.stream().collect(Collectors.toMap(ProjectAreaProjectsDetailResponse::getProjectId, ProjectAreaProjectsDetailResponse::getProjectArea));

        //查询项目用电并排序
        // 月度使用量查询
        IndexMonthRequest indexMonthRequest = new IndexMonthRequest();
        //YearMonth staYM = this.getStaYearMonth();
        YearMonth[] currentYearMonth = new YearMonth[]{ym};
        indexMonthRequest.setMonths(currentYearMonth);
        //indexMonthRequest.setProjectBizId(projectBizId);
        SubitemIndexEnum[] currentMonthIndices = new SubitemIndexEnum[]{
                // 空调用电
                SubitemIndexEnum.PROJECT_ELECTRICITY_SUBHAVCENERGY_AVGSQ,
        };
        indexMonthRequest.setIndices(currentMonthIndices);

        //指标数据
        List<ProjectAreaProjectsDetailResponse> kpiData = new ArrayList<>();
        List<ProjectAreaProjectsDetailResponse> kpiDataLastYear = new ArrayList<>();
        //当前指标
        for (String projectId : projectIds) {
            indexMonthRequest.setProjectBizId(projectId);
            Response<Map<YearMonth, Map<SubitemIndexEnum, BigDecimal>>> mapMonthResponse = subitemApi.searchDataByMonth(indexMonthRequest);
            Map<YearMonth, Map<SubitemIndexEnum, BigDecimal>> currentMonths = mapMonthResponse.getResult();
            BigDecimal value = getValue(SubitemIndexEnum.PROJECT_ELECTRICITY_SUBHAVCENERGY_AVGSQ, currentMonths.get(ym));
            value = value == null ? BigDecimal.ZERO : value.setScale(1, RoundingMode.DOWN);
            kpiData.add(new ProjectAreaProjectsDetailResponse(projectId, null, null, value));
        }
        //往年指标
        indexMonthRequest.setMonths(new YearMonth[]{ym.minusYears(1L)});
        for (String projectId : projectIds) {
            indexMonthRequest.setProjectBizId(projectId);
            Response<Map<YearMonth, Map<SubitemIndexEnum, BigDecimal>>> mapMonthResponse = subitemApi.searchDataByMonth(indexMonthRequest);
            Map<YearMonth, Map<SubitemIndexEnum, BigDecimal>> currentMonths = mapMonthResponse.getResult();
            BigDecimal value = getValue(SubitemIndexEnum.PROJECT_ELECTRICITY_SUBHAVCENERGY_AVGSQ, currentMonths.get(ym.minusYears(1L)));
            value = value == null ? BigDecimal.ZERO : value.setScale(1, RoundingMode.DOWN);
            kpiDataLastYear.add(new ProjectAreaProjectsDetailResponse(projectId, null, null, value));
        }

        //BigDecimal totalConsumption = kpiData.stream().map(ProjectAreaProjectsDetailResponse::getConsumption).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
        //计算单方 - 当年
//        kpiData = kpiData.stream().map(new Function<ProjectAreaProjectsDetailResponse, ProjectAreaProjectsDetailResponse>() {
//            @Override
//            public ProjectAreaProjectsDetailResponse apply(ProjectAreaProjectsDetailResponse detail) {
//                return new ProjectAreaProjectsDetailResponse(
//                        detail.getProjectId(),
//                        null,
//                        null,
//                        areaMap.get(detail.getProjectId()) == null || areaMap.get(detail.getProjectId()).compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : detail.getConsumption().divide(areaMap.get(detail.getProjectId()), RoundingMode.DOWN));
//
//            }
//        }).collect(Collectors.toList());
        //计算单方 - 往年
//        kpiDataLastYear = kpiDataLastYear.stream().map(new Function<ProjectAreaProjectsDetailResponse, ProjectAreaProjectsDetailResponse>() {
//            @Override
//            public ProjectAreaProjectsDetailResponse apply(ProjectAreaProjectsDetailResponse detail) {
//                return new ProjectAreaProjectsDetailResponse(
//                        detail.getProjectId(),
//                        null,
//                        null,
//                        areaMap.get(detail.getProjectId()) == null || areaMap.get(detail.getProjectId()).compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : detail.getConsumption().divide(areaMap.get(detail.getProjectId()), RoundingMode.DOWN));
//
//            }
//        }).collect(Collectors.toList());
        Map<String, BigDecimal> kpiDataLastYearMap = kpiDataLastYear.stream().collect(Collectors.toMap(ProjectAreaProjectsDetailResponse::getProjectId, ProjectAreaProjectsDetailResponse::getConsumption));
        //排序
//        Collections.sort(kpiData, new Comparator<ProjectAreaProjectsDetailResponse>() {
//            @Override
//            public int compare(ProjectAreaProjectsDetailResponse o1, ProjectAreaProjectsDetailResponse o2) {
//                if ("1".equals(order)) {
//                    return o1.getConsumption().compareTo(o2.getConsumption());
//                } else {
//                    return o2.getConsumption().compareTo(o1.getConsumption());
//                }
//            }
//        });
        if ("1".equals(order)) {
            kpiData.sort(Comparator.comparing(ProjectAreaProjectsDetailResponse::getConsumption));
        } else {
            kpiData.sort(Comparator.comparing(ProjectAreaProjectsDetailResponse::getConsumption).reversed());
        }

        response.add(new CommonStaVO(
                "空调单方用电",
                kpiData.stream().map(detail -> nameMap.get(detail.getProjectId())).collect(Collectors.toList()),
                kpiData.stream().map(detail -> detail.getConsumption() == null ? BigDecimal.ZERO.toPlainString() : detail.getConsumption().setScale(1, RoundingMode.DOWN).toPlainString()).collect(Collectors.toList()),
                null
        ));
        response.add(new CommonStaVO(
                "空调同期比例",
                kpiData.stream().map(detail -> nameMap.get(detail.getProjectId())).collect(Collectors.toList()),
                kpiData.stream().map(new Function<ProjectAreaProjectsDetailResponse, String>() {
                    @Override
                    public String apply(ProjectAreaProjectsDetailResponse detail) {
                        return KpiUtils.getYoy2(detail.getConsumption(), kpiDataLastYearMap.get(detail.getProjectId()));
                    }
                }).collect(Collectors.toList()),
                null
        ));

        return response;
    }

    public List<CommonStaVO> getEleWaterData(String nodeId, YearMonth ym, String order) {
        List<CommonStaVO> response = new ArrayList<>();
        //获取项目信息
        List<ProjectAreaProjectsDetailResponse> proAreas = projectApi.getAreaProjectIds(nodeId).getResult();
        List<String> projectIds = proAreas.stream().map(ProjectAreaProjectsDetailResponse::getProjectId).collect(Collectors.toList());
        //项目id:名称
        Map<String, String> nameMap = proAreas.stream().collect(Collectors.toMap(ProjectAreaProjectsDetailResponse::getProjectId, ProjectAreaProjectsDetailResponse::getProjectName));
        Map<String, BigDecimal> areaMap = proAreas.stream().collect(Collectors.toMap(ProjectAreaProjectsDetailResponse::getProjectId, ProjectAreaProjectsDetailResponse::getProjectArea));

        //查询项目用电并排序
        // 月度使用量查询
        IndexMonthRequest indexMonthRequest = new IndexMonthRequest();
        //YearMonth staYM = this.getStaYearMonth();
        YearMonth[] currentYearMonth = new YearMonth[]{ym};
        indexMonthRequest.setMonths(currentYearMonth);
        //indexMonthRequest.setProjectBizId(projectBizId);
        SubitemIndexEnum[] currentMonthIndices = new SubitemIndexEnum[]{
                // 热水单方用电
                SubitemIndexEnum.PROJECT_ELECTRICITY_SUBHEATINGWATERENERGY_AVGCUBE,
        };
        indexMonthRequest.setIndices(currentMonthIndices);

        //指标数据
        List<ProjectAreaProjectsDetailResponse> kpiData = new ArrayList<>();
        List<ProjectAreaProjectsDetailResponse> kpiDataLastYear = new ArrayList<>();
        //当前指标
        for (String projectId : projectIds) {
            indexMonthRequest.setProjectBizId(projectId);
            Response<Map<YearMonth, Map<SubitemIndexEnum, BigDecimal>>> mapMonthResponse = subitemApi.searchDataByMonth(indexMonthRequest);
            Map<YearMonth, Map<SubitemIndexEnum, BigDecimal>> currentMonths = mapMonthResponse.getResult();
            BigDecimal value = getValue(SubitemIndexEnum.PROJECT_ELECTRICITY_SUBHEATINGWATERENERGY_AVGCUBE, currentMonths.get(ym));
            value = value == null ? BigDecimal.ZERO : value.setScale(1, RoundingMode.DOWN);
            kpiData.add(new ProjectAreaProjectsDetailResponse(projectId, null, null, value));
        }
        //往年指标
        indexMonthRequest.setMonths(new YearMonth[]{ym.minusYears(1L)});
        for (String projectId : projectIds) {
            indexMonthRequest.setProjectBizId(projectId);
            Response<Map<YearMonth, Map<SubitemIndexEnum, BigDecimal>>> mapMonthResponse = subitemApi.searchDataByMonth(indexMonthRequest);
            Map<YearMonth, Map<SubitemIndexEnum, BigDecimal>> currentMonths = mapMonthResponse.getResult();
            BigDecimal value = getValue(SubitemIndexEnum.PROJECT_ELECTRICITY_SUBHEATINGWATERENERGY_AVGCUBE, currentMonths.get(ym.minusYears(1L)));
            value = value == null ? BigDecimal.ZERO : value.setScale(1, RoundingMode.DOWN);
            kpiDataLastYear.add(new ProjectAreaProjectsDetailResponse(projectId, null, null, value));
        }
        //计算单方 - 当年
//        kpiData = kpiData.stream().map(new Function<ProjectAreaProjectsDetailResponse, ProjectAreaProjectsDetailResponse>() {
//            @Override
//            public ProjectAreaProjectsDetailResponse apply(ProjectAreaProjectsDetailResponse detail) {
//                return new ProjectAreaProjectsDetailResponse(
//                        detail.getProjectId(),
//                        null,
//                        null,
//                        areaMap.get(detail.getProjectId()) == null || areaMap.get(detail.getProjectId()).compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : detail.getConsumption().divide(areaMap.get(detail.getProjectId()), RoundingMode.DOWN));
//
//            }
//        }).collect(Collectors.toList());
        //计算单方 - 往年
//        kpiDataLastYear = kpiDataLastYear.stream().map(new Function<ProjectAreaProjectsDetailResponse, ProjectAreaProjectsDetailResponse>() {
//            @Override
//            public ProjectAreaProjectsDetailResponse apply(ProjectAreaProjectsDetailResponse detail) {
//                return new ProjectAreaProjectsDetailResponse(
//                        detail.getProjectId(),
//                        null,
//                        null,
//                        areaMap.get(detail.getProjectId()) == null || areaMap.get(detail.getProjectId()).compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : detail.getConsumption().divide(areaMap.get(detail.getProjectId()), RoundingMode.DOWN));
//
//            }
//        }).collect(Collectors.toList());

        Map<String, BigDecimal> kpiDataLastYearMap = kpiDataLastYear.stream().collect(Collectors.toMap(ProjectAreaProjectsDetailResponse::getProjectId, ProjectAreaProjectsDetailResponse::getConsumption));
        //排序
//        Collections.sort(kpiData, new Comparator<ProjectAreaProjectsDetailResponse>() {
//            @Override
//            public int compare(ProjectAreaProjectsDetailResponse o1, ProjectAreaProjectsDetailResponse o2) {
//                if ("1".equals(order)) {
//                    return o1.getConsumption().compareTo(o2.getConsumption());
//                } else {
//                    return o2.getConsumption().compareTo(o1.getConsumption());
//                }
//            }
//        });
        if ("1".equals(order)) {
            kpiData.sort(Comparator.comparing(ProjectAreaProjectsDetailResponse::getConsumption));
        } else {
            kpiData.sort(Comparator.comparing(ProjectAreaProjectsDetailResponse::getConsumption).reversed());
        }

        response.add(new CommonStaVO(
                "热水单方用电",
                kpiData.stream().map(detail -> nameMap.get(detail.getProjectId())).collect(Collectors.toList()),
                kpiData.stream().map(detail -> detail.getConsumption() == null ? BigDecimal.ZERO.toPlainString() : detail.getConsumption().setScale(1, RoundingMode.DOWN).toPlainString()).collect(Collectors.toList()),
                null
        ));
        response.add(new CommonStaVO(
                "热水同期比例",
                kpiData.stream().map(detail -> nameMap.get(detail.getProjectId())).collect(Collectors.toList()),
                kpiData.stream().map(new Function<ProjectAreaProjectsDetailResponse, String>() {
                    @Override
                    public String apply(ProjectAreaProjectsDetailResponse detail) {
                        return KpiUtils.getYoy2(detail.getConsumption(), kpiDataLastYearMap.get(detail.getProjectId()));
                    }
                }).collect(Collectors.toList()),
                null
        ));

        return response;
    }
}
