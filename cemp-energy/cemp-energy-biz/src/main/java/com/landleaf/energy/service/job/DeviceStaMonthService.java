package com.landleaf.energy.service.job;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.landleaf.comm.constance.DeviceStaCategoryEnum;
import com.landleaf.energy.dal.mapper.*;
import com.landleaf.energy.domain.dto.StaDeviceMonthTaskContext;
import com.landleaf.energy.domain.entity.*;
import com.landleaf.monitor.api.DeviceStaApi;
import com.landleaf.monitor.api.dto.DeviceStaDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Yang
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DeviceStaMonthService {
    private final DeviceStaApi deviceStaApi;
    private final ProjectStaDeviceElectricityDayMapper projectStaDeviceElectricityDayMapper;
    private final ProjectStaDeviceElectricityMonthMapper projectStaDeviceElectricityMonthMapper;
    private final ProjectStaDeviceGasDayMapper projectStaDeviceGasDayMapper;
    private final ProjectStaDeviceGasMonthMapper projectStaDeviceGasMonthMapper;
    private final ProjectStaDeviceWaterDayMapper projectStaDeviceWaterDayMapper;
    private final ProjectStaDeviceWaterMonthMapper projectStaDeviceWaterMonthMapper;
    private final ProjectStaDeviceAirDayMapper projectStaDeviceAirDayMapper;
    private final ProjectStaDeviceAirMonthMapper projectStaDeviceAirMonthMapper;
    private final ProjectStaDeviceZnbDayMapper projectStaDeviceZnbDayMapper;
    private final ProjectStaDeviceZnbMonthMapper projectStaDeviceZnbMonthMapper;
    private final ProjectStaDeviceGscnDayMapper projectStaDeviceGscnDayMapper;
    private final ProjectStaDeviceGscnMonthMapper projectStaDeviceGscnMonthMapper;

    public String execute(Long tenantId, String tenantCode, String reportingCycle, DeviceStaCategoryEnum categoryEnum, LocalDateTime staTime, String projectIds) {
        LocalDateTime startTime;
        LocalDateTime endTime = LocalDateTime.of(staTime.getYear(), staTime.getMonthValue(), 1, 0, 0, 0);
        // 因为月报表统计周期的改动 day的查询逻辑不再只是同一个月 这边的startTime只用作手工数据查询和staTime生成，day的数据起止结束时间另用新字段
        LocalDateTime lastMonth = staTime.minusMonths(1L);
        LocalDateTime lastDay = staTime.minusDays(1L);
        if (staTime.getDayOfMonth() > 7) {
            startTime = LocalDateTime.of(staTime.getYear(), staTime.getMonthValue(), 1, 0, 0, 0);
        } else {
            startTime = LocalDateTime.of(lastMonth.getYear(), lastMonth.getMonthValue(), 1, 0, 0, 0);
        }
        LocalDateTime dayStartStaTime;
        LocalDateTime dayEndStaTime;
        if (StrUtil.equals("0", reportingCycle)) {
            // 自然月
            dayStartStaTime = LocalDateTime.of(lastMonth.getYear(), lastMonth.getMonthValue(), 1, 0, 0, 0);
            dayEndStaTime = LocalDateTime.of(lastMonth.getYear(), lastMonth.getMonthValue(), lastDay.getDayOfMonth(), 0, 0, 0);
        } else {
            dayStartStaTime = lastDay.minusMonths(1L).plusDays(1L);
            dayEndStaTime = lastDay;
        }
        StaDeviceMonthTaskContext context = new StaDeviceMonthTaskContext(tenantId, tenantCode, staTime, startTime, endTime, dayStartStaTime, dayEndStaTime);
        // 获取设备信息
        List<DeviceStaDTO> deviceStaDTOList = deviceStaApi.listStaDeviceByCategory(tenantId, categoryEnum.getCode()).getCheckedData();
        // 过滤要执行的项目id
        deviceStaDTOList = deviceStaDTOList.stream().filter(o -> StrUtil.contains(projectIds, o.getBizProjectId())).collect(Collectors.toList());
        String msg = "品类 " + categoryEnum.getCode() + " 统计设备 " + deviceStaDTOList.stream().map(DeviceStaDTO::getBizDeviceId).collect(Collectors.joining(","));
        if (CollectionUtil.isNotEmpty(deviceStaDTOList)) {
            // 统计设备数据并入库
            switch (categoryEnum) {
                case KTYKQ:
                    staAir(context, deviceStaDTOList);
                    break;
                case DB3PH:
                    staElectricity(context, deviceStaDTOList);
                    break;
                case RQB:
                    staGas(context, deviceStaDTOList);
                    break;
                case ZNSB:
                    staWater(context, deviceStaDTOList);
                    break;
                case ZNB:
                    staZnb(context, deviceStaDTOList);
                    break;
                case GSCN:
                    staGscn(context, deviceStaDTOList);
                    break;
                default:
                    throw new IllegalArgumentException("不支持的统计类型");
            }
        }
        return msg;
    }

    private void staGscn(StaDeviceMonthTaskContext context, List<DeviceStaDTO> deviceStaDTOList) {
        List<ProjectStaDeviceGscnMonthEntity> deviceList = new ArrayList<>();
        List<String> bizDeviceIds = deviceStaDTOList.stream().map(DeviceStaDTO::getBizDeviceId).collect(Collectors.toList());
        // 获取存在手工报表的设备
        List<String> manualDeviceIdList = projectStaDeviceGscnMonthMapper.selectList(
                new LambdaQueryWrapper<ProjectStaDeviceGscnMonthEntity>()
                        .select(ProjectStaDeviceGscnMonthEntity::getBizDeviceId)
                        .eq(ProjectStaDeviceGscnMonthEntity::getManualFlag, 1)
                        .eq(ProjectStaDeviceGscnMonthEntity::getYear, context.getYear())
                        .eq(ProjectStaDeviceGscnMonthEntity::getMonth, context.getMonth())
                        .in(ProjectStaDeviceGscnMonthEntity::getBizDeviceId, bizDeviceIds)
        ).stream().map(ProjectStaDeviceGscnMonthEntity::getBizDeviceId).toList();

        bizDeviceIds.removeAll(manualDeviceIdList);
        deviceStaDTOList.removeIf(o -> manualDeviceIdList.contains(o.getBizDeviceId()));
        if (bizDeviceIds.isEmpty()) {
            return;
        }

        Map<String, List<ProjectStaDeviceGscnDayEntity>> hourDataList = projectStaDeviceGscnDayMapper.selectList(new LambdaQueryWrapper<ProjectStaDeviceGscnDayEntity>()
                        .ge(ProjectStaDeviceGscnDayEntity::getStaTime, context.getDayStartStaTime())
                        .le(ProjectStaDeviceGscnDayEntity::getStaTime, context.getDayEndStaTime())
                        .in(ProjectStaDeviceGscnDayEntity::getBizDeviceId, bizDeviceIds))
                .stream()
                .collect(Collectors.groupingBy(ProjectStaDeviceGscnDayEntity::getBizDeviceId));

        for (DeviceStaDTO deviceStaDTO : deviceStaDTOList) {
            // 产品要求 没有数据给null
            ProjectStaDeviceGscnMonthEntity device = new ProjectStaDeviceGscnMonthEntity();
            deviceList.add(device);

            BeanUtil.copyProperties(deviceStaDTO, device);
            device.setTenantId(context.getTenantId()).setTenantCode(context.getTenantCode())
                    .setYear(context.getYear()).setMonth(context.getMonth()).setStaTime(context.getTimestamp());

            List<ProjectStaDeviceGscnDayEntity> deviceDayData = hourDataList.get(deviceStaDTO.getBizDeviceId());
            if (CollectionUtil.isEmpty(deviceDayData)) {
                continue;
            }
            deviceDayData.stream()
                    .map(ProjectStaDeviceGscnDayEntity::getGscnOnlineTimeTotal)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal::add)
                    .ifPresent(device::setGscnOnlineTimeTotal);
            deviceDayData.stream()
                    .map(ProjectStaDeviceGscnDayEntity::getGscnChargeTimeTotal)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal::add)
                    .ifPresent(device::setGscnChargeTimeTotal);
            deviceDayData.stream()
                    .map(ProjectStaDeviceGscnDayEntity::getGscnDischargeTimeTotal)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal::add)
                    .ifPresent(device::setGscnDischargeTimeTotal);
            deviceDayData.stream()
                    .map(ProjectStaDeviceGscnDayEntity::getGscnStandbyTimeTotal)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal::add)
                    .ifPresent(device::setGscnStandbyTimeTotal);
            deviceDayData.stream()
                    .map(ProjectStaDeviceGscnDayEntity::getGscnEpimportTotal)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal::add)
                    .ifPresent(device::setGscnEpimportTotal);
            deviceDayData.stream()
                    .map(ProjectStaDeviceGscnDayEntity::getGscnEpexportTotal)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal::add)
                    .ifPresent(device::setGscnEpexportTotal);
        }
        projectStaDeviceGscnMonthMapper.delete(new LambdaUpdateWrapper<ProjectStaDeviceGscnMonthEntity>()
                .eq(ProjectStaDeviceGscnMonthEntity::getYear, context.getYear())
                .eq(ProjectStaDeviceGscnMonthEntity::getMonth, context.getMonth())
                .in(ProjectStaDeviceGscnMonthEntity::getBizDeviceId, bizDeviceIds));
        projectStaDeviceGscnMonthMapper.insertBatchSomeColumn(deviceList);
    }

    private void staZnb(StaDeviceMonthTaskContext context, List<DeviceStaDTO> deviceStaDTOList) {
        List<ProjectStaDeviceZnbMonthEntity> deviceList = new ArrayList<>();
        List<String> bizDeviceIds = deviceStaDTOList.stream().map(DeviceStaDTO::getBizDeviceId).collect(Collectors.toList());
        // 获取存在手工报表的设备
        List<String> manualDeviceIdList = projectStaDeviceZnbMonthMapper.selectList(
                new LambdaQueryWrapper<ProjectStaDeviceZnbMonthEntity>()
                        .select(ProjectStaDeviceZnbMonthEntity::getBizDeviceId)
                        .eq(ProjectStaDeviceZnbMonthEntity::getManualFlag, 1)
                        .eq(ProjectStaDeviceZnbMonthEntity::getYear, context.getYear())
                        .eq(ProjectStaDeviceZnbMonthEntity::getMonth, context.getMonth())
                        .in(ProjectStaDeviceZnbMonthEntity::getBizDeviceId, bizDeviceIds)
        ).stream().map(ProjectStaDeviceZnbMonthEntity::getBizDeviceId).toList();

        bizDeviceIds.removeAll(manualDeviceIdList);
        deviceStaDTOList.removeIf(o -> manualDeviceIdList.contains(o.getBizDeviceId()));
        if (bizDeviceIds.isEmpty()) {
            return;
        }

        Map<String, List<ProjectStaDeviceZnbDayEntity>> dayDataList = projectStaDeviceZnbDayMapper.selectList(new LambdaQueryWrapper<ProjectStaDeviceZnbDayEntity>()
                        .ge(ProjectStaDeviceZnbDayEntity::getStaTime, context.getDayStartStaTime())
                        .le(ProjectStaDeviceZnbDayEntity::getStaTime, context.getDayEndStaTime())
                        .in(ProjectStaDeviceZnbDayEntity::getBizDeviceId, bizDeviceIds))
                .stream()
                .collect(Collectors.groupingBy(ProjectStaDeviceZnbDayEntity::getBizDeviceId));

        for (DeviceStaDTO deviceStaDTO : deviceStaDTOList) {
            // 产品要求 没有数据给null
            ProjectStaDeviceZnbMonthEntity device = new ProjectStaDeviceZnbMonthEntity();
            deviceList.add(device);

            BeanUtil.copyProperties(deviceStaDTO, device);
            device.setTenantId(context.getTenantId()).setTenantCode(context.getTenantCode())
                    .setYear(context.getYear()).setMonth(context.getMonth()).setStaTime(context.getTimestamp());

            List<ProjectStaDeviceZnbDayEntity> deviceDayData = dayDataList.get(deviceStaDTO.getBizDeviceId());
            if (CollectionUtil.isEmpty(deviceDayData)) {
                continue;
            }
            deviceDayData.stream()
                    .map(ProjectStaDeviceZnbDayEntity::getZnbOnlineTimeTotal)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal::add)
                    .ifPresent(device::setZnbOnlineTimeTotal);
            deviceDayData.stream()
                    .map(ProjectStaDeviceZnbDayEntity::getZnbRunningTimeTotal)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal::add)
                    .ifPresent(device::setZnbRunningTimeTotal);
            deviceDayData.stream()
                    .map(ProjectStaDeviceZnbDayEntity::getZnbEpexportTotal)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal::add)
                    .ifPresent(device::setZnbEpexportTotal);
            deviceDayData.stream()
                    .map(ProjectStaDeviceZnbDayEntity::getZnbEptoHourTotal)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal::add)
                    .ifPresent(device::setZnbEptoHourTotal);
            deviceDayData.stream()
                    .map(ProjectStaDeviceZnbDayEntity::getZnbEpimportTotal)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal::add)
                    .ifPresent(device::setZnbEpimportTotal);
            deviceDayData.stream()
                    .map(ProjectStaDeviceZnbDayEntity::getZnbEqexportTotal)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal::add)
                    .ifPresent(device::setZnbEqexportTotal);
            deviceDayData.stream()
                    .map(ProjectStaDeviceZnbDayEntity::getZnbEqimportTotal)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal::add)
                    .ifPresent(device::setZnbEqimportTotal);
            deviceDayData.stream()
                    .map(ProjectStaDeviceZnbDayEntity::getZnbPMax)
                    .filter(Objects::nonNull)
                    .max(BigDecimal::compareTo)
                    .ifPresent(device::setZnbPMax);
        }
        projectStaDeviceZnbMonthMapper.delete(new LambdaUpdateWrapper<ProjectStaDeviceZnbMonthEntity>()
                .eq(ProjectStaDeviceZnbMonthEntity::getYear, context.getYear())
                .eq(ProjectStaDeviceZnbMonthEntity::getMonth, context.getMonth())
                .in(ProjectStaDeviceZnbMonthEntity::getBizDeviceId, bizDeviceIds));
        projectStaDeviceZnbMonthMapper.insertBatchSomeColumn(deviceList);
    }

    private void staAir(StaDeviceMonthTaskContext context, List<DeviceStaDTO> deviceStaDTOList) {
        List<ProjectStaDeviceAirMonthEntity> deviceList = new ArrayList<>();
        List<String> bizDeviceIds = deviceStaDTOList.stream().map(DeviceStaDTO::getBizDeviceId).collect(Collectors.toList());
        // 获取存在手工报表的设备
        List<String> manualDeviceIdList = projectStaDeviceAirMonthMapper.selectList(
                new LambdaQueryWrapper<ProjectStaDeviceAirMonthEntity>()
                        .select(ProjectStaDeviceAirMonthEntity::getBizDeviceId)
                        .eq(ProjectStaDeviceAirMonthEntity::getManualFlag, 1)
                        .eq(ProjectStaDeviceAirMonthEntity::getYear, context.getYear())
                        .eq(ProjectStaDeviceAirMonthEntity::getMonth, context.getMonth())
                        .in(ProjectStaDeviceAirMonthEntity::getBizDeviceId, bizDeviceIds)
        ).stream().map(ProjectStaDeviceAirMonthEntity::getBizDeviceId).toList();

        bizDeviceIds.removeAll(manualDeviceIdList);
        deviceStaDTOList.removeIf(o -> manualDeviceIdList.contains(o.getBizDeviceId()));
        if (bizDeviceIds.isEmpty()) {
            return;
        }

        Map<String, List<ProjectStaDeviceAirDayEntity>> dayDataList = projectStaDeviceAirDayMapper.selectList(new LambdaQueryWrapper<ProjectStaDeviceAirDayEntity>()
                        .ge(ProjectStaDeviceAirDayEntity::getStaTime, context.getDayStartStaTime())
                        .le(ProjectStaDeviceAirDayEntity::getStaTime, context.getDayEndStaTime())
                        .in(ProjectStaDeviceAirDayEntity::getBizDeviceId, bizDeviceIds))
                .stream()
                .collect(Collectors.groupingBy(ProjectStaDeviceAirDayEntity::getBizDeviceId));

        for (DeviceStaDTO deviceStaDTO : deviceStaDTOList) {
            // 产品要求 没有数据给null
            ProjectStaDeviceAirMonthEntity device = new ProjectStaDeviceAirMonthEntity();
            deviceList.add(device);

            BeanUtil.copyProperties(deviceStaDTO, device);
            device.setTenantId(context.getTenantId()).setTenantCode(context.getTenantCode())
                    .setYear(context.getYear()).setMonth(context.getMonth()).setStaTime(context.getTimestamp());

            List<ProjectStaDeviceAirDayEntity> deviceDayData = dayDataList.get(deviceStaDTO.getBizDeviceId());
            if (CollectionUtil.isEmpty(deviceDayData)) {
                continue;
            }
            deviceDayData.stream()
                    .map(ProjectStaDeviceAirDayEntity::getAirconditionercontrollerOnlinetimeTotal)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal::add)
                    .ifPresent(device::setAirconditionercontrollerOnlinetimeTotal);
            deviceDayData.stream()
                    .map(ProjectStaDeviceAirDayEntity::getAirconditionercontrollerRunningtimeTotal)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal::add)
                    .ifPresent(device::setAirconditionercontrollerRunningtimeTotal);
            deviceDayData.stream()
                    .map(ProjectStaDeviceAirDayEntity::getAirconditionercontrollerActualtempAvg)
                    .filter(Objects::nonNull)
                    .mapToDouble(BigDecimal::doubleValue)
                    .average()
                    .ifPresent(avg -> device.setAirconditionercontrollerActualtempAvg(BigDecimal.valueOf(avg)));
        }
        projectStaDeviceAirMonthMapper.delete(new LambdaUpdateWrapper<ProjectStaDeviceAirMonthEntity>()
                .eq(ProjectStaDeviceAirMonthEntity::getYear, context.getYear())
                .eq(ProjectStaDeviceAirMonthEntity::getMonth, context.getMonth())
                .in(ProjectStaDeviceAirMonthEntity::getBizDeviceId,
                        deviceList.stream()
                                .map(ProjectStaDeviceAirMonthEntity::getBizDeviceId)
                                .collect(Collectors.toList()))
        );
        projectStaDeviceAirMonthMapper.insertBatchSomeColumn(deviceList);
    }

    private void staElectricity(StaDeviceMonthTaskContext context, List<DeviceStaDTO> deviceStaDTOList) {
        List<ProjectStaDeviceElectricityMonthEntity> deviceList = new ArrayList<>();
        List<String> bizDeviceIds = deviceStaDTOList.stream().map(DeviceStaDTO::getBizDeviceId).collect(Collectors.toList());
        // 获取存在手工报表的设备
        List<String> manualDeviceIdList = projectStaDeviceElectricityMonthMapper.selectList(
                new LambdaQueryWrapper<ProjectStaDeviceElectricityMonthEntity>()
                        .select(ProjectStaDeviceElectricityMonthEntity::getBizDeviceId)
                        .eq(ProjectStaDeviceElectricityMonthEntity::getManualFlag, 1)
                        .eq(ProjectStaDeviceElectricityMonthEntity::getYear, context.getYear())
                        .eq(ProjectStaDeviceElectricityMonthEntity::getMonth, context.getMonth())
                        .in(ProjectStaDeviceElectricityMonthEntity::getBizDeviceId, bizDeviceIds)
        ).stream().map(ProjectStaDeviceElectricityMonthEntity::getBizDeviceId).toList();

        bizDeviceIds.removeAll(manualDeviceIdList);
        deviceStaDTOList.removeIf(o -> manualDeviceIdList.contains(o.getBizDeviceId()));
        if (bizDeviceIds.isEmpty()) {
            return;
        }

        Map<String, List<ProjectStaDeviceElectricityDayEntity>> dayDataList = projectStaDeviceElectricityDayMapper.selectList(new LambdaQueryWrapper<ProjectStaDeviceElectricityDayEntity>()
                        .ge(ProjectStaDeviceElectricityDayEntity::getStaTime, context.getDayStartStaTime())
                        .le(ProjectStaDeviceElectricityDayEntity::getStaTime, context.getDayEndStaTime())
                        .in(ProjectStaDeviceElectricityDayEntity::getBizDeviceId, bizDeviceIds))
                .stream()
                .collect(Collectors.groupingBy(ProjectStaDeviceElectricityDayEntity::getBizDeviceId));

        for (DeviceStaDTO deviceStaDTO : deviceStaDTOList) {
            // 产品要求 没有数据给null
            ProjectStaDeviceElectricityMonthEntity device = new ProjectStaDeviceElectricityMonthEntity();
            deviceList.add(device);

            BeanUtil.copyProperties(deviceStaDTO, device);
            device.setTenantId(context.getTenantId()).setTenantCode(context.getTenantCode())
                    .setYear(context.getYear()).setMonth(context.getMonth()).setStaTime(context.getTimestamp());

            List<ProjectStaDeviceElectricityDayEntity> deviceDayData = dayDataList.get(deviceStaDTO.getBizDeviceId());
            if (CollectionUtil.isEmpty(deviceDayData)) {
                continue;
            }
            deviceDayData.stream()
                    .map(ProjectStaDeviceElectricityDayEntity::getEnergymeterEpimportTotal)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal::add)
                    .ifPresent(device::setEnergymeterEpimportTotal);
            deviceDayData.stream()
                    .map(ProjectStaDeviceElectricityDayEntity::getEnergymeterEpexportTotal)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal::add)
                    .ifPresent(device::setEnergymeterEpexportTotal);
            deviceDayData.stream()
                    .min(Comparator.comparing(ProjectStaDeviceElectricityDayEntity::getStaTime))
                    .map(ProjectStaDeviceElectricityDayEntity::getEnergymeterEpimportStart)
                    .ifPresent(device::setEnergymeterEpimportStart);
            deviceDayData.stream()
                    .max(Comparator.comparing(ProjectStaDeviceElectricityDayEntity::getStaTime))
                    .map(ProjectStaDeviceElectricityDayEntity::getEnergymeterEpimportEnd)
                    .ifPresent(device::setEnergymeterEpimportEnd);

        }
        projectStaDeviceElectricityMonthMapper.delete(new LambdaUpdateWrapper<ProjectStaDeviceElectricityMonthEntity>()
                .eq(ProjectStaDeviceElectricityMonthEntity::getYear, context.getYear())
                .eq(ProjectStaDeviceElectricityMonthEntity::getMonth, context.getMonth())
                .in(ProjectStaDeviceElectricityMonthEntity::getBizDeviceId,
                        deviceList.stream()
                                .map(ProjectStaDeviceElectricityMonthEntity::getBizDeviceId)
                                .collect(Collectors.toList()))
        );
        projectStaDeviceElectricityMonthMapper.insertBatchSomeColumn(deviceList);
    }

    private void staGas(StaDeviceMonthTaskContext context, List<DeviceStaDTO> deviceStaDTOList) {
        List<ProjectStaDeviceGasMonthEntity> deviceList = new ArrayList<>();
        List<String> bizDeviceIds = deviceStaDTOList.stream().map(DeviceStaDTO::getBizDeviceId).collect(Collectors.toList());
        // 获取存在手工报表的设备
        List<String> manualDeviceIdList = projectStaDeviceGasMonthMapper.selectList(
                new LambdaQueryWrapper<ProjectStaDeviceGasMonthEntity>()
                        .select(ProjectStaDeviceGasMonthEntity::getBizDeviceId)
                        .eq(ProjectStaDeviceGasMonthEntity::getManualFlag, 1)
                        .eq(ProjectStaDeviceGasMonthEntity::getYear, context.getYear())
                        .eq(ProjectStaDeviceGasMonthEntity::getMonth, context.getMonth())
                        .in(ProjectStaDeviceGasMonthEntity::getBizDeviceId, bizDeviceIds)
        ).stream().map(ProjectStaDeviceGasMonthEntity::getBizDeviceId).toList();

        bizDeviceIds.removeAll(manualDeviceIdList);
        deviceStaDTOList.removeIf(o -> manualDeviceIdList.contains(o.getBizDeviceId()));
        if (bizDeviceIds.isEmpty()) {
            return;
        }

        Map<String, List<ProjectStaDeviceGasDayEntity>> dayDataList = projectStaDeviceGasDayMapper.selectList(new LambdaQueryWrapper<ProjectStaDeviceGasDayEntity>()
                        .ge(ProjectStaDeviceGasDayEntity::getStaTime, context.getDayStartStaTime())
                        .le(ProjectStaDeviceGasDayEntity::getStaTime, context.getDayEndStaTime())
                        .in(ProjectStaDeviceGasDayEntity::getBizDeviceId, bizDeviceIds))
                .stream()
                .collect(Collectors.groupingBy(ProjectStaDeviceGasDayEntity::getBizDeviceId));

        for (DeviceStaDTO deviceStaDTO : deviceStaDTOList) {
            // 产品要求 没有数据给null
            ProjectStaDeviceGasMonthEntity device = new ProjectStaDeviceGasMonthEntity();
            deviceList.add(device);

            BeanUtil.copyProperties(deviceStaDTO, device);
            device.setTenantId(context.getTenantId()).setTenantCode(context.getTenantCode())
                    .setYear(context.getYear()).setMonth(context.getMonth()).setStaTime(context.getTimestamp());

            List<ProjectStaDeviceGasDayEntity> deviceDayData = dayDataList.get(deviceStaDTO.getBizDeviceId());
            if (CollectionUtil.isEmpty(deviceDayData)) {
                continue;
            }
            deviceDayData.stream()
                    .map(ProjectStaDeviceGasDayEntity::getGasmeterUsageTotal)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal::add)
                    .ifPresent(device::setGasmeterUsageTotal);
            // 新增期初期末值逻辑
            deviceDayData.stream()
                    .filter(o -> null != o.getGasmeterUsageTotal())
                    .min(Comparator.comparing(ProjectStaDeviceGasDayEntity::getStaTime))
                    .ifPresent(o -> device.setGasmeterUsageTotalStart(o.getGasmeterUsageTotalStart()));
            deviceDayData.stream()
                    .filter(o -> null != o.getGasmeterUsageTotal())
                    .max(Comparator.comparing(ProjectStaDeviceGasDayEntity::getStaTime))
                    .ifPresent(o -> device.setGasmeterUsageTotalEnd(o.getGasmeterUsageTotalEnd()));

        }
        projectStaDeviceGasMonthMapper.delete(new LambdaUpdateWrapper<ProjectStaDeviceGasMonthEntity>()
                .eq(ProjectStaDeviceGasMonthEntity::getYear, context.getYear())
                .eq(ProjectStaDeviceGasMonthEntity::getMonth, context.getMonth())
                .in(ProjectStaDeviceGasMonthEntity::getBizDeviceId,
                        deviceList.stream()
                                .map(ProjectStaDeviceGasMonthEntity::getBizDeviceId)
                                .collect(Collectors.toList()))
        );
        projectStaDeviceGasMonthMapper.insertBatchSomeColumn(deviceList);
    }

    private void staWater(StaDeviceMonthTaskContext context, List<DeviceStaDTO> deviceStaDTOList) {
        List<ProjectStaDeviceWaterMonthEntity> deviceList = new ArrayList<>();
        List<String> bizDeviceIds = deviceStaDTOList.stream().map(DeviceStaDTO::getBizDeviceId).collect(Collectors.toList());
        // 获取存在手工报表的设备
        List<String> manualDeviceIdList = projectStaDeviceWaterMonthMapper.selectList(
                new LambdaQueryWrapper<ProjectStaDeviceWaterMonthEntity>()
                        .select(ProjectStaDeviceWaterMonthEntity::getBizDeviceId)
                        .eq(ProjectStaDeviceWaterMonthEntity::getManualFlag, 1)
                        .eq(ProjectStaDeviceWaterMonthEntity::getYear, context.getYear())
                        .eq(ProjectStaDeviceWaterMonthEntity::getMonth, context.getMonth())
                        .in(ProjectStaDeviceWaterMonthEntity::getBizDeviceId, bizDeviceIds)
        ).stream().map(ProjectStaDeviceWaterMonthEntity::getBizDeviceId).toList();

        bizDeviceIds.removeAll(manualDeviceIdList);
        deviceStaDTOList.removeIf(o -> manualDeviceIdList.contains(o.getBizDeviceId()));
        if (bizDeviceIds.isEmpty()) {
            return;
        }

        Map<String, List<ProjectStaDeviceWaterDayEntity>> dayDataList = projectStaDeviceWaterDayMapper.selectList(new LambdaQueryWrapper<ProjectStaDeviceWaterDayEntity>()
                        .ge(ProjectStaDeviceWaterDayEntity::getStaTime, context.getDayStartStaTime())
                        .le(ProjectStaDeviceWaterDayEntity::getStaTime, context.getDayEndStaTime())
                        .in(ProjectStaDeviceWaterDayEntity::getBizDeviceId, bizDeviceIds))
                .stream()
                .collect(Collectors.groupingBy(ProjectStaDeviceWaterDayEntity::getBizDeviceId));

        for (DeviceStaDTO deviceStaDTO : deviceStaDTOList) {
            // 产品要求 没有数据给null
            ProjectStaDeviceWaterMonthEntity device = new ProjectStaDeviceWaterMonthEntity();
            deviceList.add(device);

            BeanUtil.copyProperties(deviceStaDTO, device);
            device.setTenantId(context.getTenantId()).setTenantCode(context.getTenantCode())
                    .setYear(context.getYear()).setMonth(context.getMonth()).setStaTime(context.getTimestamp());

            List<ProjectStaDeviceWaterDayEntity> deviceDayData = dayDataList.get(deviceStaDTO.getBizDeviceId());
            if (CollectionUtil.isEmpty(deviceDayData)) {
                continue;
            }
            deviceDayData.stream()
                    .map(ProjectStaDeviceWaterDayEntity::getWatermeterUsageTotal)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal::add)
                    .ifPresent(device::setWatermeterUsageTotal);
            // 新增期初期末值逻辑
            deviceDayData.stream()
                    .filter(o -> null != o.getWatermeterUsageTotal())
                    .min(Comparator.comparing(ProjectStaDeviceWaterDayEntity::getStaTime))
                    .ifPresent(o -> device.setWatermeterUsageTotalStart(o.getWatermeterUsageTotalStart()));
            deviceDayData.stream()
                    .filter(o -> null != o.getWatermeterUsageTotal())
                    .max(Comparator.comparing(ProjectStaDeviceWaterDayEntity::getStaTime))
                    .ifPresent(o -> device.setWatermeterUsageTotalEnd(o.getWatermeterUsageTotalEnd()));
        }
        projectStaDeviceWaterMonthMapper.delete(new LambdaUpdateWrapper<ProjectStaDeviceWaterMonthEntity>()
                .eq(ProjectStaDeviceWaterMonthEntity::getYear, context.getYear())
                .eq(ProjectStaDeviceWaterMonthEntity::getMonth, context.getMonth())
                .in(ProjectStaDeviceWaterMonthEntity::getBizDeviceId,
                        deviceList.stream()
                                .map(ProjectStaDeviceWaterMonthEntity::getBizDeviceId)
                                .collect(Collectors.toList()))
        );
        projectStaDeviceWaterMonthMapper.insertBatchSomeColumn(deviceList);
    }
}
