package com.landleaf.bms.service.impl;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.landleaf.bms.dal.mapper.DeviceServiceMapper;
import com.landleaf.bms.domain.entity.DeviceServiceEntity;
import com.landleaf.bms.domain.request.DeviceServiceChangeRequest;
import com.landleaf.bms.domain.request.FeatureQueryRequest;
import com.landleaf.bms.domain.response.DeviceServiceTabulationResponse;
import com.landleaf.bms.service.DeviceServiceService;
import com.landleaf.bms.service.FeatureManagementService;
import com.landleaf.comm.exception.ServiceException;
import com.landleaf.redis.dict.DictUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.landleaf.bms.domain.enums.ErrorCodeConstants.IDENTIFIER_NOT_UNIQUE;

/**
 * 设备服务业务
 *
 * @author yue lin
 * @since 2023/6/27 14:20
 */
@Service
@RequiredArgsConstructor
public class DeviceServiceServiceImpl implements DeviceServiceService {

    private final DeviceServiceMapper deviceServiceMapper;
    private final FeatureManagementService featureManagementService;
    private final DictUtils dictUtils;

    @Override
    public Page<DeviceServiceTabulationResponse> searchDeviceServiceTabulation(FeatureQueryRequest request) {
        return (Page<DeviceServiceTabulationResponse>) deviceServiceMapper.searchDeviceServiceTabulation(Page.of(request.getPageNo(), request.getPageSize()), request)
                .convert(DeviceServiceTabulationResponse::fill);
    }

    @Override
    public void deleteDeviceService(Long id) {
        Assert.notNull(id, "参数异常");
        DeviceServiceEntity entity = deviceServiceMapper.selectById(id);
        Assert.notNull(entity, "目标不存在");
        deviceServiceMapper.deleteById(id);
    }

    @Override
    public Long createDeviceService(DeviceServiceChangeRequest.Create request) {
        Assert.isTrue(featureManagementService.checkIdentifierUnique(request.getIdentifier()),
                () -> new ServiceException(IDENTIFIER_NOT_UNIQUE));
        DeviceServiceEntity entity = request.toEntity();
        deviceServiceMapper.insert(entity);
        return entity.getId();
    }

    @Override
    public Long updateDeviceService(DeviceServiceChangeRequest.Update request) {
        request.validate();
        Long id = request.getId();
        DeviceServiceEntity entity = deviceServiceMapper.selectById(id);
        Assert.notNull(entity, "目标不存在");
        request.mergeEntity(entity);
        deviceServiceMapper.updateById(entity);
        return entity.getId();
    }
}
