package com.landleaf.energy.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.landleaf.bms.api.ProjectApi;
import com.landleaf.bms.api.dto.ProjectDetailsResponse;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.comm.exception.BusinessException;
import com.landleaf.energy.domain.entity.*;
import com.landleaf.energy.service.*;
import com.landleaf.job.api.JobLogApi;
import com.landleaf.job.api.dto.JobLogSaveDTO;
import com.landleaf.job.api.dto.JobRpcRequest;
import com.landleaf.monitor.api.MonitorApi;
import com.landleaf.monitor.dto.DeviceMonitorVO;
import com.landleaf.oauth.api.TenantApi;
import com.landleaf.oauth.api.dto.TenantInfoResponse;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.landleaf.comm.constance.CommonConstant.JOB_EXEC_ERROR;
import static com.landleaf.comm.constance.CommonConstant.JOB_EXEC_SUCCESS;

@Service
@Slf4j
public class StaSubareaStatisticsServiceImpl implements StaSubareaStatisticsService {
    DateTimeFormatter hourFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH");
    @Resource
    private ProjectKpiConfigService projectKpiConfigServiceImpl;

    @Resource
    private ProjectCnfSubareaService projectCnfSubareaServiceImpl;

    @Resource
    private ProjectSubareaDeviceService projectSubareaDeviceServiceImpl;

    @Resource
    private MonitorApi monitorApi;

    @Resource
    private ProjectApi projectApi;

    @Resource
    private TenantApi tenantApi;

    @Resource
    private JobLogApi jobLogApi;

    @Resource
    private ProjectStaSubareaHourService projectStaSubareaHourServiceImpl;

    @Resource
    private ProjectStaSubareaDayService projectStaSubareaDayServiceImpl;

    @Resource
    private ProjectStaSubareaMonthService projectStaSubareaMonthServiceImpl;

    @Resource
    private ProjectStaSubareaYearService projectStaSubareaYearServiceImpl;

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
        List<ProjectKpiConfigEntity> kpiConfList = projectKpiConfigServiceImpl.list(new QueryWrapper<ProjectKpiConfigEntity>().lambda().like(ProjectKpiConfigEntity::getCode, "area%").eq(ProjectKpiConfigEntity::getStaIntervalHour, 1));

        if (CollectionUtils.isEmpty(kpiConfList)) {
            // 不需要统计， 返回即可
            return true;
        }

        Map<String, List<ProjectKpiConfigEntity>> kpiConfigMap = kpiConfList.stream().collect(Collectors.groupingBy(ProjectKpiConfigEntity::getKpiSubtype));

        // 通过kpiSubtype查找对应的subitem的配置
        List<String> kpiSubtypeList = kpiConfList.stream().filter(i -> StrUtil.isNotEmpty(i.getKpiSubtype())).map(ProjectKpiConfigEntity::getKpiSubtype).distinct().collect(Collectors.toList());
        List<ProjectCnfSubareaEntity> subareaConfList = projectCnfSubareaServiceImpl.list(new QueryWrapper<ProjectCnfSubareaEntity>().lambda().in(ProjectCnfSubareaEntity::getKpiSubtype, kpiSubtypeList));

        if (CollectionUtils.isEmpty(subareaConfList)) {
            // 不需要统计， 返回即可
            return true;
        }
        Map<String, List<ProjectCnfSubareaEntity>> projSubareaConfMap = subareaConfList.stream().collect(Collectors.groupingBy(ProjectCnfSubareaEntity::getProjectId));
        List<Long> subareaIdList = subareaConfList.stream().map(ProjectCnfSubareaEntity::getId).collect(Collectors.toList());

        // 获取配置的设备列表
        List<ProjectSubareaDeviceEntity> deviceConfList = projectSubareaDeviceServiceImpl.list(new QueryWrapper<ProjectSubareaDeviceEntity>().lambda().in(ProjectSubareaDeviceEntity::getSubareadId, subareaIdList));

        Map<String, String> deviceCategoryMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(deviceConfList)) {
            // 获取设备对应的品类
            List<String> bizDeviceIdList = deviceConfList.stream().map(ProjectSubareaDeviceEntity::getDeviceId).distinct().collect(Collectors.toList());
            Response<List<DeviceMonitorVO>> resp = monitorApi.getDeviceListByBizIds(bizDeviceIdList);
            if (!resp.isSuccess()) {
                throw new BusinessException(resp.getErrorCode(), resp.getErrorMsg());
            }
            List<DeviceMonitorVO> deviceList = resp.getResult();
            if (!CollectionUtils.isEmpty(deviceList)) {
                deviceCategoryMap = deviceList.stream().collect(Collectors.toMap(DeviceMonitorVO::getBizDeviceId, DeviceMonitorVO::getBizCategoryId));
            }
        }
        Map<Long, Map<Long, List<ProjectSubareaDeviceEntity>>> deviceConfMap = deviceConfList.stream().collect(Collectors.groupingBy(ProjectSubareaDeviceEntity::getTenantId)).entrySet()
                .stream().collect(Collectors.toMap(k -> k.getKey(),
                        v -> v.getValue().stream().collect(Collectors.groupingBy(ProjectSubareaDeviceEntity::getSubareadId))));

        // 通过时间，处理对应信息
        for (LocalDateTime time : timeList) {
            Map<Long, JobLogSaveDTO> jobLogMap = new HashMap<>(8);
            Map<Long, Map<String, String>> tenantProjectMap = new HashMap<>(8);

            // 如果当前时间，已有对应的记录，删掉重新出
            String bizProjectId = null;
            Long tenantId = null;
            Map<String, List<ProjectKpiConfigEntity>> kpiConfDetailMap;
            Map<Long, List<ProjectSubareaDeviceEntity>> deviceConfDetailMap;
            try {
                for (Map.Entry<String, List<ProjectCnfSubareaEntity>> entry : projSubareaConfMap.entrySet()) {
                    bizProjectId = entry.getValue().get(0).getProjectId();
                    tenantId = entry.getValue().get(0).getTenantId();

                    // modify by hebin. 这个步骤提前， 如果没有对应的分区设备的配置， 直接continue，节省性能
                    if (!deviceConfMap.containsKey(tenantId)) {
                        log.info("当前租户没有对应的配置，直接返回， 租户编号为：{}", tenantId);
                        continue;
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

                    projectStaSubareaHourServiceImpl.remove(
                            new QueryWrapper<ProjectStaSubareaHourEntity>().lambda().
                                    eq(ProjectStaSubareaHourEntity::getBizProjectId, bizProjectId).
                                    eq(ProjectStaSubareaHourEntity::getYear, String.valueOf(time.getYear())).
                                    eq(ProjectStaSubareaHourEntity::getMonth, String.valueOf(time.getMonthValue())).
                                    eq(ProjectStaSubareaHourEntity::getDay, String.valueOf(time.getDayOfMonth())).
                                    eq(ProjectStaSubareaHourEntity::getHour, String.valueOf(time.getHour())));

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
                        if (!MapUtil.isEmpty(deviceConfDetailMap)) {
                            for (ProjectCnfSubareaEntity i : entry.getValue()) {
                                ProjectStaSubareaHourEntity insertEntity = new ProjectStaSubareaHourEntity();
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
                                insertEntity.setSubareaCode(String.valueOf(i.getId()));
                                insertEntity.setSubareaName(i.getName());

                                List<ProjectSubareaDeviceEntity> deviceList = deviceConfDetailMap.get(i.getId());
                                List<ProjectKpiConfigEntity> kpiList = kpiConfigMap.get(i.getKpiSubtype());
                                if (!CollectionUtils.isEmpty(kpiList)) {
                                    // 计算设备得值
                                    List<ProjectStaDeviceAirHourEntity> airList;
                                    List<ProjectStaDeviceElectricityHourEntity> electricityList;
                                    List<ProjectStaDeviceWaterHourEntity> waterList;
                                    List<ProjectStaDeviceGasHourEntity> gasList;
                                    Map<String, Object> deviceStatus = new HashMap<>();
                                    if (!CollectionUtils.isEmpty(deviceList)) {
                                        // 查询设备的小时信息，从4张表查
                                        airList = projectStaDeviceAirHourServiceImpl.list(new QueryWrapper<ProjectStaDeviceAirHourEntity>()
                                                .lambda().in(ProjectStaDeviceAirHourEntity::getBizDeviceId, deviceList.stream().map(ProjectSubareaDeviceEntity::getDeviceId).collect(Collectors.toList()))
                                                .eq(ProjectStaDeviceAirHourEntity::getYear, String.valueOf(time.getYear())).eq(ProjectStaDeviceAirHourEntity::getMonth, String.valueOf(time.getMonthValue())).eq(ProjectStaDeviceAirHourEntity::getDay, String.valueOf(time.getDayOfMonth()))
                                                .eq(ProjectStaDeviceAirHourEntity::getHour, String.valueOf(time.getHour())));
                                        if (!CollectionUtils.isEmpty(airList)) {
                                            deviceStatus.putAll(airList.stream().collect(Collectors.toMap(ProjectStaDeviceAirHourEntity::getBizDeviceId, j -> j, (v1, v2) -> v1)));
                                        }
                                        electricityList = projectStaDeviceElectricityHourServiceImpl.list(new QueryWrapper<ProjectStaDeviceElectricityHourEntity>()
                                                .lambda().in(ProjectStaDeviceElectricityHourEntity::getBizDeviceId, deviceList.stream().map(ProjectSubareaDeviceEntity::getDeviceId).collect(Collectors.toList()))
                                                .eq(ProjectStaDeviceElectricityHourEntity::getYear, String.valueOf(time.getYear())).eq(ProjectStaDeviceElectricityHourEntity::getMonth, String.valueOf(time.getMonthValue())).eq(ProjectStaDeviceElectricityHourEntity::getDay, String.valueOf(time.getDayOfMonth()))
                                                .eq(ProjectStaDeviceElectricityHourEntity::getHour, String.valueOf(time.getHour())));
                                        if (!CollectionUtils.isEmpty(electricityList)) {
                                            deviceStatus.putAll(electricityList.stream().collect(Collectors.toMap(ProjectStaDeviceElectricityHourEntity::getBizDeviceId, j -> j, (v1, v2) -> v1)));
                                        }
                                        waterList = projectStaDeviceWaterHourServiceImpl.list(new QueryWrapper<ProjectStaDeviceWaterHourEntity>()
                                                .lambda().in(ProjectStaDeviceWaterHourEntity::getBizDeviceId, deviceList.stream().map(ProjectSubareaDeviceEntity::getDeviceId).collect(Collectors.toList()))
                                                .eq(ProjectStaDeviceWaterHourEntity::getYear, String.valueOf(time.getYear())).eq(ProjectStaDeviceWaterHourEntity::getMonth, String.valueOf(time.getMonthValue())).eq(ProjectStaDeviceWaterHourEntity::getDay, String.valueOf(time.getDayOfMonth()))
                                                .eq(ProjectStaDeviceWaterHourEntity::getHour, String.valueOf(time.getHour())));
                                        if (!CollectionUtils.isEmpty(waterList)) {
                                            deviceStatus.putAll(waterList.stream().collect(Collectors.toMap(ProjectStaDeviceWaterHourEntity::getBizDeviceId, j -> j, (v1, v2) -> v1)));
                                        }
                                        gasList = projectStaDeviceGasHourServiceImpl.list(new QueryWrapper<ProjectStaDeviceGasHourEntity>()
                                                .lambda().in(ProjectStaDeviceGasHourEntity::getBizDeviceId, deviceList.stream().map(ProjectSubareaDeviceEntity::getDeviceId).collect(Collectors.toList()))
                                                .eq(ProjectStaDeviceGasHourEntity::getYear, String.valueOf(time.getYear())).eq(ProjectStaDeviceGasHourEntity::getMonth, String.valueOf(time.getMonthValue())).eq(ProjectStaDeviceGasHourEntity::getDay, String.valueOf(time.getDayOfMonth()))
                                                .eq(ProjectStaDeviceGasHourEntity::getHour, String.valueOf(time.getHour())));
                                        if (!CollectionUtils.isEmpty(gasList)) {
                                            deviceStatus.putAll(gasList.stream().collect(Collectors.toMap(ProjectStaDeviceGasHourEntity::getBizDeviceId, j -> j, (v1, v2) -> v1)));
                                        }
                                    }
                                    JSONObject deviceObj = JSONUtil.parseObj(JSONUtil.toJsonStr(deviceStatus));
                                    for (ProjectKpiConfigEntity tempKpi : kpiList) {
                                        if ("area.electricity.energyUsage.total".equals(tempKpi.getCode())) {
                                            BigDecimal val = BigDecimal.ZERO;
                                            // 计算电量，取值包含energymeter.epimport.total的
                                            if (!CollectionUtils.isEmpty(deviceList)) {
                                                val = deviceList.stream().filter(j -> deviceObj.containsKey(j.getDeviceId()) && deviceObj.getJSONObject(j.getDeviceId()).containsKey("energymeterEpimportTotal")).map(j -> {
                                                    return deviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("energymeterEpimportTotal").multiply(new BigDecimal(j.getComputeTag()));
                                                }).filter(v -> null != v).reduce(BigDecimal.ZERO, BigDecimal::add);
                                            }
                                            insertEntity.setKpiCode(tempKpi.getCode());
                                            insertEntity.setStaValue(val);
                                        } else if ("area.water.usage.total".equals(tempKpi.getCode())) {
                                            BigDecimal val = BigDecimal.ZERO;
                                            // 计算用水量，取值包含watermeter.usage.total的
                                            if (!CollectionUtils.isEmpty(deviceList)) {
                                                val = deviceList.stream().filter(j -> deviceObj.containsKey(j.getDeviceId()) && deviceObj.getJSONObject(j.getDeviceId()).containsKey("watermeterUsageTotal")).map(j -> {
                                                    return deviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("watermeterUsageTotal").multiply(new BigDecimal(j.getComputeTag()));
                                                }).filter(v -> null != v).reduce(BigDecimal.ZERO, BigDecimal::add);
                                            }
                                            insertEntity.setKpiCode(tempKpi.getCode());
                                            insertEntity.setStaValue(val);
                                        }
                                    }
                                    // 正常插入就行
                                    projectStaSubareaHourServiceImpl.save(insertEntity);
                                }
                            }
                        }
                    } catch (Exception e) {
                        jobLogMap.get(tenantId).setStatus(JOB_EXEC_ERROR);
                        throw e;
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
            // 小时的时间，应该是yyyy-MM-dd
            for (String time : times) {
                timeList.add(LocalDateTime.parse(time + " 00", hourFormatter).withHour(0).withMinute(0).withSecond(0).withNano(0));
            }
        } else {
            timeList.add(LocalDateTime.now().minusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0));
        }
        // 获取需要分享处理的kpicode
        List<ProjectKpiConfigEntity> kpiConfList = projectKpiConfigServiceImpl.list(new QueryWrapper<ProjectKpiConfigEntity>().lambda().like(ProjectKpiConfigEntity::getCode, "area%").eq(ProjectKpiConfigEntity::getStaIntervalYmd, 1));

        if (CollectionUtils.isEmpty(kpiConfList)) {
            // 不需要统计， 返回即可
            log.error("获取kpi失败");
            return true;
        }
        Map<String, List<ProjectKpiConfigEntity>> kpiConfigMap = kpiConfList.stream().collect(Collectors.groupingBy(ProjectKpiConfigEntity::getKpiSubtype));

        // 通过kpiSubtype查找对应的subitem的配置
        List<String> kpiSubtypeList = kpiConfList.stream().filter(i -> StrUtil.isNotEmpty(i.getKpiSubtype())).map(ProjectKpiConfigEntity::getKpiSubtype).distinct().collect(Collectors.toList());
        List<ProjectCnfSubareaEntity> subareaConfList = projectCnfSubareaServiceImpl.list(new QueryWrapper<ProjectCnfSubareaEntity>().lambda().in(ProjectCnfSubareaEntity::getKpiSubtype, kpiSubtypeList));

        if (CollectionUtils.isEmpty(subareaConfList)) {
            // 不需要统计， 返回即可
            log.error("获取subareaConfList失败");
            return true;
        }

        Map<String, List<ProjectCnfSubareaEntity>> projSubareaConfMap = subareaConfList.stream().collect(Collectors.groupingBy(ProjectCnfSubareaEntity::getProjectId));

        List<Long> subareaIdList = subareaConfList.stream().map(ProjectCnfSubareaEntity::getId).collect(Collectors.toList());

        // 获取配置的设备列表
        List<ProjectSubareaDeviceEntity> deviceConfList = projectSubareaDeviceServiceImpl.list(new QueryWrapper<ProjectSubareaDeviceEntity>().lambda().in(ProjectSubareaDeviceEntity::getSubareadId, subareaIdList));
        Map<Long, Map<Long, List<ProjectSubareaDeviceEntity>>> deviceConfMap = deviceConfList.stream().collect(Collectors.groupingBy(ProjectSubareaDeviceEntity::getTenantId)).entrySet()
                .stream().collect(Collectors.toMap(k -> k.getKey(),
                        v -> v.getValue().stream().collect(Collectors.groupingBy(ProjectSubareaDeviceEntity::getSubareadId))));
        // 通过时间，处理对应信息
        for (LocalDateTime time : timeList) {
            Map<Long, JobLogSaveDTO> jobLogMap = new HashMap<>(8);
            Map<Long, Map<String, String>> tenantProjectMap = new HashMap<>(8);

//            List<ProjectStaSubareaHourEntity> records = projectStaSubareaHourServiceImpl.
//                    list(new QueryWrapper<ProjectStaSubareaHourEntity>().lambda().eq(ProjectStaSubareaHourEntity::getYear, String.valueOf(time.getYear())).
//                            eq(ProjectStaSubareaHourEntity::getMonth, String.valueOf(time.getMonthValue())).
//                            eq(ProjectStaSubareaHourEntity::getDay, String.valueOf(time.getDayOfMonth())));
//            Map<String, Map<String, List<ProjectStaSubareaHourEntity>>> recordsMap = records.stream()
//                    .collect(Collectors.groupingBy(ProjectStaSubareaHourEntity::getBizProjectId)).entrySet()
//                    .stream().collect(Collectors.toMap(k -> k.getKey(),
//                            v -> v.getValue().stream().collect(Collectors.groupingBy(ProjectStaSubareaHourEntity::getSubareaCode))));

            // 如果当前时间，已有对应的记录，删掉重新出
            String bizProjectId = null;
            Long tenantId = null;
            Map<String, List<ProjectKpiConfigEntity>> kpiConfDetailMap;
            Map<Long, List<ProjectSubareaDeviceEntity>> deviceConfDetailMap;
            try {
                for (Map.Entry<String, List<ProjectCnfSubareaEntity>> entry : projSubareaConfMap.entrySet()) {
                    deviceConfDetailMap = null;
                    bizProjectId = entry.getValue().get(0).getProjectId();
                    tenantId = entry.getValue().get(0).getTenantId();

                    // modify by hebin. 这个步骤提前， 如果没有对应的分区设备的配置， 直接continue，节省性能
                    if (!deviceConfMap.containsKey(tenantId)) {
                        log.info("当前租户没有对应的配置，直接返回， 租户编号为：{}", tenantId);
                        continue;
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
                        log.error("请求的tenantId与配置tenantId不一致，返回。");
                        continue;
                    }

                    if (CollectionUtil.isNotEmpty(request.getProjectList()) && !request.getProjectList().contains(bizProjectId)) {
                        log.error("请求的bizProjectId与配置bizProjectId不一致，返回。{}", bizProjectId);
                        continue;
                    }

                    projectStaSubareaDayServiceImpl.remove(
                            new QueryWrapper<ProjectStaSubareaDayEntity>().lambda().
                                    eq(ProjectStaSubareaDayEntity::getBizProjectId, bizProjectId).
                                    eq(ProjectStaSubareaDayEntity::getYear, String.valueOf(time.getYear())).
                                    eq(ProjectStaSubareaDayEntity::getMonth, String.valueOf(time.getMonthValue())).
                                    eq(ProjectStaSubareaDayEntity::getDay, String.valueOf(time.getDayOfMonth())));

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

                    try {
                        // 当前所有的kpiConf有了，查询对应的设备
                        deviceConfDetailMap = deviceConfMap.get(tenantId);
                        if (MapUtil.isEmpty(deviceConfDetailMap)) {
                            log.error("存在分区配置，但分区没有任何的设备信息，返回: {}", bizProjectId);
                            continue;
                        }

//                        Map<String, List<ProjectStaSubareaHourEntity>> hourDataListMap = recordsMap.get(bizProjectId);
//                        if (!MapUtil.isEmpty(hourDataListMap)) {
                        for (ProjectCnfSubareaEntity i : entry.getValue()) {
                            String subareaCode = String.valueOf(i.getId());
                            ProjectStaSubareaDayEntity insertEntity = new ProjectStaSubareaDayEntity();
                            insertEntity.setDay(String.valueOf(time.getDayOfMonth()));
                            insertEntity.setBizProjectId(bizProjectId);
                            insertEntity.setMonth(String.valueOf(time.getMonthValue()));
                            insertEntity.setYear(String.valueOf(time.getYear()));
                            insertEntity.setProjectCode(projInfo.getCode());
                            insertEntity.setProjectName(projInfo.getName());
                            insertEntity.setTenantId(tenantId);
                            insertEntity.setTenantCode(tenantInfo.getCode());
                            insertEntity.setStaTime(Timestamp.valueOf(time));
                            insertEntity.setSubareaCode(subareaCode);
                            insertEntity.setSubareaName(i.getName());

                            List<ProjectKpiConfigEntity> kpiList = kpiConfigMap.get(i.getKpiSubtype());

                            List<ProjectSubareaDeviceEntity> deviceList = deviceConfDetailMap.get(i.getId());

                            List<ProjectStaDeviceAirDayEntity> airList;
                            List<ProjectStaDeviceElectricityDayEntity> electricityList;
                            List<ProjectStaDeviceWaterDayEntity> waterList;
                            List<ProjectStaDeviceGasDayEntity> gasList;
                            Map<String, Object> deviceStatus = new HashMap<>();
                            if (!CollectionUtils.isEmpty(deviceList)) {
                                // 查询设备的小时信息，从4张表查
                                airList = projectStaDeviceAirDayServiceImpl.list(new QueryWrapper<ProjectStaDeviceAirDayEntity>()
                                        .lambda().in(ProjectStaDeviceAirDayEntity::getBizDeviceId, deviceList.stream().map(ProjectSubareaDeviceEntity::getDeviceId).collect(Collectors.toList()))
                                        .eq(ProjectStaDeviceAirDayEntity::getYear, String.valueOf(time.getYear())).eq(ProjectStaDeviceAirDayEntity::getMonth, String.valueOf(time.getMonthValue())).eq(ProjectStaDeviceAirDayEntity::getDay, String.valueOf(time.getDayOfMonth()))
                                );
                                if (!CollectionUtils.isEmpty(airList)) {
                                    deviceStatus.putAll(airList.stream().collect(Collectors.toMap(ProjectStaDeviceAirDayEntity::getBizDeviceId, j -> j, (v1, v2) -> v1)));
                                }
                                electricityList = projectStaDeviceElectricityDayServiceImpl.list(new QueryWrapper<ProjectStaDeviceElectricityDayEntity>()
                                        .lambda().in(ProjectStaDeviceElectricityDayEntity::getBizDeviceId, deviceList.stream().map(ProjectSubareaDeviceEntity::getDeviceId).collect(Collectors.toList()))
                                        .eq(ProjectStaDeviceElectricityDayEntity::getYear, String.valueOf(time.getYear())).eq(ProjectStaDeviceElectricityDayEntity::getMonth, String.valueOf(time.getMonthValue())).eq(ProjectStaDeviceElectricityDayEntity::getDay, String.valueOf(time.getDayOfMonth()))
                                );
                                if (!CollectionUtils.isEmpty(electricityList)) {
                                    deviceStatus.putAll(electricityList.stream().collect(Collectors.toMap(ProjectStaDeviceElectricityDayEntity::getBizDeviceId, j -> j, (v1, v2) -> v1)));
                                }
                                waterList = projectStaDeviceWaterDayServiceImpl.list(new QueryWrapper<ProjectStaDeviceWaterDayEntity>()
                                        .lambda().in(ProjectStaDeviceWaterDayEntity::getBizDeviceId, deviceList.stream().map(ProjectSubareaDeviceEntity::getDeviceId).collect(Collectors.toList()))
                                        .eq(ProjectStaDeviceWaterDayEntity::getYear, String.valueOf(time.getYear())).eq(ProjectStaDeviceWaterDayEntity::getMonth, String.valueOf(time.getMonthValue())).eq(ProjectStaDeviceWaterDayEntity::getDay, String.valueOf(time.getDayOfMonth()))
                                );
                                if (!CollectionUtils.isEmpty(waterList)) {
                                    deviceStatus.putAll(waterList.stream().collect(Collectors.toMap(ProjectStaDeviceWaterDayEntity::getBizDeviceId, j -> j, (v1, v2) -> v1)));
                                }
                                gasList = projectStaDeviceGasDayServiceImpl.list(new QueryWrapper<ProjectStaDeviceGasDayEntity>()
                                        .lambda().in(ProjectStaDeviceGasDayEntity::getBizDeviceId, deviceList.stream().map(ProjectSubareaDeviceEntity::getDeviceId).collect(Collectors.toList()))
                                        .eq(ProjectStaDeviceGasDayEntity::getYear, String.valueOf(time.getYear())).eq(ProjectStaDeviceGasDayEntity::getMonth, String.valueOf(time.getMonthValue())).eq(ProjectStaDeviceGasDayEntity::getDay, String.valueOf(time.getDayOfMonth()))
                                );
                                if (!CollectionUtils.isEmpty(gasList)) {
                                    deviceStatus.putAll(gasList.stream().collect(Collectors.toMap(ProjectStaDeviceGasDayEntity::getBizDeviceId, j -> j, (v1, v2) -> v1)));
                                }
                            }
                            JSONObject deviceObj = JSONUtil.parseObj(JSONUtil.toJsonStr(deviceStatus));

                            if (!CollectionUtils.isEmpty(kpiList)) {
                                // 计算设备得值
                                for (ProjectKpiConfigEntity tempKpi : kpiList) {
                                    BigDecimal val = BigDecimal.ZERO;
                                    if ("area.electricity.energyUsage.total".equals(tempKpi.getCode())) {
                                        // 计算电量，取值包含energymeter.epimport.total的
                                        if (!CollectionUtils.isEmpty(deviceList)) {
                                            val = deviceList.stream().filter(j -> deviceObj.containsKey(j.getDeviceId()) && deviceObj.getJSONObject(j.getDeviceId()).containsKey("energymeterEpimportTotal")).map(j -> {
                                                return deviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("energymeterEpimportTotal").multiply(new BigDecimal(j.getComputeTag()));
                                            }).filter(v -> null != v).reduce(BigDecimal.ZERO, BigDecimal::add);
                                        }
                                        insertEntity.setKpiCode(tempKpi.getCode());
                                        insertEntity.setStaValue(val);
                                    } else if ("area.water.usage.total".equals(tempKpi.getCode())) {
                                        // 计算用水量，取值包含watermeter.usage.total的
                                        if (!CollectionUtils.isEmpty(deviceList)) {
                                            val = deviceList.stream().filter(j -> deviceObj.containsKey(j.getDeviceId()) && deviceObj.getJSONObject(j.getDeviceId()).containsKey("watermeterUsageTotal")).map(j -> {
                                                return deviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("watermeterUsageTotal").multiply(new BigDecimal(j.getComputeTag()));
                                            }).filter(v -> null != v).reduce(BigDecimal.ZERO, BigDecimal::add);
                                        }
                                        insertEntity.setKpiCode(tempKpi.getCode());
                                        insertEntity.setStaValue(val);
                                    }
                                }
                                // 正常插入就行
                                projectStaSubareaDayServiceImpl.save(insertEntity);
                            }
                        }
//                        }
                    } catch (Exception e) {
                        jobLogMap.get(tenantId).setStatus(JOB_EXEC_ERROR);
                        log.error("分区日统计任务异常", e);
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
        List<ProjectKpiConfigEntity> kpiConfList = projectKpiConfigServiceImpl.list(new QueryWrapper<ProjectKpiConfigEntity>().lambda().like(ProjectKpiConfigEntity::getCode, "area%").eq(ProjectKpiConfigEntity::getStaIntervalYmd, 1));

        if (CollectionUtils.isEmpty(kpiConfList)) {
            // 不需要统计， 返回即可
            return true;
        }
        Map<String, List<ProjectKpiConfigEntity>> kpiConfigMap = kpiConfList.stream().collect(Collectors.groupingBy(ProjectKpiConfigEntity::getKpiSubtype));

        // 通过kpiSubtype查找对应的subitem的配置
        List<String> kpiSubtypeList = kpiConfList.stream().filter(i -> StrUtil.isNotEmpty(i.getKpiSubtype())).map(ProjectKpiConfigEntity::getKpiSubtype).distinct().collect(Collectors.toList());
        List<ProjectCnfSubareaEntity> subareaConfList = projectCnfSubareaServiceImpl.list(new QueryWrapper<ProjectCnfSubareaEntity>().lambda().in(ProjectCnfSubareaEntity::getKpiSubtype, kpiSubtypeList));

        if (CollectionUtils.isEmpty(subareaConfList)) {
            // 不需要统计， 返回即可
            return true;
        }

        Map<String, List<ProjectCnfSubareaEntity>> projSubareaConfMap = subareaConfList.stream().collect(Collectors.groupingBy(ProjectCnfSubareaEntity::getProjectId));

        List<Long> subareaIdList = subareaConfList.stream().map(ProjectCnfSubareaEntity::getId).collect(Collectors.toList());

        // 获取配置的设备列表
        List<ProjectSubareaDeviceEntity> deviceConfList = projectSubareaDeviceServiceImpl.list(new QueryWrapper<ProjectSubareaDeviceEntity>().lambda().in(ProjectSubareaDeviceEntity::getSubareadId, subareaIdList));
        Map<Long, Map<Long, List<ProjectSubareaDeviceEntity>>> deviceConfMap = deviceConfList.stream().collect(Collectors.groupingBy(ProjectSubareaDeviceEntity::getTenantId)).entrySet()
                .stream().collect(Collectors.toMap(k -> k.getKey(),
                        v -> v.getValue().stream().collect(Collectors.groupingBy(ProjectSubareaDeviceEntity::getSubareadId))));

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

//            List<ProjectStaSubareaDayEntity> records = projectStaSubareaDayServiceImpl.
//                    list(new QueryWrapper<ProjectStaSubareaDayEntity>().lambda().eq(ProjectStaSubareaDayEntity::getYear, String.valueOf(time.getYear())).
//                            eq(ProjectStaSubareaDayEntity::getMonth, String.valueOf(time.getMonthValue())));
//            Map<String, Map<String, List<ProjectStaSubareaDayEntity>>> recordsMap = records.stream()
//                    .collect(Collectors.groupingBy(ProjectStaSubareaDayEntity::getBizProjectId)).entrySet()
//                    .stream().collect(Collectors.toMap(k -> k.getKey(),
//                            v -> v.getValue().stream().collect(Collectors.groupingBy(ProjectStaSubareaDayEntity::getSubareaCode))));

            // 如果当前时间，已有对应的记录，删掉重新出
            String bizProjectId = null;
            Long tenantId = null;
            Map<String, List<ProjectKpiConfigEntity>> kpiConfDetailMap;
            Map<Long, List<ProjectSubareaDeviceEntity>> deviceConfDetailMap;

            try {
                for (Map.Entry<String, List<ProjectCnfSubareaEntity>> entry : projSubareaConfMap.entrySet()) {
                    bizProjectId = entry.getValue().get(0).getProjectId();
                    tenantId = entry.getValue().get(0).getTenantId();

                    // modify by hebin. 这个步骤提前， 如果没有对应的分区设备的配置， 直接continue，节省性能
                    if (!deviceConfMap.containsKey(tenantId)) {
                        log.info("当前租户没有对应的配置，直接返回， 租户编号为：{}", tenantId);
                        continue;
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

                    // 如果月报周期与当前时间不一致，返回
                    if (!String.valueOf(time.getDayOfMonth() - 1).equals(tenantInfo.getReportingCycle())) {
                        continue;
                    }

                    projectStaSubareaMonthServiceImpl.remove(
                            new QueryWrapper<ProjectStaSubareaMonthEntity>().lambda().
                                    eq(ProjectStaSubareaMonthEntity::getBizProjectId, bizProjectId).
                                    eq(ProjectStaSubareaMonthEntity::getYear, String.valueOf(currentYear)).
                                    eq(ProjectStaSubareaMonthEntity::getMonth, String.valueOf(currentMonth)));

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

                    try {
                        // 当前所有的kpiConf有了，查询对应的设备
                        deviceConfDetailMap = deviceConfMap.get(tenantId);
//                        Map<String, List<ProjectStaSubareaDayEntity>> dayDataListMap = recordsMap.get(bizProjectId);
//                        if (!MapUtil.isEmpty(dayDataListMap)) {
                        for (ProjectCnfSubareaEntity i : entry.getValue()) {
                            String subareaCode = String.valueOf(i.getId());
                            ProjectStaSubareaMonthEntity insertEntity = new ProjectStaSubareaMonthEntity();
                            insertEntity.setBizProjectId(bizProjectId);
                            insertEntity.setMonth(String.valueOf(currentMonth));
                            insertEntity.setYear(String.valueOf(currentYear));
                            insertEntity.setProjectCode(projInfo.getCode());
                            insertEntity.setProjectName(projInfo.getName());
                            insertEntity.setTenantId(tenantId);
                            insertEntity.setTenantCode(tenantInfo.getCode());
                            insertEntity.setStaTime(Timestamp.valueOf(currentYear + "-" + (currentMonth > 9 ? currentMonth : "0" + currentMonth) + "-01 00:00:00"));
                            insertEntity.setSubareaCode(subareaCode);
                            insertEntity.setSubareaName(i.getName());
                            List<ProjectKpiConfigEntity> kpiList = kpiConfigMap.get(i.getKpiSubtype());

                            List<ProjectSubareaDeviceEntity> deviceList = deviceConfDetailMap.get(i.getId());

                            List<ProjectStaDeviceAirMonthEntity> airList;
                            List<ProjectStaDeviceElectricityMonthEntity> electricityList;
                            List<ProjectStaDeviceWaterMonthEntity> waterList;
                            List<ProjectStaDeviceGasMonthEntity> gasList;

                            Map<String, Object> deviceStatus = new HashMap<>();
                            if (!CollectionUtils.isEmpty(deviceList)) {
                                // 查询设备的小时信息，从4张表查
                                airList = projectStaDeviceAirMonthServiceImpl.list(new QueryWrapper<ProjectStaDeviceAirMonthEntity>()
                                        .lambda().in(ProjectStaDeviceAirMonthEntity::getBizDeviceId, deviceList.stream().map(ProjectSubareaDeviceEntity::getDeviceId).collect(Collectors.toList()))
                                        .eq(ProjectStaDeviceAirMonthEntity::getYear, String.valueOf(currentYear)).eq(ProjectStaDeviceAirMonthEntity::getMonth, String.valueOf(currentMonth))
                                );
                                if (!CollectionUtils.isEmpty(airList)) {
                                    deviceStatus.putAll(airList.stream().collect(Collectors.toMap(ProjectStaDeviceAirMonthEntity::getBizDeviceId, j -> j, (v1, v2) -> v1)));
                                }
                                electricityList = projectStaDeviceElectricityMonthServiceImpl.list(new QueryWrapper<ProjectStaDeviceElectricityMonthEntity>()
                                        .lambda().in(ProjectStaDeviceElectricityMonthEntity::getBizDeviceId, deviceList.stream().map(ProjectSubareaDeviceEntity::getDeviceId).collect(Collectors.toList()))
                                        .eq(ProjectStaDeviceElectricityMonthEntity::getYear, String.valueOf(currentYear)).eq(ProjectStaDeviceElectricityMonthEntity::getMonth, String.valueOf(currentMonth))
                                );
                                if (!CollectionUtils.isEmpty(electricityList)) {
                                    deviceStatus.putAll(electricityList.stream().collect(Collectors.toMap(ProjectStaDeviceElectricityMonthEntity::getBizDeviceId, j -> j, (v1, v2) -> v1)));
                                }
                                waterList = projectStaDeviceWaterMonthServiceImpl.list(new QueryWrapper<ProjectStaDeviceWaterMonthEntity>()
                                        .lambda().in(ProjectStaDeviceWaterMonthEntity::getBizDeviceId, deviceList.stream().map(ProjectSubareaDeviceEntity::getDeviceId).collect(Collectors.toList()))
                                        .eq(ProjectStaDeviceWaterMonthEntity::getYear, String.valueOf(currentYear)).eq(ProjectStaDeviceWaterMonthEntity::getMonth, String.valueOf(currentMonth))
                                );
                                if (!CollectionUtils.isEmpty(waterList)) {
                                    deviceStatus.putAll(waterList.stream().collect(Collectors.toMap(ProjectStaDeviceWaterMonthEntity::getBizDeviceId, j -> j, (v1, v2) -> v1)));
                                }
                                gasList = projectStaDeviceGasMonthServiceImpl.list(new QueryWrapper<ProjectStaDeviceGasMonthEntity>()
                                        .lambda().in(ProjectStaDeviceGasMonthEntity::getBizDeviceId, deviceList.stream().map(ProjectSubareaDeviceEntity::getDeviceId).collect(Collectors.toList()))
                                        .eq(ProjectStaDeviceGasMonthEntity::getYear, String.valueOf(currentYear)).eq(ProjectStaDeviceGasMonthEntity::getMonth, String.valueOf(currentMonth))
                                );
                                if (!CollectionUtils.isEmpty(gasList)) {
                                    deviceStatus.putAll(gasList.stream().collect(Collectors.toMap(ProjectStaDeviceGasMonthEntity::getBizDeviceId, j -> j, (v1, v2) -> v1)));
                                }
                            }
                            JSONObject deviceObj = JSONUtil.parseObj(JSONUtil.toJsonStr(deviceStatus));

                            if (!CollectionUtils.isEmpty(kpiList)) {
                                // 计算设备得值
                                for (ProjectKpiConfigEntity tempKpi : kpiList) {
                                    BigDecimal val = BigDecimal.ZERO;
                                    if ("area.electricity.energyUsage.total".equals(tempKpi.getCode())) {
                                        // 计算电量，取值包含energymeter.epimport.total的
                                        if (!CollectionUtils.isEmpty(deviceList)) {
                                            val = deviceList.stream().filter(j -> deviceObj.containsKey(j.getDeviceId()) && deviceObj.getJSONObject(j.getDeviceId()).containsKey("energymeterEpimportTotal")).map(j -> {
                                                return deviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("energymeterEpimportTotal").multiply(new BigDecimal(j.getComputeTag()));
                                            }).filter(v -> null != v).reduce(BigDecimal.ZERO, BigDecimal::add);
                                        }
                                        insertEntity.setKpiCode(tempKpi.getCode());
                                        insertEntity.setStaValue(val);
                                    } else if ("area.water.usage.total".equals(tempKpi.getCode())) {
                                        // 计算用水量，取值包含watermeter.usage.total的
                                        if (!CollectionUtils.isEmpty(deviceList)) {
                                            val = deviceList.stream().filter(j -> deviceObj.containsKey(j.getDeviceId()) && deviceObj.getJSONObject(j.getDeviceId()).containsKey("watermeterUsageTotal")).map(j -> {
                                                return deviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("watermeterUsageTotal").multiply(new BigDecimal(j.getComputeTag()));
                                            }).filter(v -> null != v).reduce(BigDecimal.ZERO, BigDecimal::add);
                                        }
                                        insertEntity.setKpiCode(tempKpi.getCode());
                                        insertEntity.setStaValue(val);
                                    }
                                }
                                // 正常插入就行
                                projectStaSubareaMonthServiceImpl.save(insertEntity);
                            }
//                            }
                        }
                    } catch (Exception e) {
                        jobLogMap.get(tenantId).setStatus(JOB_EXEC_ERROR);
                        throw e;
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
                timeList.add(LocalDateTime.parse(time + " 00", hourFormatter).withHour(0).withMinute(0).withSecond(0).withNano(0));
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
        List<ProjectKpiConfigEntity> kpiConfList = projectKpiConfigServiceImpl.list(new QueryWrapper<ProjectKpiConfigEntity>().lambda().like(ProjectKpiConfigEntity::getCode, "area%").eq(ProjectKpiConfigEntity::getStaIntervalYmd, 1));

        if (CollectionUtils.isEmpty(kpiConfList)) {
            // 不需要统计， 返回即可
            return true;
        }
        Map<String, List<ProjectKpiConfigEntity>> kpiConfigMap = kpiConfList.stream().collect(Collectors.groupingBy(ProjectKpiConfigEntity::getKpiSubtype));

        // 通过kpiSubtype查找对应的subitem的配置
        List<String> kpiSubtypeList = kpiConfList.stream().filter(i -> StrUtil.isNotEmpty(i.getKpiSubtype())).map(ProjectKpiConfigEntity::getKpiSubtype).distinct().collect(Collectors.toList());
        List<ProjectCnfSubareaEntity> subareaConfList = projectCnfSubareaServiceImpl.list(new QueryWrapper<ProjectCnfSubareaEntity>().lambda().in(ProjectCnfSubareaEntity::getKpiSubtype, kpiSubtypeList));

        if (CollectionUtils.isEmpty(subareaConfList)) {
            // 不需要统计， 返回即可
            return true;
        }

        Map<String, List<ProjectCnfSubareaEntity>> projSubareaConfMap = subareaConfList.stream().collect(Collectors.groupingBy(ProjectCnfSubareaEntity::getProjectId));

        List<Long> subareaIdList = subareaConfList.stream().map(ProjectCnfSubareaEntity::getId).collect(Collectors.toList());

        // 获取配置的设备列表
        List<ProjectSubareaDeviceEntity> deviceConfList = projectSubareaDeviceServiceImpl.list(new QueryWrapper<ProjectSubareaDeviceEntity>().lambda().in(ProjectSubareaDeviceEntity::getSubareadId, subareaIdList));
        Map<Long, Map<Long, List<ProjectSubareaDeviceEntity>>> deviceConfMap = deviceConfList.stream().collect(Collectors.groupingBy(ProjectSubareaDeviceEntity::getTenantId)).entrySet()
                .stream().collect(Collectors.toMap(k -> k.getKey(),
                        v -> v.getValue().stream().collect(Collectors.groupingBy(ProjectSubareaDeviceEntity::getSubareadId))));

        // 通过时间，处理对应信息
        for (LocalDateTime time : timeList) {
            Map<Long, JobLogSaveDTO> jobLogMap = new HashMap<>(8);
            Map<Long, Map<String, String>> tenantProjectMap = new HashMap<>(8);
            int currentYear = time.getYear();
            if (time.getMonthValue() == 12) {
                currentYear += 1;
            }


//            List<ProjectStaSubareaMonthEntity> records = projectStaSubareaMonthServiceImpl.
//                    list(new QueryWrapper<ProjectStaSubareaMonthEntity>().lambda().eq(ProjectStaSubareaMonthEntity::getYear, String.valueOf(time.getYear())));
//            Map<String, Map<String, List<ProjectStaSubareaMonthEntity>>> recordsMap = records.stream()
//                    .collect(Collectors.groupingBy(ProjectStaSubareaMonthEntity::getBizProjectId)).entrySet()
//                    .stream().collect(Collectors.toMap(k -> k.getKey(),
//                            v -> v.getValue().stream().collect(Collectors.groupingBy(ProjectStaSubareaMonthEntity::getSubareaCode))));

            // 如果当前时间，已有对应的记录，删掉重新出
            String bizProjectId = null;
            Long tenantId = null;
            Map<String, List<ProjectKpiConfigEntity>> kpiConfDetailMap;
            Map<Long, List<ProjectSubareaDeviceEntity>> deviceConfDetailMap;

            try {
                for (Map.Entry<String, List<ProjectCnfSubareaEntity>> entry : projSubareaConfMap.entrySet()) {
                    bizProjectId = entry.getValue().get(0).getProjectId();
                    tenantId = entry.getValue().get(0).getTenantId();

                    // modify by hebin. 这个步骤提前， 如果没有对应的分区设备的配置， 直接continue，节省性能
                    if (!deviceConfMap.containsKey(tenantId)) {
                        log.info("当前租户没有对应的配置，直接返回， 租户编号为：{}", tenantId);
                        continue;
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

                    // 如果月报周期与当前时间不一致，返回
                    if (!String.valueOf(time.getDayOfMonth() - 1).equals(tenantInfo.getReportingCycle())) {
                        continue;
                    }

                    projectStaSubareaYearServiceImpl.remove(
                            new QueryWrapper<ProjectStaSubareaYearEntity>().lambda().
                                    eq(ProjectStaSubareaYearEntity::getBizProjectId, bizProjectId).
                                    eq(ProjectStaSubareaYearEntity::getYear, String.valueOf(currentYear)));

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

                    try {
                        // 当前所有的kpiConf有了，查询对应的设备
                        deviceConfDetailMap = deviceConfMap.get(tenantId);
//                        Map<String, List<ProjectStaSubareaMonthEntity>> monthDataListMap = recordsMap.get(bizProjectId);
//                        if (!MapUtil.isEmpty(monthDataListMap)) {
                        for (ProjectCnfSubareaEntity i : entry.getValue()) {
                            String subareaCode = String.valueOf(i.getId());
                            ProjectStaSubareaYearEntity insertEntity = new ProjectStaSubareaYearEntity();
                            insertEntity.setBizProjectId(bizProjectId);
                            insertEntity.setYear(String.valueOf(currentYear));
                            insertEntity.setProjectCode(projInfo.getCode());
                            insertEntity.setProjectName(projInfo.getName());
                            insertEntity.setTenantId(tenantId);
                            insertEntity.setTenantCode(tenantInfo.getCode());
                            insertEntity.setStaTime(Timestamp.valueOf(time));
                            insertEntity.setStaTime(Timestamp.valueOf(currentYear + "-01-01 00:00:00"));
                            insertEntity.setSubareaCode(subareaCode);
                            insertEntity.setSubareaName(i.getName());

                            List<ProjectKpiConfigEntity> kpiList = kpiConfigMap.get(i.getKpiSubtype());

                            List<ProjectSubareaDeviceEntity> deviceList = deviceConfDetailMap.get(i.getId());

                            List<ProjectStaDeviceAirYearEntity> airList;
                            List<ProjectStaDeviceElectricityYearEntity> electricityList;
                            List<ProjectStaDeviceWaterYearEntity> waterList;
                            List<ProjectStaDeviceGasYearEntity> gasList;
                            Map<String, Object> deviceStatus = new HashMap<>();
                            if (!CollectionUtils.isEmpty(deviceList)) {
                                // 查询设备的小时信息，从4张表查
                                airList = projectStaDeviceAirYearServiceImpl.list(new QueryWrapper<ProjectStaDeviceAirYearEntity>()
                                        .lambda().in(ProjectStaDeviceAirYearEntity::getBizDeviceId, deviceList.stream().map(ProjectSubareaDeviceEntity::getDeviceId).collect(Collectors.toList()))
                                        .eq(ProjectStaDeviceAirYearEntity::getYear, String.valueOf(currentYear))
                                );
                                if (!CollectionUtils.isEmpty(airList)) {
                                    deviceStatus.putAll(airList.stream().collect(Collectors.toMap(ProjectStaDeviceAirYearEntity::getBizDeviceId, j -> j, (v1, v2) -> v1)));
                                }
                                electricityList = projectStaDeviceElectricityYearServiceImpl.list(new QueryWrapper<ProjectStaDeviceElectricityYearEntity>()
                                        .lambda().in(ProjectStaDeviceElectricityYearEntity::getBizDeviceId, deviceList.stream().map(ProjectSubareaDeviceEntity::getDeviceId).collect(Collectors.toList()))
                                        .eq(ProjectStaDeviceElectricityYearEntity::getYear, String.valueOf(currentYear))
                                );
                                if (!CollectionUtils.isEmpty(electricityList)) {
                                    deviceStatus.putAll(electricityList.stream().collect(Collectors.toMap(ProjectStaDeviceElectricityYearEntity::getBizDeviceId, j -> j, (v1, v2) -> v1)));
                                }
                                waterList = projectStaDeviceWaterYearServiceImpl.list(new QueryWrapper<ProjectStaDeviceWaterYearEntity>()
                                        .lambda().in(ProjectStaDeviceWaterYearEntity::getBizDeviceId, deviceList.stream().map(ProjectSubareaDeviceEntity::getDeviceId).collect(Collectors.toList()))
                                        .eq(ProjectStaDeviceWaterYearEntity::getYear, String.valueOf(currentYear))
                                );
                                if (!CollectionUtils.isEmpty(waterList)) {
                                    deviceStatus.putAll(waterList.stream().collect(Collectors.toMap(ProjectStaDeviceWaterYearEntity::getBizDeviceId, j -> j, (v1, v2) -> v1)));
                                }
                                gasList = projectStaDeviceGasYearServiceImpl.list(new QueryWrapper<ProjectStaDeviceGasYearEntity>()
                                        .lambda().in(ProjectStaDeviceGasYearEntity::getBizDeviceId, deviceList.stream().map(ProjectSubareaDeviceEntity::getDeviceId).collect(Collectors.toList()))
                                        .eq(ProjectStaDeviceGasYearEntity::getYear, String.valueOf(currentYear))
                                );
                                if (!CollectionUtils.isEmpty(gasList)) {
                                    deviceStatus.putAll(gasList.stream().collect(Collectors.toMap(ProjectStaDeviceGasYearEntity::getBizDeviceId, j -> j, (v1, v2) -> v1)));
                                }
                            }
                            JSONObject deviceObj = JSONUtil.parseObj(JSONUtil.toJsonStr(deviceStatus));
                            if (!CollectionUtils.isEmpty(kpiList)) {
                                // 计算设备得值
                                for (ProjectKpiConfigEntity tempKpi : kpiList) {
                                    BigDecimal val = BigDecimal.ZERO;
                                    if ("area.electricity.energyUsage.total".equals(tempKpi.getCode())) {
                                        // 计算电量，取值包含energymeter.epimport.total的
                                        if (!CollectionUtils.isEmpty(deviceList)) {
                                            val = deviceList.stream().filter(j -> deviceObj.containsKey(j.getDeviceId()) && deviceObj.getJSONObject(j.getDeviceId()).containsKey("energymeterEpimportTotal")).map(j -> {
                                                return deviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("energymeterEpimportTotal").multiply(new BigDecimal(j.getComputeTag()));
                                            }).filter(v -> null != v).reduce(BigDecimal.ZERO, BigDecimal::add);
                                        }
                                        insertEntity.setKpiCode(tempKpi.getCode());
                                        insertEntity.setStaValue(val);
                                    } else if ("area.water.usage.total".equals(tempKpi.getCode())) {
                                        // 计算用水量，取值包含watermeter.usage.total的
                                        if (!CollectionUtils.isEmpty(deviceList)) {
                                            val = deviceList.stream().filter(j -> deviceObj.containsKey(j.getDeviceId()) && deviceObj.getJSONObject(j.getDeviceId()).containsKey("watermeterUsageTotal")).map(j -> {
                                                return deviceObj.getJSONObject(j.getDeviceId()).getBigDecimal("watermeterUsageTotal").multiply(new BigDecimal(j.getComputeTag()));
                                            }).filter(v -> null != v).reduce(BigDecimal.ZERO, BigDecimal::add);
                                        }
                                        insertEntity.setKpiCode(tempKpi.getCode());
                                        insertEntity.setStaValue(val);
                                    }
                                }
                                // 正常插入就行
                                projectStaSubareaYearServiceImpl.save(insertEntity);
                            }
//                            }
                        }
                    } catch (Exception e) {
                        jobLogMap.get(tenantId).setStatus(JOB_EXEC_ERROR);
                        throw e;
                    }
                }
            } finally {
                saveJobLog(jobLogMap, tenantProjectMap);
            }
        }
        return true;
    }

    private void saveJobLog(Map<Long, JobLogSaveDTO> jobLogMap, Map<Long, Map<String, String>> tenantProjectMap) {
        jobLogMap.forEach((key, value) -> {
            String projectIds = String.join(",", tenantProjectMap.get(key).keySet());
            String projectNames = String.join(",", tenantProjectMap.get(key).values());
            JobLogSaveDTO jobLog = value.setProjectIds(projectIds).setProjectNames(projectNames);
            jobLogApi.saveLog(jobLog);
        });
    }
}
