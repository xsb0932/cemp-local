package com.landleaf.energy.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.nacos.shaded.com.google.common.collect.Lists;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Maps;
import com.landleaf.bms.api.ProjectApi;
import com.landleaf.bms.api.dto.ProjectDetailsResponse;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.data.api.device.WeatherHistoryApi;
import com.landleaf.data.api.device.dto.WeatherHistoryDTO;
import com.landleaf.data.api.device.dto.WeatherHistoryQueryDTO;
import com.landleaf.energy.dal.mapper.*;
import com.landleaf.energy.domain.entity.*;
import com.landleaf.energy.enums.ElectricityPriceTypeEnum;
import com.landleaf.energy.service.*;
import com.landleaf.job.api.JobLogApi;
import com.landleaf.job.api.dto.JobLogSaveDTO;
import com.landleaf.job.api.dto.JobRpcRequest;
import com.landleaf.monitor.api.MonitorApi;
import com.landleaf.oauth.api.TenantApi;
import com.landleaf.oauth.api.dto.TenantInfoResponse;
import com.landleaf.oauth.api.enums.ReportingCycle;
import com.landleaf.pgsql.base.TenantBaseEntity;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.landleaf.comm.constance.CommonConstant.JOB_EXEC_ERROR;
import static com.landleaf.comm.constance.CommonConstant.JOB_EXEC_SUCCESS;

/**
 * 分小时统计逻辑实现
 */
@Service
@Slf4j
public class StaSubitemStatisticsServiceImpl implements StaSubitemStatisticsService {
    DateTimeFormatter hourFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH");

    @Resource
    private ProjectKpiConfigService projectKpiConfigServiceImpl;

    @Resource
    private ProjectCnfSubitemService projectCnfSubitemServiceImpl;

    @Resource
    private ProjectSubitemDeviceService projectSubitemDeviceServiceImpl;

    @Resource
    private MonitorApi monitorApi;

    @Resource
    private ProjectApi projectApi;

    @Resource
    private WeatherHistoryApi weatherHistoryApi;

    @Resource
    private TenantApi tenantApi;

    @Resource
    private JobLogApi jobLogApi;

    @Resource
    private ProjectStaSubitemHourService projectStaSubitemHourServiceImpl;

    @Resource
    private ProjectStaSubitemDayService projectStaSubitemDayServiceImpl;

    @Resource
    private ProjectStaSubitemMonthService projectStaSubitemMonthServiceImpl;

    @Resource
    private ProjectStaSubitemYearService projectStaSubitemYearServiceImpl;

    @Resource
    private ProjectStaDeviceAirHourService projectStaDeviceAirHourServiceImpl;

    @Resource
    private ProjectStaDeviceElectricityHourService projectStaDeviceElectricityHourServiceImpl;

    @Resource
    private ProjectStaDeviceWaterHourService projectStaDeviceWaterHourServiceImpl;

    @Resource
    private ProjectStaDeviceGasHourService projectStaDeviceGasHourServiceImpl;

    @Resource
    private ProjectStaDeviceAirDayService projectStaDeviceAirDayServiceImpl;

    @Resource
    private ProjectStaDeviceElectricityDayService projectStaDeviceElectricityDayServiceImpl;

    @Resource
    private ProjectStaDeviceWaterDayService projectStaDeviceWaterDayServiceImpl;

    @Resource
    private ProjectStaDeviceGasDayService projectStaDeviceGasDayServiceImpl;

    @Resource
    private ProjectStaDeviceAirMonthService projectStaDeviceAirMonthServiceImpl;

    @Resource
    private ProjectStaDeviceElectricityMonthService projectStaDeviceElectricityMonthServiceImpl;

    @Resource
    private ProjectStaDeviceWaterMonthService projectStaDeviceWaterMonthServiceImpl;

    @Resource
    private ProjectStaDeviceGasMonthService projectStaDeviceGasMonthServiceImpl;

    @Resource
    private ProjectStaDeviceAirYearService projectStaDeviceAirYearServiceImpl;

    @Resource
    private ProjectStaDeviceElectricityYearService projectStaDeviceElectricityYearServiceImpl;

    @Resource
    private ProjectStaDeviceWaterYearService projectStaDeviceWaterYearServiceImpl;

    @Resource
    private ProjectStaDeviceGasYearService projectStaDeviceGasYearServiceImpl;

    @Resource
    private ProjectCnfTimePeriodService projectCnfTimePeriodServiceImpl;

    @Resource
    private ProjectCnfWaterFeeService projectCnfWaterFeeServiceImpl;

    @Resource
    private ProjectCnfGasFeeService projectCnfGasFeeServiceImpl;

    @Resource
    private ProjectStaDeviceGscnHourMapper projectStaDeviceGscnHourMapper;
    @Resource
    private ProjectStaDeviceZnbHourMapper projectStaDeviceZnbHourMapper;

    @Resource
    private ProjectStaDeviceGscnDayMapper projectStaDeviceGscnDayMapper;
    @Resource
    private ProjectStaDeviceZnbDayMapper projectStaDeviceZnbDayMapper;
    @Resource
    private ProjectStaDeviceGscnMonthMapper projectStaDeviceGscnMonthMapper;
    @Resource
    private ProjectStaDeviceZnbMonthMapper projectStaDeviceZnbMonthMapper;
    @Resource
    private ProjectStaDeviceGscnYearMapper projectStaDeviceGscnYearMapper;
    @Resource
    private ProjectStaDeviceZnbYearMapper projectStaDeviceZnbYearMapper;
    @Resource
    private ProjectCnfChargeStationMapper projectCnfChargeStationMapper;
    @Resource
    private ProjectCnfPvMapper projectCnfPvMapper;

    @Autowired
    private ProjectCnfElectricityPriceService projectCnfElectricityPriceServiceImpl;

    @Override
    public boolean statisticsByHour(String[] times, JobRpcRequest request) {
        List<LocalDateTime> timeList = new ArrayList<>();
        if (!ArrayUtil.isEmpty(times)) {
            // 小时的时间，应该是yyyy-MM-dd HH
            for (String time : times) {
                timeList.add(LocalDateTime.parse(time, hourFormatter).withMinute(0).withSecond(0).withNano(0));
            }
        } else {
            timeList.add(LocalDateTime.now().minusHours(1).withMinute(0).withSecond(0).withNano(0));
        }
        // 获取需要分享处理的kpicode
        List<ProjectKpiConfigEntity> kpiConfList = projectKpiConfigServiceImpl.list(Wrappers.<ProjectKpiConfigEntity>lambdaQuery().like(ProjectKpiConfigEntity::getCode, "project%").eq(ProjectKpiConfigEntity::getStaIntervalHour, 1));

        if (CollectionUtils.isEmpty(kpiConfList)) {
            // 不需要统计， 返回即可
            return true;
        }

        Map<String, List<ProjectKpiConfigEntity>> kpiConfigMap = kpiConfList.stream().collect(Collectors.groupingBy(ProjectKpiConfigEntity::getKpiSubtype));

        // 通过kpiSubtype查找对应的subitem的配置
        List<String> kpiSubtypeList = kpiConfList.stream().map(ProjectKpiConfigEntity::getKpiSubtype).filter(StrUtil::isNotEmpty).distinct().collect(Collectors.toList());
        List<ProjectCnfSubitemEntity> subitemConfList = projectCnfSubitemServiceImpl.list(Wrappers.<ProjectCnfSubitemEntity>lambdaQuery().in(ProjectCnfSubitemEntity::getKpiSubtype, kpiSubtypeList));

        if (CollectionUtils.isEmpty(subitemConfList)) {
            // 不需要统计， 返回即可
            return true;
        }
        Map<String, List<ProjectCnfSubitemEntity>> projSubitemConfMap = subitemConfList.stream().collect(Collectors.groupingBy(ProjectCnfSubitemEntity::getProjectId));
        List<Long> subitemIdList = subitemConfList.stream().map(ProjectCnfSubitemEntity::getId).collect(Collectors.toList());

        // 获取配置的设备列表
        List<ProjectSubitemDeviceEntity> deviceConfList = projectSubitemDeviceServiceImpl.list(new QueryWrapper<ProjectSubitemDeviceEntity>().lambda().in(ProjectSubitemDeviceEntity::getSubitemId, subitemIdList));

        Map<Long, Map<Long, List<ProjectSubitemDeviceEntity>>> deviceConfMap = deviceConfList.stream().collect(Collectors.groupingBy(ProjectSubitemDeviceEntity::getTenantId, Collectors.groupingBy(ProjectSubitemDeviceEntity::getSubitemId)));

        // 通过时间，处理对应信息
        for (LocalDateTime time : timeList) {
            Map<Long, JobLogSaveDTO> jobLogMap = new HashMap<>(8);
            Map<Long, Map<String, String>> tenantProjectMap = new HashMap<>(8);

            // 如果当前时间，已有对应的记录，删掉重新出
            String bizProjectId = null;
            Long tenantId = null;
            Map<String, List<ProjectKpiConfigEntity>> kpiConfDetailMap;
            Map<Long, List<ProjectSubitemDeviceEntity>> deviceConfDetailMap;
            // 环境信息
            WeatherHistoryQueryDTO weatherHistoryQueryDTO = new WeatherHistoryQueryDTO();
            // 天气数据比当前时间少2h，所以，减2去查
            LocalDateTime weatherTime = time.minusHours(2L);
            weatherHistoryQueryDTO.setPublishTime(weatherTime);
            Response<List<WeatherHistoryDTO>> weatherHistory = weatherHistoryApi.getWeatherHistory(weatherHistoryQueryDTO);
            List<WeatherHistoryDTO> weatherHistoryList = weatherHistory.getResult();
            log.info("Weather time:{}, info {}", weatherTime, JSON.toJSONString(weatherHistoryList));

            try {
                for (Map.Entry<String, List<ProjectCnfSubitemEntity>> entry : projSubitemConfMap.entrySet()) {
                    boolean callStorageEnergyUsagePvTotal = false;
                    boolean callStorageEnergyUsageGridTotal = false;
                    bizProjectId = entry.getValue().get(0).getProjectId();
                    tenantId = entry.getValue().get(0).getTenantId();

                    // modify by hebin. 这个步骤提前， 如果没有对应的分区设备的配置， 直接continue，节省性能
                    if (!deviceConfMap.containsKey(tenantId)) {
                        log.info("当前租户没有对应的配置，给一个空配置， 租户编号为：{}", tenantId);
                        deviceConfMap.put(tenantId, new HashMap<>());
                    }

                    // 获取项目信息
                    Response<ProjectDetailsResponse> projectResp = projectApi.getProjectDetails(bizProjectId);
                    if (!projectResp.isSuccess() || null == projectResp.getResult()) {
                        log.error("获取项目信息失败, 项目编号为: {}", bizProjectId);
                        continue;
                    }
                    ProjectDetailsResponse projInfo = projectResp.getResult();

                    // 获取租户信息
                    Response<TenantInfoResponse> tenantResp = tenantApi.getTenantInfo(tenantId);
                    if (!tenantResp.isSuccess() || null == tenantResp.getResult()) {
                        log.error("获取租户信息失败, 租户编号为: {}", tenantId);
                        continue;
                    }
                    TenantInfoResponse tenantInfo = tenantResp.getResult();

                    // 2023-11-16 增加手动执行过滤条件
                    if (null != request.getTenantId() && !Objects.equals(tenantId, request.getTenantId())) {
                        continue;
                    }

                    if (CollectionUtil.isNotEmpty(request.getProjectList()) && !request.getProjectList().contains(bizProjectId)) {
                        continue;
                    }

                    projectStaSubitemHourServiceImpl.remove(Wrappers.<ProjectStaSubitemHourEntity>lambdaQuery().eq(ProjectStaSubitemHourEntity::getBizProjectId, bizProjectId).eq(ProjectStaSubitemHourEntity::getYear, String.valueOf(time.getYear())).eq(ProjectStaSubitemHourEntity::getMonth, String.valueOf(time.getMonthValue())).eq(ProjectStaSubitemHourEntity::getDay, String.valueOf(time.getDayOfMonth())).eq(ProjectStaSubitemHourEntity::getHour, String.valueOf(time.getHour())));

                    // 2023-11-15 增加日志记录
                    if (!jobLogMap.containsKey(tenantId)) {
                        JobLogSaveDTO jobLog = new JobLogSaveDTO();
                        jobLog.setJobId(request.getJobId())
                                .setTenantId(tenantId)
                                .setStatus(JOB_EXEC_SUCCESS)
                                .setExecTime(LocalDateTime.now())
                                .setExecType(request.getExecType())
                                .setExecUser(request.getExecUser());
                        jobLogMap.put(tenantId, jobLog);
                    }

                    if (!tenantProjectMap.containsKey(tenantId)) {
                        tenantProjectMap.put(tenantId, new HashMap<>(8));
                    }

                    if (!tenantProjectMap.get(tenantId).containsKey(bizProjectId)) {
                        tenantProjectMap.get(tenantId).put(bizProjectId, projInfo.getName());
                    }

                    // 通过subitem信息，获取对应的kpi信息
                    if (MapUtil.isEmpty(kpiConfigMap)) {
                        continue;
                    }

                    try {
                        // 当前所有的kpiConf有了，查询对应的设备
                        deviceConfDetailMap = deviceConfMap.get(tenantId);
                        if (MapUtil.isEmpty(deviceConfDetailMap)) {
                            // 没设备值全部置为null行了
                        } else {
                            ProjectStaSubitemHourEntity insertEntity = new ProjectStaSubitemHourEntity();
                            insertEntity.setHour(String.valueOf(time.getHour()));
                            insertEntity.setDay(String.valueOf(time.getDayOfMonth()));
                            insertEntity.setBizProjectId(bizProjectId);
                            insertEntity.setMonth(String.valueOf(time.getMonthValue()));
                            insertEntity.setYear(String.valueOf(time.getYear()));
                            insertEntity.setProjectCode(projInfo.getCode());
                            insertEntity.setProjectName(projInfo.getName());
                            insertEntity.setTenantId(tenantId);
                            insertEntity.setTenantCode(tenantInfo.getCode());
                            insertEntity.setStaTime(Timestamp.valueOf(time));

                            for (ProjectCnfSubitemEntity i : entry.getValue()) {
                                List<ProjectSubitemDeviceEntity> deviceList = deviceConfDetailMap.get(i.getId());
                                Map<String, ProjectKpiConfigEntity> kpiMap = kpiConfigMap.get(i.getKpiSubtype()).stream().collect(Collectors.toMap(ProjectKpiConfigEntity::getCode, Function.identity()));
                                if (MapUtil.isNotEmpty(kpiMap)) {
                                    // 计算设备得值
                                    Map<String, Object> deviceStatus = new HashMap<>();
                                    if (!CollectionUtils.isEmpty(deviceList)) {
                                        List<String> deviceIds = deviceList.stream().map(ProjectSubitemDeviceEntity::getDeviceId).distinct().collect(Collectors.toList());
                                        // 查询设备的小时信息，从4张表查
                                        List<ProjectStaDeviceAirHourEntity> airList = projectStaDeviceAirHourServiceImpl.list(new QueryWrapper<ProjectStaDeviceAirHourEntity>().lambda().in(ProjectStaDeviceAirHourEntity::getBizDeviceId, deviceIds).eq(ProjectStaDeviceAirHourEntity::getYear, String.valueOf(time.getYear())).eq(ProjectStaDeviceAirHourEntity::getMonth, String.valueOf(time.getMonthValue())).eq(ProjectStaDeviceAirHourEntity::getDay, String.valueOf(time.getDayOfMonth())).eq(ProjectStaDeviceAirHourEntity::getHour, String.valueOf(time.getHour())));
                                        if (!CollectionUtils.isEmpty(airList)) {
                                            deviceStatus.putAll(airList.stream().collect(Collectors.toMap(ProjectStaDeviceAirHourEntity::getBizDeviceId, j -> j, (v1, v2) -> v1)));
                                        }
                                        List<ProjectStaDeviceElectricityHourEntity> electricityList = projectStaDeviceElectricityHourServiceImpl.list(new QueryWrapper<ProjectStaDeviceElectricityHourEntity>().lambda().in(ProjectStaDeviceElectricityHourEntity::getBizDeviceId, deviceIds).eq(ProjectStaDeviceElectricityHourEntity::getYear, String.valueOf(time.getYear())).eq(ProjectStaDeviceElectricityHourEntity::getMonth, String.valueOf(time.getMonthValue())).eq(ProjectStaDeviceElectricityHourEntity::getDay, String.valueOf(time.getDayOfMonth())).eq(ProjectStaDeviceElectricityHourEntity::getHour, String.valueOf(time.getHour())));
                                        if (!CollectionUtils.isEmpty(electricityList)) {
                                            deviceStatus.putAll(electricityList.stream().collect(Collectors.toMap(ProjectStaDeviceElectricityHourEntity::getBizDeviceId, j -> j, (v1, v2) -> v1)));
                                        }
                                        List<ProjectStaDeviceWaterHourEntity> waterList = projectStaDeviceWaterHourServiceImpl.list(new QueryWrapper<ProjectStaDeviceWaterHourEntity>().lambda().in(ProjectStaDeviceWaterHourEntity::getBizDeviceId, deviceIds).eq(ProjectStaDeviceWaterHourEntity::getYear, String.valueOf(time.getYear())).eq(ProjectStaDeviceWaterHourEntity::getMonth, String.valueOf(time.getMonthValue())).eq(ProjectStaDeviceWaterHourEntity::getDay, String.valueOf(time.getDayOfMonth())).eq(ProjectStaDeviceWaterHourEntity::getHour, String.valueOf(time.getHour())));
                                        if (!CollectionUtils.isEmpty(waterList)) {
                                            deviceStatus.putAll(waterList.stream().collect(Collectors.toMap(ProjectStaDeviceWaterHourEntity::getBizDeviceId, j -> j, (v1, v2) -> v1)));
                                        }
                                        List<ProjectStaDeviceGasHourEntity> gasList = projectStaDeviceGasHourServiceImpl.list(new QueryWrapper<ProjectStaDeviceGasHourEntity>().lambda().in(ProjectStaDeviceGasHourEntity::getBizDeviceId, deviceIds).eq(ProjectStaDeviceGasHourEntity::getYear, String.valueOf(time.getYear())).eq(ProjectStaDeviceGasHourEntity::getMonth, String.valueOf(time.getMonthValue())).eq(ProjectStaDeviceGasHourEntity::getDay, String.valueOf(time.getDayOfMonth())).eq(ProjectStaDeviceGasHourEntity::getHour, String.valueOf(time.getHour())));
                                        if (!CollectionUtils.isEmpty(gasList)) {
                                            deviceStatus.putAll(gasList.stream().collect(Collectors.toMap(ProjectStaDeviceGasHourEntity::getBizDeviceId, j -> j, (v1, v2) -> v1)));
                                        }
                                        List<ProjectStaDeviceGscnHourEntity> gascnList = projectStaDeviceGscnHourMapper.selectList(Wrappers.<ProjectStaDeviceGscnHourEntity>lambdaQuery().in(ProjectStaDeviceGscnHourEntity::getBizDeviceId, deviceIds).eq(ProjectStaDeviceGscnHourEntity::getYear, String.valueOf(time.getYear())).eq(ProjectStaDeviceGscnHourEntity::getMonth, String.valueOf(time.getMonthValue())).eq(ProjectStaDeviceGscnHourEntity::getDay, String.valueOf(time.getDayOfMonth())).eq(ProjectStaDeviceGscnHourEntity::getHour, String.valueOf(time.getHour())));
                                        if (!CollectionUtils.isEmpty(gascnList)) {
                                            deviceStatus.putAll(gascnList.stream().collect(Collectors.toMap(ProjectStaDeviceGscnHourEntity::getBizDeviceId, j -> j, (v1, v2) -> v1)));
                                        }
                                        List<ProjectStaDeviceZnbHourEntity> znbList = projectStaDeviceZnbHourMapper.selectList(Wrappers.<ProjectStaDeviceZnbHourEntity>lambdaQuery().in(ProjectStaDeviceZnbHourEntity::getBizDeviceId, deviceIds).eq(ProjectStaDeviceZnbHourEntity::getYear, String.valueOf(time.getYear())).eq(ProjectStaDeviceZnbHourEntity::getMonth, String.valueOf(time.getMonthValue())).eq(ProjectStaDeviceZnbHourEntity::getDay, String.valueOf(time.getDayOfMonth())).eq(ProjectStaDeviceZnbHourEntity::getHour, String.valueOf(time.getHour())));
                                        if (!CollectionUtils.isEmpty(znbList)) {
                                            deviceStatus.putAll(znbList.stream().collect(Collectors.toMap(ProjectStaDeviceZnbHourEntity::getBizDeviceId, j -> j, (v1, v2) -> v1)));
                                        }
                                    } else {
                                        deviceList = Lists.newArrayList();
                                    }
                                    JSONObject deviceObj = JSONObject.parseObject(JSON.toJSONString(deviceStatus));

                                    if (kpiMap.containsKey("project.electricity.energyUsage.total")) {
                                        // 全部负荷总用电量，取值包含energymeterEpimportTotal的
                                        if (CollUtil.isEmpty(deviceList)) {
                                            insertEntity.setProjectElectricityEnergyusageTotal(BigDecimal.ZERO);
                                        } else {
                                            var val = deviceList.stream().filter(j -> deviceObj.containsKey(j.getDeviceId()) && deviceObj.getJSONObject(j.getDeviceId()).containsKey("energymeterEpimportTotal")).map(j -> deviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("energymeterEpimportTotal").multiply(new BigDecimal(j.getComputeTag()))).reduce(BigDecimal.ZERO, BigDecimal::add);
                                            insertEntity.setProjectElectricityEnergyusageTotal(val);
                                        }
                                    }
                                    if (kpiMap.containsKey("project.electricity.subHAVCEnergy.total")) {
                                        // 空调总用电量，取值包含energymeterEpimportTotal的
                                        if (CollUtil.isEmpty(deviceList)) {
                                            insertEntity.setProjectElectricitySubhavcenergyTotal(BigDecimal.ZERO);
                                        } else {
                                            var val = deviceList.stream().filter(j -> deviceObj.containsKey(j.getDeviceId()) && deviceObj.getJSONObject(j.getDeviceId()).containsKey("energymeterEpimportTotal")).map(j -> deviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("energymeterEpimportTotal").multiply(new BigDecimal(j.getComputeTag()))).reduce(BigDecimal.ZERO, BigDecimal::add);
                                            insertEntity.setProjectElectricitySubhavcenergyTotal(val);
                                        }
                                    }
                                    if (kpiMap.containsKey("project.electricity.subHeatingWaterEnergy.total")) {
                                        // 热水总用电量，取值包含energymeterEpimportTotal的
                                        if (CollUtil.isEmpty(deviceList)) {
                                            insertEntity.setProjectElectricitySubheatingwaterenergyTotal(BigDecimal.ZERO);
                                        } else {
                                            var val = deviceList.stream().filter(j -> deviceObj.containsKey(j.getDeviceId()) && deviceObj.getJSONObject(j.getDeviceId()).containsKey("energymeterEpimportTotal")).map(j -> deviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("energymeterEpimportTotal").multiply(new BigDecimal(j.getComputeTag()))).reduce(BigDecimal.ZERO, BigDecimal::add);
                                            insertEntity.setProjectElectricitySubheatingwaterenergyTotal(val);
                                        }
                                    }
                                    if (kpiMap.containsKey("project.electricity.subWaterSupplyEnergy.total")) {
                                        // 供水总用电量，取值包含energymeterEpimportTotal的
                                        if (CollUtil.isEmpty(deviceList)) {
                                            insertEntity.setProjectElectricitySubwatersupplyenergyTotal(BigDecimal.ZERO);
                                        } else {
                                            var val = deviceList.stream().filter(j -> deviceObj.containsKey(j.getDeviceId()) && deviceObj.getJSONObject(j.getDeviceId()).containsKey("energymeterEpimportTotal")).map(j -> deviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("energymeterEpimportTotal").multiply(new BigDecimal(j.getComputeTag()))).reduce(BigDecimal.ZERO, BigDecimal::add);
                                            insertEntity.setProjectElectricitySubwatersupplyenergyTotal(val);
                                        }
                                    }
                                    if (kpiMap.containsKey("project.electricity.subElevatorEnergy.total")) {
                                        // 电梯总用电量，取值包含energymeterEpimportTotal的
                                        if (CollUtil.isEmpty(deviceList)) {
                                            insertEntity.setProjectElectricitySubelevatorenergyTotal(BigDecimal.ZERO);
                                        } else {
                                            var val = deviceList.stream().filter(j -> deviceObj.containsKey(j.getDeviceId()) && deviceObj.getJSONObject(j.getDeviceId()).containsKey("energymeterEpimportTotal")).map(j -> deviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("energymeterEpimportTotal").multiply(new BigDecimal(j.getComputeTag()))).reduce(BigDecimal.ZERO, BigDecimal::add);
                                            insertEntity.setProjectElectricitySubelevatorenergyTotal(val);
                                        }
                                    }
                                    if (kpiMap.containsKey("project.electricity.subGuestRoomEnergy.total")) {
                                        // 计算电量，取值包含energymeterEpimportTotal的
                                        if (CollUtil.isEmpty(deviceList)) {
                                            insertEntity.setProjectElectricitySubguestroomenergyTotal(BigDecimal.ZERO);
                                        } else {
                                            var val = deviceList.stream().filter(j -> deviceObj.containsKey(j.getDeviceId()) && deviceObj.getJSONObject(j.getDeviceId()).containsKey("energymeterEpimportTotal")).map(j -> deviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("energymeterEpimportTotal").multiply(new BigDecimal(j.getComputeTag()))).reduce(BigDecimal.ZERO, BigDecimal::add);
                                            insertEntity.setProjectElectricitySubguestroomenergyTotal(val);
                                        }
                                    }
                                    if (kpiMap.containsKey("project.electricity.subPowerSupplyEnergy.total")) {
                                        // 计算电量，取值包含energymeterEpimportTotal的
                                        if (CollUtil.isEmpty(deviceList)) {
                                            insertEntity.setProjectElectricitySubpowersupplyenergyTotal(BigDecimal.ZERO);
                                        } else {
                                            var val = deviceList.stream().filter(j -> deviceObj.containsKey(j.getDeviceId()) && deviceObj.getJSONObject(j.getDeviceId()).containsKey("energymeterEpimportTotal")).map(j -> deviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("energymeterEpimportTotal").multiply(new BigDecimal(j.getComputeTag()))).reduce(BigDecimal.ZERO, BigDecimal::add);
                                            insertEntity.setProjectElectricitySubpowersupplyenergyTotal(val);
                                        }
                                    }
                                    if (kpiMap.containsKey("project.electricity.subOtherType.total")) {
                                        // 计算电量，取值包含energymeterEpimportTotal的
                                        if (CollUtil.isEmpty(deviceList)) {
                                            insertEntity.setProjectElectricitySubothertypeTotal(BigDecimal.ZERO);
                                        } else {
                                            var val = deviceList.stream().filter(j -> deviceObj.containsKey(j.getDeviceId()) && deviceObj.getJSONObject(j.getDeviceId()).containsKey("energymeterEpimportTotal")).map(j -> deviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("energymeterEpimportTotal").multiply(new BigDecimal(j.getComputeTag()))).reduce(BigDecimal.ZERO, BigDecimal::add);
                                            insertEntity.setProjectElectricitySubothertypeTotal(val);
                                        }
                                    }
                                    if (kpiMap.containsKey("project.water.usage.total")) {
                                        // 计算用水量，取值包含watermeter.usage.total的
                                        if (CollUtil.isEmpty(deviceList)) {
                                            insertEntity.setProjectWaterUsageTotal(BigDecimal.ZERO);
                                        } else {
                                            var val = deviceList.stream().filter(j -> deviceObj.containsKey(j.getDeviceId()) && deviceObj.getJSONObject(j.getDeviceId()).containsKey("watermeterUsageTotal")).map(j -> deviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("watermeterUsageTotal").multiply(new BigDecimal(j.getComputeTag()))).reduce(BigDecimal.ZERO, BigDecimal::add);
                                            insertEntity.setProjectWaterUsageTotal(val);
                                        }
                                    }
                                    if (kpiMap.containsKey("project.water.HVACUsage.total")) {
                                        // 计算空调补水，取值包含watermeter.usage.total的
                                        if (CollUtil.isEmpty(deviceList)) {
                                            insertEntity.setProjectWaterHvacusageTotal(BigDecimal.ZERO);
                                        } else {
                                            var val = deviceList.stream().filter(j -> deviceObj.containsKey(j.getDeviceId()) && deviceObj.getJSONObject(j.getDeviceId()).containsKey("watermeterUsageTotal")).map(j -> deviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("watermeterUsageTotal").multiply(new BigDecimal(j.getComputeTag()))).reduce(BigDecimal.ZERO, BigDecimal::add);
                                            insertEntity.setProjectWaterHvacusageTotal(val);
                                        }
                                    }
                                    if (kpiMap.containsKey("project.water.HeatingWaterUsage.total")) {
                                        // 计算热水补水，取值包含watermeter.usage.total的
                                        if (CollUtil.isEmpty(deviceList)) {
                                            insertEntity.setProjectWaterHeatingwaterusageTotal(BigDecimal.ZERO);
                                        } else {
                                            var val = deviceList.stream().filter(j -> deviceObj.containsKey(j.getDeviceId()) && deviceObj.getJSONObject(j.getDeviceId()).containsKey("watermeterUsageTotal")).map(j -> deviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("watermeterUsageTotal").multiply(new BigDecimal(j.getComputeTag()))).reduce(BigDecimal.ZERO, BigDecimal::add);
                                            insertEntity.setProjectWaterHeatingwaterusageTotal(val);
                                        }
                                    }
                                    if (kpiMap.containsKey("project.gas.usage.total")) {
                                        // 计算用气量，取值包含gasmeter.usage.total的
                                        if (CollUtil.isEmpty(deviceList)) {
                                            insertEntity.setProjectGasUsageTotal(BigDecimal.ZERO);
                                        } else {
                                            var val = deviceList.stream().filter(j -> deviceObj.containsKey(j.getDeviceId()) && deviceObj.getJSONObject(j.getDeviceId()).containsKey("gasmeterUsageTotal")).map(j -> deviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("gasmeterUsageTotal").multiply(new BigDecimal(j.getComputeTag()))).reduce(BigDecimal.ZERO, BigDecimal::add);
                                            insertEntity.setProjectGasUsageTotal(val);
                                        }
                                    }
                                    if (kpiMap.containsKey("project.electricity.pccEnergyUsage.total")) {
                                        //购网电量,根据电耗分类配置进行统计
                                        if (CollUtil.isEmpty(deviceList)) {
                                            insertEntity.setProjectElectricityPccEnergyUsageTotal(BigDecimal.ZERO);
                                        } else {
                                            var val = deviceList.stream().filter(j -> deviceObj.containsKey(j.getDeviceId()) && deviceObj.getJSONObject(j.getDeviceId()).containsKey("energymeterEpimportTotal"))
                                                    .map(j -> deviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("energymeterEpimportTotal").multiply(new BigDecimal(j.getComputeTag())))
                                                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                                            var val1 = deviceList.stream().filter(j -> deviceObj.containsKey(j.getDeviceId()) && deviceObj.getJSONObject(j.getDeviceId()).containsKey("gscnEpimportTotal"))
                                                    .map(j -> deviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("gscnEpimportTotal").multiply(new BigDecimal(j.getComputeTag())))
                                                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                                            var val2 = deviceList.stream().filter(j -> deviceObj.containsKey(j.getDeviceId()) && deviceObj.getJSONObject(j.getDeviceId()).containsKey("znbEpimportTotal"))
                                                    .map(j -> deviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("znbEpimportTotal").multiply(new BigDecimal(j.getComputeTag())))
                                                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                                            insertEntity.setProjectElectricityPccEnergyUsageTotal(val.add(val1).add(val2));
                                        }
                                    }
                                    if (kpiMap.containsKey("project.electricity.pccEnergyProduction.total")) {
                                        //上网电量,根据电耗分类配置进行统计
                                        if (CollUtil.isEmpty(deviceList)) {
                                            insertEntity.setProjectElectricityPccEnergyProductionTotal(BigDecimal.ZERO);
                                        }
                                        var val = deviceList.stream().filter(j -> deviceObj.containsKey(j.getDeviceId()) && deviceObj.getJSONObject(j.getDeviceId()).containsKey("energymeterEpexportTotal"))
                                                .map(j -> deviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("energymeterEpexportTotal").multiply(new BigDecimal(j.getComputeTag())))
                                                .reduce(BigDecimal.ZERO, BigDecimal::add);
                                        var val1 = deviceList.stream().filter(j -> deviceObj.containsKey(j.getDeviceId()) && deviceObj.getJSONObject(j.getDeviceId()).containsKey("gscnEpexportTotal"))
                                                .map(j -> deviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("gscnEpexportTotal").multiply(new BigDecimal(j.getComputeTag())))
                                                .reduce(BigDecimal.ZERO, BigDecimal::add);
                                        var val2 = deviceList.stream().filter(j -> deviceObj.containsKey(j.getDeviceId()) && deviceObj.getJSONObject(j.getDeviceId()).containsKey("znbEpexportTotal"))
                                                .map(j -> deviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("znbEpexportTotal").multiply(new BigDecimal(j.getComputeTag())))
                                                .reduce(BigDecimal.ZERO, BigDecimal::add);
                                        insertEntity.setProjectElectricityPccEnergyProductionTotal(val.add(val1).add(val2));
                                    }
                                    if (kpiMap.containsKey("project.electricity.pvEnergyProduction.total")) {
                                        //总发电量,根据电耗分类配置进行统计
                                        if (CollUtil.isEmpty(deviceList)) {
                                            insertEntity.setProjectElectricityPvEnergyProductionTotal(BigDecimal.ZERO);
                                        }
                                        var val = deviceList.stream().filter(j -> deviceObj.containsKey(j.getDeviceId()) && deviceObj.getJSONObject(j.getDeviceId()).containsKey("energymeterEpexportTotal"))
                                                .map(j -> deviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("energymeterEpexportTotal").multiply(new BigDecimal(j.getComputeTag())))
                                                .reduce(BigDecimal.ZERO, BigDecimal::add);
                                        var val1 = deviceList.stream().filter(j -> deviceObj.containsKey(j.getDeviceId()) && deviceObj.getJSONObject(j.getDeviceId()).containsKey("gscnEpexportTotal"))
                                                .map(j -> deviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("gscnEpexportTotal").multiply(new BigDecimal(j.getComputeTag())))
                                                .reduce(BigDecimal.ZERO, BigDecimal::add);
                                        var val2 = deviceList.stream().filter(j -> deviceObj.containsKey(j.getDeviceId()) && deviceObj.getJSONObject(j.getDeviceId()).containsKey("znbEpexportTotal"))
                                                .map(j -> deviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("znbEpexportTotal").multiply(new BigDecimal(j.getComputeTag())))
                                                .reduce(BigDecimal.ZERO, BigDecimal::add);
                                        insertEntity.setProjectElectricityPvEnergyProductionTotal(val.add(val1).add(val2));
                                    }
                                    if (kpiMap.containsKey("project.electricity.storageEnergyUsage.total")) {
                                        //储充电量,根据电耗分类配置进行统计
                                        if (CollUtil.isEmpty(deviceList)) {
                                            insertEntity.setProjectElectricityStorageEnergyUsageTotal(BigDecimal.ZERO);
                                        } else {
                                            var val = deviceList.stream().filter(j -> deviceObj.containsKey(j.getDeviceId()) && deviceObj.getJSONObject(j.getDeviceId()).containsKey("energymeterEpimportTotal"))
                                                    .map(j -> deviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("energymeterEpimportTotal").multiply(new BigDecimal(j.getComputeTag())))
                                                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                                            var val1 = deviceList.stream().filter(j -> deviceObj.containsKey(j.getDeviceId()) && deviceObj.getJSONObject(j.getDeviceId()).containsKey("gscnEpimportTotal"))
                                                    .map(j -> deviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("gscnEpimportTotal").multiply(new BigDecimal(j.getComputeTag())))
                                                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                                            var val2 = deviceList.stream().filter(j -> deviceObj.containsKey(j.getDeviceId()) && deviceObj.getJSONObject(j.getDeviceId()).containsKey("znbEpimportTotal"))
                                                    .map(j -> deviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("znbEpimportTotal").multiply(new BigDecimal(j.getComputeTag())))
                                                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                                            insertEntity.setProjectElectricityStorageEnergyUsageTotal(val.add(val1).add(val2));
                                        }
                                    }
                                    //储光电量 最后计算
                                    if (kpiMap.containsKey("project.electricity.storageEnergyUsagePv.total")) {
                                        callStorageEnergyUsagePvTotal = true;
                                    }
                                    if (kpiMap.containsKey("project.electricity.storageEnergyUsageGrid.total")) {
                                        callStorageEnergyUsageGridTotal = true;

                                    }
                                    if (kpiMap.containsKey("project.electricity.storageEnergyProduction.total")) {
                                        //储放电量,由小时表汇总得到
                                        if (CollUtil.isEmpty(deviceList)) {
                                            insertEntity.setProjectElectricityStorageEnergyProductionTotal(BigDecimal.ZERO);
                                        } else {
                                            var val = deviceList.stream().filter(j -> deviceObj.containsKey(j.getDeviceId()) && deviceObj.getJSONObject(j.getDeviceId()).containsKey("energymeterEpexportTotal"))
                                                    .map(j -> deviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("energymeterEpexportTotal").multiply(new BigDecimal(j.getComputeTag())))
                                                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                                            var val1 = deviceList.stream().filter(j -> deviceObj.containsKey(j.getDeviceId()) && deviceObj.getJSONObject(j.getDeviceId()).containsKey("gscnEpexportTotal"))
                                                    .map(j -> deviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("gscnEpexportTotal").multiply(new BigDecimal(j.getComputeTag())))
                                                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                                            var val2 = deviceList.stream().filter(j -> deviceObj.containsKey(j.getDeviceId()) && deviceObj.getJSONObject(j.getDeviceId()).containsKey("znbEpexportTotal"))
                                                    .map(j -> deviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("znbEpexportTotal").multiply(new BigDecimal(j.getComputeTag())))
                                                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                                            insertEntity.setProjectElectricityStorageEnergyProductionTotal(val.add(val1).add(val2));
                                        }
                                    }
                                    if (kpiMap.containsKey("project.electricity.subLightingEnergy.total")) {
                                        //照明总用电量,根据电耗分类配置进行统计
                                        if (CollUtil.isEmpty(deviceList)) {
                                            insertEntity.setProjectElectricitySubLightingEnergyTotal(BigDecimal.ZERO);
                                        }
                                        var val = deviceList.stream().filter(j -> deviceObj.containsKey(j.getDeviceId()) && deviceObj.getJSONObject(j.getDeviceId()).containsKey("energymeterEpimportTotal"))
                                                .map(j -> deviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("energymeterEpimportTotal").multiply(new BigDecimal(j.getComputeTag())))
                                                .reduce(BigDecimal.ZERO, BigDecimal::add);
                                        var val1 = deviceList.stream().filter(j -> deviceObj.containsKey(j.getDeviceId()) && deviceObj.getJSONObject(j.getDeviceId()).containsKey("gscnEpimportTotal"))
                                                .map(j -> deviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("gscnEpimportTotal").multiply(new BigDecimal(j.getComputeTag())))
                                                .reduce(BigDecimal.ZERO, BigDecimal::add);
                                        var val2 = deviceList.stream().filter(j -> deviceObj.containsKey(j.getDeviceId()) && deviceObj.getJSONObject(j.getDeviceId()).containsKey("znbEpimportTotal"))
                                                .map(j -> deviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("znbEpimportTotal").multiply(new BigDecimal(j.getComputeTag())))
                                                .reduce(BigDecimal.ZERO, BigDecimal::add);
                                        insertEntity.setProjectElectricitySubLightingEnergyTotal(val.add(val1).add(val2));
                                    }
                                    if (kpiMap.containsKey("project.electricity.subChargeEnergy.total")) {
                                        //总充电桩电量,根据电耗分类配置进行统计
                                        if (CollUtil.isEmpty(deviceList)) {
                                            insertEntity.setProjectElectricitySubChargeEnergyTotal(BigDecimal.ZERO);
                                        }
                                        var val = deviceList.stream().filter(j -> deviceObj.containsKey(j.getDeviceId()) && deviceObj.getJSONObject(j.getDeviceId()).containsKey("energymeterEpimportTotal"))
                                                .map(j -> deviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("energymeterEpimportTotal").multiply(new BigDecimal(j.getComputeTag())))
                                                .reduce(BigDecimal.ZERO, BigDecimal::add);
                                        var val1 = deviceList.stream().filter(j -> deviceObj.containsKey(j.getDeviceId()) && deviceObj.getJSONObject(j.getDeviceId()).containsKey("gscnEpimportTotal"))
                                                .map(j -> deviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("gscnEpimportTotal").multiply(new BigDecimal(j.getComputeTag())))
                                                .reduce(BigDecimal.ZERO, BigDecimal::add);
                                        var val2 = deviceList.stream().filter(j -> deviceObj.containsKey(j.getDeviceId()) && deviceObj.getJSONObject(j.getDeviceId()).containsKey("znbEpimportTotal"))
                                                .map(j -> deviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("znbEpimportTotal").multiply(new BigDecimal(j.getComputeTag())))
                                                .reduce(BigDecimal.ZERO, BigDecimal::add);
                                        insertEntity.setProjectElectricitySubChargeEnergyTotal(val.add(val1).add(val2));
                                    }
                                    if (kpiMap.containsKey("project.electricity.subSocketEnergy.total")) {
                                        //插座总用电量,根据电耗分类配置进行统计
                                        if (CollUtil.isEmpty(deviceList)) {
                                            insertEntity.setProjectElectricitySubSocketEnergyTotal(BigDecimal.ZERO);
                                        }
                                        var val = deviceList.stream().filter(j -> deviceObj.containsKey(j.getDeviceId()) && deviceObj.getJSONObject(j.getDeviceId()).containsKey("energymeterEpimportTotal"))
                                                .map(j -> deviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("energymeterEpimportTotal").multiply(new BigDecimal(j.getComputeTag())))
                                                .reduce(BigDecimal.ZERO, BigDecimal::add);
                                        var val1 = deviceList.stream().filter(j -> deviceObj.containsKey(j.getDeviceId()) && deviceObj.getJSONObject(j.getDeviceId()).containsKey("gscnEpimportTotal"))
                                                .map(j -> deviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("gscnEpimportTotal").multiply(new BigDecimal(j.getComputeTag())))
                                                .reduce(BigDecimal.ZERO, BigDecimal::add);
                                        var val2 = deviceList.stream().filter(j -> deviceObj.containsKey(j.getDeviceId()) && deviceObj.getJSONObject(j.getDeviceId()).containsKey("znbEpimportTotal"))
                                                .map(j -> deviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("znbEpimportTotal").multiply(new BigDecimal(j.getComputeTag())))
                                                .reduce(BigDecimal.ZERO, BigDecimal::add);
                                        insertEntity.setProjectElectricitySubSocketEnergyTotal(val.add(val1).add(val2));
                                    }
                                    // 统计环境相关信息
                                    List<WeatherHistoryDTO> projectWeatherHistoryList = weatherHistoryList.stream().filter(item -> item.getWeatherCode().equals(projInfo.getWeatherCode())).toList();

                                    if (!CollectionUtils.isEmpty(projectWeatherHistoryList)) {
                                        // 修改该项目2h前的数据
                                        ProjectStaSubitemHourEntity updateEntity = new ProjectStaSubitemHourEntity();
                                        //平均温度
                                        updateEntity.setProjectEnvironmentOutTempAvg(projectWeatherHistoryList.stream().map(WeatherHistoryDTO::getTemperature).reduce(BigDecimal.ZERO, BigDecimal::add).divide(BigDecimal.valueOf(projectWeatherHistoryList.size()), 2, RoundingMode.HALF_UP));
                                        //最大温度
                                        updateEntity.setProjectEnvironmentOutTempMax(projectWeatherHistoryList.stream().map(WeatherHistoryDTO::getTemperature).max(BigDecimal::compareTo).orElse(null));
                                        //最小温度
                                        updateEntity.setProjectEnvironmentOutTempMin(projectWeatherHistoryList.stream().map(WeatherHistoryDTO::getTemperature).min(BigDecimal::compareTo).orElse(null));
                                        //平均湿度
                                        updateEntity.setProjectEnvironmentOutTumidityAvg(projectWeatherHistoryList.stream().map(WeatherHistoryDTO::getHumidity).reduce(BigDecimal.ZERO, BigDecimal::add).divide(BigDecimal.valueOf(projectWeatherHistoryList.size()), 2, RoundingMode.HALF_UP));
                                        projectStaSubitemHourServiceImpl.update(updateEntity, new QueryWrapper<ProjectStaSubitemHourEntity>().lambda()
                                                .eq(ProjectStaSubitemHourEntity::getHour, String.valueOf(weatherTime.getHour()))
                                                .eq(ProjectStaSubitemHourEntity::getDay, String.valueOf(weatherTime.getDayOfMonth()))
                                                .eq(ProjectStaSubitemHourEntity::getBizProjectId, bizProjectId)
                                                .eq(ProjectStaSubitemHourEntity::getMonth, String.valueOf(weatherTime.getMonthValue()))
                                                .eq(ProjectStaSubitemHourEntity::getYear, String.valueOf(weatherTime.getYear())));
                                    }
                                }
                            }

                            // 最后计算的指标
                            //储光电量 最后计算
                            //储光电量,储充电量-购网电量-储放电量，
                            if (callStorageEnergyUsagePvTotal) {
                                if (BigDecimal.ZERO.compareTo(insertEntity.getProjectElectricityPccEnergyUsageTotal()) == 0) {
                                    insertEntity.setProjectElectricityStorageEnergyUsagePvTotal(null);
                                }
                                BigDecimal multiply = insertEntity.getProjectElectricityStorageEnergyUsageTotal().subtract(insertEntity.getProjectElectricityPccEnergyUsageTotal()).subtract(insertEntity.getProjectElectricityStorageEnergyProductionTotal());
                                if (BigDecimal.ZERO.compareTo(multiply) > 0) {
                                    insertEntity.setProjectElectricityStorageEnergyUsagePvTotal(BigDecimal.ZERO);
                                } else {
                                    insertEntity.setProjectElectricityStorageEnergyUsagePvTotal(multiply);
                                }
                            }
                            //储市电量
                            if (callStorageEnergyUsageGridTotal) {
                                //储市电量,由储充电量-储光电量，不可计算时，指标为空。
                                BigDecimal decimal1 = insertEntity.getProjectElectricityStorageEnergyUsageTotal();
                                BigDecimal decimal2 = insertEntity.getProjectElectricityStorageEnergyUsagePvTotal();
                                if (Objects.isNull(decimal2) || Objects.isNull(decimal1)) {
                                    insertEntity.setProjectElectricityStorageEnergyUsageGridTotal(null);
                                } else {
                                    BigDecimal bigDecimal = decimal1.subtract(decimal2);
                                    if (BigDecimal.ZERO.compareTo(bigDecimal) > 0) {
                                        insertEntity.setProjectElectricityStorageEnergyUsageGridTotal(BigDecimal.ZERO);
                                    } else {
                                        insertEntity.setProjectElectricityStorageEnergyUsageGridTotal(bigDecimal);
                                    }
                                }
                            }


                            // 正常插入就行
                            projectStaSubitemHourServiceImpl.save(insertEntity);
                        }
                    } catch (Exception e) {
                        jobLogMap.get(tenantId).setStatus(JOB_EXEC_ERROR);
                        log.error("分项小时项目执行异常", e);
                    }
                }
            } finally {
                saveJobLog(jobLogMap, tenantProjectMap);
            }
        }
        return true;
    }

    @Override
    public boolean statisticsByDay(String[] times, JobRpcRequest request) {
        List<LocalDateTime> timeList = new ArrayList<>();
        if (!ArrayUtil.isEmpty(times)) {
            // 小时的时间，应该是yyyy-MM-dd HH
            for (String time : times) {
                timeList.add(LocalDateTime.parse(time + " 00", hourFormatter).withHour(0).withMinute(0).withSecond(0).withNano(0));
            }
        } else {
            timeList.add(LocalDateTime.now().minusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0));
        }
        // 获取需要分享处理的kpicode
        List<ProjectKpiConfigEntity> kpiConfList = projectKpiConfigServiceImpl.list(new QueryWrapper<ProjectKpiConfigEntity>().lambda().like(ProjectKpiConfigEntity::getCode, "project%").eq(ProjectKpiConfigEntity::getStaIntervalYmd, 1));

        if (CollectionUtils.isEmpty(kpiConfList)) {
            // 不需要统计， 返回即可
            return true;
        }
        Map<String, List<ProjectKpiConfigEntity>> kpiConfigMap = kpiConfList.stream().collect(Collectors.groupingBy(ProjectKpiConfigEntity::getKpiSubtype));
//                kpiConfList.stream().collect(Collectors.groupingBy(ProjectKpiConfigEntity::getTenantId)).entrySet()
//                        .stream().collect(Collectors.toMap(Map.Entry::getKey,
//                                v -> v.getValue().stream().collect(Collectors.groupingBy(ProjectKpiConfigEntity::getKpiSubtype))));

        // 通过kpiSubtype查找对应的subitem的配置
        List<String> kpiSubtypeList = kpiConfList.stream().map(ProjectKpiConfigEntity::getKpiSubtype).filter(CharSequenceUtil::isNotBlank).distinct().collect(Collectors.toList());
        List<ProjectCnfSubitemEntity> subitemConfList = projectCnfSubitemServiceImpl.list(new QueryWrapper<ProjectCnfSubitemEntity>().lambda().in(ProjectCnfSubitemEntity::getKpiSubtype, kpiSubtypeList));

        if (CollectionUtils.isEmpty(subitemConfList)) {
            // 不需要统计， 返回即可
            return true;
        }

        Map<String, List<ProjectCnfSubitemEntity>> projSubitemConfMap = subitemConfList.stream().collect(Collectors.groupingBy(ProjectCnfSubitemEntity::getProjectId));

        List<Long> subitemIdList = subitemConfList.stream().map(ProjectCnfSubitemEntity::getId).collect(Collectors.toList());

        // 获取配置的设备列表
        List<ProjectSubitemDeviceEntity> deviceConfList = projectSubitemDeviceServiceImpl.list(new QueryWrapper<ProjectSubitemDeviceEntity>().lambda().in(ProjectSubitemDeviceEntity::getSubitemId, subitemIdList));

        Map<Long, Map<Long, List<ProjectSubitemDeviceEntity>>> deviceConfMap = deviceConfList.stream().collect(Collectors.groupingBy(ProjectSubitemDeviceEntity::getTenantId, Collectors.groupingBy(ProjectSubitemDeviceEntity::getSubitemId)));

        // 通过时间，处理对应信息
        for (LocalDateTime time : timeList) {
            Map<Long, JobLogSaveDTO> jobLogMap = new HashMap<>(8);
            Map<Long, Map<String, String>> tenantProjectMap = new HashMap<>(8);

            List<ProjectStaSubitemHourEntity> records = projectStaSubitemHourServiceImpl.list(Wrappers.<ProjectStaSubitemHourEntity>lambdaQuery().eq(ProjectStaSubitemHourEntity::getYear, String.valueOf(time.getYear())).eq(ProjectStaSubitemHourEntity::getMonth, String.valueOf(time.getMonthValue())).eq(ProjectStaSubitemHourEntity::getDay, String.valueOf(time.getDayOfMonth())));
            Map<String, List<ProjectStaSubitemHourEntity>> recordsMap = records.stream().collect(Collectors.groupingBy(ProjectStaSubitemHourEntity::getBizProjectId));

            // 如果当前时间，已有对应的记录，删掉重新出
            String bizProjectId = null;
            Long tenantId = null;
            Map<String, List<ProjectKpiConfigEntity>> kpiConfDetailMap;

            try {
                for (Map.Entry<String, List<ProjectCnfSubitemEntity>> entry : projSubitemConfMap.entrySet()) {
                    bizProjectId = entry.getValue().get(0).getProjectId();
                    tenantId = entry.getValue().get(0).getTenantId();

                    // modify by hebin. 这个步骤提前， 如果没有对应的分区设备的配置， 直接continue，节省性能
                    if (!deviceConfMap.containsKey(tenantId)) {
                        log.info("当前租户没有对应的配置，给一个空配置， 租户编号为：{}", tenantId);
                        deviceConfMap.put(tenantId, new HashMap<>());
                    }

                    List<String> totalKpi = new ArrayList<>();
                    // 拿到电费的峰谷平配置
                    List<ProjectCnfTimePeriodEntity> timePeriodList = projectCnfTimePeriodServiceImpl.list(new QueryWrapper<ProjectCnfTimePeriodEntity>().lambda().eq(ProjectCnfTimePeriodEntity::getProjectId, bizProjectId).eq(ProjectCnfTimePeriodEntity::getPeriodYear, String.valueOf(time.getYear())).eq(ProjectCnfTimePeriodEntity::getPeriodMonth, String.valueOf(time.getMonthValue())));
//                Map<String, ProjectCnfTimePeriodEntity> timePeriodMap = new HashMap<>();
//                Map<String, BigDecimal> priceMap = new HashMap<>();
//                if (CollUtil.isNotEmpty(timePeriodList)) {
//                    for (ProjectCnfTimePeriodEntity entity : timePeriodList) {
//                        for (int j = entity.getTimeBegin(); j <= entity.getTimeEnd(); j++) {
//                            timePeriodMap.put(String.valueOf(j), entity);
//                            priceMap.put(entity.getCode(), entity.getPrice());
//                        }
//                    }
//                }
                    List<ProjectCnfTimePeriodEntity> flat = timePeriodList.stream().filter(it -> CharSequenceUtil.equals(it.getCode(), "flat")).toList();
                    List<ProjectCnfTimePeriodEntity> tip = timePeriodList.stream().filter(it -> CharSequenceUtil.equals(it.getCode(), "tip")).toList();
                    List<ProjectCnfTimePeriodEntity> valley = timePeriodList.stream().filter(it -> CharSequenceUtil.equals(it.getCode(), "valley")).toList();
                    List<ProjectCnfTimePeriodEntity> peak = timePeriodList.stream().filter(it -> CharSequenceUtil.equals(it.getCode(), "peak")).toList();

                    // 水费配置
                    List<ProjectCnfWaterFeeEntity> waterFeeList = projectCnfWaterFeeServiceImpl.list(new QueryWrapper<ProjectCnfWaterFeeEntity>().lambda()
                            .eq(ProjectCnfWaterFeeEntity::getProjectId, bizProjectId));
                    ProjectCnfWaterFeeEntity waterPrice = CollectionUtils.isEmpty(waterFeeList) ? null : waterFeeList.get(0);

                    // 气费配置
                    List<ProjectCnfGasFeeEntity> gasFeeList = projectCnfGasFeeServiceImpl.list(new QueryWrapper<ProjectCnfGasFeeEntity>().lambda()
                            .eq(ProjectCnfGasFeeEntity::getProjectId, bizProjectId));
                    ProjectCnfGasFeeEntity gasPrice = CollectionUtils.isEmpty(gasFeeList) ? null : gasFeeList.get(0);

                    // 充电站配置
                    ProjectCnfChargeStationEntity cnfChargeStation = projectCnfChargeStationMapper
                            .selectOne(Wrappers.<ProjectCnfChargeStationEntity>lambdaQuery()
                                    .eq(ProjectCnfChargeStationEntity::getProjectId, bizProjectId)
                                    .eq(TenantBaseEntity::getTenantId, tenantId));
                    BigDecimal chargeStationPrice = Optional.ofNullable(cnfChargeStation)
                            .map(ProjectCnfChargeStationEntity::getPrice)
                            .orElse(BigDecimal.ZERO);
                    // 光伏配置
                    ProjectCnfPvEntity cnfPv = projectCnfPvMapper.selectOne(Wrappers.<ProjectCnfPvEntity>lambdaQuery()
                            .eq(ProjectCnfPvEntity::getProjectId, bizProjectId)
                            .eq(TenantBaseEntity::getTenantId, tenantId));
                    BigDecimal cnfPvPrice = Optional.ofNullable(cnfPv)
                            .map(ProjectCnfPvEntity::getPrice)
                            .orElse(BigDecimal.ZERO);

                    // 获取项目信息
                    Response<ProjectDetailsResponse> projectResp = projectApi.getProjectDetails(bizProjectId);
                    if (!projectResp.isSuccess() || null == projectResp.getResult()) {
                        log.error("获取项目信息失败, 项目编号为: {}", bizProjectId);
                        continue;
                    }
                    ProjectDetailsResponse projInfo = projectResp.getResult();

                    // 获取租户信息
                    Response<TenantInfoResponse> tenantResp = tenantApi.getTenantInfo(tenantId);
                    if (!tenantResp.isSuccess() || null == tenantResp.getResult()) {
                        log.error("获取租户信息失败, 租户编号为: {}", tenantId);
                        continue;
                    }
                    TenantInfoResponse tenantInfo = tenantResp.getResult();

                    // 2023-11-16 增加手动执行过滤条件
                    if (null != request.getTenantId() && !Objects.equals(tenantId, request.getTenantId())) {
                        continue;
                    }

                    if (CollectionUtil.isNotEmpty(request.getProjectList()) && !request.getProjectList().contains(bizProjectId)) {
                        continue;
                    }

                    projectStaSubitemDayServiceImpl.remove(new QueryWrapper<ProjectStaSubitemDayEntity>().lambda().eq(ProjectStaSubitemDayEntity::getBizProjectId, bizProjectId).eq(ProjectStaSubitemDayEntity::getYear, String.valueOf(time.getYear())).eq(ProjectStaSubitemDayEntity::getMonth, String.valueOf(time.getMonthValue())).eq(ProjectStaSubitemDayEntity::getDay, String.valueOf(time.getDayOfMonth())));

                    // 2023-11-15 增加日志记录
                    if (!jobLogMap.containsKey(tenantId)) {
                        JobLogSaveDTO jobLog = new JobLogSaveDTO();
                        jobLog.setJobId(request.getJobId())
                                .setTenantId(tenantId)
                                .setStatus(JOB_EXEC_SUCCESS)
                                .setExecTime(LocalDateTime.now())
                                .setExecType(request.getExecType())
                                .setExecUser(request.getExecUser());
                        jobLogMap.put(tenantId, jobLog);
                    }

                    if (!tenantProjectMap.containsKey(tenantId)) {
                        tenantProjectMap.put(tenantId, new HashMap<>(8));
                    }

                    if (!tenantProjectMap.get(tenantId).containsKey(bizProjectId)) {
                        tenantProjectMap.get(tenantId).put(bizProjectId, projInfo.getName());
                    }

                    ProjectCnfElectricityPriceEntity electricityPriceEntity = projectCnfElectricityPriceServiceImpl.selectByBizProjId(projInfo.getBizProjectId());

                    try {
                        // 通过日表累加获取结果
                        ProjectStaSubitemDayEntity insertEntity = new ProjectStaSubitemDayEntity();

                        insertEntity.setDay(String.valueOf(time.getDayOfMonth()));
                        insertEntity.setBizProjectId(bizProjectId);
                        insertEntity.setMonth(String.valueOf(time.getMonthValue()));
                        insertEntity.setYear(String.valueOf(time.getYear()));
                        insertEntity.setProjectCode(projInfo.getCode());
                        insertEntity.setProjectName(projInfo.getName());
                        insertEntity.setTenantId(tenantId);
                        insertEntity.setTenantCode(tenantInfo.getCode());
                        insertEntity.setStaTime(Timestamp.valueOf(time));

                        insertEntity.setProjectGasFeeTotal(BigDecimal.ZERO);
                        insertEntity.setProjectGasUsageTotal(BigDecimal.ZERO);
                        insertEntity.setProjectWaterUsageTotal(BigDecimal.ZERO);
                        insertEntity.setProjectWaterFeeWater(BigDecimal.ZERO);
                        insertEntity.setProjectWaterFeeSewerage(BigDecimal.ZERO);
                        insertEntity.setProjectWaterFeeTotal(BigDecimal.ZERO);
                        insertEntity.setProjectElectricityEnergyusageFlat(BigDecimal.ZERO);
                        insertEntity.setProjectElectricityEnergyusageTotal(BigDecimal.ZERO);
                        insertEntity.setProjectElectricityEnergyusagefeeTotal(BigDecimal.ZERO);
                        insertEntity.setProjectElectricityEnergyusageTip(BigDecimal.ZERO);
                        insertEntity.setProjectElectricityEnergyusageValley(BigDecimal.ZERO);
                        insertEntity.setProjectElectricityEnergyusagePeak(BigDecimal.ZERO);
                        insertEntity.setProjectElectricitySubelevatorenergyTotal(BigDecimal.ZERO);
                        insertEntity.setProjectElectricitySubguestroomenergyTotal(BigDecimal.ZERO);
                        insertEntity.setProjectElectricitySubheatingwaterenergyTotal(BigDecimal.ZERO);
                        insertEntity.setProjectElectricitySubhavcenergyTotal(BigDecimal.ZERO);
                        insertEntity.setProjectElectricitySubpowersupplyenergyTotal(BigDecimal.ZERO);
                        insertEntity.setProjectElectricitySubothertypeTotal(BigDecimal.ZERO);
                        insertEntity.setProjectElectricitySubwatersupplyenergyTotal(BigDecimal.ZERO);
                        insertEntity.setProjectCarbonTotalcoalTotal(BigDecimal.ZERO);
                        insertEntity.setProjectCarbonTotaldustTotal(BigDecimal.ZERO);
                        insertEntity.setProjectCarbonTotalco2Total(BigDecimal.ZERO);
                        insertEntity.setProjectCarbonTotalso2Total(BigDecimal.ZERO);
                        insertEntity.setProjectCarbonGasusageco2Total(BigDecimal.ZERO);
                        insertEntity.setProjectCarbonGasusagecoalTotal(BigDecimal.ZERO);
                        insertEntity.setProjectCarbonGasusagedustTotal(BigDecimal.ZERO);
                        insertEntity.setProjectCarbonGasusageso2Total(BigDecimal.ZERO);
                        insertEntity.setProjectCarbonElectricityusageso2Total(BigDecimal.ZERO);
                        insertEntity.setProjectCarbonElectricityusagedustTotal(BigDecimal.ZERO);
                        insertEntity.setProjectCarbonElectricityusageco2Total(BigDecimal.ZERO);
                        insertEntity.setProjectCarbonElectricityusagecoalTotal(BigDecimal.ZERO);
                        insertEntity.setProjectCarbonWaterusagedustTotal(BigDecimal.ZERO);
                        insertEntity.setProjectCarbonWaterusagecoalTotal(BigDecimal.ZERO);
                        insertEntity.setProjectCarbonWaterusageco2Total(BigDecimal.ZERO);
                        insertEntity.setProjectCarbonWaterusageso2Total(BigDecimal.ZERO);
                        insertEntity.setProjectEnvironmentOutTempAvg(BigDecimal.ZERO);
                        insertEntity.setProjectEnvironmentOutTempMax(BigDecimal.ZERO);
                        insertEntity.setProjectEnvironmentOutTempMin(BigDecimal.ZERO);
                        insertEntity.setProjectEnvironmentOutTumidityAvg(BigDecimal.ZERO);
                        insertEntity.setProjectElectricityPccEnergyUsageTotal(BigDecimal.ZERO);
                        insertEntity.setProjectElectricityPccEnergyUsageFeeTotal(BigDecimal.ZERO);
                        insertEntity.setProjectElectricityPccEnergyUsageTip(BigDecimal.ZERO);
                        insertEntity.setProjectElectricityPccEnergyUsagePeak(BigDecimal.ZERO);
                        insertEntity.setProjectElectricityPccEnergyUsageValley(BigDecimal.ZERO);
                        insertEntity.setProjectElectricityPccEnergyUsageFlat(BigDecimal.ZERO);
                        insertEntity.setProjectElectricityPccEnergyProductionTotal(BigDecimal.ZERO);
                        insertEntity.setProjectElectricityPccEnergyProductionFeeTotal(BigDecimal.ZERO);
                        insertEntity.setProjectElectricityPvEnergyProductionTotal(BigDecimal.ZERO);
                        insertEntity.setProjectElectricityPvEnergyProductionGridTotal(BigDecimal.ZERO);
                        insertEntity.setProjectElectricityPvEnergyProductionLoadTotal(BigDecimal.ZERO);
                        insertEntity.setProjectElectricityPvEnergyProductionStorageTotal(BigDecimal.ZERO);
                        insertEntity.setProjectElectricityPvEnergyProductionFeeTotal(BigDecimal.ZERO);
                        insertEntity.setProjectElectricityPvEnergyProductionGridFeeTotal(BigDecimal.ZERO);
                        insertEntity.setProjectElectricityPvEnergyProductionUsageFeeTotal(BigDecimal.ZERO);
                        insertEntity.setProjectElectricityStorageEnergyUsageTotal(BigDecimal.ZERO);
                        insertEntity.setProjectElectricityStorageEnergyUsageTip(BigDecimal.ZERO);
                        insertEntity.setProjectElectricityStorageEnergyUsagePeak(BigDecimal.ZERO);
                        insertEntity.setProjectElectricityStorageEnergyUsageValley(BigDecimal.ZERO);
                        insertEntity.setProjectElectricityStorageEnergyUsageFlat(BigDecimal.ZERO);
                        insertEntity.setProjectElectricityStorageEnergyUsagePvTotal(BigDecimal.ZERO);
                        insertEntity.setProjectElectricityStorageEnergyUsageGridTotal(BigDecimal.ZERO);
                        insertEntity.setProjectElectricityStorageEnergyProductionTotal(BigDecimal.ZERO);
                        insertEntity.setProjectElectricityStorageEnergyProductionTip(BigDecimal.ZERO);
                        insertEntity.setProjectElectricityStorageEnergyProductionPeak(BigDecimal.ZERO);
                        insertEntity.setProjectElectricityStorageEnergyProductionValley(BigDecimal.ZERO);
                        insertEntity.setProjectElectricityStorageEnergyProductionFlat(BigDecimal.ZERO);
                        insertEntity.setProjectElectricityStorageEnergyNetFeeTotal(BigDecimal.ZERO);
                        insertEntity.setProjectElectricitySubChargeEnergyTotal(BigDecimal.ZERO);
                        insertEntity.setProjectElectricitySubChargeEnergyFeeTotal(BigDecimal.ZERO);
                        insertEntity.setProjectElectricitySubChargeEnergyChargeFeeTotal(BigDecimal.ZERO);
                        insertEntity.setProjectElectricitySubChargeEnergyServiceFeeTotal(BigDecimal.ZERO);
                        insertEntity.setProjectElectricitySubLightingEnergyTotal(BigDecimal.ZERO);
                        insertEntity.setProjectElectricitySubSocketEnergyTotal(BigDecimal.ZERO);
                        insertEntity.setProjectCarbonPvReductionCoalTotal(BigDecimal.ZERO);
                        insertEntity.setProjectCarbonPvReductionCO2Total(BigDecimal.ZERO);
                        insertEntity.setProjectCarbonPvReductionSO2Total(BigDecimal.ZERO);
                        insertEntity.setProjectCarbonPvReductionDustTotal(BigDecimal.ZERO);

                        List<ProjectStaSubitemHourEntity> list = recordsMap.get(bizProjectId);
                        for (ProjectCnfSubitemEntity i : entry.getValue()) {
                            List<ProjectKpiConfigEntity> kpiList = kpiConfigMap.get(i.getKpiSubtype());
                            Map<String, ProjectKpiConfigEntity> kpiCodeMap = kpiList.stream().collect(Collectors.toMap(ProjectKpiConfigEntity::getCode, Function.identity(), (v1, v2) -> v1));
                            totalKpi.addAll(kpiCodeMap.keySet().stream().toList());
                            Map<Long, List<ProjectSubitemDeviceEntity>> deviceConfDetailMap = deviceConfMap.get(tenantId);
                            List<ProjectSubitemDeviceEntity> deviceList = deviceConfDetailMap.get(i.getId());
                            Map<String, Object> dayDeviceStatus = Maps.newHashMap();
                            if (!CollectionUtils.isEmpty(deviceList)) {
                                List<String> deviceIds = deviceList.stream().map(ProjectSubitemDeviceEntity::getDeviceId).distinct().collect(Collectors.toList());
                                // 查询设备的小时信息，从4张表查
                                List<ProjectStaDeviceAirDayEntity> dayAirList = projectStaDeviceAirDayServiceImpl.list(new QueryWrapper<ProjectStaDeviceAirDayEntity>().lambda().in(ProjectStaDeviceAirDayEntity::getBizDeviceId, deviceIds).eq(ProjectStaDeviceAirDayEntity::getYear, String.valueOf(time.getYear())).eq(ProjectStaDeviceAirDayEntity::getMonth, String.valueOf(time.getMonthValue())).eq(ProjectStaDeviceAirDayEntity::getDay, String.valueOf(time.getDayOfMonth())));
                                if (!CollectionUtils.isEmpty(dayAirList)) {
                                    dayDeviceStatus.putAll(dayAirList.stream().collect(Collectors.toMap(ProjectStaDeviceAirDayEntity::getBizDeviceId, j -> j, (v1, v2) -> v1)));
                                }
                                List<ProjectStaDeviceElectricityDayEntity> dayElectricityList = projectStaDeviceElectricityDayServiceImpl.list(new QueryWrapper<ProjectStaDeviceElectricityDayEntity>().lambda().in(ProjectStaDeviceElectricityDayEntity::getBizDeviceId, deviceIds).eq(ProjectStaDeviceElectricityDayEntity::getYear, String.valueOf(time.getYear())).eq(ProjectStaDeviceElectricityDayEntity::getMonth, String.valueOf(time.getMonthValue())).eq(ProjectStaDeviceElectricityDayEntity::getDay, String.valueOf(time.getDayOfMonth())));
                                if (!CollectionUtils.isEmpty(dayElectricityList)) {
                                    dayDeviceStatus.putAll(dayElectricityList.stream().collect(Collectors.toMap(ProjectStaDeviceElectricityDayEntity::getBizDeviceId, j -> j, (v1, v2) -> v1)));
                                }
                                List<ProjectStaDeviceWaterDayEntity> dayWaterList = projectStaDeviceWaterDayServiceImpl.list(new QueryWrapper<ProjectStaDeviceWaterDayEntity>().lambda().in(ProjectStaDeviceWaterDayEntity::getBizDeviceId, deviceIds).eq(ProjectStaDeviceWaterDayEntity::getYear, String.valueOf(time.getYear())).eq(ProjectStaDeviceWaterDayEntity::getMonth, String.valueOf(time.getMonthValue())).eq(ProjectStaDeviceWaterDayEntity::getDay, String.valueOf(time.getDayOfMonth())));
                                if (!CollectionUtils.isEmpty(dayWaterList)) {
                                    dayDeviceStatus.putAll(dayWaterList.stream().collect(Collectors.toMap(ProjectStaDeviceWaterDayEntity::getBizDeviceId, j -> j, (v1, v2) -> v1)));
                                }
                                List<ProjectStaDeviceGasDayEntity> dayGasList = projectStaDeviceGasDayServiceImpl.list(new QueryWrapper<ProjectStaDeviceGasDayEntity>().lambda().in(ProjectStaDeviceGasDayEntity::getBizDeviceId, deviceIds).eq(ProjectStaDeviceGasDayEntity::getYear, String.valueOf(time.getYear())).eq(ProjectStaDeviceGasDayEntity::getMonth, String.valueOf(time.getMonthValue())).eq(ProjectStaDeviceGasDayEntity::getDay, String.valueOf(time.getDayOfMonth())));
                                if (!CollectionUtils.isEmpty(dayGasList)) {
                                    dayDeviceStatus.putAll(dayGasList.stream().collect(Collectors.toMap(ProjectStaDeviceGasDayEntity::getBizDeviceId, j -> j, (v1, v2) -> v1)));
                                }
                                List<ProjectStaDeviceGscnDayEntity> dayGascnList = projectStaDeviceGscnDayMapper.selectList(Wrappers.<ProjectStaDeviceGscnDayEntity>lambdaQuery().in(ProjectStaDeviceGscnDayEntity::getBizDeviceId, deviceIds).eq(ProjectStaDeviceGscnDayEntity::getYear, String.valueOf(time.getYear())).eq(ProjectStaDeviceGscnDayEntity::getMonth, String.valueOf(time.getMonthValue())).eq(ProjectStaDeviceGscnDayEntity::getDay, String.valueOf(time.getDayOfMonth())));
                                if (!CollectionUtils.isEmpty(dayGascnList)) {
                                    dayDeviceStatus.putAll(dayGascnList.stream().collect(Collectors.toMap(ProjectStaDeviceGscnDayEntity::getBizDeviceId, j -> j, (v1, v2) -> v1)));
                                }
                                List<ProjectStaDeviceZnbDayEntity> dayZnbList = projectStaDeviceZnbDayMapper.selectList(Wrappers.<ProjectStaDeviceZnbDayEntity>lambdaQuery().in(ProjectStaDeviceZnbDayEntity::getBizDeviceId, deviceIds).eq(ProjectStaDeviceZnbDayEntity::getYear, String.valueOf(time.getYear())).eq(ProjectStaDeviceZnbDayEntity::getMonth, String.valueOf(time.getMonthValue())).eq(ProjectStaDeviceZnbDayEntity::getDay, String.valueOf(time.getDayOfMonth())));
                                if (!CollectionUtils.isEmpty(dayZnbList)) {
                                    dayDeviceStatus.putAll(dayZnbList.stream().collect(Collectors.toMap(ProjectStaDeviceZnbDayEntity::getBizDeviceId, j -> j, (v1, v2) -> v1)));
                                }
                            } else {
                                deviceList = Lists.newArrayList();
                            }
                            JSONObject dayDeviceObj = JSONObject.parseObject(JSON.toJSONString(dayDeviceStatus));

                            // 环境相关
                            //平均温度
                            List<BigDecimal> tempAvg = list.stream().map(ProjectStaSubitemHourEntity::getProjectEnvironmentOutTempAvg).filter(Objects::nonNull).toList();
                            BigDecimal totalTemp = tempAvg.stream().reduce(BigDecimal.ZERO, BigDecimal::add).divide(BigDecimal.valueOf(tempAvg.size()), 2, RoundingMode.HALF_UP);
                            insertEntity.setProjectEnvironmentOutTempAvg(totalTemp);
                            //最大温度
                            insertEntity.setProjectEnvironmentOutTempMax(list.stream().map(ProjectStaSubitemHourEntity::getProjectEnvironmentOutTempMax).max(BigDecimal::compareTo).orElse(null));
                            //最小温度
                            insertEntity.setProjectEnvironmentOutTempMin(list.stream().map(ProjectStaSubitemHourEntity::getProjectEnvironmentOutTempMin).min(BigDecimal::compareTo).orElse(null));
                            //平均湿度
                            List<BigDecimal> humidityAvg = list.stream().map(ProjectStaSubitemHourEntity::getProjectEnvironmentOutTumidityAvg).filter(Objects::nonNull).toList();
                            BigDecimal totalHumi = humidityAvg.stream().reduce(BigDecimal.ZERO, BigDecimal::add).divide(BigDecimal.valueOf(humidityAvg.size()), 2, RoundingMode.HALF_UP);
                            insertEntity.setProjectEnvironmentOutTumidityAvg(totalHumi);

                            if (kpiCodeMap.containsKey("project.gas.usage.total") && CollUtil.isNotEmpty(list)) {
                                // 用气量累加
                                if (CollUtil.isEmpty(deviceList)) {
                                    insertEntity.setProjectGasUsageTotal(BigDecimal.ZERO);
                                } else {
                                    var val = deviceList.stream().filter(j -> dayDeviceObj.containsKey(j.getDeviceId()) && dayDeviceObj.getJSONObject(j.getDeviceId()).containsKey("gasmeterUsageTotal")).map(j -> dayDeviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("gasmeterUsageTotal").multiply(new BigDecimal(j.getComputeTag()))).reduce(BigDecimal.ZERO, BigDecimal::add);
                                    insertEntity.setProjectGasUsageTotal(val);
                                }
                            }
                            // 燃气单价写死
                            if (null != gasPrice && null != gasPrice.getPrice()) {
                                // project.gas.usage.fee
                                insertEntity.setProjectGasFeeTotal(insertEntity.getProjectGasUsageTotal().multiply(gasPrice.getPrice()));
                            }
                            if (kpiCodeMap.containsKey("project.water.usage.total") && CollUtil.isNotEmpty(list)) {
                                // 计算用水量，取值包含watermeter.usage.total的
                                if (CollUtil.isEmpty(deviceList)) {
                                    insertEntity.setProjectWaterUsageTotal(BigDecimal.ZERO);
                                } else {
                                    var val = deviceList.stream().filter(j -> dayDeviceObj.containsKey(j.getDeviceId()) && dayDeviceObj.getJSONObject(j.getDeviceId()).containsKey("watermeterUsageTotal")).map(j -> dayDeviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("watermeterUsageTotal").multiply(new BigDecimal(j.getComputeTag()))).reduce(BigDecimal.ZERO, BigDecimal::add);
                                    insertEntity.setProjectWaterUsageTotal(val);
                                }
                            }
                            if (kpiCodeMap.containsKey("project.water.HVACUsage.total") && CollUtil.isNotEmpty(list)) {
                                // 计算空调补水，取值包含watermeter.usage.total的
                                if (CollUtil.isEmpty(deviceList)) {
                                    insertEntity.setProjectWaterHvacusageTotal(BigDecimal.ZERO);
                                } else {
                                    var val = deviceList.stream().filter(j -> dayDeviceObj.containsKey(j.getDeviceId()) && dayDeviceObj.getJSONObject(j.getDeviceId()).containsKey("watermeterUsageTotal")).map(j -> dayDeviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("watermeterUsageTotal").multiply(new BigDecimal(j.getComputeTag()))).reduce(BigDecimal.ZERO, BigDecimal::add);
                                    insertEntity.setProjectWaterHvacusageTotal(val);
                                }
                            }
                            if (kpiCodeMap.containsKey("project.water.HeatingWaterUsage.total") && CollUtil.isNotEmpty(list)) {
                                // 计算热水补水，取值包含watermeter.usage.total的
                                if (CollUtil.isEmpty(deviceList)) {
                                    insertEntity.setProjectWaterHeatingwaterusageTotal(BigDecimal.ZERO);
                                } else {
                                    var val = deviceList.stream().filter(j -> dayDeviceObj.containsKey(j.getDeviceId()) && dayDeviceObj.getJSONObject(j.getDeviceId()).containsKey("watermeterUsageTotal")).map(j -> dayDeviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("watermeterUsageTotal").multiply(new BigDecimal(j.getComputeTag()))).reduce(BigDecimal.ZERO, BigDecimal::add);
                                    insertEntity.setProjectWaterHeatingwaterusageTotal(val);
                                }
                            }
//                    if (kpiCodeMap.containsKey("project.water.fee.water")) {
                            if (null != waterPrice) {
                                if (null != waterPrice.getPrice()) {
                                    insertEntity.setProjectWaterFeeWater(insertEntity.getProjectWaterUsageTotal().multiply(waterPrice.getPrice()));
                                }
                                if (null != waterPrice.getSewageRatio() && null != waterPrice.getSewagePrice()) {
//                    if (kpiCodeMap.containsKey("project.water.fee.sewerage")) {
                                    insertEntity.setProjectWaterFeeSewerage(insertEntity.getProjectWaterUsageTotal().multiply(waterPrice.getSewageRatio()).multiply(waterPrice.getSewagePrice()));
                                }
//                    if (kpiCodeMap.containsKey("project.water.fee.total")) {
                                insertEntity.setProjectWaterFeeTotal(insertEntity.getProjectWaterFeeWater().add(insertEntity.getProjectWaterFeeSewerage()));
                            }

                            if (kpiCodeMap.containsKey("project.electricity.energyUsage.total") && CollUtil.isNotEmpty(list)) {
                                if (CollUtil.isEmpty(deviceList)) {
                                    insertEntity.setProjectElectricityEnergyusageTotal(BigDecimal.ZERO);
                                } else {
                                    var val = deviceList.stream().filter(j -> dayDeviceObj.containsKey(j.getDeviceId()) && dayDeviceObj.getJSONObject(j.getDeviceId()).containsKey("energymeterEpimportTotal")).map(j -> dayDeviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("energymeterEpimportTotal").multiply(new BigDecimal(j.getComputeTag()))).reduce(BigDecimal.ZERO, BigDecimal::add);
                                    insertEntity.setProjectElectricityEnergyusageTotal(val);
                                }
                            }

                            if (null != electricityPriceEntity && ElectricityPriceTypeEnum.TOU.getType().equals(electricityPriceEntity.getType())) {
                                if (kpiCodeMap.containsKey("project.electricity.energyUsage.flat") && CollUtil.isNotEmpty(list)) {
                                    BigDecimal total = list.stream().filter(it -> condition(it, flat)).map(ProjectStaSubitemHourEntity::getProjectElectricityEnergyusageTotal).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
                                    insertEntity.setProjectElectricityEnergyusageFlat(total);
                                }
                                if (kpiCodeMap.containsKey("project.electricity.energyUsage.tip") && CollUtil.isNotEmpty(list)) {
                                    BigDecimal total = list.stream().filter(it -> condition(it, tip)).map(ProjectStaSubitemHourEntity::getProjectElectricityEnergyusageTotal).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
                                    insertEntity.setProjectElectricityEnergyusageTip(total);
                                }
                                if (kpiCodeMap.containsKey("project.electricity.energyUsage.valley") && CollUtil.isNotEmpty(list)) {
                                    BigDecimal total = list.stream().filter(it -> condition(it, valley)).map(ProjectStaSubitemHourEntity::getProjectElectricityEnergyusageTotal).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
                                    insertEntity.setProjectElectricityEnergyusageValley(total);
                                }
                                if (kpiCodeMap.containsKey("project.electricity.energyUsage.peak") && CollUtil.isNotEmpty(list)) {
                                    BigDecimal total = list.stream().filter(it -> condition(it, peak)).map(ProjectStaSubitemHourEntity::getProjectElectricityEnergyusageTotal).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
                                    insertEntity.setProjectElectricityEnergyusagePeak(total);
                                }
                                if (kpiCodeMap.containsKey("project.electricity.energyUsage.total") && CollUtil.isNotEmpty(list)) {
                                    insertEntity.setProjectElectricityEnergyusagefeeTotal(insertEntity.getProjectElectricityEnergyusageFlat().multiply(price(flat)).add(insertEntity.getProjectElectricityEnergyusageTip().multiply(price(tip)).add(insertEntity.getProjectElectricityEnergyusageValley().multiply(price(valley))).add(insertEntity.getProjectElectricityEnergyusagePeak().multiply(price(peak)))));
                                }
                            } else if (null != electricityPriceEntity && ElectricityPriceTypeEnum.FIXED_PRICE.getType().equals(electricityPriceEntity.getType())) {
                                if (kpiCodeMap.containsKey("project.electricity.energyUsage.total") && CollUtil.isNotEmpty(list)) {
                                    insertEntity.setProjectElectricityEnergyusagefeeTotal(insertEntity.getProjectElectricityEnergyusageTotal().multiply(electricityPriceEntity.getPrice()));
                                }
                            }

//                    if (kpiCodeMap.containsKey("project.electricity.energyUsageFee.total")) {

                            if (kpiCodeMap.containsKey("project.electricity.subPowerSupplyEnergy.total") && CollUtil.isNotEmpty(list)) {
                                // 计算电量，取值包含energymeterEpimportTotal的
                                if (CollUtil.isEmpty(deviceList)) {
                                    insertEntity.setProjectElectricitySubpowersupplyenergyTotal(BigDecimal.ZERO);
                                } else {
                                    var val = deviceList.stream().filter(j -> dayDeviceObj.containsKey(j.getDeviceId()) && dayDeviceObj.getJSONObject(j.getDeviceId()).containsKey("energymeterEpimportTotal")).map(j -> dayDeviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("energymeterEpimportTotal").multiply(new BigDecimal(j.getComputeTag()))).reduce(BigDecimal.ZERO, BigDecimal::add);
                                    insertEntity.setProjectElectricitySubpowersupplyenergyTotal(val);
                                }
                            }
                            if (kpiCodeMap.containsKey("project.electricity.subHAVCEnergy.total") && CollUtil.isNotEmpty(list)) {
                                // 空调总用电量，取值包含energymeterEpimportTotal的
                                if (CollUtil.isEmpty(deviceList)) {
                                    insertEntity.setProjectElectricitySubhavcenergyTotal(BigDecimal.ZERO);
                                } else {
                                    var val = deviceList.stream().filter(j -> dayDeviceObj.containsKey(j.getDeviceId()) && dayDeviceObj.getJSONObject(j.getDeviceId()).containsKey("energymeterEpimportTotal")).map(j -> dayDeviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("energymeterEpimportTotal").multiply(new BigDecimal(j.getComputeTag()))).reduce(BigDecimal.ZERO, BigDecimal::add);
                                    insertEntity.setProjectElectricitySubhavcenergyTotal(val);
                                }
                            }
                            if (kpiCodeMap.containsKey("project.electricity.subHeatingWaterEnergy.total") && CollUtil.isNotEmpty(list)) {
                                // 热水总用电量，取值包含energymeterEpimportTotal的
                                if (CollUtil.isEmpty(deviceList)) {
                                    insertEntity.setProjectElectricitySubheatingwaterenergyTotal(BigDecimal.ZERO);
                                } else {
                                    var val = deviceList.stream().filter(j -> dayDeviceObj.containsKey(j.getDeviceId()) && dayDeviceObj.getJSONObject(j.getDeviceId()).containsKey("energymeterEpimportTotal")).map(j -> dayDeviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("energymeterEpimportTotal").multiply(new BigDecimal(j.getComputeTag()))).reduce(BigDecimal.ZERO, BigDecimal::add);
                                    insertEntity.setProjectElectricitySubheatingwaterenergyTotal(val);
                                }
                            }
                            if (kpiCodeMap.containsKey("project.electricity.subWaterSupplyEnergy.total") && CollUtil.isNotEmpty(list)) {
                                // 供水总用电量，取值包含energymeterEpimportTotal的
                                if (CollUtil.isEmpty(deviceList)) {
                                    insertEntity.setProjectElectricitySubwatersupplyenergyTotal(BigDecimal.ZERO);
                                } else {
                                    var val = deviceList.stream().filter(j -> dayDeviceObj.containsKey(j.getDeviceId()) && dayDeviceObj.getJSONObject(j.getDeviceId()).containsKey("energymeterEpimportTotal")).map(j -> dayDeviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("energymeterEpimportTotal").multiply(new BigDecimal(j.getComputeTag()))).reduce(BigDecimal.ZERO, BigDecimal::add);
                                    insertEntity.setProjectElectricitySubwatersupplyenergyTotal(val);
                                }
                            }
                            if (kpiCodeMap.containsKey("project.electricity.subElevatorEnergy.total") && CollUtil.isNotEmpty(list)) {
                                // 电梯总用电量，取值包含energymeterEpimportTotal的
                                if (CollUtil.isEmpty(deviceList)) {
                                    insertEntity.setProjectElectricitySubelevatorenergyTotal(BigDecimal.ZERO);
                                } else {
                                    var val = deviceList.stream().filter(j -> dayDeviceObj.containsKey(j.getDeviceId()) && dayDeviceObj.getJSONObject(j.getDeviceId()).containsKey("energymeterEpimportTotal")).map(j -> dayDeviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("energymeterEpimportTotal").multiply(new BigDecimal(j.getComputeTag()))).reduce(BigDecimal.ZERO, BigDecimal::add);
                                    insertEntity.setProjectElectricitySubelevatorenergyTotal(val);
                                }
                            }
                            if (kpiCodeMap.containsKey("project.electricity.subGuestRoomEnergy.total") && CollUtil.isNotEmpty(list)) {
                                // 计算电量，取值包含energymeterEpimportTotal的
                                if (CollUtil.isEmpty(deviceList)) {
                                    insertEntity.setProjectElectricitySubguestroomenergyTotal(BigDecimal.ZERO);
                                } else {
                                    var val = deviceList.stream().filter(j -> dayDeviceObj.containsKey(j.getDeviceId()) && dayDeviceObj.getJSONObject(j.getDeviceId()).containsKey("energymeterEpimportTotal")).map(j -> dayDeviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("energymeterEpimportTotal").multiply(new BigDecimal(j.getComputeTag()))).reduce(BigDecimal.ZERO, BigDecimal::add);
                                    insertEntity.setProjectElectricitySubguestroomenergyTotal(val);
                                }
                            }
                            if (kpiCodeMap.containsKey("project.electricity.subOtherType.total") && CollUtil.isNotEmpty(list)) {
                                // 计算电量，取值包含energymeterEpimportTotal的
                                if (CollUtil.isEmpty(deviceList)) {
                                    insertEntity.setProjectElectricitySubothertypeTotal(BigDecimal.ZERO);
                                } else {
                                    var val = deviceList.stream().filter(j -> dayDeviceObj.containsKey(j.getDeviceId()) && dayDeviceObj.getJSONObject(j.getDeviceId()).containsKey("energymeterEpimportTotal")).map(j -> dayDeviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("energymeterEpimportTotal").multiply(new BigDecimal(j.getComputeTag()))).reduce(BigDecimal.ZERO, BigDecimal::add);
                                    insertEntity.setProjectElectricitySubothertypeTotal(val);
                                }
                            }
                            if (kpiCodeMap.containsKey("project.carbon.electricityUsageCO2.total")) {
                                //20240109 二氧化碳排放根据购网电量计算
                                insertEntity.setProjectCarbonElectricityusageco2Total(insertEntity.getProjectElectricityPccEnergyUsageTotal().multiply(new BigDecimal("0.00042")));
                            }
                            if (kpiCodeMap.containsKey("project.carbon.electricityUsageCoal.total")) {
                                insertEntity.setProjectCarbonElectricityusagecoalTotal(insertEntity.getProjectCarbonElectricityusageco2Total().multiply(new BigDecimal("0.4012")));
                            }
                            if (kpiCodeMap.containsKey("project.carbon.electricityUsageSO2.total")) {
                                insertEntity.setProjectCarbonElectricityusageso2Total(insertEntity.getProjectCarbonElectricityusageco2Total().multiply(new BigDecimal("0.0301")));
                            }
                            if (kpiCodeMap.containsKey("project.carbon.electricityUsageDust.total")) {
                                insertEntity.setProjectCarbonElectricityusagedustTotal(insertEntity.getProjectCarbonElectricityusageco2Total().multiply(new BigDecimal("0.2782")));
                            }
                            if (kpiCodeMap.containsKey("project.carbon.gasUsageCO2.total")) {
                                insertEntity.setProjectCarbonGasusageco2Total(insertEntity.getProjectGasUsageTotal().multiply(new BigDecimal("0.00218")));
                            }
                            if (kpiCodeMap.containsKey("project.carbon.gasUsageCoal.total")) {
                                insertEntity.setProjectCarbonGasusagecoalTotal(insertEntity.getProjectCarbonElectricityusagedustTotal().multiply(new BigDecimal("0.4012")));
                            }
                            if (kpiCodeMap.containsKey("project.carbon.waterUsageCO2.total")) {
                                insertEntity.setProjectCarbonWaterusageco2Total(insertEntity.getProjectWaterUsageTotal().multiply(new BigDecimal("0.00185")));
                            }

                            if (kpiCodeMap.containsKey("project.carbon.waterUsageCoal.total")) {
                                insertEntity.setProjectCarbonWaterusagecoalTotal(insertEntity.getProjectCarbonWaterusageco2Total().multiply(new BigDecimal("0.4012")));
                            }

                            if (kpiCodeMap.containsKey("project.carbon.waterUsageSO2.total")) {
                                insertEntity.setProjectCarbonWaterusageso2Total(insertEntity.getProjectCarbonWaterusageco2Total().multiply(new BigDecimal("0.0301")));
                            }

                            if (kpiCodeMap.containsKey("project.carbon.waterUsageDust.total")) {
                                insertEntity.setProjectCarbonWaterusagedustTotal(insertEntity.getProjectCarbonWaterusageco2Total().multiply(new BigDecimal("0.2782")));
                            }

//                    if (kpiCodeMap.containsKey("project.carbon.totalCO2.total")) {
                            insertEntity.setProjectCarbonTotalco2Total(insertEntity.getProjectCarbonElectricityusageco2Total().add(insertEntity.getProjectCarbonWaterusageco2Total()).add(insertEntity.getProjectCarbonGasusageco2Total()));

//                    if (kpiCodeMap.containsKey("project.carbon.totalCoal.total")) {
                            insertEntity.setProjectCarbonTotalcoalTotal(insertEntity.getProjectCarbonElectricityusagecoalTotal().add(insertEntity.getProjectCarbonWaterusagecoalTotal()).add(insertEntity.getProjectCarbonGasusagecoalTotal()));

                            //                    if (kpiCodeMap.containsKey("project.carbon.totalSO2.total")) {
                            insertEntity.setProjectCarbonTotalso2Total(insertEntity.getProjectCarbonElectricityusageso2Total().add(insertEntity.getProjectCarbonWaterusageso2Total()).add(insertEntity.getProjectCarbonGasusageso2Total()));

                            //                    if (kpiCodeMap.containsKey("project.carbon.totalDust.total")) {
                            insertEntity.setProjectCarbonTotaldustTotal(insertEntity.getProjectCarbonElectricityusagedustTotal().add(insertEntity.getProjectCarbonWaterusagedustTotal()).add(insertEntity.getProjectCarbonGasusagedustTotal()));
                            if (kpiCodeMap.containsKey("project.electricity.pccEnergyUsage.total") && CollUtil.isNotEmpty(list)) {
                                //购网电量,根据电耗分类配置进行统计
                                if (CollUtil.isEmpty(deviceList)) {
                                    insertEntity.setProjectElectricityPccEnergyUsageTotal(BigDecimal.ZERO);
                                } else {
                                    var val = deviceList.stream().filter(j -> dayDeviceObj.containsKey(j.getDeviceId()) && dayDeviceObj.getJSONObject(j.getDeviceId()).containsKey("energymeterEpimportTotal"))
                                            .map(j -> dayDeviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("energymeterEpimportTotal").multiply(new BigDecimal(j.getComputeTag())))
                                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                                    var val1 = deviceList.stream().filter(j -> dayDeviceObj.containsKey(j.getDeviceId()) && dayDeviceObj.getJSONObject(j.getDeviceId()).containsKey("gscnEpimportTotal"))
                                            .map(j -> dayDeviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("gscnEpimportTotal").multiply(new BigDecimal(j.getComputeTag())))
                                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                                    var val2 = deviceList.stream().filter(j -> dayDeviceObj.containsKey(j.getDeviceId()) && dayDeviceObj.getJSONObject(j.getDeviceId()).containsKey("znbEpimportTotal"))
                                            .map(j -> dayDeviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("znbEpimportTotal").multiply(new BigDecimal(j.getComputeTag())))
                                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                                    insertEntity.setProjectElectricityPccEnergyUsageTotal(val.add(val1).add(val2));
                                }
                            }
                            if (null != electricityPriceEntity && ElectricityPriceTypeEnum.TOU.getType().equals(electricityPriceEntity.getType())) {
                                if (kpiCodeMap.containsKey("project.electricity.pccEnergyUsage.tip") && CollUtil.isNotEmpty(list)) {
                                    // 购网尖电量(根据尖时段配置，由小时表汇总得到。。)
                                    BigDecimal total = list.stream().filter(it -> condition(it, tip)).map(ProjectStaSubitemHourEntity::getProjectElectricityPccEnergyUsageTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
                                    insertEntity.setProjectElectricityPccEnergyUsageTip(total);

                                }
                                if (kpiCodeMap.containsKey("project.electricity.pccEnergyUsage.peak") && CollUtil.isNotEmpty(list)) {
                                    // 购网峰电量(根据峰时段配置，由小时表汇总得到。)
                                    insertEntity.setProjectElectricityPccEnergyUsagePeak(list.stream().filter(it -> condition(it, peak)).map(ProjectStaSubitemHourEntity::getProjectElectricityPccEnergyUsageTotal).reduce(BigDecimal.ZERO, BigDecimal::add));
                                }
                                if (kpiCodeMap.containsKey("project.electricity.pccEnergyUsage.valley") && CollUtil.isNotEmpty(list)) {
                                    // 购网谷电量(根据谷时段配置，由小时表汇总得到。)
                                    insertEntity.setProjectElectricityPccEnergyUsageValley(list.stream().filter(it -> condition(it, valley)).map(ProjectStaSubitemHourEntity::getProjectElectricityPccEnergyUsageTotal).reduce(BigDecimal.ZERO, BigDecimal::add));
                                }
                                if (kpiCodeMap.containsKey("project.electricity.pccEnergyUsage.flat") && CollUtil.isNotEmpty(list)) {
                                    // 购网平电量(根据平时段配置，由小时表汇总得到)
                                    insertEntity.setProjectElectricityPccEnergyUsageFlat(list.stream().filter(it -> condition(it, flat)).map(ProjectStaSubitemHourEntity::getProjectElectricityPccEnergyUsageTotal).reduce(BigDecimal.ZERO, BigDecimal::add));
                                }
                                if (kpiCodeMap.containsKey("project.electricity.pccEnergyUsageFee.total") && CollUtil.isNotEmpty(list)) {
                                    // 购网电费(由购网尖峰谷平电量及电价得到。) project.electricity.pccEnergyUsageFee.total
                                    insertEntity.setProjectElectricityPccEnergyUsageFeeTotal(
                                            insertEntity.getProjectElectricityPccEnergyUsageTip().multiply(price(tip))
                                                    .add(insertEntity.getProjectElectricityPccEnergyUsagePeak().multiply(price(peak)))
                                                    .add(insertEntity.getProjectElectricityPccEnergyUsageValley().multiply(price(valley)))
                                                    .add(insertEntity.getProjectElectricityPccEnergyUsageFlat().multiply(price(flat))));
                                }
                            } else if (null != electricityPriceEntity && ElectricityPriceTypeEnum.FIXED_PRICE.getType().equals(electricityPriceEntity.getType())) {
                                if (kpiCodeMap.containsKey("project.electricity.pccEnergyUsageFee.total") && CollUtil.isNotEmpty(list)) {
                                    // 购网电费(由购网尖峰谷平电量及电价得到。) project.electricity.pccEnergyUsageFee.total
                                    insertEntity.setProjectElectricityPccEnergyUsageFeeTotal(
                                            insertEntity.getProjectElectricityPccEnergyUsageTotal().multiply(electricityPriceEntity.getPrice()));
                                }
                            }
                            if (kpiCodeMap.containsKey("project.electricity.pccEnergyProduction.total") && CollUtil.isNotEmpty(list)) {
                                //上网电量(由小时表汇总得到) project.electricity.pccEnergyProduction.total
                                if (CollUtil.isEmpty(deviceList)) {
                                    insertEntity.setProjectElectricityPccEnergyProductionTotal(BigDecimal.ZERO);
                                }
                                var val = deviceList.stream().filter(j -> dayDeviceObj.containsKey(j.getDeviceId()) && dayDeviceObj.getJSONObject(j.getDeviceId()).containsKey("energymeterEpexportTotal"))
                                        .map(j -> dayDeviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("energymeterEpexportTotal").multiply(new BigDecimal(j.getComputeTag())))
                                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                                var val1 = deviceList.stream().filter(j -> dayDeviceObj.containsKey(j.getDeviceId()) && dayDeviceObj.getJSONObject(j.getDeviceId()).containsKey("gscnEpexportTotal"))
                                        .map(j -> dayDeviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("gscnEpexportTotal").multiply(new BigDecimal(j.getComputeTag())))
                                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                                var val2 = deviceList.stream().filter(j -> dayDeviceObj.containsKey(j.getDeviceId()) && dayDeviceObj.getJSONObject(j.getDeviceId()).containsKey("znbEpexportTotal"))
                                        .map(j -> dayDeviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("znbEpexportTotal").multiply(new BigDecimal(j.getComputeTag())))
                                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                                insertEntity.setProjectElectricityPccEnergyProductionTotal(val.add(val1).add(val2));
                            }
                            if (kpiCodeMap.containsKey("project.electricity.pccEnergyProductionFee.total") && CollUtil.isNotEmpty(list)) {
                                // 网收益(由上网电量*上网电价)project.electricity.pccEnergyProductionFee.total
                                insertEntity.setProjectElectricityPccEnergyProductionFeeTotal(insertEntity.getProjectElectricityPccEnergyProductionTotal().multiply(cnfPvPrice));
                            }
                            if (kpiCodeMap.containsKey("project.electricity.pvEnergyProduction.total") && CollUtil.isNotEmpty(list)) {
                                //光伏发电量(由小时表汇总得到) project.electricity.pvEnergyProduction.total
                                if (CollUtil.isEmpty(deviceList)) {
                                    insertEntity.setProjectElectricityPvEnergyProductionTotal(BigDecimal.ZERO);
                                }
                                var val = deviceList.stream().filter(j -> dayDeviceObj.containsKey(j.getDeviceId()) && dayDeviceObj.getJSONObject(j.getDeviceId()).containsKey("energymeterEpexportTotal"))
                                        .map(j -> dayDeviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("energymeterEpexportTotal").multiply(new BigDecimal(j.getComputeTag())))
                                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                                var val1 = deviceList.stream().filter(j -> dayDeviceObj.containsKey(j.getDeviceId()) && dayDeviceObj.getJSONObject(j.getDeviceId()).containsKey("gscnEpexportTotal"))
                                        .map(j -> dayDeviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("gscnEpexportTotal").multiply(new BigDecimal(j.getComputeTag())))
                                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                                var val2 = deviceList.stream().filter(j -> dayDeviceObj.containsKey(j.getDeviceId()) && dayDeviceObj.getJSONObject(j.getDeviceId()).containsKey("znbEpexportTotal"))
                                        .map(j -> dayDeviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("znbEpexportTotal").multiply(new BigDecimal(j.getComputeTag())))
                                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                                insertEntity.setProjectElectricityPvEnergyProductionTotal(val.add(val1).add(val2));
                            }
                            if (kpiCodeMap.containsKey("project.electricity.pvEnergyProductionGrid.total") && CollUtil.isNotEmpty(list)) {
                                // 上网电量(同关口上网电量指标) project.electricity.pvEnergyProductionGrid.total
                                insertEntity.setProjectElectricityPvEnergyProductionGridTotal(insertEntity.getProjectElectricityPccEnergyProductionTotal());
                            }
                            if (kpiCodeMap.containsKey("project.electricity.pvEnergyProductionStorage.total") && CollUtil.isNotEmpty(list)) {
                                //先储后用(由储光电量汇总) project.electricity.pvEnergyProductionStorage.total
                                insertEntity.setProjectElectricityPvEnergyProductionStorageTotal(list.stream().map(ProjectStaSubitemHourEntity::getProjectElectricityStorageEnergyUsagePvTotal).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add));
                            }
                            if (kpiCodeMap.containsKey("project.electricity.pvEnergyProductionLoad.total") && CollUtil.isNotEmpty(list)) {
                                // 直接使用(光伏总发电-上网-先储后用) project.electricity.pvEnergyProductionLoad.total
                                insertEntity.setProjectElectricityPvEnergyProductionLoadTotal(insertEntity.getProjectElectricityPvEnergyProductionTotal().subtract(insertEntity.getProjectElectricityPvEnergyProductionGridTotal()).subtract(insertEntity.getProjectElectricityPvEnergyProductionStorageTotal()));
                            }
                            if (kpiCodeMap.containsKey("project.electricity.pvEnergyProductionGridFee.total") && CollUtil.isNotEmpty(list)) {
                                //上网收益（同关口上网收益） project.electricity.pvEnergyProductionGridFee.total
                                insertEntity.setProjectElectricityPvEnergyProductionGridFeeTotal(insertEntity.getProjectElectricityPccEnergyProductionFeeTotal());
                            }
                            if (null != electricityPriceEntity && ElectricityPriceTypeEnum.TOU.getType().equals(electricityPriceEntity.getType())) {
                                if (kpiCodeMap.containsKey("project.electricity.pvEnergyProductionUsageFee.total") && CollUtil.isNotEmpty(list)) {
                                    // 消纳收益(sum(（小时发电量-小时上网电量）* 当时单价)) project.electricity.pvEnergyProductionUsageFee.total
                                    insertEntity.setProjectElectricityPvEnergyProductionUsageFeeTotal(
                                            list.stream().map(it ->
                                                    it.getProjectElectricityPvEnergyProductionTotal().subtract(it.getProjectElectricityPccEnergyProductionTotal())
                                                            .multiply(electricityPrice(it, timePeriodList))
                                            ).reduce(BigDecimal.ZERO, BigDecimal::add)
                                    );
                                }
                            } else if (null != electricityPriceEntity && ElectricityPriceTypeEnum.FIXED_PRICE.getType().equals(electricityPriceEntity.getType())) {
                                if (kpiCodeMap.containsKey("project.electricity.pvEnergyProductionUsageFee.total") && CollUtil.isNotEmpty(list)) {
                                    // 消纳收益(sum(（小时发电量-小时上网电量）* 当时单价)) project.electricity.pvEnergyProductionUsageFee.total
                                    insertEntity.setProjectElectricityPvEnergyProductionUsageFeeTotal(
                                            list.stream().map(it ->
                                                    it.getProjectElectricityPvEnergyProductionTotal().subtract(it.getProjectElectricityPccEnergyProductionTotal())
                                                            .multiply(electricityPriceEntity.getPrice())
                                            ).reduce(BigDecimal.ZERO, BigDecimal::add)
                                    );
                                }
                            }
                            if (kpiCodeMap.containsKey("project.electricity.pvEnergyProductionFee.total") && CollUtil.isNotEmpty(list)) {
                                // 光伏收益(上网收益+消纳)project.electricity.pvEnergyProductionFee.total
                                insertEntity.setProjectElectricityPvEnergyProductionFeeTotal(
                                        insertEntity.getProjectElectricityPvEnergyProductionGridFeeTotal().add(insertEntity.getProjectElectricityPvEnergyProductionUsageFeeTotal())
                                );
                            }
                            if (kpiCodeMap.containsKey("project.electricity.storageEnergyUsage.total") && CollUtil.isNotEmpty(list)) {
                                //储充电量(由小时表汇总得到) project.electricity.storageEnergyUsage.total
                                insertEntity.setProjectElectricityStorageEnergyUsageTotal(list.stream().map(ProjectStaSubitemHourEntity::getProjectElectricityStorageEnergyUsageTotal).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add));
                                if (CollUtil.isEmpty(deviceList)) {
                                    insertEntity.setProjectElectricityStorageEnergyUsageTotal(BigDecimal.ZERO);
                                } else {
                                    var val = deviceList.stream().filter(j -> dayDeviceObj.containsKey(j.getDeviceId()) && dayDeviceObj.getJSONObject(j.getDeviceId()).containsKey("energymeterEpimportTotal"))
                                            .map(j -> dayDeviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("energymeterEpimportTotal").multiply(new BigDecimal(j.getComputeTag())))
                                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                                    var val1 = deviceList.stream().filter(j -> dayDeviceObj.containsKey(j.getDeviceId()) && dayDeviceObj.getJSONObject(j.getDeviceId()).containsKey("gscnEpimportTotal"))
                                            .map(j -> dayDeviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("gscnEpimportTotal").multiply(new BigDecimal(j.getComputeTag())))
                                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                                    var val2 = deviceList.stream().filter(j -> dayDeviceObj.containsKey(j.getDeviceId()) && dayDeviceObj.getJSONObject(j.getDeviceId()).containsKey("znbEpimportTotal"))
                                            .map(j -> dayDeviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("znbEpimportTotal").multiply(new BigDecimal(j.getComputeTag())))
                                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                                    insertEntity.setProjectElectricityStorageEnergyUsageTotal(val.add(val1).add(val2));
                                }
                            }
                            if (null != electricityPriceEntity && ElectricityPriceTypeEnum.TOU.getType().equals(electricityPriceEntity.getType())) {
                                if (kpiCodeMap.containsKey("project.electricity.storageEnergyUsage.tip") && CollUtil.isNotEmpty(list)) {
                                    //尖充电量(根据尖时段配置，由小时表汇总得到。) project.electricity.storageEnergyUsage.tip
                                    insertEntity.setProjectElectricityStorageEnergyUsageTip(list.stream()
                                            .filter(it -> condition(it, tip))
                                            .map(ProjectStaSubitemHourEntity::getProjectElectricityStorageEnergyUsageTotal)
                                            .reduce(BigDecimal.ZERO, BigDecimal::add));
                                }
                                if (kpiCodeMap.containsKey("project.electricity.storageEnergyUsage.peak") && CollUtil.isNotEmpty(list)) {
                                    //峰充电量(根据峰时段配置，由小时表汇总得到) project.electricity.storageEnergyUsage.peak
                                    insertEntity.setProjectElectricityStorageEnergyUsagePeak(list.stream()
                                            .filter(it -> condition(it, peak))
                                            .map(ProjectStaSubitemHourEntity::getProjectElectricityStorageEnergyUsageTotal)
                                            .reduce(BigDecimal.ZERO, BigDecimal::add));
                                }
                                if (kpiCodeMap.containsKey("project.electricity.storageEnergyUsage.valley") && CollUtil.isNotEmpty(list)) {
                                    //谷充电量(根据谷时段配置，由小时表汇总得到) project.electricity.storageEnergyUsage.valley
                                    insertEntity.setProjectElectricityStorageEnergyUsageValley(list.stream()
                                            .filter(it -> condition(it, valley))
                                            .map(ProjectStaSubitemHourEntity::getProjectElectricityStorageEnergyUsageTotal)
                                            .reduce(BigDecimal.ZERO, BigDecimal::add));
                                }
                                if (kpiCodeMap.containsKey("project.electricity.storageEnergyUsage.flat") && CollUtil.isNotEmpty(list)) {
                                    //平充电量（根据平时段配置，由小时表汇总得到） project.electricity.storageEnergyUsage.flat
                                    insertEntity.setProjectElectricityStorageEnergyUsageFlat(list.stream()
                                            .filter(it -> condition(it, flat))
                                            .map(ProjectStaSubitemHourEntity::getProjectElectricityStorageEnergyUsageTotal)
                                            .reduce(BigDecimal.ZERO, BigDecimal::add));
                                }
                            }
                            if (kpiCodeMap.containsKey("project.electricity.storageEnergyUsagePv.total") && CollUtil.isNotEmpty(list)) {
                                // 储光电量（由小时表汇总得到） project.electricity.storageEnergyUsagePv.total
                                if (BigDecimal.ZERO.compareTo(insertEntity.getProjectElectricityPccEnergyUsageTotal()) == 0) {
                                    insertEntity.setProjectElectricityStorageEnergyUsagePvTotal(null);
                                }
                                BigDecimal multiply = insertEntity.getProjectElectricityStorageEnergyUsageTotal().subtract(insertEntity.getProjectElectricityPccEnergyUsageTotal()).subtract(insertEntity.getProjectElectricityStorageEnergyProductionTotal());
                                if (BigDecimal.ZERO.compareTo(multiply) > 0) {
                                    insertEntity.setProjectElectricityStorageEnergyUsagePvTotal(BigDecimal.ZERO);
                                } else {
                                    insertEntity.setProjectElectricityStorageEnergyUsagePvTotal(multiply);
                                }
                            }
                            if (kpiCodeMap.containsKey("project.electricity.storageEnergyUsageGrid.total") && CollUtil.isNotEmpty(list)) {
                                // 储市电量（由小时表汇总得到） project.electricity.storageEnergyUsageGrid.total
                                //储市电量,由储充电量-储光电量，不可计算时，指标为空。
                                BigDecimal decimal1 = insertEntity.getProjectElectricityStorageEnergyUsageTotal();
                                BigDecimal decimal2 = insertEntity.getProjectElectricityStorageEnergyUsagePvTotal();
                                if (Objects.isNull(decimal2) || Objects.isNull(decimal1)) {
                                    insertEntity.setProjectElectricityStorageEnergyUsageGridTotal(null);
                                } else {
                                    BigDecimal bigDecimal = decimal1.subtract(decimal2);
                                    if (BigDecimal.ZERO.compareTo(bigDecimal) > 0) {
                                        insertEntity.setProjectElectricityStorageEnergyUsageGridTotal(BigDecimal.ZERO);
                                    } else {
                                        insertEntity.setProjectElectricityStorageEnergyUsageGridTotal(bigDecimal);
                                    }
                                }
                            }
                            if (kpiCodeMap.containsKey("project.electricity.storageEnergyProduction.total") && CollUtil.isNotEmpty(list)) {
                                // 储放电量（由小时表汇总得到） project.electricity.storageEnergyProduction.total
                                if (CollUtil.isEmpty(deviceList)) {
                                    insertEntity.setProjectElectricityStorageEnergyProductionTotal(BigDecimal.ZERO);
                                } else {
                                    var val = deviceList.stream().filter(j -> dayDeviceObj.containsKey(j.getDeviceId()) && dayDeviceObj.getJSONObject(j.getDeviceId()).containsKey("energymeterEpexportTotal"))
                                            .map(j -> dayDeviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("energymeterEpexportTotal").multiply(new BigDecimal(j.getComputeTag())))
                                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                                    var val1 = deviceList.stream().filter(j -> dayDeviceObj.containsKey(j.getDeviceId()) && dayDeviceObj.getJSONObject(j.getDeviceId()).containsKey("gscnEpexportTotal"))
                                            .map(j -> dayDeviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("gscnEpexportTotal").multiply(new BigDecimal(j.getComputeTag())))
                                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                                    var val2 = deviceList.stream().filter(j -> dayDeviceObj.containsKey(j.getDeviceId()) && dayDeviceObj.getJSONObject(j.getDeviceId()).containsKey("znbEpexportTotal"))
                                            .map(j -> dayDeviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("znbEpexportTotal").multiply(new BigDecimal(j.getComputeTag())))
                                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                                    insertEntity.setProjectElectricityStorageEnergyProductionTotal(val.add(val1).add(val2));
                                }
                            }
                            if (null != electricityPriceEntity && ElectricityPriceTypeEnum.TOU.getType().equals(electricityPriceEntity.getType())) {
                                if (kpiCodeMap.containsKey("project.electricity.storageEnergyProduction.tip") && CollUtil.isNotEmpty(list)) {
                                    //尖放电量(根据尖时段配置，由小时表汇总得到。) project.electricity.storageEnergyProduction.tip
                                    insertEntity.setProjectElectricityStorageEnergyProductionTip(list.stream()
                                            .filter(it -> condition(it, tip))
                                            .map(ProjectStaSubitemHourEntity::getProjectElectricityStorageEnergyProductionTotal)
                                            .reduce(BigDecimal.ZERO, BigDecimal::add));
                                }
                                if (kpiCodeMap.containsKey("project.electricity.storageEnergyProduction.peak") && CollUtil.isNotEmpty(list)) {
                                    //峰放电量(根据峰时段配置，由小时表汇总得到) project.electricity.storageEnergyProduction.peak
                                    insertEntity.setProjectElectricityStorageEnergyProductionPeak(list.stream()
                                            .filter(it -> condition(it, peak))
                                            .map(ProjectStaSubitemHourEntity::getProjectElectricityStorageEnergyProductionTotal)
                                            .reduce(BigDecimal.ZERO, BigDecimal::add));
                                }
                                if (kpiCodeMap.containsKey("project.electricity.storageEnergyProduction.valley") && CollUtil.isNotEmpty(list)) {
                                    //谷放电量(根据谷时段配置，由小时表汇总得到) project.electricity.storageEnergyProduction.valley
                                    insertEntity.setProjectElectricityStorageEnergyProductionValley(list.stream()
                                            .filter(it -> condition(it, valley))
                                            .map(ProjectStaSubitemHourEntity::getProjectElectricityStorageEnergyProductionTotal)
                                            .reduce(BigDecimal.ZERO, BigDecimal::add));
                                }
                                if (kpiCodeMap.containsKey("project.electricity.storageEnergyProduction.flat") && CollUtil.isNotEmpty(list)) {
                                    //平放电量(根据平时段配置，由小时表汇总得到) project.electricity.storageEnergyProduction.flat
                                    insertEntity.setProjectElectricityStorageEnergyProductionFlat(list.stream()
                                            .filter(it -> condition(it, flat))
                                            .map(ProjectStaSubitemHourEntity::getProjectElectricityStorageEnergyProductionTotal)
                                            .reduce(BigDecimal.ZERO, BigDecimal::add));
                                }
                                if (kpiCodeMap.containsKey("project.electricity.storageEnergyNetFee.total") && CollUtil.isNotEmpty(list)) {
                                    //储能净收益(sum(放电时段单价*放电时段电量）-sum(充电时段单价*充电时段电量))
                                    BigDecimal decimal1 = list.stream().map(it -> it.getProjectElectricityStorageEnergyUsageTotal().multiply(electricityPrice(it, timePeriodList)))
                                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                                    BigDecimal decimal2 = list.stream().map(it -> it.getProjectElectricityStorageEnergyProductionTotal().multiply(electricityPrice(it, timePeriodList)))
                                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                                    insertEntity.setProjectElectricityStorageEnergyNetFeeTotal(decimal2.subtract(decimal1));
                                }
                            } else if (null != electricityPriceEntity && ElectricityPriceTypeEnum.FIXED_PRICE.getType().equals(electricityPriceEntity.getType())) {
                                if (kpiCodeMap.containsKey("project.electricity.storageEnergyNetFee.total") && CollUtil.isNotEmpty(list)) {
                                    //储能净收益(sum(放电时段单价*放电时段电量）-sum(充电时段单价*充电时段电量))
                                    BigDecimal decimal1 = list.stream().map(it -> it.getProjectElectricityStorageEnergyUsageTotal().multiply(electricityPriceEntity.getPrice()))
                                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                                    BigDecimal decimal2 = list.stream().map(it -> it.getProjectElectricityStorageEnergyProductionTotal().multiply(electricityPriceEntity.getPrice()))
                                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                                    insertEntity.setProjectElectricityStorageEnergyNetFeeTotal(decimal2.subtract(decimal1));
                                }
                            }
                            if (kpiCodeMap.containsKey("project.electricity.subChargeEnergy.total") && CollUtil.isNotEmpty(list)) {
                                // 充电桩总用电(由小时表汇总得到) project.electricity.subChargeEnergy.total
                                if (CollUtil.isEmpty(deviceList)) {
                                    insertEntity.setProjectElectricitySubChargeEnergyTotal(BigDecimal.ZERO);
                                }
                                var val = deviceList.stream().filter(j -> dayDeviceObj.containsKey(j.getDeviceId()) && dayDeviceObj.getJSONObject(j.getDeviceId()).containsKey("energymeterEpimportTotal"))
                                        .map(j -> dayDeviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("energymeterEpimportTotal").multiply(new BigDecimal(j.getComputeTag())))
                                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                                var val1 = deviceList.stream().filter(j -> dayDeviceObj.containsKey(j.getDeviceId()) && dayDeviceObj.getJSONObject(j.getDeviceId()).containsKey("gscnEpimportTotal"))
                                        .map(j -> dayDeviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("gscnEpimportTotal").multiply(new BigDecimal(j.getComputeTag())))
                                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                                var val2 = deviceList.stream().filter(j -> dayDeviceObj.containsKey(j.getDeviceId()) && dayDeviceObj.getJSONObject(j.getDeviceId()).containsKey("znbEpimportTotal"))
                                        .map(j -> dayDeviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("znbEpimportTotal").multiply(new BigDecimal(j.getComputeTag())))
                                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                                insertEntity.setProjectElectricitySubChargeEnergyTotal(val.add(val1).add(val2));
                            }
                            if (null != electricityPriceEntity && ElectricityPriceTypeEnum.TOU.getType().equals(electricityPriceEntity.getType())) {
                                if (kpiCodeMap.containsKey("project.electricity.subChargeEnergyChargeFee.total") && CollUtil.isNotEmpty(list)) {
                                    // 电度费 由充电电量尖峰谷平电量及电价计算得到 没有见峰谷平指标
                                    insertEntity.setProjectElectricitySubChargeEnergyChargeFeeTotal(getDDFee(list, timePeriodList));
                                }
                            } else if (null != electricityPriceEntity && ElectricityPriceTypeEnum.FIXED_PRICE.getType().equals(electricityPriceEntity.getType())) {
                                if (kpiCodeMap.containsKey("project.electricity.subChargeEnergyChargeFee.total") && CollUtil.isNotEmpty(list)) {
                                    // 电度费 由充电电量尖峰谷平电量及电价计算得到 没有见峰谷平指标
                                    insertEntity.setProjectElectricitySubChargeEnergyChargeFeeTotal(
                                            list.stream().map(ProjectStaSubitemHourEntity::getProjectElectricitySubChargeEnergyTotal).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add).multiply(electricityPriceEntity.getPrice())
                                    );
                                }
                            }
                            if (kpiCodeMap.containsKey("project.electricity.subChargeEnergyServiceFee.total") && CollUtil.isNotEmpty(list)) {
                                // 服务费(总电量*服务费单价) project.electricity.subChargeEnergyServiceFee.total
                                insertEntity.setProjectElectricitySubChargeEnergyServiceFeeTotal(insertEntity.getProjectElectricitySubChargeEnergyTotal().multiply(chargeStationPrice));
                            }
                            if (kpiCodeMap.containsKey("project.electricity.subChargeEnergyFee.total") && CollUtil.isNotEmpty(list)) {
                                // 充电总费用(电度费+服务费；计费模式及服务费配置到数据库中。) project.electricity.subChargeEnergyFee.total
                                insertEntity.setProjectElectricitySubChargeEnergyFeeTotal(insertEntity.getProjectElectricitySubChargeEnergyChargeFeeTotal()
                                        .add(insertEntity.getProjectElectricitySubChargeEnergyServiceFeeTotal()));
                            }
                            if (kpiCodeMap.containsKey("project.electricity.subLightingEnergy.total") && CollUtil.isNotEmpty(list)) {
                                //照明用电电量 project.electricity.subLightingEnergy.total
                                //照明总用电量,根据电耗分类配置进行统计
                                if (CollUtil.isEmpty(deviceList)) {
                                    insertEntity.setProjectElectricitySubLightingEnergyTotal(BigDecimal.ZERO);
                                }
                                var val = deviceList.stream().filter(j -> dayDeviceObj.containsKey(j.getDeviceId()) && dayDeviceObj.getJSONObject(j.getDeviceId()).containsKey("energymeterEpimportTotal"))
                                        .map(j -> dayDeviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("energymeterEpimportTotal").multiply(new BigDecimal(j.getComputeTag())))
                                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                                var val1 = deviceList.stream().filter(j -> dayDeviceObj.containsKey(j.getDeviceId()) && dayDeviceObj.getJSONObject(j.getDeviceId()).containsKey("gscnEpimportTotal"))
                                        .map(j -> dayDeviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("gscnEpimportTotal").multiply(new BigDecimal(j.getComputeTag())))
                                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                                var val2 = deviceList.stream().filter(j -> dayDeviceObj.containsKey(j.getDeviceId()) && dayDeviceObj.getJSONObject(j.getDeviceId()).containsKey("znbEpimportTotal"))
                                        .map(j -> dayDeviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("znbEpimportTotal").multiply(new BigDecimal(j.getComputeTag())))
                                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                                insertEntity.setProjectElectricitySubLightingEnergyTotal(val.add(val1).add(val2));
                            }
                            if (kpiCodeMap.containsKey("project.electricity.subSocketEnergy.total") && CollUtil.isNotEmpty(list)) {
                                //插座用电电量 project.electricity.subSocketEnergy.total
                                if (CollUtil.isEmpty(deviceList)) {
                                    insertEntity.setProjectElectricitySubSocketEnergyTotal(BigDecimal.ZERO);
                                }
                                var val = deviceList.stream().filter(j -> dayDeviceObj.containsKey(j.getDeviceId()) && dayDeviceObj.getJSONObject(j.getDeviceId()).containsKey("energymeterEpimportTotal"))
                                        .map(j -> dayDeviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("energymeterEpimportTotal").multiply(new BigDecimal(j.getComputeTag())))
                                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                                var val1 = deviceList.stream().filter(j -> dayDeviceObj.containsKey(j.getDeviceId()) && dayDeviceObj.getJSONObject(j.getDeviceId()).containsKey("gscnEpimportTotal"))
                                        .map(j -> dayDeviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("gscnEpimportTotal").multiply(new BigDecimal(j.getComputeTag())))
                                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                                var val2 = deviceList.stream().filter(j -> dayDeviceObj.containsKey(j.getDeviceId()) && dayDeviceObj.getJSONObject(j.getDeviceId()).containsKey("znbEpimportTotal"))
                                        .map(j -> dayDeviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("znbEpimportTotal").multiply(new BigDecimal(j.getComputeTag())))
                                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                                insertEntity.setProjectElectricitySubSocketEnergyTotal(val.add(val1).add(val2));
                            }
                            if (kpiCodeMap.containsKey("project.carbon.pvReductionCO2.total") && CollUtil.isNotEmpty(list)) {
                                // CO2(CO2减排量=光伏发电量 * CO2排放因子 排放因子同用电的排放因子。 标准煤、SO2、粉尘根据CO2*系数得到。系数同之前相同。)
                                insertEntity.setProjectCarbonPvReductionCO2Total(insertEntity.getProjectElectricityPvEnergyProductionTotal().multiply(new BigDecimal("0.00042")));
                            }
                            if (kpiCodeMap.containsKey("project.carbon.pvReductionCoal.total") && CollUtil.isNotEmpty(list)) {
                                // CO2(CO2减排量=光伏发电量 * CO2排放因子 排放因子同用电的排放因子。 标准煤、SO2、粉尘根据CO2*系数得到。系数同之前相同。)
                                insertEntity.setProjectCarbonPvReductionCoalTotal(insertEntity.getProjectCarbonPvReductionCO2Total().multiply(new BigDecimal("0.4012")));
                            }
                            if (kpiCodeMap.containsKey("project.carbon.pvReductionSO2.total") && CollUtil.isNotEmpty(list)) {
                                // CO2(CO2减排量=光伏发电量 * CO2排放因子 排放因子同用电的排放因子。 标准煤、SO2、粉尘根据CO2*系数得到。系数同之前相同。)
                                insertEntity.setProjectCarbonPvReductionSO2Total(insertEntity.getProjectCarbonPvReductionCO2Total().multiply(new BigDecimal("0.0301")));
                            }
                            if (kpiCodeMap.containsKey("project.carbon.pvReductionDust.total") && CollUtil.isNotEmpty(list)) {
                                // CO2(CO2减排量=光伏发电量 * CO2排放因子 排放因子同用电的排放因子。 标准煤、SO2、粉尘根据CO2*系数得到。系数同之前相同。)
                                insertEntity.setProjectCarbonPvReductionDustTotal(insertEntity.getProjectCarbonPvReductionCO2Total().multiply(new BigDecimal("0.2782")));
                            }
                        }

                        //需要计算的指标
                        if (null != electricityPriceEntity && ElectricityPriceTypeEnum.TOU.getType().equals(electricityPriceEntity.getType())) {
                            if (totalKpi.contains("project.electricity.energyUsage.total") && CollUtil.isNotEmpty(list)) {
                                insertEntity.setProjectElectricityEnergyusagefeeTotal(insertEntity.getProjectElectricityEnergyusageFlat().multiply(price(flat)).add(insertEntity.getProjectElectricityEnergyusageTip().multiply(price(tip)).add(insertEntity.getProjectElectricityEnergyusageValley().multiply(price(valley))).add(insertEntity.getProjectElectricityEnergyusagePeak().multiply(price(peak)))));
                            }
                        } else if (null != electricityPriceEntity && ElectricityPriceTypeEnum.FIXED_PRICE.getType().equals(electricityPriceEntity.getType())) {
                            if (totalKpi.contains("project.electricity.energyUsage.total") && CollUtil.isNotEmpty(list)) {
                                insertEntity.setProjectElectricityEnergyusagefeeTotal(insertEntity.getProjectElectricityEnergyusageTotal().multiply(electricityPriceEntity.getPrice()));
                            }
                        }
                        if (totalKpi.contains("project.carbon.electricityUsageCO2.total")) {
                            //20240109 二氧化碳排放根据购网电量计算
                            insertEntity.setProjectCarbonElectricityusageco2Total(insertEntity.getProjectElectricityPccEnergyUsageTotal().multiply(new BigDecimal("0.00042")));
                        }
                        if (totalKpi.contains("project.carbon.electricityUsageCoal.total")) {
                            insertEntity.setProjectCarbonElectricityusagecoalTotal(insertEntity.getProjectCarbonElectricityusageco2Total().multiply(new BigDecimal("0.4012")));
                        }
                        if (totalKpi.contains("project.carbon.electricityUsageSO2.total")) {
                            insertEntity.setProjectCarbonElectricityusageso2Total(insertEntity.getProjectCarbonElectricityusageco2Total().multiply(new BigDecimal("0.0301")));
                        }
                        if (totalKpi.contains("project.carbon.electricityUsageDust.total")) {
                            insertEntity.setProjectCarbonElectricityusagedustTotal(insertEntity.getProjectCarbonElectricityusageco2Total().multiply(new BigDecimal("0.2782")));
                        }
                        if (totalKpi.contains("project.carbon.gasUsageCO2.total")) {
                            insertEntity.setProjectCarbonGasusageco2Total(insertEntity.getProjectGasUsageTotal().multiply(new BigDecimal("0.00218")));
                        }
                        if (totalKpi.contains("project.carbon.gasUsageCoal.total")) {
                            insertEntity.setProjectCarbonGasusagecoalTotal(insertEntity.getProjectCarbonElectricityusagedustTotal().multiply(new BigDecimal("0.4012")));
                        }
                        if (totalKpi.contains("project.carbon.waterUsageCO2.total")) {
                            insertEntity.setProjectCarbonWaterusageco2Total(insertEntity.getProjectWaterUsageTotal().multiply(new BigDecimal("0.00185")));
                        }
                        if (totalKpi.contains("project.carbon.waterUsageCoal.total")) {
                            insertEntity.setProjectCarbonWaterusagecoalTotal(insertEntity.getProjectCarbonWaterusageco2Total().multiply(new BigDecimal("0.4012")));
                        }
                        if (totalKpi.contains("project.carbon.waterUsageSO2.total")) {
                            insertEntity.setProjectCarbonWaterusageso2Total(insertEntity.getProjectCarbonWaterusageco2Total().multiply(new BigDecimal("0.0301")));
                        }
                        if (totalKpi.contains("project.carbon.waterUsageDust.total")) {
                            insertEntity.setProjectCarbonWaterusagedustTotal(insertEntity.getProjectCarbonWaterusageco2Total().multiply(new BigDecimal("0.2782")));
                        }
                        if (null != electricityPriceEntity && ElectricityPriceTypeEnum.TOU.getType().equals(electricityPriceEntity.getType())) {
                            if (totalKpi.contains("project.electricity.pccEnergyUsageFee.total") && CollUtil.isNotEmpty(list)) {
                                // 购网电费(由购网尖峰谷平电量及电价得到。) project.electricity.pccEnergyUsageFee.total
                                insertEntity.setProjectElectricityPccEnergyUsageFeeTotal(
                                        insertEntity.getProjectElectricityPccEnergyUsageTip().multiply(price(tip))
                                                .add(insertEntity.getProjectElectricityPccEnergyUsagePeak().multiply(price(peak)))
                                                .add(insertEntity.getProjectElectricityPccEnergyUsageValley().multiply(price(valley)))
                                                .add(insertEntity.getProjectElectricityPccEnergyUsageFlat().multiply(price(flat))));
                            }
                        } else if (null != electricityPriceEntity && ElectricityPriceTypeEnum.FIXED_PRICE.getType().equals(electricityPriceEntity.getType())) {
                            if (totalKpi.contains("project.electricity.pccEnergyUsageFee.total") && CollUtil.isNotEmpty(list)) {
                                // 购网电费(由购网尖峰谷平电量及电价得到。) project.electricity.pccEnergyUsageFee.total
                                insertEntity.setProjectElectricityPccEnergyUsageFeeTotal(
                                        insertEntity.getProjectElectricityPccEnergyUsageTotal().multiply(electricityPriceEntity.getPrice()));
                            }
                        }
                        if (totalKpi.contains("project.electricity.pccEnergyProductionFee.total") && CollUtil.isNotEmpty(list)) {
                            // 网收益(由上网电量*上网电价)project.electricity.pccEnergyProductionFee.total
                            insertEntity.setProjectElectricityPccEnergyProductionFeeTotal(insertEntity.getProjectElectricityPccEnergyProductionTotal().multiply(cnfPvPrice));
                        }
                        if (null != electricityPriceEntity && ElectricityPriceTypeEnum.TOU.getType().equals(electricityPriceEntity.getType())) {
                            if (totalKpi.contains("project.electricity.subChargeEnergyChargeFee.total") && CollUtil.isNotEmpty(list)) {
                                // 电度费 由充电电量尖峰谷平电量及电价计算得到 没有见峰谷平指标
                                insertEntity.setProjectElectricitySubChargeEnergyChargeFeeTotal(getDDFee(list, timePeriodList));
                            }
                            if (totalKpi.contains("project.electricity.storageEnergyNetFee.total") && CollUtil.isNotEmpty(list)) {
                                //储能净收益(sum(放电时段单价*放电时段电量）-sum(充电时段单价*充电时段电量))
                                BigDecimal decimal1 = list.stream().map(it -> it.getProjectElectricityStorageEnergyUsageTotal().multiply(electricityPrice(it, timePeriodList)))
                                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                                BigDecimal decimal2 = list.stream().map(it -> it.getProjectElectricityStorageEnergyProductionTotal().multiply(electricityPrice(it, timePeriodList)))
                                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                                insertEntity.setProjectElectricityStorageEnergyNetFeeTotal(decimal2.subtract(decimal1));
                            }
                        } else if (null != electricityPriceEntity && ElectricityPriceTypeEnum.FIXED_PRICE.getType().equals(electricityPriceEntity.getType())) {
                            if (totalKpi.contains("project.electricity.subChargeEnergyChargeFee.total") && CollUtil.isNotEmpty(list)) {
                                // 电度费 由充电电量尖峰谷平电量及电价计算得到 没有见峰谷平指标
                                insertEntity.setProjectElectricitySubChargeEnergyChargeFeeTotal(
                                        list.stream().map(ProjectStaSubitemHourEntity::getProjectElectricitySubChargeEnergyTotal).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add).multiply(electricityPriceEntity.getPrice())
                                );
                            }
                            if (totalKpi.contains("project.electricity.storageEnergyNetFee.total") && CollUtil.isNotEmpty(list)) {
                                //储能净收益(sum(放电时段单价*放电时段电量）-sum(充电时段单价*充电时段电量))
                                BigDecimal decimal1 = list.stream().map(it -> it.getProjectElectricityStorageEnergyUsageTotal().multiply(electricityPriceEntity.getPrice()))
                                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                                BigDecimal decimal2 = list.stream().map(it -> it.getProjectElectricityStorageEnergyProductionTotal().multiply(electricityPriceEntity.getPrice()))
                                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                                insertEntity.setProjectElectricityStorageEnergyNetFeeTotal(decimal2.subtract(decimal1));
                            }
                        }
                        if (totalKpi.contains("project.carbon.pvReductionCO2.total") && CollUtil.isNotEmpty(list)) {
                            // CO2(CO2减排量=光伏发电量 * CO2排放因子 排放因子同用电的排放因子。 标准煤、SO2、粉尘根据CO2*系数得到。系数同之前相同。)
                            insertEntity.setProjectCarbonPvReductionCO2Total(insertEntity.getProjectElectricityPvEnergyProductionTotal().multiply(new BigDecimal("0.00042")));
                        }
                        if (totalKpi.contains("project.carbon.pvReductionCoal.total") && CollUtil.isNotEmpty(list)) {
                            // CO2(CO2减排量=光伏发电量 * CO2排放因子 排放因子同用电的排放因子。 标准煤、SO2、粉尘根据CO2*系数得到。系数同之前相同。)
                            insertEntity.setProjectCarbonPvReductionCoalTotal(insertEntity.getProjectCarbonPvReductionCO2Total().multiply(new BigDecimal("0.4012")));
                        }
                        if (totalKpi.contains("project.carbon.pvReductionSO2.total") && CollUtil.isNotEmpty(list)) {
                            // CO2(CO2减排量=光伏发电量 * CO2排放因子 排放因子同用电的排放因子。 标准煤、SO2、粉尘根据CO2*系数得到。系数同之前相同。)
                            insertEntity.setProjectCarbonPvReductionSO2Total(insertEntity.getProjectCarbonPvReductionCO2Total().multiply(new BigDecimal("0.0301")));
                        }
                        if (totalKpi.contains("project.carbon.pvReductionDust.total") && CollUtil.isNotEmpty(list)) {
                            // CO2(CO2减排量=光伏发电量 * CO2排放因子 排放因子同用电的排放因子。 标准煤、SO2、粉尘根据CO2*系数得到。系数同之前相同。)
                            insertEntity.setProjectCarbonPvReductionDustTotal(insertEntity.getProjectCarbonPvReductionCO2Total().multiply(new BigDecimal("0.2782")));
                        }

                        if (totalKpi.contains("project.electricity.subHAVCEnergy.avgSq") && CollUtil.isNotEmpty(list)) {
                            // 空调总用电/项目建筑面积
                            insertEntity.setProjectElectricitySubhavcenergyAvgsq(insertEntity.getProjectElectricitySubhavcenergyTotal().divide(projInfo.getArea(), 2, RoundingMode.HALF_UP));
                        }
                        if (totalKpi.contains("project.electricity.subHeatingWaterEnergy.avgCube") && CollUtil.isNotEmpty(list)) {
                            // 热水总用电量/热水补水量
                            if (insertEntity.getProjectWaterHeatingwaterusageTotal() == null || insertEntity.getProjectWaterHeatingwaterusageTotal().compareTo(BigDecimal.ZERO) == 0) {
                                insertEntity.setProjectElectricitySubheatingwaterenergyAvgcube(BigDecimal.ZERO);
                            } else {
                                insertEntity.setProjectElectricitySubheatingwaterenergyAvgcube(null == insertEntity.getProjectElectricitySubheatingwaterenergyTotal() || 0 >= insertEntity.getProjectElectricitySubheatingwaterenergyTotal().intValue() ?
                                        BigDecimal.ZERO : insertEntity.getProjectElectricitySubheatingwaterenergyTotal().divide(insertEntity.getProjectWaterHeatingwaterusageTotal(), 2, RoundingMode.HALF_UP));
                            }
                        }


                        insertEntity.setProjectCarbonTotalco2Total(insertEntity.getProjectCarbonElectricityusageco2Total().add(insertEntity.getProjectCarbonWaterusageco2Total()).add(insertEntity.getProjectCarbonGasusageco2Total()));
                        insertEntity.setProjectCarbonTotalcoalTotal(insertEntity.getProjectCarbonElectricityusagecoalTotal().add(insertEntity.getProjectCarbonWaterusagecoalTotal()).add(insertEntity.getProjectCarbonGasusagecoalTotal()));
                        insertEntity.setProjectCarbonTotalso2Total(insertEntity.getProjectCarbonElectricityusageso2Total().add(insertEntity.getProjectCarbonWaterusageso2Total()).add(insertEntity.getProjectCarbonGasusageso2Total()));
                        insertEntity.setProjectCarbonTotaldustTotal(insertEntity.getProjectCarbonElectricityusagedustTotal().add(insertEntity.getProjectCarbonWaterusagedustTotal()).add(insertEntity.getProjectCarbonGasusagedustTotal()));

                        projectStaSubitemDayServiceImpl.save(insertEntity);
                    } catch (Exception e) {
                        jobLogMap.get(tenantId).setStatus(JOB_EXEC_ERROR);
                        log.error("分项日项目执行异常", e);
                    }
                }
            } finally {
                saveJobLog(jobLogMap, tenantProjectMap);
            }
        }
        return true;
    }

    @Override
    public boolean statisticsByMonth(String[] times, JobRpcRequest request) {
        List<LocalDateTime> timeList = new ArrayList<>();
        if (!ArrayUtil.isEmpty(times)) {
            // 小时的时间，应该是yyyy-MM
            for (String time : times) {
                timeList.add(LocalDateTime.parse(time + " 00", hourFormatter).withHour(0).withMinute(0).withSecond(0).withNano(0));
            }
        } else {
            LocalDateTime now = LocalDateTime.now();
            if (now.getDayOfMonth() > 7 && now.getDayOfMonth() < 24) {
                return true;
            }
            if (now.getDayOfMonth() > 29) {
                return true;
            }
            timeList.add(now.minusMonths(1).withHour(0).withMinute(0).withSecond(0).withNano(0));
        }
        // 获取需要分享处理的kpicode
        List<ProjectKpiConfigEntity> kpiConfList = projectKpiConfigServiceImpl.list(Wrappers.<ProjectKpiConfigEntity>lambdaQuery().like(ProjectKpiConfigEntity::getCode, "project%").eq(ProjectKpiConfigEntity::getStaIntervalYmd, 1));

        if (CollectionUtils.isEmpty(kpiConfList)) {
            // 不需要统计， 返回即可
            return true;
        }
        Map<String, List<ProjectKpiConfigEntity>> kpiConfigMap = kpiConfList.stream().collect(Collectors.groupingBy(ProjectKpiConfigEntity::getKpiSubtype));
//                kpiConfList.stream().collect(Collectors.groupingBy(ProjectKpiConfigEntity::getTenantId)).entrySet()
//                        .stream().collect(Collectors.toMap(Map.Entry::getKey,
//                                v -> v.getValue().stream().collect(Collectors.groupingBy(ProjectKpiConfigEntity::getKpiSubtype))));

        // 通过kpiSubtype查找对应的subitem的配置
        List<String> kpiSubtypeList = kpiConfList.stream().map(ProjectKpiConfigEntity::getKpiSubtype).filter(StrUtil::isNotBlank).distinct().collect(Collectors.toList());
        List<ProjectCnfSubitemEntity> subitemConfList = projectCnfSubitemServiceImpl.list(new QueryWrapper<ProjectCnfSubitemEntity>().lambda().in(ProjectCnfSubitemEntity::getKpiSubtype, kpiSubtypeList));

        if (CollectionUtils.isEmpty(subitemConfList)) {
            // 不需要统计， 返回即可
            return true;
        }

        Map<String, List<ProjectCnfSubitemEntity>> projSubitemConfMap = subitemConfList.stream().collect(Collectors.groupingBy(ProjectCnfSubitemEntity::getProjectId));
        List<Long> subitemIdList = subitemConfList.stream().map(ProjectCnfSubitemEntity::getId).collect(Collectors.toList());

        // 获取配置的设备列表
        List<ProjectSubitemDeviceEntity> deviceConfList = projectSubitemDeviceServiceImpl.list(new QueryWrapper<ProjectSubitemDeviceEntity>().lambda().in(ProjectSubitemDeviceEntity::getSubitemId, subitemIdList));

        Map<Long, Map<Long, List<ProjectSubitemDeviceEntity>>> deviceConfMap = deviceConfList.stream().collect(Collectors.groupingBy(ProjectSubitemDeviceEntity::getTenantId, Collectors.groupingBy(ProjectSubitemDeviceEntity::getSubitemId)));

        // 通过时间，处理对应信息
        for (LocalDateTime time : timeList) {
            Map<Long, JobLogSaveDTO> jobLogMap = new HashMap<>(8);
            Map<Long, Map<String, String>> tenantProjectMap = new HashMap<>(8);

            int currentMonth = time.getMonthValue();
            int currentYear = time.getYear();
            if (time.getDayOfMonth() > 23) {
                currentMonth += 1;
                if (currentMonth > 12) {
                    currentMonth = 12;
                    currentYear += 1;
                }
            }

            // 如果当前时间，已有对应的记录，删掉重新出
            String bizProjectId = null;
            Long tenantId = null;
            Map<String, List<ProjectKpiConfigEntity>> kpiConfDetailMap;
            try {
                for (Map.Entry<String, List<ProjectCnfSubitemEntity>> entry : projSubitemConfMap.entrySet()) {
                    bizProjectId = entry.getValue().get(0).getProjectId();
                    tenantId = entry.getValue().get(0).getTenantId();

                    // modify by hebin. 这个步骤提前， 如果没有对应的分区设备的配置， 直接continue，节省性能
                    if (!deviceConfMap.containsKey(tenantId)) {
                        log.info("当前租户没有对应的配置，给一个空配置， 租户编号为：{}", tenantId);
                        deviceConfMap.put(tenantId, new HashMap<>());
                    }


                    List<String> totalKpi = new ArrayList<>();
                    // 获取项目信息
                    Response<ProjectDetailsResponse> projectResp = projectApi.getProjectDetails(bizProjectId);
                    if (!projectResp.isSuccess() || null == projectResp.getResult()) {
                        log.error("获取项目信息失败, 项目编号为: {}", bizProjectId);
                        continue;
                    }
                    ProjectDetailsResponse projInfo = projectResp.getResult();

                    // 获取租户信息
                    Response<TenantInfoResponse> tenantResp = tenantApi.getTenantInfo(tenantId);
                    if (!tenantResp.isSuccess() || null == tenantResp.getResult()) {
                        log.error("获取租户信息失败, 租户编号为: {}", tenantId);
                        continue;
                    }
                    TenantInfoResponse tenantInfo = tenantResp.getResult();

                    // 2023-11-16 增加手动执行过滤条件
                    if (null != request.getTenantId() && !Objects.equals(tenantId, request.getTenantId())) {
                        continue;
                    }

                    if (CollectionUtil.isNotEmpty(request.getProjectList()) && !request.getProjectList().contains(bizProjectId)) {
                        continue;
                    }

                    // 如果月报周期与当前时间不一致，返回
                    if (!String.valueOf(time.getDayOfMonth() - 1).equals(tenantInfo.getReportingCycle())) {
                        continue;
                    }

                    List<ProjectStaSubitemDayEntity> records = null;
                    if (ReportingCycle.LABEL_0.getCode().equals(tenantInfo.getReportingCycle())) {
                        records = projectStaSubitemDayServiceImpl.list(
                                new QueryWrapper<ProjectStaSubitemDayEntity>().lambda().eq(ProjectStaSubitemDayEntity::getBizProjectId, bizProjectId)
                                        .eq(ProjectStaSubitemDayEntity::getYear, String.valueOf(currentYear)).eq(ProjectStaSubitemDayEntity::getMonth, String.valueOf(currentMonth)));
                    } else if (time.getDayOfMonth() > 23) {
                        // 计算上个月到当前的
                        int startYear = currentYear;
                        int startMonth = currentMonth - 1;
                        if (currentMonth == 1) {
                            startYear = currentYear - 1;
                            startMonth = 12;
                        }
                        String startYearStr = String.valueOf(startYear);
                        String startMonthStr = String.valueOf(startMonth);
                        String currentYearStr = String.valueOf(currentYear);
                        String currentMonthStr = String.valueOf(currentMonth);
                        records = projectStaSubitemDayServiceImpl.list(
                                new QueryWrapper<ProjectStaSubitemDayEntity>().lambda()
                                        .eq(ProjectStaSubitemDayEntity::getBizProjectId, bizProjectId)
                                        .and(wrapper -> wrapper.or(w1 -> w1.eq(ProjectStaSubitemDayEntity::getYear, startYearStr).eq(ProjectStaSubitemDayEntity::getMonth, startMonthStr).gt(ProjectStaSubitemDayEntity::getDay, String.valueOf(time.getDayOfMonth() - 1)).gt(ProjectStaSubitemDayEntity::getDay, "10"))
                                                .or(w2 -> w2.eq(ProjectStaSubitemDayEntity::getYear, currentYearStr).eq(ProjectStaSubitemDayEntity::getMonth, currentMonthStr).le(ProjectStaSubitemDayEntity::getDay, String.valueOf(time.getDayOfMonth() - 1)))));
                    } else {
                        // 计算本月到下个月的
                        int endYear = currentYear;
                        int endMonth = currentMonth + 1;
                        if (currentMonth == 12) {
                            endYear = currentYear + 1;
                            endMonth = 1;
                        }
                        String endYearStr = String.valueOf(endYear);
                        String endMonthStr = String.valueOf(endMonth);
                        String currentYearStr = String.valueOf(currentYear);
                        String currentMonthStr = String.valueOf(currentMonth);
                        records = projectStaSubitemDayServiceImpl.list(
                                new QueryWrapper<ProjectStaSubitemDayEntity>().lambda()
                                        .eq(ProjectStaSubitemDayEntity::getBizProjectId, bizProjectId)
                                        .and(wrapper -> wrapper.or(w1 -> w1.eq(ProjectStaSubitemDayEntity::getYear, endYearStr).eq(ProjectStaSubitemDayEntity::getMonth, endMonthStr).le(ProjectStaSubitemDayEntity::getDay, String.valueOf(time.getDayOfMonth() - 1)).le(ProjectStaSubitemDayEntity::getDay, "10"))
                                                .or(w2 -> w2.eq(ProjectStaSubitemDayEntity::getYear, currentYearStr).eq(ProjectStaSubitemDayEntity::getMonth, currentMonthStr).gt(ProjectStaSubitemDayEntity::getDay, String.valueOf(time.getDayOfMonth() - 1)))));
                    }
                    Map<String, List<ProjectStaSubitemDayEntity>> recordsMap = records.stream().collect(Collectors.groupingBy(ProjectStaSubitemDayEntity::getBizProjectId));


                    projectStaSubitemMonthServiceImpl.remove(Wrappers.<ProjectStaSubitemMonthEntity>lambdaQuery().eq(ProjectStaSubitemMonthEntity::getBizProjectId, bizProjectId).eq(ProjectStaSubitemMonthEntity::getYear, String.valueOf(currentYear)).eq(ProjectStaSubitemMonthEntity::getMonth, String.valueOf(currentMonth)));

                    // 2023-11-15 增加日志记录
                    if (!jobLogMap.containsKey(tenantId)) {
                        JobLogSaveDTO jobLog = new JobLogSaveDTO();
                        jobLog.setJobId(request.getJobId())
                                .setTenantId(tenantId)
                                .setStatus(JOB_EXEC_SUCCESS)
                                .setExecTime(LocalDateTime.now())
                                .setExecType(request.getExecType())
                                .setExecUser(request.getExecUser());
                        jobLogMap.put(tenantId, jobLog);
                    }

                    if (!tenantProjectMap.containsKey(tenantId)) {
                        tenantProjectMap.put(tenantId, new HashMap<>(8));
                    }

                    if (!tenantProjectMap.get(tenantId).containsKey(bizProjectId)) {
                        tenantProjectMap.get(tenantId).put(bizProjectId, projInfo.getName());
                    }

                    ProjectCnfElectricityPriceEntity electricityPriceEntity = projectCnfElectricityPriceServiceImpl.selectByBizProjId(projInfo.getBizProjectId());

                    try {
                        // 通过日表累加获取结果
                        ProjectStaSubitemMonthEntity insertEntity = new ProjectStaSubitemMonthEntity();
                        insertEntity.setBizProjectId(bizProjectId);
                        insertEntity.setMonth(String.valueOf(currentMonth));
                        insertEntity.setYear(String.valueOf(currentYear));
                        insertEntity.setProjectCode(projInfo.getCode());
                        insertEntity.setProjectName(projInfo.getName());
                        insertEntity.setTenantId(tenantId);
                        insertEntity.setTenantCode(tenantInfo.getCode());
                        insertEntity.setStaTime(Timestamp.valueOf(currentYear + "-" + (currentMonth > 9 ? currentMonth : "0" + currentMonth) + "-01 00:00:00"));

                        insertEntity.setProjectGasFeeTotal(BigDecimal.ZERO);
                        insertEntity.setProjectGasUsageTotal(BigDecimal.ZERO);
                        insertEntity.setProjectWaterUsageTotal(BigDecimal.ZERO);
                        insertEntity.setProjectWaterFeeWater(BigDecimal.ZERO);
                        insertEntity.setProjectWaterFeeSewerage(BigDecimal.ZERO);
                        insertEntity.setProjectWaterFeeTotal(BigDecimal.ZERO);
                        insertEntity.setProjectElectricityEnergyusageFlat(BigDecimal.ZERO);
                        insertEntity.setProjectElectricityEnergyusageTotal(BigDecimal.ZERO);
                        insertEntity.setProjectElectricityEnergyusagefeeTotal(BigDecimal.ZERO);
                        insertEntity.setProjectElectricityEnergyusageTip(BigDecimal.ZERO);
                        insertEntity.setProjectElectricityEnergyusageValley(BigDecimal.ZERO);
                        insertEntity.setProjectElectricityEnergyusagePeak(BigDecimal.ZERO);
                        insertEntity.setProjectElectricitySubelevatorenergyTotal(BigDecimal.ZERO);
                        insertEntity.setProjectElectricitySubguestroomenergyTotal(BigDecimal.ZERO);
                        insertEntity.setProjectElectricitySubheatingwaterenergyTotal(BigDecimal.ZERO);
                        insertEntity.setProjectElectricitySubhavcenergyTotal(BigDecimal.ZERO);
                        insertEntity.setProjectElectricitySubpowersupplyenergyTotal(BigDecimal.ZERO);
                        insertEntity.setProjectElectricitySubothertypeTotal(BigDecimal.ZERO);
                        insertEntity.setProjectElectricitySubwatersupplyenergyTotal(BigDecimal.ZERO);
                        insertEntity.setProjectCarbonTotalcoalTotal(BigDecimal.ZERO);
                        insertEntity.setProjectCarbonTotaldustTotal(BigDecimal.ZERO);
                        insertEntity.setProjectCarbonTotalco2Total(BigDecimal.ZERO);
                        insertEntity.setProjectCarbonTotalso2Total(BigDecimal.ZERO);
                        insertEntity.setProjectCarbonGasusageco2Total(BigDecimal.ZERO);
                        insertEntity.setProjectCarbonGasusagecoalTotal(BigDecimal.ZERO);
                        insertEntity.setProjectCarbonGasusagedustTotal(BigDecimal.ZERO);
                        insertEntity.setProjectCarbonGasusageso2Total(BigDecimal.ZERO);
                        insertEntity.setProjectCarbonElectricityusageso2Total(BigDecimal.ZERO);
                        insertEntity.setProjectCarbonElectricityusagedustTotal(BigDecimal.ZERO);
                        insertEntity.setProjectCarbonElectricityusageco2Total(BigDecimal.ZERO);
                        insertEntity.setProjectCarbonElectricityusagecoalTotal(BigDecimal.ZERO);
                        insertEntity.setProjectCarbonWaterusagedustTotal(BigDecimal.ZERO);
                        insertEntity.setProjectCarbonWaterusagecoalTotal(BigDecimal.ZERO);
                        insertEntity.setProjectCarbonWaterusageco2Total(BigDecimal.ZERO);
                        insertEntity.setProjectCarbonWaterusageso2Total(BigDecimal.ZERO);
                        insertEntity.setProjectEnvironmentOutTempAvg(BigDecimal.ZERO);
                        insertEntity.setProjectEnvironmentOutTempMax(BigDecimal.ZERO);
                        insertEntity.setProjectEnvironmentOutTempMin(BigDecimal.ZERO);
                        insertEntity.setProjectEnvironmentOutTumidityAvg(BigDecimal.ZERO);
                        insertEntity.setProjectElectricityPccEnergyUsageTotal(BigDecimal.ZERO);
                        insertEntity.setProjectElectricityPccEnergyUsageFeeTotal(BigDecimal.ZERO);
                        insertEntity.setProjectElectricityPccEnergyUsageTip(BigDecimal.ZERO);
                        insertEntity.setProjectElectricityPccEnergyUsagePeak(BigDecimal.ZERO);
                        insertEntity.setProjectElectricityPccEnergyUsageValley(BigDecimal.ZERO);
                        insertEntity.setProjectElectricityPccEnergyUsageFlat(BigDecimal.ZERO);
                        insertEntity.setProjectElectricityPccEnergyProductionTotal(BigDecimal.ZERO);
                        insertEntity.setProjectElectricityPccEnergyProductionFeeTotal(BigDecimal.ZERO);
                        insertEntity.setProjectElectricityPvEnergyProductionTotal(BigDecimal.ZERO);
                        insertEntity.setProjectElectricityPvEnergyProductionGridTotal(BigDecimal.ZERO);
                        insertEntity.setProjectElectricityPvEnergyProductionLoadTotal(BigDecimal.ZERO);
                        insertEntity.setProjectElectricityPvEnergyProductionStorageTotal(BigDecimal.ZERO);
                        insertEntity.setProjectElectricityPvEnergyProductionFeeTotal(BigDecimal.ZERO);
                        insertEntity.setProjectElectricityPvEnergyProductionGridFeeTotal(BigDecimal.ZERO);
                        insertEntity.setProjectElectricityPvEnergyProductionUsageFeeTotal(BigDecimal.ZERO);
                        insertEntity.setProjectElectricityStorageEnergyUsageTotal(BigDecimal.ZERO);
                        insertEntity.setProjectElectricityStorageEnergyUsageTip(BigDecimal.ZERO);
                        insertEntity.setProjectElectricityStorageEnergyUsagePeak(BigDecimal.ZERO);
                        insertEntity.setProjectElectricityStorageEnergyUsageValley(BigDecimal.ZERO);
                        insertEntity.setProjectElectricityStorageEnergyUsageFlat(BigDecimal.ZERO);
                        insertEntity.setProjectElectricityStorageEnergyUsagePvTotal(BigDecimal.ZERO);
                        insertEntity.setProjectElectricityStorageEnergyUsageGridTotal(BigDecimal.ZERO);
                        insertEntity.setProjectElectricityStorageEnergyProductionTotal(BigDecimal.ZERO);
                        insertEntity.setProjectElectricityStorageEnergyProductionTip(BigDecimal.ZERO);
                        insertEntity.setProjectElectricityStorageEnergyProductionPeak(BigDecimal.ZERO);
                        insertEntity.setProjectElectricityStorageEnergyProductionValley(BigDecimal.ZERO);
                        insertEntity.setProjectElectricityStorageEnergyProductionFlat(BigDecimal.ZERO);
                        insertEntity.setProjectElectricityStorageEnergyNetFeeTotal(BigDecimal.ZERO);
                        insertEntity.setProjectElectricitySubChargeEnergyTotal(BigDecimal.ZERO);
                        insertEntity.setProjectElectricitySubChargeEnergyFeeTotal(BigDecimal.ZERO);
                        insertEntity.setProjectElectricitySubChargeEnergyChargeFeeTotal(BigDecimal.ZERO);
                        insertEntity.setProjectElectricitySubChargeEnergyServiceFeeTotal(BigDecimal.ZERO);
                        insertEntity.setProjectElectricitySubLightingEnergyTotal(BigDecimal.ZERO);
                        insertEntity.setProjectElectricitySubSocketEnergyTotal(BigDecimal.ZERO);
                        insertEntity.setProjectCarbonPvReductionCoalTotal(BigDecimal.ZERO);
                        insertEntity.setProjectCarbonPvReductionCO2Total(BigDecimal.ZERO);
                        insertEntity.setProjectCarbonPvReductionSO2Total(BigDecimal.ZERO);
                        insertEntity.setProjectCarbonPvReductionDustTotal(BigDecimal.ZERO);

                        List<ProjectStaSubitemDayEntity> list = recordsMap.get(bizProjectId);
                        for (ProjectCnfSubitemEntity i : entry.getValue()) {
                            List<ProjectKpiConfigEntity> kpiList = kpiConfigMap.get(i.getKpiSubtype());
                            Map<String, ProjectKpiConfigEntity> kpiCodeMap = kpiList.stream().collect(Collectors.toMap(ProjectKpiConfigEntity::getCode, k -> k, (v1, v2) -> v1));
                            totalKpi.addAll(kpiCodeMap.keySet().stream().toList());

                            Map<Long, List<ProjectSubitemDeviceEntity>> deviceConfDetailMap = deviceConfMap.get(tenantId);
                            List<ProjectSubitemDeviceEntity> deviceList = deviceConfDetailMap.get(i.getId());
                            Map<String, Object> monthDeviceStatus = Maps.newHashMap();
                            if (!CollectionUtils.isEmpty(deviceList)) {
                                List<String> deviceIds = deviceList.stream().map(ProjectSubitemDeviceEntity::getDeviceId).distinct().collect(Collectors.toList());
                                // 查询设备的小时信息，从4张表查
                                List<ProjectStaDeviceAirMonthEntity> airList = projectStaDeviceAirMonthServiceImpl.list(new QueryWrapper<ProjectStaDeviceAirMonthEntity>().lambda().in(ProjectStaDeviceAirMonthEntity::getBizDeviceId, deviceIds).eq(ProjectStaDeviceAirMonthEntity::getYear, String.valueOf(currentYear)).eq(ProjectStaDeviceAirMonthEntity::getMonth, String.valueOf(currentMonth)));
                                if (!CollectionUtils.isEmpty(airList)) {
                                    monthDeviceStatus.putAll(airList.stream().collect(Collectors.toMap(ProjectStaDeviceAirMonthEntity::getBizDeviceId, j -> j, (v1, v2) -> v1)));
                                }
                                List<ProjectStaDeviceElectricityMonthEntity> electricityList = projectStaDeviceElectricityMonthServiceImpl.list(new QueryWrapper<ProjectStaDeviceElectricityMonthEntity>().lambda().in(ProjectStaDeviceElectricityMonthEntity::getBizDeviceId, deviceIds).eq(ProjectStaDeviceElectricityMonthEntity::getYear, String.valueOf(currentYear)).eq(ProjectStaDeviceElectricityMonthEntity::getMonth, String.valueOf(currentMonth)));
                                if (!CollectionUtils.isEmpty(electricityList)) {
                                    monthDeviceStatus.putAll(electricityList.stream().collect(Collectors.toMap(ProjectStaDeviceElectricityMonthEntity::getBizDeviceId, j -> j, (v1, v2) -> v1)));
                                }
                                List<ProjectStaDeviceWaterMonthEntity> waterList = projectStaDeviceWaterMonthServiceImpl.list(new QueryWrapper<ProjectStaDeviceWaterMonthEntity>().lambda().in(ProjectStaDeviceWaterMonthEntity::getBizDeviceId, deviceIds).eq(ProjectStaDeviceWaterMonthEntity::getYear, String.valueOf(currentYear)).eq(ProjectStaDeviceWaterMonthEntity::getMonth, String.valueOf(currentMonth)));
                                if (!CollectionUtils.isEmpty(waterList)) {
                                    monthDeviceStatus.putAll(waterList.stream().collect(Collectors.toMap(ProjectStaDeviceWaterMonthEntity::getBizDeviceId, j -> j, (v1, v2) -> v1)));
                                }
                                List<ProjectStaDeviceGasMonthEntity> gasList = projectStaDeviceGasMonthServiceImpl.list(new QueryWrapper<ProjectStaDeviceGasMonthEntity>().lambda().in(ProjectStaDeviceGasMonthEntity::getBizDeviceId, deviceIds).eq(ProjectStaDeviceGasMonthEntity::getYear, String.valueOf(currentYear)).eq(ProjectStaDeviceGasMonthEntity::getMonth, String.valueOf(currentMonth)));
                                if (!CollectionUtils.isEmpty(gasList)) {
                                    monthDeviceStatus.putAll(gasList.stream().collect(Collectors.toMap(ProjectStaDeviceGasMonthEntity::getBizDeviceId, j -> j, (v1, v2) -> v1)));
                                }
                                List<ProjectStaDeviceGscnMonthEntity> gascnList = projectStaDeviceGscnMonthMapper.selectList(Wrappers.<ProjectStaDeviceGscnMonthEntity>lambdaQuery().in(ProjectStaDeviceGscnMonthEntity::getBizDeviceId, deviceIds).eq(ProjectStaDeviceGscnMonthEntity::getYear, String.valueOf(currentYear)).eq(ProjectStaDeviceGscnMonthEntity::getMonth, String.valueOf(currentMonth)));
                                if (!CollectionUtils.isEmpty(gascnList)) {
                                    monthDeviceStatus.putAll(gascnList.stream().collect(Collectors.toMap(ProjectStaDeviceGscnMonthEntity::getBizDeviceId, j -> j, (v1, v2) -> v1)));
                                }
                                List<ProjectStaDeviceZnbMonthEntity> znbList = projectStaDeviceZnbMonthMapper.selectList(Wrappers.<ProjectStaDeviceZnbMonthEntity>lambdaQuery().in(ProjectStaDeviceZnbMonthEntity::getBizDeviceId, deviceIds).eq(ProjectStaDeviceZnbMonthEntity::getYear, String.valueOf(currentYear)).eq(ProjectStaDeviceZnbMonthEntity::getMonth, String.valueOf(currentMonth)));
                                if (!CollectionUtils.isEmpty(znbList)) {
                                    monthDeviceStatus.putAll(znbList.stream().collect(Collectors.toMap(ProjectStaDeviceZnbMonthEntity::getBizDeviceId, j -> j, (v1, v2) -> v1)));
                                }
                            } else {
                                deviceList = Lists.newArrayList();
                            }
                            JSONObject monthDeviceObj = JSONObject.parseObject(JSON.toJSONString(monthDeviceStatus));

                            // 环境相关
                            //平均温度
                            List<BigDecimal> tempAvg = list.stream().map(ProjectStaSubitemDayEntity::getProjectEnvironmentOutTempAvg).filter(Objects::nonNull).toList();
                            BigDecimal totalTemp = tempAvg.stream().reduce(BigDecimal.ZERO, BigDecimal::add).divide(BigDecimal.valueOf(tempAvg.size()), 2, RoundingMode.HALF_UP);
                            insertEntity.setProjectEnvironmentOutTempAvg(totalTemp);
                            //最大温度
                            insertEntity.setProjectEnvironmentOutTempMax(list.stream().map(ProjectStaSubitemDayEntity::getProjectEnvironmentOutTempMax).max(BigDecimal::compareTo).orElse(null));
                            //最小温度
                            insertEntity.setProjectEnvironmentOutTempMin(list.stream().map(ProjectStaSubitemDayEntity::getProjectEnvironmentOutTempMin).min(BigDecimal::compareTo).orElse(null));
                            //平均湿度
                            List<BigDecimal> humidityAvg = list.stream().map(ProjectStaSubitemDayEntity::getProjectEnvironmentOutTumidityAvg).filter(Objects::nonNull).toList();
                            BigDecimal totalHumi = humidityAvg.stream().reduce(BigDecimal.ZERO, BigDecimal::add).divide(BigDecimal.valueOf(humidityAvg.size()), 2, RoundingMode.HALF_UP);
                            insertEntity.setProjectEnvironmentOutTumidityAvg(totalHumi);

                            if (kpiCodeMap.containsKey("project.gas.usage.total") && CollUtil.isNotEmpty(list)) {
                                // 用气量累加
                                if (CollUtil.isEmpty(deviceList)) {
                                    insertEntity.setProjectGasUsageTotal(BigDecimal.ZERO);
                                } else {
                                    var val = deviceList.stream().filter(j -> monthDeviceObj.containsKey(j.getDeviceId()) && monthDeviceObj.getJSONObject(j.getDeviceId()).containsKey("gasmeterUsageTotal")).map(j -> monthDeviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("gasmeterUsageTotal").multiply(new BigDecimal(j.getComputeTag()))).reduce(BigDecimal.ZERO, BigDecimal::add);
                                    insertEntity.setProjectGasUsageTotal(val);
                                }
                            }
                            // 燃气单价写死
                            // project.gas.usage.fee
                            insertEntity.setProjectGasFeeTotal(insertEntity.getProjectGasUsageTotal().multiply(new BigDecimal("4.88")));

                            if (kpiCodeMap.containsKey("project.water.usage.total") && CollUtil.isNotEmpty(list)) {
                                // 用水量累加
                                if (CollUtil.isEmpty(deviceList)) {
                                    insertEntity.setProjectWaterUsageTotal(BigDecimal.ZERO);
                                } else {
                                    var val = deviceList.stream().filter(j -> monthDeviceObj.containsKey(j.getDeviceId()) && monthDeviceObj.getJSONObject(j.getDeviceId()).containsKey("watermeterUsageTotal")).map(j -> monthDeviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("watermeterUsageTotal").multiply(new BigDecimal(j.getComputeTag()))).reduce(BigDecimal.ZERO, BigDecimal::add);
                                    insertEntity.setProjectWaterUsageTotal(val);
                                }
                            }
                            if (kpiCodeMap.containsKey("project.water.HVACUsage.total") && CollUtil.isNotEmpty(list)) {
                                // 用空调补水水量累加
                                if (CollUtil.isEmpty(deviceList)) {
                                    insertEntity.setProjectWaterHvacusageTotal(BigDecimal.ZERO);
                                } else {
                                    var val = deviceList.stream().filter(j -> monthDeviceObj.containsKey(j.getDeviceId()) && monthDeviceObj.getJSONObject(j.getDeviceId()).containsKey("watermeterUsageTotal")).map(j -> monthDeviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("watermeterUsageTotal").multiply(new BigDecimal(j.getComputeTag()))).reduce(BigDecimal.ZERO, BigDecimal::add);
                                    insertEntity.setProjectWaterHvacusageTotal(val);
                                }
                            }
                            if (kpiCodeMap.containsKey("project.water.HeatingWaterUsage.total") && CollUtil.isNotEmpty(list)) {
                                // 用热水补水水量累加
                                if (CollUtil.isEmpty(deviceList)) {
                                    insertEntity.setProjectWaterHeatingwaterusageTotal(BigDecimal.ZERO);
                                } else {
                                    var val = deviceList.stream().filter(j -> monthDeviceObj.containsKey(j.getDeviceId()) && monthDeviceObj.getJSONObject(j.getDeviceId()).containsKey("watermeterUsageTotal")).map(j -> monthDeviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("watermeterUsageTotal").multiply(new BigDecimal(j.getComputeTag()))).reduce(BigDecimal.ZERO, BigDecimal::add);
                                    insertEntity.setProjectWaterHeatingwaterusageTotal(val);
                                }
                            }
//                    if (kpiCodeMap.containsKey("project.water.fee.water")) {
                            insertEntity.setProjectWaterFeeWater(insertEntity.getProjectWaterUsageTotal().multiply(new BigDecimal("3.32")));
//                    if (kpiCodeMap.containsKey("project.water.fee.sewerage")) {
                            insertEntity.setProjectWaterFeeSewerage(insertEntity.getProjectWaterUsageTotal().multiply(new BigDecimal("0.9")).multiply(new BigDecimal("2.97")));
//                    if (kpiCodeMap.containsKey("project.water.fee.total")) {
                            insertEntity.setProjectWaterFeeTotal(insertEntity.getProjectWaterFeeWater().add(insertEntity.getProjectWaterFeeSewerage()));

                            if (kpiCodeMap.containsKey("project.electricity.energyUsage.total") && CollUtil.isNotEmpty(list)) {
                                if (CollUtil.isEmpty(deviceList)) {
                                    insertEntity.setProjectElectricityEnergyusageTotal(BigDecimal.ZERO);
                                } else {
                                    var val = deviceList.stream().filter(j -> monthDeviceObj.containsKey(j.getDeviceId()) && monthDeviceObj.getJSONObject(j.getDeviceId()).containsKey("energymeterEpimportTotal")).map(j -> monthDeviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("energymeterEpimportTotal").multiply(new BigDecimal(j.getComputeTag()))).reduce(BigDecimal.ZERO, BigDecimal::add);
                                    insertEntity.setProjectElectricityEnergyusageTotal(val);
                                }
                            }
                            if (null != electricityPriceEntity && ElectricityPriceTypeEnum.TOU.getType().equals(electricityPriceEntity.getType())) {
                                if (kpiCodeMap.containsKey("project.electricity.energyUsage.flat") && CollUtil.isNotEmpty(list)) {
                                    BigDecimal total = list.stream().map(ProjectStaSubitemDayEntity::getProjectElectricityEnergyusageFlat).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
                                    insertEntity.setProjectElectricityEnergyusageFlat(total);
                                }
                                if (kpiCodeMap.containsKey("project.electricity.energyUsage.tip") && CollUtil.isNotEmpty(list)) {
                                    BigDecimal total = list.stream().map(ProjectStaSubitemDayEntity::getProjectElectricityEnergyusageTip).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
                                    insertEntity.setProjectElectricityEnergyusageTip(total);
                                }
                                if (kpiCodeMap.containsKey("project.electricity.energyUsage.valley") && CollUtil.isNotEmpty(list)) {
                                    BigDecimal total = list.stream().map(ProjectStaSubitemDayEntity::getProjectElectricityEnergyusageValley).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
                                    insertEntity.setProjectElectricityEnergyusageValley(total);
                                }
                                if (kpiCodeMap.containsKey("project.electricity.energyUsage.peak") && CollUtil.isNotEmpty(list)) {
                                    BigDecimal total = list.stream().map(ProjectStaSubitemDayEntity::getProjectElectricityEnergyusagePeak).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
                                    insertEntity.setProjectElectricityEnergyusagePeak(total);
                                }
                            }
                            if (kpiCodeMap.containsKey("project.electricity.energyUsageFee.total") && CollUtil.isNotEmpty(list)) {
                                BigDecimal total = list.stream().map(ProjectStaSubitemDayEntity::getProjectElectricityEnergyusagefeeTotal).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
                                insertEntity.setProjectElectricityEnergyusagefeeTotal(total);
                            }

                            if (kpiCodeMap.containsKey("project.electricity.energyUsage.total") && CollUtil.isNotEmpty(list)) {
                                if (CollUtil.isEmpty(deviceList)) {
                                    insertEntity.setProjectElectricityEnergyusageTotal(BigDecimal.ZERO);
                                } else {
                                    var val = deviceList.stream().filter(j -> monthDeviceObj.containsKey(j.getDeviceId()) && monthDeviceObj.getJSONObject(j.getDeviceId()).containsKey("energymeterEpimportTotal")).map(j -> monthDeviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("energymeterEpimportTotal").multiply(new BigDecimal(j.getComputeTag()))).reduce(BigDecimal.ZERO, BigDecimal::add);
                                    insertEntity.setProjectElectricityEnergyusageTotal(val);
                                }
                            }

                            if (kpiCodeMap.containsKey("project.electricity.subHAVCEnergy.total") && CollUtil.isNotEmpty(list)) {
                                if (CollUtil.isEmpty(deviceList)) {
                                    insertEntity.setProjectElectricitySubhavcenergyTotal(BigDecimal.ZERO);
                                } else {
                                    var val = deviceList.stream().filter(j -> monthDeviceObj.containsKey(j.getDeviceId()) && monthDeviceObj.getJSONObject(j.getDeviceId()).containsKey("energymeterEpimportTotal")).map(j -> monthDeviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("energymeterEpimportTotal").multiply(new BigDecimal(j.getComputeTag()))).reduce(BigDecimal.ZERO, BigDecimal::add);
                                    insertEntity.setProjectElectricitySubhavcenergyTotal(val);
                                }
                            }

                            if (kpiCodeMap.containsKey("project.electricity.subPowerSupplyEnergy.total") && CollUtil.isNotEmpty(list)) {
                                // 计算电量，取值包含energymeterEpimportTotal的
                                if (CollUtil.isEmpty(deviceList)) {
                                    insertEntity.setProjectElectricitySubpowersupplyenergyTotal(BigDecimal.ZERO);
                                } else {
                                    var val = deviceList.stream().filter(j -> monthDeviceObj.containsKey(j.getDeviceId()) && monthDeviceObj.getJSONObject(j.getDeviceId()).containsKey("energymeterEpimportTotal")).map(j -> monthDeviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("energymeterEpimportTotal").multiply(new BigDecimal(j.getComputeTag()))).reduce(BigDecimal.ZERO, BigDecimal::add);
                                    insertEntity.setProjectElectricitySubpowersupplyenergyTotal(val);
                                }
                            }

                            if (kpiCodeMap.containsKey("project.electricity.subHeatingWaterEnergy.total") && CollUtil.isNotEmpty(list)) {
                                if (CollUtil.isEmpty(deviceList)) {
                                    insertEntity.setProjectElectricitySubheatingwaterenergyTotal(BigDecimal.ZERO);
                                } else {
                                    var val = deviceList.stream().filter(j -> monthDeviceObj.containsKey(j.getDeviceId()) && monthDeviceObj.getJSONObject(j.getDeviceId()).containsKey("energymeterEpimportTotal")).map(j -> monthDeviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("energymeterEpimportTotal").multiply(new BigDecimal(j.getComputeTag()))).reduce(BigDecimal.ZERO, BigDecimal::add);
                                    insertEntity.setProjectElectricitySubheatingwaterenergyTotal(val);
                                }
                            }

                            if (kpiCodeMap.containsKey("project.electricity.subWaterSupplyEnergy.total") && CollUtil.isNotEmpty(list)) {
                                if (CollUtil.isEmpty(deviceList)) {
                                    insertEntity.setProjectElectricitySubwatersupplyenergyTotal(BigDecimal.ZERO);
                                } else {
                                    var val = deviceList.stream().filter(j -> monthDeviceObj.containsKey(j.getDeviceId()) && monthDeviceObj.getJSONObject(j.getDeviceId()).containsKey("energymeterEpimportTotal")).map(j -> monthDeviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("energymeterEpimportTotal").multiply(new BigDecimal(j.getComputeTag()))).reduce(BigDecimal.ZERO, BigDecimal::add);
                                    insertEntity.setProjectElectricitySubwatersupplyenergyTotal(val);
                                }
                            }

                            if (kpiCodeMap.containsKey("project.electricity.subElevatorEnergy.total") && CollUtil.isNotEmpty(list)) {
                                if (CollUtil.isEmpty(deviceList)) {
                                    insertEntity.setProjectElectricitySubelevatorenergyTotal(BigDecimal.ZERO);
                                } else {
                                    var val = deviceList.stream().filter(j -> monthDeviceObj.containsKey(j.getDeviceId()) && monthDeviceObj.getJSONObject(j.getDeviceId()).containsKey("energymeterEpimportTotal")).map(j -> monthDeviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("energymeterEpimportTotal").multiply(new BigDecimal(j.getComputeTag()))).reduce(BigDecimal.ZERO, BigDecimal::add);
                                    insertEntity.setProjectElectricitySubelevatorenergyTotal(val);
                                }
                            }

                            if (kpiCodeMap.containsKey("project.electricity.subGuestRoomEnergy.total") && CollUtil.isNotEmpty(list)) {
                                if (CollUtil.isEmpty(deviceList)) {
                                    insertEntity.setProjectElectricitySubguestroomenergyTotal(BigDecimal.ZERO);
                                } else {
                                    var val = deviceList.stream().filter(j -> monthDeviceObj.containsKey(j.getDeviceId()) && monthDeviceObj.getJSONObject(j.getDeviceId()).containsKey("energymeterEpimportTotal")).map(j -> monthDeviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("energymeterEpimportTotal").multiply(new BigDecimal(j.getComputeTag()))).reduce(BigDecimal.ZERO, BigDecimal::add);
                                    insertEntity.setProjectElectricitySubguestroomenergyTotal(val);
                                }
                            }

                            if (kpiCodeMap.containsKey("project.electricity.subOtherType.total") && CollUtil.isNotEmpty(list)) {
                                if (CollUtil.isEmpty(deviceList)) {
                                    insertEntity.setProjectElectricitySubothertypeTotal(BigDecimal.ZERO);
                                } else {
                                    var val = deviceList.stream().filter(j -> monthDeviceObj.containsKey(j.getDeviceId()) && monthDeviceObj.getJSONObject(j.getDeviceId()).containsKey("energymeterEpimportTotal")).map(j -> monthDeviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("energymeterEpimportTotal").multiply(new BigDecimal(j.getComputeTag()))).reduce(BigDecimal.ZERO, BigDecimal::add);
                                    insertEntity.setProjectElectricitySubothertypeTotal(val);
                                }
                            }

                            if (kpiCodeMap.containsKey("project.carbon.electricityUsageCO2.total")) {
                                insertEntity.setProjectCarbonElectricityusageco2Total(insertEntity.getProjectElectricityEnergyusageTotal().multiply(new BigDecimal("0.00042")));
                            }

                            if (kpiCodeMap.containsKey("project.carbon.electricityUsageCoal.total")) {
                                insertEntity.setProjectCarbonElectricityusagecoalTotal(insertEntity.getProjectCarbonElectricityusageco2Total().multiply(new BigDecimal("0.4012")));
                            }

                            if (kpiCodeMap.containsKey("project.carbon.electricityUsageSO2.total")) {
                                insertEntity.setProjectCarbonElectricityusageso2Total(insertEntity.getProjectCarbonElectricityusageco2Total().multiply(new BigDecimal("0.0301")));
                            }

                            if (kpiCodeMap.containsKey("project.carbon.electricityUsageDust.total")) {
                                insertEntity.setProjectCarbonElectricityusagedustTotal(insertEntity.getProjectCarbonElectricityusageco2Total().multiply(new BigDecimal("0.2782")));
                            }

                            if (kpiCodeMap.containsKey("project.carbon.gasUsageCO2.total")) {
                                insertEntity.setProjectCarbonGasusageco2Total(insertEntity.getProjectGasUsageTotal().multiply(new BigDecimal("0.00218")));
                            }

                            if (kpiCodeMap.containsKey("project.carbon.gasUsageCoal.total")) {
                                insertEntity.setProjectCarbonGasusagecoalTotal(insertEntity.getProjectCarbonElectricityusagedustTotal().multiply(new BigDecimal("0.4012")));
                            }
                            if (kpiCodeMap.containsKey("project.carbon.waterUsageCO2.total")) {
                                insertEntity.setProjectCarbonWaterusageco2Total(insertEntity.getProjectWaterUsageTotal().multiply(new BigDecimal("0.00185")));
                            }

                            if (kpiCodeMap.containsKey("project.carbon.waterUsageCoal.total")) {
                                insertEntity.setProjectCarbonWaterusagecoalTotal(insertEntity.getProjectCarbonWaterusageco2Total().multiply(new BigDecimal("0.4012")));
                            }

                            if (kpiCodeMap.containsKey("project.carbon.waterUsageSO2.total")) {
                                insertEntity.setProjectCarbonWaterusageso2Total(insertEntity.getProjectCarbonWaterusageco2Total().multiply(new BigDecimal("0.0301")));
                            }

                            if (kpiCodeMap.containsKey("project.carbon.waterUsageDust.total")) {
                                insertEntity.setProjectCarbonWaterusagedustTotal(insertEntity.getProjectCarbonWaterusageco2Total().multiply(new BigDecimal("0.2782")));
                            }

//                    if (kpiCodeMap.containsKey("project.carbon.totalCO2.total")) {
                            insertEntity.setProjectCarbonTotalco2Total(insertEntity.getProjectCarbonElectricityusageco2Total().add(insertEntity.getProjectCarbonWaterusageco2Total()).add(insertEntity.getProjectCarbonGasusageco2Total()));

//                    if (kpiCodeMap.containsKey("project.carbon.totalCoal.total")) {
                            insertEntity.setProjectCarbonTotalcoalTotal(insertEntity.getProjectCarbonElectricityusagecoalTotal().add(insertEntity.getProjectCarbonWaterusagecoalTotal()).add(insertEntity.getProjectCarbonGasusagecoalTotal()));

                            //                    if (kpiCodeMap.containsKey("project.carbon.totalSO2.total")) {
                            insertEntity.setProjectCarbonTotalso2Total(insertEntity.getProjectCarbonElectricityusageso2Total().add(insertEntity.getProjectCarbonWaterusageso2Total()).add(insertEntity.getProjectCarbonGasusageso2Total()));

                            //                    if (kpiCodeMap.containsKey("project.carbon.totalDust.total")) {
                            insertEntity.setProjectCarbonTotaldustTotal(insertEntity.getProjectCarbonElectricityusagedustTotal().add(insertEntity.getProjectCarbonWaterusagedustTotal()).add(insertEntity.getProjectCarbonGasusagedustTotal()));
                            if (kpiCodeMap.containsKey("project.electricity.pccEnergyUsage.total") && CollUtil.isNotEmpty(list)) {
                                //购网电量,根据电耗分类配置进行统计
                                if (CollUtil.isEmpty(deviceList)) {
                                    insertEntity.setProjectElectricityPccEnergyUsageTotal(BigDecimal.ZERO);
                                } else {
                                    var val = deviceList.stream().filter(j -> monthDeviceObj.containsKey(j.getDeviceId()) && monthDeviceObj.getJSONObject(j.getDeviceId()).containsKey("energymeterEpimportTotal"))
                                            .map(j -> monthDeviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("energymeterEpimportTotal").multiply(new BigDecimal(j.getComputeTag())))
                                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                                    var val1 = deviceList.stream().filter(j -> monthDeviceObj.containsKey(j.getDeviceId()) && monthDeviceObj.getJSONObject(j.getDeviceId()).containsKey("gscnEpimportTotal"))
                                            .map(j -> monthDeviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("gscnEpimportTotal").multiply(new BigDecimal(j.getComputeTag())))
                                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                                    var val2 = deviceList.stream().filter(j -> monthDeviceObj.containsKey(j.getDeviceId()) && monthDeviceObj.getJSONObject(j.getDeviceId()).containsKey("znbEpimportTotal"))
                                            .map(j -> monthDeviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("znbEpimportTotal").multiply(new BigDecimal(j.getComputeTag())))
                                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                                    insertEntity.setProjectElectricityPccEnergyUsageTotal(val.add(val1).add(val2));
                                }
                            }
                            if (null != electricityPriceEntity && ElectricityPriceTypeEnum.TOU.getType().equals(electricityPriceEntity.getType())) {
                                if (kpiCodeMap.containsKey("project.electricity.pccEnergyUsage.tip") && CollUtil.isNotEmpty(list)) {
                                    // 购网尖电量(根据尖时段配置，由小时表汇总得到。。)
                                    insertEntity.setProjectElectricityPccEnergyUsageTip(list.stream().map(ProjectStaSubitemDayEntity::getProjectElectricityPccEnergyUsageTip).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add));
                                }
                                if (kpiCodeMap.containsKey("project.electricity.pccEnergyUsage.peak") && CollUtil.isNotEmpty(list)) {
                                    // 购网峰电量(根据峰时段配置，由小时表汇总得到。)
                                    insertEntity.setProjectElectricityPccEnergyUsagePeak(list.stream().map(ProjectStaSubitemDayEntity::getProjectElectricityPccEnergyUsagePeak).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add));
                                }
                                if (kpiCodeMap.containsKey("project.electricity.pccEnergyUsage.valley") && CollUtil.isNotEmpty(list)) {
                                    // 购网谷电量(根据谷时段配置，由小时表汇总得到。)
                                    insertEntity.setProjectElectricityPccEnergyUsageValley(list.stream().map(ProjectStaSubitemDayEntity::getProjectElectricityPccEnergyUsageValley).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add));
                                }
                                if (kpiCodeMap.containsKey("project.electricity.pccEnergyUsage.flat") && CollUtil.isNotEmpty(list)) {
                                    // 购网平电量(根据平时段配置，由小时表汇总得到)
                                    insertEntity.setProjectElectricityPccEnergyUsageFlat(list.stream().map(ProjectStaSubitemDayEntity::getProjectElectricityPccEnergyUsageFlat).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add));
                                }
                            }
                            if (kpiCodeMap.containsKey("project.electricity.pccEnergyUsageFee.total") && CollUtil.isNotEmpty(list)) {
                                // 购网电费(由购网尖峰谷平电量及电价得到。) project.electricity.pccEnergyUsageFee.total
                                insertEntity.setProjectElectricityPccEnergyUsageFeeTotal(list.stream().map(ProjectStaSubitemDayEntity::getProjectElectricityPccEnergyUsageFeeTotal).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add));
                            }
                            if (kpiCodeMap.containsKey("project.electricity.pccEnergyProduction.total") && CollUtil.isNotEmpty(list)) {
                                //上网电量(由小时表汇总得到) project.electricity.pccEnergyProduction.total
                                if (CollUtil.isEmpty(deviceList)) {
                                    insertEntity.setProjectElectricityPccEnergyProductionTotal(BigDecimal.ZERO);
                                }
                                var val = deviceList.stream().filter(j -> monthDeviceObj.containsKey(j.getDeviceId()) && monthDeviceObj.getJSONObject(j.getDeviceId()).containsKey("energymeterEpexportTotal"))
                                        .map(j -> monthDeviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("energymeterEpexportTotal").multiply(new BigDecimal(j.getComputeTag())))
                                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                                var val1 = deviceList.stream().filter(j -> monthDeviceObj.containsKey(j.getDeviceId()) && monthDeviceObj.getJSONObject(j.getDeviceId()).containsKey("gscnEpexportTotal"))
                                        .map(j -> monthDeviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("gscnEpexportTotal").multiply(new BigDecimal(j.getComputeTag())))
                                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                                var val2 = deviceList.stream().filter(j -> monthDeviceObj.containsKey(j.getDeviceId()) && monthDeviceObj.getJSONObject(j.getDeviceId()).containsKey("znbEpexportTotal"))
                                        .map(j -> monthDeviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("znbEpexportTotal").multiply(new BigDecimal(j.getComputeTag())))
                                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                                insertEntity.setProjectElectricityPccEnergyProductionTotal(val.add(val1).add(val2));

                            }
                            if (kpiCodeMap.containsKey("project.electricity.pccEnergyProductionFee.total") && CollUtil.isNotEmpty(list)) {
                                // 网收益(由上网电量*上网电价)project.electricity.pccEnergyProductionFee.total
                                insertEntity.setProjectElectricityPccEnergyProductionFeeTotal(list.stream().map(ProjectStaSubitemDayEntity::getProjectElectricityPccEnergyProductionFeeTotal).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add));
                            }
                            if (kpiCodeMap.containsKey("project.electricity.pvEnergyProduction.total") && CollUtil.isNotEmpty(list)) {
                                //光伏发电量(由小时表汇总得到) project.electricity.pvEnergyProduction.total
                                if (CollUtil.isEmpty(deviceList)) {
                                    insertEntity.setProjectElectricityPvEnergyProductionTotal(BigDecimal.ZERO);
                                }
                                var val = deviceList.stream().filter(j -> monthDeviceObj.containsKey(j.getDeviceId()) && monthDeviceObj.getJSONObject(j.getDeviceId()).containsKey("energymeterEpexportTotal"))
                                        .map(j -> monthDeviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("energymeterEpexportTotal").multiply(new BigDecimal(j.getComputeTag())))
                                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                                var val1 = deviceList.stream().filter(j -> monthDeviceObj.containsKey(j.getDeviceId()) && monthDeviceObj.getJSONObject(j.getDeviceId()).containsKey("gscnEpexportTotal"))
                                        .map(j -> monthDeviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("gscnEpexportTotal").multiply(new BigDecimal(j.getComputeTag())))
                                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                                var val2 = deviceList.stream().filter(j -> monthDeviceObj.containsKey(j.getDeviceId()) && monthDeviceObj.getJSONObject(j.getDeviceId()).containsKey("znbEpexportTotal"))
                                        .map(j -> monthDeviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("znbEpexportTotal").multiply(new BigDecimal(j.getComputeTag())))
                                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                                insertEntity.setProjectElectricityPvEnergyProductionTotal(val.add(val1).add(val2));

                            }
                            if (kpiCodeMap.containsKey("project.electricity.pvEnergyProductionGrid.total") && CollUtil.isNotEmpty(list)) {
                                // 上网电量(同关口上网电量指标) project.electricity.pvEnergyProductionGrid.total
                                insertEntity.setProjectElectricityPvEnergyProductionGridTotal(insertEntity.getProjectElectricityPccEnergyProductionTotal());
                            }
                            if (kpiCodeMap.containsKey("project.electricity.pvEnergyProductionStorage.total") && CollUtil.isNotEmpty(list)) {
                                //先储后用(由储光电量汇总) project.electricity.pvEnergyProductionStorage.total
                                insertEntity.setProjectElectricityPvEnergyProductionStorageTotal(list.stream().map(ProjectStaSubitemDayEntity::getProjectElectricityStorageEnergyUsagePvTotal).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add));
                            }
                            if (kpiCodeMap.containsKey("project.electricity.pvEnergyProductionLoad.total") && CollUtil.isNotEmpty(list)) {
                                // 直接使用(光伏总发电-上网-先储后用) project.electricity.pvEnergyProductionLoad.total
                                insertEntity.setProjectElectricityPvEnergyProductionLoadTotal(list.stream().map(ProjectStaSubitemDayEntity::getProjectElectricityPvEnergyProductionLoadTotal).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add));
                            }
                            if (kpiCodeMap.containsKey("project.electricity.pvEnergyProductionGridFee.total") && CollUtil.isNotEmpty(list)) {
                                //上网收益（同关口上网收益） project.electricity.pvEnergyProductionGridFee.total
                                insertEntity.setProjectElectricityPvEnergyProductionGridFeeTotal(insertEntity.getProjectElectricityPccEnergyProductionFeeTotal());
                            }
                            if (kpiCodeMap.containsKey("project.electricity.pvEnergyProductionUsageFee.total") && CollUtil.isNotEmpty(list)) {
                                // 消纳收益(sum(（小时发电量-小时上网电量）* 当时单价)) project.electricity.pvEnergyProductionUsageFee.total
                                insertEntity.setProjectElectricityPvEnergyProductionUsageFeeTotal(list.stream().map(ProjectStaSubitemDayEntity::getProjectElectricityPvEnergyProductionUsageFeeTotal).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add));
                            }
                            if (kpiCodeMap.containsKey("project.electricity.pvEnergyProductionFee.total") && CollUtil.isNotEmpty(list)) {
                                // 光伏收益(上网收益+消纳)project.electricity.pvEnergyProductionFee.total
                                insertEntity.setProjectElectricityPvEnergyProductionFeeTotal(list.stream().map(ProjectStaSubitemDayEntity::getProjectElectricityPvEnergyProductionFeeTotal).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add));
                            }
                            if (kpiCodeMap.containsKey("project.electricity.storageEnergyUsage.total") && CollUtil.isNotEmpty(list)) {
                                //储充电量(由小时表汇总得到) project.electricity.storageEnergyUsage.total
                                if (CollUtil.isEmpty(deviceList)) {
                                    insertEntity.setProjectElectricityStorageEnergyUsageTotal(BigDecimal.ZERO);
                                } else {
                                    var val = deviceList.stream().filter(j -> monthDeviceObj.containsKey(j.getDeviceId()) && monthDeviceObj.getJSONObject(j.getDeviceId()).containsKey("energymeterEpimportTotal"))
                                            .map(j -> monthDeviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("energymeterEpimportTotal").multiply(new BigDecimal(j.getComputeTag())))
                                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                                    var val1 = deviceList.stream().filter(j -> monthDeviceObj.containsKey(j.getDeviceId()) && monthDeviceObj.getJSONObject(j.getDeviceId()).containsKey("gscnEpimportTotal"))
                                            .map(j -> monthDeviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("gscnEpimportTotal").multiply(new BigDecimal(j.getComputeTag())))
                                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                                    var val2 = deviceList.stream().filter(j -> monthDeviceObj.containsKey(j.getDeviceId()) && monthDeviceObj.getJSONObject(j.getDeviceId()).containsKey("znbEpimportTotal"))
                                            .map(j -> monthDeviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("znbEpimportTotal").multiply(new BigDecimal(j.getComputeTag())))
                                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                                    insertEntity.setProjectElectricityStorageEnergyUsageTotal(val.add(val1).add(val2));
                                }
                            }
                            if (null != electricityPriceEntity && ElectricityPriceTypeEnum.TOU.getType().equals(electricityPriceEntity.getType())) {
                                if (kpiCodeMap.containsKey("project.electricity.storageEnergyUsage.tip") && CollUtil.isNotEmpty(list)) {
                                    //尖充电量(根据尖时段配置，由小时表汇总得到。) project.electricity.storageEnergyUsage.tip
                                    insertEntity.setProjectElectricityStorageEnergyUsageTip(list.stream().map(ProjectStaSubitemDayEntity::getProjectElectricityStorageEnergyUsageTip).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add));
                                }
                                if (kpiCodeMap.containsKey("project.electricity.storageEnergyUsage.peak") && CollUtil.isNotEmpty(list)) {
                                    //峰充电量(根据峰时段配置，由小时表汇总得到) project.electricity.storageEnergyUsage.peak
                                    insertEntity.setProjectElectricityStorageEnergyUsagePeak(list.stream().map(ProjectStaSubitemDayEntity::getProjectElectricityStorageEnergyUsagePeak).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add));
                                }
                                if (kpiCodeMap.containsKey("project.electricity.storageEnergyUsage.valley") && CollUtil.isNotEmpty(list)) {
                                    //谷充电量(根据谷时段配置，由小时表汇总得到) project.electricity.storageEnergyUsage.valley
                                    insertEntity.setProjectElectricityStorageEnergyUsageValley(list.stream().map(ProjectStaSubitemDayEntity::getProjectElectricityStorageEnergyUsageValley).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add));
                                }
                                if (kpiCodeMap.containsKey("project.electricity.storageEnergyUsage.flat") && CollUtil.isNotEmpty(list)) {
                                    //平充电量（根据平时段配置，由小时表汇总得到） project.electricity.storageEnergyUsage.flat
                                    insertEntity.setProjectElectricityStorageEnergyUsageFlat(list.stream().map(ProjectStaSubitemDayEntity::getProjectElectricityStorageEnergyUsageFlat).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add));
                                }
                            }
                            if (kpiCodeMap.containsKey("project.electricity.storageEnergyUsagePv.total") && CollUtil.isNotEmpty(list)) {
                                // 储光电量（由小时表汇总得到） project.electricity.storageEnergyUsagePv.total
                                if (BigDecimal.ZERO.compareTo(insertEntity.getProjectElectricityPccEnergyUsageTotal()) == 0) {
                                    insertEntity.setProjectElectricityStorageEnergyUsagePvTotal(null);
                                }
                                BigDecimal multiply = insertEntity.getProjectElectricityStorageEnergyUsageTotal().subtract(insertEntity.getProjectElectricityPccEnergyUsageTotal()).subtract(insertEntity.getProjectElectricityStorageEnergyProductionTotal());
                                if (BigDecimal.ZERO.compareTo(multiply) > 0) {
                                    insertEntity.setProjectElectricityStorageEnergyUsagePvTotal(BigDecimal.ZERO);
                                } else {
                                    insertEntity.setProjectElectricityStorageEnergyUsagePvTotal(multiply);
                                }
                            }
                            if (kpiCodeMap.containsKey("project.electricity.storageEnergyUsageGrid.total") && CollUtil.isNotEmpty(list)) {
                                // 储市电量（由小时表汇总得到） project.electricity.storageEnergyUsageGrid.total
                                BigDecimal decimal1 = insertEntity.getProjectElectricityStorageEnergyUsageTotal();
                                BigDecimal decimal2 = insertEntity.getProjectElectricityStorageEnergyUsagePvTotal();
                                if (Objects.isNull(decimal2) || Objects.isNull(decimal1)) {
                                    insertEntity.setProjectElectricityStorageEnergyUsageGridTotal(null);
                                } else {
                                    BigDecimal bigDecimal = decimal1.subtract(decimal2);
                                    if (BigDecimal.ZERO.compareTo(bigDecimal) > 0) {
                                        insertEntity.setProjectElectricityStorageEnergyUsageGridTotal(BigDecimal.ZERO);
                                    } else {
                                        insertEntity.setProjectElectricityStorageEnergyUsageGridTotal(bigDecimal);
                                    }
                                }
                            }
                            if (kpiCodeMap.containsKey("project.electricity.storageEnergyProduction.total") && CollUtil.isNotEmpty(list)) {
                                // 储放电量（由小时表汇总得到） project.electricity.storageEnergyProduction.total
                                if (CollUtil.isEmpty(deviceList)) {
                                    insertEntity.setProjectElectricityStorageEnergyProductionTotal(BigDecimal.ZERO);
                                } else {
                                    var val = deviceList.stream().filter(j -> monthDeviceObj.containsKey(j.getDeviceId()) && monthDeviceObj.getJSONObject(j.getDeviceId()).containsKey("energymeterEpexportTotal"))
                                            .map(j -> monthDeviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("energymeterEpexportTotal").multiply(new BigDecimal(j.getComputeTag())))
                                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                                    var val1 = deviceList.stream().filter(j -> monthDeviceObj.containsKey(j.getDeviceId()) && monthDeviceObj.getJSONObject(j.getDeviceId()).containsKey("gscnEpexportTotal"))
                                            .map(j -> monthDeviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("gscnEpexportTotal").multiply(new BigDecimal(j.getComputeTag())))
                                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                                    var val2 = deviceList.stream().filter(j -> monthDeviceObj.containsKey(j.getDeviceId()) && monthDeviceObj.getJSONObject(j.getDeviceId()).containsKey("znbEpexportTotal"))
                                            .map(j -> monthDeviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("znbEpexportTotal").multiply(new BigDecimal(j.getComputeTag())))
                                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                                    insertEntity.setProjectElectricityStorageEnergyProductionTotal(val.add(val1).add(val2));
                                }
                            }
                            if (null != electricityPriceEntity && ElectricityPriceTypeEnum.TOU.getType().equals(electricityPriceEntity.getType())) {
                                if (kpiCodeMap.containsKey("project.electricity.storageEnergyProduction.tip") && CollUtil.isNotEmpty(list)) {
                                    //尖放电量(根据尖时段配置，由小时表汇总得到。) project.electricity.storageEnergyProduction.tip
                                    insertEntity.setProjectElectricityStorageEnergyProductionTip(list.stream().map(ProjectStaSubitemDayEntity::getProjectElectricityStorageEnergyProductionTip).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add));
                                }
                                if (kpiCodeMap.containsKey("project.electricity.storageEnergyProduction.peak") && CollUtil.isNotEmpty(list)) {
                                    //峰放电量(根据峰时段配置，由小时表汇总得到) project.electricity.storageEnergyProduction.peak
                                    insertEntity.setProjectElectricityStorageEnergyProductionPeak(list.stream().map(ProjectStaSubitemDayEntity::getProjectElectricityStorageEnergyProductionPeak).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add));
                                }
                                if (kpiCodeMap.containsKey("project.electricity.storageEnergyProduction.valley") && CollUtil.isNotEmpty(list)) {
                                    //谷放电量(根据谷时段配置，由小时表汇总得到) project.electricity.storageEnergyProduction.valley
                                    insertEntity.setProjectElectricityStorageEnergyProductionValley(list.stream().map(ProjectStaSubitemDayEntity::getProjectElectricityStorageEnergyProductionValley).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add));
                                }
                                if (kpiCodeMap.containsKey("project.electricity.storageEnergyProduction.flat") && CollUtil.isNotEmpty(list)) {
                                    //平放电量(根据平时段配置，由小时表汇总得到) project.electricity.storageEnergyProduction.flat
                                    insertEntity.setProjectElectricityStorageEnergyProductionFlat(list.stream().map(ProjectStaSubitemDayEntity::getProjectElectricityStorageEnergyProductionFlat).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add));
                                }
                            }
                            if (kpiCodeMap.containsKey("project.electricity.storageEnergyNetFee.total") && CollUtil.isNotEmpty(list)) {
                                //储能净收益(sum(放电时段单价*放电时段电量）-sum(充电时段单价*充电时段电量))
                                insertEntity.setProjectElectricityStorageEnergyNetFeeTotal(list.stream().map(ProjectStaSubitemDayEntity::getProjectElectricityStorageEnergyNetFeeTotal).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add));
                            }
                            if (kpiCodeMap.containsKey("project.electricity.subChargeEnergy.total") && CollUtil.isNotEmpty(list)) {
                                // 充电桩总用电(由小时表汇总得到) project.electricity.subChargeEnergy.total
                                if (CollUtil.isEmpty(deviceList)) {
                                    insertEntity.setProjectElectricitySubChargeEnergyTotal(BigDecimal.ZERO);
                                }
                                var val = deviceList.stream().filter(j -> monthDeviceObj.containsKey(j.getDeviceId()) && monthDeviceObj.getJSONObject(j.getDeviceId()).containsKey("energymeterEpimportTotal"))
                                        .map(j -> monthDeviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("energymeterEpimportTotal").multiply(new BigDecimal(j.getComputeTag())))
                                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                                var val1 = deviceList.stream().filter(j -> monthDeviceObj.containsKey(j.getDeviceId()) && monthDeviceObj.getJSONObject(j.getDeviceId()).containsKey("gscnEpimportTotal"))
                                        .map(j -> monthDeviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("gscnEpimportTotal").multiply(new BigDecimal(j.getComputeTag())))
                                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                                var val2 = deviceList.stream().filter(j -> monthDeviceObj.containsKey(j.getDeviceId()) && monthDeviceObj.getJSONObject(j.getDeviceId()).containsKey("znbEpimportTotal"))
                                        .map(j -> monthDeviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("znbEpimportTotal").multiply(new BigDecimal(j.getComputeTag())))
                                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                                insertEntity.setProjectElectricitySubChargeEnergyTotal(val.add(val1).add(val2));
                            }
                            if (kpiCodeMap.containsKey("project.electricity.subChargeEnergyChargeFee.total") && CollUtil.isNotEmpty(list)) {
                                // 充电总费用(电度费+服务费；计费模式及服务费配置到数据库中。) project.electricity.subChargeEnergyFee.total
                                insertEntity.setProjectElectricitySubChargeEnergyChargeFeeTotal(list.stream().map(ProjectStaSubitemDayEntity::getProjectElectricitySubChargeEnergyChargeFeeTotal).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add));
                            }
                            if (kpiCodeMap.containsKey("project.electricity.subChargeEnergyServiceFee.total") && CollUtil.isNotEmpty(list)) {
                                // 上网收益(由上网电量*上网电价) project.electricity.pccEnergyProductionFee.total
                                insertEntity.setProjectElectricitySubChargeEnergyServiceFeeTotal(list.stream().map(ProjectStaSubitemDayEntity::getProjectElectricitySubChargeEnergyServiceFeeTotal).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add));
                            }
                            if (kpiCodeMap.containsKey("project_electricity_pccenergyproductionfee_total") && CollUtil.isNotEmpty(list)) {
                                // 上网收益(由上网电量*上网电价) project.electricity.pccEnergyProductionFee.total
                                insertEntity.setProjectElectricityPccEnergyProductionFeeTotal(list.stream().map(ProjectStaSubitemDayEntity::getProjectElectricityPccEnergyProductionFeeTotal).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add));
                            }
                            if (kpiCodeMap.containsKey("project.electricity.subChargeEnergyFee.total") && CollUtil.isNotEmpty(list)) {
                                // 服务费(总电量*服务费单价) project.electricity.subChargeEnergyServiceFee.total
                                insertEntity.setProjectElectricitySubChargeEnergyFeeTotal(insertEntity.getProjectElectricitySubChargeEnergyChargeFeeTotal()
                                        .add(insertEntity.getProjectElectricitySubChargeEnergyServiceFeeTotal()));
                            }
                            if (kpiCodeMap.containsKey("project.electricity.subLightingEnergy.total") && CollUtil.isNotEmpty(list)) {
                                //照明用电电量 project.electricity.subLightingEnergy.total
                                if (CollUtil.isEmpty(deviceList)) {
                                    insertEntity.setProjectElectricitySubLightingEnergyTotal(BigDecimal.ZERO);
                                }
                                var val = deviceList.stream().filter(j -> monthDeviceObj.containsKey(j.getDeviceId()) && monthDeviceObj.getJSONObject(j.getDeviceId()).containsKey("energymeterEpimportTotal"))
                                        .map(j -> monthDeviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("energymeterEpimportTotal").multiply(new BigDecimal(j.getComputeTag())))
                                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                                var val1 = deviceList.stream().filter(j -> monthDeviceObj.containsKey(j.getDeviceId()) && monthDeviceObj.getJSONObject(j.getDeviceId()).containsKey("gscnEpimportTotal"))
                                        .map(j -> monthDeviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("gscnEpimportTotal").multiply(new BigDecimal(j.getComputeTag())))
                                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                                var val2 = deviceList.stream().filter(j -> monthDeviceObj.containsKey(j.getDeviceId()) && monthDeviceObj.getJSONObject(j.getDeviceId()).containsKey("znbEpimportTotal"))
                                        .map(j -> monthDeviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("znbEpimportTotal").multiply(new BigDecimal(j.getComputeTag())))
                                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                                insertEntity.setProjectElectricitySubLightingEnergyTotal(val.add(val1).add(val2));
                            }
                            if (kpiCodeMap.containsKey("project.electricity.subSocketEnergy.total") && CollUtil.isNotEmpty(list)) {
                                //插座用电电量 project.electricity.subSocketEnergy.total
                                if (CollUtil.isEmpty(deviceList)) {
                                    insertEntity.setProjectElectricitySubSocketEnergyTotal(BigDecimal.ZERO);
                                }
                                var val = deviceList.stream().filter(j -> monthDeviceObj.containsKey(j.getDeviceId()) && monthDeviceObj.getJSONObject(j.getDeviceId()).containsKey("energymeterEpimportTotal"))
                                        .map(j -> monthDeviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("energymeterEpimportTotal").multiply(new BigDecimal(j.getComputeTag())))
                                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                                var val1 = deviceList.stream().filter(j -> monthDeviceObj.containsKey(j.getDeviceId()) && monthDeviceObj.getJSONObject(j.getDeviceId()).containsKey("gscnEpimportTotal"))
                                        .map(j -> monthDeviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("gscnEpimportTotal").multiply(new BigDecimal(j.getComputeTag())))
                                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                                var val2 = deviceList.stream().filter(j -> monthDeviceObj.containsKey(j.getDeviceId()) && monthDeviceObj.getJSONObject(j.getDeviceId()).containsKey("znbEpimportTotal"))
                                        .map(j -> monthDeviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("znbEpimportTotal").multiply(new BigDecimal(j.getComputeTag())))
                                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                                insertEntity.setProjectElectricitySubSocketEnergyTotal(val.add(val1).add(val2));
                            }
                            if (kpiCodeMap.containsKey("project.carbon.pvReductionCoal.total") && CollUtil.isNotEmpty(list)) {
                                // 标准煤(CO2减排量=光伏发电量 * CO2排放因子 排放因子同用电的排放因子。 标准煤、SO2、粉尘根据CO2*系数得到。系数同之前相同。) project.carbon.pvReductionCoal.total
                                insertEntity.setProjectCarbonPvReductionCoalTotal(list.stream().map(ProjectStaSubitemDayEntity::getProjectCarbonPvReductionCoalTotal).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add));
                            }
                            if (kpiCodeMap.containsKey("project.carbon.pvReductionCO2.total") && CollUtil.isNotEmpty(list)) {
                                // 标准煤(CO2减排量=光伏发电量 * CO2排放因子 排放因子同用电的排放因子。 标准煤、SO2、粉尘根据CO2*系数得到。系数同之前相同。) project.carbon.pvReductionCoal.total
                                insertEntity.setProjectCarbonPvReductionCO2Total(list.stream().map(ProjectStaSubitemDayEntity::getProjectCarbonPvReductionCO2Total).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add));
                            }
                            if (kpiCodeMap.containsKey("project.carbon.pvReductionSO2.total") && CollUtil.isNotEmpty(list)) {
                                // 标准煤(CO2减排量=光伏发电量 * CO2排放因子 排放因子同用电的排放因子。 标准煤、SO2、粉尘根据CO2*系数得到。系数同之前相同。) project.carbon.pvReductionCoal.total
                                insertEntity.setProjectCarbonPvReductionSO2Total(list.stream().map(ProjectStaSubitemDayEntity::getProjectCarbonPvReductionSO2Total).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add));
                            }
                            if (kpiCodeMap.containsKey("project.carbon.pvReductionDust.total") && CollUtil.isNotEmpty(list)) {
                                // 标准煤(CO2减排量=光伏发电量 * CO2排放因子 排放因子同用电的排放因子。 标准煤、SO2、粉尘根据CO2*系数得到。系数同之前相同。) project.carbon.pvReductionCoal.total
                                insertEntity.setProjectCarbonPvReductionDustTotal(list.stream().map(ProjectStaSubitemDayEntity::getProjectCarbonPvReductionDustTotal).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add));
                            }
                        }

                        //需要计算的指标
                        insertEntity.setProjectGasFeeTotal(insertEntity.getProjectGasUsageTotal().multiply(new BigDecimal("4.88")));
                        insertEntity.setProjectWaterFeeWater(insertEntity.getProjectWaterUsageTotal().multiply(new BigDecimal("3.32")));
                        insertEntity.setProjectWaterFeeSewerage(insertEntity.getProjectWaterUsageTotal().multiply(new BigDecimal("0.9")).multiply(new BigDecimal("2.97")));
                        insertEntity.setProjectWaterFeeTotal(insertEntity.getProjectWaterFeeWater().add(insertEntity.getProjectWaterFeeSewerage()));

                        if (totalKpi.contains("project.carbon.electricityUsageCO2.total")) {
                            insertEntity.setProjectCarbonElectricityusageco2Total(insertEntity.getProjectElectricityEnergyusageTotal().multiply(new BigDecimal("0.00042")));
                        }

                        if (totalKpi.contains("project.carbon.electricityUsageCoal.total")) {
                            insertEntity.setProjectCarbonElectricityusagecoalTotal(insertEntity.getProjectCarbonElectricityusageco2Total().multiply(new BigDecimal("0.4012")));
                        }

                        if (totalKpi.contains("project.carbon.electricityUsageSO2.total")) {
                            insertEntity.setProjectCarbonElectricityusageso2Total(insertEntity.getProjectCarbonElectricityusageco2Total().multiply(new BigDecimal("0.0301")));
                        }

                        if (totalKpi.contains("project.carbon.electricityUsageDust.total")) {
                            insertEntity.setProjectCarbonElectricityusagedustTotal(insertEntity.getProjectCarbonElectricityusageco2Total().multiply(new BigDecimal("0.2782")));
                        }

                        if (totalKpi.contains("project.carbon.gasUsageCO2.total")) {
                            insertEntity.setProjectCarbonGasusageco2Total(insertEntity.getProjectGasUsageTotal().multiply(new BigDecimal("0.00218")));
                        }

                        if (totalKpi.contains("project.carbon.gasUsageCoal.total")) {
                            insertEntity.setProjectCarbonGasusagecoalTotal(insertEntity.getProjectCarbonElectricityusagedustTotal().multiply(new BigDecimal("0.4012")));
                        }
                        if (totalKpi.contains("project.carbon.waterUsageCO2.total")) {
                            insertEntity.setProjectCarbonWaterusageco2Total(insertEntity.getProjectWaterUsageTotal().multiply(new BigDecimal("0.00185")));
                        }

                        if (totalKpi.contains("project.carbon.waterUsageCoal.total")) {
                            insertEntity.setProjectCarbonWaterusagecoalTotal(insertEntity.getProjectCarbonWaterusageco2Total().multiply(new BigDecimal("0.4012")));
                        }

                        if (totalKpi.contains("project.carbon.waterUsageSO2.total")) {
                            insertEntity.setProjectCarbonWaterusageso2Total(insertEntity.getProjectCarbonWaterusageco2Total().multiply(new BigDecimal("0.0301")));
                        }

                        if (totalKpi.contains("project.carbon.waterUsageDust.total")) {
                            insertEntity.setProjectCarbonWaterusagedustTotal(insertEntity.getProjectCarbonWaterusageco2Total().multiply(new BigDecimal("0.2782")));
                        }

                        if (totalKpi.contains("project.carbon.pvReductionCO2.total") && CollUtil.isNotEmpty(list)) {
                            // CO2(CO2减排量=光伏发电量 * CO2排放因子 排放因子同用电的排放因子。 标准煤、SO2、粉尘根据CO2*系数得到。系数同之前相同。)
                            insertEntity.setProjectCarbonPvReductionCO2Total(insertEntity.getProjectElectricityPvEnergyProductionTotal().multiply(new BigDecimal("0.00042")));
                        }
                        if (totalKpi.contains("project.carbon.pvReductionCoal.total") && CollUtil.isNotEmpty(list)) {
                            // CO2(CO2减排量=光伏发电量 * CO2排放因子 排放因子同用电的排放因子。 标准煤、SO2、粉尘根据CO2*系数得到。系数同之前相同。)
                            insertEntity.setProjectCarbonPvReductionCoalTotal(insertEntity.getProjectCarbonPvReductionCO2Total().multiply(new BigDecimal("0.4012")));
                        }
                        if (totalKpi.contains("project.carbon.pvReductionSO2.total") && CollUtil.isNotEmpty(list)) {
                            // CO2(CO2减排量=光伏发电量 * CO2排放因子 排放因子同用电的排放因子。 标准煤、SO2、粉尘根据CO2*系数得到。系数同之前相同。)
                            insertEntity.setProjectCarbonPvReductionSO2Total(insertEntity.getProjectCarbonPvReductionCO2Total().multiply(new BigDecimal("0.0301")));
                        }
                        if (totalKpi.contains("project.carbon.pvReductionDust.total") && CollUtil.isNotEmpty(list)) {
                            // CO2(CO2减排量=光伏发电量 * CO2排放因子 排放因子同用电的排放因子。 标准煤、SO2、粉尘根据CO2*系数得到。系数同之前相同。)
                            insertEntity.setProjectCarbonPvReductionDustTotal(insertEntity.getProjectCarbonPvReductionCO2Total().multiply(new BigDecimal("0.2782")));
                        }

                        if (totalKpi.contains("project.electricity.subHAVCEnergy.avgSq") && CollUtil.isNotEmpty(list)) {
                            // 空调总用电/项目建筑面积
                            insertEntity.setProjectElectricitySubhavcenergyAvgsq(insertEntity.getProjectElectricitySubhavcenergyTotal().divide(projInfo.getArea(), 2, RoundingMode.HALF_UP));
                        }
                        if (totalKpi.contains("project.electricity.subHeatingWaterEnergy.avgCube") && CollUtil.isNotEmpty(list)) {
                            // 热水总用电量/热水补水量
                            if (insertEntity.getProjectWaterHeatingwaterusageTotal() == null || insertEntity.getProjectWaterHeatingwaterusageTotal().compareTo(BigDecimal.ZERO) == 0) {
                                insertEntity.setProjectElectricitySubheatingwaterenergyAvgcube(BigDecimal.ZERO);
                            } else {
                                insertEntity.setProjectElectricitySubheatingwaterenergyAvgcube(null == insertEntity.getProjectElectricitySubheatingwaterenergyTotal() || 0 >= insertEntity.getProjectElectricitySubheatingwaterenergyTotal().intValue() ?
                                        BigDecimal.ZERO : insertEntity.getProjectElectricitySubheatingwaterenergyTotal().divide(insertEntity.getProjectWaterHeatingwaterusageTotal(), 2, RoundingMode.HALF_UP));
                            }
                        }

                        insertEntity.setProjectCarbonTotalco2Total(insertEntity.getProjectCarbonElectricityusageco2Total().add(insertEntity.getProjectCarbonWaterusageco2Total()).add(insertEntity.getProjectCarbonGasusageco2Total()));
                        insertEntity.setProjectCarbonTotalcoalTotal(insertEntity.getProjectCarbonElectricityusagecoalTotal().add(insertEntity.getProjectCarbonWaterusagecoalTotal()).add(insertEntity.getProjectCarbonGasusagecoalTotal()));
                        insertEntity.setProjectCarbonTotalso2Total(insertEntity.getProjectCarbonElectricityusageso2Total().add(insertEntity.getProjectCarbonWaterusageso2Total()).add(insertEntity.getProjectCarbonGasusageso2Total()));
                        insertEntity.setProjectCarbonTotaldustTotal(insertEntity.getProjectCarbonElectricityusagedustTotal().add(insertEntity.getProjectCarbonWaterusagedustTotal()).add(insertEntity.getProjectCarbonGasusagedustTotal()));

                        projectStaSubitemMonthServiceImpl.save(insertEntity);
                    } catch (Exception e) {
                        jobLogMap.get(tenantId).setStatus(JOB_EXEC_ERROR);
                        log.error("分项月项目执行异常", e);
                    }
                }
            } finally {
                saveJobLog(jobLogMap, tenantProjectMap);
            }
        }
        return true;
    }

    @Override
    public boolean statisticsByYear(String[] times, JobRpcRequest request) {
        List<LocalDateTime> timeList = new ArrayList<>();
        if (!ArrayUtil.isEmpty(times)) {
            // 年的时间，应该是yyyy
            for (String time : times) {
                timeList.add(LocalDateTime.parse(time + " 00", hourFormatter).withMonth(1).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0));
            }
        } else {
            // 仅有每年的12月和1月会执行此统计，且是12月23-28号到1月1-6号
            LocalDateTime now = LocalDateTime.now();
            if (1 != now.getMonthValue() && 12 != now.getMonthValue()) {
                return true;
            }
            if (1 == now.getMonthValue() && now.getDayOfMonth() > 7) {
                return true;
            }
            if (12 == now.getMonthValue() && (now.getDayOfMonth() < 24 || now.getDayOfMonth() > 29)) {
                return true;
            }
            timeList.add(now.minusYears(1).withHour(0).withMinute(0).withSecond(0).withNano(0));
        }
        // 获取需要分享处理的kpicode
        List<ProjectKpiConfigEntity> kpiConfList = projectKpiConfigServiceImpl.list(new QueryWrapper<ProjectKpiConfigEntity>().lambda().like(ProjectKpiConfigEntity::getCode, "project%").eq(ProjectKpiConfigEntity::getStaIntervalYmd, 1));

        if (CollectionUtils.isEmpty(kpiConfList)) {
            // 不需要统计， 返回即可
            return true;
        }
        Map<String, List<ProjectKpiConfigEntity>> kpiConfigMap = kpiConfList.stream().collect(Collectors.groupingBy(ProjectKpiConfigEntity::getKpiSubtype));
//                kpiConfList.stream().collect(Collectors.groupingBy(ProjectKpiConfigEntity::getTenantId)).entrySet()
//                        .stream().collect(Collectors.toMap(Map.Entry::getKey,
//                                v -> v.getValue().stream().collect(Collectors.groupingBy(ProjectKpiConfigEntity::getKpiSubtype))));

        // 通过kpiSubtype查找对应的subitem的配置
        List<String> kpiSubtypeList = kpiConfList.stream().map(ProjectKpiConfigEntity::getKpiSubtype).filter(StrUtil::isNotEmpty).distinct().collect(Collectors.toList());
        List<ProjectCnfSubitemEntity> subitemConfList = projectCnfSubitemServiceImpl.list(new QueryWrapper<ProjectCnfSubitemEntity>().lambda().in(ProjectCnfSubitemEntity::getKpiSubtype, kpiSubtypeList));

        if (CollectionUtils.isEmpty(subitemConfList)) {
            // 不需要统计， 返回即可
            return true;
        }

        Map<String, List<ProjectCnfSubitemEntity>> projSubitemConfMap = subitemConfList.stream().collect(Collectors.groupingBy(ProjectCnfSubitemEntity::getProjectId));
        List<Long> subitemIdList = subitemConfList.stream().map(ProjectCnfSubitemEntity::getId).collect(Collectors.toList());

        // 获取配置的设备列表
        List<ProjectSubitemDeviceEntity> deviceConfList = projectSubitemDeviceServiceImpl.list(new QueryWrapper<ProjectSubitemDeviceEntity>().lambda().in(ProjectSubitemDeviceEntity::getSubitemId, subitemIdList));

        Map<Long, Map<Long, List<ProjectSubitemDeviceEntity>>> deviceConfMap = deviceConfList.stream().collect(Collectors.groupingBy(ProjectSubitemDeviceEntity::getTenantId, Collectors.groupingBy(ProjectSubitemDeviceEntity::getSubitemId)));

        // 通过时间，处理对应信息
        for (LocalDateTime time : timeList) {
            Map<Long, JobLogSaveDTO> jobLogMap = new HashMap<>(8);
            Map<Long, Map<String, String>> tenantProjectMap = new HashMap<>(8);
            int currentYear = time.getYear();
            if (time.getMonthValue() == 12) {
                currentYear += 1;
            }


            List<ProjectStaSubitemMonthEntity> records = projectStaSubitemMonthServiceImpl.list(new QueryWrapper<ProjectStaSubitemMonthEntity>().lambda().eq(ProjectStaSubitemMonthEntity::getYear, String.valueOf(currentYear)));
            Map<String, List<ProjectStaSubitemMonthEntity>> recordsMap = records.stream().collect(Collectors.groupingBy(ProjectStaSubitemMonthEntity::getBizProjectId));

            // 如果当前时间，已有对应的记录，删掉重新出
            String bizProjectId = null;
            Long tenantId = null;
            Map<String, List<ProjectKpiConfigEntity>> kpiConfDetailMap;
            try {
                for (Map.Entry<String, List<ProjectCnfSubitemEntity>> entry : projSubitemConfMap.entrySet()) {
                    bizProjectId = entry.getValue().get(0).getProjectId();
                    tenantId = entry.getValue().get(0).getTenantId();

                    // modify by hebin. 这个步骤提前， 如果没有对应的分区设备的配置， 直接continue，节省性能
                    if (!deviceConfMap.containsKey(tenantId)) {
                        log.info("当前租户没有对应的配置，给一个空配置， 租户编号为：{}", tenantId);
                        deviceConfMap.put(tenantId, new HashMap<>());
                    }

                    List<String> totalKpi = new ArrayList<>();
                    // 获取项目信息
                    Response<ProjectDetailsResponse> projectResp = projectApi.getProjectDetails(bizProjectId);
                    if (!projectResp.isSuccess() || null == projectResp.getResult()) {
                        log.error("获取项目信息失败, 项目编号为: {}", bizProjectId);
                        continue;
                    }
                    ProjectDetailsResponse projInfo = projectResp.getResult();

                    // 获取租户信息
                    Response<TenantInfoResponse> tenantResp = tenantApi.getTenantInfo(tenantId);
                    if (!tenantResp.isSuccess() || null == tenantResp.getResult()) {
                        log.error("获取租户信息失败, 租户编号为: {}", tenantId);
                        continue;
                    }
                    TenantInfoResponse tenantInfo = tenantResp.getResult();

                    // 2023-11-16 增加手动执行过滤条件
                    if (null != request.getTenantId() && !Objects.equals(tenantId, request.getTenantId())) {
                        continue;
                    }

                    if (CollectionUtil.isNotEmpty(request.getProjectList()) && !request.getProjectList().contains(bizProjectId)) {
                        continue;
                    }

                    // 如果月报周期与当前时间不一致，返回
                    if (!String.valueOf(time.getDayOfMonth() - 1).equals(tenantInfo.getReportingCycle())) {
                        continue;
                    }

                    projectStaSubitemYearServiceImpl.remove(new QueryWrapper<ProjectStaSubitemYearEntity>().lambda().eq(ProjectStaSubitemYearEntity::getBizProjectId, bizProjectId).eq(ProjectStaSubitemYearEntity::getYear, String.valueOf(currentYear)));

                    // 2023-11-15 增加日志记录
                    if (!jobLogMap.containsKey(tenantId)) {
                        JobLogSaveDTO jobLog = new JobLogSaveDTO();
                        jobLog.setJobId(request.getJobId())
                                .setTenantId(tenantId)
                                .setStatus(JOB_EXEC_SUCCESS)
                                .setExecTime(LocalDateTime.now())
                                .setExecType(request.getExecType())
                                .setExecUser(request.getExecUser());
                        jobLogMap.put(tenantId, jobLog);
                    }

                    if (!tenantProjectMap.containsKey(tenantId)) {
                        tenantProjectMap.put(tenantId, new HashMap<>(8));
                    }

                    if (!tenantProjectMap.get(tenantId).containsKey(bizProjectId)) {
                        tenantProjectMap.get(tenantId).put(bizProjectId, projInfo.getName());
                    }

                    ProjectCnfElectricityPriceEntity electricityPriceEntity = projectCnfElectricityPriceServiceImpl.selectByBizProjId(projInfo.getBizProjectId());

                    try {
                        // 通过日表累加获取结果
                        ProjectStaSubitemYearEntity insertEntity = new ProjectStaSubitemYearEntity();
                        insertEntity.setBizProjectId(bizProjectId);
                        insertEntity.setYear(String.valueOf(currentYear));
                        insertEntity.setProjectCode(projInfo.getCode());
                        insertEntity.setProjectName(projInfo.getName());
                        insertEntity.setTenantId(tenantId);
                        insertEntity.setTenantCode(tenantInfo.getCode());
                        insertEntity.setStaTime(Timestamp.valueOf(currentYear + "-01-01 00:00:00"));

                        insertEntity.setProjectGasFeeTotal(BigDecimal.ZERO);
                        insertEntity.setProjectGasUsageTotal(BigDecimal.ZERO);
                        insertEntity.setProjectWaterUsageTotal(BigDecimal.ZERO);
                        insertEntity.setProjectWaterFeeWater(BigDecimal.ZERO);
                        insertEntity.setProjectWaterFeeSewerage(BigDecimal.ZERO);
                        insertEntity.setProjectWaterFeeTotal(BigDecimal.ZERO);
                        insertEntity.setProjectElectricityEnergyusageFlat(BigDecimal.ZERO);
                        insertEntity.setProjectElectricityEnergyusageTotal(BigDecimal.ZERO);
                        insertEntity.setProjectElectricityEnergyusagefeeTotal(BigDecimal.ZERO);
                        insertEntity.setProjectElectricityEnergyusageTip(BigDecimal.ZERO);
                        insertEntity.setProjectElectricityEnergyusageValley(BigDecimal.ZERO);
                        insertEntity.setProjectElectricityEnergyusagePeak(BigDecimal.ZERO);
                        insertEntity.setProjectElectricitySubelevatorenergyTotal(BigDecimal.ZERO);
                        insertEntity.setProjectElectricitySubguestroomenergyTotal(BigDecimal.ZERO);
                        insertEntity.setProjectElectricitySubheatingwaterenergyTotal(BigDecimal.ZERO);
                        insertEntity.setProjectElectricitySubhavcenergyTotal(BigDecimal.ZERO);
                        insertEntity.setProjectElectricitySubpowersupplyenergyTotal(BigDecimal.ZERO);
                        insertEntity.setProjectElectricitySubothertypeTotal(BigDecimal.ZERO);
                        insertEntity.setProjectElectricitySubwatersupplyenergyTotal(BigDecimal.ZERO);
                        insertEntity.setProjectCarbonTotalcoalTotal(BigDecimal.ZERO);
                        insertEntity.setProjectCarbonTotaldustTotal(BigDecimal.ZERO);
                        insertEntity.setProjectCarbonTotalco2Total(BigDecimal.ZERO);
                        insertEntity.setProjectCarbonTotalso2Total(BigDecimal.ZERO);
                        insertEntity.setProjectCarbonGasusageco2Total(BigDecimal.ZERO);
                        insertEntity.setProjectCarbonGasusagecoalTotal(BigDecimal.ZERO);
                        insertEntity.setProjectCarbonGasusagedustTotal(BigDecimal.ZERO);
                        insertEntity.setProjectCarbonGasusageso2Total(BigDecimal.ZERO);
                        insertEntity.setProjectCarbonElectricityusageso2Total(BigDecimal.ZERO);
                        insertEntity.setProjectCarbonElectricityusagedustTotal(BigDecimal.ZERO);
                        insertEntity.setProjectCarbonElectricityusageco2Total(BigDecimal.ZERO);
                        insertEntity.setProjectCarbonElectricityusagecoalTotal(BigDecimal.ZERO);
                        insertEntity.setProjectCarbonWaterusagedustTotal(BigDecimal.ZERO);
                        insertEntity.setProjectCarbonWaterusagecoalTotal(BigDecimal.ZERO);
                        insertEntity.setProjectCarbonWaterusageco2Total(BigDecimal.ZERO);
                        insertEntity.setProjectCarbonWaterusageso2Total(BigDecimal.ZERO);
                        insertEntity.setProjectEnvironmentOutTempAvg(BigDecimal.ZERO);
                        insertEntity.setProjectEnvironmentOutTempMax(BigDecimal.ZERO);
                        insertEntity.setProjectEnvironmentOutTempMin(BigDecimal.ZERO);
                        insertEntity.setProjectEnvironmentOutTumidityAvg(BigDecimal.ZERO);
                        insertEntity.setProjectElectricityPccEnergyUsageTotal(BigDecimal.ZERO);
                        insertEntity.setProjectElectricityPccEnergyUsageFeeTotal(BigDecimal.ZERO);
                        insertEntity.setProjectElectricityPccEnergyUsageTip(BigDecimal.ZERO);
                        insertEntity.setProjectElectricityPccEnergyUsagePeak(BigDecimal.ZERO);
                        insertEntity.setProjectElectricityPccEnergyUsageValley(BigDecimal.ZERO);
                        insertEntity.setProjectElectricityPccEnergyUsageFlat(BigDecimal.ZERO);
                        insertEntity.setProjectElectricityPccEnergyProductionTotal(BigDecimal.ZERO);
                        insertEntity.setProjectElectricityPccEnergyProductionFeeTotal(BigDecimal.ZERO);
                        insertEntity.setProjectElectricityPvEnergyProductionTotal(BigDecimal.ZERO);
                        insertEntity.setProjectElectricityPvEnergyProductionGridTotal(BigDecimal.ZERO);
                        insertEntity.setProjectElectricityPvEnergyProductionLoadTotal(BigDecimal.ZERO);
                        insertEntity.setProjectElectricityPvEnergyProductionStorageTotal(BigDecimal.ZERO);
                        insertEntity.setProjectElectricityPvEnergyProductionFeeTotal(BigDecimal.ZERO);
                        insertEntity.setProjectElectricityPvEnergyProductionGridFeeTotal(BigDecimal.ZERO);
                        insertEntity.setProjectElectricityPvEnergyProductionUsageFeeTotal(BigDecimal.ZERO);
                        insertEntity.setProjectElectricityStorageEnergyUsageTotal(BigDecimal.ZERO);
                        insertEntity.setProjectElectricityStorageEnergyUsageTip(BigDecimal.ZERO);
                        insertEntity.setProjectElectricityStorageEnergyUsagePeak(BigDecimal.ZERO);
                        insertEntity.setProjectElectricityStorageEnergyUsageValley(BigDecimal.ZERO);
                        insertEntity.setProjectElectricityStorageEnergyUsageFlat(BigDecimal.ZERO);
                        insertEntity.setProjectElectricityStorageEnergyUsagePvTotal(BigDecimal.ZERO);
                        insertEntity.setProjectElectricityStorageEnergyUsageGridTotal(BigDecimal.ZERO);
                        insertEntity.setProjectElectricityStorageEnergyProductionTotal(BigDecimal.ZERO);
                        insertEntity.setProjectElectricityStorageEnergyProductionTip(BigDecimal.ZERO);
                        insertEntity.setProjectElectricityStorageEnergyProductionPeak(BigDecimal.ZERO);
                        insertEntity.setProjectElectricityStorageEnergyProductionValley(BigDecimal.ZERO);
                        insertEntity.setProjectElectricityStorageEnergyProductionFlat(BigDecimal.ZERO);
                        insertEntity.setProjectElectricityStorageEnergyNetFeeTotal(BigDecimal.ZERO);
                        insertEntity.setProjectElectricitySubChargeEnergyTotal(BigDecimal.ZERO);
                        insertEntity.setProjectElectricitySubChargeEnergyFeeTotal(BigDecimal.ZERO);
                        insertEntity.setProjectElectricitySubChargeEnergyChargeFeeTotal(BigDecimal.ZERO);
                        insertEntity.setProjectElectricitySubChargeEnergyServiceFeeTotal(BigDecimal.ZERO);
                        insertEntity.setProjectElectricitySubLightingEnergyTotal(BigDecimal.ZERO);
                        insertEntity.setProjectElectricitySubSocketEnergyTotal(BigDecimal.ZERO);
                        insertEntity.setProjectCarbonPvReductionCoalTotal(BigDecimal.ZERO);
                        insertEntity.setProjectCarbonPvReductionCO2Total(BigDecimal.ZERO);
                        insertEntity.setProjectCarbonPvReductionSO2Total(BigDecimal.ZERO);
                        insertEntity.setProjectCarbonPvReductionDustTotal(BigDecimal.ZERO);

                        List<ProjectStaSubitemMonthEntity> list = recordsMap.get(bizProjectId);
                        for (ProjectCnfSubitemEntity i : entry.getValue()) {
                            List<ProjectKpiConfigEntity> kpiList = kpiConfigMap.get(i.getKpiSubtype());
                            Map<String, ProjectKpiConfigEntity> kpiCodeMap = kpiList.stream().collect(Collectors.toMap(ProjectKpiConfigEntity::getCode, k -> k, (v1, v2) -> v1));
                            totalKpi.addAll(kpiCodeMap.keySet().stream().toList());

                            Map<Long, List<ProjectSubitemDeviceEntity>> deviceConfDetailMap = deviceConfMap.get(tenantId);
                            List<ProjectSubitemDeviceEntity> deviceList = deviceConfDetailMap.get(i.getId());
                            Map<String, Object> yearDeviceStatus = Maps.newHashMap();
                            if (!CollectionUtils.isEmpty(deviceList)) {
                                List<String> deviceIds = deviceList.stream().map(ProjectSubitemDeviceEntity::getDeviceId).distinct().collect(Collectors.toList());
                                // 查询设备的小时信息，从4张表查
                                List<ProjectStaDeviceAirYearEntity> airList = projectStaDeviceAirYearServiceImpl.list(new QueryWrapper<ProjectStaDeviceAirYearEntity>().lambda().in(ProjectStaDeviceAirYearEntity::getBizDeviceId, deviceIds).eq(ProjectStaDeviceAirYearEntity::getYear, String.valueOf(currentYear)));
                                if (!CollectionUtils.isEmpty(airList)) {
                                    yearDeviceStatus.putAll(airList.stream().collect(Collectors.toMap(ProjectStaDeviceAirYearEntity::getBizDeviceId, j -> j, (v1, v2) -> v1)));
                                }
                                List<ProjectStaDeviceElectricityYearEntity> electricityList = projectStaDeviceElectricityYearServiceImpl.list(new QueryWrapper<ProjectStaDeviceElectricityYearEntity>().lambda().in(ProjectStaDeviceElectricityYearEntity::getBizDeviceId, deviceIds).eq(ProjectStaDeviceElectricityYearEntity::getYear, String.valueOf(currentYear)));
                                if (!CollectionUtils.isEmpty(electricityList)) {
                                    yearDeviceStatus.putAll(electricityList.stream().collect(Collectors.toMap(ProjectStaDeviceElectricityYearEntity::getBizDeviceId, j -> j, (v1, v2) -> v1)));
                                }
                                List<ProjectStaDeviceWaterYearEntity> waterList = projectStaDeviceWaterYearServiceImpl.list(new QueryWrapper<ProjectStaDeviceWaterYearEntity>().lambda().in(ProjectStaDeviceWaterYearEntity::getBizDeviceId, deviceIds).eq(ProjectStaDeviceWaterYearEntity::getYear, String.valueOf(currentYear)));
                                if (!CollectionUtils.isEmpty(waterList)) {
                                    yearDeviceStatus.putAll(waterList.stream().collect(Collectors.toMap(ProjectStaDeviceWaterYearEntity::getBizDeviceId, j -> j, (v1, v2) -> v1)));
                                }
                                List<ProjectStaDeviceGasYearEntity> gasList = projectStaDeviceGasYearServiceImpl.list(new QueryWrapper<ProjectStaDeviceGasYearEntity>().lambda().in(ProjectStaDeviceGasYearEntity::getBizDeviceId, deviceIds).eq(ProjectStaDeviceGasYearEntity::getYear, String.valueOf(currentYear)));
                                if (!CollectionUtils.isEmpty(gasList)) {
                                    yearDeviceStatus.putAll(gasList.stream().collect(Collectors.toMap(ProjectStaDeviceGasYearEntity::getBizDeviceId, j -> j, (v1, v2) -> v1)));
                                }
                                List<ProjectStaDeviceGscnYearEntity> gascnList = projectStaDeviceGscnYearMapper.selectList(Wrappers.<ProjectStaDeviceGscnYearEntity>lambdaQuery().in(ProjectStaDeviceGscnYearEntity::getBizDeviceId, deviceIds).eq(ProjectStaDeviceGscnYearEntity::getYear, String.valueOf(currentYear)));
                                if (!CollectionUtils.isEmpty(gascnList)) {
                                    yearDeviceStatus.putAll(gascnList.stream().collect(Collectors.toMap(ProjectStaDeviceGscnYearEntity::getBizDeviceId, j -> j, (v1, v2) -> v1)));
                                }
                                List<ProjectStaDeviceZnbYearEntity> znbList = projectStaDeviceZnbYearMapper.selectList(Wrappers.<ProjectStaDeviceZnbYearEntity>lambdaQuery().in(ProjectStaDeviceZnbYearEntity::getBizDeviceId, deviceIds).eq(ProjectStaDeviceZnbYearEntity::getYear, String.valueOf(currentYear)));
                                if (!CollectionUtils.isEmpty(znbList)) {
                                    yearDeviceStatus.putAll(znbList.stream().collect(Collectors.toMap(ProjectStaDeviceZnbYearEntity::getBizDeviceId, j -> j, (v1, v2) -> v1)));
                                }
                            } else {
                                deviceList = Lists.newArrayList();
                            }
                            JSONObject yearDeviceObj = JSONObject.parseObject(JSON.toJSONString(yearDeviceStatus));

                            // 环境相关
                            if (kpiCodeMap.containsKey("project.environment.outTemp.avg") && CollUtil.isNotEmpty(list)) {
                                //平均温度
                                List<BigDecimal> tempAvg = list.stream().map(ProjectStaSubitemMonthEntity::getProjectEnvironmentOutTempAvg).filter(Objects::nonNull).toList();
                                BigDecimal total = tempAvg.stream().reduce(BigDecimal.ZERO, BigDecimal::add).divide(BigDecimal.valueOf(tempAvg.size()), 2, RoundingMode.HALF_UP);
                                insertEntity.setProjectEnvironmentOutTempAvg(total);
                            }
                            if (kpiCodeMap.containsKey("project.environment.outTemp.max") && CollUtil.isNotEmpty(list)) {
                                //最大温度
                                insertEntity.setProjectEnvironmentOutTempMax(list.stream().map(ProjectStaSubitemMonthEntity::getProjectEnvironmentOutTempMax).max(BigDecimal::compareTo).orElse(null));
                            }
                            if (kpiCodeMap.containsKey("project.environment.outTemp.min") && CollUtil.isNotEmpty(list)) {
                                //最小温度
                                insertEntity.setProjectEnvironmentOutTempMin(list.stream().map(ProjectStaSubitemMonthEntity::getProjectEnvironmentOutTempMin).min(BigDecimal::compareTo).orElse(null));
                            }
                            if (kpiCodeMap.containsKey("project.environment.outTumidity.avg") && CollUtil.isNotEmpty(list)) {
                                //平均湿度
                                List<BigDecimal> tumidityAvg = list.stream().map(ProjectStaSubitemMonthEntity::getProjectEnvironmentOutTumidityAvg).filter(Objects::nonNull).toList();
                                BigDecimal total = tumidityAvg.stream().reduce(BigDecimal.ZERO, BigDecimal::add).divide(BigDecimal.valueOf(tumidityAvg.size()), 2, RoundingMode.HALF_UP);
                                insertEntity.setProjectEnvironmentOutTumidityAvg(total);
                            }

                            if (kpiCodeMap.containsKey("project.gas.usage.total") && CollUtil.isNotEmpty(list)) {
                                // 用气量累加
                                if (CollUtil.isEmpty(deviceList)) {
                                    insertEntity.setProjectGasUsageTotal(BigDecimal.ZERO);
                                } else {
                                    var val = deviceList.stream().filter(j -> yearDeviceObj.containsKey(j.getDeviceId()) && yearDeviceObj.getJSONObject(j.getDeviceId()).containsKey("gasmeterUsageTotal")).map(j -> yearDeviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("gasmeterUsageTotal").multiply(new BigDecimal(j.getComputeTag()))).reduce(BigDecimal.ZERO, BigDecimal::add);
                                    insertEntity.setProjectGasUsageTotal(val);
                                }
                            }
                            // 燃气单价写死
                            // project.gas.usage.fee
                            insertEntity.setProjectGasFeeTotal(insertEntity.getProjectGasUsageTotal().multiply(new BigDecimal("4.88")));

                            if (kpiCodeMap.containsKey("project.water.usage.total") && CollUtil.isNotEmpty(list)) {
                                // 计算用水量，取值包含watermeter.usage.total的
                                if (CollUtil.isEmpty(deviceList)) {
                                    insertEntity.setProjectWaterUsageTotal(BigDecimal.ZERO);
                                } else {
                                    var val = deviceList.stream().filter(j -> yearDeviceObj.containsKey(j.getDeviceId()) && yearDeviceObj.getJSONObject(j.getDeviceId()).containsKey("watermeterUsageTotal")).map(j -> yearDeviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("watermeterUsageTotal").multiply(new BigDecimal(j.getComputeTag()))).reduce(BigDecimal.ZERO, BigDecimal::add);
                                    insertEntity.setProjectWaterUsageTotal(val);
                                }
                            }
                            if (kpiCodeMap.containsKey("project.water.HVACUsage.total") && CollUtil.isNotEmpty(list)) {
                                // 计算空调补水，取值包含watermeter.usage.total的
                                if (CollUtil.isEmpty(deviceList)) {
                                    insertEntity.setProjectWaterHvacusageTotal(BigDecimal.ZERO);
                                } else {
                                    var val = deviceList.stream().filter(j -> yearDeviceObj.containsKey(j.getDeviceId()) && yearDeviceObj.getJSONObject(j.getDeviceId()).containsKey("watermeterUsageTotal")).map(j -> yearDeviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("watermeterUsageTotal").multiply(new BigDecimal(j.getComputeTag()))).reduce(BigDecimal.ZERO, BigDecimal::add);
                                    insertEntity.setProjectWaterHvacusageTotal(val);
                                }
                            }
                            if (kpiCodeMap.containsKey("project.water.HeatingWaterUsage.total") && CollUtil.isNotEmpty(list)) {
                                // 用热水补水水量累加
                                if (CollUtil.isEmpty(deviceList)) {
                                    insertEntity.setProjectWaterHeatingwaterusageTotal(BigDecimal.ZERO);
                                } else {
                                    var val = deviceList.stream().filter(j -> yearDeviceObj.containsKey(j.getDeviceId()) && yearDeviceObj.getJSONObject(j.getDeviceId()).containsKey("watermeterUsageTotal")).map(j -> yearDeviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("watermeterUsageTotal").multiply(new BigDecimal(j.getComputeTag()))).reduce(BigDecimal.ZERO, BigDecimal::add);
                                    insertEntity.setProjectWaterHeatingwaterusageTotal(val);
                                }
                            }
//                    if (kpiCodeMap.containsKey("project.water.fee.water")) {
                            insertEntity.setProjectWaterFeeWater(insertEntity.getProjectWaterUsageTotal().multiply(new BigDecimal("3.32")));
//                    if (kpiCodeMap.containsKey("project.water.fee.sewerage")) {
                            insertEntity.setProjectWaterFeeSewerage(insertEntity.getProjectWaterUsageTotal().multiply(new BigDecimal("0.9")).multiply(new BigDecimal("2.97")));
//                    if (kpiCodeMap.containsKey("project.water.fee.total")) {
                            insertEntity.setProjectWaterFeeTotal(insertEntity.getProjectWaterFeeWater().add(insertEntity.getProjectWaterFeeSewerage()));

                            if (kpiCodeMap.containsKey("project.electricity.energyUsage.total") && CollUtil.isNotEmpty(list)) {
                                if (CollUtil.isEmpty(deviceList)) {
                                    insertEntity.setProjectElectricityEnergyusageTotal(BigDecimal.ZERO);
                                } else {
                                    var val = deviceList.stream().filter(j -> yearDeviceObj.containsKey(j.getDeviceId()) && yearDeviceObj.getJSONObject(j.getDeviceId()).containsKey("energymeterEpimportTotal")).map(j -> yearDeviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("energymeterEpimportTotal").multiply(new BigDecimal(j.getComputeTag()))).reduce(BigDecimal.ZERO, BigDecimal::add);
                                    insertEntity.setProjectElectricityEnergyusageTotal(val);
                                }
                            }
                            if (null != electricityPriceEntity && ElectricityPriceTypeEnum.TOU.getType().equals(electricityPriceEntity.getType())) {
                                if (kpiCodeMap.containsKey("project.electricity.energyUsage.flat") && CollUtil.isNotEmpty(list)) {
                                    BigDecimal total = list.stream().map(ProjectStaSubitemMonthEntity::getProjectElectricityEnergyusageFlat).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
                                    insertEntity.setProjectElectricityEnergyusageFlat(total);
                                }
                                if (kpiCodeMap.containsKey("project.electricity.energyUsage.tip") && CollUtil.isNotEmpty(list)) {
                                    BigDecimal total = list.stream().map(ProjectStaSubitemMonthEntity::getProjectElectricityEnergyusageTip).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
                                    insertEntity.setProjectElectricityEnergyusageTip(total);
                                }
                                if (kpiCodeMap.containsKey("project.electricity.energyUsage.valley") && CollUtil.isNotEmpty(list)) {
                                    BigDecimal total = list.stream().map(ProjectStaSubitemMonthEntity::getProjectElectricityEnergyusageValley).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
                                    insertEntity.setProjectElectricityEnergyusageValley(total);
                                }
                                if (kpiCodeMap.containsKey("project.electricity.energyUsage.peak") && CollUtil.isNotEmpty(list)) {
                                    BigDecimal total = list.stream().map(ProjectStaSubitemMonthEntity::getProjectElectricityEnergyusagePeak).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
                                    insertEntity.setProjectElectricityEnergyusagePeak(total);
                                }
                            }
                            if (kpiCodeMap.containsKey("project.electricity.energyUsageFee.total") && CollUtil.isNotEmpty(list)) {
                                BigDecimal total = list.stream().map(ProjectStaSubitemMonthEntity::getProjectElectricityEnergyusagefeeTotal).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
                                insertEntity.setProjectElectricityEnergyusagefeeTotal(total);
                            }

                            if (kpiCodeMap.containsKey("project.electricity.energyUsage.total") && CollUtil.isNotEmpty(list)) {
                                if (CollUtil.isEmpty(deviceList)) {
                                    insertEntity.setProjectElectricityEnergyusageTotal(BigDecimal.ZERO);
                                } else {
                                    var val = deviceList.stream().filter(j -> yearDeviceObj.containsKey(j.getDeviceId()) && yearDeviceObj.getJSONObject(j.getDeviceId()).containsKey("energymeterEpimportTotal")).map(j -> yearDeviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("energymeterEpimportTotal").multiply(new BigDecimal(j.getComputeTag()))).reduce(BigDecimal.ZERO, BigDecimal::add);
                                    insertEntity.setProjectElectricityEnergyusageTotal(val);
                                }
                            }

                            if (kpiCodeMap.containsKey("project.electricity.subHAVCEnergy.total") && CollUtil.isNotEmpty(list)) {
                                if (CollUtil.isEmpty(deviceList)) {
                                    insertEntity.setProjectElectricitySubhavcenergyTotal(BigDecimal.ZERO);
                                } else {
                                    var val = deviceList.stream().filter(j -> yearDeviceObj.containsKey(j.getDeviceId()) && yearDeviceObj.getJSONObject(j.getDeviceId()).containsKey("energymeterEpimportTotal")).map(j -> yearDeviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("energymeterEpimportTotal").multiply(new BigDecimal(j.getComputeTag()))).reduce(BigDecimal.ZERO, BigDecimal::add);
                                    insertEntity.setProjectElectricitySubhavcenergyTotal(val);
                                }
                            }

                            if (kpiCodeMap.containsKey("project.electricity.subPowerSupplyEnergy.total") && CollUtil.isNotEmpty(list)) {
                                if (CollUtil.isEmpty(deviceList)) {
                                    insertEntity.setProjectElectricitySubpowersupplyenergyTotal(BigDecimal.ZERO);
                                } else {
                                    var val = deviceList.stream().filter(j -> yearDeviceObj.containsKey(j.getDeviceId()) && yearDeviceObj.getJSONObject(j.getDeviceId()).containsKey("energymeterEpimportTotal")).map(j -> yearDeviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("energymeterEpimportTotal").multiply(new BigDecimal(j.getComputeTag()))).reduce(BigDecimal.ZERO, BigDecimal::add);
                                    insertEntity.setProjectElectricitySubpowersupplyenergyTotal(val);
                                }
                            }

                            if (kpiCodeMap.containsKey("project.electricity.subHeatingWaterEnergy.total") && CollUtil.isNotEmpty(list)) {
                                if (CollUtil.isEmpty(deviceList)) {
                                    insertEntity.setProjectElectricitySubheatingwaterenergyTotal(BigDecimal.ZERO);
                                } else {
                                    var val = deviceList.stream().filter(j -> yearDeviceObj.containsKey(j.getDeviceId()) && yearDeviceObj.getJSONObject(j.getDeviceId()).containsKey("energymeterEpimportTotal")).map(j -> yearDeviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("energymeterEpimportTotal").multiply(new BigDecimal(j.getComputeTag()))).reduce(BigDecimal.ZERO, BigDecimal::add);
                                    insertEntity.setProjectElectricitySubheatingwaterenergyTotal(val);
                                }
                            }

                            if (kpiCodeMap.containsKey("project.electricity.subWaterSupplyEnergy.total") && CollUtil.isNotEmpty(list)) {
                                if (CollUtil.isEmpty(deviceList)) {
                                    insertEntity.setProjectElectricitySubwatersupplyenergyTotal(BigDecimal.ZERO);
                                } else {
                                    var val = deviceList.stream().filter(j -> yearDeviceObj.containsKey(j.getDeviceId()) && yearDeviceObj.getJSONObject(j.getDeviceId()).containsKey("energymeterEpimportTotal")).map(j -> yearDeviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("energymeterEpimportTotal").multiply(new BigDecimal(j.getComputeTag()))).reduce(BigDecimal.ZERO, BigDecimal::add);
                                    insertEntity.setProjectElectricitySubwatersupplyenergyTotal(val);
                                }
                            }

                            if (kpiCodeMap.containsKey("project.electricity.subElevatorEnergy.total") && CollUtil.isNotEmpty(list)) {
                                if (CollUtil.isEmpty(deviceList)) {
                                    insertEntity.setProjectElectricitySubelevatorenergyTotal(BigDecimal.ZERO);
                                } else {
                                    var val = deviceList.stream().filter(j -> yearDeviceObj.containsKey(j.getDeviceId()) && yearDeviceObj.getJSONObject(j.getDeviceId()).containsKey("energymeterEpimportTotal")).map(j -> yearDeviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("energymeterEpimportTotal").multiply(new BigDecimal(j.getComputeTag()))).reduce(BigDecimal.ZERO, BigDecimal::add);
                                    insertEntity.setProjectElectricitySubelevatorenergyTotal(val);
                                }
                            }

                            if (kpiCodeMap.containsKey("project.electricity.subGuestRoomEnergy.total") && CollUtil.isNotEmpty(list)) {
                                if (CollUtil.isEmpty(deviceList)) {
                                    insertEntity.setProjectElectricitySubguestroomenergyTotal(BigDecimal.ZERO);
                                } else {
                                    var val = deviceList.stream().filter(j -> yearDeviceObj.containsKey(j.getDeviceId()) && yearDeviceObj.getJSONObject(j.getDeviceId()).containsKey("energymeterEpimportTotal")).map(j -> yearDeviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("energymeterEpimportTotal").multiply(new BigDecimal(j.getComputeTag()))).reduce(BigDecimal.ZERO, BigDecimal::add);
                                    insertEntity.setProjectElectricitySubguestroomenergyTotal(val);
                                }
                            }

                            if (kpiCodeMap.containsKey("project.electricity.subOtherType.total") && CollUtil.isNotEmpty(list)) {
                                if (CollUtil.isEmpty(deviceList)) {
                                    insertEntity.setProjectElectricitySubothertypeTotal(BigDecimal.ZERO);
                                } else {
                                    var val = deviceList.stream().filter(j -> yearDeviceObj.containsKey(j.getDeviceId()) && yearDeviceObj.getJSONObject(j.getDeviceId()).containsKey("energymeterEpimportTotal")).map(j -> yearDeviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("energymeterEpimportTotal").multiply(new BigDecimal(j.getComputeTag()))).reduce(BigDecimal.ZERO, BigDecimal::add);
                                    insertEntity.setProjectElectricitySubothertypeTotal(val);
                                }
                            }

                            if (kpiCodeMap.containsKey("project.carbon.electricityUsageCO2.total")) {
                                insertEntity.setProjectCarbonElectricityusageco2Total(insertEntity.getProjectElectricityEnergyusageTotal().multiply(new BigDecimal("0.00042")));
                            }

                            if (kpiCodeMap.containsKey("project.carbon.electricityUsageCoal.total")) {
                                insertEntity.setProjectCarbonElectricityusagecoalTotal(insertEntity.getProjectCarbonElectricityusageco2Total().multiply(new BigDecimal("0.4012")));
                            }

                            if (kpiCodeMap.containsKey("project.carbon.electricityUsageSO2.total")) {
                                insertEntity.setProjectCarbonElectricityusageso2Total(insertEntity.getProjectCarbonElectricityusageco2Total().multiply(new BigDecimal("0.0301")));
                            }

                            if (kpiCodeMap.containsKey("project.carbon.electricityUsageDust.total")) {
                                insertEntity.setProjectCarbonElectricityusagedustTotal(insertEntity.getProjectCarbonElectricityusageco2Total().multiply(new BigDecimal("0.2782")));
                            }

                            if (kpiCodeMap.containsKey("project.carbon.gasUsageCO2.total")) {
                                insertEntity.setProjectCarbonGasusageco2Total(insertEntity.getProjectGasUsageTotal().multiply(new BigDecimal("0.00218")));
                            }

                            if (kpiCodeMap.containsKey("project.carbon.gasUsageCoal.total")) {
                                insertEntity.setProjectCarbonGasusagecoalTotal(insertEntity.getProjectCarbonElectricityusagedustTotal().multiply(new BigDecimal("0.4012")));
                            }
                            if (kpiCodeMap.containsKey("project.carbon.waterUsageCO2.total")) {
                                insertEntity.setProjectCarbonWaterusageco2Total(insertEntity.getProjectWaterUsageTotal().multiply(new BigDecimal("0.00185")));
                            }

                            if (kpiCodeMap.containsKey("project.carbon.waterUsageCoal.total")) {
                                insertEntity.setProjectCarbonWaterusagecoalTotal(insertEntity.getProjectCarbonWaterusageco2Total().multiply(new BigDecimal("0.4012")));
                            }

                            if (kpiCodeMap.containsKey("project.carbon.waterUsageSO2.total")) {
                                insertEntity.setProjectCarbonWaterusageso2Total(insertEntity.getProjectCarbonWaterusageco2Total().multiply(new BigDecimal("0.0301")));
                            }

                            if (kpiCodeMap.containsKey("project.carbon.waterUsageDust.total")) {
                                insertEntity.setProjectCarbonWaterusagedustTotal(insertEntity.getProjectCarbonWaterusageco2Total().multiply(new BigDecimal("0.2782")));
                            }

//                    if (kpiCodeMap.containsKey("project.carbon.totalCO2.total")) {
                            insertEntity.setProjectCarbonTotalco2Total(insertEntity.getProjectCarbonElectricityusageco2Total().add(insertEntity.getProjectCarbonWaterusageco2Total()).add(insertEntity.getProjectCarbonGasusageco2Total()));

//                    if (kpiCodeMap.containsKey("project.carbon.totalCoal.total")) {
                            insertEntity.setProjectCarbonTotalcoalTotal(insertEntity.getProjectCarbonElectricityusagecoalTotal().add(insertEntity.getProjectCarbonWaterusagecoalTotal()).add(insertEntity.getProjectCarbonGasusagecoalTotal()));

                            //                    if (kpiCodeMap.containsKey("project.carbon.totalSO2.total")) {
                            insertEntity.setProjectCarbonTotalso2Total(insertEntity.getProjectCarbonElectricityusageso2Total().add(insertEntity.getProjectCarbonWaterusageso2Total()).add(insertEntity.getProjectCarbonGasusageso2Total()));

                            //                    if (kpiCodeMap.containsKey("project.carbon.totalDust.total")) {
                            insertEntity.setProjectCarbonTotaldustTotal(insertEntity.getProjectCarbonElectricityusagedustTotal().add(insertEntity.getProjectCarbonWaterusagedustTotal()).add(insertEntity.getProjectCarbonGasusagedustTotal()));
                            if (kpiCodeMap.containsKey("project.electricity.pccEnergyUsage.total") && CollUtil.isNotEmpty(list)) {
                                //购网电量 project.electricity.pccEnergyUsage.total
                                if (CollUtil.isEmpty(deviceList)) {
                                    insertEntity.setProjectElectricityPccEnergyUsageTotal(BigDecimal.ZERO);
                                } else {
                                    var val = deviceList.stream().filter(j -> yearDeviceObj.containsKey(j.getDeviceId()) && yearDeviceObj.getJSONObject(j.getDeviceId()).containsKey("energymeterEpimportTotal"))
                                            .map(j -> yearDeviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("energymeterEpimportTotal").multiply(new BigDecimal(j.getComputeTag())))
                                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                                    var val1 = deviceList.stream().filter(j -> yearDeviceObj.containsKey(j.getDeviceId()) && yearDeviceObj.getJSONObject(j.getDeviceId()).containsKey("gscnEpimportTotal"))
                                            .map(j -> yearDeviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("gscnEpimportTotal").multiply(new BigDecimal(j.getComputeTag())))
                                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                                    var val2 = deviceList.stream().filter(j -> yearDeviceObj.containsKey(j.getDeviceId()) && yearDeviceObj.getJSONObject(j.getDeviceId()).containsKey("znbEpimportTotal"))
                                            .map(j -> yearDeviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("znbEpimportTotal").multiply(new BigDecimal(j.getComputeTag())))
                                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                                    insertEntity.setProjectElectricityPccEnergyUsageTotal(val.add(val1).add(val2));
                                }
                            }
                            if (null != electricityPriceEntity && ElectricityPriceTypeEnum.TOU.getType().equals(electricityPriceEntity.getType())) {
                                if (kpiCodeMap.containsKey("project.electricity.pccEnergyUsage.tip") && CollUtil.isNotEmpty(list)) {
                                    // 购网尖电量(根据尖时段配置，由小时表汇总得到。。)
                                    insertEntity.setProjectElectricityPccEnergyUsageTip(list.stream().map(ProjectStaSubitemMonthEntity::getProjectElectricityPccEnergyUsageTip).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add));
                                }
                                if (kpiCodeMap.containsKey("project.electricity.pccEnergyUsage.peak") && CollUtil.isNotEmpty(list)) {
                                    // 购网峰电量(根据峰时段配置，由小时表汇总得到。)
                                    insertEntity.setProjectElectricityPccEnergyUsagePeak(list.stream().map(ProjectStaSubitemMonthEntity::getProjectElectricityPccEnergyUsagePeak).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add));
                                }
                                if (kpiCodeMap.containsKey("project.electricity.pccEnergyUsage.valley") && CollUtil.isNotEmpty(list)) {
                                    // 购网谷电量(根据谷时段配置，由小时表汇总得到。)
                                    insertEntity.setProjectElectricityPccEnergyUsageValley(list.stream().map(ProjectStaSubitemMonthEntity::getProjectElectricityPccEnergyUsageValley).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add));
                                }
                                if (kpiCodeMap.containsKey("project.electricity.pccEnergyUsage.flat") && CollUtil.isNotEmpty(list)) {
                                    // 购网平电量(根据平时段配置，由小时表汇总得到)
                                    insertEntity.setProjectElectricityPccEnergyUsageFlat(list.stream().map(ProjectStaSubitemMonthEntity::getProjectElectricityPccEnergyUsageFlat).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add));
                                }
                            }
                            if (kpiCodeMap.containsKey("project.electricity.pccEnergyUsageFee.total") && CollUtil.isNotEmpty(list)) {
                                // 购网电费(由购网尖峰谷平电量及电价得到。) project.electricity.pccEnergyUsageFee.total
                                insertEntity.setProjectElectricityPccEnergyUsageFeeTotal(list.stream().map(ProjectStaSubitemMonthEntity::getProjectElectricityPccEnergyUsageFeeTotal).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add));
                            }
                            if (kpiCodeMap.containsKey("project.electricity.pccEnergyProduction.total") && CollUtil.isNotEmpty(list)) {
                                //上网电量(由小时表汇总得到) project.electricity.pccEnergyProduction.total
                                if (CollUtil.isEmpty(deviceList)) {
                                    insertEntity.setProjectElectricityPccEnergyProductionTotal(BigDecimal.ZERO);
                                }
                                var val = deviceList.stream().filter(j -> yearDeviceObj.containsKey(j.getDeviceId()) && yearDeviceObj.getJSONObject(j.getDeviceId()).containsKey("energymeterEpexportTotal"))
                                        .map(j -> yearDeviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("energymeterEpexportTotal").multiply(new BigDecimal(j.getComputeTag())))
                                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                                var val1 = deviceList.stream().filter(j -> yearDeviceObj.containsKey(j.getDeviceId()) && yearDeviceObj.getJSONObject(j.getDeviceId()).containsKey("gscnEpexportTotal"))
                                        .map(j -> yearDeviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("gscnEpexportTotal").multiply(new BigDecimal(j.getComputeTag())))
                                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                                var val2 = deviceList.stream().filter(j -> yearDeviceObj.containsKey(j.getDeviceId()) && yearDeviceObj.getJSONObject(j.getDeviceId()).containsKey("znbEpexportTotal"))
                                        .map(j -> yearDeviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("znbEpexportTotal").multiply(new BigDecimal(j.getComputeTag())))
                                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                                insertEntity.setProjectElectricityPccEnergyProductionTotal(val.add(val1).add(val2));
                            }
                            if (kpiCodeMap.containsKey("project.electricity.pccEnergyProductionFee.total") && CollUtil.isNotEmpty(list)) {
                                // 网收益(由上网电量*上网电价)project.electricity.pccEnergyProductionFee.total
                                insertEntity.setProjectElectricityPccEnergyProductionFeeTotal(list.stream().map(ProjectStaSubitemMonthEntity::getProjectElectricityPccEnergyProductionFeeTotal).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add));
                            }
                            if (kpiCodeMap.containsKey("project.electricity.pvEnergyProduction.total") && CollUtil.isNotEmpty(list)) {
                                //光伏发电量(由小时表汇总得到) project.electricity.pvEnergyProduction.total
                                if (CollUtil.isEmpty(deviceList)) {
                                    insertEntity.setProjectElectricityPvEnergyProductionTotal(BigDecimal.ZERO);
                                }
                                var val = deviceList.stream().filter(j -> yearDeviceObj.containsKey(j.getDeviceId()) && yearDeviceObj.getJSONObject(j.getDeviceId()).containsKey("energymeterEpexportTotal"))
                                        .map(j -> yearDeviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("energymeterEpexportTotal").multiply(new BigDecimal(j.getComputeTag())))
                                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                                var val1 = deviceList.stream().filter(j -> yearDeviceObj.containsKey(j.getDeviceId()) && yearDeviceObj.getJSONObject(j.getDeviceId()).containsKey("gscnEpexportTotal"))
                                        .map(j -> yearDeviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("gscnEpexportTotal").multiply(new BigDecimal(j.getComputeTag())))
                                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                                var val2 = deviceList.stream().filter(j -> yearDeviceObj.containsKey(j.getDeviceId()) && yearDeviceObj.getJSONObject(j.getDeviceId()).containsKey("znbEpexportTotal"))
                                        .map(j -> yearDeviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("znbEpexportTotal").multiply(new BigDecimal(j.getComputeTag())))
                                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                                insertEntity.setProjectElectricityPvEnergyProductionTotal(val.add(val1).add(val2));
                            }
                            if (kpiCodeMap.containsKey("project.electricity.pvEnergyProductionGrid.total") && CollUtil.isNotEmpty(list)) {
                                // 上网电量(同关口上网电量指标) project.electricity.pvEnergyProductionGrid.total
                                insertEntity.setProjectElectricityPvEnergyProductionGridTotal(insertEntity.getProjectElectricityPccEnergyProductionTotal());
                            }
                            if (kpiCodeMap.containsKey("project.electricity.pvEnergyProductionStorage.total") && CollUtil.isNotEmpty(list)) {
                                //先储后用(由储光电量汇总) project.electricity.pvEnergyProductionStorage.total
                                insertEntity.setProjectElectricityPvEnergyProductionStorageTotal(list.stream().map(ProjectStaSubitemMonthEntity::getProjectElectricityStorageEnergyUsagePvTotal).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add));
                            }
                            if (kpiCodeMap.containsKey("project.electricity.pvEnergyProductionLoad.total") && CollUtil.isNotEmpty(list)) {
                                // 直接使用(光伏总发电-上网-先储后用) project.electricity.pvEnergyProductionLoad.total
                                insertEntity.setProjectElectricityPvEnergyProductionLoadTotal(list.stream().map(ProjectStaSubitemMonthEntity::getProjectElectricityPvEnergyProductionLoadTotal).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add));
                            }
                            if (kpiCodeMap.containsKey("project.electricity.pvEnergyProductionGridFee.total") && CollUtil.isNotEmpty(list)) {
                                //上网收益（同关口上网收益） project.electricity.pvEnergyProductionGridFee.total
                                insertEntity.setProjectElectricityPvEnergyProductionGridFeeTotal(insertEntity.getProjectElectricityPccEnergyProductionFeeTotal());
                            }
                            if (kpiCodeMap.containsKey("project.electricity.pvEnergyProductionUsageFee.total") && CollUtil.isNotEmpty(list)) {
                                // 消纳收益(sum(（小时发电量-小时上网电量）* 当时单价)) project.electricity.pvEnergyProductionUsageFee.total
                                insertEntity.setProjectElectricityPvEnergyProductionUsageFeeTotal(list.stream().map(ProjectStaSubitemMonthEntity::getProjectElectricityPvEnergyProductionUsageFeeTotal).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add));
                            }
                            if (kpiCodeMap.containsKey("project.electricity.pvEnergyProductionFee.total") && CollUtil.isNotEmpty(list)) {
                                // 光伏收益(上网收益+消纳)project.electricity.pvEnergyProductionFee.total
                                insertEntity.setProjectElectricityPvEnergyProductionFeeTotal(list.stream().map(ProjectStaSubitemMonthEntity::getProjectElectricityPvEnergyProductionFeeTotal).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add));
                            }
                            if (kpiCodeMap.containsKey("project.electricity.storageEnergyUsage.total") && CollUtil.isNotEmpty(list)) {
                                //储充电量(由小时表汇总得到) project.electricity.storageEnergyUsage.total
                                if (CollUtil.isEmpty(deviceList)) {
                                    insertEntity.setProjectElectricityStorageEnergyUsageTotal(BigDecimal.ZERO);
                                } else {
                                    var val = deviceList.stream().filter(j -> yearDeviceObj.containsKey(j.getDeviceId()) && yearDeviceObj.getJSONObject(j.getDeviceId()).containsKey("energymeterEpimportTotal"))
                                            .map(j -> yearDeviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("energymeterEpimportTotal").multiply(new BigDecimal(j.getComputeTag())))
                                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                                    var val1 = deviceList.stream().filter(j -> yearDeviceObj.containsKey(j.getDeviceId()) && yearDeviceObj.getJSONObject(j.getDeviceId()).containsKey("gscnEpimportTotal"))
                                            .map(j -> yearDeviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("gscnEpimportTotal").multiply(new BigDecimal(j.getComputeTag())))
                                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                                    var val2 = deviceList.stream().filter(j -> yearDeviceObj.containsKey(j.getDeviceId()) && yearDeviceObj.getJSONObject(j.getDeviceId()).containsKey("znbEpimportTotal"))
                                            .map(j -> yearDeviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("znbEpimportTotal").multiply(new BigDecimal(j.getComputeTag())))
                                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                                    insertEntity.setProjectElectricityStorageEnergyUsageTotal(val.add(val1).add(val2));
                                }
                            }
                            if (null != electricityPriceEntity && ElectricityPriceTypeEnum.TOU.getType().equals(electricityPriceEntity.getType())) {
                                if (kpiCodeMap.containsKey("project.electricity.storageEnergyUsage.tip") && CollUtil.isNotEmpty(list)) {
                                    //尖充电量(根据尖时段配置，由小时表汇总得到。) project.electricity.storageEnergyUsage.tip
                                    insertEntity.setProjectElectricityStorageEnergyUsageTip(list.stream().map(ProjectStaSubitemMonthEntity::getProjectElectricityStorageEnergyUsageTip).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add));
                                }
                                if (kpiCodeMap.containsKey("project.electricity.storageEnergyUsage.peak") && CollUtil.isNotEmpty(list)) {
                                    //峰充电量(根据峰时段配置，由小时表汇总得到) project.electricity.storageEnergyUsage.peak
                                    insertEntity.setProjectElectricityStorageEnergyUsagePeak(list.stream().map(ProjectStaSubitemMonthEntity::getProjectElectricityStorageEnergyUsagePeak).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add));
                                }
                                if (kpiCodeMap.containsKey("project.electricity.storageEnergyUsage.valley") && CollUtil.isNotEmpty(list)) {
                                    //谷充电量(根据谷时段配置，由小时表汇总得到) project.electricity.storageEnergyUsage.valley
                                    insertEntity.setProjectElectricityStorageEnergyUsageValley(list.stream().map(ProjectStaSubitemMonthEntity::getProjectElectricityStorageEnergyUsageValley).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add));
                                }
                                if (kpiCodeMap.containsKey("project.electricity.storageEnergyUsage.flat") && CollUtil.isNotEmpty(list)) {
                                    //平充电量（根据平时段配置，由小时表汇总得到） project.electricity.storageEnergyUsage.flat
                                    insertEntity.setProjectElectricityStorageEnergyUsageFlat(list.stream().map(ProjectStaSubitemMonthEntity::getProjectElectricityStorageEnergyUsageFlat).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add));
                                }
                            }
                            if (kpiCodeMap.containsKey("project.electricity.storageEnergyUsagePv.total") && CollUtil.isNotEmpty(list)) {
                                // 储光电量（由小时表汇总得到） project.electricity.storageEnergyUsagePv.total
                                if (BigDecimal.ZERO.compareTo(insertEntity.getProjectElectricityPccEnergyUsageTotal()) == 0) {
                                    insertEntity.setProjectElectricityStorageEnergyUsagePvTotal(null);
                                }
                                BigDecimal multiply = insertEntity.getProjectElectricityStorageEnergyUsageTotal().subtract(insertEntity.getProjectElectricityPccEnergyUsageTotal()).subtract(insertEntity.getProjectElectricityStorageEnergyProductionTotal());
                                if (BigDecimal.ZERO.compareTo(multiply) > 0) {
                                    insertEntity.setProjectElectricityStorageEnergyUsagePvTotal(BigDecimal.ZERO);
                                } else {
                                    insertEntity.setProjectElectricityStorageEnergyUsagePvTotal(multiply);
                                }
                            }
                            if (kpiCodeMap.containsKey("project.electricity.storageEnergyUsageGrid.total") && CollUtil.isNotEmpty(list)) {
                                // 储市电量（由小时表汇总得到） project.electricity.storageEnergyUsageGrid.total
                                BigDecimal decimal1 = insertEntity.getProjectElectricityStorageEnergyUsageTotal();
                                BigDecimal decimal2 = insertEntity.getProjectElectricityStorageEnergyUsagePvTotal();
                                if (Objects.isNull(decimal2) || Objects.isNull(decimal1)) {
                                    insertEntity.setProjectElectricityStorageEnergyUsageGridTotal(null);
                                } else {
                                    BigDecimal bigDecimal = decimal1.subtract(decimal2);
                                    if (BigDecimal.ZERO.compareTo(bigDecimal) > 0) {
                                        insertEntity.setProjectElectricityStorageEnergyUsageGridTotal(BigDecimal.ZERO);
                                    } else {
                                        insertEntity.setProjectElectricityStorageEnergyUsageGridTotal(bigDecimal);
                                    }
                                }
                            }
                            if (kpiCodeMap.containsKey("project.electricity.storageEnergyProduction.total") && CollUtil.isNotEmpty(list)) {
                                // 储放电量（由小时表汇总得到） project.electricity.storageEnergyProduction.total
                                if (CollUtil.isEmpty(deviceList)) {
                                    insertEntity.setProjectElectricityStorageEnergyProductionTotal(BigDecimal.ZERO);
                                } else {
                                    var val = deviceList.stream().filter(j -> yearDeviceObj.containsKey(j.getDeviceId()) && yearDeviceObj.getJSONObject(j.getDeviceId()).containsKey("energymeterEpexportTotal"))
                                            .map(j -> yearDeviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("energymeterEpexportTotal").multiply(new BigDecimal(j.getComputeTag())))
                                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                                    var val1 = deviceList.stream().filter(j -> yearDeviceObj.containsKey(j.getDeviceId()) && yearDeviceObj.getJSONObject(j.getDeviceId()).containsKey("gscnEpexportTotal"))
                                            .map(j -> yearDeviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("gscnEpexportTotal").multiply(new BigDecimal(j.getComputeTag())))
                                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                                    var val2 = deviceList.stream().filter(j -> yearDeviceObj.containsKey(j.getDeviceId()) && yearDeviceObj.getJSONObject(j.getDeviceId()).containsKey("znbEpexportTotal"))
                                            .map(j -> yearDeviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("znbEpexportTotal").multiply(new BigDecimal(j.getComputeTag())))
                                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                                    insertEntity.setProjectElectricityStorageEnergyProductionTotal(val.add(val1).add(val2));
                                }
                            }
                            if (null != electricityPriceEntity && ElectricityPriceTypeEnum.TOU.getType().equals(electricityPriceEntity.getType())) {
                                if (kpiCodeMap.containsKey("project.electricity.storageEnergyProduction.tip") && CollUtil.isNotEmpty(list)) {
                                    //尖放电量(根据尖时段配置，由小时表汇总得到。) project.electricity.storageEnergyProduction.tip
                                    insertEntity.setProjectElectricityStorageEnergyProductionTip(list.stream().map(ProjectStaSubitemMonthEntity::getProjectElectricityStorageEnergyProductionTip).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add));
                                }
                                if (kpiCodeMap.containsKey("project.electricity.storageEnergyProduction.peak") && CollUtil.isNotEmpty(list)) {
                                    //峰放电量(根据峰时段配置，由小时表汇总得到) project.electricity.storageEnergyProduction.peak
                                    insertEntity.setProjectElectricityStorageEnergyProductionPeak(list.stream().map(ProjectStaSubitemMonthEntity::getProjectElectricityStorageEnergyProductionPeak).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add));
                                }
                                if (kpiCodeMap.containsKey("project.electricity.storageEnergyProduction.valley") && CollUtil.isNotEmpty(list)) {
                                    //谷放电量(根据谷时段配置，由小时表汇总得到) project.electricity.storageEnergyProduction.valley
                                    insertEntity.setProjectElectricityStorageEnergyProductionValley(list.stream().map(ProjectStaSubitemMonthEntity::getProjectElectricityStorageEnergyProductionValley).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add));
                                }
                                if (kpiCodeMap.containsKey("project.electricity.storageEnergyProduction.flat") && CollUtil.isNotEmpty(list)) {
                                    //平放电量(根据平时段配置，由小时表汇总得到) project.electricity.storageEnergyProduction.flat
                                    insertEntity.setProjectElectricityStorageEnergyProductionFlat(list.stream().map(ProjectStaSubitemMonthEntity::getProjectElectricityStorageEnergyProductionFlat).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add));
                                }
                            }
                            if (kpiCodeMap.containsKey("project.electricity.storageEnergyNetFee.total") && CollUtil.isNotEmpty(list)) {
                                //储能净收益(sum(放电时段单价*放电时段电量）-sum(充电时段单价*充电时段电量))
                                insertEntity.setProjectElectricityStorageEnergyNetFeeTotal(list.stream().map(ProjectStaSubitemMonthEntity::getProjectElectricityStorageEnergyNetFeeTotal).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add));
                            }
                            if (kpiCodeMap.containsKey("project.electricity.subChargeEnergy.total") && CollUtil.isNotEmpty(list)) {
                                // 充电桩总用电(由小时表汇总得到) project.electricity.subChargeEnergy.total
                                if (CollUtil.isEmpty(deviceList)) {
                                    insertEntity.setProjectElectricitySubChargeEnergyTotal(BigDecimal.ZERO);
                                }
                                var val = deviceList.stream().filter(j -> yearDeviceObj.containsKey(j.getDeviceId()) && yearDeviceObj.getJSONObject(j.getDeviceId()).containsKey("energymeterEpimportTotal"))
                                        .map(j -> yearDeviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("energymeterEpimportTotal").multiply(new BigDecimal(j.getComputeTag())))
                                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                                var val1 = deviceList.stream().filter(j -> yearDeviceObj.containsKey(j.getDeviceId()) && yearDeviceObj.getJSONObject(j.getDeviceId()).containsKey("gscnEpimportTotal"))
                                        .map(j -> yearDeviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("gscnEpimportTotal").multiply(new BigDecimal(j.getComputeTag())))
                                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                                var val2 = deviceList.stream().filter(j -> yearDeviceObj.containsKey(j.getDeviceId()) && yearDeviceObj.getJSONObject(j.getDeviceId()).containsKey("znbEpimportTotal"))
                                        .map(j -> yearDeviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("znbEpimportTotal").multiply(new BigDecimal(j.getComputeTag())))
                                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                                insertEntity.setProjectElectricitySubChargeEnergyTotal(val.add(val1).add(val2));

                            }
                            if (kpiCodeMap.containsKey("project.electricity.subChargeEnergyChargeFee.total") && CollUtil.isNotEmpty(list)) {
                                // 充电总费用(电度费+服务费；计费模式及服务费配置到数据库中。) project.electricity.subChargeEnergyFee.total
                                insertEntity.setProjectElectricitySubChargeEnergyChargeFeeTotal(list.stream().map(ProjectStaSubitemMonthEntity::getProjectElectricitySubChargeEnergyChargeFeeTotal).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add));
                            }
                            if (kpiCodeMap.containsKey("project.electricity.subChargeEnergyServiceFee.total") && CollUtil.isNotEmpty(list)) {
                                // 上网收益(由上网电量*上网电价) project.electricity.pccEnergyProductionFee.total
                                insertEntity.setProjectElectricitySubChargeEnergyServiceFeeTotal(list.stream().map(ProjectStaSubitemMonthEntity::getProjectElectricitySubChargeEnergyServiceFeeTotal).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add));
                            }
                            if (kpiCodeMap.containsKey("project_electricity_pccenergyproductionfee_total") && CollUtil.isNotEmpty(list)) {
                                // 上网收益(由上网电量*上网电价) project.electricity.pccEnergyProductionFee.total
                                insertEntity.setProjectElectricityPccEnergyProductionFeeTotal(list.stream().map(ProjectStaSubitemMonthEntity::getProjectElectricityPccEnergyProductionFeeTotal).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add));
                            }
                            if (kpiCodeMap.containsKey("project.electricity.subChargeEnergyFee.total") && CollUtil.isNotEmpty(list)) {
                                // 服务费(总电量*服务费单价) project.electricity.subChargeEnergyServiceFee.total
                                insertEntity.setProjectElectricitySubChargeEnergyFeeTotal(insertEntity.getProjectElectricitySubChargeEnergyChargeFeeTotal()
                                        .add(insertEntity.getProjectElectricitySubChargeEnergyServiceFeeTotal()));
                            }
                            if (kpiCodeMap.containsKey("project.electricity.subLightingEnergy.total") && CollUtil.isNotEmpty(list)) {
                                //照明用电电量 project.electricity.subLightingEnergy.total
                                if (CollUtil.isEmpty(deviceList)) {
                                    insertEntity.setProjectElectricitySubLightingEnergyTotal(BigDecimal.ZERO);
                                }
                                var val = deviceList.stream().filter(j -> yearDeviceObj.containsKey(j.getDeviceId()) && yearDeviceObj.getJSONObject(j.getDeviceId()).containsKey("energymeterEpimportTotal"))
                                        .map(j -> yearDeviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("energymeterEpimportTotal").multiply(new BigDecimal(j.getComputeTag())))
                                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                                var val1 = deviceList.stream().filter(j -> yearDeviceObj.containsKey(j.getDeviceId()) && yearDeviceObj.getJSONObject(j.getDeviceId()).containsKey("gscnEpimportTotal"))
                                        .map(j -> yearDeviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("gscnEpimportTotal").multiply(new BigDecimal(j.getComputeTag())))
                                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                                var val2 = deviceList.stream().filter(j -> yearDeviceObj.containsKey(j.getDeviceId()) && yearDeviceObj.getJSONObject(j.getDeviceId()).containsKey("znbEpimportTotal"))
                                        .map(j -> yearDeviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("znbEpimportTotal").multiply(new BigDecimal(j.getComputeTag())))
                                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                                insertEntity.setProjectElectricitySubLightingEnergyTotal(val.add(val1).add(val2));
                            }
                            if (kpiCodeMap.containsKey("project.electricity.subSocketEnergy.total") && CollUtil.isNotEmpty(list)) {
                                //插座用电电量 project.electricity.subSocketEnergy.total
                                if (CollUtil.isEmpty(deviceList)) {
                                    insertEntity.setProjectElectricitySubSocketEnergyTotal(BigDecimal.ZERO);
                                }
                                var val = deviceList.stream().filter(j -> yearDeviceObj.containsKey(j.getDeviceId()) && yearDeviceObj.getJSONObject(j.getDeviceId()).containsKey("energymeterEpimportTotal"))
                                        .map(j -> yearDeviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("energymeterEpimportTotal").multiply(new BigDecimal(j.getComputeTag())))
                                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                                var val1 = deviceList.stream().filter(j -> yearDeviceObj.containsKey(j.getDeviceId()) && yearDeviceObj.getJSONObject(j.getDeviceId()).containsKey("gscnEpimportTotal"))
                                        .map(j -> yearDeviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("gscnEpimportTotal").multiply(new BigDecimal(j.getComputeTag())))
                                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                                var val2 = deviceList.stream().filter(j -> yearDeviceObj.containsKey(j.getDeviceId()) && yearDeviceObj.getJSONObject(j.getDeviceId()).containsKey("znbEpimportTotal"))
                                        .map(j -> yearDeviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("znbEpimportTotal").multiply(new BigDecimal(j.getComputeTag())))
                                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                                insertEntity.setProjectElectricitySubSocketEnergyTotal(val.add(val1).add(val2));
                            }
                            if (kpiCodeMap.containsKey("project.carbon.pvReductionCoal.total") && CollUtil.isNotEmpty(list)) {
                                // 标准煤(CO2减排量=光伏发电量 * CO2排放因子 排放因子同用电的排放因子。 标准煤、SO2、粉尘根据CO2*系数得到。系数同之前相同。) project.carbon.pvReductionCoal.total
                                insertEntity.setProjectCarbonPvReductionCoalTotal(list.stream().map(ProjectStaSubitemMonthEntity::getProjectCarbonPvReductionCoalTotal).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add));
                            }
                            if (kpiCodeMap.containsKey("project.carbon.pvReductionCO2.total") && CollUtil.isNotEmpty(list)) {
                                // 标准煤(CO2减排量=光伏发电量 * CO2排放因子 排放因子同用电的排放因子。 标准煤、SO2、粉尘根据CO2*系数得到。系数同之前相同。) project.carbon.pvReductionCoal.total
                                insertEntity.setProjectCarbonPvReductionCO2Total(list.stream().map(ProjectStaSubitemMonthEntity::getProjectCarbonPvReductionCO2Total).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add));
                            }
                            if (kpiCodeMap.containsKey("project.carbon.pvReductionSO2.total") && CollUtil.isNotEmpty(list)) {
                                // 标准煤(CO2减排量=光伏发电量 * CO2排放因子 排放因子同用电的排放因子。 标准煤、SO2、粉尘根据CO2*系数得到。系数同之前相同。) project.carbon.pvReductionCoal.total
                                insertEntity.setProjectCarbonPvReductionSO2Total(list.stream().map(ProjectStaSubitemMonthEntity::getProjectCarbonPvReductionSO2Total).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add));
                            }
                            if (kpiCodeMap.containsKey("project.carbon.pvReductionDust.total") && CollUtil.isNotEmpty(list)) {
                                // 标准煤(CO2减排量=光伏发电量 * CO2排放因子 排放因子同用电的排放因子。 标准煤、SO2、粉尘根据CO2*系数得到。系数同之前相同。) project.carbon.pvReductionCoal.total
                                insertEntity.setProjectCarbonPvReductionDustTotal(list.stream().map(ProjectStaSubitemMonthEntity::getProjectCarbonPvReductionDustTotal).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add));
                            }
                        }

                        //需要计算的指标
                        insertEntity.setProjectGasFeeTotal(insertEntity.getProjectGasUsageTotal().multiply(new BigDecimal("4.88")));
                        insertEntity.setProjectWaterFeeWater(insertEntity.getProjectWaterUsageTotal().multiply(new BigDecimal("3.32")));
                        insertEntity.setProjectWaterFeeSewerage(insertEntity.getProjectWaterUsageTotal().multiply(new BigDecimal("0.9")).multiply(new BigDecimal("2.97")));
                        insertEntity.setProjectWaterFeeTotal(insertEntity.getProjectWaterFeeWater().add(insertEntity.getProjectWaterFeeSewerage()));

                        if (totalKpi.contains("project.carbon.electricityUsageCO2.total")) {
                            insertEntity.setProjectCarbonElectricityusageco2Total(insertEntity.getProjectElectricityEnergyusageTotal().multiply(new BigDecimal("0.00042")));
                        }

                        if (totalKpi.contains("project.carbon.electricityUsageCoal.total")) {
                            insertEntity.setProjectCarbonElectricityusagecoalTotal(insertEntity.getProjectCarbonElectricityusageco2Total().multiply(new BigDecimal("0.4012")));
                        }

                        if (totalKpi.contains("project.carbon.electricityUsageSO2.total")) {
                            insertEntity.setProjectCarbonElectricityusageso2Total(insertEntity.getProjectCarbonElectricityusageco2Total().multiply(new BigDecimal("0.0301")));
                        }

                        if (totalKpi.contains("project.carbon.electricityUsageDust.total")) {
                            insertEntity.setProjectCarbonElectricityusagedustTotal(insertEntity.getProjectCarbonElectricityusageco2Total().multiply(new BigDecimal("0.2782")));
                        }

                        if (totalKpi.contains("project.carbon.gasUsageCO2.total")) {
                            insertEntity.setProjectCarbonGasusageco2Total(insertEntity.getProjectGasUsageTotal().multiply(new BigDecimal("0.00218")));
                        }

                        if (totalKpi.contains("project.carbon.gasUsageCoal.total")) {
                            insertEntity.setProjectCarbonGasusagecoalTotal(insertEntity.getProjectCarbonElectricityusagedustTotal().multiply(new BigDecimal("0.4012")));
                        }
                        if (totalKpi.contains("project.carbon.waterUsageCO2.total")) {
                            insertEntity.setProjectCarbonWaterusageco2Total(insertEntity.getProjectWaterUsageTotal().multiply(new BigDecimal("0.00185")));
                        }

                        if (totalKpi.contains("project.carbon.waterUsageCoal.total")) {
                            insertEntity.setProjectCarbonWaterusagecoalTotal(insertEntity.getProjectCarbonWaterusageco2Total().multiply(new BigDecimal("0.4012")));
                        }

                        if (totalKpi.contains("project.carbon.waterUsageSO2.total")) {
                            insertEntity.setProjectCarbonWaterusageso2Total(insertEntity.getProjectCarbonWaterusageco2Total().multiply(new BigDecimal("0.0301")));
                        }

                        if (totalKpi.contains("project.carbon.waterUsageDust.total")) {
                            insertEntity.setProjectCarbonWaterusagedustTotal(insertEntity.getProjectCarbonWaterusageco2Total().multiply(new BigDecimal("0.2782")));
                        }

                        if (totalKpi.contains("project.carbon.waterUsageDust.total")) {
                            insertEntity.setProjectCarbonWaterusagedustTotal(insertEntity.getProjectCarbonWaterusageco2Total().multiply(new BigDecimal("0.2782")));
                        }

                        if (totalKpi.contains("project.carbon.pvReductionCO2.total") && CollUtil.isNotEmpty(list)) {
                            // CO2(CO2减排量=光伏发电量 * CO2排放因子 排放因子同用电的排放因子。 标准煤、SO2、粉尘根据CO2*系数得到。系数同之前相同。)
                            insertEntity.setProjectCarbonPvReductionCO2Total(insertEntity.getProjectElectricityPvEnergyProductionTotal().multiply(new BigDecimal("0.00042")));
                        }
                        if (totalKpi.contains("project.carbon.pvReductionCoal.total") && CollUtil.isNotEmpty(list)) {
                            // CO2(CO2减排量=光伏发电量 * CO2排放因子 排放因子同用电的排放因子。 标准煤、SO2、粉尘根据CO2*系数得到。系数同之前相同。)
                            insertEntity.setProjectCarbonPvReductionCoalTotal(insertEntity.getProjectCarbonPvReductionCO2Total().multiply(new BigDecimal("0.4012")));
                        }
                        if (totalKpi.contains("project.carbon.pvReductionSO2.total") && CollUtil.isNotEmpty(list)) {
                            // CO2(CO2减排量=光伏发电量 * CO2排放因子 排放因子同用电的排放因子。 标准煤、SO2、粉尘根据CO2*系数得到。系数同之前相同。)
                            insertEntity.setProjectCarbonPvReductionSO2Total(insertEntity.getProjectCarbonPvReductionCO2Total().multiply(new BigDecimal("0.0301")));
                        }
                        if (totalKpi.contains("project.carbon.pvReductionDust.total") && CollUtil.isNotEmpty(list)) {
                            // CO2(CO2减排量=光伏发电量 * CO2排放因子 排放因子同用电的排放因子。 标准煤、SO2、粉尘根据CO2*系数得到。系数同之前相同。)
                            insertEntity.setProjectCarbonPvReductionDustTotal(insertEntity.getProjectCarbonPvReductionCO2Total().multiply(new BigDecimal("0.2782")));
                        }

                        if (totalKpi.contains("project.electricity.subHAVCEnergy.avgSq") && CollUtil.isNotEmpty(list)) {
                            // 空调总用电/项目建筑面积
                            insertEntity.setProjectElectricitySubhavcenergyAvgsq(insertEntity.getProjectElectricitySubhavcenergyTotal().divide(projInfo.getArea(), 2, RoundingMode.HALF_UP));
                        }
                        if (totalKpi.contains("project.electricity.subHeatingWaterEnergy.avgCube") && CollUtil.isNotEmpty(list)) {
                            // 热水总用电量/热水补水量
                            if (insertEntity.getProjectWaterHeatingwaterusageTotal() == null || insertEntity.getProjectWaterHeatingwaterusageTotal().compareTo(BigDecimal.ZERO) == 0) {
                                insertEntity.setProjectElectricitySubheatingwaterenergyAvgcube(BigDecimal.ZERO);
                            } else {
                                insertEntity.setProjectElectricitySubheatingwaterenergyAvgcube(null == insertEntity.getProjectElectricitySubheatingwaterenergyTotal() || 0 >= insertEntity.getProjectElectricitySubheatingwaterenergyTotal().intValue() ?
                                        BigDecimal.ZERO : insertEntity.getProjectElectricitySubheatingwaterenergyTotal().divide(insertEntity.getProjectWaterHeatingwaterusageTotal(), 2, RoundingMode.HALF_UP));
                            }
                        }

                        insertEntity.setProjectCarbonTotalco2Total(insertEntity.getProjectCarbonElectricityusageco2Total().add(insertEntity.getProjectCarbonWaterusageco2Total()).add(insertEntity.getProjectCarbonGasusageco2Total()));
                        insertEntity.setProjectCarbonTotalcoalTotal(insertEntity.getProjectCarbonElectricityusagecoalTotal().add(insertEntity.getProjectCarbonWaterusagecoalTotal()).add(insertEntity.getProjectCarbonGasusagecoalTotal()));
                        insertEntity.setProjectCarbonTotalso2Total(insertEntity.getProjectCarbonElectricityusageso2Total().add(insertEntity.getProjectCarbonWaterusageso2Total()).add(insertEntity.getProjectCarbonGasusageso2Total()));
                        insertEntity.setProjectCarbonTotaldustTotal(insertEntity.getProjectCarbonElectricityusagedustTotal().add(insertEntity.getProjectCarbonWaterusagedustTotal()).add(insertEntity.getProjectCarbonGasusagedustTotal()));

                        projectStaSubitemYearServiceImpl.save(insertEntity);
                    } catch (Exception e) {
                        jobLogMap.get(tenantId).setStatus(JOB_EXEC_ERROR);
                        log.error("分项年项目执行异常", e);
                    }
                }
            } finally {
                saveJobLog(jobLogMap, tenantProjectMap);
            }
        }
        return true;
    }

    /**
     * 判断数据是否否和时间条件爱in
     *
     * @param hourEntity 时数据
     * @param period     时条
     * @return 结果
     */
    private boolean condition(ProjectStaSubitemHourEntity hourEntity, List<ProjectCnfTimePeriodEntity> period) {
        return period.stream()
                .anyMatch(it -> Objects.nonNull(hourEntity) &&
                        Integer.parseInt(hourEntity.getHour()) <= it.getTimeEnd() && Integer.parseInt(hourEntity.getHour()) >= it.getTimeBegin());
    }

    /**
     * 获取该区间的电价
     *
     * @param hourEntity 时数据
     * @param periods    时条
     * @return 结果
     */
    private BigDecimal electricityPrice(ProjectStaSubitemHourEntity hourEntity, List<ProjectCnfTimePeriodEntity> periods) {
        return periods.stream()
                .filter(it -> Integer.parseInt(hourEntity.getHour()) <= it.getTimeEnd() && Integer.parseInt(hourEntity.getHour()) >= it.getTimeBegin())
                .map(ProjectCnfTimePeriodEntity::getPrice)
                .findFirst()
                .orElse(BigDecimal.ZERO);
    }

    /**
     * 获取电价
     *
     * @param period 时条
     * @return 结果
     */
    private BigDecimal price(List<ProjectCnfTimePeriodEntity> period) {
        BigDecimal price = period.stream().map(ProjectCnfTimePeriodEntity::getPrice).findFirst().orElse(BigDecimal.ZERO);
        return price;
    }

    private void saveJobLog(Map<Long, JobLogSaveDTO> jobLogMap, Map<Long, Map<String, String>> tenantProjectMap) {
        jobLogMap.forEach((key, value) -> {
            String projectIds = String.join(",", tenantProjectMap.get(key).keySet());
            String projectNames = String.join(",", tenantProjectMap.get(key).values());
            JobLogSaveDTO jobLog = value.setProjectIds(projectIds).setProjectNames(projectNames);
            jobLogApi.saveLog(jobLog);
        });
    }

    public static void main(String[] args) {
        ProjectStaSubitemYearEntity insertEntity = new ProjectStaSubitemYearEntity();
        insertEntity.setProjectGasUsageTotal(new BigDecimal(12));
        insertEntity.setProjectCarbonGasusageco2Total(insertEntity.getProjectGasUsageTotal().multiply(new BigDecimal("0.00218")));
        System.out.println("test");
    }

    /**
     * 获取充电桩电度费
     *
     * @param data 小时数据
     * @param cnf  尖峰谷平配置
     * @return
     */
    private BigDecimal getDDFee(List<ProjectStaSubitemHourEntity> data, List<ProjectCnfTimePeriodEntity> cnf) {
        List<ProjectCnfTimePeriodEntity> flat = cnf.stream().filter(it -> CharSequenceUtil.equals(it.getCode(), "flat")).toList();
        List<ProjectCnfTimePeriodEntity> tip = cnf.stream().filter(it -> CharSequenceUtil.equals(it.getCode(), "tip")).toList();
        List<ProjectCnfTimePeriodEntity> valley = cnf.stream().filter(it -> CharSequenceUtil.equals(it.getCode(), "valley")).toList();
        List<ProjectCnfTimePeriodEntity> peak = cnf.stream().filter(it -> CharSequenceUtil.equals(it.getCode(), "peak")).toList();

        BigDecimal flatSum = data.stream().filter(it -> condition(it, flat)).map(ProjectStaSubitemHourEntity::getProjectElectricitySubChargeEnergyTotal).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal tipSum = data.stream().filter(it -> condition(it, tip)).map(ProjectStaSubitemHourEntity::getProjectElectricitySubChargeEnergyTotal).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal valleySum = data.stream().filter(it -> condition(it, valley)).map(ProjectStaSubitemHourEntity::getProjectElectricitySubChargeEnergyTotal).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal peakSum = data.stream().filter(it -> condition(it, peak)).map(ProjectStaSubitemHourEntity::getProjectElectricitySubChargeEnergyTotal).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
        return NumberUtil.add(
                flatSum.multiply(price(flat)),
                tipSum.multiply(price(tip)),
                valleySum.multiply(price(valley)),
                peakSum.multiply(price(peak))
        );
    }

}



