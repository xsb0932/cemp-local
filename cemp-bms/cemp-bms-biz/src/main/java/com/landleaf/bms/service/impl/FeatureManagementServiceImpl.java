package com.landleaf.bms.service.impl;

import cn.hutool.core.lang.Assert;
import com.landleaf.bms.dal.mapper.*;
import com.landleaf.bms.service.FeatureManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 功能管理业务实现
 *
 * @author yue lin
 * @since 2023/6/26 9:42
 */
@Service
@RequiredArgsConstructor
public class FeatureManagementServiceImpl implements FeatureManagementService {

    private final DeviceAttributeMapper deviceAttributeMapper;
    private final DeviceEventMapper deviceEventMapper;
    private final DeviceParameterMapper deviceParameterMapper;
    private final DeviceServiceMapper deviceServiceMapper;
    private final ProductParameterMapper productParameterMapper;

    @Override
    public boolean checkIdentifierUnique(String identifier, Long id) {
        Assert.notBlank(identifier, "标识符不能为空");
        boolean existsIdentifier = deviceAttributeMapper.existsIdentifier(identifier, id) ||
                deviceEventMapper.existsIdentifier(identifier, id) ||
                deviceParameterMapper.existsIdentifier(identifier, id) ||
                deviceServiceMapper.existsIdentifier(identifier, id) ||
                productParameterMapper.existsIdentifier(identifier, id);
        return !existsIdentifier;
    }

    @Override
    public boolean checkIdentifierUnique(String identifier) {
        return checkIdentifierUnique(identifier, null);
    }

}
