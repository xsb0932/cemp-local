package com.landleaf.sdl.service;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.landleaf.bms.api.ProjectApi;
import com.landleaf.bms.api.dto.ProjectDetailsResponse;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.comm.sta.util.DateUtils;
import com.landleaf.comm.vo.CommonStaVO;
import com.landleaf.data.api.device.DeviceHistoryApi;
import com.landleaf.data.api.device.dto.BasePRequest;
import com.landleaf.data.api.device.dto.ZnbPResponse;
import com.landleaf.energy.api.SubitemApi;
import com.landleaf.energy.enums.SubitemIndexEnum;
import com.landleaf.energy.request.*;
import com.landleaf.energy.response.SubitemYearRatioResoponse;
import com.landleaf.redis.RedisUtils;
import com.landleaf.sdl.dal.mapper.*;
import com.landleaf.sdl.domain.entity.ProjectStaDeviceElectricityHourEntity;
import com.landleaf.sdl.domain.entity.ProjectStaSubareaDayEntity;
import com.landleaf.sdl.domain.enums.SDLConstants;
import com.landleaf.sdl.domain.response.SDLCurrentDataPage1Response;
import com.landleaf.sdl.domain.response.SDLCurrentDataPage2Response;
import com.landleaf.sdl.domain.response.SDLOverviewPage1Response;
import com.landleaf.sdl.domain.response.SDLOverviewPage2Response;
import com.landleaf.sdl.domain.vo.SDLCommonStaVO;
import com.landleaf.sdl.domain.vo.SDLEnergyMonthRatio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.landleaf.redis.constance.KeyConstance.DEVICE_CURRENT_STATUS_V1;
import static com.landleaf.sdl.domain.enums.SDLConstants.*;

/**
 * SDLScreenService
 *
 * @author xushibai
 * @since 2023/11/29
 **/
@Service
@RequiredArgsConstructor
public class SDLScreenService {

    private final DeviceHistoryApi deviceHistoryApi;
    private final SubitemApi subitemApi;
    private final RedisUtils redisUtils;
    private final ProjectApi projectApi;
    private final ProjectStaSubareaDayMapper projectStaSubareaDayMapper;
    private final ProjectStaDeviceElectricityDayMapper projectStaDeviceElectricityDayMapper;
    private final ProjectStaDeviceElectricityHourMapper projectStaDeviceElectricityHourMapper;
    private final ProjectStaDeviceElectricityMonthMapper projectStaDeviceElectricityMonthMapper;
    private final ProjectStaDeviceElectricityYearMapper projectStaDeviceElectricityYearMapper;


    private final String[] X_LIST_YEAR_DESC = new String[]{"1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月"};
    private final String[] X_LIST_YEAR = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"};

    private BigDecimal getValue(SubitemIndexEnum key, Map<SubitemIndexEnum, BigDecimal> values) {
        if (values == null) {
            return null;
        }
        return values.get(key);
    }

    private LocalDate[] getCurrentMonthDay(LocalDate now) {
        List<LocalDate> monthDays = new ArrayList<>();
        int days = now.lengthOfMonth();
        for (int i = 1; i <= days; i++) {
            monthDays.add(LocalDate.of(now.getYear(), now.getMonth(), i));
        }
        return monthDays.toArray(new LocalDate[0]);
    }

    private YearMonth[] getCurrentYearMonth(YearMonth now) {
        List<YearMonth> yearMonths = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            yearMonths.add(YearMonth.of(now.getYear(), i));
        }
        return yearMonths.toArray(new YearMonth[0]);
    }

    public SDLOverviewPage1Response overview1(String projectBizId) {
        SDLOverviewPage1Response overviewResponse = new SDLOverviewPage1Response();
        Response<ProjectDetailsResponse> projectDetails = projectApi.getProjectDetails(projectBizId);
        BigDecimal area = projectDetails.getResult().getArea();

        // 当年使用量查询
        IndexYearRequest indexYearRequest = new IndexYearRequest();
        indexYearRequest.setYears(new Year[]{Year.now()});
        indexYearRequest.setProjectBizId(projectBizId);
        SubitemIndexEnum[] currentYearIndices = new SubitemIndexEnum[]{
                SubitemIndexEnum.TOTAL_ELECTRICITY_CONSUMPTION_OF_ALL_LOADS,
                // 当年购网电量
                SubitemIndexEnum.PURCHASE_NETWORK_ELECTRICITY,
                // 当年上网电量
                SubitemIndexEnum.ON_GRID_ENERGY,
                // 当年光伏发电量
                SubitemIndexEnum.PHOTOVOLTAIC_POWER_GENERATION,
                // 当年储能放电量
                SubitemIndexEnum.ENERGY_STORAGE_AND_DISCHARGE_CAPACITY,
                // 当年充电桩充电量
                SubitemIndexEnum.ELECTRIC_QUANTITY_OF_CHARGING_STATION,
                // 累计二氧化碳减少量
                SubitemIndexEnum.CARBON_DIOXIDE_EMISSIONS,
                // 充电桩当年收益
                SubitemIndexEnum.INCOME_OF_CHARGING_STATION,
                // 当年暖通用电量
                SubitemIndexEnum.WARM_UNIVERSAL_POWER,
                // 当年照明用电量
                SubitemIndexEnum.LIGHTING_POWER_CONSUMPTION,
                // 当年插座用电量
                SubitemIndexEnum.SOCKET_POWER_CONSUMPTION,
                // 当年其他用电量
                SubitemIndexEnum.OTHER_ELECTRICITY_CONSUMPTION,
                //电梯
                SubitemIndexEnum.ENERGY_SUBELEVATOR_TOTAL,
                //热水
                SubitemIndexEnum.ENERGY_HEATWATER_TOTAL,
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
                // 购网电量
                SubitemIndexEnum.PURCHASE_NETWORK_ELECTRICITY,
                // 市电
                SubitemIndexEnum.PURCHASE_NETWORK_ELECTRICITY,
                SubitemIndexEnum.ELECTRIC_QUANTITY_OF_CHARGING_STATION,
                SubitemIndexEnum.WARM_UNIVERSAL_POWER,
                SubitemIndexEnum.LIGHTING_POWER_CONSUMPTION,
                SubitemIndexEnum.SOCKET_POWER_CONSUMPTION,
                SubitemIndexEnum.OTHER_ELECTRICITY_CONSUMPTION,

        };
        indexMonthRequest.setIndices(currentMonthIndices);
        Response<Map<YearMonth, Map<SubitemIndexEnum, BigDecimal>>> mapMonthResponse = subitemApi.searchDataByMonth(indexMonthRequest);
        Map<YearMonth, Map<SubitemIndexEnum, BigDecimal>> currentMonths = mapMonthResponse.getResult();

        //去年 当月
        YearMonth[] lastYearMonth = getCurrentYearMonth(yearMonth.minusYears(1));
        indexMonthRequest.setMonths(lastYearMonth);
        Response<Map<YearMonth, Map<SubitemIndexEnum, BigDecimal>>> lastYearMonthsResponse = subitemApi.searchDataByMonth(indexMonthRequest);
        Map<YearMonth, Map<SubitemIndexEnum, BigDecimal>> lastYearMonths = lastYearMonthsResponse.getResult();

        // 累计使用量查询
        IndexCumulativeRequest indexCumulativeRequest = new IndexCumulativeRequest();
        indexCumulativeRequest.setProjectBizId(projectBizId);
        SubitemIndexEnum[] cumulativeIndices = new SubitemIndexEnum[]{
                // 累计光伏发电量
                SubitemIndexEnum.PHOTOVOLTAIC_POWER_GENERATION,
                // 累计储能放电量
                SubitemIndexEnum.ENERGY_STORAGE_AND_DISCHARGE_CAPACITY,
                // 累计充电桩充电量
                SubitemIndexEnum.ELECTRIC_QUANTITY_OF_CHARGING_STATION,
                // 累计二氧化碳减少量
                SubitemIndexEnum.CARBON_DIOXIDE_REDUCTION,
                // 光伏累计收益
                SubitemIndexEnum.PHOTOVOLTAIC_REVENUE,
                // 储能累计收益
                SubitemIndexEnum.ENERGY_STORAGE_REVENUE,
                // 充电桩累计收益
                SubitemIndexEnum.INCOME_OF_CHARGING_STATION,
        };
        indexCumulativeRequest.setIndices(cumulativeIndices);
        Response<Map<SubitemIndexEnum, BigDecimal>> cumulativeResponse = subitemApi.searchDataByCumulative(indexCumulativeRequest);
        Map<SubitemIndexEnum, BigDecimal> cumulative = cumulativeResponse.getResult();


        // 当年购网电量
        overviewResponse.setElectricityPccEnergyUsageCurrentYear(getValue(SubitemIndexEnum.PURCHASE_NETWORK_ELECTRICITY, currentYear));
        // 当年上网电量
        overviewResponse.setElectricityPccEnergyProductionCurrentYear(getValue(SubitemIndexEnum.TOTAL_ELECTRICITY_CONSUMPTION_OF_ALL_LOADS, currentYear));
        // 当年光伏发电量
        overviewResponse.setElectricityPvEnergyProductionCurrentYear(getValue(SubitemIndexEnum.PHOTOVOLTAIC_POWER_GENERATION, currentYear));
        // 累计光伏发电量
        overviewResponse.setElectricityPvEnergyProductionTotal(getValue(SubitemIndexEnum.PHOTOVOLTAIC_POWER_GENERATION, cumulative));
        // 当年储能放电量
        overviewResponse.setElectricityStorageEnergyUsageCurrentYear(getValue(SubitemIndexEnum.ENERGY_STORAGE_AND_DISCHARGE_CAPACITY, currentYear));
        // 累计储能放电量
        overviewResponse.setElectricityStorageEnergyUsageTotal(getValue(SubitemIndexEnum.ENERGY_STORAGE_AND_DISCHARGE_CAPACITY, cumulative));
        // 当年充电桩充电量
        overviewResponse.setElectricitySubChargeEnergyCurrentYear(getValue(SubitemIndexEnum.ELECTRIC_QUANTITY_OF_CHARGING_STATION, currentYear));
        // 累计充电桩充电量
        overviewResponse.setElectricitySubChargeEnergyTotal(getValue(SubitemIndexEnum.ELECTRIC_QUANTITY_OF_CHARGING_STATION, cumulative));
        // 当年二氧化碳排放量
        overviewResponse.setCarbonTotalCurrentYear(NumberUtil.mul(getValue(SubitemIndexEnum.CARBON_DIOXIDE_EMISSIONS, currentYear), BigDecimal.valueOf(1000)));
        // 累计二氧化碳减少量
        overviewResponse.setCarbonPvReductionTotal(NumberUtil.mul(getValue(SubitemIndexEnum.CARBON_DIOXIDE_REDUCTION, cumulative), BigDecimal.valueOf(1000)));

        //环境
        Map<Object, Object> dcsgq4f = redisUtils.hmget(DEVICE_CURRENT_STATUS_V1 + AIR_DCSGQ4F);
        Map<Object, Object> dcsqt1f = redisUtils.hmget(DEVICE_CURRENT_STATUS_V1 + AIR_DCSQT1F);
        Map<Object, Object> dcsbg2f = redisUtils.hmget(DEVICE_CURRENT_STATUS_V1 + AIR_DCSBG2F);
        Map<Object, Object> dcsgd3f = redisUtils.hmget(DEVICE_CURRENT_STATUS_V1 + AIR_DCSGD3F);

        // PM25
        overviewResponse.setPm25( NumberUtil.add(getPropertyValue(dcsgq4f,"pm25"),getPropertyValue(dcsqt1f,"pm25"),getPropertyValue(dcsbg2f,"pm25"),getPropertyValue(dcsgd3f,"pm25")).divide(BigDecimal.valueOf(4),2,RoundingMode.HALF_UP));
        // CO2
        overviewResponse.setCo2( NumberUtil.add(getPropertyValue(dcsgq4f,"CO2"),getPropertyValue(dcsqt1f,"CO2"),getPropertyValue(dcsbg2f,"CO2"),getPropertyValue(dcsgd3f,"CO2")).divide(BigDecimal.valueOf(4),2,RoundingMode.HALF_UP));
        // 甲醛 - 写死
        overviewResponse.setFormaldehyde(new BigDecimal(0.02));
        // 温度
        overviewResponse.setTemp(NumberUtil.add(getPropertyValue(dcsgq4f,"Temperature"),getPropertyValue(dcsqt1f,"Temperature"),getPropertyValue(dcsbg2f,"Temperature"),getPropertyValue(dcsgd3f,"Temperature")).divide(BigDecimal.valueOf(4),2,RoundingMode.HALF_UP));
        // 湿度
        overviewResponse.setHum(NumberUtil.add(getPropertyValue(dcsgq4f,"Humidity"),getPropertyValue(dcsqt1f,"Humidity"),getPropertyValue(dcsbg2f,"Humidity"),getPropertyValue(dcsgd3f,"Humidity")).divide(BigDecimal.valueOf(4),2,RoundingMode.HALF_UP));
        // 当年实际能耗强度 // 项目报表月+ 日+ 小时 的关口用电量/项目面积。 2023/12/26 修改为新的计算公式
        // overviewResponse.setBuildingEnergyActual(NumberUtil.div(getValue(SubitemIndexEnum.PURCHASE_NETWORK_ELECTRICITY, currentYear),area));
        // 上海市超低能耗建筑 固定： 60
        overviewResponse.setBuildingEnergyStandard(BigDecimal.valueOf(60));


        //当年负荷使用结构
//        SubitemRequest subitemRequest = new SubitemRequest(projectBizId, SDL_TENANT_ID);
//        Response<SubitemYearRatioResoponse> response = subitemApi.getSubitemYearRatio(subitemRequest);
//        overviewResponse.setSubitemYearRatioData(response.getResult());

        // 当年负荷使用结构
        SDLOverviewPage1Response.ElectricityLoadStructure loadStructure = new SDLOverviewPage1Response.ElectricityLoadStructure();
        loadStructure.setCharge(getValue(SubitemIndexEnum.ELECTRIC_QUANTITY_OF_CHARGING_STATION, currentYear));
        loadStructure.setHvac(getValue(SubitemIndexEnum.WARM_UNIVERSAL_POWER, currentYear));
        loadStructure.setLight(getValue(SubitemIndexEnum.LIGHTING_POWER_CONSUMPTION, currentYear));
        loadStructure.setSocket(getValue(SubitemIndexEnum.SOCKET_POWER_CONSUMPTION, currentYear));
        loadStructure.setOther(getValue(SubitemIndexEnum.OTHER_ELECTRICITY_CONSUMPTION, currentYear));
        overviewResponse.setElectricityLoadStructureCurrentYear(loadStructure);
        //  2023/12/26 修改为新的计算公式 暖通 （供暖 + 供冷） + 照明 + 生活热水 + 电梯 - 光伏（可再生）
        BigDecimal totalEnergy = NumberUtil.add(loadStructure.getHvac(),
                loadStructure.getLight(),
                getValue(SubitemIndexEnum.ENERGY_SUBELEVATOR_TOTAL, currentYear),
                getValue(SubitemIndexEnum.ENERGY_HEATWATER_TOTAL, currentYear)
                ).subtract(getValue(SubitemIndexEnum.PHOTOVOLTAIC_POWER_GENERATION, currentYear));
        overviewResponse.setBuildingEnergyActual(NumberUtil.div(totalEnergy,area));

        // 月度负荷使用结构
        SDLOverviewPage1Response.ElectricityLoadStructureDataSet monthElectricityLoadStructure = new SDLOverviewPage1Response.ElectricityLoadStructureDataSet();
        SDLOverviewPage1Response.ElectricityLoadStructureDataSet lastYearMonthElectricityLoadStructure = new SDLOverviewPage1Response.ElectricityLoadStructureDataSet();
        SDLOverviewPage1Response.ElectricityLoadStructureDataSet yoyElectricityLoadStructure = new SDLOverviewPage1Response.ElectricityLoadStructureDataSet();
        // 当年
        if (CollUtil.isNotEmpty(currentMonths)) {
            currentMonths.forEach((key, value) -> {
                monthElectricityLoadStructure.getCharge().add(getValue(SubitemIndexEnum.ELECTRIC_QUANTITY_OF_CHARGING_STATION, value));
                monthElectricityLoadStructure.getHvac().add(getValue(SubitemIndexEnum.WARM_UNIVERSAL_POWER, value));
                monthElectricityLoadStructure.getLight().add(getValue(SubitemIndexEnum.LIGHTING_POWER_CONSUMPTION, value));
                monthElectricityLoadStructure.getSocket().add(getValue(SubitemIndexEnum.SOCKET_POWER_CONSUMPTION, value));
                monthElectricityLoadStructure.getOther().add(getValue(SubitemIndexEnum.OTHER_ELECTRICITY_CONSUMPTION, value));
                monthElectricityLoadStructure.getX().add(key.getMonthValue());
            });
        } else {
            for (YearMonth month : currentYearMonth) {
                monthElectricityLoadStructure.getCharge().add(BigDecimal.ZERO);
                monthElectricityLoadStructure.getHvac().add(BigDecimal.ZERO);
                monthElectricityLoadStructure.getLight().add(BigDecimal.ZERO);
                monthElectricityLoadStructure.getSocket().add(BigDecimal.ZERO);
                monthElectricityLoadStructure.getOther().add(BigDecimal.ZERO);
                monthElectricityLoadStructure.getX().add(month.getMonthValue());
            }
        }
        //去年
        if (CollUtil.isNotEmpty(lastYearMonths)) {
            lastYearMonths.forEach((key, value) -> {
                lastYearMonthElectricityLoadStructure.getCharge().add(getValue(SubitemIndexEnum.ELECTRIC_QUANTITY_OF_CHARGING_STATION, value));
                lastYearMonthElectricityLoadStructure.getHvac().add(getValue(SubitemIndexEnum.WARM_UNIVERSAL_POWER, value));
                lastYearMonthElectricityLoadStructure.getLight().add(getValue(SubitemIndexEnum.LIGHTING_POWER_CONSUMPTION, value));
                lastYearMonthElectricityLoadStructure.getSocket().add(getValue(SubitemIndexEnum.SOCKET_POWER_CONSUMPTION, value));
                lastYearMonthElectricityLoadStructure.getOther().add(getValue(SubitemIndexEnum.OTHER_ELECTRICITY_CONSUMPTION, value));
                lastYearMonthElectricityLoadStructure.getX().add(key.getMonthValue());
            });
        } else {
            for (YearMonth month : lastYearMonth) {
                lastYearMonthElectricityLoadStructure.getCharge().add(BigDecimal.ZERO);
                lastYearMonthElectricityLoadStructure.getHvac().add(BigDecimal.ZERO);
                lastYearMonthElectricityLoadStructure.getLight().add(BigDecimal.ZERO);
                lastYearMonthElectricityLoadStructure.getSocket().add(BigDecimal.ZERO);
                lastYearMonthElectricityLoadStructure.getOther().add(BigDecimal.ZERO);
                lastYearMonthElectricityLoadStructure.getX().add(month.getMonthValue());
            }
        }
        //同比
        yoyElectricityLoadStructure.setX(monthElectricityLoadStructure.getX());
        yoyElectricityLoadStructure.setCharge(getYoyList(monthElectricityLoadStructure.getCharge(),lastYearMonthElectricityLoadStructure.getCharge()));
        yoyElectricityLoadStructure.setHvac(getYoyList(monthElectricityLoadStructure.getHvac(),lastYearMonthElectricityLoadStructure.getHvac()));
        yoyElectricityLoadStructure.setLight(getYoyList(monthElectricityLoadStructure.getLight(),lastYearMonthElectricityLoadStructure.getLight()));
        yoyElectricityLoadStructure.setSocket(getYoyList(monthElectricityLoadStructure.getSocket(),lastYearMonthElectricityLoadStructure.getSocket()));
        yoyElectricityLoadStructure.setOther(getYoyList(monthElectricityLoadStructure.getOther(),lastYearMonthElectricityLoadStructure.getOther()));

        overviewResponse.setMonthElectricityLoadStructure(monthElectricityLoadStructure);
        overviewResponse.setLastYearMonthElectricityLoadStructure(lastYearMonthElectricityLoadStructure);
        overviewResponse.setYoyMonthElectricityLoadStructure(yoyElectricityLoadStructure);

        //当年电源结构月趋势
        SDLEnergyMonthRatio monthRatio = new SDLEnergyMonthRatio();
        List<CommonStaVO> barChartData = new ArrayList<>();
        //当年电源结构月趋势 - 光伏发电
        CommonStaVO pv = new CommonStaVO(
                "光伏发电",
                Arrays.asList(X_LIST_YEAR),
                Arrays.stream(X_LIST_YEAR).map(month -> getValue(SubitemIndexEnum.PHOTOVOLTAIC_POWER_GENERATION, currentMonths.get(YearMonth.of(Year.now().getValue(), Integer.valueOf(month)))).toPlainString()).collect(Collectors.toList()));
        //当年电源结构月趋势 - 关口用电
        CommonStaVO pur = new CommonStaVO(
                "市电",
                Arrays.asList(X_LIST_YEAR),
                Arrays.stream(X_LIST_YEAR).map(month -> getValue(SubitemIndexEnum.PURCHASE_NETWORK_ELECTRICITY, currentMonths.get(YearMonth.of(Year.now().getValue(), Integer.valueOf(month)))).toPlainString()).collect(Collectors.toList()));
        barChartData.add(pv);
        barChartData.add(pur);
        monthRatio.setBarChartData(barChartData);
        overviewResponse.setEnergyMonthRatioData(monthRatio);

        // 光伏绿电占总用电比例 根据项目报表指标计算，光伏发电量 /（光伏发电量 + 关口购网电量），需要月 + 日 + 小时得到当年值。
        overviewResponse.setElePvRatio(overviewResponse.getElectricityPvEnergyProductionCurrentYear().divide(NumberUtil.add(overviewResponse.getElectricityPvEnergyProductionCurrentYear(),overviewResponse.getElectricityPccEnergyUsageCurrentYear()),3,RoundingMode.HALF_UP));

        // 直流用电比例 根据项目报表及设备报表计算。直流年用电量 = 项目报表光伏当年发电量 + DB-DC-FCS电表的当年用电量 - DB-DC-FCS电表的当年发电量；负荷总用电量取项目报表月+ 日 + 小时。
        BigDecimal val1 = overviewResponse.getElectricityPvEnergyProductionCurrentYear();   //项目报表光伏当年发电量

        DeviceDayRequest request = new DeviceDayRequest();
        request.setDay(LocalDate.now());
        request.setDeviceBizId(new String[]{ELE_DIR_DEVICE_DBDCFCS});
        Response<Map<String, BigDecimal>> result2 = subitemApi.searchDeviceEpimportYear(request);
        Response<Map<String, BigDecimal>> result3 = subitemApi.searchDeviceEpexportYear(request);
        BigDecimal val2 = result2.getResult().get(ELE_DIR_DEVICE_DBDCFCS);   //DB-DC-FCS电表的当年用电量
        BigDecimal val3 = result3.getResult().get(ELE_DIR_DEVICE_DBDCFCS);   //DB-DC-FCS电表的当年发电量
        BigDecimal total = val1.add(val2).subtract(val3);
        overviewResponse.setEleDirRatio(total.divide(overviewResponse.getElectricityPccEnergyProductionCurrentYear(),3,RoundingMode.HALF_UP));

        return overviewResponse;
    }

    private BigDecimal getYoyVal(BigDecimal thisYearVal, BigDecimal lastYearVal){
        if(thisYearVal.compareTo(BigDecimal.ZERO) == 0 || lastYearVal.compareTo(BigDecimal.ZERO) == 0){
            return BigDecimal.ZERO;
        }
        return thisYearVal.min(lastYearVal).divide(lastYearVal,2, RoundingMode.HALF_UP);

    }

    private List<BigDecimal> getYoyList(List<BigDecimal> thisYearList,List<BigDecimal> lastYearList ){
        List<BigDecimal> yoyList = new ArrayList<>();
        for (int i = 0; i < thisYearList.size(); i++) {
            yoyList.add(getYoyVal(thisYearList.get(i), lastYearList.get(i)));
        }
        return yoyList;
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

    public SDLCurrentDataPage1Response current1(String projectBizId) {
        SDLCurrentDataPage1Response current = new SDLCurrentDataPage1Response();

        // 用电量
        List<String> epimportBizDeviceIds = Arrays.asList(
                ELE_ALT_DEVICE_1GP01,
                ELE_ALT_DEVICE_1GP02,
                ELE_ALT_DEVICE_1GP03,
                ELE_DIR_DEVICE_DBDCFCS,
                ELE_DIR_DEVICE_DBDCCD,
                ELE_DIR_DEVICE_DC220,
                ELE_DIR_DEVICE_DBDCFAN,
                ELE_DIR_DEVICE_DBDC5FAIRCON);
        DeviceDayRequest deviceDayRequest = new DeviceDayRequest();
        deviceDayRequest.setDay(LocalDate.now());
        deviceDayRequest.setDeviceBizId(epimportBizDeviceIds.toArray(new String[0]));
        Map<String, BigDecimal> epimportData = subitemApi.searchDeviceEpimport(deviceDayRequest).getResult();
        // 发电量
        List<String> epexportBizDeviceIds = Arrays.asList(
                ELE_DIR_DEVICE_DBDCPV,
                ELE_DIR_DEVICE_DBDCFCS);
        deviceDayRequest.setDeviceBizId(epexportBizDeviceIds.toArray(new String[0]));
        //发电量
        Map<String, BigDecimal> epexportData = subitemApi.searchDeviceEpexport(deviceDayRequest).getResult();

        Map<Object, Object> gp01 = redisUtils.hmget(DEVICE_CURRENT_STATUS_V1 + ELE_ALT_DEVICE_1GP01);
        Map<Object, Object> gp02 = redisUtils.hmget(DEVICE_CURRENT_STATUS_V1 + ELE_ALT_DEVICE_1GP02);
        Map<Object, Object> gp03 = redisUtils.hmget(DEVICE_CURRENT_STATUS_V1 + ELE_ALT_DEVICE_1GP03);
        Map<Object, Object> fcs = redisUtils.hmget(DEVICE_CURRENT_STATUS_V1 + ELE_DIR_DEVICE_DBDCFCS);
        Map<Object, Object> dbdccn = redisUtils.hmget(DEVICE_CURRENT_STATUS_V1 + ELE_DIR_DEVICE_DBDCCN);
        Map<Object, Object> bms1 = redisUtils.hmget(DEVICE_CURRENT_STATUS_V1 + STORAGE_DEVICE_BMS1);
        Map<Object, Object> dbdcpv = redisUtils.hmget(DEVICE_CURRENT_STATUS_V1 + ELE_DIR_DEVICE_DBDCPV);
        Map<Object, Object> dbdccd = redisUtils.hmget(DEVICE_CURRENT_STATUS_V1 + ELE_DIR_DEVICE_DBDCCD);
        Map<Object, Object> dbdc220 = redisUtils.hmget(DEVICE_CURRENT_STATUS_V1 + ELE_DIR_DEVICE_DC220);
        Map<Object, Object> deviceDBDCFAN = redisUtils.hmget(DEVICE_CURRENT_STATUS_V1 + ELE_DIR_DEVICE_DBDCFAN);
        Map<Object, Object> deviceDBDC5FAIRCON = redisUtils.hmget(DEVICE_CURRENT_STATUS_V1 + ELE_DIR_DEVICE_DBDC5FAIRCON);
        // 电网 P: GP01 + GP02 + GP03电表有功功率
        current.setEleNetP(NumberUtil.add(
                new BigDecimal(getPropertyValue(gp01,"P")),
                new BigDecimal(getPropertyValue(gp02,"P")),
                new BigDecimal(getPropertyValue(gp03,"P"))
        ));

        // 电网Q: GP01 + GP02 + GP03电表无功功率
        current.setEleNetQ(NumberUtil.add(
                new BigDecimal(getPropertyValue(gp01,"Q")),
                new BigDecimal(getPropertyValue(gp01,"Q")),
                new BigDecimal(getPropertyValue(gp01,"Q"))
        ));

        // 电网 P + FCS直流电表
        current.setEleALtP(NumberUtil.add(current.getEleNetP(),new BigDecimal(getPropertyValue(fcs,"P"))));

        // 交流系统 Q: todo
        current.setEleALtQ(NumberUtil.add(current.getEleNetQ(),new BigDecimal(getPropertyValue(fcs,"Q"))));

        //储能总电压 BMS总压 ;储能SOC BMS SOC
        current.setStorageVol(new BigDecimal(getPropertyValue(bms1,"BatteryU")));
        current.setStorageSOC(new BigDecimal(getPropertyValue(bms1,"SOC")));
        // 储能P 储能回路电表的P 光伏P： 光伏回路电表的P 充电桩P: 充电回路电表的P 直流负荷P: 220 V直流母线电表的P
        current.setStorageP(new BigDecimal(getPropertyValue(dbdccn,"P")));
        current.setPvP(new BigDecimal(getPropertyValue(dbdcpv,"P")));
        current.setChargeP(new BigDecimal(getPropertyValue(dbdccd,"P")));
        current.setDbusP(NumberUtil.add(new BigDecimal(getPropertyValue(dbdc220,"P")),new BigDecimal(getPropertyValue(deviceDBDCFAN,"P")),new BigDecimal(getPropertyValue(deviceDBDC5FAIRCON,"P"))) );
        //直流母线 电压 ：FCS直流电表的电压
        current.setFcsVol(new BigDecimal(getPropertyValue(fcs,"Ua")));


        // 日使用量查询
        IndexDayRequest indexDayRequest = new IndexDayRequest();
        LocalDate localDate = LocalDate.now();
        LocalDate[] currentMonthDay = getCurrentMonthDay(localDate);
        indexDayRequest.setDays(currentMonthDay);
        indexDayRequest.setProjectBizId(projectBizId);
        SubitemIndexEnum[] currentDayIndices = new SubitemIndexEnum[]{
                // 购网电量
                SubitemIndexEnum.PURCHASE_NETWORK_ELECTRICITY,

        };
        indexDayRequest.setIndices(currentDayIndices);
        Response<Map<LocalDate, Map<SubitemIndexEnum, BigDecimal>>> mapDayResponse = subitemApi.searchDataByDay(indexDayRequest);
        Map<LocalDate, Map<SubitemIndexEnum, BigDecimal>> currentDays = mapDayResponse.getResult();

        //电网当日购电量： GP01 + GP02 + GP03电表当日电量的和
        //current.setEpimportTodayTotal(NumberUtil.add(epimportData.getOrDefault(ELE_ALT_DEVICE_1GP01,BigDecimal.ZERO),epimportData.getOrDefault(ELE_ALT_DEVICE_1GP02,BigDecimal.ZERO),epimportData.getOrDefault(ELE_ALT_DEVICE_1GP03,BigDecimal.ZERO)));
        // 当日购电量 取 项目购电量
        current.setEpimportTodayTotal(getValue(SubitemIndexEnum.PURCHASE_NETWORK_ELECTRICITY, currentDays.get(LocalDate.now())));

        //FCS系统柔性控制 当日用电量：FCS直流电表的当日用电量
        current.setEpimportTodayFCS(epimportData.getOrDefault(ELE_DIR_DEVICE_DBDCFCS,BigDecimal.ZERO));
        //FCS系统柔性控制 当日发电量： FCS直流电表的当日发电量
        current.setEpexportTodayFCS(epexportData.getOrDefault(ELE_DIR_DEVICE_DBDCFCS,BigDecimal.ZERO));
        //交流系统当日用电量： 电网当日购电量 + FCS直流电表的当日发电量 - FCS直流电表的当日用电量
        current.setEpimportTodayAlt(current.getEpimportTodayTotal().add(current.getEpexportTodayFCS()).subtract(current.getEpimportTodayFCS()));

        //current.setEpimportTodayTotal(current.getEpimportTodayTotal().add(current.getEpexportTodayFCS()).subtract(current.getEpimportTodayFCS()));
        //光伏当日发电量： 光伏回路电表的当日发电量
        current.setEpexportTodayPV(epexportData.getOrDefault(ELE_DIR_DEVICE_DBDCPV,BigDecimal.ZERO));
        //充电桩当日用电量： 充电回路电流的当日用电量
        current.setEpimportTodayCharge(epimportData.getOrDefault(ELE_DIR_DEVICE_DBDCCD,BigDecimal.ZERO));
        //直流负荷当日用电量： 220 V直流母线电表的当日用电量
        current.setEpimportTodayDbus(NumberUtil.add(epimportData.getOrDefault(ELE_DIR_DEVICE_DC220,BigDecimal.ZERO),epimportData.getOrDefault(ELE_DIR_DEVICE_DBDCFAN,BigDecimal.ZERO),epimportData.getOrDefault(ELE_DIR_DEVICE_DBDC5FAIRCON,BigDecimal.ZERO)));



        return current;
    }

    public SDLCurrentDataPage2Response current2(String projectBizId) {
        SDLCurrentDataPage2Response response = new SDLCurrentDataPage2Response();
        Map<Object, Object> deviceATE1PC1 = redisUtils.hmget(DEVICE_CURRENT_STATUS_V1 + ELE_ALT_DEVICE_ATE1PC1);
        Map<Object, Object> deviceATE1XKS = redisUtils.hmget(DEVICE_CURRENT_STATUS_V1 + ELE_ALT_DEVICE_ATE1XKS);
        Map<Object, Object> deviceAT1AL3 = redisUtils.hmget(DEVICE_CURRENT_STATUS_V1 + ELE_ALT_DEVICE_AT1AL3);
        Map<Object, Object> device1KL1 = redisUtils.hmget(DEVICE_CURRENT_STATUS_V1 + ELE_ALT_DEVICE_1KL1);
        Map<Object, Object> device1AL1 = redisUtils.hmget(DEVICE_CURRENT_STATUS_V1 + ELE_ALT_DEVICE_1AL1);
        Map<Object, Object> device1AL2 = redisUtils.hmget(DEVICE_CURRENT_STATUS_V1 + ELE_ALT_DEVICE_1AL2);
        Map<Object, Object> deviceAP1DT1 = redisUtils.hmget(DEVICE_CURRENT_STATUS_V1 + ELE_ALT_DEVICE_AP1DT1);
        Map<Object, Object> deviceAP1AL3 = redisUtils.hmget(DEVICE_CURRENT_STATUS_V1 + ELE_ALT_DEVICE_AP1AL3);
        Map<Object, Object> device3AL2 = redisUtils.hmget(DEVICE_CURRENT_STATUS_V1 + ELE_ALT_DEVICE_3AL2);
        Map<Object, Object> device3AL1 = redisUtils.hmget(DEVICE_CURRENT_STATUS_V1 + ELE_ALT_DEVICE_3AL1);
        Map<Object, Object> device3KL1 = redisUtils.hmget(DEVICE_CURRENT_STATUS_V1 + ELE_ALT_DEVICE_3KL1);
        Map<Object, Object> device3AL3 = redisUtils.hmget(DEVICE_CURRENT_STATUS_V1 + ELE_ALT_DEVICE_3AL3);
        Map<Object, Object> deviceAT3RD = redisUtils.hmget(DEVICE_CURRENT_STATUS_V1 + ELE_ALT_DEVICE_AT3RD);
        Map<Object, Object> device2KL1 = redisUtils.hmget(DEVICE_CURRENT_STATUS_V1 + ELE_ALT_DEVICE_2KL1);
        Map<Object, Object> device2AL1 = redisUtils.hmget(DEVICE_CURRENT_STATUS_V1 + ELE_ALT_DEVICE_2AL1);
        Map<Object, Object> deviceAP2WY1 = redisUtils.hmget(DEVICE_CURRENT_STATUS_V1 + ELE_ALT_DEVICE_AP2WY1);
        Map<Object, Object> deviceAT2WY1 = redisUtils.hmget(DEVICE_CURRENT_STATUS_V1 + ELE_ALT_DEVICE_AT2WY1);
        Map<Object, Object> device2AL3 = redisUtils.hmget(DEVICE_CURRENT_STATUS_V1 + ELE_ALT_DEVICE_2AL3);
        Map<Object, Object> device2AL2 = redisUtils.hmget(DEVICE_CURRENT_STATUS_V1 + ELE_ALT_DEVICE_2AL2);
        Map<Object, Object> deviceAP4SY1 = redisUtils.hmget(DEVICE_CURRENT_STATUS_V1 + ELE_ALT_DEVICE_AP4SY1);
        Map<Object, Object> device4AL2 = redisUtils.hmget(DEVICE_CURRENT_STATUS_V1 + ELE_ALT_DEVICE_4AL2);
        Map<Object, Object> device4AL1 = redisUtils.hmget(DEVICE_CURRENT_STATUS_V1 + ELE_ALT_DEVICE_4AL1);
        Map<Object, Object> device4KL1 = redisUtils.hmget(DEVICE_CURRENT_STATUS_V1 + ELE_ALT_DEVICE_4KL1);
        Map<Object, Object> deviceAT4SY1 = redisUtils.hmget(DEVICE_CURRENT_STATUS_V1 + ELE_ALT_DEVICE_AT4SY1);
        Map<Object, Object> deviceAT4HC1 = redisUtils.hmget(DEVICE_CURRENT_STATUS_V1 + ELE_ALT_DEVICE_AT4HC1);
        Map<Object, Object> deviceAT4DT1 = redisUtils.hmget(DEVICE_CURRENT_STATUS_V1 + ELE_ALT_DEVICE_AT4DT1);
        Map<Object, Object> device4AL3 = redisUtils.hmget(DEVICE_CURRENT_STATUS_V1 + ELE_ALT_DEVICE_4AL3);
        Map<Object, Object> deviceRAL1 = redisUtils.hmget(DEVICE_CURRENT_STATUS_V1 + ELE_ALT_DEVICE_RAL1);
        Map<Object, Object> deviceRKKL1 = redisUtils.hmget(DEVICE_CURRENT_STATUS_V1 + ELE_ALT_DEVICE_RKKL1);
        Map<Object, Object> deviceRKL1 = redisUtils.hmget(DEVICE_CURRENT_STATUS_V1 + ELE_ALT_DEVICE_RKL1);
        Map<Object, Object> device1GP03 = redisUtils.hmget(DEVICE_CURRENT_STATUS_V1 + ELE_ALT_DEVICE_1GP03);
        Map<Object, Object> device1GP02 = redisUtils.hmget(DEVICE_CURRENT_STATUS_V1 + ELE_ALT_DEVICE_1GP02);
        Map<Object, Object> device1GP01 = redisUtils.hmget(DEVICE_CURRENT_STATUS_V1 + ELE_ALT_DEVICE_1GP01);

        Map<Object, Object> deviceDBDC1FDIS = redisUtils.hmget(DEVICE_CURRENT_STATUS_V1 + ELE_DIR_DEVICE_DBDC1FDIS);
        Map<Object, Object> deviceDBDCFAN = redisUtils.hmget(DEVICE_CURRENT_STATUS_V1 + ELE_DIR_DEVICE_DBDCFAN);
        Map<Object, Object> deviceDBDC1FSOCK = redisUtils.hmget(DEVICE_CURRENT_STATUS_V1 + ELE_DIR_DEVICE_DBDC1FSOCK);
        Map<Object, Object> deviceDBDC3FDIS = redisUtils.hmget(DEVICE_CURRENT_STATUS_V1 + ELE_DIR_DEVICE_DBDC3FDIS);
        Map<Object, Object> deviceDBDC2FDIS = redisUtils.hmget(DEVICE_CURRENT_STATUS_V1 + ELE_DIR_DEVICE_DBDC2FDIS);
        Map<Object, Object> deviceDBDC4FDIS = redisUtils.hmget(DEVICE_CURRENT_STATUS_V1 + ELE_DIR_DEVICE_DBDC4FDIS);
        Map<Object, Object> deviceDBDC5FLIGHT = redisUtils.hmget(DEVICE_CURRENT_STATUS_V1 + ELE_DIR_DEVICE_DBDC5FLIGHT);
        Map<Object, Object> deviceDBDC5FAIRCON = redisUtils.hmget(DEVICE_CURRENT_STATUS_V1 + ELE_DIR_DEVICE_DBDC5FAIRCON);

        //1层交流有功功率
        response.setAltl1P(NumberUtil.add(
                new BigDecimal(getPropertyValue(deviceATE1PC1,"P")),
                new BigDecimal(getPropertyValue(deviceATE1XKS,"P")),
                new BigDecimal(getPropertyValue(deviceAT1AL3,"P")),
                new BigDecimal(getPropertyValue(device1KL1,"P")),
                new BigDecimal(getPropertyValue(device1AL1,"P")),
                new BigDecimal(getPropertyValue(device1AL2,"P")),
                new BigDecimal(getPropertyValue(deviceAP1DT1,"P")),
                new BigDecimal(getPropertyValue(deviceAP1AL3,"P"))
        ));
        //1层交流无功功率
        response.setAltl1Q(NumberUtil.add(
                new BigDecimal(getPropertyValue(deviceATE1PC1,"Q")),
                new BigDecimal(getPropertyValue(deviceATE1XKS,"Q")),
                new BigDecimal(getPropertyValue(deviceAT1AL3,"Q")),
                new BigDecimal(getPropertyValue(device1KL1,"Q")),
                new BigDecimal(getPropertyValue(device1AL1,"Q")),
                new BigDecimal(getPropertyValue(device1AL2,"Q")),
                new BigDecimal(getPropertyValue(deviceAP1DT1,"Q")),
                new BigDecimal(getPropertyValue(deviceAP1AL3,"Q"))
        ));
        //2层交流有功功率
        response.setAltl2P(NumberUtil.add(
                new BigDecimal(getPropertyValue(device2KL1,"P")),
                new BigDecimal(getPropertyValue(device2AL1,"P")),
                new BigDecimal(getPropertyValue(deviceAP2WY1,"P")),
                new BigDecimal(getPropertyValue(deviceAT2WY1,"P")),
                new BigDecimal(getPropertyValue(device2AL3,"P")),
                new BigDecimal(getPropertyValue(device2AL2,"P"))

        ));
        //2层交流无功功率
        response.setAltl2Q(NumberUtil.add(
                new BigDecimal(getPropertyValue(device2KL1,"Q")),
                new BigDecimal(getPropertyValue(device2AL1,"Q")),
                new BigDecimal(getPropertyValue(deviceAP2WY1,"Q")),
                new BigDecimal(getPropertyValue(deviceAT2WY1,"Q")),
                new BigDecimal(getPropertyValue(device2AL3,"Q")),
                new BigDecimal(getPropertyValue(device2AL2,"Q"))

        ));
        //3层交流有功功率
        response.setAltl3P(NumberUtil.add(
                new BigDecimal(getPropertyValue(device3AL2,"P")),
                new BigDecimal(getPropertyValue(device3AL1,"P")),
                new BigDecimal(getPropertyValue(device3KL1,"P")),
                new BigDecimal(getPropertyValue(device3AL3,"P")),
                new BigDecimal(getPropertyValue(deviceAT3RD,"P"))

        ));
        //3层交流无功功率
        response.setAltl3Q(NumberUtil.add(
                new BigDecimal(getPropertyValue(device3AL2,"Q")),
                new BigDecimal(getPropertyValue(device3AL1,"Q")),
                new BigDecimal(getPropertyValue(device3KL1,"Q")),
                new BigDecimal(getPropertyValue(device3AL3,"Q")),
                new BigDecimal(getPropertyValue(deviceAT3RD,"Q"))

        ));
        //4层交流有功功率
        response.setAltl4P(NumberUtil.add(
                new BigDecimal(getPropertyValue(deviceAP4SY1,"P")),
                new BigDecimal(getPropertyValue(device4AL2,"P")),
                new BigDecimal(getPropertyValue(device4AL1,"P")),
                new BigDecimal(getPropertyValue(device4KL1,"P")),
                new BigDecimal(getPropertyValue(deviceAT4SY1,"P")),
                new BigDecimal(getPropertyValue(deviceAT4HC1,"P")),
                new BigDecimal(getPropertyValue(deviceAT4DT1,"P")),
                new BigDecimal(getPropertyValue(device4AL3,"P"))
        ));
        //1层交流无功功率
        response.setAltl4Q(NumberUtil.add(
                new BigDecimal(getPropertyValue(deviceAP4SY1,"Q")),
                new BigDecimal(getPropertyValue(device4AL2,"Q")),
                new BigDecimal(getPropertyValue(device4AL1,"Q")),
                new BigDecimal(getPropertyValue(device4KL1,"Q")),
                new BigDecimal(getPropertyValue(deviceAT4SY1,"Q")),
                new BigDecimal(getPropertyValue(deviceAT4HC1,"Q")),
                new BigDecimal(getPropertyValue(deviceAT4DT1,"Q")),
                new BigDecimal(getPropertyValue(device4AL3,"Q"))
        ));
        //屋面交流有功功率
        response.setAltrfP(NumberUtil.add(
                new BigDecimal(getPropertyValue(deviceRAL1,"P")),
                new BigDecimal(getPropertyValue(deviceRKKL1,"P")),
                new BigDecimal(getPropertyValue(deviceRKL1,"P"))
        ));
        //屋面交流无功功率
        response.setAltrfQ(NumberUtil.add(
                new BigDecimal(getPropertyValue(deviceRAL1,"Q")),
                new BigDecimal(getPropertyValue(deviceRKKL1,"Q")),
                new BigDecimal(getPropertyValue(deviceRKL1,"Q"))
        ));

        //1层直流 有功功率
        response.setDirl1P(NumberUtil.add(
                new BigDecimal(getPropertyValue(deviceDBDC1FDIS,"P")),
                new BigDecimal(getPropertyValue(deviceDBDCFAN,"P")),
                new BigDecimal(getPropertyValue(deviceDBDC1FSOCK,"P"))
        ));
        //2层直流 有功功率
        response.setDirl2P(NumberUtil.add(
                new BigDecimal(getPropertyValue(deviceDBDC2FDIS,"P"))
        ));
        //3层直流 有功功率
        response.setDirl3P(NumberUtil.add(
                new BigDecimal(getPropertyValue(deviceDBDC3FDIS,"P"))
        ));
        //4层直流 有功功率
        response.setDirl4P(NumberUtil.add(
                new BigDecimal(getPropertyValue(deviceDBDC4FDIS,"P"))
        ));
        //屋面直流有功功率
        response.setDirrfP(NumberUtil.add(
                new BigDecimal(getPropertyValue(deviceDBDC5FLIGHT,"P")),
                new BigDecimal(getPropertyValue(deviceDBDC5FAIRCON,"P"))
        ));
        //屋面直流无功功率
        response.setDirrfQ(NumberUtil.add(
                new BigDecimal(getPropertyValue(deviceDBDC5FLIGHT,"Q")),
                new BigDecimal(getPropertyValue(deviceDBDC5FAIRCON,"Q"))
        ));
        return response;
    }

    private SDLCommonStaVO dapData(String projectId){
        SDLCommonStaVO dap = new SDLCommonStaVO();
        LocalDateTime time = LocalDateTime.now();
        String now = String.format("%s-%s-%s 00:00:00",time.getYear(),String.format("%02d",time.getMonthValue()),String.format("%02d",time.getDayOfMonth()));
        List<ProjectStaDeviceElectricityHourEntity> result =  projectStaDeviceElectricityHourMapper.selectList(new LambdaQueryWrapper<ProjectStaDeviceElectricityHourEntity>()
                .eq(ProjectStaDeviceElectricityHourEntity::getBizProjectId,projectId)
                .eq(ProjectStaDeviceElectricityHourEntity::getStaTime,now)
                .eq(ProjectStaDeviceElectricityHourEntity::getBizDeviceId,Arrays.asList(new String[]{ELE_ALT_DEVICE_1GP01,ELE_ALT_DEVICE_1GP02,ELE_ALT_DEVICE_1GP03,ELE_DIR_DEVICE_DBDCFCS}))
        );
        Map<String,Map<String,String>> gDetailMap = new HashMap<>();
        Map<String,List<ProjectStaDeviceElectricityHourEntity>> gresult = result.stream().collect(Collectors.groupingBy(ProjectStaDeviceElectricityHourEntity::getBizDeviceId));
        gresult.forEach(new BiConsumer<String, List<ProjectStaDeviceElectricityHourEntity>>() {
            @Override
            public void accept(String bizDeviceId, List<ProjectStaDeviceElectricityHourEntity> hourEntities) {
                //todo 有功功率 怎么取？？？？
                hourEntities.stream().collect(Collectors.toMap(ProjectStaDeviceElectricityHourEntity::getHour,ProjectStaDeviceElectricityHourEntity::getEnergymeterEpexportTotal));

                gDetailMap.put(bizDeviceId,null);
            }
        });
        String[] xlist = new String[]{"00","01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12","13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23"};
        CommonStaVO aData = new CommonStaVO(
                "交流负载",
                Arrays.asList(xlist),
                Arrays.stream(xlist).map(new Function<String, String>() {
                    @Override
                    public String apply(String s) {
                        return null;
                    }
                }).collect(Collectors.toList())

        );
        return dap;
    }

    //获取设备当年负荷用电量
    private BigDecimal getDevicePYear(String bizDeviceId){
        LocalDate ld = LocalDate.now();
        BigDecimal pyear = projectStaDeviceElectricityMonthMapper.getPTotalYear(bizDeviceId,String.valueOf(ld.getYear()));
        pyear = pyear == null? BigDecimal.ZERO : pyear;
        return pyear.add(getDevicePMonth(bizDeviceId));
    }

    //获取设备当月负荷用电量
    private BigDecimal getDevicePMonth(String bizDeviceId){
        LocalDate ld = LocalDate.now();
        List<BigDecimal> pMonths =  projectStaDeviceElectricityMonthMapper.getPTotalMonth(bizDeviceId,String.valueOf(ld.getYear()),String.valueOf(ld.getMonthValue()),String.valueOf(ld.getDayOfMonth()));
        if(pMonths != null &&pMonths.size() > 0){
            return pMonths.stream().filter(Objects::nonNull).reduce(BigDecimal.ZERO,BigDecimal::add);
        }else{
            return BigDecimal.ZERO;
        }

    }

    private SDLCommonStaVO getPSubitem(Map<SubitemIndexEnum, BigDecimal> currentYear,Map<YearMonth, Map<SubitemIndexEnum, BigDecimal>> currentMonths){
        List<CommonStaVO> data = new ArrayList<>();
        //String[] xlist = new String[]{"充电桩","暖通","照明","插座","其他"};
        String[] xlist = new String[]{"电梯","插座","暖通","照明","热水"};
        //当月
        List<BigDecimal> pbMonth = new ArrayList<>();
        YearMonth yearMonth = YearMonth.now();
        //电梯
        pbMonth.add(getValue(SubitemIndexEnum.ENERGY_SUBELEVATOR_TOTAL, currentMonths.get(yearMonth)));
        //插座
        pbMonth.add(getValue(SubitemIndexEnum.SOCKET_POWER_CONSUMPTION, currentMonths.get(yearMonth)));
        //暖通
        pbMonth.add(getValue(SubitemIndexEnum.WARM_UNIVERSAL_POWER, currentMonths.get(yearMonth)));
        //照明
        pbMonth.add(getValue(SubitemIndexEnum.LIGHTING_POWER_CONSUMPTION, currentMonths.get(yearMonth)));
        //热水
        pbMonth.add(getValue(SubitemIndexEnum.ENERGY_HEATWATER_TOTAL, currentMonths.get(yearMonth)));
//        pbMonth.add(getValue(SubitemIndexEnum.ELECTRIC_QUANTITY_OF_CHARGING_STATION, currentMonths.get(yearMonth)));
//        pbMonth.add(getValue(SubitemIndexEnum.WARM_UNIVERSAL_POWER, currentMonths.get(yearMonth)));
//        pbMonth.add(getValue(SubitemIndexEnum.LIGHTING_POWER_CONSUMPTION, currentMonths.get(yearMonth)));
//        pbMonth.add(getValue(SubitemIndexEnum.SOCKET_POWER_CONSUMPTION, currentMonths.get(yearMonth)));
//        pbMonth.add(getValue(SubitemIndexEnum.OTHER_ELECTRICITY_CONSUMPTION, currentMonths.get(yearMonth)));
        CommonStaVO mVo = new CommonStaVO(
                "当月",
                Arrays.asList(xlist),
                pbMonth.stream().map(val -> val == null ? BigDecimal.ZERO.toPlainString(): val.toPlainString()).collect(Collectors.toList()));
        data.add(mVo);
        //当年
        List<BigDecimal> pbYear = new ArrayList<>();
        //电梯
        pbYear.add(getValue(SubitemIndexEnum.ENERGY_SUBELEVATOR_TOTAL, currentYear));
        //插座
        pbYear.add(getValue(SubitemIndexEnum.SOCKET_POWER_CONSUMPTION, currentYear));
        //暖通
        pbYear.add(getValue(SubitemIndexEnum.WARM_UNIVERSAL_POWER, currentYear));
        //照明
        pbYear.add(getValue(SubitemIndexEnum.LIGHTING_POWER_CONSUMPTION, currentYear));
        //热水
        pbYear.add(getValue(SubitemIndexEnum.ENERGY_HEATWATER_TOTAL, currentYear));
//        pbYear.add(getValue(SubitemIndexEnum.ELECTRIC_QUANTITY_OF_CHARGING_STATION, currentYear));
//        pbYear.add(getValue(SubitemIndexEnum.WARM_UNIVERSAL_POWER, currentYear));
//        pbYear.add(getValue(SubitemIndexEnum.LIGHTING_POWER_CONSUMPTION, currentYear));
//        pbYear.add(getValue(SubitemIndexEnum.SOCKET_POWER_CONSUMPTION, currentYear));
//        pbYear.add(getValue(SubitemIndexEnum.OTHER_ELECTRICITY_CONSUMPTION, currentYear));
        CommonStaVO yVo = new CommonStaVO(
                "当年",
                Arrays.asList(xlist),
                pbYear.stream().map(val -> val == null ? BigDecimal.ZERO.toPlainString(): val.toPlainString()).collect(Collectors.toList()));
        data.add(yVo);
        return new SDLCommonStaVO(data);
    }

    /*
        交直流负荷结构
        直流:FCS直流电表的用电量 交流: gp01 + gp02 + gp03电表的有功功率-FCS直流电表的 用电量
     */
    private SDLCommonStaVO getPDRatio(){

        List<CommonStaVO> data = new ArrayList<>();
        String [] xlist = new String[]{"交流负荷","直流负荷"};
        //----当月
        //--当月交流
        BigDecimal ppMonth = NumberUtil.add(getDevicePMonth(ELE_ALT_DEVICE_1GP01),getDevicePMonth(ELE_ALT_DEVICE_1GP02),getDevicePMonth(ELE_ALT_DEVICE_1GP03));
        //--当月直流
        BigDecimal pdMonth = getDevicePMonth(ELE_DIR_DEVICE_DBDCFCS);
        List<BigDecimal> pMonthYValue = new ArrayList<>();
        pMonthYValue.add(ppMonth);
        pMonthYValue.add(pdMonth);
        CommonStaVO pMonth = new CommonStaVO("当月", Arrays.asList(xlist),pMonthYValue.stream().map(BigDecimal::toPlainString).collect(Collectors.toList()));
        //当年
        //--当年交流
        BigDecimal ppYear = NumberUtil.add(getDevicePYear(ELE_ALT_DEVICE_1GP01),getDevicePYear(ELE_ALT_DEVICE_1GP02),getDevicePYear(ELE_ALT_DEVICE_1GP03));
        //--当年直流
        BigDecimal pdYear = getDevicePYear(ELE_DIR_DEVICE_DBDCFCS);
        List<BigDecimal> pYearYValue = new ArrayList<>();
        pYearYValue.add(ppYear);
        pYearYValue.add(pdYear);
        CommonStaVO pYear = new CommonStaVO("当年", Arrays.asList(xlist),pYearYValue.stream().map(BigDecimal::toPlainString).collect(Collectors.toList()));
        data.add(pMonth);
        data.add(pYear);
        return new SDLCommonStaVO(data);
    }

    /**
     * 最近30天用电量
     * @param projectId
     * @return
     */
    private SDLCommonStaVO getPDay30(String projectId){
        SDLCommonStaVO data = new SDLCommonStaVO();
        List<CommonStaVO> barChartData = new ArrayList<>();
        //String[] xlist = new String[]{"1","2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12","13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23","24","25","26","27","28","29","30"};
        // 日使用量查询
        IndexDayRequest indexDayRequest = new IndexDayRequest();
        LocalDate localDate = LocalDate.now();
//        // 最近30天
//        List<LocalDate> days = new ArrayList<>();
//        int i = 30;
//        while ( i > 0) {
//            days.add(LocalDate.now().minusDays(i));
//            i --;
//        }
        //本月 天
        List<LocalDate> days = new ArrayList<>();
        LocalDate today = LocalDate.now();
        LocalDate monthFirst = LocalDate.of(today.getYear(),today.getMonthValue(),1);
        LocalDate monthLast  = monthFirst.plusMonths(1).minusDays(1);
        days.add(monthFirst);
        while(true){
            monthFirst = monthFirst.plusDays(1);
            days.add(monthFirst);
            if(monthFirst.compareTo(monthLast) == 0){
                break;
            }
        }
        String[] xlist = days.stream().map(LocalDate::getDayOfMonth).map(String::valueOf).collect(Collectors.toList()).toArray(new String[]{});

        indexDayRequest.setDays(days.toArray(new LocalDate[]{}));
        indexDayRequest.setProjectBizId(projectId);
        SubitemIndexEnum[] currentDayIndices = new SubitemIndexEnum[]{
                // 全部负荷总用电量
                SubitemIndexEnum.TOTAL_ELECTRICITY_CONSUMPTION_OF_ALL_LOADS,
        };
        indexDayRequest.setIndices(currentDayIndices);
        Response<Map<LocalDate, Map<SubitemIndexEnum, BigDecimal>>> mapDayResponse = subitemApi.searchDataByDay(indexDayRequest);
        Map<LocalDate, Map<SubitemIndexEnum, BigDecimal>> p30 = mapDayResponse.getResult();
        barChartData.add(new CommonStaVO(
                "最近30日用电量",
                Arrays.asList(xlist),
                days.stream().map(new Function<LocalDate, String>() {
                    @Override
                    public String apply(LocalDate date) {
                        return getValue(SubitemIndexEnum.TOTAL_ELECTRICITY_CONSUMPTION_OF_ALL_LOADS,p30.get(date)).toPlainString();
                    }
                }).collect(Collectors.toList())
        ));
        data.setBarChartData(barChartData);
        return data;
    }

    /**
     * 最近12月用电量
     * @param projectId
     * @return
     */
    private SDLCommonStaVO getPMonth12(String projectId){
        SDLCommonStaVO data = new SDLCommonStaVO();
        List<CommonStaVO> barChartData = new ArrayList<>();
        List<YearMonth> months = new ArrayList<>();

        //当年 月
        LocalDate today = LocalDate.now();
        YearMonth monthFirst = YearMonth.of(today.getYear(),1);
        YearMonth monthLast  = monthFirst.plusMonths(11);
        months.add(monthFirst);
        while(true){
            monthFirst = monthFirst.plusMonths(1);
            months.add(monthFirst);
            if(monthFirst.compareTo(monthLast) == 0){
                break;
            }
        }
        String[] xlist = months.stream().map(YearMonth::getMonthValue).map(String::valueOf).collect(Collectors.toList()).toArray(new String[]{});
        // 月度使用量查询
        IndexMonthRequest indexMonthRequest = new IndexMonthRequest();
        YearMonth yearMonth = YearMonth.now();
        indexMonthRequest.setMonths(months.toArray(new YearMonth[]{}));
        indexMonthRequest.setProjectBizId(projectId);
        SubitemIndexEnum[] currentMonthIndices = new SubitemIndexEnum[]{
                // 全部负荷总用电量
                SubitemIndexEnum.TOTAL_ELECTRICITY_CONSUMPTION_OF_ALL_LOADS,

        };
        indexMonthRequest.setIndices(currentMonthIndices);
        Response<Map<YearMonth, Map<SubitemIndexEnum, BigDecimal>>> mapMonthResponse = subitemApi.searchDataByMonth(indexMonthRequest);
        Map<YearMonth, Map<SubitemIndexEnum, BigDecimal>> pm12 = mapMonthResponse.getResult();
        //上年 12个月
        List<YearMonth> lastYearmonths = months.stream().map(yearMonth1 -> yearMonth1.minusYears(1)).collect(Collectors.toList());
        indexMonthRequest.setMonths(lastYearmonths.toArray(new YearMonth[]{}));
        Response<Map<YearMonth, Map<SubitemIndexEnum, BigDecimal>>> mapMonthLastYearResponse = subitemApi.searchDataByMonth(indexMonthRequest);
        Map<YearMonth, Map<SubitemIndexEnum, BigDecimal>> pm12LastYear = mapMonthLastYearResponse.getResult();
        //上年
        barChartData.add(new CommonStaVO(
                "上年用电量",
                Arrays.asList(xlist),
                lastYearmonths.stream().map(new Function<YearMonth, String>() {
                    @Override
                    public String apply(YearMonth yearMonth) {
                        return getValue(SubitemIndexEnum.TOTAL_ELECTRICITY_CONSUMPTION_OF_ALL_LOADS,pm12LastYear.get(yearMonth)).toPlainString();
                    }
                }).collect(Collectors.toList())
        ));
        //当年
        barChartData.add(new CommonStaVO(
                "当年用电量",
                Arrays.asList(xlist),
                months.stream().map(new Function<YearMonth, String>() {
                    @Override
                    public String apply(YearMonth yearMonth) {
                        return getValue(SubitemIndexEnum.TOTAL_ELECTRICITY_CONSUMPTION_OF_ALL_LOADS,pm12.get(yearMonth)).toPlainString();
                    }
                }).collect(Collectors.toList())
        ));

        //同比
        List<BigDecimal> yoyList = getYoyList(
                barChartData.get(1).getYlist().stream().map(s -> new BigDecimal(s)).collect(Collectors.toList()),
                barChartData.get(0).getYlist().stream().map(s -> new BigDecimal(s)).collect(Collectors.toList())
        );

        barChartData.add(new CommonStaVO(
                "同比",
                Arrays.asList(xlist),
                yoyList.stream().map(BigDecimal::toPlainString).collect(Collectors.toList())
        ));
        data.setBarChartData(barChartData);
        return data;
    }



    public SDLOverviewPage2Response overview2(String projectBizId) {
        //设备数量

        // 年 月 日 指标统计
        // 当年使用量查询
        IndexYearRequest indexYearRequest = new IndexYearRequest();
        indexYearRequest.setYears(new Year[]{Year.now()});
        indexYearRequest.setProjectBizId(projectBizId);
        SubitemIndexEnum[] currentYearIndices = new SubitemIndexEnum[]{
                //全部负荷
                SubitemIndexEnum.TOTAL_ELECTRICITY_CONSUMPTION_OF_ALL_LOADS,
                //总电费
                SubitemIndexEnum.ELECTRICITY_ENERGY_USAGE_FEE_TOTAL,
                SubitemIndexEnum.ELECTRIC_QUANTITY_OF_CHARGING_STATION,
                SubitemIndexEnum.WARM_UNIVERSAL_POWER,
                SubitemIndexEnum.LIGHTING_POWER_CONSUMPTION,
                SubitemIndexEnum.SOCKET_POWER_CONSUMPTION,
                SubitemIndexEnum.OTHER_ELECTRICITY_CONSUMPTION
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
                //全部负荷
                SubitemIndexEnum.TOTAL_ELECTRICITY_CONSUMPTION_OF_ALL_LOADS,
                //总电费
                SubitemIndexEnum.ELECTRICITY_ENERGY_USAGE_FEE_TOTAL,

                SubitemIndexEnum.ELECTRIC_QUANTITY_OF_CHARGING_STATION,
                SubitemIndexEnum.WARM_UNIVERSAL_POWER,
                SubitemIndexEnum.LIGHTING_POWER_CONSUMPTION,
                SubitemIndexEnum.SOCKET_POWER_CONSUMPTION,
                SubitemIndexEnum.OTHER_ELECTRICITY_CONSUMPTION
        };
        indexMonthRequest.setIndices(currentMonthIndices);
        Response<Map<YearMonth, Map<SubitemIndexEnum, BigDecimal>>> mapMonthResponse = subitemApi.searchDataByMonth(indexMonthRequest);
        Map<YearMonth, Map<SubitemIndexEnum, BigDecimal>> currentMonths = mapMonthResponse.getResult();
        // 日使用量查询
        IndexDayRequest indexDayRequest = new IndexDayRequest();
        LocalDate localDate = LocalDate.now();
        LocalDate[] currentMonthDay = getCurrentMonthDay(localDate);
        indexDayRequest.setDays(currentMonthDay);
        indexDayRequest.setProjectBizId(projectBizId);
        SubitemIndexEnum[] currentDayIndices = new SubitemIndexEnum[]{
                // 暖通用电量
                SubitemIndexEnum.TOTAL_ELECTRICITY_CONSUMPTION_OF_ALL_LOADS,

        };
        indexDayRequest.setIndices(currentDayIndices);
        Response<Map<LocalDate, Map<SubitemIndexEnum, BigDecimal>>> mapDayResponse = subitemApi.searchDataByDay(indexDayRequest);
        Map<LocalDate, Map<SubitemIndexEnum, BigDecimal>> currentDays = mapDayResponse.getResult();



        SDLOverviewPage2Response response = new SDLOverviewPage2Response();
        //主要用能设备构成 写死 todo
        response.setLightNum("20");
        response.setChargeNum("60");
        response.setWindboardNum("0.2");
        response.setHostNum("178");
        response.setElevatorNum("9");
        response.setPrintNum("4");

        //title : 当日用电量 当月用电量 当年用电量
        response.setPYear(getValue(SubitemIndexEnum.TOTAL_ELECTRICITY_CONSUMPTION_OF_ALL_LOADS, currentYear));
        response.setPMonth(getValue(SubitemIndexEnum.TOTAL_ELECTRICITY_CONSUMPTION_OF_ALL_LOADS, currentMonths.get(yearMonth)));
        response.setPDay(getValue(SubitemIndexEnum.TOTAL_ELECTRICITY_CONSUMPTION_OF_ALL_LOADS, currentDays.get(LocalDate.now())));
        // 电度电费： 当月电度电费 当年电度电费
        response.setFeeYear(getValue(SubitemIndexEnum.ELECTRICITY_ENERGY_USAGE_FEE_TOTAL, currentYear));
        response.setFeeMonth(getValue(SubitemIndexEnum.ELECTRICITY_ENERGY_USAGE_FEE_TOTAL, currentMonths.get(yearMonth)));

        //交直流负荷结构   直流:FCS直流电表的用电量 交流: gp01 + gp02 + gp03电表的有功功率-FCS直流电表的 用电量
        response.setPaRatio(getPDRatio());

        //分类负荷结构 - 当月
        response.setPDay30(getPDay30(projectBizId));
        //分类负荷结构 - 当年
        response.setPMonth12(getPMonth12(projectBizId));
        //当月用电分区排名
        //总用电量
        String kpi = "area.electricity.energyUsage.total";
        LocalDate today = LocalDate.now();
        LocalDate monthFirst = LocalDate.of(today.getYear(),today.getMonthValue(),1);
        String beginTime = monthFirst.minusDays(1).format(DateUtils.LC_DT_FMT_DAY);
        String endTime = monthFirst.plusMonths(1).format(DateUtils.LC_DT_FMT_DAY);
        List<ProjectStaSubareaDayEntity> subareaOrders =  projectStaSubareaDayMapper.getSubareaOrder(projectBizId,beginTime,endTime);
        CommonStaVO staVo = new CommonStaVO(
                "当月分区用电排名",
                subareaOrders.stream().map(ProjectStaSubareaDayEntity::getSubareaName).collect(Collectors.toList()),
                subareaOrders.stream().map(ProjectStaSubareaDayEntity::getStaValue).map(BigDecimal::toPlainString).collect(Collectors.toList())
        );
        SDLCommonStaVO subareaOrderVo = new SDLCommonStaVO(List.of(staVo));
        response.setSubareaOrder(subareaOrderVo);

        //分类负荷结构
        response.setPaSubitem(getPSubitem(currentYear,currentMonths));

        //当日交直流负荷曲线
        LocalDateTime now = LocalDateTime.now();

        LocalDateTime todayZero = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 0, 0);
        LocalDateTime todayEnd = todayZero.plusDays(1L);
        LocalDateTime yesterday = now.minusDays(1);
        LocalDateTime yesterDayZero = LocalDateTime.of(yesterday.getYear(), yesterday.getMonth(), yesterday.getDayOfMonth(), 0, 0);
        BasePRequest basePRequest = new BasePRequest();
        basePRequest.setEnd(todayEnd);
        basePRequest.setProductBizId(PRODUCT_DIR_ID);
        basePRequest.setStart(todayZero);
        basePRequest.setDeviceIds(List.of(ELE_DIR_DEVICE_DBDCFCS));
        Response<List<ZnbPResponse>> todayResponseFCS = deviceHistoryApi.getZnbPResponse(basePRequest);
        basePRequest.setProductBizId(PRODUCT_AT_ID);
        basePRequest.setDeviceIds(List.of(ELE_ALT_DEVICE_1GP01));
        Response<List<ZnbPResponse>> todayResponseGP01 = deviceHistoryApi.getZnbPResponse(basePRequest);
        basePRequest.setDeviceIds(List.of(ELE_ALT_DEVICE_1GP02));
        Response<List<ZnbPResponse>> todayResponseGP02 = deviceHistoryApi.getZnbPResponse(basePRequest);
        basePRequest.setDeviceIds(List.of(ELE_ALT_DEVICE_1GP03));
        Response<List<ZnbPResponse>> todayResponseGP03 = deviceHistoryApi.getZnbPResponse(basePRequest);

        basePRequest.setEnd(todayZero);
        basePRequest.setStart(yesterDayZero);
        basePRequest.setDeviceIds(List.of(ELE_DIR_DEVICE_DBDCFCS));
        Response<List<ZnbPResponse>> yesterdayResponseFCS = deviceHistoryApi.getZnbPResponse(basePRequest);
        basePRequest.setDeviceIds(List.of(ELE_ALT_DEVICE_1GP01));
        Response<List<ZnbPResponse>> yesterdayResponseGP01 = deviceHistoryApi.getZnbPResponse(basePRequest);
        basePRequest.setDeviceIds(List.of(ELE_ALT_DEVICE_1GP02));
        Response<List<ZnbPResponse>> yesterdayResponseGP02 = deviceHistoryApi.getZnbPResponse(basePRequest);
        basePRequest.setDeviceIds(List.of(ELE_ALT_DEVICE_1GP03));
        Response<List<ZnbPResponse>> yesterdayResponseGP03 = deviceHistoryApi.getZnbPResponse(basePRequest);

        List<ZnbPResponse> todayResponseResultFCS = todayResponseFCS.getResult().stream().sorted(Comparator.comparing(ZnbPResponse::getTime)).toList();
        List<ZnbPResponse> todayResponseResultGP01 = todayResponseGP01.getResult().stream().sorted(Comparator.comparing(ZnbPResponse::getTime)).toList();
        List<ZnbPResponse> todayResponseResultGP02 = todayResponseGP02.getResult().stream().sorted(Comparator.comparing(ZnbPResponse::getTime)).toList();
        List<ZnbPResponse> todayResponseResultGP03 = todayResponseGP03.getResult().stream().sorted(Comparator.comparing(ZnbPResponse::getTime)).toList();
        SDLOverviewPage2Response.DayP dayP = new SDLOverviewPage2Response.DayP();

        for (ZnbPResponse znbPResponseFCS : todayResponseResultFCS) {
            String time = znbPResponseFCS.getTime();
            ZnbPResponse znbPResponseGP01 = todayResponseResultGP01.stream().filter(item -> item.getTime().equals(time)).findAny().orElse(new ZnbPResponse());
            ZnbPResponse znbPResponseGP02 = todayResponseResultGP02.stream().filter(item -> item.getTime().equals(time)).findAny().orElse(new ZnbPResponse());
            ZnbPResponse znbPResponseGP03 = todayResponseResultGP03.stream().filter(item -> item.getTime().equals(time)).findAny().orElse(new ZnbPResponse());
            ZonedDateTime zonedDateTime = ZonedDateTime.parse(time);
            ZonedDateTime adjustedDateTime = zonedDateTime.withZoneSameInstant(ZoneOffset.ofHours(8));
            LocalDateTime localDateTime = adjustedDateTime.toLocalDateTime();
            BigDecimal todayPD = znbPResponseFCS.getP() == null ? BigDecimal.ZERO : NumberUtil.mul(BigDecimal.valueOf(-1),znbPResponseFCS.getP());
            BigDecimal todayPA = NumberUtil.add(
                    znbPResponseGP01.getP() == null ? BigDecimal.ZERO : znbPResponseGP01.getP(),
                    znbPResponseGP02.getP() == null ? BigDecimal.ZERO : znbPResponseGP02.getP(),
                    znbPResponseGP03.getP() == null ? BigDecimal.ZERO : znbPResponseGP03.getP()
                    );
            dayP.getX().add(DateUtil.format(localDateTime, DatePattern.NORM_DATETIME_PATTERN));
            if (now.isAfter(localDateTime)) {
                dayP.getPd().add(todayPD);
                dayP.getPa().add(todayPA.subtract(todayPD));
            }

        }
        response.setDayP(dayP);

        return response;
    }
}
