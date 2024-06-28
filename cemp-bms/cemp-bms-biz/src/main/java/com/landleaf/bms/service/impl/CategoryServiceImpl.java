package com.landleaf.bms.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.text.CharSequenceUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.landleaf.bms.dal.mapper.*;
import com.landleaf.bms.domain.entity.*;
import com.landleaf.bms.domain.enums.FunctionCategoryEnum;
import com.landleaf.bms.domain.enums.FunctionTypeEnum;
import com.landleaf.bms.domain.request.*;
import com.landleaf.bms.domain.response.*;
import com.landleaf.bms.service.CategoryService;
import com.landleaf.comm.exception.ServerException;
import com.landleaf.comm.exception.ServiceException;
import com.landleaf.comm.tenant.TenantContext;
import com.landleaf.pgsql.core.BizSequenceService;
import com.landleaf.pgsql.enums.BizSequenceEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.landleaf.bms.domain.enums.ErrorCodeConstants.*;

/**
 * 品类管理-业务实现
 *
 * @author yue lin
 * @since 2023/7/6 10:09
 */
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final BizSequenceService bizSequenceService;

    private final CategoryMapper categoryMapper;
    private final CategoryCatalogueMapper categoryCatalogueMapper;

    private final DeviceServiceMapper deviceServiceMapper;
    private final DeviceParameterMapper deviceParameterMapper;
    private final DeviceEventMapper deviceEventMapper;
    private final DeviceAttributeMapper deviceAttributeMapper;
    private final ProductParameterMapper productParameterMapper;

    private final CategoryDeviceAttributeMapper categoryDeviceAttributeMapper;
    private final CategoryDeviceEventMapper categoryDeviceEventMapper;
    private final CategoryDeviceParameterMapper categoryDeviceParameterMapper;
    private final CategoryDeviceServiceMapper categoryDeviceServiceMapper;
    private final CategoryProductParameterMapper categoryProductParameterMapper;

    @Override
    public void createCatalogue(CatalogueChangeRequest request) {
        Assert.notNull(request, "参数异常");
        TenantContext.setIgnore(true);
        Long parentId = request.getParentId();
        if (parentId != 0) {
            Assert.notNull(categoryCatalogueMapper.selectById(parentId), () -> new ServiceException(PARENT_CATEGORY_CATALOGUE_NOT_EXIST));
        }
        Assert.isTrue(catalogueValidation(null, request.getName()), () -> new ServerException(CATEGORY_CATALOGUE_NAME_EXIST));
        categoryCatalogueMapper.insert(request.toEntity());
    }

    @Override
    public void updateCatalogue(CatalogueChangeRequest request) {
        Assert.notNull(request, "参数异常");
        TenantContext.setIgnore(true);
        Assert.notNull(categoryCatalogueMapper.selectById(request.getId()), () -> new ServerException(CATEGORY_CATALOGUE_NOT_EXIST));
        Assert.isTrue(catalogueValidation(request.getId(), request.getName()), () -> new ServerException(CATEGORY_CATALOGUE_NAME_EXIST));
        categoryCatalogueMapper.updateById(request.toEntity());
    }

    @Override
    public void deleteCatalogue(Long catalogueId) {
        Assert.notNull(catalogueId, "参数异常");
        TenantContext.setIgnore(true);
        Assert.notNull(categoryCatalogueMapper.selectById(catalogueId), () -> new ServerException(CATEGORY_CATALOGUE_NOT_EXIST));
        // 判断目录是否还有下级目录或品类
        boolean catalogueExists = categoryCatalogueMapper.existChildren(catalogueId);
        boolean categoryExists = categoryMapper.existChildren(catalogueId);
        Assert.isFalse(catalogueExists || categoryExists, () -> new ServerException(CATEGORY_CATALOGUE_NOT_DELETE));
        categoryCatalogueMapper.deleteById(catalogueId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createCategory(CategoryChangeRequest request) {
        Assert.notNull(request, "参数异常");
        TenantContext.setIgnore(true);
        Long parentId = request.getParentId();
        if (parentId != 0) {
            Assert.notNull(categoryCatalogueMapper.selectById(parentId), () -> new ServiceException(PARENT_CATEGORY_CATALOGUE_NOT_EXIST));
        }
        Assert.isTrue(categoryValidation(null, request.getName()), () -> new ServiceException(CATEGORY_NAME_EXIST));
        CategoryEntity categoryEntity = request.toEntity();
        categoryEntity.setBizId(bizSequenceService.next(BizSequenceEnum.CATEGORY));
        categoryMapper.insert(categoryEntity);
        Long categoryEntityId = categoryEntity.getId();
        // 拉取系统默认功能属性
        addDeviceAttributeToCategory(deviceAttributeMapper.selectDefaultData(), categoryEntityId);
        addDeviceParameterToCategory(deviceParameterMapper.selectDefaultData(), categoryEntityId);
        addDeviceServiceToCategory(deviceServiceMapper.selectDefaultData(), categoryEntityId);
        addDeviceEventToCategory(deviceEventMapper.selectDefaultData(), categoryEntityId);
        addProductParameterToCategory(productParameterMapper.selectDefaultData(), categoryEntityId);
        return categoryEntity.getBizId();
    }

    @Override
    public String updateCategory(CategoryChangeRequest request) {
        Assert.notNull(request, "参数异常");
        TenantContext.setIgnore(true);
        CategoryEntity categoryEntity = categoryMapper.selectById(request.getId());
        Assert.notNull(categoryEntity, () -> new ServiceException(CATEGORY_NOT_EXIST));
        Assert.isTrue(categoryValidation(request.getId(), request.getName()), () -> new ServiceException(CATEGORY_NAME_EXIST));
        categoryMapper.updateById(request.toEntity());
        return categoryEntity.getBizId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCategory(String categoryBizId) {
        TenantContext.setIgnore(true);
        Assert.notBlank(categoryBizId, "参数异常");
        CategoryEntity categoryEntity = categoryMapper.selectByBizId(categoryBizId);
        Assert.notNull(categoryEntity, () -> new ServiceException(CATEGORY_NOT_EXIST));
        Long categoryEntityId = categoryEntity.getId();
        categoryMapper.deleteById(categoryEntityId);
        // 关联删除
        categoryDeviceAttributeMapper.delete(
                Wrappers.<CategoryDeviceAttributeEntity>lambdaQuery().eq(CategoryDeviceAttributeEntity::getCategoryId, categoryEntityId));
        categoryDeviceParameterMapper.delete(
                Wrappers.<CategoryDeviceParameterEntity>lambdaQuery().eq(CategoryDeviceParameterEntity::getCategoryId, categoryEntityId));
        categoryDeviceEventMapper.delete(
                Wrappers.<CategoryDeviceEventEntity>lambdaQuery().eq(CategoryDeviceEventEntity::getCategoryId, categoryEntityId));
        categoryDeviceServiceMapper.delete(
                Wrappers.<CategoryDeviceServiceEntity>lambdaQuery().eq(CategoryDeviceServiceEntity::getCategoryId, categoryEntityId));
        categoryProductParameterMapper.delete(
                Wrappers.<CategoryProductParameterEntity>lambdaQuery().eq(CategoryProductParameterEntity::getCategoryId, categoryEntityId));
    }

    @Override
    public void addFeatureToCategory(OperateFeatureFromCategoryRequest request) {
        TenantContext.setIgnore(true);
        FunctionCategoryEnum categoryEnum = FunctionCategoryEnum.convertValue(request.getFunctionCategory());
        CategoryEntity categoryEntity = categoryMapper.selectByBizId(request.getCategoryBizId());
        Assert.notNull(categoryEntity, () -> new ServiceException(CATEGORY_NOT_EXIST));
        Long categoryId = categoryEntity.getId();
        List<Long> featureIds = request.getFeatureIds();
        switch (categoryEnum) {
            case PRODUCT_PARAMETER ->
                    addProductParameterToCategory(productParameterMapper.selectBatchIds(featureIds), categoryId);
            case DEVICE_PARAMETER ->
                    addDeviceParameterToCategory(deviceParameterMapper.selectBatchIds(featureIds), categoryId);
            case DEVICE_ATTRIBUTE ->
                    addDeviceAttributeToCategory(deviceAttributeMapper.selectBatchIds(featureIds), categoryId);
            case DEVICE_EVENT -> addDeviceEventToCategory(deviceEventMapper.selectBatchIds(featureIds), categoryId);
            case DEVICE_SERVICE ->
                    addDeviceServiceToCategory(deviceServiceMapper.selectBatchIds(featureIds), categoryId);
        }
    }

    @Override
    public void deleteFeatureFromCategory(OperateFeatureFromCategoryRequest request) {
        TenantContext.setIgnore(true);
        FunctionCategoryEnum categoryEnum = FunctionCategoryEnum.convertValue(request.getFunctionCategory());
        CategoryEntity categoryEntity = categoryMapper.selectByBizId(request.getCategoryBizId());
        Assert.notNull(categoryEntity, () -> new ServiceException(CATEGORY_NOT_EXIST));
        Long categoryId = categoryEntity.getId();
        List<Long> featureIds = request.getFeatureIds();
        switch (categoryEnum) {
            case PRODUCT_PARAMETER -> deleteCategoryProductParameter(featureIds, categoryId);
            case DEVICE_PARAMETER -> deleteCategoryDeviceParameter(featureIds, categoryId);
            case DEVICE_ATTRIBUTE -> deleteCategoryDeviceAttribute(featureIds, categoryId);
            case DEVICE_EVENT -> deleteCategoryDeviceEvent(featureIds, categoryId);
            case DEVICE_SERVICE -> deleteCategoryDeviceService(featureIds, categoryId);
        }
    }

    @Override
    public List<CategoryTreeResponse> searchCategoryCatalogueTree(Boolean containCategory, Boolean containTop) {
        TenantContext.setIgnore(true);
        List<CategoryTreeResponse> treeResponses;
        if (containCategory) {
            List<CategoryTreeResponse> categoryTreeResponses = categoryCatalogueMapper.selectList(null)
                    .stream()
                    .map(CategoryTreeResponse::parseCatalogue)
                    .toList();
            List<CategoryTreeResponse> categoryTreeResponses1 = categoryMapper.selectList(null)
                    .stream()
                    .map(CategoryTreeResponse::parseCategory)
                    .toList();
            treeResponses = CollUtil.unionAll(categoryTreeResponses, categoryTreeResponses1);
        } else {
            treeResponses = categoryCatalogueMapper.selectList(null)
                    .stream()
                    .map(CategoryTreeResponse::parseCatalogue)
                    .toList();
        }
        List<CategoryTreeResponse> responses = treeResponses
                .stream()
                .filter(it -> it.getParentId() == 0L)
                .peek(it -> it.setChildren(getChildren(it, treeResponses)))
                .toList();
        if (containTop) {
            CategoryTreeResponse categoryTreeResponse = CategoryTreeResponse.createTopLevel();
            categoryTreeResponse.setChildren(responses);
            return List.of(categoryTreeResponse);
        } else {
            return responses;
        }
    }

    @Override
    public List<CategoryTreeResponse> searchCatalogueTree() {
        TenantContext.setIgnore(true);
        List<CategoryTreeResponse> categoryTreeResponses = categoryCatalogueMapper.selectList(null)
                .stream()
                .map(CategoryTreeResponse::parseCatalogue)
                .toList();
        return categoryTreeResponses.stream().filter(it -> it.getParentId() == 0L)
                .peek(it -> it.setChildren(getChildren(it, categoryTreeResponses)))
                .toList();
    }

    @Override
    public Boolean checkCodeUnique(String code, Long id) {
        TenantContext.setIgnore(true);
        return !categoryMapper.exists(Wrappers.<CategoryEntity>lambdaQuery()
                .eq(CategoryEntity::getCode, code)
                .ne(Objects.nonNull(id), CategoryEntity::getId, id)
        );
    }

    @Override
    public IPage<?> searchCandidateData(CategoryFeatureQueryRequest request) {
        TenantContext.setIgnore(true);
        CategoryEntity categoryEntity = categoryMapper.selectByBizId(request.getCategoryBizId());
        Assert.notNull(categoryEntity, () -> new ServiceException(CATEGORY_NOT_EXIST));
        request.setCategoryId(categoryEntity.getId());
        FunctionCategoryEnum functionCategory = FunctionCategoryEnum.convertValue(request.getFunctionCategory());
        Page<?> page = Page.of(request.getPageNo(), request.getPageSize());
        return switch (functionCategory) {
            case PRODUCT_PARAMETER ->
                    categoryProductParameterMapper.searchCandidateData(page, request).convert(ProductParameterTabulationResponse::fill);
            case DEVICE_PARAMETER ->
                    categoryDeviceParameterMapper.searchCandidateData(page, request).convert(DeviceParameterTabulationResponse::fill);
            case DEVICE_ATTRIBUTE ->
                    categoryDeviceAttributeMapper.searchCandidateData(page, request).convert(DeviceAttributeTabulationResponse::fill);
            case DEVICE_EVENT ->
                    categoryDeviceEventMapper.searchCandidateData(page, request).convert(DeviceEventTabulationResponse::fill);
            case DEVICE_SERVICE ->
                    categoryDeviceServiceMapper.searchCandidateData(page, request).convert(DeviceServiceTabulationResponse::fill);
        };
    }

    @Override
    public Page<?> searchFunctionPage(CategoryFeatureQueryRequest request) {
        TenantContext.setIgnore(true);
        FunctionCategoryEnum categoryEnum = FunctionCategoryEnum.convertValue(request.getFunctionCategory());
        CategoryEntity categoryEntity = categoryMapper.selectByBizId(request.getCategoryBizId());
        Assert.notNull(categoryEntity, () -> new ServiceException(CATEGORY_NOT_EXIST));
        request.setCategoryId(categoryEntity.getId());
        Page<?> page = Page.of(request.getPageNo(), request.getPageSize());
        return switch (categoryEnum) {
            case PRODUCT_PARAMETER -> (Page<ProductParameterTabulationResponse>) categoryProductParameterMapper
                    .searchFunctionPage(page, request).convert(ProductParameterTabulationResponse::fill);
            case DEVICE_PARAMETER -> (Page<DeviceParameterTabulationResponse>) categoryDeviceParameterMapper
                    .searchFunctionPage(page, request).convert(DeviceParameterTabulationResponse::fill);
            case DEVICE_ATTRIBUTE -> (Page<DeviceAttributeTabulationResponse>) categoryDeviceAttributeMapper
                    .searchFunctionPage(page, request).convert(DeviceAttributeTabulationResponse::fill);
            case DEVICE_EVENT -> (Page<DeviceEventTabulationResponse>) categoryDeviceEventMapper
                    .searchFunctionPage(page, request).convert(DeviceEventTabulationResponse::fill);
            case DEVICE_SERVICE -> (Page<DeviceServiceTabulationResponse>) categoryDeviceServiceMapper
                    .searchFunctionPage(page, request).convert(DeviceServiceTabulationResponse::fill);
        };
    }

    @Override
    public CategoryInfoResponse searchCategoryInfo(String categoryBizId) {
        TenantContext.setIgnore(true);
        Assert.notBlank(categoryBizId, "参数异常");
        CategoryEntity categoryEntity = categoryMapper.selectOne(Wrappers.<CategoryEntity>lambdaQuery().eq(CategoryEntity::getBizId, categoryBizId));
        Assert.notNull(categoryEntity, () -> new ServiceException(CATEGORY_NOT_EXIST));
        String ascription = categoryCatalogueMapper.recursiveUpListByIds(List.of(categoryEntity.getParentId()))
                .stream()
                .sorted(Comparator.comparing(CategoryTreeListResponse::getId))
                .map(CategoryTreeListResponse::getName)
                .collect(Collectors.joining("/"));
        return CategoryInfoResponse.parse(categoryEntity, ascription);
    }

    @Override
    public Boolean catalogueValidation(Long id, String name) {
        TenantContext.setIgnore(true);
        return !categoryCatalogueMapper.exists(Wrappers.<CategoryCatalogueEntity>lambdaQuery()
                .ne(Objects.nonNull(id), CategoryCatalogueEntity::getId, id)
                .eq(CategoryCatalogueEntity::getName, name));
    }

    @Override
    public Boolean categoryValidation(Long id, String name) {
        TenantContext.setIgnore(true);
        return !categoryMapper.exists(Wrappers.<CategoryEntity>lambdaQuery()
                .ne(Objects.nonNull(id), CategoryEntity::getId, id)
                .eq(CategoryEntity::getName, name));
    }

    @Override
    public void functionCategoryChange(CategoryFeatureChangeRequest request) {
        TenantContext.setIgnore(true);
        FunctionCategoryEnum categoryEnum = FunctionCategoryEnum.convertValue(request.getFunctionCategory());
        CategoryEntity categoryEntity = categoryMapper.selectByBizId(request.getCategoryBizId());
        Assert.notNull(categoryEntity, () -> new ServiceException(CATEGORY_NOT_EXIST));
        switch (categoryEnum) {
            case PRODUCT_PARAMETER -> {
                Assert.isTrue(categoryProductParameterMapper.exists(request.getFunctionId(), categoryEntity.getId()), () -> new ServerException(CATEGORY_ATTRIBUTE_NOT_EXIST_OR_NOT_EDITABLE));
                categoryProductParameterMapper.updateById(request.toProductParameter());
            }
            case DEVICE_PARAMETER -> {
                Assert.isTrue(categoryDeviceParameterMapper.exists(request.getFunctionId(), categoryEntity.getId()), () -> new ServerException(CATEGORY_ATTRIBUTE_NOT_EXIST_OR_NOT_EDITABLE));
                categoryDeviceParameterMapper.updateById(request.toDeviceParameter());
            }
            case DEVICE_ATTRIBUTE -> {
                Assert.isTrue(categoryDeviceAttributeMapper.exists(request.getFunctionId(), categoryEntity.getId()), () -> new ServerException(CATEGORY_ATTRIBUTE_NOT_EXIST_OR_NOT_EDITABLE));
                categoryDeviceAttributeMapper.updateById(request.toDeviceAttribute());
            }
            case DEVICE_EVENT -> {
                Assert.isTrue(categoryDeviceEventMapper.exists(request.getFunctionId(), categoryEntity.getId()), () -> new ServerException(CATEGORY_ATTRIBUTE_NOT_EXIST_OR_NOT_EDITABLE));
                categoryDeviceEventMapper.updateById(request.toDeviceEvent());
            }
            case DEVICE_SERVICE -> {
                Assert.isTrue(categoryDeviceServiceMapper.exists(request.getFunctionId(), categoryEntity.getId()), () -> new ServerException(CATEGORY_ATTRIBUTE_NOT_EXIST_OR_NOT_EDITABLE));
                categoryDeviceServiceMapper.updateById(request.toDeviceService());
            }
        }
    }

    private List<CategoryTreeResponse> getChildren(CategoryTreeResponse response, List<CategoryTreeResponse> responses) {
        TenantContext.setIgnore(true);
        return responses.stream()
                .filter(it -> CharSequenceUtil.equals(String.valueOf(it.getParentId()), response.getId()))
                .peek(it -> {
                    if (it.getIsCatalogue()) {
                        it.setChildren(getChildren(it, responses));
                    }
                }).toList();
    }

    private void addProductParameterToCategory(List<ProductParameterEntity> entityList, Long categoryId) {
        TenantContext.setIgnore(true);
        List<CategoryProductParameterEntity> list = entityList
                .stream()
                .map(it -> BeanUtil.copyProperties(it, CategoryProductParameterEntity.class))
                .peek(it -> it.setId(null))
                .peek(it -> it.setCreator(null))
                .peek(it -> it.setCreateTime(null))
                .peek(it -> it.setUpdater(null))
                .peek(it -> it.setUpdateTime(null))
                .peek(it -> it.setCategoryId(categoryId))
                .toList();
        if (CollUtil.isNotEmpty(list)) {
            categoryProductParameterMapper.insertBatchSomeColumn(list);
        }
    }

    private void addDeviceParameterToCategory(List<DeviceParameterEntity> entityList, Long categoryId) {
        TenantContext.setIgnore(true);
        List<CategoryDeviceParameterEntity> list = entityList
                .stream()
                .map(it -> BeanUtil.copyProperties(it, CategoryDeviceParameterEntity.class))
                .peek(it -> it.setId(null))
                .peek(it -> it.setCreator(null))
                .peek(it -> it.setCreateTime(null))
                .peek(it -> it.setUpdater(null))
                .peek(it -> it.setUpdateTime(null))
                .peek(it -> it.setCategoryId(categoryId))
                .toList();
        if (CollUtil.isNotEmpty(list)) {
            categoryDeviceParameterMapper.insertBatchSomeColumn(list);
        }
    }

    private void addDeviceAttributeToCategory(List<DeviceAttributeEntity> entityList, Long categoryId) {
        TenantContext.setIgnore(true);
        List<CategoryDeviceAttributeEntity> list = entityList
                .stream()
                .map(it -> BeanUtil.copyProperties(it, CategoryDeviceAttributeEntity.class))
                .peek(it -> it.setId(null))
                .peek(it -> it.setCreator(null))
                .peek(it -> it.setCreateTime(null))
                .peek(it -> it.setUpdater(null))
                .peek(it -> it.setUpdateTime(null))
                .peek(it -> it.setCategoryId(categoryId))
                .toList();
        if (CollUtil.isNotEmpty(list)) {
            categoryDeviceAttributeMapper.insertBatchSomeColumn(list);
        }
    }

    private void addDeviceEventToCategory(List<DeviceEventEntity> entityList, Long categoryId) {
        TenantContext.setIgnore(true);
        List<CategoryDeviceEventEntity> list = entityList
                .stream()
                .map(it -> BeanUtil.copyProperties(it, CategoryDeviceEventEntity.class))
                .peek(it -> it.setId(null))
                .peek(it -> it.setCreator(null))
                .peek(it -> it.setCreateTime(null))
                .peek(it -> it.setUpdater(null))
                .peek(it -> it.setUpdateTime(null))
                .peek(it -> it.setCategoryId(categoryId))
                .toList();
        if (CollUtil.isNotEmpty(list)) {
            categoryDeviceEventMapper.insertBatchSomeColumn(list);
        }
    }

    private void addDeviceServiceToCategory(List<DeviceServiceEntity> entityList, Long categoryId) {
        TenantContext.setIgnore(true);
        List<CategoryDeviceServiceEntity> list = entityList
                .stream()
                .map(it -> BeanUtil.copyProperties(it, CategoryDeviceServiceEntity.class))
                .peek(it -> it.setId(null))
                .peek(it -> it.setCreator(null))
                .peek(it -> it.setCreateTime(null))
                .peek(it -> it.setUpdater(null))
                .peek(it -> it.setUpdateTime(null))
                .peek(it -> it.setCategoryId(categoryId))
                .toList();
        if (CollUtil.isNotEmpty(list)) {
            categoryDeviceServiceMapper.insertBatchSomeColumn(list);
        }
    }

    private void deleteCategoryProductParameter(List<Long> functionIds, Long categoryId) {
        TenantContext.setIgnore(true);
        boolean b = categoryProductParameterMapper.selectCount(
                Wrappers.<CategoryProductParameterEntity>lambdaQuery()
                        .in(CategoryProductParameterEntity::getId, functionIds)
                        .in(CategoryProductParameterEntity::getFunctionType, FunctionTypeEnum.SYSTEM_OPTIONAL.getValue(),
                                FunctionTypeEnum.STANDARD_OPTIONAL.getValue())
                        .eq(CategoryProductParameterEntity::getCategoryId, categoryId)
        ) == functionIds.size();
        Assert.isTrue(b, "删除品类功能管理异常");
        categoryProductParameterMapper.deleteBatchIds(functionIds);
    }

    private void deleteCategoryDeviceParameter(List<Long> functionIds, Long categoryId) {
        TenantContext.setIgnore(true);
        boolean b = categoryDeviceParameterMapper.selectCount(
                Wrappers.<CategoryDeviceParameterEntity>lambdaQuery()
                        .in(CategoryDeviceParameterEntity::getId, functionIds)
                        .in(CategoryDeviceParameterEntity::getFunctionType, FunctionTypeEnum.SYSTEM_OPTIONAL.getValue(),
                                FunctionTypeEnum.STANDARD_OPTIONAL.getValue())
                        .eq(CategoryDeviceParameterEntity::getCategoryId, categoryId)
        ) == functionIds.size();
        Assert.isTrue(b, "删除品类功能管理异常");
        categoryDeviceParameterMapper.deleteBatchIds(functionIds);
    }

    private void deleteCategoryDeviceAttribute(List<Long> functionIds, Long categoryId) {
        TenantContext.setIgnore(true);
        boolean b = categoryDeviceAttributeMapper.selectCount(
                Wrappers.<CategoryDeviceAttributeEntity>lambdaQuery()
                        .in(CategoryDeviceAttributeEntity::getId, functionIds)
                        .in(CategoryDeviceAttributeEntity::getFunctionType, FunctionTypeEnum.SYSTEM_OPTIONAL.getValue(),
                                FunctionTypeEnum.STANDARD_OPTIONAL.getValue())
                        .eq(CategoryDeviceAttributeEntity::getCategoryId, categoryId)
        ) == functionIds.size();
        Assert.isTrue(b, "删除品类功能管理异常");
        categoryDeviceAttributeMapper.deleteBatchIds(functionIds);
    }

    private void deleteCategoryDeviceEvent(List<Long> functionIds, Long categoryId) {
        TenantContext.setIgnore(true);
        boolean b = categoryDeviceEventMapper.selectCount(
                Wrappers.<CategoryDeviceEventEntity>lambdaQuery()
                        .in(CategoryDeviceEventEntity::getId, functionIds)
                        .in(CategoryDeviceEventEntity::getFunctionType, FunctionTypeEnum.SYSTEM_OPTIONAL.getValue(),
                                FunctionTypeEnum.STANDARD_OPTIONAL.getValue())
                        .eq(CategoryDeviceEventEntity::getCategoryId, categoryId)
        ) == functionIds.size();
        Assert.isTrue(b, "删除品类功能管理异常");
        categoryDeviceEventMapper.deleteBatchIds(functionIds);
    }

    private void deleteCategoryDeviceService(List<Long> functionIds, Long categoryId) {
        TenantContext.setIgnore(true);
        boolean b = categoryDeviceServiceMapper.selectCount(
                Wrappers.<CategoryDeviceServiceEntity>lambdaQuery()
                        .in(CategoryDeviceServiceEntity::getId, functionIds)
                        .in(CategoryDeviceServiceEntity::getFunctionType, FunctionTypeEnum.SYSTEM_OPTIONAL.getValue(),
                                FunctionTypeEnum.STANDARD_OPTIONAL.getValue())
                        .eq(CategoryDeviceServiceEntity::getCategoryId, categoryId)
        ) == functionIds.size();
        Assert.isTrue(b, "删除品类功能管理异常");
        categoryDeviceServiceMapper.deleteBatchIds(functionIds);
    }

}
