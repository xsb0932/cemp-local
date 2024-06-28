package com.landleaf.energy.service.job;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.landleaf.comm.constance.DeviceStaCategoryEnum;
import com.landleaf.data.api.device.DeviceHistoryApi;
import com.landleaf.data.api.device.dto.*;
import com.landleaf.energy.dal.mapper.*;
import com.landleaf.energy.domain.dto.StaDeviceHourTaskContext;
import com.landleaf.energy.domain.entity.*;
import com.landleaf.monitor.api.DeviceStaApi;
import com.landleaf.monitor.api.dto.DeviceStaDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.landleaf.energy.domain.enums.DeviceConstants.MULTIPLYING_FACTOR;

/**
 * @author Yang
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DeviceStaHourService {
    private final DeviceStaApi deviceStaApi;
    private final DeviceHistoryApi deviceHistoryApi;
    private final ProjectStaDeviceElectricityHourMapper projectStaDeviceElectricityHourMapper;
    private final ProjectStaDeviceGasHourMapper projectStaDeviceGasHourMapper;
    private final ProjectStaDeviceWaterHourMapper projectStaDeviceWaterHourMapper;
    private final ProjectStaDeviceAirHourMapper projectStaDeviceAirHourMapper;
    private final ProjectStaDeviceZnbHourMapper projectStaDeviceZnbHourMapper;
    private final ProjectStaDeviceGscnHourMapper projectStaDeviceGscnHourMapper;
    private final DeviceParameterDetailMapper deviceParameterDetailMapper;

    public String execute(Long tenantId, String tenantCode, DeviceStaCategoryEnum categoryEnum, LocalDateTime staTime, String projectIds) {
        LocalDateTime lastHour = staTime.minusHours(1L);
        LocalDateTime startTime = LocalDateTime.of(lastHour.getYear(), lastHour.getMonthValue(), lastHour.getDayOfMonth(), lastHour.getHour(), 0, 0);
        LocalDateTime endTime = LocalDateTime.of(staTime.getYear(), staTime.getMonthValue(), staTime.getDayOfMonth(), staTime.getHour(), 0, 0);
        StaDeviceHourTaskContext context = new StaDeviceHourTaskContext(tenantId, tenantCode, staTime, startTime, endTime);
        // 获取设备信息
        List<DeviceStaDTO> deviceStaDTOList = deviceStaApi.listStaDeviceByCategory(tenantId, categoryEnum.getCode()).getCheckedData();
        // 过滤要执行的项目id
        deviceStaDTOList = deviceStaDTOList.stream().filter(o -> StrUtil.contains(projectIds, o.getBizProjectId())).collect(Collectors.toList());
        String msg = "品类 " + categoryEnum.getCode() + " 统计设备 " + deviceStaDTOList.stream().map(DeviceStaDTO::getBizDeviceId).collect(Collectors.joining(","));
        // 统计设备数据并入库
        if (CollectionUtil.isNotEmpty(deviceStaDTOList)) {
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

    private void staAir(StaDeviceHourTaskContext context, List<DeviceStaDTO> deviceStaDTOList) {
        List<ProjectStaDeviceAirHourEntity> deviceList = new ArrayList<>();
        // 获取存在手工报表的设备
        List<String> manualDeviceIdList = projectStaDeviceAirHourMapper.selectList(
                new LambdaQueryWrapper<ProjectStaDeviceAirHourEntity>()
                        .select(ProjectStaDeviceAirHourEntity::getBizDeviceId)
                        .eq(ProjectStaDeviceAirHourEntity::getManualFlag, 1)
                        .eq(ProjectStaDeviceAirHourEntity::getYear, context.getYear())
                        .eq(ProjectStaDeviceAirHourEntity::getMonth, context.getMonth())
                        .eq(ProjectStaDeviceAirHourEntity::getDay, context.getDay())
                        .eq(ProjectStaDeviceAirHourEntity::getHour, context.getHour())
                        .in(ProjectStaDeviceAirHourEntity::getBizDeviceId, deviceStaDTOList.stream().map(DeviceStaDTO::getBizDeviceId).toList())
        ).stream().map(ProjectStaDeviceAirHourEntity::getBizDeviceId).toList();

        deviceStaDTOList.removeIf(o -> manualDeviceIdList.contains(o.getBizDeviceId()));
        if (CollectionUtil.isEmpty(deviceStaDTOList)) {
            return;
        }

        Map<String, List<String>> productDeviceIds = deviceStaDTOList.stream()
                .collect(Collectors.groupingBy(DeviceStaDTO::getBizProductId, Collectors.mapping(DeviceStaDTO::getBizDeviceId, Collectors.toList())));
        // 获取空调运行数据
        Map<String, StaDeviceAirResponse> deviceDataMap = deviceHistoryApi.getStaAirDeviceData(
                StaDeviceBaseRequest.builder()
                        .start(context.getStartTime())
                        .end(context.getEndTime())
                        .productDeviceIds(productDeviceIds)
                        .build()
        ).getCheckedData().stream().collect(Collectors.toMap(StaDeviceAirResponse::getBizDeviceId, o -> o));
        // 处理数据
        for (DeviceStaDTO deviceStaDTO : deviceStaDTOList) {
            // 产品要求 没有数据给null
            ProjectStaDeviceAirHourEntity entity = new ProjectStaDeviceAirHourEntity();
            deviceList.add(entity);
            BeanUtil.copyProperties(deviceStaDTO, entity);
            entity.setTenantId(context.getTenantId()).setTenantCode(context.getTenantCode())
                    .setYear(context.getYear()).setMonth(context.getMonth()).setDay(context.getDay()).setHour(context.getHour()).setStaTime(context.getTimestamp());
            StaDeviceAirResponse influxData = deviceDataMap.get(deviceStaDTO.getBizDeviceId());
            if (null == influxData) {
                continue;
            }
            if (CollectionUtil.isNotEmpty(influxData.getCstDataList())) {
                entity.setAirconditionercontrollerOnlinetimeTotal(
                        BigDecimal.valueOf(countValueTime(influxData.getCstDataList(), 1))
                );
            }
            if (CollectionUtil.isNotEmpty(influxData.getRstDataList())) {
                entity.setAirconditionercontrollerRunningtimeTotal(
                        BigDecimal.valueOf(countValueTime(influxData.getRstDataList(), 1))
                );
            }
            if (CollectionUtil.isNotEmpty(influxData.getTemperatureDataList())) {
                influxData.getTemperatureDataList()
                        .stream()
                        .mapToDouble(o -> o.value().doubleValue())
                        .average()
                        .ifPresent(o -> entity.setAirconditionercontrollerActualtempAvg(BigDecimal.valueOf(o)));
            }
        }
        projectStaDeviceAirHourMapper.delete(new LambdaUpdateWrapper<ProjectStaDeviceAirHourEntity>()
                .eq(ProjectStaDeviceAirHourEntity::getYear, context.getYear())
                .eq(ProjectStaDeviceAirHourEntity::getMonth, context.getMonth())
                .eq(ProjectStaDeviceAirHourEntity::getDay, context.getDay())
                .eq(ProjectStaDeviceAirHourEntity::getHour, context.getHour())
                .in(ProjectStaDeviceAirHourEntity::getBizDeviceId,
                        deviceList.stream()
                                .map(ProjectStaDeviceAirHourEntity::getBizDeviceId)
                                .toList()));
        projectStaDeviceAirHourMapper.insertBatchSomeColumn(deviceList);
    }

    /**
     * 获取指定数值的时间区间
     *
     * @param dataList  数据
     * @param targetVal 指定值
     * @return 秒数
     */
    private long countValueTime(List<IntAttrValue> dataList, Integer targetVal) {
        // 按照时间排序
        dataList.sort(Comparator.comparing(IntAttrValue::time));

        // 计算值为 targetVal 的持续时间总时长
        Duration totalDuration = Duration.ZERO;
        LocalDateTime startTime = null;

        for (IntAttrValue attrValue : dataList) {
            if (Objects.equals(attrValue.value(), targetVal)) {
                if (startTime == null) {
                    // 进入值为 targetVal  的区间
                    startTime = attrValue.time();
                }
            } else {
                if (startTime != null) {
                    // 离开值为 targetVal 的区间，计算持续时间并累加
                    Duration duration = Duration.between(startTime, attrValue.time());
                    totalDuration = totalDuration.plus(duration);
                    startTime = null;
                }
            }
        }

        // 如果最后一个数据对象的值为 targetVal，则需要计算最后一个区间的持续时间
        if (startTime != null) {
            Duration duration = Duration.between(startTime, dataList.get(dataList.size() - 1).time());
            totalDuration = totalDuration.plus(duration);
        }
        return totalDuration.getSeconds();
    }

    private void staElectricity(StaDeviceHourTaskContext context, List<DeviceStaDTO> deviceStaDTOList) {
        List<ProjectStaDeviceElectricityHourEntity> deviceList = new ArrayList<>();
        // 获取存在手工报表的设备
        List<String> manualDeviceIdList = projectStaDeviceElectricityHourMapper.selectList(
                new LambdaQueryWrapper<ProjectStaDeviceElectricityHourEntity>()
                        .select(ProjectStaDeviceElectricityHourEntity::getBizDeviceId)
                        .eq(ProjectStaDeviceElectricityHourEntity::getManualFlag, 1)
                        .eq(ProjectStaDeviceElectricityHourEntity::getYear, context.getYear())
                        .eq(ProjectStaDeviceElectricityHourEntity::getMonth, context.getMonth())
                        .eq(ProjectStaDeviceElectricityHourEntity::getDay, context.getDay())
                        .eq(ProjectStaDeviceElectricityHourEntity::getHour, context.getHour())
                        .in(ProjectStaDeviceElectricityHourEntity::getBizDeviceId, deviceStaDTOList.stream().map(DeviceStaDTO::getBizDeviceId).toList())
        ).stream().map(ProjectStaDeviceElectricityHourEntity::getBizDeviceId).toList();

        deviceStaDTOList.removeIf(o -> manualDeviceIdList.contains(o.getBizDeviceId()));
        if (CollectionUtil.isEmpty(deviceStaDTOList)) {
            return;
        }

        Map<String, List<String>> productDeviceIds = deviceStaDTOList.stream()
                .collect(Collectors.groupingBy(DeviceStaDTO::getBizProductId, Collectors.mapping(DeviceStaDTO::getBizDeviceId, Collectors.toList())));
        // 获取设备电量数据
        Map<String, StaDeviceElectricityResponse> deviceDataMap = deviceHistoryApi.getStaElectricityDeviceData(
                StaDeviceBaseRequest.builder()
                        .start(context.getStartTime())
                        .end(context.getEndTime())
                        .productDeviceIds(productDeviceIds)
                        .build()
        ).getCheckedData().stream().collect(Collectors.toMap(StaDeviceElectricityResponse::getBizDeviceId, o -> o));
        // 处理数据
        // 电表倍率
        for (DeviceStaDTO deviceStaDTO : deviceStaDTOList) {
            DeviceParameterDetailEntity parameter = deviceParameterDetailMapper.getParameter(deviceStaDTO.getBizDeviceId(), MULTIPLYING_FACTOR);
            BigDecimal coefficient = null;
            if (parameter != null && StringUtils.isNotBlank(parameter.getValue())) {
                coefficient = new BigDecimal(parameter.getValue());
            }
            // 产品要求 没有数据给null
            ProjectStaDeviceElectricityHourEntity entity = new ProjectStaDeviceElectricityHourEntity();
            deviceList.add(entity);
            BeanUtil.copyProperties(deviceStaDTO, entity);
            entity.setTenantId(context.getTenantId()).setTenantCode(context.getTenantCode())
                    .setYear(context.getYear()).setMonth(context.getMonth()).setDay(context.getDay()).setHour(context.getHour()).setStaTime(context.getTimestamp());
            StaDeviceElectricityResponse influxData = deviceDataMap.get(deviceStaDTO.getBizDeviceId());
            if (null != influxData) {
                if (null != influxData.getStartData() && null != influxData.getEndData()) {
                    entity.setEnergymeterEpimportTotal(
                            influxData.getEndData().subtract(influxData.getStartData())
                                    .multiply(coefficient != null ? coefficient : BigDecimal.ONE)
                                    .setScale(2, RoundingMode.HALF_UP)
                    );
                    entity.setEnergymeterEpimportStart(influxData.getStartData());
                    entity.setEnergymeterEpimportEnd(influxData.getEndData());
                }
                if (null != influxData.getReStartData() && null != influxData.getReEndData()) {
                    entity.setEnergymeterEpexportTotal(
                            influxData.getReEndData().subtract(influxData.getReStartData())
                                    .multiply(coefficient != null ? coefficient : BigDecimal.ONE)
                                    .setScale(2, RoundingMode.HALF_UP)
                    );
                }
            }
        }
        projectStaDeviceElectricityHourMapper.delete(new LambdaUpdateWrapper<ProjectStaDeviceElectricityHourEntity>()
                .eq(ProjectStaDeviceElectricityHourEntity::getYear, context.getYear())
                .eq(ProjectStaDeviceElectricityHourEntity::getMonth, context.getMonth())
                .eq(ProjectStaDeviceElectricityHourEntity::getDay, context.getDay())
                .eq(ProjectStaDeviceElectricityHourEntity::getHour, context.getHour())
                .in(ProjectStaDeviceElectricityHourEntity::getBizDeviceId,
                        deviceList.stream()
                                .map(ProjectStaDeviceElectricityHourEntity::getBizDeviceId)
                                .toList()));
        projectStaDeviceElectricityHourMapper.insertBatchSomeColumn(deviceList);
    }

    private void staGas(StaDeviceHourTaskContext context, List<DeviceStaDTO> deviceStaDTOList) {
        List<ProjectStaDeviceGasHourEntity> deviceList = new ArrayList<>();
        // 获取存在手工报表的设备
        List<String> manualDeviceIdList = projectStaDeviceGasHourMapper.selectList(
                new LambdaQueryWrapper<ProjectStaDeviceGasHourEntity>()
                        .select(ProjectStaDeviceGasHourEntity::getBizDeviceId)
                        .eq(ProjectStaDeviceGasHourEntity::getManualFlag, 1)
                        .eq(ProjectStaDeviceGasHourEntity::getYear, context.getYear())
                        .eq(ProjectStaDeviceGasHourEntity::getMonth, context.getMonth())
                        .eq(ProjectStaDeviceGasHourEntity::getDay, context.getDay())
                        .eq(ProjectStaDeviceGasHourEntity::getHour, context.getHour())
                        .in(ProjectStaDeviceGasHourEntity::getBizDeviceId, deviceStaDTOList.stream().map(DeviceStaDTO::getBizDeviceId).toList())
        ).stream().map(ProjectStaDeviceGasHourEntity::getBizDeviceId).toList();

        deviceStaDTOList.removeIf(o -> manualDeviceIdList.contains(o.getBizDeviceId()));
        if (CollectionUtil.isEmpty(deviceStaDTOList)) {
            return;
        }

        Map<String, List<String>> productDeviceIds = deviceStaDTOList.stream()
                .collect(Collectors.groupingBy(DeviceStaDTO::getBizProductId, Collectors.mapping(DeviceStaDTO::getBizDeviceId, Collectors.toList())));
        // 获取设备用气量数据
        Map<String, StaDeviceGasResponse> deviceDataMap = deviceHistoryApi.getStaGasDeviceData(
                StaDeviceBaseRequest.builder()
                        .start(context.getStartTime())
                        .end(context.getEndTime())
                        .productDeviceIds(productDeviceIds)
                        .build()
        ).getCheckedData().stream().collect(Collectors.toMap(StaDeviceGasResponse::getBizDeviceId, o -> o));
        // 处理数据
        for (DeviceStaDTO deviceStaDTO : deviceStaDTOList) {
            // 产品要求 没有数据给null
            ProjectStaDeviceGasHourEntity entity = new ProjectStaDeviceGasHourEntity();
            deviceList.add(entity);
            BeanUtil.copyProperties(deviceStaDTO, entity);
            entity.setTenantId(context.getTenantId()).setTenantCode(context.getTenantCode())
                    .setYear(context.getYear()).setMonth(context.getMonth()).setDay(context.getDay()).setHour(context.getHour()).setStaTime(context.getTimestamp());
            StaDeviceGasResponse influxData = deviceDataMap.get(deviceStaDTO.getBizDeviceId());
            if (null == influxData) {
                continue;
            }
            // 新增期初值和期末值逻辑
            entity.setGasmeterUsageTotalStart(influxData.getStartData())
                    .setGasmeterUsageTotalEnd(influxData.getEndData());
            if (null == influxData.getStartData() || null == influxData.getEndData()) {
                continue;
            }
            entity.setGasmeterUsageTotal(
                    influxData.getEndData().subtract(influxData.getStartData()).setScale(2, RoundingMode.HALF_UP)
            );
        }
        projectStaDeviceGasHourMapper.delete(new LambdaUpdateWrapper<ProjectStaDeviceGasHourEntity>()
                .eq(ProjectStaDeviceGasHourEntity::getYear, context.getYear())
                .eq(ProjectStaDeviceGasHourEntity::getMonth, context.getMonth())
                .eq(ProjectStaDeviceGasHourEntity::getDay, context.getDay())
                .eq(ProjectStaDeviceGasHourEntity::getHour, context.getHour())
                .in(ProjectStaDeviceGasHourEntity::getBizDeviceId,
                        deviceList.stream()
                                .map(ProjectStaDeviceGasHourEntity::getBizDeviceId)
                                .toList()));
        projectStaDeviceGasHourMapper.insertBatchSomeColumn(deviceList);
    }

    private void staWater(StaDeviceHourTaskContext context, List<DeviceStaDTO> deviceStaDTOList) {
        List<ProjectStaDeviceWaterHourEntity> deviceList = new ArrayList<>();
        // 获取存在手工报表的设备
        List<String> manualDeviceIdList = projectStaDeviceWaterHourMapper.selectList(
                new LambdaQueryWrapper<ProjectStaDeviceWaterHourEntity>()
                        .select(ProjectStaDeviceWaterHourEntity::getBizDeviceId)
                        .eq(ProjectStaDeviceWaterHourEntity::getManualFlag, 1)
                        .eq(ProjectStaDeviceWaterHourEntity::getYear, context.getYear())
                        .eq(ProjectStaDeviceWaterHourEntity::getMonth, context.getMonth())
                        .eq(ProjectStaDeviceWaterHourEntity::getDay, context.getDay())
                        .eq(ProjectStaDeviceWaterHourEntity::getHour, context.getHour())
                        .in(ProjectStaDeviceWaterHourEntity::getBizDeviceId, deviceStaDTOList.stream().map(DeviceStaDTO::getBizDeviceId).toList())
        ).stream().map(ProjectStaDeviceWaterHourEntity::getBizDeviceId).toList();

        deviceStaDTOList.removeIf(o -> manualDeviceIdList.contains(o.getBizDeviceId()));
        if (CollectionUtil.isEmpty(deviceStaDTOList)) {
            return;
        }

        Map<String, List<String>> productDeviceIds = deviceStaDTOList.stream()
                .collect(Collectors.groupingBy(DeviceStaDTO::getBizProductId, Collectors.mapping(DeviceStaDTO::getBizDeviceId, Collectors.toList())));
        // 获取设备用水量数据
        Map<String, StaDeviceWaterResponse> deviceDataMap = deviceHistoryApi.getStaWaterDeviceData(
                StaDeviceBaseRequest.builder()
                        .start(context.getStartTime())
                        .end(context.getEndTime())
                        .productDeviceIds(productDeviceIds)
                        .build()
        ).getCheckedData().stream().collect(Collectors.toMap(StaDeviceWaterResponse::getBizDeviceId, o -> o));
        // 处理数据
        for (DeviceStaDTO deviceStaDTO : deviceStaDTOList) {
            // 产品要求 没有数据给null
            ProjectStaDeviceWaterHourEntity entity = new ProjectStaDeviceWaterHourEntity();
            deviceList.add(entity);
            BeanUtil.copyProperties(deviceStaDTO, entity);
            entity.setTenantId(context.getTenantId()).setTenantCode(context.getTenantCode())
                    .setYear(context.getYear()).setMonth(context.getMonth()).setDay(context.getDay()).setHour(context.getHour()).setStaTime(context.getTimestamp());
            StaDeviceWaterResponse influxData = deviceDataMap.get(deviceStaDTO.getBizDeviceId());
            if (null == influxData) {
                continue;
            }
            // 新增期初值和期末值逻辑
            entity.setWatermeterUsageTotalStart(influxData.getStartData())
                    .setWatermeterUsageTotalEnd(influxData.getEndData());
            if (null == influxData.getStartData() || null == influxData.getEndData()) {
                continue;
            }
            entity.setWatermeterUsageTotal(
                    influxData.getEndData().subtract(influxData.getStartData()).setScale(2, RoundingMode.HALF_UP)
            );
        }
        projectStaDeviceWaterHourMapper.delete(new LambdaUpdateWrapper<ProjectStaDeviceWaterHourEntity>()
                .eq(ProjectStaDeviceWaterHourEntity::getYear, context.getYear())
                .eq(ProjectStaDeviceWaterHourEntity::getMonth, context.getMonth())
                .eq(ProjectStaDeviceWaterHourEntity::getDay, context.getDay())
                .eq(ProjectStaDeviceWaterHourEntity::getHour, context.getHour())
                .in(ProjectStaDeviceWaterHourEntity::getBizDeviceId,
                        deviceList.stream()
                                .map(ProjectStaDeviceWaterHourEntity::getBizDeviceId)
                                .toList()));
        projectStaDeviceWaterHourMapper.insertBatchSomeColumn(deviceList);
    }

    private void staZnb(StaDeviceHourTaskContext context, List<DeviceStaDTO> deviceStaDTOList) {
        List<ProjectStaDeviceZnbHourEntity> deviceList = new ArrayList<>();
        // 获取存在手工报表的设备
        List<String> manualDeviceIdList = projectStaDeviceZnbHourMapper.selectList(
                new LambdaQueryWrapper<ProjectStaDeviceZnbHourEntity>()
                        .select(ProjectStaDeviceZnbHourEntity::getBizDeviceId)
                        .eq(ProjectStaDeviceZnbHourEntity::getManualFlag, 1)
                        .eq(ProjectStaDeviceZnbHourEntity::getYear, context.getYear())
                        .eq(ProjectStaDeviceZnbHourEntity::getMonth, context.getMonth())
                        .eq(ProjectStaDeviceZnbHourEntity::getDay, context.getDay())
                        .eq(ProjectStaDeviceZnbHourEntity::getHour, context.getHour())
                        .in(ProjectStaDeviceZnbHourEntity::getBizDeviceId, deviceStaDTOList.stream().map(DeviceStaDTO::getBizDeviceId).toList())
        ).stream().map(ProjectStaDeviceZnbHourEntity::getBizDeviceId).toList();

        deviceStaDTOList.removeIf(o -> manualDeviceIdList.contains(o.getBizDeviceId()));
        if (CollectionUtil.isEmpty(deviceStaDTOList)) {
            return;
        }

        Map<String, List<String>> productDeviceIds = deviceStaDTOList.stream()
                .collect(Collectors.groupingBy(DeviceStaDTO::getBizProductId, Collectors.mapping(DeviceStaDTO::getBizDeviceId, Collectors.toList())));
        // 获取设备电量数据
        Map<String, StaDeviceZnbResponse> deviceDataMap = deviceHistoryApi.getStaZnbDeviceData(
                StaDeviceBaseRequest.builder()
                        .start(context.getStartTime())
                        .end(context.getEndTime())
                        .productDeviceIds(productDeviceIds)
                        .build()
        ).getCheckedData().stream().collect(Collectors.toMap(StaDeviceZnbResponse::getBizDeviceId, o -> o));
        // 处理数据
        for (DeviceStaDTO deviceStaDTO : deviceStaDTOList) {
            // 产品要求 没有数据给null
            ProjectStaDeviceZnbHourEntity entity = new ProjectStaDeviceZnbHourEntity();
            deviceList.add(entity);
            BeanUtil.copyProperties(deviceStaDTO, entity);
            entity.setTenantId(context.getTenantId()).setTenantCode(context.getTenantCode())
                    .setYear(context.getYear()).setMonth(context.getMonth()).setDay(context.getDay()).setHour(context.getHour()).setStaTime(context.getTimestamp());
            StaDeviceZnbResponse influxData = deviceDataMap.get(deviceStaDTO.getBizDeviceId());
            if (null != influxData) {
                if (null != influxData.getEpimpEndData() && null != influxData.getEpimpStartData()) {
                    entity.setZnbEpimportTotal(
                            influxData.getEpimpEndData().subtract(influxData.getEpimpStartData())
                                    .setScale(2, RoundingMode.HALF_UP)
                    );
                }
                // 有功发电量
                if (null != influxData.getEpexpEndData() && null != influxData.getEpexpStartData()) {
                    BigDecimal epexp = influxData.getEpexpEndData().subtract(influxData.getEpexpStartData());
                    entity.setZnbEpexportTotal(
                            epexp.setScale(2, RoundingMode.HALF_UP)
                    );
                    if (deviceStaDTO.getOtherParams() != null) {
                        String nbPvPower = deviceStaDTO.getOtherParams().get("nbPvPower");
                        if (nbPvPower != null) {
                            entity.setZnbEptoHourTotal(epexp.divide(new BigDecimal(nbPvPower), 2, RoundingMode.HALF_UP));
                        }
                    }
                }
                if (null != influxData.getEqimpEndData() && null != influxData.getEqimpStartData()) {
                    entity.setZnbEqimportTotal(
                            influxData.getEqimpEndData().subtract(influxData.getEqimpStartData())
                                    .setScale(2, RoundingMode.HALF_UP)
                    );
                }
                if (null != influxData.getEqexpEndData() && null != influxData.getEqexpStartData()) {
                    entity.setZnbEpexportTotal(
                            influxData.getEqexpEndData().subtract(influxData.getEqexpStartData())
                                    .setScale(2, RoundingMode.HALF_UP)
                    );
                }
                if (null != influxData.getPMax()) {
                    entity.setZnbPMax(influxData.getPMax()
                            .setScale(2, RoundingMode.HALF_UP)
                    );
                }
                if (CollUtil.isNotEmpty(influxData.getCstDataList())) {
                    //整点补偿
                    IntAttrValue ivalue = influxData.getCstDataList().get(0);
                    StaLatestDataRequest request = StaLatestDataRequest.builder()
                            .time(ivalue.time())
                            .bizDeviceId(influxData.getBizDeviceId())
                            .bizProductId(influxData.getBizProductId())
                            .field("CST")
                            .build();

                    String latestValue = deviceHistoryApi.getLatestData(request).getResult();
                    LocalDateTime addTime = LocalDateTime.of(ivalue.time().getYear(), ivalue.time().getMonthValue(), ivalue.time().getDayOfMonth(), ivalue.time().getHour(), 0, 0);
                    influxData.getCstDataList().add(0, new IntAttrValue(addTime, new BigDecimal(latestValue).intValue()));
                    entity.setZnbOnlineTimeTotal(
                            BigDecimal.valueOf(countValueTime(influxData.getCstDataList(), 1))
                    );
                }
                if (CollUtil.isNotEmpty(influxData.getRstDataList())) {
                    //整点补偿
                    IntAttrValue ivalue = influxData.getRstDataList().get(0);
                    StaLatestDataRequest request = StaLatestDataRequest.builder()
                            .time(ivalue.time())
                            .bizDeviceId(influxData.getBizDeviceId())
                            .bizProductId(influxData.getBizProductId())
                            .field("znbRST")
                            .build();

                    String latestValue = deviceHistoryApi.getLatestData(request).getResult();
                    LocalDateTime addTime = LocalDateTime.of(ivalue.time().getYear(), ivalue.time().getMonthValue(), ivalue.time().getDayOfMonth(), ivalue.time().getHour(), 0, 0);
                    influxData.getRstDataList().add(0, new IntAttrValue(addTime, new BigDecimal(latestValue).intValue()));
                    entity.setZnbRunningTimeTotal(
                            BigDecimal.valueOf(countValueTime(influxData.getRstDataList(), 2))
                    );
                }
            }
        }
        projectStaDeviceZnbHourMapper.delete(new LambdaUpdateWrapper<ProjectStaDeviceZnbHourEntity>()
                .eq(ProjectStaDeviceZnbHourEntity::getYear, context.getYear())
                .eq(ProjectStaDeviceZnbHourEntity::getMonth, context.getMonth())
                .eq(ProjectStaDeviceZnbHourEntity::getDay, context.getDay())
                .eq(ProjectStaDeviceZnbHourEntity::getHour, context.getHour())
                .in(ProjectStaDeviceZnbHourEntity::getBizDeviceId,
                        deviceList.stream()
                                .map(ProjectStaDeviceZnbHourEntity::getBizDeviceId)
                                .toList()));
        projectStaDeviceZnbHourMapper.insertBatchSomeColumn(deviceList);
    }

    private void staGscn(StaDeviceHourTaskContext context, List<DeviceStaDTO> deviceStaDTOList) {
        List<ProjectStaDeviceGscnHourEntity> deviceList = new ArrayList<>();
        // 获取存在手工报表的设备
        List<String> manualDeviceIdList = projectStaDeviceGscnHourMapper.selectList(
                new LambdaQueryWrapper<ProjectStaDeviceGscnHourEntity>()
                        .select(ProjectStaDeviceGscnHourEntity::getBizDeviceId)
                        .eq(ProjectStaDeviceGscnHourEntity::getManualFlag, 1)
                        .eq(ProjectStaDeviceGscnHourEntity::getYear, context.getYear())
                        .eq(ProjectStaDeviceGscnHourEntity::getMonth, context.getMonth())
                        .eq(ProjectStaDeviceGscnHourEntity::getDay, context.getDay())
                        .eq(ProjectStaDeviceGscnHourEntity::getHour, context.getHour())
                        .in(ProjectStaDeviceGscnHourEntity::getBizDeviceId, deviceStaDTOList.stream().map(DeviceStaDTO::getBizDeviceId).toList())
        ).stream().map(ProjectStaDeviceGscnHourEntity::getBizDeviceId).toList();

        deviceStaDTOList.removeIf(o -> manualDeviceIdList.contains(o.getBizDeviceId()));
        if (CollectionUtil.isEmpty(deviceStaDTOList)) {
            return;
        }

        Map<String, List<String>> productDeviceIds = deviceStaDTOList.stream()
                .collect(Collectors.groupingBy(DeviceStaDTO::getBizProductId, Collectors.mapping(DeviceStaDTO::getBizDeviceId, Collectors.toList())));
        // 获取设备电量数据
        Map<String, StaDeviceGscnResponse> deviceDataMap = deviceHistoryApi.getStaGscnDeviceData(
                StaDeviceBaseRequest.builder()
                        .start(context.getStartTime())
                        .end(context.getEndTime())
                        .productDeviceIds(productDeviceIds)
                        .build()
        ).getCheckedData().stream().collect(Collectors.toMap(StaDeviceGscnResponse::getBizDeviceId, o -> o));
        // 处理数据
        for (DeviceStaDTO deviceStaDTO : deviceStaDTOList) {
            // 产品要求 没有数据给null
            ProjectStaDeviceGscnHourEntity entity = new ProjectStaDeviceGscnHourEntity();
            deviceList.add(entity);
            BeanUtil.copyProperties(deviceStaDTO, entity);
            entity.setTenantId(context.getTenantId()).setTenantCode(context.getTenantCode())
                    .setYear(context.getYear()).setMonth(context.getMonth()).setDay(context.getDay()).setHour(context.getHour()).setStaTime(context.getTimestamp());
            StaDeviceGscnResponse influxData = deviceDataMap.get(deviceStaDTO.getBizDeviceId());
            if (null != influxData) {
                if (null != influxData.getEpimpEndData() && null != influxData.getEpimpStartData()) {
                    entity.setGscnEpimportTotal(
                            influxData.getEpimpEndData().subtract(influxData.getEpimpStartData())
                                    .setScale(2, RoundingMode.HALF_UP)
                    );
                }
                if (null != influxData.getEpexpEndData() && null != influxData.getEpexpStartData()) {
                    BigDecimal epexp = influxData.getEpexpEndData().subtract(influxData.getEpexpStartData());
                    entity.setGscnEpexportTotal(
                            epexp.setScale(2, RoundingMode.HALF_UP)
                    );
                }
                if (CollectionUtil.isNotEmpty(influxData.getCstDataList())) {
                    //整点补偿
                    IntAttrValue ivalue = influxData.getCstDataList().get(0);
                    StaLatestDataRequest request = StaLatestDataRequest.builder()
                            .time(ivalue.time())
                            .bizDeviceId(influxData.getBizDeviceId())
                            .bizProductId(influxData.getBizProductId())
                            .field("CST")
                            .build();
                    String latestValue = deviceHistoryApi.getLatestData(request).getResult();
                    LocalDateTime addTime = LocalDateTime.of(ivalue.time().getYear(), ivalue.time().getMonthValue(), ivalue.time().getDayOfMonth(), ivalue.time().getHour(), 0, 0);
                    influxData.getCstDataList().add(0, new IntAttrValue(addTime, new BigDecimal(latestValue).intValue()));

                    entity.setGscnOnlineTimeTotal(
                            BigDecimal.valueOf(countValueTime(influxData.getCstDataList(), 1))
                    );
                }
                if (CollectionUtil.isNotEmpty(influxData.getPcsrstDataList())) {
                    //整点补偿
                    IntAttrValue ivalue = influxData.getPcsrstDataList().get(0);
                    StaLatestDataRequest request = StaLatestDataRequest.builder()
                            .time(ivalue.time())
                            .bizDeviceId(influxData.getBizDeviceId())
                            .bizProductId(influxData.getBizProductId())
                            .field("pcsRST")
                            .build();
                    String latestValue = deviceHistoryApi.getLatestData(request).getResult();
                    LocalDateTime addTime = LocalDateTime.of(ivalue.time().getYear(), ivalue.time().getMonthValue(), ivalue.time().getDayOfMonth(), ivalue.time().getHour(), 0, 0);
                    influxData.getPcsrstDataList().add(0, new IntAttrValue(addTime, new BigDecimal(latestValue).intValue()));

                    entity.setGscnChargeTimeTotal(
                            BigDecimal.valueOf(countValueTime(influxData.getPcsrstDataList(), 2))
                    );
                    entity.setGscnDischargeTimeTotal(
                            BigDecimal.valueOf(countValueTime(influxData.getPcsrstDataList(), 3))
                    );
                    entity.setGscnStandbyTimeTotal(
                            BigDecimal.valueOf(countValueTime(influxData.getPcsrstDataList(), 1))
                    );
                }
            }
        }
        projectStaDeviceGscnHourMapper.delete(new LambdaUpdateWrapper<ProjectStaDeviceGscnHourEntity>()
                .eq(ProjectStaDeviceGscnHourEntity::getYear, context.getYear())
                .eq(ProjectStaDeviceGscnHourEntity::getMonth, context.getMonth())
                .eq(ProjectStaDeviceGscnHourEntity::getDay, context.getDay())
                .eq(ProjectStaDeviceGscnHourEntity::getHour, context.getHour())
                .in(ProjectStaDeviceGscnHourEntity::getBizDeviceId,
                        deviceList.stream()
                                .map(ProjectStaDeviceGscnHourEntity::getBizDeviceId)
                                .toList()));
        projectStaDeviceGscnHourMapper.insertBatchSomeColumn(deviceList);
    }
}
