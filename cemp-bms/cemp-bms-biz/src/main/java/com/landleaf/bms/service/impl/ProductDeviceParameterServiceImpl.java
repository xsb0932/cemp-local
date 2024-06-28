package com.landleaf.bms.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.text.CharSequenceUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.landleaf.bms.dal.mapper.ProductDeviceParameterMapper;
import com.landleaf.bms.dal.mapper.ProductMapper;
import com.landleaf.bms.domain.entity.ProductDeviceParameterEntity;
import com.landleaf.bms.domain.entity.ProductEntity;
import com.landleaf.bms.api.json.ValueDescription;
import com.landleaf.bms.domain.request.CustomDeviceParameterAddRequest;
import com.landleaf.bms.domain.request.ProductDeviceParameterEditRequest;
import com.landleaf.bms.domain.request.ProductFeatureQueryRequest;
import com.landleaf.bms.domain.request.ValueDescriptionParam;
import com.landleaf.bms.domain.response.DeviceParameterDetailResponse;
import com.landleaf.bms.api.dto.ProductDeviceParameterListResponse;
import com.landleaf.bms.service.ProductDeviceParameterService;
import com.landleaf.bms.service.ProductFeatureManagementService;
import com.landleaf.bms.util.ValueDescriptionUtil;
import com.landleaf.comm.exception.ServiceException;
import com.landleaf.comm.tenant.TenantContext;
import com.landleaf.redis.constance.DictConstance;
import com.landleaf.redis.dict.DictUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.landleaf.bms.domain.enums.ErrorCodeConstants.IDENTIFIER_NOT_UNIQUE;

/**
 * ProductDeviceParameterServiceImpl
 *
 * @author 张力方
 * @since 2023/7/5
 **/
@Service
@RequiredArgsConstructor
public class ProductDeviceParameterServiceImpl implements ProductDeviceParameterService {
    private final ProductMapper productMapper;
    private final ProductDeviceParameterMapper productDeviceParameterMapper;
    private final ProductFeatureManagementService productFeatureManagementService;
    private final DictUtils dictUtils;

    @Override
    public void addCustomDeviceParameter(CustomDeviceParameterAddRequest request) {
        TenantContext.setIgnore(true);
        // 校验功能标识符是否唯一
        Assert.isTrue(productFeatureManagementService.checkIdentifierUnique(request.getIdentifier(), request.getProductId()),
                () -> new ServiceException(IDENTIFIER_NOT_UNIQUE));
        // 设备参数所属租户与产品一致
        ProductEntity productEntity = productMapper.selectById(request.getProductId());
        ProductDeviceParameterEntity productDeviceParameterEntity = request.toEntity();
        productDeviceParameterEntity.setTenantId(productEntity.getTenantId());
        productDeviceParameterMapper.insert(productDeviceParameterEntity);
    }

    @Override
    public void editDeviceParameter(ProductDeviceParameterEditRequest request) {
        TenantContext.setIgnore(true);
        Long id = request.getId();
        ProductDeviceParameterEntity entity = productDeviceParameterMapper.selectById(id);
        Assert.notNull(entity, "目标不存在");
        request.mergeEntity(entity);
        // 校验
        Assert.isTrue(CharSequenceUtil.equals(entity.getDataType(), request.getDataType()), "数据类型无法变更");
        Assert.isTrue(CollUtil.containsAll(request.getValueDescription().stream().map(ValueDescriptionParam.ValueAccount::getKey).toList(),
                entity.getValueDescription().stream().map(ValueDescription::getKey).toList()), "值描述Key无法变更");
        productDeviceParameterMapper.updateById(entity);
    }

    @Override
    public void deleteDeviceParameter(Long id) {
        TenantContext.setIgnore(true);
        Assert.notNull(id, "参数异常");
        ProductDeviceParameterEntity entity = productDeviceParameterMapper.selectById(id);
        Assert.notNull(entity, "目标不存在");
        productDeviceParameterMapper.deleteById(id);
    }

    @Override
    public Page<ProductDeviceParameterListResponse> pageQueryDeviceParameter(ProductFeatureQueryRequest request) {
        TenantContext.setIgnore(true);
        return (Page<ProductDeviceParameterListResponse>) productDeviceParameterMapper.pageQuery(
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

    @Override
    public List<DeviceParameterDetailResponse> listByProduct(String productId) {
        TenantContext.setIgnore(true);
        //查询排除dataType:01 (产品默认参数)
        List<ProductDeviceParameterEntity> list = productDeviceParameterMapper.selectList(new LambdaQueryWrapper<ProductDeviceParameterEntity>().
                eq(ProductDeviceParameterEntity::getProductId,productId).ne(ProductDeviceParameterEntity::getFunctionType,"01"));
        return list.stream().map(paramater -> {
            DeviceParameterDetailResponse response = new DeviceParameterDetailResponse();
            //response.setProductParameterId(productDeviceParameterEntity.getId());
            response.setIdentifier(paramater.getIdentifier());
            response.setFunctionName(paramater.getFunctionName());
            response.setDataTpe(paramater.getDataType());
            response.setValueDescription(paramater.getValueDescription());
            return response;
        }).collect(Collectors.toList());
    }
}
