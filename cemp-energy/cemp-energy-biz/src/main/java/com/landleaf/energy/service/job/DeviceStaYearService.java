package com.landleaf.energy.service.job;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.landleaf.comm.constance.DeviceStaCategoryEnum;
import com.landleaf.energy.dal.mapper.*;
import com.landleaf.energy.domain.dto.StaDeviceYearTaskContext;
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
public class DeviceStaYearService {
    private final DeviceStaApi deviceStaApi;
    private final ProjectStaDeviceElectricityMonthMapper projectStaDeviceElectricityMonthMapper;
    private final ProjectStaDeviceElectricityYearMapper projectStaDeviceElectricityYearMapper;
    private final ProjectStaDeviceGasMonthMapper projectStaDeviceGasMonthMapper;
    private final ProjectStaDeviceGasYearMapper projectStaDeviceGasYearMapper;
    private final ProjectStaDeviceWaterMonthMapper projectStaDeviceWaterMonthMapper;
    private final ProjectStaDeviceWaterYearMapper projectStaDeviceWaterYearMapper;
    private final ProjectStaDeviceAirMonthMapper projectStaDeviceAirMonthMapper;
    private final ProjectStaDeviceAirYearMapper projectStaDeviceAirYearMapper;
    private final ProjectStaDeviceZnbMonthMapper projectStaDeviceZnbMonthMapper;
    private final ProjectStaDeviceZnbYearMapper projectStaDeviceZnbYearMapper;
    private final ProjectStaDeviceGscnMonthMapper projectStaDeviceGscnMonthMapper;
    private final ProjectStaDeviceGscnYearMapper projectStaDeviceGscnYearMapper;

    public String execute(Long tenantId, String tenantCode, String reportingCycle, DeviceStaCategoryEnum categoryEnum, LocalDateTime staTime, String projectIds) {
        LocalDateTime startTime;
        LocalDateTime endTime = LocalDateTime.of(staTime.getYear(), 1, 1, 0, 0, 0);
        LocalDateTime lastYear = staTime.minusYears(1L);
        if (staTime.getDayOfMonth() > 7) {
            startTime = LocalDateTime.of(staTime.getYear(), 1, 1, 0, 0, 0);
        } else {
            startTime = LocalDateTime.of(lastYear.getYear(), 1, 1, 0, 0, 0);
        }
        StaDeviceYearTaskContext context = new StaDeviceYearTaskContext(tenantId, tenantCode, staTime, startTime, endTime);
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

    private void staGscn(StaDeviceYearTaskContext context, List<DeviceStaDTO> deviceStaDTOList) {
        List<ProjectStaDeviceGscnYearEntity> deviceList = new ArrayList<>();
        List<String> bizDeviceIds = deviceStaDTOList.stream().map(DeviceStaDTO::getBizDeviceId).collect(Collectors.toList());
        // 获取存在手工报表的设备
        List<String> manualDeviceIdList = projectStaDeviceGscnYearMapper.selectList(
                new LambdaQueryWrapper<ProjectStaDeviceGscnYearEntity>()
                        .select(ProjectStaDeviceGscnYearEntity::getBizDeviceId)
                        .eq(ProjectStaDeviceGscnYearEntity::getManualFlag, 1)
                        .eq(ProjectStaDeviceGscnYearEntity::getYear, context.getYear())
                        .in(ProjectStaDeviceGscnYearEntity::getBizDeviceId, bizDeviceIds)
        ).stream().map(ProjectStaDeviceGscnYearEntity::getBizDeviceId).toList();

        bizDeviceIds.removeAll(manualDeviceIdList);
        deviceStaDTOList.removeIf(o -> manualDeviceIdList.contains(o.getBizDeviceId()));
        if (bizDeviceIds.isEmpty()) {
            return;
        }

        Map<String, List<ProjectStaDeviceGscnMonthEntity>> monthDataList = projectStaDeviceGscnMonthMapper.selectList(new LambdaQueryWrapper<ProjectStaDeviceGscnMonthEntity>()
                        .eq(ProjectStaDeviceGscnMonthEntity::getYear, context.getYear())
                        .in(ProjectStaDeviceGscnMonthEntity::getBizDeviceId, bizDeviceIds))
                .stream()
                .collect(Collectors.groupingBy(ProjectStaDeviceGscnMonthEntity::getBizDeviceId));

        for (DeviceStaDTO deviceStaDTO : deviceStaDTOList) {
            // 产品要求 没有数据给null
            ProjectStaDeviceGscnYearEntity device = new ProjectStaDeviceGscnYearEntity();
            deviceList.add(device);

            BeanUtil.copyProperties(deviceStaDTO, device);
            device.setTenantId(context.getTenantId()).setTenantCode(context.getTenantCode())
                    .setYear(context.getYear()).setStaTime(context.getTimestamp());

            List<ProjectStaDeviceGscnMonthEntity> deviceMonthData = monthDataList.get(deviceStaDTO.getBizDeviceId());
            if (CollectionUtil.isEmpty(deviceMonthData)) {
                continue;
            }
            deviceMonthData.stream()
                    .map(ProjectStaDeviceGscnMonthEntity::getGscnOnlineTimeTotal)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal::add)
                    .ifPresent(device::setGscnOnlineTimeTotal);
            deviceMonthData.stream()
                    .map(ProjectStaDeviceGscnMonthEntity::getGscnChargeTimeTotal)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal::add)
                    .ifPresent(device::setGscnChargeTimeTotal);
            deviceMonthData.stream()
                    .map(ProjectStaDeviceGscnMonthEntity::getGscnDischargeTimeTotal)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal::add)
                    .ifPresent(device::setGscnDischargeTimeTotal);
            deviceMonthData.stream()
                    .map(ProjectStaDeviceGscnMonthEntity::getGscnStandbyTimeTotal)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal::add)
                    .ifPresent(device::setGscnStandbyTimeTotal);
            deviceMonthData.stream()
                    .map(ProjectStaDeviceGscnMonthEntity::getGscnEpimportTotal)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal::add)
                    .ifPresent(device::setGscnEpimportTotal);
            deviceMonthData.stream()
                    .map(ProjectStaDeviceGscnMonthEntity::getGscnEpexportTotal)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal::add)
                    .ifPresent(device::setGscnEpexportTotal);
        }
        projectStaDeviceGscnYearMapper.delete(new LambdaUpdateWrapper<ProjectStaDeviceGscnYearEntity>()
                .eq(ProjectStaDeviceGscnYearEntity::getYear, context.getYear())
                .in(ProjectStaDeviceGscnYearEntity::getBizDeviceId, bizDeviceIds));
        projectStaDeviceGscnYearMapper.insertBatchSomeColumn(deviceList);
    }

    private void staZnb(StaDeviceYearTaskContext context, List<DeviceStaDTO> deviceStaDTOList) {
        List<ProjectStaDeviceZnbYearEntity> deviceList = new ArrayList<>();
        List<String> bizDeviceIds = deviceStaDTOList.stream().map(DeviceStaDTO::getBizDeviceId).collect(Collectors.toList());
        // 获取存在手工报表的设备
        List<String> manualDeviceIdList = projectStaDeviceZnbYearMapper.selectList(
                new LambdaQueryWrapper<ProjectStaDeviceZnbYearEntity>()
                        .select(ProjectStaDeviceZnbYearEntity::getBizDeviceId)
                        .eq(ProjectStaDeviceZnbYearEntity::getManualFlag, 1)
                        .eq(ProjectStaDeviceZnbYearEntity::getYear, context.getYear())
                        .in(ProjectStaDeviceZnbYearEntity::getBizDeviceId, bizDeviceIds)
        ).stream().map(ProjectStaDeviceZnbYearEntity::getBizDeviceId).toList();

        bizDeviceIds.removeAll(manualDeviceIdList);
        deviceStaDTOList.removeIf(o -> manualDeviceIdList.contains(o.getBizDeviceId()));
        if (bizDeviceIds.isEmpty()) {
            return;
        }

        Map<String, List<ProjectStaDeviceZnbMonthEntity>> monthDataList = projectStaDeviceZnbMonthMapper.selectList(new LambdaQueryWrapper<ProjectStaDeviceZnbMonthEntity>()
                        .eq(ProjectStaDeviceZnbMonthEntity::getYear, context.getYear())
                        .in(ProjectStaDeviceZnbMonthEntity::getBizDeviceId, bizDeviceIds))
                .stream()
                .collect(Collectors.groupingBy(ProjectStaDeviceZnbMonthEntity::getBizDeviceId));

        for (DeviceStaDTO deviceStaDTO : deviceStaDTOList) {
            // 产品要求 没有数据给null
            ProjectStaDeviceZnbYearEntity device = new ProjectStaDeviceZnbYearEntity();
            deviceList.add(device);

            BeanUtil.copyProperties(deviceStaDTO, device);
            device.setTenantId(context.getTenantId()).setTenantCode(context.getTenantCode())
                    .setYear(context.getYear()).setStaTime(context.getTimestamp());

            List<ProjectStaDeviceZnbMonthEntity> deviceMonthData = monthDataList.get(deviceStaDTO.getBizDeviceId());
            if (CollectionUtil.isEmpty(deviceMonthData)) {
                continue;
            }
            deviceMonthData.stream()
                    .map(ProjectStaDeviceZnbMonthEntity::getZnbOnlineTimeTotal)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal::add)
                    .ifPresent(device::setZnbOnlineTimeTotal);
            deviceMonthData.stream()
                    .map(ProjectStaDeviceZnbMonthEntity::getZnbRunningTimeTotal)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal::add)
                    .ifPresent(device::setZnbRunningTimeTotal);
            deviceMonthData.stream()
                    .map(ProjectStaDeviceZnbMonthEntity::getZnbEpexportTotal)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal::add)
                    .ifPresent(device::setZnbEpexportTotal);
            deviceMonthData.stream()
                    .map(ProjectStaDeviceZnbMonthEntity::getZnbEptoHourTotal)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal::add)
                    .ifPresent(device::setZnbEptoHourTotal);
            deviceMonthData.stream()
                    .map(ProjectStaDeviceZnbMonthEntity::getZnbEpimportTotal)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal::add)
                    .ifPresent(device::setZnbEpimportTotal);
            deviceMonthData.stream()
                    .map(ProjectStaDeviceZnbMonthEntity::getZnbEqexportTotal)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal::add)
                    .ifPresent(device::setZnbEqexportTotal);
            deviceMonthData.stream()
                    .map(ProjectStaDeviceZnbMonthEntity::getZnbEqimportTotal)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal::add)
                    .ifPresent(device::setZnbEqimportTotal);
            deviceMonthData.stream()
                    .map(ProjectStaDeviceZnbMonthEntity::getZnbPMax)
                    .filter(Objects::nonNull)
                    .max(BigDecimal::compareTo)
                    .ifPresent(device::setZnbPMax);
        }
        projectStaDeviceZnbYearMapper.delete(new LambdaUpdateWrapper<ProjectStaDeviceZnbYearEntity>()
                .eq(ProjectStaDeviceZnbYearEntity::getYear, context.getYear())
                .in(ProjectStaDeviceZnbYearEntity::getBizDeviceId, bizDeviceIds));
        projectStaDeviceZnbYearMapper.insertBatchSomeColumn(deviceList);
    }

    private void staAir(StaDeviceYearTaskContext context, List<DeviceStaDTO> deviceStaDTOList) {
        List<ProjectStaDeviceAirYearEntity> deviceList = new ArrayList<>();
        List<String> bizDeviceIds = deviceStaDTOList.stream().map(DeviceStaDTO::getBizDeviceId).collect(Collectors.toList());
        // 获取存在手工报表的设备
        List<String> manualDeviceIdList = projectStaDeviceAirYearMapper.selectList(
                new LambdaQueryWrapper<ProjectStaDeviceAirYearEntity>()
                        .select(ProjectStaDeviceAirYearEntity::getBizDeviceId)
                        .eq(ProjectStaDeviceAirYearEntity::getManualFlag, 1)
                        .eq(ProjectStaDeviceAirYearEntity::getYear, context.getYear())
                        .in(ProjectStaDeviceAirYearEntity::getBizDeviceId, bizDeviceIds)
        ).stream().map(ProjectStaDeviceAirYearEntity::getBizDeviceId).toList();

        bizDeviceIds.removeAll(manualDeviceIdList);
        deviceStaDTOList.removeIf(o -> manualDeviceIdList.contains(o.getBizDeviceId()));
        if (bizDeviceIds.isEmpty()) {
            return;
        }

        Map<String, List<ProjectStaDeviceAirMonthEntity>> monthDataList = projectStaDeviceAirMonthMapper.selectList(new LambdaQueryWrapper<ProjectStaDeviceAirMonthEntity>()
                        .eq(ProjectStaDeviceAirMonthEntity::getYear, context.getYear())
                        .in(ProjectStaDeviceAirMonthEntity::getBizDeviceId, bizDeviceIds))
                .stream()
                .collect(Collectors.groupingBy(ProjectStaDeviceAirMonthEntity::getBizDeviceId));

        for (DeviceStaDTO deviceStaDTO : deviceStaDTOList) {
            // 产品要求 没有数据给null
            ProjectStaDeviceAirYearEntity device = new ProjectStaDeviceAirYearEntity();
            deviceList.add(device);

            BeanUtil.copyProperties(deviceStaDTO, device);
            device.setTenantId(context.getTenantId()).setTenantCode(context.getTenantCode())
                    .setYear(context.getYear()).setStaTime(context.getTimestamp());

            List<ProjectStaDeviceAirMonthEntity> deviceMonthData = monthDataList.get(deviceStaDTO.getBizDeviceId());
            if (CollectionUtil.isEmpty(deviceMonthData)) {
                continue;
            }
            deviceMonthData.stream()
                    .map(ProjectStaDeviceAirMonthEntity::getAirconditionercontrollerOnlinetimeTotal)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal::add)
                    .ifPresent(device::setAirconditionercontrollerOnlinetimeTotal);
            deviceMonthData.stream()
                    .map(ProjectStaDeviceAirMonthEntity::getAirconditionercontrollerRunningtimeTotal)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal::add)
                    .ifPresent(device::setAirconditionercontrollerRunningtimeTotal);
            deviceMonthData.stream()
                    .map(ProjectStaDeviceAirMonthEntity::getAirconditionercontrollerActualtempAvg)
                    .filter(Objects::nonNull)
                    .mapToDouble(BigDecimal::doubleValue)
                    .average()
                    .ifPresent(avg -> device.setAirconditionercontrollerActualtempAvg(BigDecimal.valueOf(avg)));
        }
        projectStaDeviceAirYearMapper.delete(new LambdaUpdateWrapper<ProjectStaDeviceAirYearEntity>()
                .eq(ProjectStaDeviceAirYearEntity::getYear, context.getYear())
                .in(ProjectStaDeviceAirYearEntity::getBizDeviceId,
                        deviceList.stream()
                                .map(ProjectStaDeviceAirYearEntity::getBizDeviceId)
                                .collect(Collectors.toList()))
        );
        projectStaDeviceAirYearMapper.insertBatchSomeColumn(deviceList);
    }

    private void staElectricity(StaDeviceYearTaskContext context, List<DeviceStaDTO> deviceStaDTOList) {
        List<ProjectStaDeviceElectricityYearEntity> deviceList = new ArrayList<>();
        List<String> bizDeviceIds = deviceStaDTOList.stream().map(DeviceStaDTO::getBizDeviceId).collect(Collectors.toList());
        // 获取存在手工报表的设备
        List<String> manualDeviceIdList = projectStaDeviceElectricityYearMapper.selectList(
                new LambdaQueryWrapper<ProjectStaDeviceElectricityYearEntity>()
                        .select(ProjectStaDeviceElectricityYearEntity::getBizDeviceId)
                        .eq(ProjectStaDeviceElectricityYearEntity::getManualFlag, 1)
                        .eq(ProjectStaDeviceElectricityYearEntity::getYear, context.getYear())
                        .in(ProjectStaDeviceElectricityYearEntity::getBizDeviceId, bizDeviceIds)
        ).stream().map(ProjectStaDeviceElectricityYearEntity::getBizDeviceId).toList();

        bizDeviceIds.removeAll(manualDeviceIdList);
        deviceStaDTOList.removeIf(o -> manualDeviceIdList.contains(o.getBizDeviceId()));
        if (bizDeviceIds.isEmpty()) {
            return;
        }

        Map<String, List<ProjectStaDeviceElectricityMonthEntity>> monthDataList = projectStaDeviceElectricityMonthMapper.selectList(new LambdaQueryWrapper<ProjectStaDeviceElectricityMonthEntity>()
                        .eq(ProjectStaDeviceElectricityMonthEntity::getYear, context.getYear())
                        .in(ProjectStaDeviceElectricityMonthEntity::getBizDeviceId, bizDeviceIds))
                .stream()
                .collect(Collectors.groupingBy(ProjectStaDeviceElectricityMonthEntity::getBizDeviceId));

        for (DeviceStaDTO deviceStaDTO : deviceStaDTOList) {
            // 产品要求 没有数据给null
            ProjectStaDeviceElectricityYearEntity device = new ProjectStaDeviceElectricityYearEntity();
            deviceList.add(device);

            BeanUtil.copyProperties(deviceStaDTO, device);
            device.setTenantId(context.getTenantId()).setTenantCode(context.getTenantCode())
                    .setYear(context.getYear()).setStaTime(context.getTimestamp());

            List<ProjectStaDeviceElectricityMonthEntity> deviceMonthData = monthDataList.get(deviceStaDTO.getBizDeviceId());
            if (CollectionUtil.isEmpty(deviceMonthData)) {
                continue;
            }
            deviceMonthData.stream()
                    .map(ProjectStaDeviceElectricityMonthEntity::getEnergymeterEpimportTotal)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal::add)
                    .ifPresent(device::setEnergymeterEpimportTotal);
            deviceMonthData.stream()
                    .map(ProjectStaDeviceElectricityMonthEntity::getEnergymeterEpexportTotal)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal::add)
                    .ifPresent(device::setEnergymeterEpexportTotal);
        }
        projectStaDeviceElectricityYearMapper.delete(new LambdaUpdateWrapper<ProjectStaDeviceElectricityYearEntity>()
                .eq(ProjectStaDeviceElectricityYearEntity::getYear, context.getYear())
                .in(ProjectStaDeviceElectricityYearEntity::getBizDeviceId,
                        deviceList.stream()
                                .map(ProjectStaDeviceElectricityYearEntity::getBizDeviceId)
                                .collect(Collectors.toList()))
        );
        projectStaDeviceElectricityYearMapper.insertBatchSomeColumn(deviceList);
    }

    private void staGas(StaDeviceYearTaskContext context, List<DeviceStaDTO> deviceStaDTOList) {
        List<ProjectStaDeviceGasYearEntity> deviceList = new ArrayList<>();
        List<String> bizDeviceIds = deviceStaDTOList.stream().map(DeviceStaDTO::getBizDeviceId).collect(Collectors.toList());
        // 获取存在手工报表的设备
        List<String> manualDeviceIdList = projectStaDeviceGasYearMapper.selectList(
                new LambdaQueryWrapper<ProjectStaDeviceGasYearEntity>()
                        .select(ProjectStaDeviceGasYearEntity::getBizDeviceId)
                        .eq(ProjectStaDeviceGasYearEntity::getManualFlag, 1)
                        .eq(ProjectStaDeviceGasYearEntity::getYear, context.getYear())
                        .in(ProjectStaDeviceGasYearEntity::getBizDeviceId, bizDeviceIds)
        ).stream().map(ProjectStaDeviceGasYearEntity::getBizDeviceId).toList();

        bizDeviceIds.removeAll(manualDeviceIdList);
        deviceStaDTOList.removeIf(o -> manualDeviceIdList.contains(o.getBizDeviceId()));
        if (bizDeviceIds.isEmpty()) {
            return;
        }

        Map<String, List<ProjectStaDeviceGasMonthEntity>> monthDataList = projectStaDeviceGasMonthMapper.selectList(new LambdaQueryWrapper<ProjectStaDeviceGasMonthEntity>()
                        .eq(ProjectStaDeviceGasMonthEntity::getYear, context.getYear())
                        .in(ProjectStaDeviceGasMonthEntity::getBizDeviceId, bizDeviceIds))
                .stream()
                .collect(Collectors.groupingBy(ProjectStaDeviceGasMonthEntity::getBizDeviceId));

        for (DeviceStaDTO deviceStaDTO : deviceStaDTOList) {
            // 产品要求 没有数据给null
            ProjectStaDeviceGasYearEntity device = new ProjectStaDeviceGasYearEntity();
            deviceList.add(device);

            BeanUtil.copyProperties(deviceStaDTO, device);
            device.setTenantId(context.getTenantId()).setTenantCode(context.getTenantCode())
                    .setYear(context.getYear()).setStaTime(context.getTimestamp());

            List<ProjectStaDeviceGasMonthEntity> deviceMonthData = monthDataList.get(deviceStaDTO.getBizDeviceId());
            if (CollectionUtil.isEmpty(deviceMonthData)) {
                continue;
            }
            deviceMonthData.stream()
                    .map(ProjectStaDeviceGasMonthEntity::getGasmeterUsageTotal)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal::add)
                    .ifPresent(device::setGasmeterUsageTotal);
            // 新增期初期末值逻辑
            deviceMonthData.stream()
                    .filter(o -> null != o.getGasmeterUsageTotal())
                    .min(Comparator.comparing(ProjectStaDeviceGasMonthEntity::getStaTime))
                    .ifPresent(o -> device.setGasmeterUsageTotalStart(o.getGasmeterUsageTotalStart()));
            deviceMonthData.stream()
                    .filter(o -> null != o.getGasmeterUsageTotal())
                    .max(Comparator.comparing(ProjectStaDeviceGasMonthEntity::getStaTime))
                    .ifPresent(o -> device.setGasmeterUsageTotalEnd(o.getGasmeterUsageTotalEnd()));
        }
        projectStaDeviceGasYearMapper.delete(new LambdaUpdateWrapper<ProjectStaDeviceGasYearEntity>()
                .eq(ProjectStaDeviceGasYearEntity::getYear, context.getYear())
                .in(ProjectStaDeviceGasYearEntity::getBizDeviceId,
                        deviceList.stream()
                                .map(ProjectStaDeviceGasYearEntity::getBizDeviceId)
                                .collect(Collectors.toList()))
        );
        projectStaDeviceGasYearMapper.insertBatchSomeColumn(deviceList);
    }

    private void staWater(StaDeviceYearTaskContext context, List<DeviceStaDTO> deviceStaDTOList) {
        List<ProjectStaDeviceWaterYearEntity> deviceList = new ArrayList<>();
        List<String> bizDeviceIds = deviceStaDTOList.stream().map(DeviceStaDTO::getBizDeviceId).collect(Collectors.toList());
        // 获取存在手工报表的设备
        List<String> manualDeviceIdList = projectStaDeviceWaterYearMapper.selectList(
                new LambdaQueryWrapper<ProjectStaDeviceWaterYearEntity>()
                        .select(ProjectStaDeviceWaterYearEntity::getBizDeviceId)
                        .eq(ProjectStaDeviceWaterYearEntity::getManualFlag, 1)
                        .eq(ProjectStaDeviceWaterYearEntity::getYear, context.getYear())
                        .in(ProjectStaDeviceWaterYearEntity::getBizDeviceId, bizDeviceIds)
        ).stream().map(ProjectStaDeviceWaterYearEntity::getBizDeviceId).toList();

        bizDeviceIds.removeAll(manualDeviceIdList);
        deviceStaDTOList.removeIf(o -> manualDeviceIdList.contains(o.getBizDeviceId()));
        if (bizDeviceIds.isEmpty()) {
            return;
        }

        Map<String, List<ProjectStaDeviceWaterMonthEntity>> monthDataList = projectStaDeviceWaterMonthMapper.selectList(new LambdaQueryWrapper<ProjectStaDeviceWaterMonthEntity>()
                        .eq(ProjectStaDeviceWaterMonthEntity::getYear, context.getYear())
                        .in(ProjectStaDeviceWaterMonthEntity::getBizDeviceId, bizDeviceIds))
                .stream()
                .collect(Collectors.groupingBy(ProjectStaDeviceWaterMonthEntity::getBizDeviceId));

        for (DeviceStaDTO deviceStaDTO : deviceStaDTOList) {
            // 产品要求 没有数据给null
            ProjectStaDeviceWaterYearEntity device = new ProjectStaDeviceWaterYearEntity();
            deviceList.add(device);

            BeanUtil.copyProperties(deviceStaDTO, device);
            device.setTenantId(context.getTenantId()).setTenantCode(context.getTenantCode())
                    .setYear(context.getYear()).setStaTime(context.getTimestamp());

            List<ProjectStaDeviceWaterMonthEntity> deviceMonthData = monthDataList.get(deviceStaDTO.getBizDeviceId());
            if (CollectionUtil.isEmpty(deviceMonthData)) {
                continue;
            }
            deviceMonthData.stream()
                    .map(ProjectStaDeviceWaterMonthEntity::getWatermeterUsageTotal)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal::add)
                    .ifPresent(device::setWatermeterUsageTotal);
            // 新增期初期末值逻辑
            deviceMonthData.stream()
                    .filter(o -> null != o.getWatermeterUsageTotal())
                    .min(Comparator.comparing(ProjectStaDeviceWaterMonthEntity::getStaTime))
                    .ifPresent(o -> device.setWatermeterUsageTotalStart(o.getWatermeterUsageTotalStart()));
            deviceMonthData.stream()
                    .filter(o -> null != o.getWatermeterUsageTotal())
                    .max(Comparator.comparing(ProjectStaDeviceWaterMonthEntity::getStaTime))
                    .ifPresent(o -> device.setWatermeterUsageTotalEnd(o.getWatermeterUsageTotalEnd()));
        }
        projectStaDeviceWaterYearMapper.delete(new LambdaUpdateWrapper<ProjectStaDeviceWaterYearEntity>()
                .eq(ProjectStaDeviceWaterYearEntity::getYear, context.getYear())
                .in(ProjectStaDeviceWaterYearEntity::getBizDeviceId,
                        deviceList.stream()
                                .map(ProjectStaDeviceWaterYearEntity::getBizDeviceId)
                                .collect(Collectors.toList()))
        );
        projectStaDeviceWaterYearMapper.insertBatchSomeColumn(deviceList);
    }
}
