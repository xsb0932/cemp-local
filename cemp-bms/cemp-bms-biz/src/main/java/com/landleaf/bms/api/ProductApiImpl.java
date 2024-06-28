package com.landleaf.bms.api;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.landleaf.bms.api.dto.*;
import com.landleaf.bms.api.json.ValueDescription;
import com.landleaf.bms.dal.mapper.*;
import com.landleaf.bms.domain.entity.ProductDeviceAttributeEntity;
import com.landleaf.bms.domain.entity.ProductDeviceParameterEntity;
import com.landleaf.bms.domain.entity.ProductEntity;
import com.landleaf.bms.domain.request.ProductAlarmConfQueryRequest;
import com.landleaf.bms.domain.request.ProductFeatureQueryRequest;
import com.landleaf.bms.api.dto.ProductDeviceEventListResponse;
import com.landleaf.bms.service.ProductDeviceAttributeService;
import com.landleaf.bms.service.ProductDeviceEventService;
import com.landleaf.bms.service.ProductDeviceParameterService;
import com.landleaf.bms.service.ProductDeviceServiceService;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.comm.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * ProductApiImpl
 *
 * @author 张力方
 * @since 2023/7/26
 **/
@RestController
@RequiredArgsConstructor
public class ProductApiImpl implements ProductApi {
    private final ProductDeviceAttributeMapper productDeviceAttributeMapper;
    private final ProductMapper productMapper;
    private final ProductDeviceParameterMapper productDeviceParameterMapper;
    private final ProductAlarmConfMapper productAlarmConfMapper;
    private final ProductDeviceServiceService productDeviceServiceServiceImpl;
    private final ProductDeviceEventService productDeviceEventService;
    private final ProductDeviceParameterService productDeviceParameterServiceImpl;
    private final ProductDeviceAttributeService productDeviceAttributeServiceIml;
    private final CategoryDeviceAttributeMapper categoryDeviceAttributeMapper;

    @Override
    public Response<Map<String, List<ProductDeviceAttrResponse>>> getProjectAttrs(List<String> bizProductIds) {
        TenantContext.setIgnore(true);
        if (CollectionUtils.isEmpty(bizProductIds)) {
            return Response.success();
        }
        List<Long> productIds = productMapper.selectList(Wrappers.<ProductEntity>lambdaQuery().in(ProductEntity::getBizId, bizProductIds)).stream().map(ProductEntity::getId).toList();
        List<ProductDeviceAttributeEntity> productDeviceAttributeEntities = productDeviceAttributeMapper.selectList(Wrappers
                .<ProductDeviceAttributeEntity>lambdaQuery()
                .in(ProductDeviceAttributeEntity::getProductId, productIds));
        Map<String, List<ProductDeviceAttrResponse>> result = new LinkedHashMap<>();
        Map<Long, List<ProductDeviceAttributeEntity>> attrMap = productDeviceAttributeEntities.stream().collect(Collectors.groupingBy(ProductDeviceAttributeEntity::getProductId));
        attrMap.forEach((key, value) -> {
            List<ProductDeviceAttrResponse> productDeviceAttrResponses = new ArrayList<>();
            for (ProductDeviceAttributeEntity productDeviceAttributeEntity : value) {
                ProductDeviceAttrResponse productDeviceAttrResponse = new ProductDeviceAttrResponse();
                productDeviceAttrResponse.setAttrCode(productDeviceAttributeEntity.getIdentifier());
                productDeviceAttrResponse.setAttrName(productDeviceAttributeEntity.getFunctionName());
                if ("02".equals(productDeviceAttributeEntity.getDataType()) || "03".equals(productDeviceAttributeEntity.getDataType())) {
                    List<ValueDescription> valueDescription = productDeviceAttributeEntity.getValueDescription();
                    valueDescription.stream().filter(item -> "UNIT".equals(item.getKey())).findFirst()
                            .ifPresent(description -> productDeviceAttrResponse.setUnit(description.getValue()));
                }
                productDeviceAttrResponses.add(productDeviceAttrResponse);
            }
            ProductEntity productEntity = productMapper.selectById(key);
            result.put(productEntity.getBizId(), productDeviceAttrResponses);
        });
        return Response.success(result);
    }

    @Override
    public Response<List<ProductDeviceAttrMapResponse>> getProductAttrsMap(String categoryId) {
        TenantContext.setIgnore(true);
        List<String> productIds = productMapper.selectList(new LambdaQueryWrapper<ProductEntity>().eq(ProductEntity::getCategoryId, categoryId))
                .stream().map(ProductEntity::getId).map(String::valueOf).collect(Collectors.toList());
        List<ProductDeviceAttributeEntity> productDeviceAttributeEntities = productDeviceAttributeMapper.selectList(Wrappers
                .<ProductDeviceAttributeEntity>lambdaQuery()
                .in(ProductDeviceAttributeEntity::getProductId, productIds));
        List<ProductDeviceAttrMapResponse> responses = productDeviceAttributeEntities.stream().map(entity -> {
            ProductDeviceAttrMapResponse res = new ProductDeviceAttrMapResponse();
            BeanUtils.copyProperties(entity, res);
            res.setValueDescription(entity.getValueDescription().stream().map(description -> {
                String key = description.getKey();
                if("04".equals(entity.getDataType())){
                    if("TRUE".equals(description.getKey())){
                        key = "1";
                    }else {
                        key = "0";
                    }
                }
                return new ValueDescriptionResponse(key,description.getValue());
            }).collect(Collectors.toList()));
            return res;
        }).collect(Collectors.toList());
        return Response.success(responses);
    }

    @Override
    public Response<Map<String, List<ProductDeviceAttrMapResponse>>> getProductAttrsMapByProdId(List<String> bizProductIds) {
        TenantContext.setIgnore(true);
        if (CollectionUtils.isEmpty(bizProductIds)) {
            return Response.success();
        }
        Map<Long, String> productIds = productMapper.selectList(Wrappers.<ProductEntity>lambdaQuery().in(ProductEntity::getBizId, bizProductIds)).stream().collect(Collectors.toMap(ProductEntity::getId, ProductEntity::getBizId));
        List<ProductDeviceAttributeEntity> productDeviceAttributeEntities = productDeviceAttributeMapper.selectList(Wrappers
                .<ProductDeviceAttributeEntity>lambdaQuery()
                .in(ProductDeviceAttributeEntity::getProductId, productIds.keySet()));
        Map<String, List<ProductDeviceAttrMapResponse>> responses = productDeviceAttributeEntities.stream().map(entity -> {
            ProductDeviceAttrMapResponse res = new ProductDeviceAttrMapResponse();
            BeanUtils.copyProperties(entity, res);
            res.setValueDescription(entity.getValueDescription().stream().map(description -> new ValueDescriptionResponse(description.getKey(), description.getValue())).collect(Collectors.toList()));
            return res;
        }).collect(Collectors.groupingBy(i->productIds.get(i.getProductId())));
        return Response.success(responses);

//        Map<String,Map<String,String>> descs=  productDeviceAttributeEntities.stream().collect(Collectors.toMap(
//                attr -> String.format("%s_%s", attr.getProductId(), attr.getIdentifier()),
//                productDeviceAttributeEntity -> productDeviceAttributeEntity.getValueDescription().stream().collect(Collectors.toMap(ValueDescription::getKey,ValueDescription::getValue))));


    }

    @Override
    public Response<List<String>> getProductEnumAttrs(Long productId) {
        TenantContext.setIgnore(true);
        return Response.success(productDeviceAttributeMapper.getEnumAttrs(productId));
    }

    @Override
    public Response<List<String>> getCategoryEnumAttrs(String bizCategoryId) {
        TenantContext.setIgnore(true);
        return Response.success(categoryDeviceAttributeMapper.getEnumAttrs(bizCategoryId));
    }

    @Override
    public Response<List<ProductAlarmConfListResponse>> getProductAlarm(String bizProductId) {
        TenantContext.setIgnore(true);
        if (!StringUtils.hasText(bizProductId)) {
            return Response.success();
        }
        ProductEntity product = productMapper.selectOne(Wrappers.<ProductEntity>lambdaQuery().eq(ProductEntity::getBizId, bizProductId));
        if (null == product) {
            return Response.success();
        }
        ProductAlarmConfQueryRequest request = new ProductAlarmConfQueryRequest();
        request.setProductId(product.getId());
        Page<ProductAlarmConfListResponse> productAlarmConfListResponsePage = productAlarmConfMapper.pageQuery(
                Page.of(1, Integer.MAX_VALUE), request);
        return Response.success(productAlarmConfListResponsePage.getRecords());
    }

    @Override
    public Response<ProductDetailResponse> getProductDetail(Long id) {
        TenantContext.setIgnore(true);
        try {
            ProductEntity product = productMapper.selectById(id);
            ProductDetailResponse response = new ProductDetailResponse();
            BeanUtils.copyProperties(product, response);
            return Response.success(response);
        } finally {
            TenantContext.setIgnore(false);
        }
    }

    @Override
    public Response<List<ProductDetailResponse>> getByBizids(List<String> bizProductIds) {
        TenantContext.setIgnore(true);
        List<ProductEntity> result = productMapper.selectList(new LambdaQueryWrapper<ProductEntity>().in(ProductEntity::getBizId, bizProductIds));
        List<ProductDetailResponse> response = result.stream().map(new Function<ProductEntity, ProductDetailResponse>() {
            @Override
            public ProductDetailResponse apply(ProductEntity product) {
                ProductDetailResponse response = new ProductDetailResponse();
                BeanUtils.copyProperties(product, response);
                return response;
            }
        }).collect(Collectors.toList());
        return Response.success(response);
    }

    @Override
    public Response<ProductDeviceParameterResponse> getParameter(Long productId, String identifier) {
        TenantContext.setIgnore(true);
        //添加设备参数
        ProductDeviceParameterEntity productParameter = productDeviceParameterMapper.selectOne(new LambdaQueryWrapper<ProductDeviceParameterEntity>().
                eq(ProductDeviceParameterEntity::getProductId, productId).
                eq(ProductDeviceParameterEntity::getIdentifier, identifier));

        ProductDeviceParameterResponse response = new ProductDeviceParameterResponse();
        BeanUtils.copyProperties(productParameter, response);
        return Response.success(response);
    }

    @Override
    public Response<List<ProductDeviceParameterListResponse>> getParameterByProdId(Long productId) {
        TenantContext.setIgnore(true);
        ProductFeatureQueryRequest req = new ProductFeatureQueryRequest();
        req.setPageNo(1);
        req.setPageSize(Integer.MAX_VALUE);
        req.setProductId(productId);
        Page<ProductDeviceParameterListResponse> page = productDeviceParameterServiceImpl.pageQueryDeviceParameter(req);
        return Response.success(page.getRecords());
    }

    @Override
    public Response<List<ProductDeviceAttributeListResponse>> getAttrsByProdId(Long productId) {
        TenantContext.setIgnore(true);
        ProductFeatureQueryRequest req = new ProductFeatureQueryRequest();
        req.setPageNo(1);
        req.setPageSize(Integer.MAX_VALUE);
        req.setProductId(productId);
        Page<ProductDeviceAttributeListResponse> page = productDeviceAttributeServiceIml.pageQueryDeviceAttribute(req);
        return Response.success(page.getRecords());
    }


    @Override
    public Response<List<ProductDeviceServiceListResponse>> getServiceByProdId(Long productId) {
        TenantContext.setIgnore(true);
        ProductFeatureQueryRequest req = new ProductFeatureQueryRequest();
        req.setPageNo(1);
        req.setPageSize(Integer.MAX_VALUE);
        req.setProductId(productId);
        Page<ProductDeviceServiceListResponse> page = productDeviceServiceServiceImpl.pageQueryDeviceService(req);
        return Response.success(page.getRecords());
    }

    @Override
    public Response<List<ProductDeviceServiceListResponse>> getServiceByBizProdId(String bizProdId) {
        TenantContext.setIgnore(true);
        ProductEntity product = productMapper.selectOne(Wrappers.<ProductEntity>lambdaQuery().eq(ProductEntity::getBizId, bizProdId));
        if (null == product) {
            return Response.success();
        }
        ProductFeatureQueryRequest req = new ProductFeatureQueryRequest();
        req.setPageNo(1);
        req.setPageSize(Integer.MAX_VALUE);
        req.setProductId(product.getId());
        Page<ProductDeviceServiceListResponse> page = productDeviceServiceServiceImpl.pageQueryDeviceService(req);
        return Response.success(page.getRecords());
    }

    @Override
    public Response<List<ProductDeviceEventListResponse>> getEventByProdId(String bizProductId) {
        TenantContext.setIgnore(true);
        ProductEntity product = productMapper.selectOne(Wrappers.<ProductEntity>lambdaQuery().eq(ProductEntity::getBizId, bizProductId));
        if (null == product) {
            return Response.success();
        }
        ProductFeatureQueryRequest req = new ProductFeatureQueryRequest();
        req.setPageNo(1);
        req.setPageSize(Integer.MAX_VALUE);
        req.setProductId(product.getId());
        Page<ProductDeviceEventListResponse> productDeviceEventListResponsePage = productDeviceEventService.pageQueryDeviceEvent(req);
        return Response.success(productDeviceEventListResponsePage.getRecords());
    }

}
