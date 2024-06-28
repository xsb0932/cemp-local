package com.landleaf.jzd.service;

import cn.hutool.core.util.NumberUtil;
import com.alibaba.fastjson2.JSONObject;
import com.landleaf.bms.api.ProjectApi;

import com.landleaf.comm.base.pojo.Response;
import com.landleaf.comm.vo.CommonStaVO;
import com.landleaf.data.api.device.DeviceHistoryApi;
import com.landleaf.energy.api.PlanedElectricityApi;
import com.landleaf.energy.api.SubareaApi;
import com.landleaf.energy.api.SubitemApi;
import com.landleaf.energy.enums.SubitemIndexEnum;
import com.landleaf.energy.request.*;
import com.landleaf.jzd.domain.enums.JzdConstants;
import com.landleaf.jzd.domain.response.JzdCurrentDatResponse;
import com.landleaf.jzd.domain.response.JzdOverviewResponse;
import com.landleaf.jzd.domain.vo.JzdBarCartData;
import com.landleaf.redis.RedisUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.landleaf.redis.constance.KeyConstance.DEVICE_CURRENT_STATUS_V1;
import static com.landleaf.jzd.domain.enums.JzdConstants.*;
/**
 * JzdScreenService
 *
 * @author xushibai
 * @since 2024/01/22
 **/
@SuppressWarnings("ALL")
@Service
@RequiredArgsConstructor
public class JzdScreenService {

    private final DeviceHistoryApi deviceHistoryApi;
    private final SubitemApi subitemApi;
    private final SubareaApi subareaApi;
    private final RedisUtils redisUtils;
    private final ProjectApi projectApi;
    private final PlanedElectricityApi planedElectricityApi;
    private final String[] X_LIST_YEAR_DESC = new String[]{"1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月"};
    private final String[] X_LIST_YEAR = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"};
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

    private LocalDate[] getCurrentMonthDay(LocalDate now) {
        List<LocalDate> monthDays = new ArrayList<>();
        int days = now.lengthOfMonth();
        for (int i = 1; i <= days; i++) {
            monthDays.add(LocalDate.of(now.getYear(), now.getMonth(), i));
        }
        return monthDays.toArray(new LocalDate[0]);
    }

    @SuppressWarnings("LambdaBodyCanBeCodeBlock")
    public JzdOverviewResponse overview(String projectBizId) {

        // 当年使用量查询
        IndexYearRequest indexYearRequest = new IndexYearRequest();
        indexYearRequest.setYears(new Year[]{Year.now()});
        indexYearRequest.setProjectBizId(projectBizId);
        SubitemIndexEnum[] currentYearIndices = new SubitemIndexEnum[]{
                // 当年光伏发电量
                SubitemIndexEnum.PHOTOVOLTAIC_POWER_GENERATION,
                // 当年购网电量
                SubitemIndexEnum.PURCHASE_NETWORK_ELECTRICITY,
                // 当年上网电量
                SubitemIndexEnum.ON_GRID_ENERGY,
                //当年光伏上网电量
                SubitemIndexEnum.PV_ON_GRID_ENERGY,
                // 当年总用电量
                SubitemIndexEnum.TOTAL_ELECTRICITY_CONSUMPTION_OF_ALL_LOADS,
                // 当年用水
                SubitemIndexEnum.WATER_USAGE_TOTAL,
                // 当年二氧化碳排放
                SubitemIndexEnum.CARBON_DIOXIDE_EMISSIONS,

        };
        indexYearRequest.setIndices(currentYearIndices);
        Response<Map<Year, Map<SubitemIndexEnum, BigDecimal>>> mapYearResponse = subitemApi.searchDataByYear(indexYearRequest);
        Map<SubitemIndexEnum, BigDecimal> currentYear = mapYearResponse.getResult().get(Year.now());

        // 月度使用量查询
        IndexMonthRequest indexMonthRequest = new IndexMonthRequest();
        YearMonth yearMonth = YearMonth.now();
        YearMonth[] currentYearMonth = getCurrentYearMonth(yearMonth);
        indexMonthRequest.setMonths(currentYearMonth);
        indexMonthRequest.setProjectBizId(projectBizId);
        SubitemIndexEnum[] currentMonthIndices = new SubitemIndexEnum[]{
                // 光伏发电
                SubitemIndexEnum.PHOTOVOLTAIC_POWER_GENERATION,
                // 用电量
                SubitemIndexEnum.TOTAL_ELECTRICITY_CONSUMPTION_OF_ALL_LOADS,
                // 购网电量
                SubitemIndexEnum.PURCHASE_NETWORK_ELECTRICITY,
                // 上网电量
                SubitemIndexEnum.PV_ON_GRID_ENERGY,
        };
        indexMonthRequest.setIndices(currentMonthIndices);
        Response<Map<YearMonth, Map<SubitemIndexEnum, BigDecimal>>> mapMonthResponse = subitemApi.searchDataByMonth(indexMonthRequest);
        Map<YearMonth, Map<SubitemIndexEnum, BigDecimal>> currentMonths = mapMonthResponse.getResult();

        // 上年同期-月度使用量查询
        IndexMonthRequest indexMonthLastYearRequest = new IndexMonthRequest();
        YearMonth indexMonthLastYear = YearMonth.now();
        YearMonth[] lastYearMonth = Arrays.stream(getCurrentYearMonth(yearMonth)).filter(new Predicate<YearMonth>() {
            @Override
            public boolean test(YearMonth yearMonth) {
                return yearMonth.isBefore(YearMonth.now());
            }
        }).map(yearMonth1 -> yearMonth1.minusYears(1L)).collect(Collectors.toList()).toArray(new YearMonth[]{});
        indexMonthLastYearRequest.setMonths(lastYearMonth);
        indexMonthLastYearRequest.setProjectBizId(projectBizId);
        SubitemIndexEnum[] lastYearMonthIndices = new SubitemIndexEnum[]{
                // 用水量
                SubitemIndexEnum.WATER_USAGE_TOTAL,

        };
        indexMonthLastYearRequest.setIndices(lastYearMonthIndices);
        Response<Map<YearMonth, Map<SubitemIndexEnum, BigDecimal>>> mapLastYearMonthResponse = subitemApi.searchDataByMonth(indexMonthLastYearRequest);
        Map<YearMonth, Map<SubitemIndexEnum, BigDecimal>> lastYearMonths = mapLastYearMonthResponse.getResult();
        BigDecimal lymWaterUsage = lastYearMonths.keySet().stream().map(new Function<YearMonth, BigDecimal>() {
            @Override
            public BigDecimal apply(YearMonth month) {
                BigDecimal b = getValue(SubitemIndexEnum.WATER_USAGE_TOTAL, lastYearMonths.get(month));
                return b;
            }
        }).reduce(BigDecimal.ZERO,BigDecimal::add);
        //BigDecimal lymWaterUsage = lastYearMonths.entrySet().stream().map(month -> getValue(SubitemIndexEnum.WATER_USAGE_TOTAL, lastYearMonths.get(month))).reduce(BigDecimal.ZERO,BigDecimal::add);

        // 日用电量 用来统计上年同期
        IndexDayRequest indexDayRequest = new IndexDayRequest();
        LocalDate localDate = LocalDate.now();
        LocalDate[] currentMonthDay = getCurrentMonthDay(localDate);
        indexDayRequest.setDays(currentMonthDay);
        indexDayRequest.setProjectBizId(projectBizId);
        SubitemIndexEnum[] currentDayIndices = new SubitemIndexEnum[]{
                // 用水量
                SubitemIndexEnum.WATER_USAGE_TOTAL,

        };
        indexDayRequest.setIndices(currentDayIndices);
        Response<Map<LocalDate, Map<SubitemIndexEnum, BigDecimal>>> mapDayResponse = subitemApi.searchDataByDay(indexDayRequest);
        Map<LocalDate, Map<SubitemIndexEnum, BigDecimal>> currentDays = mapDayResponse.getResult();


        JzdOverviewResponse response = new JzdOverviewResponse();
        response.setElectricityPccEnergyUsageCurrentYear(getValue(SubitemIndexEnum.PURCHASE_NETWORK_ELECTRICITY, currentYear));
        // 累计光伏发电量
        response.setElectricityPvEnergyProductionCurrentYear(getValue(SubitemIndexEnum.PHOTOVOLTAIC_POWER_GENERATION, currentYear));
        //response.setEleUseCurrentYear(getValue(SubitemIndexEnum.TOTAL_ELECTRICITY_CONSUMPTION_OF_ALL_LOADS, currentYear));
        response.setWaterUseCurrentYear(getValue(SubitemIndexEnum.WATER_USAGE_TOTAL, currentYear));
        response.setCarbonUseCurrentYear(getValue(SubitemIndexEnum.CARBON_DIOXIDE_EMISSIONS, currentYear));
        response.setElePvLastMonth(getValue(SubitemIndexEnum.TOTAL_ELECTRICITY_CONSUMPTION_OF_ALL_LOADS, currentYear));     //todo
        response.setWaterUseLastYear(lymWaterUsage);
        //当月发电量
        response.setElePvCurrentMonth(getValue(SubitemIndexEnum.PHOTOVOLTAIC_POWER_GENERATION, currentMonths.get(YearMonth.now())));
        //response.setEleUseCurrentMonth(getValue(SubitemIndexEnum.TOTAL_ELECTRICITY_CONSUMPTION_OF_ALL_LOADS, currentMonths.get(YearMonth.now())));
        response.setWaterUseCurrentMonth(BigDecimal.valueOf(967));
        //当年减少二氧化碳
        response.setCarbonReCurrentYear(NumberUtil.mul(response.getElectricityPvEnergyProductionCurrentYear(),BigDecimal.valueOf(0.42)));

        /** 暖通能耗用电强度 **/
        response.setEnergyActualCurrentYear(BigDecimal.valueOf(5));
        response.setEnergyActualLastYear(BigDecimal.valueOf(75));
        response.setElePlan(planedElectricityApi.getProjectYearTotalPlan(projectBizId,String.valueOf(localDate.getYear())).getResult());

        /** 当年用电自给比例&光伏消纳比例 **/
        // 光伏绿电占总用电比例 根据项目报表指标计算，光伏发电量 /（光伏发电量 + 关口购网电量），需要月 + 日 + 小时得到当年值。
        response.setPvTotalRatio(response.getElectricityPvEnergyProductionCurrentYear().divide(NumberUtil.add(response.getElectricityPvEnergyProductionCurrentYear(),response.getElectricityPccEnergyUsageCurrentYear()),3, RoundingMode.HALF_UP));
        // 当年光伏发电消纳比例 1-当年光伏上网电量/当年光伏发电量
        BigDecimal currentYearGrid = getValue(SubitemIndexEnum.PV_ON_GRID_ENERGY, currentYear);
        // 当年上网电量
        response.setEleGridCurrentYear(getValue(SubitemIndexEnum.PV_ON_GRID_ENERGY, currentYear));

        BigDecimal currentYearPv = getValue(SubitemIndexEnum.PHOTOVOLTAIC_POWER_GENERATION, currentYear);

        //消纳比例 = 光伏上网电量/光伏总发电量
        if (currentYearPv == null || currentYearGrid == null || currentYearPv.equals(BigDecimal.ZERO)) {
            response.setPvConsRatio(null);
        } else {
            response.setPvConsRatio((BigDecimal.ONE.subtract(currentYearGrid.divide(currentYearPv, 4, RoundingMode.HALF_UP))));
            //response.setPvConsRatio((currentYearGrid.divide(currentYearPv, 4, RoundingMode.HALF_UP)));
        }
//        response.setPvTotalRatio(BigDecimal.valueOf(0.042));
//        response.setPvConsRatio(BigDecimal.valueOf(0.99));


        /** 月电源结构趋势 **/
        JzdBarCartData eleMonthRatioTrend = new JzdBarCartData();
        List<CommonStaVO> barChartData1= new ArrayList<>();
        //光伏发电
        //String[] pvDataList = new String[]{"22128","0","0","0","0","0","0","0","0","0","0","0"};
        List<String> pvDataList = Arrays.stream(X_LIST_YEAR).map(month -> getValue(SubitemIndexEnum.PHOTOVOLTAIC_POWER_GENERATION, currentMonths.get(YearMonth.of(Year.now().getValue(), Integer.valueOf(month)))).toPlainString()).collect(Collectors.toList());
        CommonStaVO pvSta = new CommonStaVO(
                "光伏发电",
                Arrays.asList(X_LIST_YEAR_DESC),
                pvDataList,
                null
        );
        //市电
        //String[] cDataList = new String[]{"682789","0","0","0","0","0","0","0","0","0","0","0"};
        List<String> cDataList = Arrays.stream(X_LIST_YEAR).map(new Function<String, String>() {
            @Override
            public String apply(String month) {
                BigDecimal b = getValue(SubitemIndexEnum.PURCHASE_NETWORK_ELECTRICITY, currentMonths.get(YearMonth.of(Year.now().getValue(), Integer.valueOf(month))));
                return b.toPlainString();
            }
        }).collect(Collectors.toList());
        CommonStaVO cSta = new CommonStaVO(
                "市电",
                Arrays.asList(X_LIST_YEAR_DESC),
                cDataList,
                null
        );
        barChartData1.add(pvSta);
        barChartData1.add(cSta);
        eleMonthRatioTrend.setBarChartData(barChartData1);
        response.setEleMonthRatioTrend(eleMonthRatioTrend);

        /** 当年用电量占比 **/
        String[] subitemList = new String[]{"医疗板块","汽车板块","检测板块","集团公司"};
        String[] subitemList4Water = new String[]{"医疗板块","汽车板块","检测板块","集团"};
        SubareaMonthRequest subareaMonthRequest = new SubareaMonthRequest(projectBizId, JZD_TENANT_ID,getYms(),"area.electricity.energyUsage.total",Arrays.asList(subitemList),"电");
        SubareaMonthRequest subareaWaterMonthRequest = new SubareaMonthRequest(projectBizId,JZD_TENANT_ID,getYms(),"area.water.usage.total",Arrays.asList(subitemList4Water),"水");
        Map<String,Map<YearMonth,BigDecimal>> subareaData = subareaApi.searchMonth(subareaMonthRequest).getResult();
        Map<String,Map<YearMonth,BigDecimal>> subareaWaterData = subareaApi.searchMonth(subareaWaterMonthRequest).getResult();
        //subareaData ->subitemValueList
        List<String > result  =Arrays.asList(subitemList).stream().map(new Function<String, String>() {
            @Override
            public String apply(String name) {
                Map<YearMonth,BigDecimal> subare = subareaData.get(name);
                BigDecimal total = subareaData.get(name).keySet().stream().map(ym -> subare.get(ym)).reduce(BigDecimal.ZERO,BigDecimal::add);
                return total.toPlainString();
            }
        }).collect(Collectors.toList());


        //String[] subitemValueList = new String[]{"22342","646191","8868","27516"};
        CommonStaVO eleUseSta = new CommonStaVO(
                "当年用电量占比",
                Arrays.asList(subitemList),
                result,
                null
                );
        response.setEleUseRatio(eleUseSta);

        /** 月负荷结构趋势-电 **/
        JzdBarCartData eleTrendData = new JzdBarCartData();
        List<CommonStaVO> eleTrendDataList= new ArrayList<>();
        //医疗
        Map<YearMonth,BigDecimal> eleTrend1Map = subareaData.get("医疗板块");
        List<String> eleTrend1 = eleTrend1Map != null ? Arrays.asList(X_LIST_YEAR).stream().map(new Function<String, String>() {
            @Override
            public String apply(String month) {
                return eleTrend1Map.getOrDefault(YearMonth.of(YearMonth.now().getYear(),Integer.valueOf(month)),BigDecimal.ZERO).toPlainString();
            }
        }).collect(Collectors.toList()) : null;
        //String[] eleTrend1 = new String[]{"22342","0","0","0","0","0","0","0","0","0","0","0"};
        CommonStaVO eleTrendSta1 = new CommonStaVO(
                "医疗",
                Arrays.asList(X_LIST_YEAR_DESC),
                eleTrend1,
                null
        );
        //监测
        Map<YearMonth,BigDecimal> eleTrend2Map = subareaData.get("检测板块");
        List<String> eleTrend2 = eleTrend2Map != null ? Arrays.asList(X_LIST_YEAR).stream().map(new Function<String, String>() {
            @Override
            public String apply(String month) {
                return eleTrend2Map.getOrDefault(YearMonth.of(YearMonth.now().getYear(),Integer.valueOf(month)),BigDecimal.ZERO).toPlainString();
            }
        }).collect(Collectors.toList()) : null;
//        String[] eleTrend2 = new String[]{"8868","0","0","0","0","0","0","0","0","0","0","0"};
        CommonStaVO eleTrendSta2 = new CommonStaVO(
                "检测",
                Arrays.asList(X_LIST_YEAR_DESC),
                eleTrend2,
                null
        );
        //汽车
        Map<YearMonth,BigDecimal> eleTrend3Map = subareaData.get("汽车板块");
        List<String> eleTrend3 = eleTrend3Map!=null ? Arrays.asList(X_LIST_YEAR).stream().map(new Function<String, String>() {
            @Override
            public String apply(String month) {
                return eleTrend3Map.getOrDefault(YearMonth.of(YearMonth.now().getYear(),Integer.valueOf(month)),BigDecimal.ZERO).toPlainString();
            }
        }).collect(Collectors.toList()): null;
//        String[] eleTrend3 = new String[]{"646191","0","0","0","0","0","0","0","0","0","0","0"};
        CommonStaVO eleTrendSta3 = new CommonStaVO(
                "汽车",
                Arrays.asList(X_LIST_YEAR_DESC),
                eleTrend3,
                null
        );
        //集团
        Map<YearMonth,BigDecimal> eleTrend4Map = subareaData.get("集团公司");
        List<String> eleTrend4 = eleTrend4Map != null ? Arrays.asList(X_LIST_YEAR).stream().map(new Function<String, String>() {
            @Override
            public String apply(String month) {
                return eleTrend4Map.getOrDefault(YearMonth.of(YearMonth.now().getYear(),Integer.valueOf(month)),BigDecimal.ZERO).toPlainString();
            }
        }).collect(Collectors.toList()):null;
//        String[] eleTrend4 = new String[]{"27516","0","0","0","0","0","0","0","0","0","0","0"};
        CommonStaVO eleTrendSta4 = new CommonStaVO(
                "集团",
                Arrays.asList(X_LIST_YEAR_DESC),
                eleTrend4,
                null
        );

        eleTrendDataList.add(eleTrendSta1);
        eleTrendDataList.add(eleTrendSta2);
        eleTrendDataList.add(eleTrendSta3);
        eleTrendDataList.add(eleTrendSta4);
        eleTrendData.setBarChartData(eleTrendDataList);
        response.setPEleMonthRatioTrend(eleTrendData);

        /** 月负荷结构趋势-水 **/
        JzdBarCartData waterTrendData = new JzdBarCartData();
        List<CommonStaVO> waterTrendDataList= new ArrayList<>();
        //医疗
        Map<YearMonth,BigDecimal> waterTrend1Map = subareaWaterData.get("医疗板块");
        List<String> waterTrend1 = waterTrend1Map != null ?Arrays.asList(X_LIST_YEAR).stream().map(new Function<String, String>() {
            @Override
            public String apply(String month) {
                return waterTrend1Map.getOrDefault(YearMonth.of(YearMonth.now().getYear(),Integer.valueOf(month)),BigDecimal.ZERO).toPlainString();
            }
        }).collect(Collectors.toList()): null;
//        String[] waterTrend1 = new String[]{"115","0","0","0","0","0","0","0","0","0","0","0"};
        CommonStaVO waterTrendSta1 = new CommonStaVO(
                "医疗",
                Arrays.asList(X_LIST_YEAR_DESC),
                waterTrend1,
                null
        );
        //监测
        Map<YearMonth,BigDecimal> waterTrend2Map = subareaWaterData.get("检测板块");
        List<String> waterTrend2 = waterTrend2Map != null ? Arrays.asList(X_LIST_YEAR).stream().map(new Function<String, String>() {
            @Override
            public String apply(String month) {
                return waterTrend2Map.getOrDefault(YearMonth.of(YearMonth.now().getYear(),Integer.valueOf(month)),BigDecimal.ZERO).toPlainString();
            }
        }).collect(Collectors.toList()): null;
//        String[] waterTrend2 = new String[]{"9","0","0","0","0","0","0","0","0","0","0","0"};
        CommonStaVO waterTrendSta2 = new CommonStaVO(
                "检测",
                Arrays.asList(X_LIST_YEAR_DESC),
                waterTrend2,
                null
        );
        //汽车
        Map<YearMonth,BigDecimal> waterTrend3Map = subareaWaterData.get("汽车板块");
        List<String> waterTrend3 = waterTrend3Map != null ? Arrays.asList(X_LIST_YEAR).stream().map(new Function<String, String>() {
            @Override
            public String apply(String month) {
                return waterTrend3Map.getOrDefault(YearMonth.of(YearMonth.now().getYear(),Integer.valueOf(month)),BigDecimal.ZERO).toPlainString();
            }
        }).collect(Collectors.toList()): null;
//        String[] waterTrend3 = new String[]{"462","0","0","0","0","0","0","0","0","0","0","0"};
        CommonStaVO waterTrendSta3 = new CommonStaVO(
                "汽车",
                Arrays.asList(X_LIST_YEAR_DESC),
                waterTrend3,
                null
        );
        //集团
        Map<YearMonth,BigDecimal> waterTrend4Map = subareaWaterData.get("集团");
        List<String> waterTrend4 = waterTrend4Map != null ? Arrays.asList(X_LIST_YEAR).stream().map(new Function<String, String>() {
            @Override
            public String apply(String month) {
                return waterTrend4Map.getOrDefault(YearMonth.of(YearMonth.now().getYear(),Integer.valueOf(month)),BigDecimal.ZERO).toPlainString();
            }
        }).collect(Collectors.toList()) : null;
//        String[] waterTrend4 = new String[]{"381","0","0","0","0","0","0","0","0","0","0","0"};
        CommonStaVO waterTrendSta4 = new CommonStaVO(
                "集团",
                Arrays.asList(X_LIST_YEAR_DESC),
                waterTrend4,
                null
        );

        waterTrendDataList.add(waterTrendSta1);
        waterTrendDataList.add(waterTrendSta2);
        waterTrendDataList.add(waterTrendSta3);
        waterTrendDataList.add(waterTrendSta4);
        waterTrendData.setBarChartData(waterTrendDataList);
        response.setPWaterEleMonthRatioTrend(waterTrendData);


        //当年用电量 = 购网电量 + 发电量 - 上网电量
        response.setEleUseCurrentYear(NumberUtil.add(response.getElectricityPccEnergyUsageCurrentYear(),response.getElectricityPvEnergyProductionCurrentYear()).subtract(response.getEleGridCurrentYear()));
        //当月用电量 = 购网电量 + 发电量 - 上网电量
        //当月购网电量
        BigDecimal elePurchaseMonth = getValue(SubitemIndexEnum.PURCHASE_NETWORK_ELECTRICITY, currentMonths.get(YearMonth.now()));
        //当月上网电量
        BigDecimal elePvGridMonth = getValue(SubitemIndexEnum.PV_ON_GRID_ENERGY, currentMonths.get(YearMonth.now()));
        response.setEleUseCurrentMonth(NumberUtil.add(elePurchaseMonth,response.getElePvCurrentMonth()).subtract(elePvGridMonth));
        response.setEleTotal(response.getEleUseCurrentYear());


        /** 当年用水量占比 **/
        String[] subitemWaterList = new String[]{"医疗板块","汽车板块","检测板块","集团"};
        List<String > waterResult  =Arrays.asList(subitemWaterList).stream().map(new Function<String, String>() {
            @Override
            public String apply(String name) {
                Map<YearMonth,BigDecimal> subare = subareaWaterData.get(name);
                BigDecimal total = subare != null ?subareaWaterData.get(name).keySet().stream().map(ym -> subare.getOrDefault(ym,BigDecimal.ZERO)).reduce(BigDecimal.ZERO,BigDecimal::add) : BigDecimal.ZERO;
                return total.toPlainString();
            }
        }).collect(Collectors.toList());

        //String[] subitemWaterValueList = new String[]{"115","462","9","381"};
        CommonStaVO waterUseSta = new CommonStaVO(
                "当年用水量占比",
                Arrays.asList(subitemWaterList),
                waterResult,
                null
        );
        response.setWaterUseRatio(waterUseSta);
        return response;
    }

    private List<YearMonth> getYms(){
        List<YearMonth> yms = new ArrayList<>();
        YearMonth ymNow = YearMonth.now();
        YearMonth firstMonth = YearMonth.of(ymNow.getYear(),1);
        while (ymNow.isAfter(firstMonth)){
            yms.add(ymNow);
            ymNow  = ymNow.minusMonths(1L);
        }
        yms.add(firstMonth);
        return yms;
    }

    public JzdCurrentDatResponse current(String projectBizId) {
        List<String> epimportBizDeviceIds = Arrays.asList(
                DEV_JZD_NB_01,
                DEV_2APEZ2_2,
                DEV_3F_5,
                DEV_3F_7,
                DEV_3F_1,
                DEV_3F_8,
                DEV_2APEZ1_2,
                DEV_4F_1,
                DEV_2APEZ1_3,
                DEV_4F_2,
                DEV_3F_3,
                DEV_4F_3,
                DEV_3F_2,
                DEV_4F_4,
                DEV_3F_4,
                DEV_4F_5,
                DEV_2APEZ1_5,
                DEV_3F_6,
                DEV_2APEZ2_5,
                DEV_RF_5,
                DEV_RF_4,
                DEV_RF_3,
                DEV_RF_2,
                DEV_RF_1,
                DEV_4F_6,
                DEV_2APEZ2_4,
                DEV_2APEZ2_3,
                DEV_2APEZ1_4,
                DEV_2APZ2_6,
                DEV_2APZ1_6,
                DEV_2APZ2_5,
                DEV_2APZ2_4,
                DEV_2APZ1_5
        );
        // 月度使用量查询
        IndexMonthRequest indexMonthRequest = new IndexMonthRequest();
        YearMonth yearMonth = YearMonth.now();
        YearMonth[] currentYearMonth = getCurrentYearMonth(yearMonth);
        indexMonthRequest.setMonths(currentYearMonth);
        indexMonthRequest.setProjectBizId(projectBizId);
        SubitemIndexEnum[] currentMonthIndices = new SubitemIndexEnum[]{
                // 光伏发电
                SubitemIndexEnum.PHOTOVOLTAIC_POWER_GENERATION,
                // 电梯
                SubitemIndexEnum.ENERGY_SUBELEVATOR_TOTAL,
                // 照明
                SubitemIndexEnum.LIGHTING_POWER_CONSUMPTION,
                // 插座
                SubitemIndexEnum.SOCKET_POWER_CONSUMPTION,
                // 空调
                SubitemIndexEnum.ENERGY_SUBHVAC_TOTAL,
                // 特殊
                SubitemIndexEnum.OTHER_ELECTRICITY_CONSUMPTION,
                SubitemIndexEnum.PURCHASE_NETWORK_ELECTRICITY,
                SubitemIndexEnum.PHOTOVOLTAIC_POWER_GENERATION,
                SubitemIndexEnum.PV_ON_GRID_ENERGY
        };
        indexMonthRequest.setIndices(currentMonthIndices);
        Response<Map<YearMonth, Map<SubitemIndexEnum, BigDecimal>>> mapMonthResponse = subitemApi.searchDataByMonth(indexMonthRequest);
        Map<YearMonth, Map<SubitemIndexEnum, BigDecimal>> currentMonths = mapMonthResponse.getResult();

        //插座
        Map<Object, Object> dev2APEZ22 = redisUtils.hmget(DEVICE_CURRENT_STATUS_V1 + DEV_2APEZ2_2);
        Map<Object, Object> dev3F5 = redisUtils.hmget(DEVICE_CURRENT_STATUS_V1 + DEV_3F_5);
        Map<Object, Object> dev3F7 = redisUtils.hmget(DEVICE_CURRENT_STATUS_V1 + DEV_3F_7);
        Map<Object, Object> dev3F1 = redisUtils.hmget(DEVICE_CURRENT_STATUS_V1 + DEV_3F_1);
        Map<Object, Object> dev3F8 = redisUtils.hmget(DEVICE_CURRENT_STATUS_V1 + DEV_3F_8);
        Map<Object, Object> dev2APEZ12 = redisUtils.hmget(DEVICE_CURRENT_STATUS_V1 + DEV_2APEZ1_2);
        Map<Object, Object> dev4F1 = redisUtils.hmget(DEVICE_CURRENT_STATUS_V1 + DEV_4F_1);
        Map<Object, Object> dev2APEZ13 = redisUtils.hmget(DEVICE_CURRENT_STATUS_V1 + DEV_2APEZ1_3);
        Map<Object, Object> dev4F2 = redisUtils.hmget(DEVICE_CURRENT_STATUS_V1 + DEV_4F_2);
        Map<Object, Object> dev3F3 = redisUtils.hmget(DEVICE_CURRENT_STATUS_V1 + DEV_3F_3);
        Map<Object, Object> dev4F3 = redisUtils.hmget(DEVICE_CURRENT_STATUS_V1 + DEV_4F_3);
        Map<Object, Object> dev3F2 = redisUtils.hmget(DEVICE_CURRENT_STATUS_V1 + DEV_3F_2);
        Map<Object, Object> dev4F4 = redisUtils.hmget(DEVICE_CURRENT_STATUS_V1 + DEV_4F_4);
        Map<Object, Object> dev3F4 = redisUtils.hmget(DEVICE_CURRENT_STATUS_V1 + DEV_3F_4);
        Map<Object, Object> dev4F5 = redisUtils.hmget(DEVICE_CURRENT_STATUS_V1 + DEV_4F_5);
        Map<Object, Object> dev2APEZ15 = redisUtils.hmget(DEVICE_CURRENT_STATUS_V1 + DEV_2APEZ1_5);
        Map<Object, Object> dev3F6 = redisUtils.hmget(DEVICE_CURRENT_STATUS_V1 + DEV_3F_6);
        Map<Object, Object> dev2APEZ25 = redisUtils.hmget(DEVICE_CURRENT_STATUS_V1 + DEV_2APEZ2_5);
        //暖通
        Map<Object, Object> devRF5 = redisUtils.hmget(DEVICE_CURRENT_STATUS_V1 + DEV_RF_5);
        Map<Object, Object> devRF4 = redisUtils.hmget(DEVICE_CURRENT_STATUS_V1 + DEV_RF_4);
        Map<Object, Object> devRF3 = redisUtils.hmget(DEVICE_CURRENT_STATUS_V1 + DEV_RF_3);
        Map<Object, Object> devRF2 = redisUtils.hmget(DEVICE_CURRENT_STATUS_V1 + DEV_RF_2);
        Map<Object, Object> devRF1 = redisUtils.hmget(DEVICE_CURRENT_STATUS_V1 + DEV_RF_1);
        //特殊
        Map<Object, Object> dev4F6 = redisUtils.hmget(DEVICE_CURRENT_STATUS_V1 + DEV_4F_6);
        Map<Object, Object> dev2APEZ24 = redisUtils.hmget(DEVICE_CURRENT_STATUS_V1 + DEV_2APEZ2_4);
        Map<Object, Object> dev2APEZ23 = redisUtils.hmget(DEVICE_CURRENT_STATUS_V1 + DEV_2APEZ2_3);
        Map<Object, Object> dev2APEZ14 = redisUtils.hmget(DEVICE_CURRENT_STATUS_V1 + DEV_2APEZ1_4);
        Map<Object, Object> dev2APZ26 = redisUtils.hmget(DEVICE_CURRENT_STATUS_V1 + DEV_2APZ2_6);
        //电梯
        Map<Object, Object> dev2APZ16 = redisUtils.hmget(DEVICE_CURRENT_STATUS_V1 + DEV_2APZ1_6);
        Map<Object, Object> dev2APZ25 = redisUtils.hmget(DEVICE_CURRENT_STATUS_V1 + DEV_2APZ2_5);
        Map<Object, Object> dev2APZ24 = redisUtils.hmget(DEVICE_CURRENT_STATUS_V1 + DEV_2APZ2_4);
        Map<Object, Object> dev2APZ15 = redisUtils.hmget(DEVICE_CURRENT_STATUS_V1 + DEV_2APZ1_5);
        //光伏
        Map<Object, Object> devJZDNB01 = redisUtils.hmget(DEVICE_CURRENT_STATUS_V1 + DEV_JZD_NB_01);
        Map<Object, Object> devJZDNB03 = redisUtils.hmget(DEVICE_CURRENT_STATUS_V1 + DEV_JZD_NB_03);
        Map<Object, Object> devJZDNB02 = redisUtils.hmget(DEVICE_CURRENT_STATUS_V1 + DEV_JZD_NB_02);
        Map<Object, Object> devJZDNB04 = redisUtils.hmget(DEVICE_CURRENT_STATUS_V1 + DEV_JZD_NB_04);



        JzdCurrentDatResponse response = new JzdCurrentDatResponse();
        response.setEleNetP("--");
        response.setEleNetQ("--");
        response.setPvP(NumberUtil.add(
                new BigDecimal(getPropertyValue(devJZDNB01,"P")),
                new BigDecimal(getPropertyValue(devJZDNB03,"P")),
                new BigDecimal(getPropertyValue(devJZDNB02,"P")),
                new BigDecimal(getPropertyValue(devJZDNB04,"P"))
                ));
        response.setPvQ(NumberUtil.add(
                new BigDecimal(getPropertyValue(devJZDNB01,"Q")),
                new BigDecimal(getPropertyValue(devJZDNB03,"Q")),
                new BigDecimal(getPropertyValue(devJZDNB02,"Q")),
                new BigDecimal(getPropertyValue(devJZDNB04,"Q"))
                ));

        response.setPurchaseMonth(getValue(SubitemIndexEnum.PURCHASE_NETWORK_ELECTRICITY, currentMonths.get(YearMonth.now())));
        response.setEpexportMonth(getValue(SubitemIndexEnum.PHOTOVOLTAIC_POWER_GENERATION, currentMonths.get(YearMonth.now())));
        response.setEleGrid(getValue(SubitemIndexEnum.PV_ON_GRID_ENERGY, currentMonths.get(YearMonth.now())));

        response.setElevatorP(NumberUtil.add(
                new BigDecimal(getPropertyValue(dev2APZ16,"P")),
                new BigDecimal(getPropertyValue(dev2APZ25,"P")),
                new BigDecimal(getPropertyValue(dev2APZ24,"P")),
                new BigDecimal(getPropertyValue(dev2APZ15,"P"))
                ));
        response.setElevatorQ(NumberUtil.add(
                new BigDecimal(getPropertyValue(dev2APZ16,"Q")),
                new BigDecimal(getPropertyValue(dev2APZ25,"Q")),
                new BigDecimal(getPropertyValue(dev2APZ24,"Q")),
                new BigDecimal(getPropertyValue(dev2APZ15,"Q"))
                ));
        response.setElevatorEpimportMonth(getValue(SubitemIndexEnum.ENERGY_SUBELEVATOR_TOTAL, currentMonths.get(YearMonth.now())));
        response.setLightP(NumberUtil.add(
                new BigDecimal(getPropertyValue(dev2APEZ22,"P")),
                new BigDecimal(getPropertyValue(dev3F5,"P")),
                new BigDecimal(getPropertyValue(dev3F7,"P")),
                new BigDecimal(getPropertyValue(dev3F1,"P")),
                new BigDecimal(getPropertyValue(dev3F8,"P")),
                new BigDecimal(getPropertyValue(dev2APEZ12,"P")),
                new BigDecimal(getPropertyValue(dev4F1,"P")),
                new BigDecimal(getPropertyValue(dev2APEZ13,"P")),
                new BigDecimal(getPropertyValue(dev4F2,"P")),
                new BigDecimal(getPropertyValue(dev3F3,"P")),
                new BigDecimal(getPropertyValue(dev4F3,"P")),
                new BigDecimal(getPropertyValue(dev3F2,"P")),
                new BigDecimal(getPropertyValue(dev4F4,"P")),
                new BigDecimal(getPropertyValue(dev3F4,"P")),
                new BigDecimal(getPropertyValue(dev4F5,"P")),
                new BigDecimal(getPropertyValue(dev2APEZ15,"P")),
                new BigDecimal(getPropertyValue(dev3F6,"P")),
                new BigDecimal(getPropertyValue(dev2APEZ25,"P"))
                ));
        response.setLightQ(NumberUtil.add(
                new BigDecimal(getPropertyValue(dev2APEZ22,"Q")),
                new BigDecimal(getPropertyValue(dev3F5,"Q")),
                new BigDecimal(getPropertyValue(dev3F7,"Q")),
                new BigDecimal(getPropertyValue(dev3F1,"Q")),
                new BigDecimal(getPropertyValue(dev3F8,"Q")),
                new BigDecimal(getPropertyValue(dev2APEZ12,"Q")),
                new BigDecimal(getPropertyValue(dev4F1,"Q")),
                new BigDecimal(getPropertyValue(dev2APEZ13,"Q")),
                new BigDecimal(getPropertyValue(dev4F2,"Q")),
                new BigDecimal(getPropertyValue(dev3F3,"Q")),
                new BigDecimal(getPropertyValue(dev4F3,"Q")),
                new BigDecimal(getPropertyValue(dev3F2,"Q")),
                new BigDecimal(getPropertyValue(dev4F4,"Q")),
                new BigDecimal(getPropertyValue(dev3F4,"Q")),
                new BigDecimal(getPropertyValue(dev4F5,"Q")),
                new BigDecimal(getPropertyValue(dev2APEZ15,"Q")),
                new BigDecimal(getPropertyValue(dev3F6,"Q")),
                new BigDecimal(getPropertyValue(dev2APEZ25,"Q"))
                ));

        response.setLightEpimportMonth(NumberUtil.add(
                getValue(SubitemIndexEnum.LIGHTING_POWER_CONSUMPTION, currentMonths.get(YearMonth.now())),
                getValue(SubitemIndexEnum.SOCKET_POWER_CONSUMPTION, currentMonths.get(YearMonth.now()))
        ));
        response.setHavcP(NumberUtil.add(
                new BigDecimal(getPropertyValue(devRF5,"P")),
                new BigDecimal(getPropertyValue(devRF4,"P")),
                new BigDecimal(getPropertyValue(devRF3,"P")),
                new BigDecimal(getPropertyValue(devRF2,"P")),
                new BigDecimal(getPropertyValue(devRF1,"P"))
                ));
        response.setHavcQ(NumberUtil.add(
                new BigDecimal(getPropertyValue(devRF5,"Q")),
                new BigDecimal(getPropertyValue(devRF4,"Q")),
                new BigDecimal(getPropertyValue(devRF3,"Q")),
                new BigDecimal(getPropertyValue(devRF2,"Q")),
                new BigDecimal(getPropertyValue(devRF1,"Q"))
                ));
        response.setHavcEpimportMonth(getValue(SubitemIndexEnum.ENERGY_SUBHVAC_TOTAL, currentMonths.get(YearMonth.now())));
        response.setOtherP(NumberUtil.add(
                new BigDecimal(getPropertyValue(dev4F6,"P")),
                new BigDecimal(getPropertyValue(dev2APEZ24,"P")),
                new BigDecimal(getPropertyValue(dev2APEZ23,"P")),
                new BigDecimal(getPropertyValue(dev2APEZ14,"P")),
                new BigDecimal(getPropertyValue(dev2APZ26,"P"))

                ));
        response.setOtherQ(NumberUtil.add(
                new BigDecimal(getPropertyValue(dev4F6,"Q")),
                new BigDecimal(getPropertyValue(dev2APEZ24,"Q")),
                new BigDecimal(getPropertyValue(dev2APEZ23,"Q")),
                new BigDecimal(getPropertyValue(dev2APEZ14,"Q")),
                new BigDecimal(getPropertyValue(dev2APZ26,"Q"))

                ));
        response.setOtherEpimportMonth(getValue(SubitemIndexEnum.OTHER_ELECTRICITY_CONSUMPTION, currentMonths.get(YearMonth.now())));
        return response;
    }
}
