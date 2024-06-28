package com.landleaf.bms.service.impl;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.landleaf.bms.dal.mapper.ProductDeviceEventMapper;
import com.landleaf.bms.dal.mapper.ProductMapper;
import com.landleaf.bms.domain.entity.ProductDeviceEventEntity;
import com.landleaf.bms.domain.entity.ProductEntity;
import com.landleaf.bms.domain.request.CustomDeviceEventAddRequest;
import com.landleaf.bms.domain.request.ProductDeviceEventEditRequest;
import com.landleaf.bms.domain.request.ProductFeatureQueryRequest;
import com.landleaf.bms.api.dto.ProductDeviceEventListResponse;
import com.landleaf.bms.service.ProductDeviceEventService;
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
 * ProductDeviceEventServiceImpl
 *
 * @author 张力方
 * @since 2023/7/5
 **/
@Service
@RequiredArgsConstructor
public class ProductDeviceEventServiceImpl implements ProductDeviceEventService {
    private final ProductMapper productMapper;
    private final ProductDeviceEventMapper productDeviceEventMapper;
    private final ProductFeatureManagementService productFeatureManagementService;
    private final DictUtils dictUtils;

    @Override
    public void addCustomDeviceEvent(CustomDeviceEventAddRequest request) {
        TenantContext.setIgnore(true);
        // 校验功能标识符是否唯一
        Assert.isTrue(productFeatureManagementService.checkIdentifierUnique(request.getIdentifier(), request.getProductId()),
                () -> new ServiceException(IDENTIFIER_NOT_UNIQUE));
        // 设备事件所属租户与产品一致
        ProductEntity productEntity = productMapper.selectById(request.getProductId());
        ProductDeviceEventEntity productDeviceEventEntity = request.toEntity();
        productDeviceEventEntity.setTenantId(productEntity.getTenantId());
        productDeviceEventMapper.insert(productDeviceEventEntity);
    }

    @Override
    public void editDeviceEvent(ProductDeviceEventEditRequest request) {
        TenantContext.setIgnore(true);
        Long id = request.getId();
        ProductDeviceEventEntity entity = productDeviceEventMapper.selectById(id);
        Assert.notNull(entity, "目标不存在");
        request.mergeEntity(entity);
        productDeviceEventMapper.updateById(entity);
    }

    @Override
    public void deleteDeviceEvent(Long id) {
        TenantContext.setIgnore(true);
        Assert.notNull(id, "参数异常");
        ProductDeviceEventEntity entity = productDeviceEventMapper.selectById(id);
        Assert.notNull(entity, "目标不存在");
        productDeviceEventMapper.deleteById(id);
    }

    @Override
    public Page<ProductDeviceEventListResponse> pageQueryDeviceEvent(ProductFeatureQueryRequest request) {
        TenantContext.setIgnore(true);
        return (Page<ProductDeviceEventListResponse>) productDeviceEventMapper.pageQuery(Page.of(request.getPageNo(), request.getPageSize()), request)
                .convert(it -> {
                    it.setFunctionTypeContent(dictUtils.selectDictLabel(DictConstance.PRODUCT_FUNCTION_TYPE, it.getFunctionType()));
                    it.setEventParameterContent(FunctionParameterUtil.convertToString(it.getEventParameter()));
                    it.setResponseParameterContent(FunctionParameterUtil.convertToString(it.getResponseParameter()));
                    return it;
                });
    }
}
