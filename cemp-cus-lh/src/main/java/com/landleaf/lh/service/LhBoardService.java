package com.landleaf.lh.service;

import cn.hutool.core.util.NumberUtil;
import com.alibaba.fastjson2.JSONObject;
import com.landleaf.bms.api.ProjectApi;
import com.landleaf.bms.api.dto.ProjectDetailsResponse;
import com.landleaf.bms.api.weather.ProjectWeatherApi;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.comm.sta.util.DateUtils;
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
import com.landleaf.energy.request.IndexDayRequest;
import com.landleaf.energy.request.IndexMonthRequest;
import com.landleaf.energy.request.IndexYearRequest;
import com.landleaf.energy.response.PlannedElectricityResponse;
import com.landleaf.energy.response.PlannedWaterResponse;
import com.landleaf.lh.domain.response.*;
import com.landleaf.redis.RedisUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * LhBoardService
 *
 * @author xushibai
 * @since 2024/01/26
 **/
@Service
@RequiredArgsConstructor
public class LhBoardService {

    private final WeatherHistoryApi weatherHistoryApi;
    private final PlanedElectricityApi planedElectricityApi;
    private final PlannedWaterApi plannedWaterApi;
    private final DeviceHistoryApi deviceHistoryApi;
    private final SubitemApi subitemApi;
    private final RedisUtils redisUtils;
    private final ProjectApi projectApi;
    private final ProjectWeatherApi projectWeatherApi;
    private final String[] X_LIST_YEAR_DESC = new String[]{"1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月"};
    private final String[] X_LIST_MONTH_DESC = new String[]{"1","2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12","13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23","24","25","26","27","28","29","30"};


    private BigDecimal getValue(SubitemIndexEnum key, Map<SubitemIndexEnum, BigDecimal> values) {
        if (values == null) {
            return null;
        }
        return values.get(key);
    }

    private YearMonth[] getCurrentYearMonth(YearMonth now) {
        List<YearMonth> yearMonths = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            yearMonths.add(YearMonth.of(now.getYear(), i));
        }
        return yearMonths.toArray(new YearMonth[0]);
    }
    private String getPropertyValue(Map<Object, Object> data, String code){
        if(data.containsKey("propertys")){
            JSONObject obj = (JSONObject)data.get("propertys");
            if(obj.containsKey(code)){
                return obj.getJSONObject(code).getString("val");
            }
        }
        return "0";
    }


    public LhBoardTitleResponse getTitle(String projectId) {
        ProjectDetailsResponse detail = projectApi.getProjectDetails(projectId).getResult();
        BigDecimal area = detail.getArea();

        // 当年使用量查询
        IndexYearRequest indexYearRequest = new IndexYearRequest();
        indexYearRequest.setYears(new Year[]{Year.now()});
        indexYearRequest.setProjectBizId(projectId);
        SubitemIndexEnum[] currentYearIndices = new SubitemIndexEnum[]{
                // 空调用电量
                SubitemIndexEnum.WARM_UNIVERSAL_POWER,
                // 热水用电量
                SubitemIndexEnum.ENERGY_HEATWATER_TOTAL,
                // 用水量
                SubitemIndexEnum.WATER_USAGE_TOTAL,
                // 热水补水
                SubitemIndexEnum.PROJECT_WATER_HEATINGWATERUSAGE_TOTAL,
                // 总用电量
                SubitemIndexEnum.TOTAL_ELECTRICITY_CONSUMPTION_OF_ALL_LOADS,
        };
        indexYearRequest.setIndices(currentYearIndices);
        Response<Map<Year, Map<SubitemIndexEnum, BigDecimal>>> mapYearResponse = subitemApi.searchDataByYear(indexYearRequest);
        Map<SubitemIndexEnum, BigDecimal> currentYear = mapYearResponse.getResult().get(Year.now());

        // 月度使用量查询
        IndexMonthRequest indexMonthRequest = new IndexMonthRequest();
        YearMonth yearMonth = YearMonth.now();
        YearMonth[] currentYearMonth = getCurrentYearMonth(yearMonth);
        indexMonthRequest.setMonths(currentYearMonth);
        indexMonthRequest.setProjectBizId(projectId);
        SubitemIndexEnum[] currentMonthIndices = new SubitemIndexEnum[]{
                // 热水用电量
                SubitemIndexEnum.ENERGY_HEATWATER_TOTAL,
                // 用水量
                SubitemIndexEnum.WATER_USAGE_TOTAL,
        };
        indexMonthRequest.setIndices(currentMonthIndices);
        Response<Map<YearMonth, Map<SubitemIndexEnum, BigDecimal>>> mapMonthResponse = subitemApi.searchDataByMonth(indexMonthRequest);
        Map<YearMonth, Map<SubitemIndexEnum, BigDecimal>> currentMonths = mapMonthResponse.getResult();

        LhBoardTitleResponse response = new LhBoardTitleResponse();
        response.setHvacEleEnergyDensityYear(NumberUtil.div(getValue(SubitemIndexEnum.WARM_UNIVERSAL_POWER, currentYear),area,2,RoundingMode.HALF_UP));
        response.setHvacEleEnergyYear(getValue(SubitemIndexEnum.WARM_UNIVERSAL_POWER, currentYear));
        response.setWaterYear(getValue(SubitemIndexEnum.WATER_USAGE_TOTAL, currentYear));
        //热水补水
        BigDecimal wht = getValue(SubitemIndexEnum.PROJECT_WATER_HEATINGWATERUSAGE_TOTAL, currentYear);
        if(wht != null && wht.compareTo(BigDecimal.ZERO)>0){
            response.setWaterEleEnergyDesityMonth(NumberUtil.div(getValue(SubitemIndexEnum.ENERGY_HEATWATER_TOTAL, currentYear),wht,2,RoundingMode.HALF_UP));
        }
        response.setWaterEleEnergyYear(getValue(SubitemIndexEnum.ENERGY_HEATWATER_TOTAL, currentYear));
        response.setWaterMonth(getValue(SubitemIndexEnum.WATER_USAGE_TOTAL, currentMonths.get(YearMonth.now())));
        response.setEleEnergyYear(getValue(SubitemIndexEnum.TOTAL_ELECTRICITY_CONSUMPTION_OF_ALL_LOADS, currentYear));
        //计划值
        Response<List<PlannedElectricityResponse>> eleResponse = planedElectricityApi.getElectricityPlanYear(projectId,String.valueOf(Year.now().getValue()),"");
        BigDecimal elePlan = BigDecimal.ZERO;
        response.setUseRatio(BigDecimal.ZERO);
        if(eleResponse != null){
            elePlan = eleResponse.getResult().stream().map(PlannedElectricityResponse::getPlanElectricityConsumption).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
        }
        if (elePlan != null && elePlan.compareTo(BigDecimal.ZERO) > 0){
            response.setUseRatio(NumberUtil.div(response.getEleEnergyYear().multiply(BigDecimal.valueOf(100)),elePlan,2, RoundingMode.HALF_UP));
        }
        return response;
    }

    public LhBoardTargetResponse getTarget(String projectId) {
        // 当年使用量查询
        IndexYearRequest indexYearRequest = new IndexYearRequest();
        indexYearRequest.setYears(new Year[]{Year.now()});
        indexYearRequest.setProjectBizId(projectId);
        SubitemIndexEnum[] currentYearIndices = new SubitemIndexEnum[]{
                // 用水量
                SubitemIndexEnum.WATER_USAGE_TOTAL,
                // 总用电量
                SubitemIndexEnum.TOTAL_ELECTRICITY_CONSUMPTION_OF_ALL_LOADS,
        };
        indexYearRequest.setIndices(currentYearIndices);
        Response<Map<Year, Map<SubitemIndexEnum, BigDecimal>>> mapYearResponse = subitemApi.searchDataByYear(indexYearRequest);
        Map<SubitemIndexEnum, BigDecimal> currentYear = mapYearResponse.getResult().get(Year.now());

        LhBoardTargetResponse response = new LhBoardTargetResponse();
        response.setEleEnergy(getValue(SubitemIndexEnum.TOTAL_ELECTRICITY_CONSUMPTION_OF_ALL_LOADS, currentYear));
        Response<List<PlannedElectricityResponse>> eleResponse = planedElectricityApi.getElectricityPlanYear(projectId,String.valueOf(Year.now().getValue()),"");
        if(eleResponse != null){
            response.setEleEnergyTarget(eleResponse.getResult().stream().map(PlannedElectricityResponse::getPlanElectricityConsumption).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add));
        }else {
            response.setEleEnergyTarget(null);
        }
        response.setWaterUse(getValue(SubitemIndexEnum.WATER_USAGE_TOTAL, currentYear));
        Response<List<PlannedWaterResponse>> waterResonse = plannedWaterApi.getWaterPlanYear(projectId,String.valueOf(Year.now().getValue()),"");
        if(waterResonse != null){
            response.setWaterUseTarget(waterResonse.getResult().stream().map(PlannedWaterResponse::getPlanWaterConsumption).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add));
        }else{
            response.setWaterUseTarget(null);
        }
        return response;
    }

    private List<String> getRandomListMonth(){
        List<String> randomList = new ArrayList<>();
        Random random = new Random();
        int randomNumber = random.nextInt(100) + 1;
        for (int i = 0; i < 30; i++) {
            randomList.add(String.valueOf(random.nextInt(100) + 1));
        }
        return randomList;
    }

    private List<String> getRandomListYear(){
        List<String> randomList = new ArrayList<>();
        Random random = new Random();
        int randomNumber = random.nextInt(100) + 1;
        for (int i = 0; i < 12; i++) {
            randomList.add(String.valueOf(random.nextInt(100) + 1));
        }
        return randomList;
    }

    private List<String> getEveListMonth(){
        List<String> eveList = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < 30; i++) {
            eveList.add("40");
        }
        return eveList;
    }

    private List<String> getEveListYear(){
        List<String> eveList = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < 12; i++) {
            eveList.add("40");
        }
        return eveList;
    }


    public LhBoardEnergyTrendMonthResponse getTrendMonth(String projectId,String year,String month,String type) {

        // 日使用量查询
        IndexDayRequest indexDayRequest = new IndexDayRequest();
        LocalDate begin = LocalDate.of(Integer.valueOf(year),Integer.valueOf(month),1);
        List<LocalDate> currentMonthList = DateUtils.getDaysBetween(begin,begin.plusMonths(1));
        LocalDate[] currentMonthDay = currentMonthList.toArray(new LocalDate[]{});
        indexDayRequest.setDays(currentMonthDay);
        indexDayRequest.setProjectBizId(projectId);
        SubitemIndexEnum[] currentDayIndices = new SubitemIndexEnum[]{
                // 总用电量
                SubitemIndexEnum.TOTAL_ELECTRICITY_CONSUMPTION_OF_ALL_LOADS,
                SubitemIndexEnum.WATER_USAGE_TOTAL,
        };
        indexDayRequest.setIndices(currentDayIndices);
        Response<Map<LocalDate, Map<SubitemIndexEnum, BigDecimal>>> mapDayResponse = subitemApi.searchDataByDay(indexDayRequest);
        Map<LocalDate, Map<SubitemIndexEnum, BigDecimal>> currentDays = mapDayResponse.getResult();

        //温度
        WeatherStaQueryDTO queryDTO = new WeatherStaQueryDTO();
        List<YearMonth> queryMonths = new ArrayList<>();
        queryMonths.add(YearMonth.of(Integer.valueOf(year),Integer.valueOf(month)));
        queryDTO.setYms(queryMonths);
        queryDTO.setCityName(projectWeatherApi.getProjectWeatherName(projectId).getResult());
        queryDTO.setType(1);
        List<WeatherHistoryDTO> humResult = weatherHistoryApi.getWeatherHistoryEverage(queryDTO).getResult();

        LhBoardEnergyTrendMonthResponse response = new LhBoardEnergyTrendMonthResponse();
        List<CommonStaVO> list =new ArrayList<>();
        if("1".equals(type)){       //总用电
            List<PlannedElectricityResponse> elePlan = planedElectricityApi.getElectricityPlanYear(projectId,year,month.replaceFirst("^0*", "")).getResult();
            CommonStaVO eleEnergy = new CommonStaVO(
                    "总电量",
                    currentMonthList.stream().map(LocalDate::getDayOfMonth).map(String::valueOf).collect(Collectors.toList()),
                    showList(currentMonthList.stream().map(date -> getValue(SubitemIndexEnum.TOTAL_ELECTRICITY_CONSUMPTION_OF_ALL_LOADS, currentDays.get(date)).toPlainString()).collect(Collectors.toList())),
                    null
            );
            CommonStaVO hum = new CommonStaVO(
                    "温度",
                    humResult.stream().map(his -> his.getStaTime()).collect(Collectors.toList()),
                    showList(humResult.stream().map(his -> his.getTemperature().toPlainString()).collect(Collectors.toList())),
                    null
            );
            BigDecimal everElePlan = NumberUtil.div(elePlan.stream().map(PlannedElectricityResponse::getPlanElectricityConsumption).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add),new BigDecimal(currentMonthList.size()),2,RoundingMode.HALF_UP);
            CommonStaVO everTarget = new CommonStaVO(
                    "平均计划值",
                    currentMonthList.stream().map(LocalDate::getDayOfMonth).map(String::valueOf).collect(Collectors.toList()),
                    showList(currentMonthList.stream().map(date -> everElePlan.toPlainString()).collect(Collectors.toList())),
                    null
            );
            list.add(eleEnergy);
            list.add(hum);
            list.add(everTarget);


        }else if("2".equals(type)){     //总用水
            List<PlannedWaterResponse> waterPlan = plannedWaterApi.getWaterPlanYear(projectId,year,month.replaceFirst("^0*", "")).getResult();
            CommonStaVO waterUse = new CommonStaVO(
                    "用水量",
                    currentMonthList.stream().map(LocalDate::getDayOfMonth).map(String::valueOf).collect(Collectors.toList()),
                    showList(currentMonthList.stream().map(date -> getValue(SubitemIndexEnum.WATER_USAGE_TOTAL, currentDays.get(date)).toPlainString()).collect(Collectors.toList())),
                    null
            );
            CommonStaVO hum = new CommonStaVO(
                    "温度",
                    humResult.stream().map(his -> his.getStaTime()).collect(Collectors.toList()),
                    showList(humResult.stream().map(his -> his.getTemperature().toPlainString()).collect(Collectors.toList())),
                    null
            );
            BigDecimal everWaterPlan = NumberUtil.div(waterPlan.stream().map(PlannedWaterResponse::getPlanWaterConsumption).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add),new BigDecimal(currentMonthList.size()),2,RoundingMode.HALF_UP);
            CommonStaVO everTarget = new CommonStaVO(
                    "平均计划值",
                    currentMonthList.stream().map(LocalDate::getDayOfMonth).map(String::valueOf).collect(Collectors.toList()),
                    showList(currentMonthList.stream().map(date -> everWaterPlan.toPlainString()).collect(Collectors.toList())),
                    null
            );
            list.add(waterUse);
            list.add(hum);
            list.add(everTarget);
        }
        response.setBarChartData(list);
        return response;

    }

    public LhBoardEnergyTrendTotalResponse getTrendTotal(String projectId,String year,String type) {
        // 月度使用量查询
        IndexMonthRequest indexMonthRequest = new IndexMonthRequest();
        YearMonth ym = YearMonth.of(Integer.valueOf(year),1);
        List<YearMonth> ymList =  DateUtils.getMonthsBetween(ym,ym.plusYears(1));
        YearMonth[] currentYearMonth = ymList.toArray(new YearMonth[]{});
        indexMonthRequest.setMonths(currentYearMonth);
        indexMonthRequest.setProjectBizId(projectId);
        SubitemIndexEnum[] currentMonthIndices = new SubitemIndexEnum[]{
                SubitemIndexEnum.TOTAL_ELECTRICITY_CONSUMPTION_OF_ALL_LOADS,
                SubitemIndexEnum.WATER_USAGE_TOTAL,
        };
        indexMonthRequest.setIndices(currentMonthIndices);
        Response<Map<YearMonth, Map<SubitemIndexEnum, BigDecimal>>> mapMonthResponse = subitemApi.searchDataByMonth(indexMonthRequest);
        Map<YearMonth, Map<SubitemIndexEnum, BigDecimal>> currentMonths = mapMonthResponse.getResult();

        //温度
        WeatherStaQueryDTO queryDTO = new WeatherStaQueryDTO();
        List<YearMonth> queryMonths = new ArrayList<>();
        queryMonths = DateUtils.getMonthsBetween(YearMonth.of(Integer.valueOf(year),1),YearMonth.of(Integer.valueOf(year)+1,1));//测试type2
        queryDTO.setYms(queryMonths);
        queryDTO.setCityName(projectWeatherApi.getProjectWeatherName(projectId).getResult());
        queryDTO.setType(2);
        List<WeatherHistoryDTO> humResult = weatherHistoryApi.getWeatherHistoryEverage(queryDTO).getResult();

        LhBoardEnergyTrendTotalResponse response = new LhBoardEnergyTrendTotalResponse();
        List<CommonStaVO> list =new ArrayList<>();
        if("1".equals(type)){       //总用电
            List<PlannedElectricityResponse> elePlanList = planedElectricityApi.getElectricityPlanYear(projectId,year,"").getResult();
            CommonStaVO eleEnergyTarget = new CommonStaVO(
                    "计划值",
                    ymList.stream().map( ymonth -> String.valueOf(ymonth.getMonthValue()).concat("月")).collect(Collectors.toList()),
                    showList(elePlanList.stream().map(plannedElectricityResponse -> (plannedElectricityResponse.getPlanElectricityConsumption() == null ? BigDecimal.ZERO :plannedElectricityResponse.getPlanElectricityConsumption()).toPlainString()).collect(Collectors.toList())),
                    null
            );
            CommonStaVO eleEnergyAct = new CommonStaVO(
                    "实际值",
                    ymList.stream().map(ymonth -> String.valueOf(ymonth.getMonthValue()).concat("月")).collect(Collectors.toList()),
                    showList(currentMonths.entrySet().stream().map(new Function<Map.Entry<YearMonth, Map<SubitemIndexEnum, BigDecimal>>, String>() {
                        @Override
                        public String apply(Map.Entry<YearMonth, Map<SubitemIndexEnum, BigDecimal>> yearMonthMapEntry) {
                            return getValue(SubitemIndexEnum.TOTAL_ELECTRICITY_CONSUMPTION_OF_ALL_LOADS, currentMonths.get(yearMonthMapEntry.getKey())).toPlainString();
                        }
                    }).collect(Collectors.toList())),
                    null
            );
            CommonStaVO everHum = new CommonStaVO(
                    "平均温度",
                    humResult.stream().map(his -> his.getStaTime().concat("月")).collect(Collectors.toList()),
                    showList(humResult.stream().map(his -> his.getTemperature().toPlainString()).collect(Collectors.toList())),
                    null
            );
            list.add(eleEnergyTarget);
            list.add(eleEnergyAct);
            list.add(everHum);


        }else if("2".equals(type)){     //总用水
            List<PlannedWaterResponse> waterPlanList = plannedWaterApi.getWaterPlanYear(projectId,year,"").getResult();
            CommonStaVO waterUseTarget = new CommonStaVO(
                    "计划值",
                    ymList.stream().map(ymonth -> String.valueOf(ymonth.getMonthValue()).concat("月")).collect(Collectors.toList()),
                    showList(waterPlanList.stream().map(plannedWaterResponse -> (plannedWaterResponse.getPlanWaterConsumption() == null ? BigDecimal.ZERO: plannedWaterResponse.getPlanWaterConsumption()).toPlainString()).collect(Collectors.toList())),
                    null
            );
            CommonStaVO waterUseAct = new CommonStaVO(
                    "实际值",
                    ymList.stream().map(ymonth -> String.valueOf(ymonth.getMonthValue()).concat("月")).collect(Collectors.toList()),
                    showList(currentMonths.entrySet().stream().map(new Function<Map.Entry<YearMonth, Map<SubitemIndexEnum, BigDecimal>>, String>() {
                        @Override
                        public String apply(Map.Entry<YearMonth, Map<SubitemIndexEnum, BigDecimal>> yearMonthMapEntry) {
                            return getValue(SubitemIndexEnum.WATER_USAGE_TOTAL, currentMonths.get(yearMonthMapEntry.getKey())).toPlainString();
                        }
                    }).collect(Collectors.toList())),
                    null
            );
            CommonStaVO everHum = new CommonStaVO(
                    "平均温度",
                    humResult.stream().map(his -> his.getStaTime().concat("月")).collect(Collectors.toList()),
                    showList(humResult.stream().map(his -> his.getTemperature().toPlainString()).collect(Collectors.toList())),
                    null
            );
            list.add(waterUseTarget);
            list.add(waterUseAct);
            list.add(everHum);
        }
        response.setBarChartData(list);
        return response;
    }

    public LhBoardHavcEnergyCompareResponse getHavcCompare(String projectId,String year,String type) {
        // 当年使用量查询
        IndexMonthRequest indexMonthRequest = new IndexMonthRequest();
        YearMonth ym = YearMonth.of(Integer.valueOf(year),1);
        List<YearMonth> ymList =  DateUtils.getMonthsBetween(ym,ym.plusYears(1));
        YearMonth[] currentYearMonth = ymList.toArray(new YearMonth[]{});
        indexMonthRequest.setMonths(currentYearMonth);
        indexMonthRequest.setProjectBizId(projectId);
        SubitemIndexEnum[] currentMonthIndices = new SubitemIndexEnum[]{
                SubitemIndexEnum.PROJECT_ELECTRICITY_SUBHAVCENERGY_AVGSQ,
                SubitemIndexEnum.PROJECT_ELECTRICITY_SUBHEATINGWATERENERGY_AVGCUBE,
                SubitemIndexEnum.TOTAL_ELECTRICITY_CONSUMPTION_OF_ALL_LOADS,
                SubitemIndexEnum.ENERGY_SUBHVAC_TOTAL
        };
        indexMonthRequest.setIndices(currentMonthIndices);
        Response<Map<YearMonth, Map<SubitemIndexEnum, BigDecimal>>> mapMonthResponse = subitemApi.searchDataByMonth(indexMonthRequest);
        Map<YearMonth, Map<SubitemIndexEnum, BigDecimal>> currentMonths = mapMonthResponse.getResult();

        // 上年使用量查询
        YearMonth ymLastYear = YearMonth.of(Integer.valueOf(year)-1,1);
        List<YearMonth> ymLastYearList =  DateUtils.getMonthsBetween(ymLastYear,ymLastYear.plusYears(1));
        YearMonth[] lastYearMonth = ymLastYearList.toArray(new YearMonth[]{});
        indexMonthRequest.setMonths(lastYearMonth);
        indexMonthRequest.setProjectBizId(projectId);
        indexMonthRequest.setIndices(currentMonthIndices);
        Response<Map<YearMonth, Map<SubitemIndexEnum, BigDecimal>>> mapLastYearMonthResponse = subitemApi.searchDataByMonth(indexMonthRequest);
        Map<YearMonth, Map<SubitemIndexEnum, BigDecimal>> lastYearMonths = mapLastYearMonthResponse.getResult();


        //当年温度
        WeatherStaQueryDTO queryDTO = new WeatherStaQueryDTO();
        List<YearMonth> queryMonths = new ArrayList<>();
        queryMonths = DateUtils.getMonthsBetween(YearMonth.of(Integer.valueOf(year),1),YearMonth.of(Integer.valueOf(year)+1,1));//测试type2
        queryDTO.setYms(queryMonths);
        queryDTO.setCityName(projectWeatherApi.getProjectWeatherName(projectId).getResult());
        queryDTO.setType(2);
        List<WeatherHistoryDTO> humResult = weatherHistoryApi.getWeatherHistoryEverage(queryDTO).getResult();
        //上年温度
        queryMonths = DateUtils.getMonthsBetween(YearMonth.of(Integer.valueOf(year)-1,1),YearMonth.of(Integer.valueOf(year),1));//测试type2
        queryDTO.setYms(queryMonths);
        List<WeatherHistoryDTO> humResultLastYear = weatherHistoryApi.getWeatherHistoryEverage(queryDTO).getResult();

        LhBoardHavcEnergyCompareResponse response = new LhBoardHavcEnergyCompareResponse();
        List<CommonStaVO> list =new ArrayList<>();
        if("1".equals(type)){       //单平
            CommonStaVO eleLastYear = new CommonStaVO(
                    "上年电量",
                    ymLastYearList.stream().map(ymonth -> String.valueOf(ymonth.getMonthValue()).concat("月")).collect(Collectors.toList()),
                    showList(lastYearMonths.entrySet().stream().map(new Function<Map.Entry<YearMonth, Map<SubitemIndexEnum, BigDecimal>>, String>() {
                        @Override
                        public String apply(Map.Entry<YearMonth, Map<SubitemIndexEnum, BigDecimal>> yearMonthMapEntry) {
                            return getValue(SubitemIndexEnum.PROJECT_ELECTRICITY_SUBHAVCENERGY_AVGSQ, lastYearMonths.get(yearMonthMapEntry.getKey())).toPlainString();
                        }
                    }).collect(Collectors.toList())),
                    null
            );
            CommonStaVO eleThisYear = new CommonStaVO(
                    "当年电量",
                    ymList.stream().map(ymonth -> String.valueOf(ymonth.getMonthValue()).concat("月")).collect(Collectors.toList()),
                    showList(currentMonths.entrySet().stream().map(new Function<Map.Entry<YearMonth, Map<SubitemIndexEnum, BigDecimal>>, String>() {
                        @Override
                        public String apply(Map.Entry<YearMonth, Map<SubitemIndexEnum, BigDecimal>> yearMonthMapEntry) {
                            return getValue(SubitemIndexEnum.PROJECT_ELECTRICITY_SUBHAVCENERGY_AVGSQ, currentMonths.get(yearMonthMapEntry.getKey())).toPlainString();
                        }
                    }).collect(Collectors.toList())),
                    null
            );
            CommonStaVO humThisYear = new CommonStaVO(
                    "当年温度",
                    humResult.stream().map(his -> his.getStaTime().concat("月")).collect(Collectors.toList()),
                    showList(humResult.stream().map(his -> his.getTemperature().toPlainString()).collect(Collectors.toList())),
                    null
            );
            CommonStaVO humLastYear = new CommonStaVO(
                    "上年温度",
                    humResultLastYear.stream().map(his -> his.getStaTime().concat("月")).collect(Collectors.toList()),
                    showList(humResultLastYear.stream().map(his -> his.getTemperature().toPlainString()).collect(Collectors.toList())),
                    null
            );
            list.add(eleThisYear);
            list.add(eleLastYear);
            list.add(humThisYear);
            list.add(humLastYear);

        }else if("2".equals(type)){     //单立
            CommonStaVO eleLastYear = new CommonStaVO(
                    "上年用水量",
                    ymLastYearList.stream().map(ymonth -> String.valueOf(ymonth.getMonthValue()).concat("月")).collect(Collectors.toList()),
                    showList(lastYearMonths.entrySet().stream().map(new Function<Map.Entry<YearMonth, Map<SubitemIndexEnum, BigDecimal>>, String>() {
                        @Override
                        public String apply(Map.Entry<YearMonth, Map<SubitemIndexEnum, BigDecimal>> yearMonthMapEntry) {
                            return getValue(SubitemIndexEnum.PROJECT_ELECTRICITY_SUBHEATINGWATERENERGY_AVGCUBE, lastYearMonths.get(yearMonthMapEntry.getKey())).toPlainString();
                        }
                    }).collect(Collectors.toList())),
                    null
            );
            CommonStaVO eleThisYear = new CommonStaVO(
                    "当年用水量",
                    ymList.stream().map(ymonth -> String.valueOf(ymonth.getMonthValue()).concat("月")).collect(Collectors.toList()),
                    showList(currentMonths.entrySet().stream().map(new Function<Map.Entry<YearMonth, Map<SubitemIndexEnum, BigDecimal>>, String>() {
                        @Override
                        public String apply(Map.Entry<YearMonth, Map<SubitemIndexEnum, BigDecimal>> yearMonthMapEntry) {
                            return getValue(SubitemIndexEnum.PROJECT_ELECTRICITY_SUBHEATINGWATERENERGY_AVGCUBE, currentMonths.get(yearMonthMapEntry.getKey())).toPlainString();
                        }
                    }).collect(Collectors.toList())),
                    null
            );
            CommonStaVO humThisYear = new CommonStaVO(
                    "当年温度",
                    humResult.stream().map(his -> his.getStaTime().concat("月")).collect(Collectors.toList()),
                    showList(humResult.stream().map(his -> his.getTemperature().toPlainString()).collect(Collectors.toList())),
                    null
            );
            CommonStaVO humLastYear = new CommonStaVO(
                    "上年温度",
                    humResultLastYear.stream().map(his -> his.getStaTime().concat("月")).collect(Collectors.toList()),
                    showList(humResultLastYear.stream().map(his -> his.getTemperature().toPlainString()).collect(Collectors.toList())),
                    null
            );
            list.add(eleThisYear);
            list.add(eleLastYear);
            list.add(humThisYear);
            list.add(humLastYear);
        }
        response.setBarChartData(list);
        return response;
    }

    public LhProjectInfoResponse getCityInfo(String projectId) {
        LhProjectInfoResponse response = new LhProjectInfoResponse();
        ProjectDetailsResponse detail = projectApi.getProjectDetails(projectId).getResult();
//        ProjectDetailsResponse projectInfo  = projectApi.getDetails(detail.getId()).getResult();
        response.setArea(detail.getArea().toPlainString());
        response.setCityName(projectWeatherApi.getProjectWeatherName(projectId).getResult());
        return response;
    }

    public List<String> showList(Collection<String> collection){
        return collection.stream().map(s -> {
            return new BigDecimal(s).compareTo(BigDecimal.ZERO) == 0 ? "" : s;
        }).collect(Collectors.toList());
    }
}
