package com.landleaf.bms.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.landleaf.bms.api.enums.AlarmConfirmTypeEnum;
import com.landleaf.bms.api.enums.AlarmLevelEnum;
import com.landleaf.bms.dal.mapper.*;
import com.landleaf.bms.domain.entity.*;
import com.landleaf.bms.domain.enums.*;
import com.landleaf.bms.domain.request.*;
import com.landleaf.bms.domain.response.CategoryTreeListResponse;
import com.landleaf.bms.domain.response.CategoryTreeResponse;
import com.landleaf.bms.domain.response.RepoProductDetailsResponse;
import com.landleaf.bms.domain.response.RepoProductResponse;
import com.landleaf.bms.service.ProductRepositoryService;
import com.landleaf.comm.exception.ServiceException;
import com.landleaf.comm.tenant.TenantContext;
import com.landleaf.pgsql.base.TenantBaseEntity;
import com.landleaf.pgsql.core.BizSequenceService;
import com.landleaf.pgsql.enums.BizSequenceEnum;
import com.landleaf.redis.constance.DictConstance;
import com.landleaf.redis.dict.DictUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

import static com.landleaf.bms.domain.enums.BmsConstants.ROOT_CATEGORY_CATALOGUE_ID;
import static com.landleaf.bms.domain.enums.ErrorCodeConstants.*;

/**
 * ProductRepositoryServiceImpl
 *
 * @author 张力方
 * @since 2023/7/5
 **/
@Service
@RequiredArgsConstructor
public class ProductRepositoryServiceImpl implements ProductRepositoryService {
    private final ProductMapper productMapper;
    private final ProductRefMapper productRefMapper;
    private final ProductProductParameterMapper productProductParameterMapper;
    private final ProductDeviceParameterMapper productDeviceParameterMapper;
    private final ProductDeviceAttributeMapper productDeviceAttributeMapper;
    private final ProductDeviceEventMapper productDeviceEventMapper;
    private final ProductDeviceServiceMapper productDeviceServiceMapper;
    private final CategoryProductParameterMapper categoryProductParameterMapper;
    private final CategoryDeviceParameterMapper categoryDeviceParameterMapper;
    private final CategoryDeviceAttributeMapper categoryDeviceAttributeMapper;
    private final CategoryDeviceEventMapper categoryDeviceEventMapper;
    private final CategoryDeviceServiceMapper categoryDeviceServiceMapper;
    private final DictUtils dictUtils;
    private final BizSequenceService bizSequenceService;
    private final CategoryMapper categoryMapper;
    private final CategoryCatalogueMapper categoryCatalogueMapper;
    private final DeviceIotMapper deviceIotMapper;
    private final ProductAlarmConfMapper productAlarmConfMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addRepoProduct(ProductAddRequest request) {
        // 只有平台成员能新增产品库
        Long tenantId = TenantContext.getTenantId();
        if (!tenantId.equals(TenantConstants.PLATFORM_ID)) {
            throw new ServiceException(NOT_PERMISSION);
        }
        // 产品库新增的产品租户id为空
        TenantContext.setIgnore(true);
        ProductEntity productEntity = new ProductEntity();
        productEntity.setBizId(bizSequenceService.next(BizSequenceEnum.PRODUCT));
        productEntity.setStatus(ProductStatusEnum.NOT_RELEASE.getType());
        BeanUtils.copyProperties(request, productEntity);
        productMapper.insert(productEntity);
        // 产品创建时，默认添加所有功能
        Long productId = productEntity.getId();
        String categoryBizId = productEntity.getCategoryId();
        CategoryEntity categoryEntity = categoryMapper.selectByBizId(categoryBizId);
        initFunctions(productId, categoryEntity.getId());
    }

    @Override
    public void editRepoProduct(ProductEditRequest request) {
        TenantContext.setIgnore(true);
        ProductEntity productEntity = productMapper.selectById(request.getId());
        // 已发布状态不可修改
        Integer status = productEntity.getStatus();
        if (status.equals(ProductStatusEnum.RELEASE.getType())) {
            throw new ServiceException(PRODUCT_EDIT_FORBID);
        }
        BeanUtils.copyProperties(request, productEntity);
        productMapper.updateById(productEntity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRepoProduct(Long productId) {
        TenantContext.setIgnore(true);
        // 产品发布状态不允许删除
        ProductEntity productEntity = productMapper.selectById(productId);
        Integer status = productEntity.getStatus();
        if (status.equals(ProductStatusEnum.RELEASE.getType())) {
            throw new ServiceException(PRODUCT_DELETE_FORBID);
        }
        // 产品被引用，不允许删除
        boolean exists = productRefMapper.exists(Wrappers.<ProductRefEntity>lambdaQuery()
                .eq(ProductRefEntity::getProductId, productId));
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
    public RepoProductDetailsResponse getRepoProductDetails(Long productId) {
        TenantContext.setIgnore(true);
        ProductEntity productEntity = productMapper.selectById(productId);
        RepoProductDetailsResponse response = new RepoProductDetailsResponse();
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
        return response;
    }

    @Override
    public Page<RepoProductResponse> pageQuery(ProductPageListRequest request) {
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
        Page<RepoProductResponse> repoProductResponsePage = productMapper.pageQueryRepo(Page.of(request.getPageNo(), request.getPageSize()), request, categoryBizIds);
        List<RepoProductResponse> repoProductResponseList = repoProductResponsePage.getRecords();
        for (RepoProductResponse repoProductResponse : repoProductResponseList) {
            // 处理品类描述
            String categoryBizId = repoProductResponse.getCategoryId();
            CategoryEntity categoryEntity = categoryMapper.selectByBizId(categoryBizId);
            Long parentId = categoryEntity.getParentId();
            String longName = categoryCatalogueMapper.getLongName(parentId);
            longName = longName + "/" + categoryEntity.getName();
            repoProductResponse.setCategoryName(longName);
            String productStatusLabel = dictUtils.selectDictLabel(DictConstance.PRODUCT_STATUS, repoProductResponse.getStatus().toString());
            repoProductResponse.setStatusLabel(productStatusLabel);
            String communicationTypeLabel = dictUtils.selectDictLabel(DictConstance.PRODUCT_COMMUNICATION_TYPE, repoProductResponse.getCommunicationType().toString());
            repoProductResponse.setCommunicationTypeLabel(communicationTypeLabel);
        }
        return repoProductResponsePage;
    }

    @Override
    public void changeProductStatus(ProductChangeStatusRequest request) {
        TenantContext.setIgnore(true);
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
        productEntity.setStatus(status);
        productMapper.updateById(productEntity);
    }

    @Override
    public void refProduct(RepoProductRefRequest request) {
        if (TenantContext.getTenantId().equals(TenantConstants.PLATFORM_ID)) {
            throw new ServiceException(PRODUCT_REF_FORBID);
        }
        Long productId = request.getProductId();
        boolean exists = productRefMapper.exists(Wrappers.<ProductRefEntity>lambdaQuery()
                .eq(ProductRefEntity::getProductId, productId));
        if (exists) {
            throw new ServiceException(PRODUCT_REF_FORBID);
        }
        // 只有发布状态的产品才能被引用
        TenantContext.setIgnore(true);
        ProductEntity productEntity = productMapper.selectById(productId);
        if (!productEntity.getStatus().equals(0)) {
            throw new ServiceException(NOT_PERMISSION_REF);
        }
        ProductRefEntity productRefEntity = new ProductRefEntity();
        productRefEntity.setProductId(productId);
        productRefEntity.setTenantId(TenantContext.getTenantId());
        productRefMapper.insert(productRefEntity);
    }

    @Override
    public List<CategoryTreeResponse> getCategoryTree() {
        TenantContext.setIgnore(true);
        // 查询所有的品类id
        List<String> categoryIds = productMapper.selectList(Wrappers.<ProductEntity>lambdaQuery()
                        .isNull(TenantBaseEntity::getTenantId))
                .stream().map(ProductEntity::getCategoryId).toList();
        if (CollectionUtils.isEmpty(categoryIds)) {
            return Collections.emptyList();
        }
        List<CategoryEntity> categoryEntities = categoryMapper.selectBatchByBizIds(categoryIds);
        List<Long> catalogueIds = categoryEntities.stream().map(CategoryEntity::getParentId).toList();
        List<CategoryTreeListResponse> categoryTreeResponses = categoryCatalogueMapper.recursiveUpListByIds(catalogueIds);
        CategoryTreeResponse categoryTreeResponse = covertList2Tree(categoryTreeResponses, categoryEntities);
        return Collections.singletonList(categoryTreeResponse);
    }

    @Override
    public CategoryTreeResponse covertList2Tree(List<CategoryTreeListResponse> categoryTreeListResponses, List<CategoryEntity> categoryEntities) {
        if (CollectionUtils.isEmpty(categoryTreeListResponses)) {
            return null;
        }
        CategoryTreeResponse rootCategory = new CategoryTreeResponse();
        rootCategory.setId(ROOT_CATEGORY_CATALOGUE_ID);
        rootCategory.setName(BmsConstants.ROOT_CATEGORY_CATALOGUE_NAME);
        rootCategory.setIsCatalogue(true);
        recursiveBuildCategoryTreeResponse(rootCategory, categoryTreeListResponses, categoryEntities);
        return rootCategory;
    }

    private void recursiveBuildCategoryTreeResponse(CategoryTreeResponse response, List<CategoryTreeListResponse> categoryTreeListResponses, List<CategoryEntity> categoryEntities) {
        List<CategoryTreeResponse> categoryTreeResponses = new java.util.ArrayList<>(categoryTreeListResponses.stream()
                .filter(item -> item.getParentId().toString().equals(response.getId()))
                .map(item -> {
                    CategoryTreeResponse categoryTreeResponse = new CategoryTreeResponse();
                    categoryTreeResponse.setId(item.getId());
                    categoryTreeResponse.setName(item.getName());
                    categoryTreeResponse.setIsCatalogue(true);
                    categoryTreeResponse.setParentId(item.getParentId());
                    return categoryTreeResponse;
                })
                .toList());
        List<CategoryTreeResponse> categoryTreeLeaf = categoryEntities.stream().filter(item -> item.getParentId().toString().equals(response.getId()))
                .map(item -> {
                    CategoryTreeResponse categoryTreeResponse = new CategoryTreeResponse();
                    categoryTreeResponse.setId(item.getBizId());
                    categoryTreeResponse.setName(item.getName());
                    categoryTreeResponse.setIsCatalogue(false);
                    categoryTreeResponse.setParentId(item.getParentId());
                    categoryTreeResponse.setImage(item.getImage());
                    return categoryTreeResponse;
                }).toList();
        categoryTreeResponses.addAll(categoryTreeLeaf);
        if (CollectionUtils.isEmpty(categoryTreeResponses)) {
            return;
        }
        response.setChildren(categoryTreeResponses);
        for (CategoryTreeResponse child : categoryTreeResponses) {
            recursiveBuildCategoryTreeResponse(child, categoryTreeListResponses, categoryEntities);
        }
    }

    @Override
    public void initFunctions(Long productId, Long categoryId) {
        boolean isIgnore = TenantContext.isIgnore();
        if (!isIgnore) {
            TenantContext.setIgnore(true);
        }
        List<CategoryProductParameterEntity> productParameterEntities = categoryProductParameterMapper.selectList(Wrappers.<CategoryProductParameterEntity>lambdaQuery()
                .eq(CategoryProductParameterEntity::getCategoryId, categoryId));
        List<CategoryDeviceParameterEntity> deviceParameterEntities = categoryDeviceParameterMapper.selectList(Wrappers.<CategoryDeviceParameterEntity>lambdaQuery()
                .eq(CategoryDeviceParameterEntity::getCategoryId, categoryId));
        List<CategoryDeviceAttributeEntity> deviceAttributeEntities = categoryDeviceAttributeMapper.selectList(Wrappers.<CategoryDeviceAttributeEntity>lambdaQuery()
                .eq(CategoryDeviceAttributeEntity::getCategoryId, categoryId));
        List<CategoryDeviceEventEntity> deviceEventEntities = categoryDeviceEventMapper.selectList(Wrappers.<CategoryDeviceEventEntity>lambdaQuery()
                .eq(CategoryDeviceEventEntity::getCategoryId, categoryId));
        List<CategoryDeviceServiceEntity> deviceServiceEntities = categoryDeviceServiceMapper.selectList(Wrappers.<CategoryDeviceServiceEntity>lambdaQuery()
                .eq(CategoryDeviceServiceEntity::getCategoryId, categoryId));
        List<ProductAlarmConfEntity> alarmList = productAlarmConfMapper.selectList(new QueryWrapper<ProductAlarmConfEntity>().lambda().eq(ProductAlarmConfEntity::getProductId, productId).eq(ProductAlarmConfEntity::getAlarmCode, "CC_00_01"));
        if (!isIgnore) {
            TenantContext.setIgnore(false);
        }
        if (!CollectionUtils.isEmpty(productParameterEntities)) {
            List<ProductProductParameterEntity> productProductParameterEntities = BeanUtil.copyToList(productParameterEntities, ProductProductParameterEntity.class);
            productProductParameterEntities.forEach(item -> {
                item.setProductId(productId);
                item.setId(null);
            });
            productProductParameterMapper.insertBatchSomeColumn(productProductParameterEntities);
        }
        if (!CollectionUtils.isEmpty(deviceParameterEntities)) {
            List<ProductDeviceParameterEntity> productDeviceParameterEntities = BeanUtil.copyToList(deviceParameterEntities, ProductDeviceParameterEntity.class);
            productDeviceParameterEntities.forEach(item -> {
                item.setProductId(productId);
                item.setId(null);
            });
            productDeviceParameterMapper.insertBatchSomeColumn(productDeviceParameterEntities);
        }
        if (!CollectionUtils.isEmpty(deviceAttributeEntities)) {
            List<ProductDeviceAttributeEntity> productDeviceAttributeEntities = BeanUtil.copyToList(deviceAttributeEntities, ProductDeviceAttributeEntity.class);
            productDeviceAttributeEntities.forEach(item -> {
                item.setProductId(productId);
                item.setId(null);
            });
            productDeviceAttributeMapper.insertBatchSomeColumn(productDeviceAttributeEntities);
        }
        if (!CollectionUtils.isEmpty(deviceEventEntities)) {
            List<ProductDeviceEventEntity> productDeviceEventEntities = BeanUtil.copyToList(deviceEventEntities, ProductDeviceEventEntity.class);
            productDeviceEventEntities.forEach(item -> {
                item.setProductId(productId);
                item.setId(null);
            });
            productDeviceEventMapper.insertBatchSomeColumn(productDeviceEventEntities);
        }
        if (!CollectionUtils.isEmpty(deviceServiceEntities)) {
            List<ProductDeviceServiceEntity> productDeviceServiceEntities = BeanUtil.copyToList(deviceServiceEntities, ProductDeviceServiceEntity.class);
            productDeviceServiceEntities.forEach(item -> {
                item.setProductId(productId);
                item.setId(null);
            });
            productDeviceServiceMapper.insertBatchSomeColumn(productDeviceServiceEntities);
        }

        // 按照PD的要求，新增产品时，默认增加一个告警码CC_00_01
        // 产品创建时，添加默认通讯告警码
        if (CollectionUtils.isEmpty(alarmList)) {
            ProductAlarmConfEntity productAlarmConfEntity = new ProductAlarmConfEntity();
            productAlarmConfEntity.setIsDefault(Boolean.TRUE);
            productAlarmConfEntity.setProductId(productId);
            productAlarmConfEntity.setAlarmCode("CC_00_01");
            productAlarmConfEntity.setAlarmType(AlarmTypeEnum.CON_ALARM.getCode());
            productAlarmConfEntity.setAlarmTriggerLevel(AlarmLevelEnum.WARN.getCode());
            productAlarmConfEntity.setAlarmRelapseLevel(AlarmLevelEnum.INFO.getCode());
            productAlarmConfEntity.setAlarmConfirmType(AlarmConfirmTypeEnum.AUTO.getCode());
            productAlarmConfEntity.setAlarmDesc("设备通信超时");
            productAlarmConfMapper.insert(productAlarmConfEntity);
        }
    }
}
