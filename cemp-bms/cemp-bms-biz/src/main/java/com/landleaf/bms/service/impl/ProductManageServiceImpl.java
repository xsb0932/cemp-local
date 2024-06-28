package com.landleaf.bms.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.landleaf.bms.dal.mapper.*;
import com.landleaf.bms.domain.entity.*;
import com.landleaf.bms.domain.enums.ProductStatusEnum;
import com.landleaf.bms.domain.request.ProductAddRequest;
import com.landleaf.bms.domain.request.ProductChangeStatusRequest;
import com.landleaf.bms.domain.request.ProductEditRequest;
import com.landleaf.bms.domain.request.ProductPageListRequest;
import com.landleaf.bms.domain.response.CategoryTreeListResponse;
import com.landleaf.bms.domain.response.CategoryTreeResponse;
import com.landleaf.bms.domain.response.ProductDetailsResponse;
import com.landleaf.bms.domain.response.ProductResponse;
import com.landleaf.bms.service.ProductManageService;
import com.landleaf.bms.service.ProductRepositoryService;
import com.landleaf.comm.exception.ServiceException;
import com.landleaf.comm.tenant.TenantContext;
import com.landleaf.pgsql.base.TenantBaseEntity;
import com.landleaf.pgsql.core.BizSequenceService;
import com.landleaf.pgsql.enums.BizSequenceEnum;
import com.landleaf.redis.constance.DictConstance;
import com.landleaf.redis.dao.ProductCacheDao;
import com.landleaf.redis.dao.dto.ProductCacheDTO;
import com.landleaf.redis.dict.DictUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

import static com.landleaf.bms.domain.enums.BmsConstants.ROOT_CATEGORY_CATALOGUE_ID;
import static com.landleaf.bms.domain.enums.ErrorCodeConstants.*;

/**
 * ProductManageServiceImpl
 *
 * @author 张力方
 * @since 2023/7/6
 **/
@Service
@RequiredArgsConstructor
public class ProductManageServiceImpl implements ProductManageService {
    private final ProductMapper productMapper;
    private final ProductRefMapper productRefMapper;
    private final ProductProductParameterMapper productProductParameterMapper;
    private final ProductDeviceParameterMapper productDeviceParameterMapper;
    private final ProductDeviceAttributeMapper productDeviceAttributeMapper;
    private final ProductDeviceEventMapper productDeviceEventMapper;
    private final ProductDeviceServiceMapper productDeviceServiceMapper;
    private final DictUtils dictUtils;
    private final BizSequenceService bizSequenceService;
    private final CategoryMapper categoryMapper;
    private final CategoryCatalogueMapper categoryCatalogueMapper;
    private final ProductRepositoryService productRepositoryService;
    private final DeviceIotMapper deviceIotMapper;
    private final ProductCacheDao productCacheDao;

    @Override
    public void addProduct(ProductAddRequest request) {
        ProductEntity productEntity = new ProductEntity();
        productEntity.setBizId(bizSequenceService.next(BizSequenceEnum.PRODUCT));
        productEntity.setStatus(ProductStatusEnum.NOT_RELEASE.getType());
        BeanUtils.copyProperties(request, productEntity);
        productMapper.insert(productEntity);
        // 加入缓存
        ProductCacheDTO productCacheDTO = BeanUtil.copyProperties(productEntity, ProductCacheDTO.class);
        productCacheDao.saveProdInfoCache(productCacheDTO);
        // 产品创建时，默认添加品类中所有功能
        Long productId = productEntity.getId();
        String categoryBizId = productEntity.getCategoryId();
        CategoryEntity categoryEntity = categoryMapper.selectOne(Wrappers.<CategoryEntity>lambdaQuery().eq(CategoryEntity::getBizId, categoryBizId));
        productRepositoryService.initFunctions(productId, categoryEntity.getId());
    }

    @Override
    public void editProduct(ProductEditRequest request) {
        ProductEntity productEntity = productMapper.selectById(request.getId());
        // 已发布状态不可修改
        Integer status = productEntity.getStatus();
        if (status.equals(ProductStatusEnum.RELEASE.getType())) {
            throw new ServiceException(PRODUCT_EDIT_FORBID);
        }
        BeanUtils.copyProperties(request, productEntity);
        productMapper.updateById(productEntity);

        // 加入缓存
        ProductCacheDTO productCacheDTO = BeanUtil.copyProperties(productEntity, ProductCacheDTO.class);
        productCacheDao.saveProdInfoCache(productCacheDTO);
    }

    @Override
    public void deleteProduct(Long productId) {
        // 产品发布状态不允许删除
        ProductEntity productEntity = productMapper.selectById(productId);
        Integer status = productEntity.getStatus();
        if (status.equals(ProductStatusEnum.RELEASE.getType())) {
            throw new ServiceException(PRODUCT_DELETE_FORBID);
        }
        // 产品下有设备，不允许删除
        boolean exists = deviceIotMapper.exists(Wrappers.<DeviceIotEntity>lambdaQuery()
                .eq(DeviceIotEntity::getProductId, productId));
        if (exists) {
            throw new ServiceException(PRODUCT_DELETE_FORBID);
        }
        productMapper.deleteById(productId);
        // 关联删除相关功能
        productProductParameterMapper.delete(Wrappers.<ProductProductParameterEntity>lambdaQuery().eq(ProductProductParameterEntity::getProductId, productId));
        productDeviceParameterMapper.delete(Wrappers.<ProductDeviceParameterEntity>lambdaQuery().eq(ProductDeviceParameterEntity::getProductId, productId));
        productDeviceAttributeMapper.delete(Wrappers.<ProductDeviceAttributeEntity>lambdaQuery().eq(ProductDeviceAttributeEntity::getProductId, productId));
        productDeviceEventMapper.delete(Wrappers.<ProductDeviceEventEntity>lambdaQuery().eq(ProductDeviceEventEntity::getProductId, productId));
        productDeviceServiceMapper.delete(Wrappers.<ProductDeviceServiceEntity>lambdaQuery().eq(ProductDeviceServiceEntity::getProductId, productId));
    }

    @Override
    public void unrefProduct(Long productId) {
        // 如果产品下有设备不允许解除关系
        boolean exists = deviceIotMapper.exists(Wrappers.<DeviceIotEntity>lambdaQuery()
                .eq(DeviceIotEntity::getProductId, productId));
        if (exists) {
            throw new ServiceException(PRODUCT_DEVICE_EXIST);
        }
        productRefMapper.delete(Wrappers.<ProductRefEntity>lambdaQuery().eq(ProductRefEntity::getProductId, productId));
    }

    @Override
    public ProductDetailsResponse getProductDetails(Long productId) {
        TenantContext.setIgnore(true);
        ProductEntity productEntity = productMapper.selectById(productId);
        ProductDetailsResponse response = new ProductDetailsResponse();
        BeanUtils.copyProperties(productEntity, response);
        // 处理品类描述
        String categoryBizId = response.getCategoryId();
        CategoryEntity categoryEntity = categoryMapper.selectByBizId(categoryBizId);
        Long parentId = categoryEntity.getParentId();
        String longName = categoryCatalogueMapper.getLongName(parentId);
        longName = longName + "/" + categoryEntity.getName();
        response.setCategoryName(longName);
        response.setImageUrl(categoryEntity.getImage());
        String productStatusLabel = dictUtils.selectDictLabel(DictConstance.PRODUCT_STATUS, response.getStatus().toString());
        response.setStatusLabel(productStatusLabel);
        String communicationTypeLabel = dictUtils.selectDictLabel(DictConstance.PRODUCT_COMMUNICATION_TYPE, response.getCommunicationType());
        response.setCommunicationTypeLabel(communicationTypeLabel);
        response.setIsRepo(productEntity.getTenantId() == null);
        return response;
    }

    @Override
    public Page<ProductResponse> pageQuery(ProductPageListRequest request) {
        TenantContext.setIgnore(true);
        Boolean isCatalogue = request.getIsCatalogue();
        List<String> categoryBizIds;
        if (isCatalogue.equals(Boolean.TRUE)) {
            List<Long> categoryIds = categoryCatalogueMapper.recursiveDownIdsById(Long.valueOf(request.getCategoryId()));
            categoryBizIds = categoryMapper.selectList(Wrappers
                            .<CategoryEntity>lambdaQuery()
                            .in(!ROOT_CATEGORY_CATALOGUE_ID.equals(request.getCategoryId()), CategoryEntity::getParentId, categoryIds))
                    .stream()
                    .map(CategoryEntity::getBizId)
                    .toList();
        } else {
            categoryBizIds = List.of(request.getCategoryId());
        }
        // 自定义产品 & 关联产品
        Page<ProductResponse> productResponsePage = productMapper.pageQueryCustom(
                Page.of(request.getPageNo(), request.getPageSize()), request, categoryBizIds, TenantContext.getTenantId());
        List<ProductResponse> responsePageRecords = productResponsePage.getRecords();
        for (ProductResponse productResponse : responsePageRecords) {
            // 处理品类描述
            String categoryBizId = productResponse.getCategoryId();
            CategoryEntity categoryEntity = categoryMapper.selectByBizId(categoryBizId);
            Long parentId = categoryEntity.getParentId();
            String longName = categoryCatalogueMapper.getLongName(parentId);
            productResponse.setCategoryName(longName);
            String productStatusLabel = dictUtils.selectDictLabel(DictConstance.PRODUCT_STATUS, productResponse.getStatus().toString());
            productResponse.setStatusLabel(productStatusLabel);
            String communicationTypeLabel = dictUtils.selectDictLabel(DictConstance.PRODUCT_COMMUNICATION_TYPE, productResponse.getCommunicationType().toString());
            productResponse.setCommunicationTypeLabel(communicationTypeLabel);
        }
        return productResponsePage;
    }

    @Override
    public void changeProductStatus(ProductChangeStatusRequest request) {
        Integer status = request.getStatus();
        if (status.equals(1)) {
            // 如果产品下有设备不允许修改为未发布
            boolean exists = deviceIotMapper.exists(Wrappers.<DeviceIotEntity>lambdaQuery()
                    .eq(DeviceIotEntity::getProductId, request.getId()));
            if (exists) {
                throw new ServiceException(PRODUCT_DEVICE_EXIST);
            }
        }
        ProductEntity productEntity = productMapper.selectById(request.getId());
        productEntity.setStatus(request.getStatus());
        productMapper.updateById(productEntity);

        // 加入缓存
        ProductCacheDTO productCacheDTO = BeanUtil.copyProperties(productEntity, ProductCacheDTO.class);
        productCacheDao.saveProdInfoCache(productCacheDTO);
    }

    @Override
    public List<CategoryTreeResponse> getCategoryTree() {
        TenantContext.setIgnore(true);
        // 查询所有的品类id
        List<String> categoryIds = new java.util.ArrayList<>(productMapper.selectList(Wrappers.<ProductEntity>lambdaQuery()
                        .eq(TenantBaseEntity::getTenantId, TenantContext.getTenantId()))
                .stream().map(ProductEntity::getCategoryId).toList());
        List<ProductRefEntity> productRefEntities = productRefMapper.selectList(Wrappers.<ProductRefEntity>lambdaQuery()
                .eq(TenantBaseEntity::getTenantId, TenantContext.getTenantId()));
        if (!CollectionUtils.isEmpty(productRefEntities)) {
            List<String> strings = productMapper.selectBatchIds(productRefEntities.stream().map(ProductRefEntity::getProductId).toList())
                    .stream().map(ProductEntity::getCategoryId).toList();
            categoryIds.addAll(strings);
        }
        if (CollectionUtils.isEmpty(categoryIds)) {
            return Collections.emptyList();
        }
        List<CategoryEntity> categoryEntities = categoryMapper.selectBatchByBizIds(categoryIds);
        List<Long> catalogueIds = categoryEntities.stream().map(CategoryEntity::getParentId).toList();
        List<CategoryTreeListResponse> categoryTreeResponses = categoryCatalogueMapper.recursiveUpListByIds(catalogueIds);
        CategoryTreeResponse categoryTreeResponse = productRepositoryService.covertList2Tree(categoryTreeResponses, categoryEntities);
        return Collections.singletonList(categoryTreeResponse);
    }

}
