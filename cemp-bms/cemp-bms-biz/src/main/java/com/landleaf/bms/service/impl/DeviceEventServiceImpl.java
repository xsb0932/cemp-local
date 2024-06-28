package com.landleaf.bms.service.impl;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.landleaf.bms.dal.mapper.DeviceEventMapper;
import com.landleaf.bms.domain.entity.DeviceEventEntity;
import com.landleaf.bms.domain.request.DeviceEventChangeRequest;
import com.landleaf.bms.domain.request.FeatureQueryRequest;
import com.landleaf.bms.domain.response.DeviceEventTabulationResponse;
import com.landleaf.bms.service.DeviceEventService;
import com.landleaf.bms.service.FeatureManagementService;
import com.landleaf.comm.exception.ServiceException;
import com.landleaf.redis.dict.DictUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.landleaf.bms.domain.enums.ErrorCodeConstants.IDENTIFIER_NOT_UNIQUE;

/**
 * 设备事件业务
 *
 * @author yue lin
 * @since 2023/6/27 14:21
 */
@Service
@RequiredArgsConstructor
public class DeviceEventServiceImpl implements DeviceEventService {

    private final DeviceEventMapper deviceEventMapper;
    private final FeatureManagementService featureManagementService;
    private final DictUtils dictUtils;

    @Override
    public Page<DeviceEventTabulationResponse> searchDeviceEventTabulation(FeatureQueryRequest request) {
        return (Page<DeviceEventTabulationResponse>) deviceEventMapper.searchDeviceEventTabulation(Page.of(request.getPageNo(), request.getPageSize()), request)
                .convert(DeviceEventTabulationResponse::fill);
    }

    @Override
    public void deleteDeviceEvent(Long id) {
        Assert.notNull(id, "参数异常");
        DeviceEventEntity entity = deviceEventMapper.selectById(id);
        Assert.notNull(entity, "目标不存在");
        deviceEventMapper.deleteById(id);
    }

    @Override
    public Long createDeviceEvent(DeviceEventChangeRequest.Create request) {
        Assert.isTrue(featureManagementService.checkIdentifierUnique(request.getIdentifier()),
                () -> new ServiceException(IDENTIFIER_NOT_UNIQUE));
        DeviceEventEntity entity = request.toEntity();
        deviceEventMapper.insert(entity);
        return entity.getId();
    }

    @Override
    public Long updateDeviceEvent(DeviceEventChangeRequest.Update request) {
        request.validate();
        Long id = request.getId();
        DeviceEventEntity entity = deviceEventMapper.selectById(id);
        Assert.notNull(entity, "目标不存在");
        request.mergeEntity(entity);
        deviceEventMapper.updateById(entity);
        return entity.getId();
    }
}

