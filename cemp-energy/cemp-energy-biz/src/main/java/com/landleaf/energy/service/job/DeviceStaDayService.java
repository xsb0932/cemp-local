package com.landleaf.energy.service.job;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.landleaf.comm.constance.DeviceStaCategoryEnum;
import com.landleaf.energy.dal.mapper.*;
import com.landleaf.energy.domain.dto.StaDeviceDayTaskContext;
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
public class DeviceStaDayService {
    private final DeviceStaApi deviceStaApi;
    private final ProjectStaDeviceElectricityHourMapper projectStaDeviceElectricityHourMapper;
    private final ProjectStaDeviceElectricityDayMapper projectStaDeviceElectricityDayMapper;
    private final ProjectStaDeviceGasHourMapper projectStaDeviceGasHourMapper;
    private final ProjectStaDeviceGasDayMapper projectStaDeviceGasDayMapper;
    private final ProjectStaDeviceWaterHourMapper projectStaDeviceWaterHourMapper;
    private final ProjectStaDeviceWaterDayMapper projectStaDeviceWaterDayMapper;
    private final ProjectStaDeviceAirHourMapper projectStaDeviceAirHourMapper;
    private final ProjectStaDeviceAirDayMapper projectStaDeviceAirDayMapper;
    private final ProjectStaDeviceZnbHourMapper projectStaDeviceZnbHourMapper;
    private final ProjectStaDeviceZnbDayMapper projectStaDeviceZnbDayMapper;
    private final ProjectStaDeviceGscnHourMapper projectStaDeviceGscnHourMapper;
    private final ProjectStaDeviceGscnDayMapper projectStaDeviceGscnDayMapper;

    public String execute(Long tenantId, String tenantCode, DeviceStaCategoryEnum categoryEnum, LocalDateTime staTime, String projectIds) {
        LocalDateTime yesterday = staTime.minusDays(1L);
        LocalDateTime startTime = LocalDateTime.of(yesterday.getYear(), yesterday.getMonthValue(), yesterday.getDayOfMonth(), 0, 0, 0);
        LocalDateTime endTime = LocalDateTime.of(staTime.getYear(), staTime.getMonthValue(), staTime.getDayOfMonth(), 0, 0, 0);
        StaDeviceDayTaskContext context = new StaDeviceDayTaskContext(tenantId, tenantCode, staTime, startTime, endTime);
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

    private void staGscn(StaDeviceDayTaskContext context, List<DeviceStaDTO> deviceStaDTOList) {
        List<ProjectStaDeviceGscnDayEntity> deviceList = new ArrayList<>();
        List<String> bizDeviceIds = deviceStaDTOList.stream().map(DeviceStaDTO::getBizDeviceId).collect(Collectors.toList());
        // 获取存在手工报表的设备
        List<String> manualDeviceIdList = projectStaDeviceGscnDayMapper.selectList(
                new LambdaQueryWrapper<ProjectStaDeviceGscnDayEntity>()
                        .select(ProjectStaDeviceGscnDayEntity::getBizDeviceId)
                        .eq(ProjectStaDeviceGscnDayEntity::getManualFlag, 1)
                        .eq(ProjectStaDeviceGscnDayEntity::getYear, context.getYear())
                        .eq(ProjectStaDeviceGscnDayEntity::getMonth, context.getMonth())
                        .eq(ProjectStaDeviceGscnDayEntity::getDay, context.getDay())
                        .in(ProjectStaDeviceGscnDayEntity::getBizDeviceId, bizDeviceIds)
        ).stream().map(ProjectStaDeviceGscnDayEntity::getBizDeviceId).toList();

        bizDeviceIds.removeAll(manualDeviceIdList);
        deviceStaDTOList.removeIf(o -> manualDeviceIdList.contains(o.getBizDeviceId()));
        if (bizDeviceIds.isEmpty()) {
            return;
        }

        Map<String, List<ProjectStaDeviceGscnHourEntity>> hourDataList = projectStaDeviceGscnHourMapper.selectList(new LambdaQueryWrapper<ProjectStaDeviceGscnHourEntity>()
                        .eq(ProjectStaDeviceGscnHourEntity::getYear, context.getYear())
                        .eq(ProjectStaDeviceGscnHourEntity::getMonth, context.getMonth())
                        .eq(ProjectStaDeviceGscnHourEntity::getDay, context.getDay())
                        .in(ProjectStaDeviceGscnHourEntity::getBizDeviceId, bizDeviceIds))
                .stream()
                .collect(Collectors.groupingBy(ProjectStaDeviceGscnHourEntity::getBizDeviceId));

        for (DeviceStaDTO deviceStaDTO : deviceStaDTOList) {
            // 产品要求 没有数据给null
            ProjectStaDeviceGscnDayEntity device = new ProjectStaDeviceGscnDayEntity();
            deviceList.add(device);

            BeanUtil.copyProperties(deviceStaDTO, device);
            device.setTenantId(context.getTenantId()).setTenantCode(context.getTenantCode())
                    .setYear(context.getYear()).setMonth(context.getMonth()).setDay(context.getDay()).setStaTime(context.getTimestamp());

            List<ProjectStaDeviceGscnHourEntity> deviceHourData = hourDataList.get(deviceStaDTO.getBizDeviceId());
            if (CollectionUtil.isEmpty(deviceHourData)) {
                continue;
            }
            deviceHourData.stream()
                    .map(ProjectStaDeviceGscnHourEntity::getGscnOnlineTimeTotal)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal::add)
                    .ifPresent(device::setGscnOnlineTimeTotal);
            deviceHourData.stream()
                    .map(ProjectStaDeviceGscnHourEntity::getGscnChargeTimeTotal)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal::add)
                    .ifPresent(device::setGscnChargeTimeTotal);
            deviceHourData.stream()
                    .map(ProjectStaDeviceGscnHourEntity::getGscnDischargeTimeTotal)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal::add)
                    .ifPresent(device::setGscnDischargeTimeTotal);
            deviceHourData.stream()
                    .map(ProjectStaDeviceGscnHourEntity::getGscnStandbyTimeTotal)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal::add)
                    .ifPresent(device::setGscnStandbyTimeTotal);
            deviceHourData.stream()
                    .map(ProjectStaDeviceGscnHourEntity::getGscnEpimportTotal)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal::add)
                    .ifPresent(device::setGscnEpimportTotal);
            deviceHourData.stream()
                    .map(ProjectStaDeviceGscnHourEntity::getGscnEpexportTotal)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal::add)
                    .ifPresent(device::setGscnEpexportTotal);
        }
        projectStaDeviceGscnDayMapper.delete(new LambdaUpdateWrapper<ProjectStaDeviceGscnDayEntity>()
                .eq(ProjectStaDeviceGscnDayEntity::getYear, context.getYear())
                .eq(ProjectStaDeviceGscnDayEntity::getMonth, context.getMonth())
                .eq(ProjectStaDeviceGscnDayEntity::getDay, context.getDay())
                .in(ProjectStaDeviceGscnDayEntity::getBizDeviceId, bizDeviceIds));
        projectStaDeviceGscnDayMapper.insertBatchSomeColumn(deviceList);
    }

    private void staZnb(StaDeviceDayTaskContext context, List<DeviceStaDTO> deviceStaDTOList) {
        List<ProjectStaDeviceZnbDayEntity> deviceList = new ArrayList<>();
        List<String> bizDeviceIds = deviceStaDTOList.stream().map(DeviceStaDTO::getBizDeviceId).collect(Collectors.toList());
        // 获取存在手工报表的设备
        List<String> manualDeviceIdList = projectStaDeviceZnbDayMapper.selectList(
                new LambdaQueryWrapper<ProjectStaDeviceZnbDayEntity>()
                        .select(ProjectStaDeviceZnbDayEntity::getBizDeviceId)
                        .eq(ProjectStaDeviceZnbDayEntity::getManualFlag, 1)
                        .eq(ProjectStaDeviceZnbDayEntity::getYear, context.getYear())
                        .eq(ProjectStaDeviceZnbDayEntity::getMonth, context.getMonth())
                        .eq(ProjectStaDeviceZnbDayEntity::getDay, context.getDay())
                        .in(ProjectStaDeviceZnbDayEntity::getBizDeviceId, bizDeviceIds)
        ).stream().map(ProjectStaDeviceZnbDayEntity::getBizDeviceId).toList();

        bizDeviceIds.removeAll(manualDeviceIdList);
        deviceStaDTOList.removeIf(o -> manualDeviceIdList.contains(o.getBizDeviceId()));
        if (bizDeviceIds.isEmpty()) {
            return;
        }

        Map<String, List<ProjectStaDeviceZnbHourEntity>> hourDataList = projectStaDeviceZnbHourMapper.selectList(new LambdaQueryWrapper<ProjectStaDeviceZnbHourEntity>()
                        .eq(ProjectStaDeviceZnbHourEntity::getYear, context.getYear())
                        .eq(ProjectStaDeviceZnbHourEntity::getMonth, context.getMonth())
                        .eq(ProjectStaDeviceZnbHourEntity::getDay, context.getDay())
                        .in(ProjectStaDeviceZnbHourEntity::getBizDeviceId, bizDeviceIds))
                .stream()
                .collect(Collectors.groupingBy(ProjectStaDeviceZnbHourEntity::getBizDeviceId));

        for (DeviceStaDTO deviceStaDTO : deviceStaDTOList) {
            // 产品要求 没有数据给null
            ProjectStaDeviceZnbDayEntity device = new ProjectStaDeviceZnbDayEntity();
            deviceList.add(device);

            BeanUtil.copyProperties(deviceStaDTO, device);
            device.setTenantId(context.getTenantId()).setTenantCode(context.getTenantCode())
                    .setYear(context.getYear()).setMonth(context.getMonth()).setDay(context.getDay()).setStaTime(context.getTimestamp());

            List<ProjectStaDeviceZnbHourEntity> deviceHourData = hourDataList.get(deviceStaDTO.getBizDeviceId());
            if (CollectionUtil.isEmpty(deviceHourData)) {
                continue;
            }
            deviceHourData.stream()
                    .map(ProjectStaDeviceZnbHourEntity::getZnbOnlineTimeTotal)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal::add)
                    .ifPresent(device::setZnbOnlineTimeTotal);
            deviceHourData.stream()
                    .map(ProjectStaDeviceZnbHourEntity::getZnbRunningTimeTotal)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal::add)
                    .ifPresent(device::setZnbRunningTimeTotal);
            deviceHourData.stream()
                    .map(ProjectStaDeviceZnbHourEntity::getZnbEpexportTotal)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal::add)
                    .ifPresent(device::setZnbEpexportTotal);
            deviceHourData.stream()
                    .map(ProjectStaDeviceZnbHourEntity::getZnbEptoHourTotal)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal::add)
                    .ifPresent(device::setZnbEptoHourTotal);
            deviceHourData.stream()
                    .map(ProjectStaDeviceZnbHourEntity::getZnbEpimportTotal)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal::add)
                    .ifPresent(device::setZnbEpimportTotal);
            deviceHourData.stream()
                    .map(ProjectStaDeviceZnbHourEntity::getZnbEqexportTotal)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal::add)
                    .ifPresent(device::setZnbEqexportTotal);
            deviceHourData.stream()
                    .map(ProjectStaDeviceZnbHourEntity::getZnbEqimportTotal)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal::add)
                    .ifPresent(device::setZnbEqimportTotal);
            deviceHourData.stream()
                    .map(ProjectStaDeviceZnbHourEntity::getZnbPMax)
                    .filter(Objects::nonNull)
                    .max(BigDecimal::compareTo)
                    .ifPresent(device::setZnbPMax);
        }
        projectStaDeviceZnbDayMapper.delete(new LambdaUpdateWrapper<ProjectStaDeviceZnbDayEntity>()
                .eq(ProjectStaDeviceZnbDayEntity::getYear, context.getYear())
                .eq(ProjectStaDeviceZnbDayEntity::getMonth, context.getMonth())
                .eq(ProjectStaDeviceZnbDayEntity::getDay, context.getDay())
                .in(ProjectStaDeviceZnbDayEntity::getBizDeviceId, bizDeviceIds));
        projectStaDeviceZnbDayMapper.insertBatchSomeColumn(deviceList);
    }

    private void staAir(StaDeviceDayTaskContext context, List<DeviceStaDTO> deviceStaDTOList) {
        List<ProjectStaDeviceAirDayEntity> deviceList = new ArrayList<>();
        List<String> bizDeviceIds = deviceStaDTOList.stream().map(DeviceStaDTO::getBizDeviceId).collect(Collectors.toList());
        // 获取存在手工报表的设备
        List<String> manualDeviceIdList = projectStaDeviceAirDayMapper.selectList(
                new LambdaQueryWrapper<ProjectStaDeviceAirDayEntity>()
                        .select(ProjectStaDeviceAirDayEntity::getBizDeviceId)
                        .eq(ProjectStaDeviceAirDayEntity::getManualFlag, 1)
                        .eq(ProjectStaDeviceAirDayEntity::getYear, context.getYear())
                        .eq(ProjectStaDeviceAirDayEntity::getMonth, context.getMonth())
                        .eq(ProjectStaDeviceAirDayEntity::getDay, context.getDay())
                        .in(ProjectStaDeviceAirDayEntity::getBizDeviceId, bizDeviceIds)
        ).stream().map(ProjectStaDeviceAirDayEntity::getBizDeviceId).toList();

        bizDeviceIds.removeAll(manualDeviceIdList);
        deviceStaDTOList.removeIf(o -> manualDeviceIdList.contains(o.getBizDeviceId()));
        if (bizDeviceIds.isEmpty()) {
            return;
        }

        Map<String, List<ProjectStaDeviceAirHourEntity>> hourDataList = projectStaDeviceAirHourMapper.selectList(new LambdaQueryWrapper<ProjectStaDeviceAirHourEntity>()
                        .eq(ProjectStaDeviceAirHourEntity::getYear, context.getYear())
                        .eq(ProjectStaDeviceAirHourEntity::getMonth, context.getMonth())
                        .eq(ProjectStaDeviceAirHourEntity::getDay, context.getDay())
                        .in(ProjectStaDeviceAirHourEntity::getBizDeviceId, bizDeviceIds))
                .stream()
                .collect(Collectors.groupingBy(ProjectStaDeviceAirHourEntity::getBizDeviceId));

        for (DeviceStaDTO deviceStaDTO : deviceStaDTOList) {
            // 产品要求 没有数据给null
            ProjectStaDeviceAirDayEntity device = new ProjectStaDeviceAirDayEntity();
            deviceList.add(device);

            BeanUtil.copyProperties(deviceStaDTO, device);
            device.setTenantId(context.getTenantId()).setTenantCode(context.getTenantCode())
                    .setYear(context.getYear()).setMonth(context.getMonth()).setDay(context.getDay()).setStaTime(context.getTimestamp());

            List<ProjectStaDeviceAirHourEntity> deviceHourData = hourDataList.get(deviceStaDTO.getBizDeviceId());
            if (CollectionUtil.isEmpty(deviceHourData)) {
                continue;
            }
            deviceHourData.stream()
                    .map(ProjectStaDeviceAirHourEntity::getAirconditionercontrollerOnlinetimeTotal)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal::add)
                    .ifPresent(device::setAirconditionercontrollerOnlinetimeTotal);
            deviceHourData.stream()
                    .map(ProjectStaDeviceAirHourEntity::getAirconditionercontrollerRunningtimeTotal)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal::add)
                    .ifPresent(device::setAirconditionercontrollerRunningtimeTotal);
            deviceHourData.stream()
                    .map(ProjectStaDeviceAirHourEntity::getAirconditionercontrollerActualtempAvg)
                    .filter(Objects::nonNull)
                    .mapToDouble(BigDecimal::doubleValue)
                    .average()
                    .ifPresent(avg -> device.setAirconditionercontrollerActualtempAvg(BigDecimal.valueOf(avg)));
        }
        projectStaDeviceAirDayMapper.delete(new LambdaUpdateWrapper<ProjectStaDeviceAirDayEntity>()
                .eq(ProjectStaDeviceAirDayEntity::getYear, context.getYear())
                .eq(ProjectStaDeviceAirDayEntity::getMonth, context.getMonth())
                .eq(ProjectStaDeviceAirDayEntity::getDay, context.getDay())
                .in(ProjectStaDeviceAirDayEntity::getBizDeviceId, bizDeviceIds));
        projectStaDeviceAirDayMapper.insertBatchSomeColumn(deviceList);
    }

    private void staElectricity(StaDeviceDayTaskContext context, List<DeviceStaDTO> deviceStaDTOList) {
        List<ProjectStaDeviceElectricityDayEntity> deviceList = new ArrayList<>();
        List<String> bizDeviceIds = deviceStaDTOList.stream().map(DeviceStaDTO::getBizDeviceId).collect(Collectors.toList());
        // 获取存在手工报表的设备
        List<String> manualDeviceIdList = projectStaDeviceElectricityDayMapper.selectList(
                new LambdaQueryWrapper<ProjectStaDeviceElectricityDayEntity>()
                        .select(ProjectStaDeviceElectricityDayEntity::getBizDeviceId)
                        .eq(ProjectStaDeviceElectricityDayEntity::getManualFlag, 1)
                        .eq(ProjectStaDeviceElectricityDayEntity::getYear, context.getYear())
                        .eq(ProjectStaDeviceElectricityDayEntity::getMonth, context.getMonth())
                        .eq(ProjectStaDeviceElectricityDayEntity::getDay, context.getDay())
                        .in(ProjectStaDeviceElectricityDayEntity::getBizDeviceId, bizDeviceIds)
        ).stream().map(ProjectStaDeviceElectricityDayEntity::getBizDeviceId).toList();

        bizDeviceIds.removeAll(manualDeviceIdList);
        deviceStaDTOList.removeIf(o -> manualDeviceIdList.contains(o.getBizDeviceId()));
        if (bizDeviceIds.isEmpty()) {
            return;
        }

        Map<String, List<ProjectStaDeviceElectricityHourEntity>> hourDataList = projectStaDeviceElectricityHourMapper.selectList(new LambdaQueryWrapper<ProjectStaDeviceElectricityHourEntity>()
                        .eq(ProjectStaDeviceElectricityHourEntity::getYear, context.getYear())
                        .eq(ProjectStaDeviceElectricityHourEntity::getMonth, context.getMonth())
                        .eq(ProjectStaDeviceElectricityHourEntity::getDay, context.getDay())
                        .in(ProjectStaDeviceElectricityHourEntity::getBizDeviceId, bizDeviceIds))
                .stream()
                .collect(Collectors.groupingBy(ProjectStaDeviceElectricityHourEntity::getBizDeviceId));

        for (DeviceStaDTO deviceStaDTO : deviceStaDTOList) {
            // 产品要求 没有数据给null
            ProjectStaDeviceElectricityDayEntity device = new ProjectStaDeviceElectricityDayEntity();
            deviceList.add(device);

            BeanUtil.copyProperties(deviceStaDTO, device);
            device.setTenantId(context.getTenantId()).setTenantCode(context.getTenantCode())
                    .setYear(context.getYear()).setMonth(context.getMonth()).setDay(context.getDay()).setStaTime(context.getTimestamp());

            List<ProjectStaDeviceElectricityHourEntity> deviceHourData = hourDataList.get(deviceStaDTO.getBizDeviceId());
            if (CollectionUtil.isEmpty(deviceHourData)) {
                continue;
            }
            deviceHourData.stream()
                    .map(ProjectStaDeviceElectricityHourEntity::getEnergymeterEpimportTotal)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal::add)
                    .ifPresent(device::setEnergymeterEpimportTotal);
            deviceHourData.stream()
                    .map(ProjectStaDeviceElectricityHourEntity::getEnergymeterEpexportTotal)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal::add)
                    .ifPresent(device::setEnergymeterEpexportTotal);
            deviceHourData.stream()
                    .min(Comparator.comparing(ProjectStaDeviceElectricityHourEntity::getStaTime))
                    .map(ProjectStaDeviceElectricityHourEntity::getEnergymeterEpimportStart)
                    .ifPresent(device::setEnergymeterEpimportStart);
            deviceHourData.stream()
                    .max(Comparator.comparing(ProjectStaDeviceElectricityHourEntity::getStaTime))
                    .map(ProjectStaDeviceElectricityHourEntity::getEnergymeterEpimportEnd)
                    .ifPresent(device::setEnergymeterEpimportEnd);
        }
        projectStaDeviceElectricityDayMapper.delete(new LambdaUpdateWrapper<ProjectStaDeviceElectricityDayEntity>()
                .eq(ProjectStaDeviceElectricityDayEntity::getYear, context.getYear())
                .eq(ProjectStaDeviceElectricityDayEntity::getMonth, context.getMonth())
                .eq(ProjectStaDeviceElectricityDayEntity::getDay, context.getDay())
                .in(ProjectStaDeviceElectricityDayEntity::getBizDeviceId, bizDeviceIds));
        projectStaDeviceElectricityDayMapper.insertBatchSomeColumn(deviceList);
    }

    private void staGas(StaDeviceDayTaskContext context, List<DeviceStaDTO> deviceStaDTOList) {
        List<ProjectStaDeviceGasDayEntity> deviceList = new ArrayList<>();
        List<String> bizDeviceIds = deviceStaDTOList.stream().map(DeviceStaDTO::getBizDeviceId).collect(Collectors.toList());
        // 获取存在手工报表的设备
        List<String> manualDeviceIdList = projectStaDeviceGasDayMapper.selectList(
                new LambdaQueryWrapper<ProjectStaDeviceGasDayEntity>()
                        .select(ProjectStaDeviceGasDayEntity::getBizDeviceId)
                        .eq(ProjectStaDeviceGasDayEntity::getManualFlag, 1)
                        .eq(ProjectStaDeviceGasDayEntity::getYear, context.getYear())
                        .eq(ProjectStaDeviceGasDayEntity::getMonth, context.getMonth())
                        .eq(ProjectStaDeviceGasDayEntity::getDay, context.getDay())
                        .in(ProjectStaDeviceGasDayEntity::getBizDeviceId, bizDeviceIds)
        ).stream().map(ProjectStaDeviceGasDayEntity::getBizDeviceId).toList();

        bizDeviceIds.removeAll(manualDeviceIdList);
        deviceStaDTOList.removeIf(o -> manualDeviceIdList.contains(o.getBizDeviceId()));
        if (bizDeviceIds.isEmpty()) {
            return;
        }

        Map<String, List<ProjectStaDeviceGasHourEntity>> hourDataList = projectStaDeviceGasHourMapper.selectList(new LambdaQueryWrapper<ProjectStaDeviceGasHourEntity>()
                        .eq(ProjectStaDeviceGasHourEntity::getYear, context.getYear())
                        .eq(ProjectStaDeviceGasHourEntity::getMonth, context.getMonth())
                        .eq(ProjectStaDeviceGasHourEntity::getDay, context.getDay())
                        .in(ProjectStaDeviceGasHourEntity::getBizDeviceId, bizDeviceIds))
                .stream()
                .collect(Collectors.groupingBy(ProjectStaDeviceGasHourEntity::getBizDeviceId));

        for (DeviceStaDTO deviceStaDTO : deviceStaDTOList) {
            // 产品要求 没有数据给null
            ProjectStaDeviceGasDayEntity device = new ProjectStaDeviceGasDayEntity();
            deviceList.add(device);

            BeanUtil.copyProperties(deviceStaDTO, device);
            device.setTenantId(context.getTenantId()).setTenantCode(context.getTenantCode())
                    .setYear(context.getYear()).setMonth(context.getMonth()).setDay(context.getDay()).setStaTime(context.getTimestamp());

            List<ProjectStaDeviceGasHourEntity> deviceHourData = hourDataList.get(deviceStaDTO.getBizDeviceId());
            if (CollectionUtil.isEmpty(deviceHourData)) {
                continue;
            }
            deviceHourData.stream()
                    .map(ProjectStaDeviceGasHourEntity::getGasmeterUsageTotal)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal::add)
                    .ifPresent(device::setGasmeterUsageTotal);
            // 新增期初期末值逻辑
            deviceHourData.stream()
                    .filter(o -> null != o.getGasmeterUsageTotal())
                    .min(Comparator.comparing(ProjectStaDeviceGasHourEntity::getStaTime))
                    .ifPresent(o -> device.setGasmeterUsageTotalStart(o.getGasmeterUsageTotalStart()));
            deviceHourData.stream()
                    .filter(o -> null != o.getGasmeterUsageTotal())
                    .max(Comparator.comparing(ProjectStaDeviceGasHourEntity::getStaTime))
                    .ifPresent(o -> device.setGasmeterUsageTotalEnd(o.getGasmeterUsageTotalEnd()));

        }
        projectStaDeviceGasDayMapper.delete(new LambdaUpdateWrapper<ProjectStaDeviceGasDayEntity>()
                .eq(ProjectStaDeviceGasDayEntity::getYear, context.getYear())
                .eq(ProjectStaDeviceGasDayEntity::getMonth, context.getMonth())
                .eq(ProjectStaDeviceGasDayEntity::getDay, context.getDay())
                .in(ProjectStaDeviceGasDayEntity::getBizDeviceId, bizDeviceIds));
        projectStaDeviceGasDayMapper.insertBatchSomeColumn(deviceList);
    }

    private void staWater(StaDeviceDayTaskContext context, List<DeviceStaDTO> deviceStaDTOList) {
        List<ProjectStaDeviceWaterDayEntity> deviceList = new ArrayList<>();
        List<String> bizDeviceIds = deviceStaDTOList.stream().map(DeviceStaDTO::getBizDeviceId).collect(Collectors.toList());
        // 获取存在手工报表的设备
        List<String> manualDeviceIdList = projectStaDeviceWaterDayMapper.selectList(
                new LambdaQueryWrapper<ProjectStaDeviceWaterDayEntity>()
                        .select(ProjectStaDeviceWaterDayEntity::getBizDeviceId)
                        .eq(ProjectStaDeviceWaterDayEntity::getManualFlag, 1)
                        .eq(ProjectStaDeviceWaterDayEntity::getYear, context.getYear())
                        .eq(ProjectStaDeviceWaterDayEntity::getMonth, context.getMonth())
                        .eq(ProjectStaDeviceWaterDayEntity::getDay, context.getDay())
                        .in(ProjectStaDeviceWaterDayEntity::getBizDeviceId, bizDeviceIds)
        ).stream().map(ProjectStaDeviceWaterDayEntity::getBizDeviceId).toList();

        bizDeviceIds.removeAll(manualDeviceIdList);
        deviceStaDTOList.removeIf(o -> manualDeviceIdList.contains(o.getBizDeviceId()));
        if (bizDeviceIds.isEmpty()) {
            return;
        }

        Map<String, List<ProjectStaDeviceWaterHourEntity>> hourDataList = projectStaDeviceWaterHourMapper.selectList(new LambdaQueryWrapper<ProjectStaDeviceWaterHourEntity>()
                        .eq(ProjectStaDeviceWaterHourEntity::getYear, context.getYear())
                        .eq(ProjectStaDeviceWaterHourEntity::getMonth, context.getMonth())
                        .eq(ProjectStaDeviceWaterHourEntity::getDay, context.getDay())
                        .in(ProjectStaDeviceWaterHourEntity::getBizDeviceId, bizDeviceIds))
                .stream()
                .collect(Collectors.groupingBy(ProjectStaDeviceWaterHourEntity::getBizDeviceId));

        for (DeviceStaDTO deviceStaDTO : deviceStaDTOList) {
            // 产品要求 没有数据给null
            ProjectStaDeviceWaterDayEntity device = new ProjectStaDeviceWaterDayEntity();
            deviceList.add(device);

            BeanUtil.copyProperties(deviceStaDTO, device);
            device.setTenantId(context.getTenantId()).setTenantCode(context.getTenantCode())
                    .setYear(context.getYear()).setMonth(context.getMonth()).setDay(context.getDay()).setStaTime(context.getTimestamp());

            List<ProjectStaDeviceWaterHourEntity> deviceHourData = hourDataList.get(deviceStaDTO.getBizDeviceId());
            if (CollectionUtil.isEmpty(deviceHourData)) {
                continue;
            }
            deviceHourData.stream()
                    .map(ProjectStaDeviceWaterHourEntity::getWatermeterUsageTotal)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal::add)
                    .ifPresent(device::setWatermeterUsageTotal);
            // 新增期初期末值逻辑
            deviceHourData.stream()
                    .filter(o -> null != o.getWatermeterUsageTotal())
                    .min(Comparator.comparing(ProjectStaDeviceWaterHourEntity::getStaTime))
                    .ifPresent(o -> device.setWatermeterUsageTotalStart(o.getWatermeterUsageTotalStart()));
            deviceHourData.stream()
                    .filter(o -> null != o.getWatermeterUsageTotal())
                    .max(Comparator.comparing(ProjectStaDeviceWaterHourEntity::getStaTime))
                    .ifPresent(o -> device.setWatermeterUsageTotalEnd(o.getWatermeterUsageTotalEnd()));
        }
        projectStaDeviceWaterDayMapper.delete(new LambdaUpdateWrapper<ProjectStaDeviceWaterDayEntity>()
                .eq(ProjectStaDeviceWaterDayEntity::getYear, context.getYear())
                .eq(ProjectStaDeviceWaterDayEntity::getMonth, context.getMonth())
                .eq(ProjectStaDeviceWaterDayEntity::getDay, context.getDay())
                .in(ProjectStaDeviceWaterDayEntity::getBizDeviceId, bizDeviceIds));
        projectStaDeviceWaterDayMapper.insertBatchSomeColumn(deviceList);
    }
}
