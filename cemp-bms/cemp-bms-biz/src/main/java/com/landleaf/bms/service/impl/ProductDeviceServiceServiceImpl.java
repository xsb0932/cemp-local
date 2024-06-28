package com.landleaf.bms.service.impl;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.landleaf.bms.dal.mapper.ProductDeviceServiceMapper;
import com.landleaf.bms.dal.mapper.ProductMapper;
import com.landleaf.bms.domain.entity.ProductDeviceServiceEntity;
import com.landleaf.bms.domain.entity.ProductEntity;
import com.landleaf.bms.domain.request.CustomDeviceServiceAddRequest;
import com.landleaf.bms.domain.request.ProductDeviceServiceEditRequest;
import com.landleaf.bms.domain.request.ProductFeatureQueryRequest;
import com.landleaf.bms.api.dto.ProductDeviceServiceListResponse;
import com.landleaf.bms.service.ProductDeviceServiceService;
import com.landleaf.bms.service.ProductFeatureManagementService;
import com.landleaf.bms.util.FunctionParameterUtil;
import com.landleaf.comm.exception.ServiceException;
import com.landleaf.comm.tenant.TenantContext;
import com.landleaf.redis.constance.DictConstance;
import com.landleaf.redis.dict.DictUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.landleaf.bms.domain.enums.ErrorCodeConstants.IDENTIFIER_NOT_UNIQUE;

/**
 * ProductDeviceServiceServiceImpl
 *
 * @author 张力方
 * @since 2023/7/5
 **/
@Service
@RequiredArgsConstructor
public class ProductDeviceServiceServiceImpl implements ProductDeviceServiceService {
    private final ProductMapper productMapper;
    private final ProductDeviceServiceMapper productDeviceServiceMapper;
    private final ProductFeatureManagementService productFeatureManagementService;
    private final DictUtils dictUtils;

    @Override
    public void addCustomDeviceService(CustomDeviceServiceAddRequest request) {
        TenantContext.setIgnore(true);
        // 校验功能标识符是否唯一
        Assert.isTrue(productFeatureManagementService.checkIdentifierUnique(request.getIdentifier(), request.getProductId()),
                () -> new ServiceException(IDENTIFIER_NOT_UNIQUE));
        // 设备服务所属租户与产品一致
        ProductEntity productEntity = productMapper.selectById(request.getProductId());
        ProductDeviceServiceEntity productDeviceServiceEntity = request.toEntity();
        productDeviceServiceEntity.setTenantId(productEntity.getTenantId());
        productDeviceServiceMapper.insert(productDeviceServiceEntity);
    }

    @Override
    public void editDeviceService(ProductDeviceServiceEditRequest request) {
        TenantContext.setIgnore(true);
        Long id = request.getId();
        ProductDeviceServiceEntity entity = productDeviceServiceMapper.selectById(id);
        Assert.notNull(entity, "目标不存在");
        request.mergeEntity(entity);
        productDeviceServiceMapper.updateById(entity);
    }

    @Override
    public void deleteDeviceService(Long id) {
        TenantContext.setIgnore(true);
        Assert.notNull(id, "参数异常");
        ProductDeviceServiceEntity entity = productDeviceServiceMapper.selectById(id);
        Assert.notNull(entity, "目标不存在");
        productDeviceServiceMapper.deleteById(id);
    }

    @Override
    public Page<ProductDeviceServiceListResponse> pageQueryDeviceService(ProductFeatureQueryRequest request) {
        TenantContext.setIgnore(true);
        return (Page<ProductDeviceServiceListResponse>) productDeviceServiceMapper.pageQuery(Page.of(request.getPageNo(), request.getPageSize()), request)
                .convert(it -> {
                    it.setFunctionTypeContent(dictUtils.selectDictLabel(DictConstance.PRODUCT_FUNCTION_TYPE, it.getFunctionType()));
                    it.setFunctionParameterContent(FunctionParameterUtil.convertToString(it.getFunctionParameter()));
                    it.setResponseParameterContent(FunctionParameterUtil.convertToString(it.getResponseParameter()));
                    return it;
                });
    }
}
