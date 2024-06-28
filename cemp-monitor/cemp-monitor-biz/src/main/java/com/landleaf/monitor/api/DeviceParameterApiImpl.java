package com.landleaf.monitor.api;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.landleaf.bms.api.CategoryApi;
import com.landleaf.bms.api.UserProjectApi;
import com.landleaf.bms.api.dto.UserProjectDTO;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.comm.constance.DeviceStaCategoryEnum;
import com.landleaf.comm.tenant.TenantContext;
import com.landleaf.monitor.api.dto.MeterDeviceDTO;
import com.landleaf.monitor.api.request.MeterDeviceRequest;
import com.landleaf.monitor.dal.mapper.DeviceMonitorMapper;
import com.landleaf.monitor.dal.mapper.DeviceParameterMapper;
import com.landleaf.monitor.domain.entity.DeviceMonitorEntity;
import com.landleaf.monitor.domain.entity.DeviceParameterEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 设备参数Api
 *
 * @author Tycoon
 * @since 2023/8/18 10:19
 **/
@RestController
@RequiredArgsConstructor
public class DeviceParameterApiImpl implements DeviceParameterApi {

    private final DeviceParameterMapper deviceParameterMapper;
    private final UserProjectApi userProjectApi;
    private final CategoryApi categoryApi;
    private final DeviceMonitorMapper deviceMonitorMapper;

    @Override
    public Response<String> searchDeviceParameterValue(String bizDeviceId, String identifier) {
        String value = Optional.ofNullable(deviceParameterMapper.searchDeviceParameter(bizDeviceId, identifier))
                .map(DeviceParameterEntity::getValue)
                .orElse("");
        return Response.success(value);
    }

    @Override
    public Response<Map<String, BigDecimal>> getDevicesMultiplyingFactor(List<String> bizDeviceIdList) {
        Map<String, BigDecimal> result = CollUtil.isEmpty(bizDeviceIdList) ? new HashMap<>() : deviceParameterMapper.selectList(
                        new LambdaQueryWrapper<DeviceParameterEntity>()
                                .in(DeviceParameterEntity::getBizDeviceId, bizDeviceIdList)
                                .eq(DeviceParameterEntity::getIdentifier, "multiplyingFactor")
                ).stream()
                .collect(Collectors.toMap(DeviceParameterEntity::getBizDeviceId, o -> new BigDecimal(o.getValue()), (o1, o2) -> o1));
        return Response.success(result);
    }

    @Override
    public Response<List<String>> searchDeviceBizIds(Long tenantId, String meterRead, String meterReadCycle, Long userId) {
        TenantContext.setIgnore(false);
        TenantContext.setTenantId(tenantId);
        // 这个bug不想大改。。。尽量兼容不动的情况下，就酱吧，先查有权限的项目，再查项目下所有电表，再过滤属性
        List<String> projectIds = userProjectApi.getUserProjectList(userId)
                .getCheckedData()
                .stream()
                .map(UserProjectDTO::getBizProjectId)
                .toList();
        List<String> deviceIds = new ArrayList<>();
        String bizCategoryId = categoryApi.getBizCategoryId(DeviceStaCategoryEnum.DB3PH.getCode()).getCheckedData();

        if (!projectIds.isEmpty()) {
            deviceIds = deviceMonitorMapper.selectList(new LambdaQueryWrapper<DeviceMonitorEntity>()
                            .select(DeviceMonitorEntity::getBizDeviceId)
                            .eq(DeviceMonitorEntity::getBizCategoryId, bizCategoryId)
                            .in(DeviceMonitorEntity::getBizProjectId, projectIds))
                    .stream()
                    .map(DeviceMonitorEntity::getBizDeviceId)
                    .toList();
        }
        if (deviceIds.isEmpty()) {
            return Response.success(deviceIds);
        }
        List<String> meterReadList = deviceParameterMapper.selectList(
                        Wrappers.<DeviceParameterEntity>lambdaQuery()
                                .in(DeviceParameterEntity::getBizDeviceId, deviceIds)
                                .eq(DeviceParameterEntity::getIdentifier, "meterRead")
                                .eq(DeviceParameterEntity::getValue, meterRead)
                ).stream()
                .map(DeviceParameterEntity::getBizDeviceId)
                .distinct()
                .toList();
        List<String> meterReadCycleList = deviceParameterMapper.selectList(
                        Wrappers.<DeviceParameterEntity>lambdaQuery()
                                .in(DeviceParameterEntity::getBizDeviceId, deviceIds)
                                .eq(DeviceParameterEntity::getIdentifier, "meterReadCycle")
                                .in(DeviceParameterEntity::getValue, "0", meterReadCycle)
                ).stream()
                .map(DeviceParameterEntity::getBizDeviceId)
                .distinct()
                .toList();
        return Response.success(List.copyOf(CollUtil.intersection(meterReadList, meterReadCycleList)));
    }

    @Override
    public Response<List<MeterDeviceDTO>> getDeviceByProjectCategoryParameter(MeterDeviceRequest request) {
        TenantContext.setIgnore(true);
        try {
            List<MeterDeviceDTO> result = deviceParameterMapper.getDeviceByProjectCategoryParameter(
                    request.getBizProjectIds(),
                    request.getBizCategoryId(),
                    request.getMeterRead(),
                    request.getMeterReadCycle()
            );
            return Response.success(result);
        } finally {
            TenantContext.setIgnore(false);
        }
    }

}
