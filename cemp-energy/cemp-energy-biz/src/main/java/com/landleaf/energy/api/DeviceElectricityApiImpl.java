package com.landleaf.energy.api;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.landleaf.bms.api.DeviceIotApi;
import com.landleaf.bms.api.dto.DeviceIoResponse;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.comm.exception.ServiceException;
import com.landleaf.comm.tenant.TenantContext;
import com.landleaf.energy.dal.mapper.ProjectStaDeviceElectricityDayMapper;
import com.landleaf.energy.dal.mapper.ProjectStaDeviceElectricityHourMapper;
import com.landleaf.energy.dal.mapper.ProjectStaDeviceElectricityMonthMapper;
import com.landleaf.energy.dal.mapper.ProjectSubitemDeviceMapper;
import com.landleaf.energy.domain.entity.ProjectStaDeviceElectricityDayEntity;
import com.landleaf.energy.domain.entity.ProjectStaDeviceElectricityHourEntity;
import com.landleaf.energy.domain.entity.ProjectStaDeviceElectricityMonthEntity;
import com.landleaf.energy.domain.entity.ProjectSubitemDeviceEntity;
import com.landleaf.energy.response.DeviceElectricityResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 实现
 *
 * @author yue lin
 * @since 2023/8/3 10:54
 */
@RestController
@RequiredArgsConstructor
public class DeviceElectricityApiImpl implements DeviceElectricityApi {

    private final ProjectSubitemDeviceMapper projectSubitemDeviceMapper;
    private final ProjectStaDeviceElectricityHourMapper projectStaDeviceElectricityHourMapper;
    private final ProjectStaDeviceElectricityDayMapper projectStaDeviceElectricityDayMapper;
    private final ProjectStaDeviceElectricityMonthMapper projectStaDeviceElectricityMonthMapper;
    private final DeviceIotApi deviceIotApi;

    @Override
    public Response<List<DeviceElectricityResponse>> searchChargingDayTotal(List<String> bizDeviceIds) {
        TenantContext.setIgnore(true);
        if (CollUtil.isEmpty(bizDeviceIds)) {
            return Response.success(List.of());
        }
        LocalDate now = LocalDate.now();
        List<ProjectSubitemDeviceEntity> deviceEntities = projectSubitemDeviceMapper.selectList(Wrappers.<ProjectSubitemDeviceEntity>lambdaQuery().in(ProjectSubitemDeviceEntity::getDeviceId, bizDeviceIds));
        Map<String, BigDecimal> collect = deviceEntities
                .stream()
                .collect(Collectors.toMap(ProjectSubitemDeviceEntity::getDeviceId, it -> new BigDecimal(it.getComputeTag())));

        Response<List<DeviceIoResponse>> listResponse = deviceIotApi.searchDeviceIot(bizDeviceIds);
        Assert.isTrue(listResponse.isSuccess(), () -> new ServiceException(listResponse.getErrorCode(), listResponse.getMessage()));
        Map<String, String> collectNameMap = listResponse.getResult()
                .stream()
                .collect(Collectors.toMap(DeviceIoResponse::getBizDeviceId, DeviceIoResponse::getDeviceName));

        Map<String, List<BigDecimal>> hourMap = projectStaDeviceElectricityHourMapper.selectList(
                        Wrappers.<ProjectStaDeviceElectricityHourEntity>lambdaQuery()
                                .in(ProjectStaDeviceElectricityHourEntity::getBizDeviceId, bizDeviceIds)
                                .eq(ProjectStaDeviceElectricityHourEntity::getYear, String.valueOf(now.getYear()))
                                .eq(ProjectStaDeviceElectricityHourEntity::getMonth, String.valueOf(now.getMonthValue()))
                                .eq(ProjectStaDeviceElectricityHourEntity::getDay, String.valueOf(now.getDayOfMonth())))
                .stream()
                .filter(it -> Objects.nonNull(it.getEnergymeterEpimportTotal()))
                .collect(Collectors.groupingBy(
                        ProjectStaDeviceElectricityHourEntity::getBizDeviceId,
                        Collectors.mapping(ProjectStaDeviceElectricityHourEntity::getEnergymeterEpimportTotal, Collectors.toList())
                ));

        List<DeviceElectricityResponse> responseList = bizDeviceIds.stream()
                .map(it -> {
                    DeviceElectricityResponse response = new DeviceElectricityResponse();
                    BigDecimal reduce = hourMap.getOrDefault(it, List.of())
                            .stream()
                            .filter(Objects::nonNull)
                            .map(m -> m.multiply(collect.getOrDefault(it, BigDecimal.ZERO)))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    response.setBizDeviceId(it);
                    response.setDeviceName(collectNameMap.getOrDefault(it, ""));
                    response.setEpimportTotal(reduce);
                    return response;
                }).toList();
        return Response.success(responseList);
    }

    @Override
    public Response<List<DeviceElectricityResponse>> searchChargingYearTotal(List<String> bizDeviceIds) {
        TenantContext.setIgnore(true);
        if (CollUtil.isEmpty(bizDeviceIds)) {
            return Response.success(List.of());
        }
        LocalDate now = LocalDate.now();
        List<ProjectSubitemDeviceEntity> deviceEntities = projectSubitemDeviceMapper.selectList(Wrappers.<ProjectSubitemDeviceEntity>lambdaQuery().in(ProjectSubitemDeviceEntity::getDeviceId, bizDeviceIds));
        Map<String, BigDecimal> collect = deviceEntities
                .stream()
                .collect(Collectors.toMap(ProjectSubitemDeviceEntity::getDeviceId, it -> new BigDecimal(it.getComputeTag())));

        Response<List<DeviceIoResponse>> listResponse = deviceIotApi.searchDeviceIot(bizDeviceIds);
        Assert.isTrue(listResponse.isSuccess(), () -> new ServiceException(listResponse.getErrorCode(), listResponse.getMessage()));
        Map<String, String> collectNameMap = listResponse.getResult()
                .stream()
                .collect(Collectors.toMap(DeviceIoResponse::getBizDeviceId, DeviceIoResponse::getDeviceName));

        Map<String, List<BigDecimal>> hourMap = projectStaDeviceElectricityHourMapper.selectList(
                        Wrappers.<ProjectStaDeviceElectricityHourEntity>lambdaQuery()
                                .in(ProjectStaDeviceElectricityHourEntity::getBizDeviceId, bizDeviceIds)
                                .eq(ProjectStaDeviceElectricityHourEntity::getYear, String.valueOf(now.getYear()))
                                .eq(ProjectStaDeviceElectricityHourEntity::getMonth, String.valueOf(now.getMonthValue()))
                                .eq(ProjectStaDeviceElectricityHourEntity::getDay, String.valueOf(now.getDayOfMonth())))
                .stream()
                .filter(it -> Objects.nonNull(it.getEnergymeterEpimportTotal()))
                .collect(Collectors.groupingBy(
                        ProjectStaDeviceElectricityHourEntity::getBizDeviceId,
                        Collectors.mapping(ProjectStaDeviceElectricityHourEntity::getEnergymeterEpimportTotal, Collectors.toList())
                ));
        Map<String, List<BigDecimal>> dayMap = projectStaDeviceElectricityDayMapper.selectList(
                        Wrappers.<ProjectStaDeviceElectricityDayEntity>lambdaQuery()
                                .in(ProjectStaDeviceElectricityDayEntity::getBizDeviceId, bizDeviceIds)
                                .eq(ProjectStaDeviceElectricityDayEntity::getYear, String.valueOf(now.getYear()))
                                .eq(ProjectStaDeviceElectricityDayEntity::getMonth, String.valueOf(now.getMonthValue())))
                .stream()
                .filter(it -> Objects.nonNull(it.getEnergymeterEpimportTotal()))
                .collect(Collectors.groupingBy(
                        ProjectStaDeviceElectricityDayEntity::getBizDeviceId,
                        Collectors.mapping(ProjectStaDeviceElectricityDayEntity::getEnergymeterEpimportTotal, Collectors.toList())
                ));
        Map<String, List<BigDecimal>> monthMap = projectStaDeviceElectricityMonthMapper.selectList(
                        Wrappers.<ProjectStaDeviceElectricityMonthEntity>lambdaQuery()
                                .in(ProjectStaDeviceElectricityMonthEntity::getBizDeviceId, bizDeviceIds)
                                .eq(ProjectStaDeviceElectricityMonthEntity::getYear, String.valueOf(now.getYear())))
                .stream()
                .filter(it -> Objects.nonNull(it.getEnergymeterEpimportTotal()))
                .collect(Collectors.groupingBy(
                        ProjectStaDeviceElectricityMonthEntity::getBizDeviceId,
                        Collectors.mapping(ProjectStaDeviceElectricityMonthEntity::getEnergymeterEpimportTotal, Collectors.toList())
                ));
        List<DeviceElectricityResponse> responseList = bizDeviceIds.stream()
                .map(it -> {
                    DeviceElectricityResponse response = new DeviceElectricityResponse();
                    BigDecimal reduce = hourMap.getOrDefault(it, List.of())
                            .stream()
                            .filter(Objects::nonNull)
                            .map(m -> m.multiply(collect.getOrDefault(it, BigDecimal.ZERO)))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal reduce1 = dayMap.getOrDefault(it, List.of())
                            .stream()
                            .filter(Objects::nonNull)
                            .map(m -> m.multiply(collect.getOrDefault(it, BigDecimal.ZERO)))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal reduce2 = monthMap.getOrDefault(it, List.of())
                            .stream()
                            .filter(Objects::nonNull)
                            .map(m -> m.multiply(collect.getOrDefault(it, BigDecimal.ZERO)))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    response.setBizDeviceId(it);
                    response.setDeviceName(collectNameMap.getOrDefault(it, ""));
                    response.setEpimportTotal(reduce.add(reduce1).add(reduce2));
                    return response;
                }).toList();
        return Response.success(responseList);
    }
}
