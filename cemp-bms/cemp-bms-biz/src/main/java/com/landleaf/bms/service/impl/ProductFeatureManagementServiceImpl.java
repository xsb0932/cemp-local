package com.landleaf.bms.service.impl;

import cn.hutool.core.lang.Assert;
import com.landleaf.bms.dal.mapper.*;
import com.landleaf.bms.service.ProductFeatureManagementService;
import com.landleaf.comm.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 产品功能管理业务实现
 *
 * @author yue lin
 * @since 2023/6/26 9:42
 */
@Service
@RequiredArgsConstructor
public class ProductFeatureManagementServiceImpl implements ProductFeatureManagementService {

    private final ProductDeviceAttributeMapper productDeviceAttributeMapper;
    private final ProductDeviceEventMapper productDeviceEventMapper;
    private final ProductDeviceParameterMapper productDeviceParameterMapper;
    private final ProductDeviceServiceMapper productDeviceServiceMapper;
    private final ProductProductParameterMapper productproductParameterMapper;

    @Override
    public boolean checkIdentifierUnique(String identifier, Long id, Long productId) {
        TenantContext.setIgnore(true);
        Assert.notBlank(identifier, "标识符不能为空");
        boolean existsIdentifier = productDeviceAttributeMapper.existsIdentifier(identifier, id, productId) ||
                productDeviceEventMapper.existsIdentifier(identifier, id, productId) ||
                productDeviceParameterMapper.existsIdentifier(identifier, id, productId) ||
                productDeviceServiceMapper.existsIdentifier(identifier, id, productId) ||
                productproductParameterMapper.existsIdentifier(identifier, id, productId);
        return !existsIdentifier;
    }

    @Override
    public boolean checkIdentifierUnique(String identifier, Long productId) {
        return checkIdentifierUnique(identifier, null, productId);
    }

}
