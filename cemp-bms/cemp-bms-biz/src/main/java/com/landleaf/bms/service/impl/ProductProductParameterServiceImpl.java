package com.landleaf.bms.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.text.CharSequenceUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.landleaf.bms.dal.mapper.ProductMapper;
import com.landleaf.bms.dal.mapper.ProductProductParameterMapper;
import com.landleaf.bms.domain.entity.ProductEntity;
import com.landleaf.bms.domain.entity.ProductProductParameterEntity;
import com.landleaf.bms.domain.enums.FunctionTypeEnum;
import com.landleaf.bms.api.json.ValueDescription;
import com.landleaf.bms.domain.request.*;
import com.landleaf.bms.domain.response.ProductProductParameterListResponse;
import com.landleaf.bms.service.ProductFeatureManagementService;
import com.landleaf.bms.service.ProductProductParameterService;
import com.landleaf.bms.util.ValueDescriptionUtil;
import com.landleaf.comm.exception.ServiceException;
import com.landleaf.comm.tenant.TenantContext;
import com.landleaf.redis.constance.DictConstance;
import com.landleaf.redis.dict.DictUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.landleaf.bms.domain.enums.ErrorCodeConstants.*;

/**
 * ProductProductParameterServiceImpl
 *
 * @author 张力方
 * @since 2023/7/5
 **/
@Service
@RequiredArgsConstructor
public class ProductProductParameterServiceImpl implements ProductProductParameterService {
    private final ProductMapper productMapper;
    private final ProductProductParameterMapper productProductParameterMapper;
    private final ProductFeatureManagementService productFeatureManagementService;
    private final DictUtils dictUtils;

    @Override
    public void addCustomProductParameter(CustomProductParameterAddRequest request) {
        TenantContext.setIgnore(true);
        // 校验功能标识符是否唯一
        Assert.isTrue(productFeatureManagementService.checkIdentifierUnique(request.getIdentifier(), request.getProductId()),
                () -> new ServiceException(IDENTIFIER_NOT_UNIQUE));
        // 产品参数所属租户与产品一致
        ProductEntity productEntity = productMapper.selectById(request.getProductId());
        ProductProductParameterEntity productProductParameterEntity = request.toEntity();
        productProductParameterEntity.setTenantId(productEntity.getTenantId());
        productProductParameterMapper.insert(productProductParameterEntity);
    }

    @Override
    public void editProductParameter(ProductProductParameterEditRequest request) {
        TenantContext.setIgnore(true);
        Long id = request.getId();
        ProductProductParameterEntity entity = productProductParameterMapper.selectById(id);
        Assert.notNull(entity, "目标不存在");
        request.mergeEntity(entity);
        // 校验
        Assert.isTrue(CharSequenceUtil.equals(entity.getDataType(), request.getDataType()), "数据类型无法变更");
        Assert.isTrue(CollUtil.containsAll(request.getValueDescription().stream().map(ValueDescriptionParam.ValueAccount::getKey).toList(),
                entity.getValueDescription().stream().map(ValueDescription::getKey).toList()), "值描述Key无法变更");
        productProductParameterMapper.updateById(entity);
    }

    @Override
    public void deleteProductParameter(Long id) {
        TenantContext.setIgnore(true);
        Assert.notNull(id, "参数异常");
        ProductProductParameterEntity entity = productProductParameterMapper.selectById(id);
        Assert.notNull(entity, "目标不存在");
        productProductParameterMapper.deleteById(id);
    }

    @Override
    public Page<ProductProductParameterListResponse> pageQueryProductParameter(ProductFeatureQueryRequest request) {
        TenantContext.setIgnore(true);
        return (Page<ProductProductParameterListResponse>) productProductParameterMapper.pageQuery(
                        Page.of(request.getPageNo(), request.getPageSize()),
                        request)
                .convert(it -> {
                    it.setFunctionTypeContent(dictUtils.selectDictLabel(DictConstance.PRODUCT_FUNCTION_TYPE, it.getFunctionType()));
                    it.setDataTypeContent(dictUtils.selectDictLabel(DictConstance.PARAM_DATA_TYPE, it.getDataType()));
                    it.setValueDescriptionContent(ValueDescriptionUtil.convertToString(it.getDataType(), it.getValueDescription()));
                    it.setUnit(ValueDescriptionUtil.unitToString(it.getValueDescription()));
                    return it;
                });
    }

    @Override
    public void updateProductParameterValue(ProductParameterValueUpdateRequest request) {
        TenantContext.setIgnore(true);
        ProductProductParameterEntity entity = productProductParameterMapper.selectById(request.getId());
        if (entity == null){
            throw new ServiceException(PRODUCT_PARAM_NOT_EXISTED);
        }
        if (entity.getFunctionType().equals(FunctionTypeEnum.SYSTEM_DEFAULT.getValue())) {
            throw new ServiceException(PRODUCT_PARAM_MODIFY_FORBID);
        }
        entity.setValue(request.getValue());
        productProductParameterMapper.updateById(entity);
    }
}
