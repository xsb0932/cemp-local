package com.landleaf.bms.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.text.CharSequenceUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.landleaf.bms.dal.mapper.DeviceAttributeMapper;
import com.landleaf.bms.domain.entity.DeviceAttributeEntity;
import com.landleaf.bms.api.json.ValueDescription;
import com.landleaf.bms.domain.request.DeviceAttributeChangeRequest;
import com.landleaf.bms.domain.request.FeatureQueryRequest;
import com.landleaf.bms.domain.request.ValueDescriptionParam;
import com.landleaf.bms.domain.response.DeviceAttributeTabulationResponse;
import com.landleaf.bms.service.DeviceAttributeService;
import com.landleaf.bms.service.FeatureManagementService;
import com.landleaf.comm.exception.ServiceException;
import com.landleaf.redis.dict.DictUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.landleaf.bms.domain.enums.ErrorCodeConstants.IDENTIFIER_NOT_UNIQUE;

/**
 * 设备属性业务实现
 *
 * @author yue lin
 * @since 2023/6/25 15:13
 */
@Service
@RequiredArgsConstructor
public class DeviceAttributeServiceImpl implements DeviceAttributeService {

    private final DeviceAttributeMapper deviceAttributeMapper;
    private final FeatureManagementService featureManagementService;
    private final DictUtils dictUtils;

    @Override
    public Page<DeviceAttributeTabulationResponse> searchDeviceAttributeTabulation(FeatureQueryRequest request) {
        Page<DeviceAttributeTabulationResponse> page = Page.of(request.getPageNo(), request.getPageSize());
        return (Page<DeviceAttributeTabulationResponse>) deviceAttributeMapper.searchDeviceAttributeTabulation(page, request)
                .convert(DeviceAttributeTabulationResponse::fill);
    }

    @Override
    public void deleteDeviceAttribute(Long id) {
        Assert.notNull(id, "参数异常");
        DeviceAttributeEntity deviceAttribute = deviceAttributeMapper.selectById(id);
        Assert.notNull(deviceAttribute, "目标不存在");
        deviceAttributeMapper.deleteById(id);
    }

    @Override
    public Long createDeviceAttribute(DeviceAttributeChangeRequest.Create request) {
        Assert.isTrue(featureManagementService.checkIdentifierUnique(request.getIdentifier()),
                () -> new ServiceException(IDENTIFIER_NOT_UNIQUE));
        DeviceAttributeEntity deviceAttributeEntity = request.toEntity();
        deviceAttributeMapper.insert(deviceAttributeEntity);
        return deviceAttributeEntity.getId();
    }

    @Override
    public Long updateDeviceAttribute(DeviceAttributeChangeRequest.Update request) {
        Long id = request.getId();
        DeviceAttributeEntity entity = deviceAttributeMapper.selectById(id);
        Assert.notNull(entity, "目标不存在");
        request.mergeEntity(entity);
        // 校验
        Assert.isTrue(CharSequenceUtil.equals(entity.getDataType(), request.getDataType()), "数据类型无法变更");
        Assert.isTrue(CollUtil.containsAll(request.getValueDescription().stream().map(ValueDescriptionParam.ValueAccount::getKey).toList(),
                entity.getValueDescription().stream().map(ValueDescription::getKey).toList()), "值描述Key无法变更");
        deviceAttributeMapper.updateById(entity);
        return entity.getId();
    }
}
