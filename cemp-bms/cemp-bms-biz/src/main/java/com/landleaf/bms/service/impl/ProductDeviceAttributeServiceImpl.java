package com.landleaf.bms.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.text.CharSequenceUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.landleaf.bms.dal.mapper.ProductDeviceAttributeMapper;
import com.landleaf.bms.dal.mapper.ProductMapper;
import com.landleaf.bms.domain.entity.ProductDeviceAttributeEntity;
import com.landleaf.bms.domain.entity.ProductEntity;
import com.landleaf.bms.api.json.ValueDescription;
import com.landleaf.bms.domain.request.CustomDeviceAttributeAddRequest;
import com.landleaf.bms.domain.request.ProductDeviceAttributeEditRequest;
import com.landleaf.bms.domain.request.ProductFeatureQueryRequest;
import com.landleaf.bms.domain.request.ValueDescriptionParam;
import com.landleaf.bms.api.dto.ProductDeviceAttributeListResponse;
import com.landleaf.bms.service.ProductDeviceAttributeService;
import com.landleaf.bms.service.ProductFeatureManagementService;
import com.landleaf.bms.util.ValueDescriptionUtil;
import com.landleaf.comm.exception.ServiceException;
import com.landleaf.comm.tenant.TenantContext;
import com.landleaf.redis.constance.DictConstance;
import com.landleaf.redis.dict.DictUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.landleaf.bms.domain.enums.ErrorCodeConstants.IDENTIFIER_NOT_UNIQUE;

/**
 * ProductDeviceAttributeServiceImpl
 *
 * @author 张力方
 * @since 2023/7/5
 **/
@Service
@RequiredArgsConstructor
public class ProductDeviceAttributeServiceImpl implements ProductDeviceAttributeService {
    private final ProductMapper productMapper;
    private final ProductDeviceAttributeMapper productDeviceAttributeMapper;
    private final ProductFeatureManagementService productFeatureManagementService;
    private final DictUtils dictUtils;

    @Override
    public void addCustomDeviceAttribute(CustomDeviceAttributeAddRequest request) {
        TenantContext.setIgnore(true);
        // 校验功能标识符是否唯一
        Assert.isTrue(productFeatureManagementService.checkIdentifierUnique(request.getIdentifier(), request.getProductId()),
                () -> new ServiceException(IDENTIFIER_NOT_UNIQUE));
        // 设备属性所属租户与产品一致
        ProductEntity productEntity = productMapper.selectById(request.getProductId());
        ProductDeviceAttributeEntity productDeviceAttributeEntity = request.toEntity();
        productDeviceAttributeEntity.setTenantId(productEntity.getTenantId());
        productDeviceAttributeMapper.insert(productDeviceAttributeEntity);
    }

    @Override
    public void editDeviceAttribute(ProductDeviceAttributeEditRequest request) {
        TenantContext.setIgnore(true);
        Long id = request.getId();
        ProductDeviceAttributeEntity entity = productDeviceAttributeMapper.selectById(id);
        Assert.notNull(entity, "目标不存在");
        request.mergeEntity(entity);
        // 校验
        Assert.isTrue(CharSequenceUtil.equals(entity.getDataType(), request.getDataType()), "数据类型无法变更");
        Assert.isTrue(CollUtil.containsAll(request.getValueDescription().stream().map(ValueDescriptionParam.ValueAccount::getKey).toList(),
                entity.getValueDescription().stream().map(ValueDescription::getKey).toList()), "值描述Key无法变更");
        productDeviceAttributeMapper.updateById(entity);
    }

    @Override
    public void deleteDeviceAttribute(Long id) {
        TenantContext.setIgnore(true);
        Assert.notNull(id, "参数异常");
        ProductDeviceAttributeEntity entity = productDeviceAttributeMapper.selectById(id);
        Assert.notNull(entity, "目标不存在");
        productDeviceAttributeMapper.deleteById(id);
    }

    @Override
    public Page<ProductDeviceAttributeListResponse> pageQueryDeviceAttribute(ProductFeatureQueryRequest request) {
        TenantContext.setIgnore(true);
        return (Page<ProductDeviceAttributeListResponse>) productDeviceAttributeMapper.pageQuery(
                        Page.of(request.getPageNo(), request.getPageSize()),
                        request)
                .convert(it -> {
                    it.setFunctionTypeContent(dictUtils.selectDictLabel(DictConstance.PRODUCT_FUNCTION_TYPE, it.getFunctionType()));
                    it.setDataTypeContent(dictUtils.selectDictLabel(DictConstance.PARAM_DATA_TYPE, it.getDataType()));
                    it.setValueDescriptionContent(ValueDescriptionUtil.convertToString(it.getDataType(), it.getValueDescription()));
                    it.setUnit(ValueDescriptionUtil.unitToString(it.getValueDescription()));
                    it.setRwContent(dictUtils.selectDictLabel(DictConstance.RW_TYPE, it.getRw()));
                    return it;
                });
    }
}
