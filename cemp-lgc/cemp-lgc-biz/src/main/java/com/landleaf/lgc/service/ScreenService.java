package com.landleaf.lgc.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.nacos.shaded.com.google.gson.JsonObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.landleaf.bms.api.ProjectApi;
import com.landleaf.bms.api.dto.ProjectDetailsResponse;
import com.landleaf.bms.api.weather.ProjectWeatherApi;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.comm.tenant.TenantContext;
import com.landleaf.comm.util.WeatherUtil;
import com.landleaf.data.api.device.DeviceCurrentApi;
import com.landleaf.data.api.device.DeviceHistoryApi;
import com.landleaf.data.api.device.dto.BasePRequest;
import com.landleaf.data.api.device.dto.ChargePResponse;
import com.landleaf.data.api.device.dto.GscnPResponse;
import com.landleaf.data.api.device.dto.ZnbPResponse;
import com.landleaf.energy.api.DeviceElectricityApi;
import com.landleaf.energy.api.ProjectCnfTimePeriodApi;
import com.landleaf.energy.api.SubitemApi;
import com.landleaf.energy.enums.SubitemIndexEnum;
import com.landleaf.energy.request.*;
import com.landleaf.energy.response.DeviceElectricityResponse;
import com.landleaf.lgc.dal.mapper.CustomFunctionConfMapper;
import com.landleaf.lgc.domain.entity.CustomFunctionConfEntity;
import com.landleaf.lgc.domain.enums.CustomFunctionConfConstants;
import com.landleaf.lgc.domain.enums.IbssasEnum;
import com.landleaf.lgc.domain.enums.LgcConstants;
import com.landleaf.lgc.domain.enums.StoragePcsRSTEnum;
import com.landleaf.lgc.domain.response.*;
import com.landleaf.lgc.domain.response.origin.*;
import com.landleaf.monitor.api.AlarmApi;
import com.landleaf.monitor.dto.AlarmResponse;
import com.landleaf.redis.RedisUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.landleaf.lgc.domain.enums.LgcConstants.*;
import static com.landleaf.redis.constance.KeyConstance.DEVICE_CURRENT_STATUS;
import static com.landleaf.redis.constance.KeyConstance.WEATHER_CACHE;

/**
 * ScreenService
 *
 * @author 张力方
 * @since 2023/7/28
 **/
@Service
@RequiredArgsConstructor
public class ScreenService {
    private final SubitemApi subitemApi;
    private final DeviceHistoryApi deviceHistoryApi;
    private final ProjectApi projectApi;
    private final ProjectCnfTimePeriodApi projectCnfTimePeriodApi;
    private final DeviceElectricityApi deviceElectricityApi;
    private final CustomFunctionConfMapper customFunctionConfMapper;
    private final AlarmApi alarmApi;
    private final ProjectWeatherApi projectWeatherApi;
    private final RestTemplate restTemplate;
    private final RedisUtils redisUtils;
    private final DeviceCurrentApi deviceCurrentApi;

    private static final Map<String, String> DEVICE_ID_NAME_MAP = new HashMap<>() {
        {
            put("D000000001238", "1#交流桩");
            put("D000000001239", "2#交流桩");
            put("D000000001240", "3#交流桩");
            put("D000000001241", "4#交流桩");
            put("D000000001242", "5#交流桩");
            put("D000000001237", "1#直流桩");
        }
    };

    /**
     * 总览
     *
     * @param projectBizId 项目业务id
     */
    public OverviewResponse overview(String projectBizId) {
        OverviewResponse overviewResponse = new OverviewResponse();
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
        };
        indexYearRequest.setIndices(currentYearIndices);
        Response<Map<Year, Map<SubitemIndexEnum, BigDecimal>>> mapYearResponse = subitemApi.searchDataByYear(indexYearRequest);
        Map<SubitemIndexEnum, BigDecimal> currentYear = mapYearResponse.getResult().get(Year.now());
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

        // 月度使用量查询
        IndexMonthRequest indexMonthRequest = new IndexMonthRequest();
        YearMonth yearMonth = YearMonth.now();
        YearMonth[] currentYearMonth = getCurrentYearMonth(yearMonth);
        indexMonthRequest.setMonths(currentYearMonth);
        indexMonthRequest.setProjectBizId(projectBizId);
        SubitemIndexEnum[] currentMonthIndices = new SubitemIndexEnum[]{
                // 购网电量
                SubitemIndexEnum.PURCHASE_NETWORK_ELECTRICITY,
                // 光伏发电量
                SubitemIndexEnum.PHOTOVOLTAIC_POWER_GENERATION,
                // 充电桩充电量
                SubitemIndexEnum.ELECTRIC_QUANTITY_OF_CHARGING_STATION,
                // 暖通用电量
                SubitemIndexEnum.WARM_UNIVERSAL_POWER,
                // 照明用电量
                SubitemIndexEnum.LIGHTING_POWER_CONSUMPTION,
                // 插座用电量
                SubitemIndexEnum.SOCKET_POWER_CONSUMPTION,
                // 其他用电量
                SubitemIndexEnum.OTHER_ELECTRICITY_CONSUMPTION,
        };
        indexMonthRequest.setIndices(currentMonthIndices);
        Response<Map<YearMonth, Map<SubitemIndexEnum, BigDecimal>>> mapMonthResponse = subitemApi.searchDataByMonth(indexMonthRequest);
        Map<YearMonth, Map<SubitemIndexEnum, BigDecimal>> currentMonths = mapMonthResponse.getResult();

        // 当年购网电量
        overviewResponse.setElectricityPccEnergyUsageCurrentYear(getValue(SubitemIndexEnum.PURCHASE_NETWORK_ELECTRICITY, currentYear));
        // 当年上网电量
        overviewResponse.setElectricityPccEnergyProductionCurrentYear(getValue(SubitemIndexEnum.ON_GRID_ENERGY, currentYear));
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
        // 光伏累计收益
        overviewResponse.setElectricityPvEnergyProductionFeeTotal(getValue(SubitemIndexEnum.PHOTOVOLTAIC_REVENUE, cumulative));
        // 储能累计收益
        overviewResponse.setElectricityStorageEnergyNetFeeTotal(getValue(SubitemIndexEnum.ENERGY_STORAGE_REVENUE, cumulative));
        // 当年光伏绿电占总用电比例 当年光伏发电量/当年总用电量
        BigDecimal currentYearTotal = getValue(SubitemIndexEnum.PURCHASE_NETWORK_ELECTRICITY, currentYear);
        BigDecimal currentYearPv = getValue(SubitemIndexEnum.PHOTOVOLTAIC_POWER_GENERATION, currentYear);
        if (currentYearTotal == null || currentYearPv == null || currentYearTotal.equals(BigDecimal.ZERO)) {
            overviewResponse.setPvVsTotalPercentageCurrentYear(null);
        } else {
            overviewResponse.setPvVsTotalPercentageCurrentYear(currentYearPv.divide(currentYearTotal, 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100)));
        }
        // 当年光伏发电消纳比例 1-当年上网电量/当年光伏发电量
        BigDecimal currentYearGrid = getValue(SubitemIndexEnum.ON_GRID_ENERGY, currentYear);
        if (currentYearPv == null || currentYearGrid == null || currentYearPv.equals(BigDecimal.ZERO)) {
            overviewResponse.setPvAbsorptivePercentageCurrentYear(null);
        } else {
            overviewResponse.setPvAbsorptivePercentageCurrentYear((BigDecimal.ONE.subtract(currentYearGrid.divide(currentYearPv, 4, RoundingMode.HALF_UP))).multiply(new BigDecimal(100)));
        }
        // 月度电源结构趋势
        OverviewResponse.ElectricityStructure electricityStructure = new OverviewResponse.ElectricityStructure();
        if (CollUtil.isNotEmpty(currentMonths)) {
            currentMonths.forEach((key, value) -> {
                electricityStructure.getPcc().add(getValue(SubitemIndexEnum.PURCHASE_NETWORK_ELECTRICITY, value));
                electricityStructure.getPv().add(getValue(SubitemIndexEnum.PHOTOVOLTAIC_POWER_GENERATION, value));
                electricityStructure.getX().add(key.getMonthValue());
            });
        } else {
            for (YearMonth month : currentYearMonth) {
                electricityStructure.getPcc().add(BigDecimal.ZERO);
                electricityStructure.getPv().add(BigDecimal.ZERO);
                electricityStructure.getX().add(month.getMonthValue());
            }
        }
        overviewResponse.setMonthElectricityStructure(electricityStructure);

        // 充电桩累计收益
        overviewResponse.setElectricitySubEnergyNetFeeTotal(getValue(SubitemIndexEnum.INCOME_OF_CHARGING_STATION, cumulative));
        // 当年负荷使用结构
        OverviewResponse.ElectricityLoadStructure loadStructure = new OverviewResponse.ElectricityLoadStructure();
        loadStructure.setCharge(getValue(SubitemIndexEnum.ELECTRIC_QUANTITY_OF_CHARGING_STATION, currentYear));
        loadStructure.setHvac(getValue(SubitemIndexEnum.WARM_UNIVERSAL_POWER, currentYear));
        loadStructure.setLight(getValue(SubitemIndexEnum.LIGHTING_POWER_CONSUMPTION, currentYear));
        loadStructure.setSocket(getValue(SubitemIndexEnum.SOCKET_POWER_CONSUMPTION, currentYear));
        loadStructure.setOther(getValue(SubitemIndexEnum.OTHER_ELECTRICITY_CONSUMPTION, currentYear));
        overviewResponse.setElectricityLoadStructureCurrentYear(loadStructure);
        // 月度负荷使用结构
        OverviewResponse.ElectricityLoadStructureDataSet monthElectricityLoadStructure = new OverviewResponse.ElectricityLoadStructureDataSet();
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
        overviewResponse.setMonthElectricityLoadStructure(monthElectricityLoadStructure);

        // 日使用量查询
        IndexDayRequest indexDayRequest = new IndexDayRequest();
        LocalDate localDate = LocalDate.now();
        LocalDate[] currentMonthDay = getCurrentMonthDay(localDate);
        indexDayRequest.setDays(currentMonthDay);
        indexDayRequest.setProjectBizId(projectBizId);
        SubitemIndexEnum[] currentDayIndices = new SubitemIndexEnum[]{
                // 光伏发电量
                SubitemIndexEnum.PHOTOVOLTAIC_POWER_GENERATION,
                // 购网电量
                SubitemIndexEnum.PURCHASE_NETWORK_ELECTRICITY,
                // 上网电量
                SubitemIndexEnum.ON_GRID_ENERGY,
                // 储放电量
                SubitemIndexEnum.ENERGY_STORAGE_AND_DISCHARGE_CAPACITY,
                // 储充电量
                SubitemIndexEnum.STORAGE_AND_CHARGING_CAPACITY,
                // 充电桩充电量
                SubitemIndexEnum.ELECTRIC_QUANTITY_OF_CHARGING_STATION,
        };
        indexDayRequest.setIndices(currentDayIndices);
        Response<Map<LocalDate, Map<SubitemIndexEnum, BigDecimal>>> mapDayResponse = subitemApi.searchDataByDay(indexDayRequest);
        Map<LocalDate, Map<SubitemIndexEnum, BigDecimal>> currentDays = mapDayResponse.getResult();

        // 光伏系统实时有功功率
        Map<String, Object> currentPv1 = deviceCurrentApi.getDeviceCurrentById(ZNB1).getResult();
        Map<String, Object> currentPv2 = deviceCurrentApi.getDeviceCurrentById(ZNB2).getResult();
        overviewResponse.getMiddle().setPvCurrentActiveP(new BigDecimal(String.valueOf(currentPv1.get("P")))
                .add(new BigDecimal(String.valueOf(currentPv2.get("P")))));
        // 光伏当日发电量
        if (CollUtil.isNotEmpty(currentDays)) {
            overviewResponse.getMiddle().setElectricityPvEnergyProductionCurrentDay(getValue(SubitemIndexEnum.PHOTOVOLTAIC_POWER_GENERATION, currentDays.get(localDate)));
        }

        // 市电实时有功功率
        Map<String, Object> currentPcc1 = deviceCurrentApi.getDeviceCurrentById(PCC1).getResult();
        Map<String, Object> currentPcc2 = deviceCurrentApi.getDeviceCurrentById(PCC2).getResult();
        overviewResponse.getMiddle().setPccCurrentActiveP(new BigDecimal(String.valueOf(currentPcc1.get("P")))
                .add(new BigDecimal(String.valueOf(currentPcc2.get("P")))));
        // 市电实时无功功率
        overviewResponse.getMiddle().setPccCurrentReactiveP(new BigDecimal(String.valueOf(currentPcc1.get("Q")))
                .add(new BigDecimal(String.valueOf(currentPcc2.get("Q")))));
        // 市电当日购网电量
        if (CollUtil.isNotEmpty(currentDays)) {
            overviewResponse.getMiddle().setElectricityPccEnergyUsageCurrentDay(getValue(SubitemIndexEnum.PURCHASE_NETWORK_ELECTRICITY, currentDays.get(localDate)));
        }
        // 市电当日上网电量
        if (CollUtil.isNotEmpty(currentDays)) {
            overviewResponse.getMiddle().setElectricityPvEnergyProductionGridCurrentDay(getValue(SubitemIndexEnum.ON_GRID_ENERGY, currentDays.get(localDate)));
        }

        // 储能系统实时运行状态
        Map<String, Object> currentStorage = deviceCurrentApi.getDeviceCurrentById(STORAGE_DEVICE_BIZ_ID).getResult();
        // 运行状态
        String pcsRST = String.valueOf(currentStorage.get("pcsRST"));
        if ("0".equals(pcsRST)) {
            overviewResponse.getMiddle().setStorageCurrentRunningStatus("关机");
        }
        if ("1".equals(pcsRST)) {
            overviewResponse.getMiddle().setStorageCurrentRunningStatus("待机");
        }
        if ("2".equals(pcsRST)) {
            overviewResponse.getMiddle().setStorageCurrentRunningStatus("充电");
        }
        if ("3".equals(pcsRST)) {
            overviewResponse.getMiddle().setStorageCurrentRunningStatus("放电");
        }
        // 储能系统实时有功功率
        overviewResponse.getMiddle().setStorageCurrentActiveP(new BigDecimal(String.valueOf(currentStorage.get("P"))));
        // 储能系统实时SOC
        overviewResponse.getMiddle().setStorageCurrentSoc(new BigDecimal(String.valueOf(currentStorage.get("SOC"))));

        // 充电桩实时充电中数量
        Map<String, Object> currentStation1 = deviceCurrentApi.getDeviceCurrentById(AL_STATION1).getResult();
        Map<String, Object> currentStation2 = deviceCurrentApi.getDeviceCurrentById(AL_STATION2).getResult();
        Map<String, Object> currentStation3 = deviceCurrentApi.getDeviceCurrentById(AL_STATION3).getResult();
        Map<String, Object> currentStation4 = deviceCurrentApi.getDeviceCurrentById(AL_STATION4).getResult();
        Map<String, Object> currentStation5 = deviceCurrentApi.getDeviceCurrentById(AL_STATION5).getResult();
        Map<String, Object> currentDirStation1 = deviceCurrentApi.getDeviceCurrentById(DIR_STATION1).getResult();

        // 充电桩实时充电中数量 P>0.05 算充电中
        int inChargeNum = 0;
        BigDecimal pStation1 = new BigDecimal(String.valueOf(currentStation1.get("P")));
        BigDecimal pStation2 = new BigDecimal(String.valueOf(currentStation2.get("P")));
        BigDecimal pStation3 = new BigDecimal(String.valueOf(currentStation3.get("P")));
        BigDecimal pStation4 = new BigDecimal(String.valueOf(currentStation4.get("P")));
        BigDecimal pStation5 = new BigDecimal(String.valueOf(currentStation5.get("P")));
        BigDecimal pDirStation1 = new BigDecimal(String.valueOf(currentDirStation1.get("P")));
        if (pStation1.compareTo(BigDecimal.valueOf(0.05)) > 0) {
            inChargeNum++;
        }
        if (pStation2.compareTo(BigDecimal.valueOf(0.05)) > 0) {
            inChargeNum++;
        }
        if (pStation3.compareTo(BigDecimal.valueOf(0.05)) > 0) {
            inChargeNum++;
        }
        if (pStation4.compareTo(BigDecimal.valueOf(0.05)) > 0) {
            inChargeNum++;
        }
        if (pStation5.compareTo(BigDecimal.valueOf(0.05)) > 0) {
            inChargeNum++;
        }
        if (pDirStation1.compareTo(BigDecimal.valueOf(0.05)) > 0) {
            inChargeNum++;
        }
        overviewResponse.getMiddle().setStationChargeCurrentNum(BigDecimal.valueOf(inChargeNum));
        // 充电桩实时有功功率
        overviewResponse.getMiddle().setStationCurrentActiveP(pStation1.add(pStation2).add(pStation3).add(pStation4).add(pStation5).add(pDirStation1));
        // 充电桩实时当日充电量
        if (CollUtil.isNotEmpty(currentDays)) {
            overviewResponse.getMiddle().setStationChargeCurrentDay(getValue(SubitemIndexEnum.ELECTRIC_QUANTITY_OF_CHARGING_STATION, currentDays.get(localDate)));
        }

        // 上海办公建筑平均能耗
        TenantContext.setIgnore(true);
        CustomFunctionConfEntity customFunctionConfEntity = customFunctionConfMapper.selectOne(Wrappers.<CustomFunctionConfEntity>lambdaQuery()
                .eq(CustomFunctionConfEntity::getCode, CustomFunctionConfConstants.SH_OFFICE_ENERGY_MONTH));
        String value = customFunctionConfEntity.getValue();
        JSONObject jsonObject = JSON.parseObject(value);
        int monthValue = yearMonth.getMonthValue();
        BigDecimal buildingAvgEnergyUsage = BigDecimal.ZERO;
        // 将上月之前的数据累加 -> 所有的配置累加
        for (int i = 1; i <= 12; i++) {
            Object o = jsonObject.get("" + i);
            if (o instanceof BigDecimal valu) {
                buildingAvgEnergyUsage = buildingAvgEnergyUsage.add(valu);
            } else if (o instanceof Integer valu) {
                buildingAvgEnergyUsage = buildingAvgEnergyUsage.add(BigDecimal.valueOf(valu));
            } else if (o instanceof Float valu) {
                buildingAvgEnergyUsage = buildingAvgEnergyUsage.add(BigDecimal.valueOf(valu));
            } else if (o instanceof Double valu) {
                buildingAvgEnergyUsage = buildingAvgEnergyUsage.add(BigDecimal.valueOf(valu));
            }
        }
        overviewResponse.setBuildingAvgEnergyUsage(buildingAvgEnergyUsage);

        // LGC能耗
        // 月度使用量查询
//        IndexCumulativeMonthRequest indexCumulativeMonthRequest = new IndexCumulativeMonthRequest();
//        indexCumulativeMonthRequest.setMonth(yearMonth);
//        indexCumulativeMonthRequest.setProjectBizId(projectBizId);
//        SubitemIndexEnum[] monthCumulativeIndices = new SubitemIndexEnum[]{
//                // 总用电量
//                SubitemIndexEnum.TOTAL_ELECTRICITY_CONSUMPTION_OF_ALL_LOADS,
//        };
//        indexCumulativeMonthRequest.setIndices(monthCumulativeIndices);
//        Response<Map<SubitemIndexEnum, BigDecimal>> searchDataByMonthCumulative = subitemApi.searchDataByMonthCumulative(indexCumulativeMonthRequest);
//        Map<SubitemIndexEnum, BigDecimal> cumulativeMonth = searchDataByMonthCumulative.getResult();
//        BigDecimal totalElectricityUsage = getValue(SubitemIndexEnum.TOTAL_ELECTRICITY_CONSUMPTION_OF_ALL_LOADS, cumulativeMonth);
        BigDecimal totalElectricityUsage = getValue(SubitemIndexEnum.TOTAL_ELECTRICITY_CONSUMPTION_OF_ALL_LOADS, currentYear);
        Response<ProjectDetailsResponse> projectDetails = projectApi.getProjectDetails(projectBizId);
        BigDecimal area = projectDetails.getResult().getArea();
        if (area != null && !area.equals(BigDecimal.ZERO)) {
            overviewResponse.setLgcEnergyUsage(totalElectricityUsage.divide(area, 2, RoundingMode.HALF_UP));
        }

        // 建筑用电负荷有功功率 光伏有功功率 + 电网有功功率 + 储能有功功率 - 充电桩有功功率
        overviewResponse.getMiddle().setBuildingUsageElectricityActiveP(
                ObjectUtil.defaultIfNull(overviewResponse.getMiddle().getPvCurrentActiveP(), BigDecimal.ZERO)
                        .add(ObjectUtil.defaultIfNull(overviewResponse.getMiddle().getPccCurrentActiveP(), BigDecimal.ZERO))
                        .add(ObjectUtil.defaultIfNull(overviewResponse.getMiddle().getStorageCurrentActiveP(), BigDecimal.ZERO))
                        .subtract(ObjectUtil.defaultIfNull(overviewResponse.getMiddle().getStationCurrentActiveP(), BigDecimal.ZERO)));

        if (CollUtil.isNotEmpty(currentDays)) {
            // 建筑用电负荷当日用电量 光伏当日发电量 -上网当日电量 + 购网当日电量 - 储能当日充电量 + 储能当日放电量 - 充电桩当日用电量
            overviewResponse.getMiddle().setBuildingUsageElectricityCurrentDay(
                    ObjectUtil.defaultIfNull(overviewResponse.getMiddle().getElectricityPvEnergyProductionCurrentDay(), BigDecimal.ZERO)
                            .subtract(ObjectUtil.defaultIfNull(overviewResponse.getMiddle().getElectricityPvEnergyProductionGridCurrentDay(), BigDecimal.ZERO))
                            .add(ObjectUtil.defaultIfNull(overviewResponse.getMiddle().getElectricityPccEnergyUsageCurrentDay(), BigDecimal.ZERO))
                            .subtract(ObjectUtil.defaultIfNull(new BigDecimal(String.valueOf(currentStorage.get("dayChargeE"))), BigDecimal.ZERO))
                            .add(ObjectUtil.defaultIfNull(new BigDecimal(String.valueOf(currentStorage.get("dayDischargeE"))), BigDecimal.ZERO))
                            .subtract(ObjectUtil.defaultIfNull(overviewResponse.getMiddle().getStationChargeCurrentDay(), BigDecimal.ZERO))
            );
        }
        return overviewResponse;
    }

    /**
     * 光伏
     *
     * @param projectBizId 项目业务id
     */
    public PvResponse pv(String projectBizId) {
        PvResponse pvResponse = new PvResponse();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime todayZero = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 0, 0);
        LocalDateTime yesterday = now.minusDays(1);
        LocalDateTime yesterDayZero = LocalDateTime.of(yesterday.getYear(), yesterday.getMonth(), yesterday.getDayOfMonth(), 0, 0);
        BasePRequest basePRequest = new BasePRequest();
        // 逆变器一  逆变器二
        LocalDateTime todayEnd = todayZero.plusDays(1L);
        basePRequest.setEnd(todayEnd);
        basePRequest.setProductBizId(ZNB1_PRODUCT_BIZ_ID);
        basePRequest.setStart(todayZero);
        basePRequest.setDeviceIds(List.of(ZNB1));
        Response<List<ZnbPResponse>> todayResponse1 = deviceHistoryApi.getZnbPResponse(basePRequest);
        basePRequest.setDeviceIds(List.of(ZNB2));
        Response<List<ZnbPResponse>> todayResponse2 = deviceHistoryApi.getZnbPResponse(basePRequest);
        basePRequest.setEnd(todayZero);
        basePRequest.setStart(yesterDayZero);
        basePRequest.setDeviceIds(List.of(ZNB1));
        Response<List<ZnbPResponse>> yesterdayResponse1 = deviceHistoryApi.getZnbPResponse(basePRequest);
        basePRequest.setDeviceIds(List.of(ZNB2));
        Response<List<ZnbPResponse>> yesterdayResponse2 = deviceHistoryApi.getZnbPResponse(basePRequest);
        List<ZnbPResponse> todayResponseResult1 = todayResponse1.getResult().stream().sorted(Comparator.comparing(ZnbPResponse::getTime)).toList();
        List<ZnbPResponse> todayResponseResult2 = todayResponse2.getResult().stream().sorted(Comparator.comparing(ZnbPResponse::getTime)).toList();
        List<ZnbPResponse> yesterdayResponseResult1 = yesterdayResponse1.getResult().stream().sorted(Comparator.comparing(ZnbPResponse::getTime)).toList();
        List<ZnbPResponse> yesterdayResponseResult2 = yesterdayResponse2.getResult().stream().sorted(Comparator.comparing(ZnbPResponse::getTime)).toList();

        // 当日功率曲线
        PvResponse.DayP dayP = new PvResponse.DayP();
        for (ZnbPResponse znbPResponse1 : todayResponseResult1) {
            String time = znbPResponse1.getTime();
            ZnbPResponse znbPResponse2 = todayResponseResult2.stream().filter(item -> item.getTime().equals(time)).findAny().orElse(new ZnbPResponse());
            ZonedDateTime zonedDateTime = ZonedDateTime.parse(time);
            ZonedDateTime adjustedDateTime = zonedDateTime.withZoneSameInstant(ZoneOffset.ofHours(8));
            LocalDateTime localDateTime = adjustedDateTime.toLocalDateTime();
            BigDecimal todayP1 = znbPResponse1.getP() == null ? BigDecimal.ZERO : znbPResponse1.getP();
            BigDecimal todayP2 = znbPResponse2.getP() == null ? BigDecimal.ZERO : znbPResponse2.getP();
            dayP.getX().add(DateUtil.format(localDateTime, DatePattern.NORM_DATETIME_PATTERN));
            if (now.isAfter(localDateTime)) {
                dayP.getTodayP().add(todayP1.add(todayP2));
            }

            Optional<ZnbPResponse> yesterdayP1 = yesterdayResponseResult1.stream().filter(item -> item.getTime().substring(10).equals(time.substring(10))).findAny();
            ZnbPResponse yesterdayP2 = yesterdayResponseResult2.stream().filter(item -> item.getTime().substring(10).equals(time.substring(10))).findAny().orElse(new ZnbPResponse());
            if (yesterdayP1.isPresent()) {
                ZnbPResponse response = yesterdayP1.get();
                BigDecimal p2 = yesterdayP2.getP() == null ? BigDecimal.ZERO : yesterdayP2.getP();
                BigDecimal p1 = response.getP() == null ? BigDecimal.ZERO : response.getP();
                dayP.getYesterdayP().add(p1.add(p2));
            } else {
                dayP.getYesterdayP().add(null);
            }
        }
        pvResponse.setDayP(dayP);

        // 当年使用量查询
        IndexYearRequest indexYearRequest = new IndexYearRequest();
        indexYearRequest.setYears(new Year[]{Year.now()});
        indexYearRequest.setProjectBizId(projectBizId);
        SubitemIndexEnum[] currentYearIndices = new SubitemIndexEnum[]{
                // 当年购网电量
                SubitemIndexEnum.PURCHASE_NETWORK_ELECTRICITY,
                // 当年光伏发电量
                SubitemIndexEnum.PHOTOVOLTAIC_POWER_GENERATION,
                // 当年光伏上网电量
                SubitemIndexEnum.PV_ON_GRID_ENERGY,
                // 当年光伏直接使用
                SubitemIndexEnum.PV_DIRECT_USE,
                // 当年光伏先储后用
                SubitemIndexEnum.PV_STORE_BEFORE_USE,
                // 当年光伏收益
                SubitemIndexEnum.PHOTOVOLTAIC_REVENUE,

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
                // 上网电量
                SubitemIndexEnum.PV_ON_GRID_ENERGY,
                // 光伏发电量
                SubitemIndexEnum.PHOTOVOLTAIC_POWER_GENERATION,
                // 光伏收益
                SubitemIndexEnum.PHOTOVOLTAIC_REVENUE,
        };
        indexMonthRequest.setIndices(currentMonthIndices);
        Response<Map<YearMonth, Map<SubitemIndexEnum, BigDecimal>>> mapMonthResponse = subitemApi.searchDataByMonth(indexMonthRequest);
        Map<YearMonth, Map<SubitemIndexEnum, BigDecimal>> currentMonths = mapMonthResponse.getResult();
        indexMonthRequest.setMonths(getLastYearMonth(yearMonth));
        Response<Map<YearMonth, Map<SubitemIndexEnum, BigDecimal>>> mapLastYearMonthResponse = subitemApi.searchDataByMonth(indexMonthRequest);
        Map<YearMonth, Map<SubitemIndexEnum, BigDecimal>> lastYearMonths = mapLastYearMonthResponse.getResult();

        // 日使用量查询
        IndexDayRequest indexDayRequest = new IndexDayRequest();
        LocalDate localDate = LocalDate.now();
        LocalDate[] currentMonthDay = getCurrentMonthDay(localDate);
        indexDayRequest.setDays(currentMonthDay);
        indexDayRequest.setProjectBizId(projectBizId);
        SubitemIndexEnum[] currentDayIndices = new SubitemIndexEnum[]{
                // 光伏发电量
                SubitemIndexEnum.PHOTOVOLTAIC_POWER_GENERATION,
        };
        indexDayRequest.setIndices(currentDayIndices);
        Response<Map<LocalDate, Map<SubitemIndexEnum, BigDecimal>>> mapDayResponse = subitemApi.searchDataByDay(indexDayRequest);
        Map<LocalDate, Map<SubitemIndexEnum, BigDecimal>> currentDays = mapDayResponse.getResult();

        // 当年绿电自给比例 % 当年光伏发电量/当年总用电量
        BigDecimal currentYearTotal = getValue(SubitemIndexEnum.PURCHASE_NETWORK_ELECTRICITY, currentYear);
        BigDecimal currentYearPv = getValue(SubitemIndexEnum.PHOTOVOLTAIC_POWER_GENERATION, currentYear);
        if (currentYearTotal == null || currentYearPv == null || currentYearTotal.equals(BigDecimal.ZERO)) {
            pvResponse.setPvVsTotalPercentageCurrentYear(null);
        } else {
            pvResponse.setPvVsTotalPercentageCurrentYear(currentYearPv.divide(currentYearTotal, 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100)));
        }
        // 当年光伏发电消纳比例 1-当年光伏上网电量/当年光伏发电量
        BigDecimal currentYearGrid = getValue(SubitemIndexEnum.PV_ON_GRID_ENERGY, currentYear);
        if (currentYearPv == null || currentYearGrid == null || currentYearPv.equals(BigDecimal.ZERO)) {
            pvResponse.setPvAbsorptivePercentageCurrentYear(null);
        } else {
            pvResponse.setPvAbsorptivePercentageCurrentYear((BigDecimal.ONE.subtract(currentYearGrid.divide(currentYearPv, 4, RoundingMode.HALF_UP))).multiply(new BigDecimal(100)));
        }
        // 当年上网电量
        pvResponse.setElectricityPvEnergyProductionGridCurrentYear(getValue(SubitemIndexEnum.PV_ON_GRID_ENERGY, currentYear));
        // 当年光伏发电量
        pvResponse.setElectricityPvEnergyProductionCurrentYear(getValue(SubitemIndexEnum.PHOTOVOLTAIC_POWER_GENERATION, currentYear));
        // 当年直接使用
        pvResponse.setElectricityPvEnergyProductionLoadCurrentYear(getValue(SubitemIndexEnum.PV_DIRECT_USE, currentYear));
        // 当年先储后用
        pvResponse.setElectricityPvEnergyProductionStorageCurrentYear(getValue(SubitemIndexEnum.PV_STORE_BEFORE_USE, currentYear));
        // 当年收益
        pvResponse.setElectricityPvEnergyProductionFeeCurrentYear(getValue(SubitemIndexEnum.PHOTOVOLTAIC_REVENUE, currentYear));
        // 当月收益
        if (CollUtil.isNotEmpty(currentMonths)) {
            pvResponse.setElectricityPvEnergyProductionFeeCurrentMonth(getValue(SubitemIndexEnum.PHOTOVOLTAIC_REVENUE, currentMonths.get(yearMonth)));
        }
        // 日发电趋势
        PvResponse.DayTrend dayPvEnergyProduction = new PvResponse.DayTrend();
        if (CollUtil.isNotEmpty(currentDays)) {
            currentDays.forEach((key, value) -> {
                int dayOfMonth = key.getDayOfMonth();
                dayPvEnergyProduction.getX().add(dayOfMonth);
                dayPvEnergyProduction.getValue().add(getValue(SubitemIndexEnum.PHOTOVOLTAIC_POWER_GENERATION, value));
            });
        }
        pvResponse.setDayPvEnergyProduction(dayPvEnergyProduction);

        // 月发电趋势
        PvResponse.PvProduction pvProduction = new PvResponse.PvProduction();
        if (CollUtil.isNotEmpty(lastYearMonths)) {
            lastYearMonths.forEach((key, value) -> {
                pvProduction.getLastYear().add(getValue(SubitemIndexEnum.PHOTOVOLTAIC_POWER_GENERATION, value));
                Map<SubitemIndexEnum, BigDecimal> monthData = currentMonths.get(key.plusYears(1));
                BigDecimal pv = getValue(SubitemIndexEnum.PHOTOVOLTAIC_POWER_GENERATION, monthData);
                BigDecimal grid = getValue(SubitemIndexEnum.PV_ON_GRID_ENERGY, monthData);
                pvProduction.getCurrentYear().add(pv);
                if (pv == null || grid == null || pv.equals(BigDecimal.ZERO)) {
                    pvProduction.getAbsorptivePercentage().add(BigDecimal.ZERO);
                } else {
                    pvProduction.getAbsorptivePercentage().add((BigDecimal.ONE.subtract(grid.divide(pv, 4, RoundingMode.HALF_UP))).multiply(new BigDecimal(100)));
                }
                pvProduction.getX().add(key.getMonth().getValue());
            });
        }
        pvResponse.setMonthPvEnergyProduction(pvProduction);

        // 设备id 写死
        Map<String, Object> currentZnb1 = deviceCurrentApi.getDeviceCurrentById(ZNB1).getResult();
        Map<String, Object> currentZnb2 = deviceCurrentApi.getDeviceCurrentById(ZNB2).getResult();
        // 逆变器一
        PvResponse.Znb01 znb01 = pvResponse.getMiddle().getZnb01();
        znb01.setPv1(new PvResponse.Pv(new BigDecimal(String.valueOf(currentZnb1.get("PV1U"))), new BigDecimal(String.valueOf(currentZnb1.get("PV1I")))));
        znb01.setPv2(new PvResponse.Pv(new BigDecimal(String.valueOf(currentZnb1.get("PV3U"))), new BigDecimal(String.valueOf(currentZnb1.get("PV3I")))));
        znb01.setPv3(new PvResponse.Pv(new BigDecimal(String.valueOf(currentZnb1.get("PV5U"))), new BigDecimal(String.valueOf(currentZnb1.get("PV5I")))));
        znb01.setPv4(new PvResponse.Pv(new BigDecimal(String.valueOf(currentZnb1.get("PV7U"))), new BigDecimal(String.valueOf(currentZnb1.get("PV7I")))));
        znb01.setPv5(new PvResponse.Pv(new BigDecimal(String.valueOf(currentZnb1.get("PV9U"))), new BigDecimal(String.valueOf(currentZnb1.get("PV9I")))));
        znb01.setPv6(new PvResponse.Pv(new BigDecimal(String.valueOf(currentZnb1.get("PV11U"))), new BigDecimal(String.valueOf(currentZnb1.get("PV11I")))));
        znb01.setPv7(new PvResponse.Pv(new BigDecimal(String.valueOf(currentZnb1.get("PV12U"))), new BigDecimal(String.valueOf(currentZnb1.get("PV12I")))));
        znb01.setPv8(new PvResponse.Pv(new BigDecimal(String.valueOf(currentZnb1.get("PV13U"))), new BigDecimal(String.valueOf(currentZnb1.get("PV13I")))));
        znb01.setPv9(new PvResponse.Pv(new BigDecimal(String.valueOf(currentZnb1.get("PV15U"))), new BigDecimal(String.valueOf(currentZnb1.get("PV15I")))));
        znb01.setPv10(new PvResponse.Pv(new BigDecimal(String.valueOf(currentZnb1.get("PV17U"))), new BigDecimal(String.valueOf(currentZnb1.get("PV17I")))));
        znb01.setPv11(new PvResponse.Pv(new BigDecimal(String.valueOf(currentZnb1.get("PV19U"))), new BigDecimal(String.valueOf(currentZnb1.get("PV19I")))));
        znb01.setEfficiency(new BigDecimal(String.valueOf(currentZnb1.get("E"))));
        znb01.setTemp(new BigDecimal(String.valueOf(currentZnb1.get("Temperature"))));
        znb01.setUa(new BigDecimal(String.valueOf(currentZnb1.get("Ua"))));
        znb01.setIa(new BigDecimal(String.valueOf(currentZnb1.get("Ia"))));
        znb01.setUb(new BigDecimal(String.valueOf(currentZnb1.get("Ub"))));
        znb01.setIb(new BigDecimal(String.valueOf(currentZnb1.get("Ib"))));
        znb01.setUc(new BigDecimal(String.valueOf(currentZnb1.get("Uc"))));
        znb01.setIc(new BigDecimal(String.valueOf(currentZnb1.get("Ic"))));
        znb01.setP(new BigDecimal(String.valueOf(currentZnb1.get("P"))));
        znb01.setQ(new BigDecimal(String.valueOf(currentZnb1.get("Q"))));
        znb01.setpInput(new BigDecimal(String.valueOf(currentZnb1.get("PInput"))));
        znb01.setCurrentElectricity(new BigDecimal(String.valueOf(currentZnb1.get("EpexpDay"))));
        // 逆变器二
        PvResponse.Znb02 znb02 = pvResponse.getMiddle().getZnb02();
        znb02.setPv1(new PvResponse.Pv(new BigDecimal(String.valueOf(currentZnb2.get("PV1U"))), new BigDecimal(String.valueOf(currentZnb2.get("PV1I")))));
        znb02.setPv2(new PvResponse.Pv(new BigDecimal(String.valueOf(currentZnb2.get("PV2U"))), new BigDecimal(String.valueOf(currentZnb2.get("PV2I")))));
        znb02.setEfficiency(new BigDecimal(String.valueOf(currentZnb2.get("E"))));
        znb02.setTemp(new BigDecimal(String.valueOf(currentZnb2.get("Temperature"))));
        znb02.setUa(new BigDecimal(String.valueOf(currentZnb2.get("Ua"))));
        znb02.setIa(new BigDecimal(String.valueOf(currentZnb2.get("Ia"))));
        znb02.setUb(new BigDecimal(String.valueOf(currentZnb2.get("Ub"))));
        znb02.setIb(new BigDecimal(String.valueOf(currentZnb2.get("Ib"))));
        znb02.setUc(new BigDecimal(String.valueOf(currentZnb2.get("Uc"))));
        znb02.setIc(new BigDecimal(String.valueOf(currentZnb2.get("Ic"))));
        znb02.setP(new BigDecimal(String.valueOf(currentZnb2.get("P"))));
        znb02.setQ(new BigDecimal(String.valueOf(currentZnb2.get("Q"))));
        znb02.setpInput(new BigDecimal(String.valueOf(currentZnb2.get("PInput"))));
        znb02.setCurrentElectricity(new BigDecimal(String.valueOf(currentZnb2.get("EpexpDay"))));

        // 发电实时功率
        pvResponse.setCurrentTotalP(new BigDecimal(String.valueOf(currentZnb1.get("P"))).add(new BigDecimal(String.valueOf(currentZnb2.get("P")))));
        return pvResponse;
    }

    /**
     * 储能
     *
     * @param projectBizId 项目业务id
     */
    public StorageResponse storage(String projectBizId) {
        StorageResponse storageResponse = new StorageResponse();
        // 所属省市名称
        Response<ProjectDetailsResponse> projectDetails = projectApi.getProjectDetails(projectBizId);
        String addressName = projectDetails.getResult().getAddressName();
        storageResponse.setCityName(addressName);
        // 当月电价
        // 获取当月电价信息
        Response<List<ProjectCnfTimePeriodResponse>> electricityPriceResponse = projectCnfTimePeriodApi.getElectricityPrice(projectBizId);
        List<ProjectCnfTimePeriodResponse> electricityPriceResult = electricityPriceResponse.getResult();
        StorageResponse.ElectricityPrice electricityPrice = new StorageResponse.ElectricityPrice();

        // 如果电价信息不为空
        if (CollUtil.isNotEmpty(electricityPriceResult)) {
            // 对电价信息按照开始时间进行排序
            List<ProjectCnfTimePeriodResponse> sortedElectricityPriceResult = electricityPriceResult.stream()
                    .sorted(Comparator.comparing(ProjectCnfTimePeriodResponse::getTimeBegin))
                    .toList();

            // 遍历电价信息
            for (ProjectCnfTimePeriodResponse projectCnfTimePeriodResponse : sortedElectricityPriceResult) {
                Integer timeBegin = projectCnfTimePeriodResponse.getTimeBegin();
                Integer timeEnd = projectCnfTimePeriodResponse.getTimeEnd();

                // 根据开始时间和结束时间生成每个小时的电价信息
                for (int i = timeBegin; i <= timeEnd; i++) {
                    electricityPrice.getX().add(i);
                    electricityPrice.getValue().add(projectCnfTimePeriodResponse.getPrice());
                }
            }
        }

        storageResponse.setElectricityPrice(electricityPrice);
        // 日充放电功率
        BasePRequest basePRequest = new BasePRequest();
        LocalDateTime now = LocalDateTime.now();
        basePRequest.setProductBizId(STORAGE_PRODUCT_BIZ_ID);
        basePRequest.setDeviceIds(Collections.singletonList(STORAGE_DEVICE_BIZ_ID));
        LocalDateTime todayZero = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 0, 0);
        basePRequest.setStart(todayZero);
        LocalDateTime todayEnd = todayZero.plusDays(1L);
        basePRequest.setEnd(todayEnd);
        Response<List<GscnPResponse>> gscnPResponse = deviceHistoryApi.getGscnPResponse(basePRequest);
        List<GscnPResponse> gscnPResponseResult = gscnPResponse.getResult();
        StorageResponse.DayP dayP = new StorageResponse.DayP();
        for (GscnPResponse pResponse : gscnPResponseResult) {
            String time = pResponse.getTime();
            ZonedDateTime zonedDateTime = ZonedDateTime.parse(time);
            ZonedDateTime adjustedDateTime = zonedDateTime.withZoneSameInstant(ZoneOffset.ofHours(8));
            LocalDateTime localDateTime = adjustedDateTime.toLocalDateTime();
            dayP.getX().add(DateUtil.format(localDateTime, DatePattern.NORM_DATETIME_PATTERN));
            if (now.isAfter(localDateTime)) {
                dayP.getP().add(pResponse.getP());
            }
        }
        storageResponse.setDayP(dayP);

        // 当年使用量查询
        IndexYearRequest indexYearRequest = new IndexYearRequest();
        indexYearRequest.setYears(new Year[]{Year.now()});
        indexYearRequest.setProjectBizId(projectBizId);
        SubitemIndexEnum[] currentYearIndices = new SubitemIndexEnum[]{
                // 储能收益
                SubitemIndexEnum.ENERGY_STORAGE_REVENUE,
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
                // 储能收益
                SubitemIndexEnum.ENERGY_STORAGE_REVENUE,
                // 储市电量
                SubitemIndexEnum.STORAGE_OF_ELECTRICITY_IN_THE_CITY,
                // 储光电量
                SubitemIndexEnum.STORAGE_CAPACITY,
                // 尖放电
                SubitemIndexEnum.ENERGY_STORAGE_AND_DISCHARGE_CAPACITY_TIP,
                // 峰放电
                SubitemIndexEnum.ENERGY_STORAGE_AND_DISCHARGE_CAPACITY_PEAK,
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
                // 储放电量
                SubitemIndexEnum.ENERGY_STORAGE_AND_DISCHARGE_CAPACITY,
                // 储充电量
                SubitemIndexEnum.STORAGE_AND_CHARGING_CAPACITY,
        };
        indexDayRequest.setIndices(currentDayIndices);
        Response<Map<LocalDate, Map<SubitemIndexEnum, BigDecimal>>> mapDayResponse = subitemApi.searchDataByDay(indexDayRequest);
        Map<LocalDate, Map<SubitemIndexEnum, BigDecimal>> currentDays = mapDayResponse.getResult();

        // 累计使用量查询
        IndexCumulativeRequest indexCumulativeRequest = new IndexCumulativeRequest();
        indexCumulativeRequest.setProjectBizId(projectBizId);
        SubitemIndexEnum[] cumulativeIndices = new SubitemIndexEnum[]{
                // 累计充电量
                SubitemIndexEnum.STORAGE_AND_CHARGING_CAPACITY,
                // 累计储能放电量
                SubitemIndexEnum.ENERGY_STORAGE_AND_DISCHARGE_CAPACITY,
        };
        indexCumulativeRequest.setIndices(cumulativeIndices);
        Response<Map<SubitemIndexEnum, BigDecimal>> cumulativeResponse = subitemApi.searchDataByCumulative(indexCumulativeRequest);
        Map<SubitemIndexEnum, BigDecimal> cumulative = cumulativeResponse.getResult();

        // 当月收益
        if (CollUtil.isNotEmpty(currentMonths)) {
            storageResponse.setElectricityStorageEnergyNetFeeCurrentMonth(getValue(SubitemIndexEnum.ENERGY_STORAGE_REVENUE, currentMonths.get(yearMonth)));
        }
        // 当年收益
        storageResponse.setElectricityStorageEnergyNetFeeCurrentYear(getValue(SubitemIndexEnum.ENERGY_STORAGE_REVENUE, currentYear));
        // 累计充电量
        storageResponse.setElectricityStorageEnergyProductionTotal(getValue(SubitemIndexEnum.STORAGE_AND_CHARGING_CAPACITY, cumulative));
        // 累计放电量
        storageResponse.setElectricityStorageEnergyUsageTotal(getValue(SubitemIndexEnum.ENERGY_STORAGE_AND_DISCHARGE_CAPACITY, cumulative));
        // 当月充电趋势
        StorageResponse.Usage monthUsage = new StorageResponse.Usage();
        if (CollUtil.isNotEmpty(currentMonths)) {
            currentMonths.forEach((key, value) -> {
                monthUsage.getStorageEnergyUsageGrid().add(getValue(SubitemIndexEnum.STORAGE_OF_ELECTRICITY_IN_THE_CITY, value));
                monthUsage.getStorageEnergyUsagePv().add(getValue(SubitemIndexEnum.STORAGE_CAPACITY, value));
                monthUsage.getX().add(key.getMonth().getValue());
            });
        }
        storageResponse.setMonthUsage(monthUsage);
        // 当月放电趋势
        StorageResponse.Production monthProduction = new StorageResponse.Production();
        if (CollUtil.isNotEmpty(currentMonths)) {
            currentMonths.forEach((key, value) -> {
                monthProduction.getStorageEnergyProductionTip().add(getValue(SubitemIndexEnum.ENERGY_STORAGE_AND_DISCHARGE_CAPACITY_TIP, value));
                monthProduction.getStorageEnergyProductionPeak().add(getValue(SubitemIndexEnum.ENERGY_STORAGE_AND_DISCHARGE_CAPACITY_PEAK, value));
                monthProduction.getX().add(key.getMonth().getValue());
            });
        }
        storageResponse.setMonthProduction(monthProduction);

        // 设备id 写死
        Map<String, Object> currentStorage = deviceCurrentApi.getDeviceCurrentById(STORAGE_DEVICE_BIZ_ID).getResult();
        // 实时功率
        storageResponse.setCurrentP(new BigDecimal(String.valueOf(currentStorage.get("P"))));
        // SOC
        storageResponse.setSoc(new BigDecimal(String.valueOf(currentStorage.get("SOC"))));
        // SOH
        storageResponse.setSoh(new BigDecimal(String.valueOf(currentStorage.get("SOH"))));

        // 中间部分
        // 运行模式
        storageResponse.getMiddle().setRunMode("并网运行");
        // 控制方式
        storageResponse.getMiddle().setControlType("收益最优");
        // PCS状态
        storageResponse.getMiddle().setPcsStatus(StoragePcsRSTEnum.getName(String.valueOf(currentStorage.get("pcsRST"))));
        // 当日放电量
        storageResponse.getMiddle().setProductionCurrentDay(new BigDecimal(String.valueOf(currentStorage.get("dayDischargeE"))));
        // 当日充电量
        storageResponse.getMiddle().setUsageCurrentDay(new BigDecimal(String.valueOf(currentStorage.get("dayChargeE"))));
        // 总压
        storageResponse.getMiddle().setBatteryU(new BigDecimal(String.valueOf(currentStorage.get("BatteryU"))));
        // 电流
        storageResponse.getMiddle().setBatteryI(new BigDecimal(String.valueOf(currentStorage.get("BatteryI"))));
        // 最高单体电压
        storageResponse.getMiddle().setMaxVoltage(currentStorage.get("UnitMaxU") == null ? null : new BigDecimal(String.valueOf(currentStorage.get("UnitMaxU"))));
        // 最低单体电压
        storageResponse.getMiddle().setMinVoltage(currentStorage.get("UnitMinU") == null ? null : new BigDecimal(String.valueOf(currentStorage.get("UnitMinU"))));
        // 最高单体温度
        storageResponse.getMiddle().setMaxTemp(currentStorage.get("UnitMaxT") == null ? null : new BigDecimal(String.valueOf(currentStorage.get("UnitMaxT"))));
        // 最低单体温度
        storageResponse.getMiddle().setMinTemp(currentStorage.get("UnitMinT") == null ? null : new BigDecimal(String.valueOf(currentStorage.get("UnitMinT"))));
        // Uab
        storageResponse.getMiddle().setUab(new BigDecimal(String.valueOf(currentStorage.get("Uab"))));
        // Ubc
        storageResponse.getMiddle().setUbc(new BigDecimal(String.valueOf(currentStorage.get("Ubc"))));
        // Uca
        storageResponse.getMiddle().setUca(new BigDecimal(String.valueOf(currentStorage.get("Uca"))));
        // Ia
        storageResponse.getMiddle().setIa(new BigDecimal(String.valueOf(currentStorage.get("Ia"))));
        // Ib
        storageResponse.getMiddle().setIb(new BigDecimal(String.valueOf(currentStorage.get("Ib"))));
        // Ic
        storageResponse.getMiddle().setIc(new BigDecimal(String.valueOf(currentStorage.get("Ic"))));
        // F
        storageResponse.getMiddle().setF(new BigDecimal(String.valueOf(currentStorage.get("F"))));
        // P
        storageResponse.getMiddle().setP(new BigDecimal(String.valueOf(currentStorage.get("P"))));
        // Q
        storageResponse.getMiddle().setQ(new BigDecimal(String.valueOf(currentStorage.get("Q"))));
        // PF
        storageResponse.getMiddle().setPf(new BigDecimal(String.valueOf(currentStorage.get("PF"))));
        return storageResponse;
    }

    /**
     * 充电桩
     *
     * @param projectBizId 项目业务id
     */
    public ChargeResponse charge(String projectBizId) {
        ChargeResponse chargeResponse = new ChargeResponse();
        // 当日充电功率曲线
        BasePRequest basePRequest = new BasePRequest();
        basePRequest.setProductBizId(STATION_PRODUCT_BIZ_ID);
        List<String> bizDeviceIds = Arrays.asList(AL_STATION1, AL_STATION2, AL_STATION3, AL_STATION4, AL_STATION5, DIR_STATION1);
        basePRequest.setDeviceIds(List.of(AL_STATION1));
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 0, 0);
        basePRequest.setStart(start);
        basePRequest.setEnd(start.plusDays(1L));
        Response<List<ChargePResponse>> chargePResponse1 = deviceHistoryApi.getChargePResponse(basePRequest);
        basePRequest.setDeviceIds(List.of(AL_STATION2));
        Response<List<ChargePResponse>> chargePResponse2 = deviceHistoryApi.getChargePResponse(basePRequest);
        basePRequest.setDeviceIds(List.of(AL_STATION3));
        Response<List<ChargePResponse>> chargePResponse3 = deviceHistoryApi.getChargePResponse(basePRequest);
        basePRequest.setDeviceIds(List.of(AL_STATION4));
        Response<List<ChargePResponse>> chargePResponse4 = deviceHistoryApi.getChargePResponse(basePRequest);
        basePRequest.setDeviceIds(List.of(AL_STATION5));
        Response<List<ChargePResponse>> chargePResponse5 = deviceHistoryApi.getChargePResponse(basePRequest);
        basePRequest.setDeviceIds(List.of(DIR_STATION1));
        Response<List<ChargePResponse>> chargePResponse6 = deviceHistoryApi.getChargePResponse(basePRequest);
        List<ChargePResponse> chargePResponseResult1 = chargePResponse1.getResult();
        List<ChargePResponse> chargePResponseResult2 = chargePResponse2.getResult();
        List<ChargePResponse> chargePResponseResult3 = chargePResponse3.getResult();
        List<ChargePResponse> chargePResponseResult4 = chargePResponse4.getResult();
        List<ChargePResponse> chargePResponseResult5 = chargePResponse5.getResult();
        List<ChargePResponse> chargePResponseResult6 = chargePResponse6.getResult();
        ChargeResponse.DayP dayP = new ChargeResponse.DayP();
        if (CollUtil.isNotEmpty(chargePResponseResult1)) {
            for (ChargePResponse pResponse : chargePResponseResult1) {
                String time = pResponse.getTime();
                ChargePResponse chargeP2 = chargePResponseResult2.stream().filter(item -> item.getTime().equals(time)).findAny().orElse(new ChargePResponse());
                ChargePResponse chargeP3 = chargePResponseResult3.stream().filter(item -> item.getTime().equals(time)).findAny().orElse(new ChargePResponse());
                ChargePResponse chargeP4 = chargePResponseResult4.stream().filter(item -> item.getTime().equals(time)).findAny().orElse(new ChargePResponse());
                ChargePResponse chargeP5 = chargePResponseResult5.stream().filter(item -> item.getTime().equals(time)).findAny().orElse(new ChargePResponse());
                ChargePResponse chargeP6 = chargePResponseResult6.stream().filter(item -> item.getTime().equals(time)).findAny().orElse(new ChargePResponse());
                ZonedDateTime zonedDateTime = ZonedDateTime.parse(time);
                ZonedDateTime adjustedDateTime = zonedDateTime.withZoneSameInstant(ZoneOffset.ofHours(8));
                LocalDateTime localDateTime = adjustedDateTime.toLocalDateTime();
                dayP.getX().add(DateUtil.format(localDateTime, DatePattern.NORM_DATETIME_PATTERN));
                if (now.isAfter(localDateTime)) {
                    BigDecimal p1 = pResponse.getP() == null ? BigDecimal.ZERO : pResponse.getP();
                    BigDecimal p2 = chargeP2.getP() == null ? BigDecimal.ZERO : chargeP2.getP();
                    BigDecimal p3 = chargeP3.getP() == null ? BigDecimal.ZERO : chargeP3.getP();
                    BigDecimal p4 = chargeP4.getP() == null ? BigDecimal.ZERO : chargeP4.getP();
                    BigDecimal p5 = chargeP5.getP() == null ? BigDecimal.ZERO : chargeP5.getP();
                    BigDecimal p6 = chargeP6.getP() == null ? BigDecimal.ZERO : chargeP6.getP();
                    dayP.getP().add(p1.add(p2).add(p3).add(p4).add(p5).add(p6));
                }
            }
        }
        chargeResponse.setDayP(dayP);

        // 日使用量查询
        IndexDayRequest indexDayRequest = new IndexDayRequest();
        LocalDate localDate = LocalDate.now();
        LocalDate[] currentMonthDay = getCurrentMonthDay(localDate);
        indexDayRequest.setDays(currentMonthDay);
        indexDayRequest.setProjectBizId(projectBizId);
        SubitemIndexEnum[] currentDayIndices = new SubitemIndexEnum[]{
                // 充电桩充电量
                SubitemIndexEnum.ELECTRIC_QUANTITY_OF_CHARGING_STATION,
        };
        indexDayRequest.setIndices(currentDayIndices);
        Response<Map<LocalDate, Map<SubitemIndexEnum, BigDecimal>>> mapDayResponse = subitemApi.searchDataByDay(indexDayRequest);
        Map<LocalDate, Map<SubitemIndexEnum, BigDecimal>> currentDays = mapDayResponse.getResult();

        // 月度使用量查询
        IndexMonthRequest indexMonthRequest = new IndexMonthRequest();
        YearMonth yearMonth = YearMonth.now();
        YearMonth[] currentYearMonth = getCurrentYearMonth(yearMonth);
        indexMonthRequest.setMonths(currentYearMonth);
        indexMonthRequest.setProjectBizId(projectBizId);
        SubitemIndexEnum[] currentMonthIndices = new SubitemIndexEnum[]{
                // 充电桩充电量
                SubitemIndexEnum.ELECTRIC_QUANTITY_OF_CHARGING_STATION,
                // 充电桩收益
                SubitemIndexEnum.INCOME_OF_CHARGING_STATION,
        };
        indexMonthRequest.setIndices(currentMonthIndices);
        Response<Map<YearMonth, Map<SubitemIndexEnum, BigDecimal>>> mapMonthResponse = subitemApi.searchDataByMonth(indexMonthRequest);
        Map<YearMonth, Map<SubitemIndexEnum, BigDecimal>> currentMonths = mapMonthResponse.getResult();

        // 当年使用量查询
        IndexYearRequest indexYearRequest = new IndexYearRequest();
        indexYearRequest.setYears(new Year[]{Year.now()});
        indexYearRequest.setProjectBizId(projectBizId);
        SubitemIndexEnum[] currentYearIndices = new SubitemIndexEnum[]{
                // 充电桩充电量
                SubitemIndexEnum.ELECTRIC_QUANTITY_OF_CHARGING_STATION,
                // 充电桩收益
                SubitemIndexEnum.INCOME_OF_CHARGING_STATION,
        };
        indexYearRequest.setIndices(currentYearIndices);
        Response<Map<Year, Map<SubitemIndexEnum, BigDecimal>>> mapYearResponse = subitemApi.searchDataByYear(indexYearRequest);
        Map<SubitemIndexEnum, BigDecimal> currentYear = mapYearResponse.getResult().get(Year.now());

        // 当日充电量
        if (CollUtil.isNotEmpty(currentDays)) {
            chargeResponse.setElectricityStorageEnergyUsageCurrentDay(getValue(SubitemIndexEnum.ELECTRIC_QUANTITY_OF_CHARGING_STATION, currentDays.get(localDate)));
        }
        // 当月充电量
        if (CollUtil.isNotEmpty(currentMonths)) {
            chargeResponse.setElectricityStorageEnergyUsageCurrentMonth(getValue(SubitemIndexEnum.ELECTRIC_QUANTITY_OF_CHARGING_STATION, currentMonths.get(yearMonth)));
        }
        // 当年充电量
        chargeResponse.setElectricityStorageEnergyUsageCurrentYear(getValue(SubitemIndexEnum.ELECTRIC_QUANTITY_OF_CHARGING_STATION, currentYear));
        // 当月收益
        if (CollUtil.isNotEmpty(currentMonths)) {
            chargeResponse.setElectricityStorageEnergyFeeCurrentMonth(getValue(SubitemIndexEnum.INCOME_OF_CHARGING_STATION, currentMonths.get(yearMonth)));
        }
        // 当年收益
        chargeResponse.setElectricityStorageEnergyFeeCurrentYear(getValue(SubitemIndexEnum.INCOME_OF_CHARGING_STATION, currentYear));
        // 日充电量趋势
        ChargeResponse.DayTrend electricityStorageEnergyUsageDayTrend = new ChargeResponse.DayTrend();
        if (CollUtil.isNotEmpty(currentDays)) {
            currentDays.forEach((key, value) -> {
                electricityStorageEnergyUsageDayTrend.getX().add(key.getDayOfMonth());
                electricityStorageEnergyUsageDayTrend.getValue().add(getValue(SubitemIndexEnum.ELECTRIC_QUANTITY_OF_CHARGING_STATION, value));
            });
        }
        chargeResponse.setElectricityStorageEnergyUsageDayTrend(electricityStorageEnergyUsageDayTrend);
        // 月充电量趋势
        ChargeResponse.MonthTrend electricityStorageEnergyUsageMonthTrend = new ChargeResponse.MonthTrend();
        if (CollUtil.isNotEmpty(currentMonths)) {
            currentMonths.forEach((key, value) -> {
                electricityStorageEnergyUsageMonthTrend.getX().add(key.getMonth().getValue());
                electricityStorageEnergyUsageMonthTrend.getValue().add(getValue(SubitemIndexEnum.ELECTRIC_QUANTITY_OF_CHARGING_STATION, value));
            });
        }
        chargeResponse.setElectricityStorageEnergyUsageMonthTrend(electricityStorageEnergyUsageMonthTrend);
        // 充电量排名
        Response<List<DeviceElectricityResponse>> yearResponse = deviceElectricityApi.searchChargingYearTotal(bizDeviceIds);
        List<DeviceElectricityResponse> yearResponseResult = yearResponse.getResult();
        List<ChargeResponse.Device> electricityStorageEnergyUsageRanking = new ArrayList<>();
        yearResponseResult.stream().sorted(Comparator.comparing(DeviceElectricityResponse::getEpimportTotal, Comparator.reverseOrder()))
                .forEach(item -> {
                    ChargeResponse.Device device = new ChargeResponse.Device();
                    device.setDeviceBizId(item.getBizDeviceId());
                    device.setName(DEVICE_ID_NAME_MAP.get(item.getBizDeviceId()));
                    device.setValue(item.getEpimportTotal());
                    electricityStorageEnergyUsageRanking.add(device);
                });
        chargeResponse.setElectricityStorageEnergyUsageRanking(electricityStorageEnergyUsageRanking);

        // 设备id 写死
        Map<String, Object> currentAlStation1 = deviceCurrentApi.getDeviceCurrentById(AL_STATION1).getResult();
        Map<String, Object> currentAlStation2 = deviceCurrentApi.getDeviceCurrentById(AL_STATION2).getResult();
        Map<String, Object> currentAlStation3 = deviceCurrentApi.getDeviceCurrentById(AL_STATION3).getResult();
        Map<String, Object> currentAlStation4 = deviceCurrentApi.getDeviceCurrentById(AL_STATION4).getResult();
        Map<String, Object> currentAlStation5 = deviceCurrentApi.getDeviceCurrentById(AL_STATION5).getResult();
        Map<String, Object> currentDirStation1 = deviceCurrentApi.getDeviceCurrentById(DIR_STATION1).getResult();
        DeviceDayRequest deviceDayRequest = new DeviceDayRequest();
        deviceDayRequest.setDay(LocalDate.now());
        deviceDayRequest.setDeviceBizId(bizDeviceIds.toArray(new String[0]));
        Map<String, BigDecimal> stationInChargeResult = subitemApi.searchDeviceDataByDay(deviceDayRequest).getResult();
        BigDecimal alP1 = new BigDecimal(String.valueOf(currentAlStation1.get("P")));
        chargeResponse.getMiddle().getAlStation01().setInChargeP(alP1);
        chargeResponse.getMiddle().getAlStation01().setStatus(alP1.compareTo(BigDecimal.valueOf(0.5)) > 0 ? "充电中" : "充电停止");
        BigDecimal alP2 = new BigDecimal(String.valueOf(currentAlStation2.get("P")));
        chargeResponse.getMiddle().getAlStation02().setInChargeP(alP2);
        chargeResponse.getMiddle().getAlStation02().setStatus(alP2.compareTo(BigDecimal.valueOf(0.5)) > 0 ? "充电中" : "充电停止");
        BigDecimal alP3 = new BigDecimal(String.valueOf(currentAlStation3.get("P")));
        chargeResponse.getMiddle().getAlStation03().setInChargeP(alP3);
        chargeResponse.getMiddle().getAlStation03().setStatus(alP3.compareTo(BigDecimal.valueOf(0.5)) > 0 ? "充电中" : "充电停止");
        BigDecimal alP4 = new BigDecimal(String.valueOf(currentAlStation4.get("P")));
        chargeResponse.getMiddle().getAlStation04().setInChargeP(alP4);
        chargeResponse.getMiddle().getAlStation04().setStatus(alP4.compareTo(BigDecimal.valueOf(0.5)) > 0 ? "充电中" : "充电停止");
        BigDecimal alP5 = new BigDecimal(String.valueOf(currentAlStation5.get("P")));
        chargeResponse.getMiddle().getAlStation05().setInChargeP(alP5);
        chargeResponse.getMiddle().getAlStation05().setStatus(alP5.compareTo(BigDecimal.valueOf(0.5)) > 0 ? "充电中" : "充电停止");
        BigDecimal dirP1 = new BigDecimal(String.valueOf(currentDirStation1.get("P")));
        chargeResponse.getMiddle().getDirStation01().setInChargeP(dirP1);
        chargeResponse.getMiddle().getDirStation01().setStatus(dirP1.compareTo(BigDecimal.valueOf(0.5)) > 0 ? "充电中" : "充电停止");
        if (CollUtil.isNotEmpty(stationInChargeResult)) {
            chargeResponse.getMiddle().getAlStation01().setInChargeCurrentDay(stationInChargeResult.get(AL_STATION1));
            chargeResponse.getMiddle().getAlStation02().setInChargeCurrentDay(stationInChargeResult.get(AL_STATION2));
            chargeResponse.getMiddle().getAlStation03().setInChargeCurrentDay(stationInChargeResult.get(AL_STATION3));
            chargeResponse.getMiddle().getAlStation04().setInChargeCurrentDay(stationInChargeResult.get(AL_STATION4));
            chargeResponse.getMiddle().getAlStation05().setInChargeCurrentDay(stationInChargeResult.get(AL_STATION5));
            chargeResponse.getMiddle().getDirStation01().setInChargeCurrentDay(stationInChargeResult.get(DIR_STATION1));
        }
        return chargeResponse;
    }

    private JSONObject getIbsaas(String url) {
        String response = restTemplate.getForObject(url, String.class);
        if (StringUtils.isNotBlank(response)) {
            return JSONObject.parseObject(response);
        }
        return null;
    }

    private String getIbsaasSid() {
        JSONObject loginRequest = JSON.parseObject("{\"userCode\": \"test\",\"password\": \"098f6bcd4621d373cade4e832627b4f6\"}");
        String response = restTemplate.postForObject("http://47.102.37.61:8885/ibsaas/login/username-login", loginRequest, String.class);
        JSONObject result = JSONObject.parseObject(response);
        return result.getJSONObject("result").getString("sid");
    }

    /**
     * 暖通
     *
     * @param projectBizId 项目业务id
     */
    public HvacResponse hvac(String projectBizId) {
        HvacResponse hvacResponse = new HvacResponse();

        //请求sso sid
        String sid = this.getIbsaasSid();

        //新风机
        JSONObject response = this.getIbsaas("http://47.102.37.61:8885/ibsaas/new-fan/overview?sid=".concat(sid));
        JSONObject air1 = response.getJSONArray("result").getJSONObject(0);
        JSONObject air2 = response.getJSONArray("result").getJSONObject(1);
        JSONObject air3 = response.getJSONArray("result").getJSONObject(2);
        JSONObject air4 = response.getJSONArray("result").getJSONObject(3);
        hvacResponse.getAirs().add(new HvacResponse.Air(air1.getString("goWindTemp"), IbssasEnum.getName(air1.getString("runningMode")), air1.getString("onOff")));
        hvacResponse.getAirs().add(new HvacResponse.Air(air2.getString("goWindTemp"), IbssasEnum.getName(air2.getString("runningMode")), air2.getString("onOff")));
        hvacResponse.getAirs().add(new HvacResponse.Air(air3.getString("goWindTemp"), IbssasEnum.getName(air3.getString("runningMode")), air3.getString("onOff")));
        hvacResponse.getAirs().add(new HvacResponse.Air(air4.getString("goWindTemp"), IbssasEnum.getName(air4.getString("runningMode")), air4.getString("onOff")));

        //主机
        JSONObject responseHost = this.getIbsaas("http://47.102.37.61:8885/ibsaas/achp/detail/overview?sid=".concat(sid));
        JSONObject host1 = responseHost.getJSONObject("result").getJSONArray("low").getJSONObject(0);
        JSONObject host2 = responseHost.getJSONObject("result").getJSONArray("low").getJSONObject(1);
        JSONObject host3 = responseHost.getJSONObject("result").getJSONArray("low").getJSONObject(2);
        JSONObject host4 = responseHost.getJSONObject("result").getJSONArray("high").getJSONObject(0);
        JSONObject host5 = responseHost.getJSONObject("result").getJSONArray("high").getJSONObject(1);
        JSONObject host6 = responseHost.getJSONObject("result").getJSONArray("high").getJSONObject(2);
        hvacResponse.getHosts().add(new HvacResponse.Host(host1.getString("adSysBackWaterTemp"), host1.getString("adSysGoWaterTemp"), host1.getString("adOnOffState")));
        hvacResponse.getHosts().add(new HvacResponse.Host(host2.getString("adSysBackWaterTemp"), host2.getString("adSysGoWaterTemp"), host2.getString("adOnOffState")));
        hvacResponse.getHosts().add(new HvacResponse.Host(host3.getString("adSysBackWaterTemp"), host3.getString("adSysGoWaterTemp"), host3.getString("adOnOffState")));
        hvacResponse.getHosts().add(new HvacResponse.Host(host4.getString("adSysBackWaterTemp"), host4.getString("adSysGoWaterTemp"), host4.getString("adOnOffState")));
        hvacResponse.getHosts().add(new HvacResponse.Host(host5.getString("adSysBackWaterTemp"), host5.getString("adSysGoWaterTemp"), host5.getString("adOnOffState")));
        hvacResponse.getHosts().add(new HvacResponse.Host(host6.getString("adSysBackWaterTemp"), host6.getString("adSysGoWaterTemp"), host6.getString("adOnOffState")));

        //风机盘管末端
        JSONObject responseFancoil = this.getIbsaas("http://47.102.37.61:9097/ibsaas/web/screen/total");
        String totalNum = responseFancoil.getJSONObject("result").getJSONObject("FAN_COIL").getString("totalNum");
        String onNum = responseFancoil.getJSONObject("result").getJSONObject("FAN_COIL").getString("onNum");
        hvacResponse.setFancoilOn(onNum);
        hvacResponse.setFancoilOff(NumberUtil.sub(totalNum, onNum).toPlainString());

        // 日使用量查询
        IndexDayRequest indexDayRequest = new IndexDayRequest();
        LocalDate localDate = LocalDate.now();
        LocalDate[] currentMonthDay = getCurrentMonthDay(localDate);
        indexDayRequest.setDays(currentMonthDay);
        indexDayRequest.setProjectBizId(projectBizId);
        SubitemIndexEnum[] currentDayIndices = new SubitemIndexEnum[]{
                // 暖通用电量
                SubitemIndexEnum.WARM_UNIVERSAL_POWER,
        };
        indexDayRequest.setIndices(currentDayIndices);
        Response<Map<LocalDate, Map<SubitemIndexEnum, BigDecimal>>> mapDayResponse = subitemApi.searchDataByDay(indexDayRequest);
        Map<LocalDate, Map<SubitemIndexEnum, BigDecimal>> currentDays = mapDayResponse.getResult();

        // 月度使用量查询
        IndexMonthRequest indexMonthRequest = new IndexMonthRequest();
        YearMonth yearMonth = YearMonth.now();
        YearMonth[] currentYearMonth = getCurrentYearMonth(yearMonth);
        indexMonthRequest.setMonths(currentYearMonth);
        indexMonthRequest.setProjectBizId(projectBizId);
        SubitemIndexEnum[] currentMonthIndices = new SubitemIndexEnum[]{
                // 暖通用电量
                SubitemIndexEnum.WARM_UNIVERSAL_POWER,
        };
        indexMonthRequest.setIndices(currentMonthIndices);
        Response<Map<YearMonth, Map<SubitemIndexEnum, BigDecimal>>> mapMonthResponse = subitemApi.searchDataByMonth(indexMonthRequest);
        Map<YearMonth, Map<SubitemIndexEnum, BigDecimal>> currentMonths = mapMonthResponse.getResult();
        indexMonthRequest.setMonths(getLastYearMonth(yearMonth));
        Response<Map<YearMonth, Map<SubitemIndexEnum, BigDecimal>>> mapLastYearMonthResponse = subitemApi.searchDataByMonth(indexMonthRequest);
        Map<YearMonth, Map<SubitemIndexEnum, BigDecimal>> lastYearMonths = mapLastYearMonthResponse.getResult();

        // 当年使用量查询
        IndexYearRequest indexYearRequest = new IndexYearRequest();
        indexYearRequest.setYears(new Year[]{Year.now()});
        indexYearRequest.setProjectBizId(projectBizId);
        SubitemIndexEnum[] currentYearIndices = new SubitemIndexEnum[]{
                // 暖通用电量
                SubitemIndexEnum.WARM_UNIVERSAL_POWER,
        };
        indexYearRequest.setIndices(currentYearIndices);
        Response<Map<Year, Map<SubitemIndexEnum, BigDecimal>>> mapYearResponse = subitemApi.searchDataByYear(indexYearRequest);
        Map<SubitemIndexEnum, BigDecimal> currentYear = mapYearResponse.getResult().get(Year.now());

        // 多参数传感器 合并求平均值
        Map<String, Object> params1 = deviceCurrentApi.getDeviceCurrentById(MULTI_DEVICE_BIZ_ID1).getResult();
        Map<String, Object> params2 = deviceCurrentApi.getDeviceCurrentById(MULTI_DEVICE_BIZ_ID2).getResult();
        Map<String, Object> params3 = deviceCurrentApi.getDeviceCurrentById(MULTI_DEVICE_BIZ_ID3).getResult();
        Map<String, Object> params4 = deviceCurrentApi.getDeviceCurrentById(MULTI_DEVICE_BIZ_ID4).getResult();
        // 室内平均温度
        hvacResponse.setIndoorAvgTemp((new BigDecimal(String.valueOf(params1.get("Temperature")))
                .add(new BigDecimal(String.valueOf(params2.get("Temperature"))))
                .add(new BigDecimal(String.valueOf(params3.get("Temperature"))))
                .add(new BigDecimal(String.valueOf(params4.get("Temperature"))))
        ).divide(new BigDecimal("4"), 2, RoundingMode.HALF_UP));
        // 室内平均湿度
        hvacResponse.setIndoorAvgHumidity((new BigDecimal(String.valueOf(params1.get("Humidity")))
                .add(new BigDecimal(String.valueOf(params2.get("Humidity"))))
                .add(new BigDecimal(String.valueOf(params3.get("Humidity"))))
                .add(new BigDecimal(String.valueOf(params4.get("Humidity"))))
        ).divide(new BigDecimal("4"), 2, RoundingMode.HALF_UP));
        // 室内平均甲醛
        hvacResponse.setIndoorAvgFormaldehyde((new BigDecimal(String.valueOf(params1.get("HCHO")))
                .add(new BigDecimal(String.valueOf(params2.get("HCHO"))))
                .add(new BigDecimal(String.valueOf(params3.get("HCHO"))))
                .add(new BigDecimal(String.valueOf(params4.get("HCHO"))))
        ).divide(new BigDecimal("4"), 2, RoundingMode.HALF_UP));
        // 室内平均CO2
        hvacResponse.setIndoorAvgCO2((new BigDecimal(String.valueOf(params1.get("CO2")))
                .add(new BigDecimal(String.valueOf(params2.get("CO2"))))
                .add(new BigDecimal(String.valueOf(params3.get("CO2"))))
                .add(new BigDecimal(String.valueOf(params4.get("CO2"))))
        ).divide(new BigDecimal("4"), 2, RoundingMode.HALF_UP));
        // 室内平均PM2.5
        hvacResponse.setIndoorAvgPM25((new BigDecimal(String.valueOf(params1.get("pm25")))
                .add(new BigDecimal(String.valueOf(params2.get("pm25"))))
                .add(new BigDecimal(String.valueOf(params3.get("pm25"))))
                .add(new BigDecimal(String.valueOf(params4.get("pm25"))))
        ).divide(new BigDecimal("4"), 2, RoundingMode.HALF_UP));
        // 当日用电量
        if (CollUtil.isNotEmpty(currentDays)) {
            hvacResponse.setElectricityEnergyUsageCurrentDay(getValue(SubitemIndexEnum.WARM_UNIVERSAL_POWER, currentDays.get(localDate)));
        }
        Response<ProjectDetailsResponse> projectDetails = projectApi.getProjectDetails(projectBizId);
        BigDecimal area = projectDetails.getResult().getArea();
        // 当月用电量
        if (CollUtil.isNotEmpty(currentMonths)) {
            hvacResponse.setElectricityEnergyUsageCurrentMonth(getValue(SubitemIndexEnum.WARM_UNIVERSAL_POWER, currentMonths.get(yearMonth)));
            // 当月单位用电量
            hvacResponse.setElectricityEnergyUsageCurrentMonthPM(hvacResponse.getElectricityEnergyUsageCurrentMonth().divide(area, 2, RoundingMode.HALF_UP));
        }
        // 当年用电量
        hvacResponse.setElectricityEnergyUsageCurrentYear(getValue(SubitemIndexEnum.WARM_UNIVERSAL_POWER, currentYear));
        // 当年单位用电量
        hvacResponse.setElectricityEnergyUsageCurrentYearPM(hvacResponse.getElectricityEnergyUsageCurrentYear().divide(area, 2, RoundingMode.HALF_UP));

        // 毛细辐射末端开启数量 TODO
        // 毛细辐射末端关闭数量 TODO
        // 风机盘末端开启数量 TODO
        // 风机盘末端关闭数量 TODO
        // 日用电量趋势
        HvacResponse.DayTrend electricityEnergyUsageDayTrend = new HvacResponse.DayTrend();
        if (CollUtil.isNotEmpty(currentDays)) {
            currentDays.forEach((key, value) -> {
                electricityEnergyUsageDayTrend.getX().add(key.getDayOfMonth());
                electricityEnergyUsageDayTrend.getValue().add(getValue(SubitemIndexEnum.WARM_UNIVERSAL_POWER, value));
            });
        }
        hvacResponse.setElectricityEnergyUsageDayTrend(electricityEnergyUsageDayTrend);
        // 月用电量趋势
        HvacResponse.Usage electricityEnergyUsageMonthTrend = new HvacResponse.Usage();
        if (CollUtil.isNotEmpty(lastYearMonths)) {
            lastYearMonths.forEach((key, value) -> {
                BigDecimal lastYearUsage = getValue(SubitemIndexEnum.WARM_UNIVERSAL_POWER, value);
                electricityEnergyUsageMonthTrend.getLastYear().add(lastYearUsage);
                Map<SubitemIndexEnum, BigDecimal> monthData = currentMonths.get(key.plusYears(1));
                BigDecimal currentYearUsage = getValue(SubitemIndexEnum.WARM_UNIVERSAL_POWER, monthData);
                electricityEnergyUsageMonthTrend.getCurrentYear().add(currentYearUsage);
                if (lastYearUsage != null && currentYearUsage != null && lastYearUsage.compareTo(BigDecimal.ZERO) != 0) {
                    electricityEnergyUsageMonthTrend.getYoy().add((currentYearUsage.subtract(lastYearUsage)).divide(lastYearUsage, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)));
                } else {
                    electricityEnergyUsageMonthTrend.getYoy().add(null);
                }
                electricityEnergyUsageMonthTrend.getX().add(key.getMonth().getValue());
            });
        }
        hvacResponse.setElectricityEnergyUsageMonthTrend(electricityEnergyUsageMonthTrend);
        return hvacResponse;
    }

    public List<AlarmResponse> getPvAlarm(String projectBizId) {
        List<String> deviceBizIds = Arrays.asList(ZNB1, ZNB2);
        return alarmApi.query(deviceBizIds).getResult();
    }

    public List<AlarmResponse> getStorageAlarm(String projectBizId) {
        List<String> deviceBizIds = List.of(STORAGE_DEVICE_BIZ_ID);
        return alarmApi.query(deviceBizIds).getResult();
    }

    public List<AlarmResponse> getChargeAlarm(String projectBizId) {
        List<String> deviceBizIds = Arrays.asList(AL_STATION1, AL_STATION2, AL_STATION3, AL_STATION4, AL_STATION5, DIR_STATION1);
        return alarmApi.query(deviceBizIds).getResult();
    }

    public List<AlarmResponse> getHvacAlarm(String projectBizId) {
        List<String> deviceBizIds = List.of(MULTI_DEVICE_BIZ_ID1);
        return alarmApi.query(deviceBizIds).getResult();
    }

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

    private YearMonth[] getLastYearMonth(YearMonth now) {
        List<YearMonth> yearMonths = new ArrayList<>();
        int lastYear = now.minusYears(1).getYear();
        for (int i = 1; i <= 12; i++) {
            yearMonths.add(YearMonth.of(lastYear, i));
        }
        return yearMonths.toArray(new YearMonth[0]);
    }

    private LocalDate[] getCurrentMonthDay(LocalDate now) {
        List<LocalDate> monthDays = new ArrayList<>();
        int days = now.lengthOfMonth();
        for (int i = 1; i <= days; i++) {
            monthDays.add(LocalDate.of(now.getYear(), now.getMonth(), i));
        }
        return monthDays.toArray(new LocalDate[0]);
    }

    public Map<String, EnergySensor> getSensor() {
        //
        // 多参数传感器 合并求平均值
        Map<String, Object> params1 = deviceCurrentApi.getDeviceCurrentById(MULTI_DEVICE_BIZ_ID1).getResult();
        Map<String, Object> params2 = deviceCurrentApi.getDeviceCurrentById(MULTI_DEVICE_BIZ_ID2).getResult();
        Map<String, Object> params3 = deviceCurrentApi.getDeviceCurrentById(MULTI_DEVICE_BIZ_ID3).getResult();
        Map<String, Object> params4 = deviceCurrentApi.getDeviceCurrentById(MULTI_DEVICE_BIZ_ID4).getResult();

        EnergySensor sensor1 = new EnergySensor();
        sensor1.setId(MULTI_DEVICE_BIZ_ID1);
        sensor1.setSsPm25(new BigDecimal(String.valueOf(params1.get("pm25"))).toPlainString());
        sensor1.setSsTemp(new BigDecimal(String.valueOf(params1.get("Temperature"))).setScale(1, RoundingMode.HALF_UP).toPlainString());
        sensor1.setSsVoc(new BigDecimal(String.valueOf(params1.get("TVOC"))).toPlainString());
        sensor1.setSsCo2(new BigDecimal(String.valueOf(params1.get("CO2"))).toPlainString());
        sensor1.setSsHum(new BigDecimal(String.valueOf(params1.get("Humidity"))).setScale(1, RoundingMode.HALF_UP).toPlainString());
        sensor1.setSsHcho(new BigDecimal(String.valueOf(params1.get("HCHO"))).toPlainString());
        sensor1.setSsHchoLevel("优");
        sensor1.setSsVocLevel("优");

        EnergySensor sensor2 = new EnergySensor();
        sensor2.setId(MULTI_DEVICE_BIZ_ID2);
        sensor2.setSsPm25(new BigDecimal(String.valueOf(params2.get("pm25"))).toPlainString());
        sensor2.setSsTemp(new BigDecimal(String.valueOf(params2.get("Temperature"))).setScale(1, RoundingMode.HALF_UP).toPlainString());
        sensor2.setSsVoc(new BigDecimal(String.valueOf(params2.get("TVOC"))).toPlainString());
        sensor2.setSsCo2(new BigDecimal(String.valueOf(params2.get("CO2"))).toPlainString());
        sensor2.setSsHum(new BigDecimal(String.valueOf(params2.get("Humidity"))).setScale(1, RoundingMode.HALF_UP).toPlainString());
        sensor2.setSsHcho(new BigDecimal(String.valueOf(params1.get("HCHO"))).toPlainString());
        sensor2.setSsHchoLevel("优");
        sensor2.setSsVocLevel("优");

        EnergySensor sensor3 = new EnergySensor();
        sensor3.setId(MULTI_DEVICE_BIZ_ID3);
        sensor3.setSsPm25(new BigDecimal(String.valueOf(params3.get("pm25"))).toPlainString());
        sensor3.setSsTemp(new BigDecimal(String.valueOf(params3.get("Temperature"))).setScale(1, RoundingMode.HALF_UP).toPlainString());
        sensor3.setSsVoc(new BigDecimal(String.valueOf(params3.get("TVOC"))).toPlainString());
        sensor3.setSsCo2(new BigDecimal(String.valueOf(params3.get("CO2"))).toPlainString());
        sensor3.setSsHum(new BigDecimal(String.valueOf(params3.get("Humidity"))).setScale(1, RoundingMode.HALF_UP).toPlainString());
        sensor3.setSsHcho(new BigDecimal(String.valueOf(params1.get("HCHO"))).toPlainString());
        sensor3.setSsHchoLevel("优");
        sensor3.setSsVocLevel("优");

        EnergySensor sensor4 = new EnergySensor();
        sensor4.setId(MULTI_DEVICE_BIZ_ID4);
        sensor4.setSsPm25(new BigDecimal(String.valueOf(params4.get("pm25"))).toPlainString());
        sensor4.setSsTemp(new BigDecimal(String.valueOf(params4.get("Temperature"))).setScale(1, RoundingMode.HALF_UP).toPlainString());
        sensor4.setSsVoc(new BigDecimal(String.valueOf(params4.get("TVOC"))).toPlainString());
        sensor4.setSsCo2(new BigDecimal(String.valueOf(params4.get("CO2"))).toPlainString());
        sensor4.setSsHum(new BigDecimal(String.valueOf(params4.get("Humidity"))).setScale(1, RoundingMode.HALF_UP).toPlainString());
        sensor4.setSsHcho(new BigDecimal(String.valueOf(params1.get("HCHO"))).toPlainString());
        sensor4.setSsHchoLevel("优");
        sensor4.setSsVocLevel("优");

        Map<String, EnergySensor> response = MapUtil.newHashMap();
        response.put("1", sensor1);
        response.put("2", sensor2);
        response.put("3", sensor3);
        response.put("4", sensor4);
        return response;
    }

    public EnergyAcc getEnergyAcc() {
        EnergyAcc energyAcc = new EnergyAcc();
        //年度累计能耗(+年度单方能耗) 年度累计光伏发电量 年度累计发电量(+年度单方碳排量)
        IndexYearRequest indexYearRequest = new IndexYearRequest();
        indexYearRequest.setYears(new Year[]{Year.now()});
        indexYearRequest.setProjectBizId(LgcConstants.PROJECT_BIZ_ID);
        SubitemIndexEnum[] currentYearIndices = new SubitemIndexEnum[]{
                // 总负荷用电量
                SubitemIndexEnum.TOTAL_ELECTRICITY_CONSUMPTION_OF_ALL_LOADS,
                // 当年光伏发电量
                SubitemIndexEnum.PHOTOVOLTAIC_POWER_GENERATION,
                // 累计二氧化碳减少量
                SubitemIndexEnum.CARBON_DIOXIDE_EMISSIONS,
        };
        indexYearRequest.setIndices(currentYearIndices);
        Response<Map<Year, Map<SubitemIndexEnum, BigDecimal>>> mapYearResponse = subitemApi.searchDataByYear(indexYearRequest);
        Map<SubitemIndexEnum, BigDecimal> currentYear = mapYearResponse.getResult().get(Year.now());
        energyAcc.setYearEnergyAcc(getValue(SubitemIndexEnum.TOTAL_ELECTRICITY_CONSUMPTION_OF_ALL_LOADS, currentYear));
        energyAcc.setYearGfAcc(getValue(SubitemIndexEnum.PHOTOVOLTAIC_POWER_GENERATION, currentYear));
        energyAcc.setYearCarbonAcc(NumberUtil.mul(getValue(SubitemIndexEnum.CARBON_DIOXIDE_EMISSIONS, currentYear), new BigDecimal(1000)));

//        Response<ProjectDetailsResponse> projectDetail = projectApi.getProjectDetails(LgcConstants.PROJECT_BIZ_ID);
//        BigDecimal area =  projectDetail.getResult().getArea();
        return energyAcc;
    }

    public List<EnergyItem> getSubitemYear() {
        IndexYearRequest indexYearRequest = new IndexYearRequest();
        indexYearRequest.setYears(new Year[]{Year.now()});
        indexYearRequest.setProjectBizId(LgcConstants.PROJECT_BIZ_ID);
        SubitemIndexEnum[] currentYearIndices = new SubitemIndexEnum[]{
                // 分项-照明用电量
                SubitemIndexEnum.LIGHTING_POWER_CONSUMPTION,
                // 分项-插座用电量
                SubitemIndexEnum.SOCKET_POWER_CONSUMPTION,
                // 分项-暖通用电量
                SubitemIndexEnum.WARM_UNIVERSAL_POWER,
                // 分项-动力用电量
                SubitemIndexEnum.SUPPLY_ENERTY,
                // 分项-特殊用电量
                SubitemIndexEnum.OTHER_ELECTRICITY_CONSUMPTION,
        };
        indexYearRequest.setIndices(currentYearIndices);
        Response<Map<Year, Map<SubitemIndexEnum, BigDecimal>>> mapYearResponse = subitemApi.searchDataByYear(indexYearRequest);
        Map<SubitemIndexEnum, BigDecimal> currentYear = mapYearResponse.getResult().get(Year.now());

        List<EnergyItem> list = new ArrayList<>();
        list.add(new EnergyItem("照明", getValue(SubitemIndexEnum.LIGHTING_POWER_CONSUMPTION, currentYear)));
        list.add(new EnergyItem("插座", getValue(SubitemIndexEnum.SOCKET_POWER_CONSUMPTION, currentYear)));
        list.add(new EnergyItem("暖通空调", getValue(SubitemIndexEnum.WARM_UNIVERSAL_POWER, currentYear)));
        list.add(new EnergyItem("动力设备", getValue(SubitemIndexEnum.SUPPLY_ENERTY, currentYear)));
        list.add(new EnergyItem("特殊用电", getValue(SubitemIndexEnum.OTHER_ELECTRICITY_CONSUMPTION, currentYear)));
        return list;
    }

    public List<EnergyItem> getSubitemMonth() {
        // 月度使用量查询
        IndexMonthRequest indexMonthRequest = new IndexMonthRequest();
        YearMonth yearMonth = YearMonth.now();
        YearMonth[] currentYearMonth = getCurrentYearMonth(yearMonth);
        indexMonthRequest.setMonths(currentYearMonth);
        indexMonthRequest.setProjectBizId(LgcConstants.PROJECT_BIZ_ID);
        SubitemIndexEnum[] currentMonthIndices = new SubitemIndexEnum[]{
                // 分项-照明用电量
                SubitemIndexEnum.LIGHTING_POWER_CONSUMPTION,
                // 分项-插座用电量
                SubitemIndexEnum.SOCKET_POWER_CONSUMPTION,
                // 分项-暖通用电量
                SubitemIndexEnum.WARM_UNIVERSAL_POWER,
                // 分项-动力用电量
                SubitemIndexEnum.SUPPLY_ENERTY,
                // 分项-特殊用电量
                SubitemIndexEnum.OTHER_ELECTRICITY_CONSUMPTION,
        };
        indexMonthRequest.setIndices(currentMonthIndices);
        Response<Map<YearMonth, Map<SubitemIndexEnum, BigDecimal>>> mapMonthResponse = subitemApi.searchDataByMonth(indexMonthRequest);
        Map<YearMonth, Map<SubitemIndexEnum, BigDecimal>> currentMonths = mapMonthResponse.getResult();

        List<EnergyItem> list = new ArrayList<>();
        list.add(new EnergyItem("照明", getValue(SubitemIndexEnum.LIGHTING_POWER_CONSUMPTION, currentMonths.get(yearMonth))));
        list.add(new EnergyItem("插座", getValue(SubitemIndexEnum.SOCKET_POWER_CONSUMPTION, currentMonths.get(yearMonth))));
        list.add(new EnergyItem("暖通空调", getValue(SubitemIndexEnum.WARM_UNIVERSAL_POWER, currentMonths.get(yearMonth))));
        list.add(new EnergyItem("动力设备", getValue(SubitemIndexEnum.SUPPLY_ENERTY, currentMonths.get(yearMonth))));
        list.add(new EnergyItem("特殊用电", getValue(SubitemIndexEnum.OTHER_ELECTRICITY_CONSUMPTION, currentMonths.get(yearMonth))));
        return list;
    }

    public EnergyLineChart getGfChartMonth() {
        EnergyLineChart gfChart = new EnergyLineChart();
        IndexMonthRequest indexMonthRequest = new IndexMonthRequest();
        //最近一年月光伏发电量
        SubitemIndexEnum[] currentMonthIndices = new SubitemIndexEnum[]{
                //  月光伏发电量
                SubitemIndexEnum.PHOTOVOLTAIC_POWER_GENERATION,
        };
        YearMonth yearMonth = YearMonth.now();
        List<YearMonth> staMonths = new ArrayList<>();
        int index = 12;
        while (index > 0) {
            YearMonth staMonth = yearMonth.minusMonths(index);
            staMonths.add(staMonth);
            index--;
        }
        indexMonthRequest.setMonths(staMonths.toArray(new YearMonth[]{}));
        indexMonthRequest.setProjectBizId(LgcConstants.PROJECT_BIZ_ID);
        indexMonthRequest.setIndices(currentMonthIndices);
        Response<Map<YearMonth, Map<SubitemIndexEnum, BigDecimal>>> mapMonthResponse = subitemApi.searchDataByMonth(indexMonthRequest);
        Map<YearMonth, Map<SubitemIndexEnum, BigDecimal>> response = mapMonthResponse.getResult();

        List<YearMonth> yms = response.keySet().stream().collect(Collectors.toList());
        List<String> xs = yms.stream().map(yearMonth1 -> String.format("%02d", yearMonth1.getMonthValue())).collect(Collectors.toList());
        List<BigDecimal> ys = yms.stream().map(new Function<YearMonth, BigDecimal>() {
            @Override
            public BigDecimal apply(YearMonth ym) {
                return getValue(SubitemIndexEnum.PHOTOVOLTAIC_POWER_GENERATION, response.get(ym));
            }
        }).collect(Collectors.toList());

        gfChart.setXs(xs);
        gfChart.setYs(ys);
        return gfChart;
    }

    public EnergyLineChart getEnergyChartMonth() {
        EnergyLineChart gfChart = new EnergyLineChart();
        IndexMonthRequest indexMonthRequest = new IndexMonthRequest();
        //最近一年月光伏发电量
        SubitemIndexEnum[] currentMonthIndices = new SubitemIndexEnum[]{
                //  月光伏发电量
                SubitemIndexEnum.PURCHASE_NETWORK_ELECTRICITY,
        };
        YearMonth yearMonth = YearMonth.now();
        List<YearMonth> staMonths = new ArrayList<>();
        int index = 12;
        while (index > 0) {
            YearMonth staMonth = yearMonth.minusMonths(index);
            staMonths.add(staMonth);
            index--;
        }
        indexMonthRequest.setMonths(staMonths.toArray(new YearMonth[]{}));
        indexMonthRequest.setProjectBizId(LgcConstants.PROJECT_BIZ_ID);
        indexMonthRequest.setIndices(currentMonthIndices);
        Response<Map<YearMonth, Map<SubitemIndexEnum, BigDecimal>>> mapMonthResponse = subitemApi.searchDataByMonth(indexMonthRequest);
        Map<YearMonth, Map<SubitemIndexEnum, BigDecimal>> response = mapMonthResponse.getResult();

        List<YearMonth> yms = response.keySet().stream().collect(Collectors.toList());
        List<String> xs = yms.stream().map(yearMonth1 -> String.format("%02d", yearMonth1.getMonthValue())).collect(Collectors.toList());
        List<BigDecimal> ys = yms.stream().map(new Function<YearMonth, BigDecimal>() {
            @Override
            public BigDecimal apply(YearMonth ym) {
                return getValue(SubitemIndexEnum.PURCHASE_NETWORK_ELECTRICITY, response.get(ym));
            }
        }).collect(Collectors.toList());

        gfChart.setXs(xs);
        gfChart.setYs(ys);
        return gfChart;
    }

    public EnergyWeather getEnergyEnergyWeather() {
        EnergyWeather weather = new EnergyWeather();
        String weatherName = projectWeatherApi.getProjectWeatherName(LgcConstants.PROJECT_BIZ_ID).getCheckedData();
        if (StrUtil.isNotBlank(weatherName)) {
            Object obj = redisUtils.hget(WEATHER_CACHE, weatherName);
            if (obj != null) {
                JSONObject weatherObject = JSONObject.from(obj);
                weather.setPicUrl(WeatherUtil.replace(weatherObject.getString("picUrl")));
                weather.setWeatherStatus(weatherObject.getString("weatherStatus"));
                weather.setWsHum(weatherObject.getString("humidity"));
                weather.setWsTemp(weatherObject.getString("temp"));
                weather.setWsPm25(weatherObject.getString("pm25"));
            }
        }
        return weather;
    }
}
