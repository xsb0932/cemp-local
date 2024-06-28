package com.landleaf.bms.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.text.CharSequenceUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.landleaf.bms.dal.mapper.ProductParameterMapper;
import com.landleaf.bms.domain.entity.ProductParameterEntity;
import com.landleaf.bms.api.json.ValueDescription;
import com.landleaf.bms.domain.request.FeatureQueryRequest;
import com.landleaf.bms.domain.request.ProductParameterChangeRequest;
import com.landleaf.bms.domain.request.ValueDescriptionParam;
import com.landleaf.bms.domain.response.ProductParameterTabulationResponse;
import com.landleaf.bms.service.FeatureManagementService;
import com.landleaf.bms.service.ProductParameterService;
import com.landleaf.comm.exception.ServiceException;
import com.landleaf.redis.dict.DictUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.landleaf.bms.domain.enums.ErrorCodeConstants.IDENTIFIER_NOT_UNIQUE;

/**
 * ProductParameterServiceImpl
 *
 * @author 张力方
 * @since 2023/6/25
 **/
@Service
@RequiredArgsConstructor
public class ProductParameterServiceImpl implements ProductParameterService {

    private final ProductParameterMapper productParameterMapper;
    private final FeatureManagementService featureManagementService;
    private final DictUtils dictUtils;

    @Override
    public Long create(ProductParameterChangeRequest.Create request) {
        Assert.isTrue(featureManagementService.checkIdentifierUnique(request.getIdentifier()),
                () -> new ServiceException(IDENTIFIER_NOT_UNIQUE));
        ProductParameterEntity productParameterEntity = request.toEntity();
        productParameterMapper.insert(productParameterEntity);
        return productParameterEntity.getId();
    }

    @Override
    public Long update(ProductParameterChangeRequest.Update request) {
        Long id = request.getId();
        ProductParameterEntity entity = productParameterMapper.selectById(id);
        Assert.notNull(entity, "目标不存在");
        request.mergeEntity(entity);
        // 校验
        Assert.isTrue(CharSequenceUtil.equals(entity.getDataType(), request.getDataType()), "数据类型无法变更");
        Assert.isTrue(CollUtil.containsAll(request.getValueDescription().stream().map(ValueDescriptionParam.ValueAccount::getKey).toList(),
                entity.getValueDescription().stream().map(ValueDescription::getKey).toList()), "值描述Key无法变更");
        productParameterMapper.updateById(entity);
        return entity.getId();
    }

    @Override
    public void delete(Long id) {
        Assert.notNull(id, "参数异常");
        ProductParameterEntity productParameterEntity = productParameterMapper.selectById(id);
        Assert.notNull(productParameterEntity, "目标不存在");
        productParameterMapper.deleteById(id);
    }

    @Override
    public Page<ProductParameterTabulationResponse> searchProductParameterTabulation(FeatureQueryRequest request) {
        return (Page<ProductParameterTabulationResponse>) productParameterMapper.searchProductParameterTabulation(
                        Page.of(request.getPageNo(), request.getPageSize()),
                        request)
                .convert(ProductParameterTabulationResponse::fill);
    }

}
