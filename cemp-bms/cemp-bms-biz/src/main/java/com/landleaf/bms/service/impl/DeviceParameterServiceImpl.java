package com.landleaf.bms.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.text.CharSequenceUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.landleaf.bms.dal.mapper.DeviceParameterMapper;
import com.landleaf.bms.domain.entity.DeviceParameterEntity;
import com.landleaf.bms.api.json.ValueDescription;
import com.landleaf.bms.domain.request.DeviceParameterChangeRequest;
import com.landleaf.bms.domain.request.FeatureQueryRequest;
import com.landleaf.bms.domain.request.ValueDescriptionParam;
import com.landleaf.bms.domain.response.DeviceAttributeTabulationResponse;
import com.landleaf.bms.domain.response.DeviceParameterTabulationResponse;
import com.landleaf.bms.service.DeviceParameterService;
import com.landleaf.bms.service.FeatureManagementService;
import com.landleaf.comm.exception.ServiceException;
import com.landleaf.redis.dict.DictUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.landleaf.bms.domain.enums.ErrorCodeConstants.IDENTIFIER_NOT_UNIQUE;

/**
 * 设备参数业务实现
 *
 * @author yue lin
 * @since 2023/6/25 15:13
 */
@Service
@RequiredArgsConstructor
public class DeviceParameterServiceImpl implements DeviceParameterService {

    private final DeviceParameterMapper deviceParameterMapper;
    private final FeatureManagementService featureManagementService;
    private final DictUtils dictUtils;

    @Override
    public Page<DeviceParameterTabulationResponse> searchDeviceParameterTabulation(FeatureQueryRequest request) {
        Page<DeviceAttributeTabulationResponse> page = Page.of(request.getPageNo(), request.getPageSize());
        return (Page<DeviceParameterTabulationResponse>) deviceParameterMapper.searchDeviceParameterTabulation(page, request)
                .convert(DeviceParameterTabulationResponse::fill);
    }

    @Override
    public void deleteDeviceParameter(Long id) {
        Assert.notNull(id, "参数异常");
        DeviceParameterEntity deviceParameter = deviceParameterMapper.selectById(id);
        Assert.notNull(deviceParameter, "目标不存在");
        deviceParameterMapper.deleteById(id);
    }

    @Override
    public Long createDeviceParameter(DeviceParameterChangeRequest.Create request) {
        DeviceParameterEntity deviceParameter = request.toEntity();
        Assert.isTrue(featureManagementService.checkIdentifierUnique(request.getIdentifier()),
                () -> new ServiceException(IDENTIFIER_NOT_UNIQUE));
        deviceParameterMapper.insert(deviceParameter);
        return deviceParameter.getId();
    }

    @Override
    public Long updateDeviceParameter(DeviceParameterChangeRequest.Update request) {
        Long id = request.getId();
        DeviceParameterEntity entity = deviceParameterMapper.selectById(id);
        Assert.notNull(entity, "目标不存在");
        request.mergeEntity(entity);
        // 校验
        Assert.isTrue(CharSequenceUtil.equals(entity.getDataType(), request.getDataType()), "数据类型无法变更");
        Assert.isTrue(CollUtil.containsAll(request.getValueDescription().stream().map(ValueDescriptionParam.ValueAccount::getKey).toList(),
                entity.getValueDescription().stream().map(ValueDescription::getKey).toList()), "值描述Key无法变更");
        deviceParameterMapper.updateById(entity);
        return entity.getId();
    }
}
