package com.landleaf.bms.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.landleaf.bms.domain.request.*;
import com.landleaf.bms.domain.response.CategoryInfoResponse;
import com.landleaf.bms.domain.response.CategoryTreeResponse;

import java.util.List;

/**
 * 品类管理Service
 *
 * @author yue lin
 * @since 2023/7/6 10:09
 */
public interface CategoryService {

    /**
     * 目录创建
     *
     * @param request 参数
     */
    void createCatalogue(CatalogueChangeRequest request);

    /**
     * 目录更新
     *
     * @param request 参数
     */
    void updateCatalogue(CatalogueChangeRequest request);

    /**
     * 目录删除
     *
     * @param catalogueId 目录ID
     */
    void deleteCatalogue(Long catalogueId);

    /**
     * 品类创建
     *
     * @param request 参数
     * @return 结果
     */
    String createCategory(CategoryChangeRequest request);

    /**
     * 品类更新
     *
     * @param request 参数
     * @return 结果
     */
    String updateCategory(CategoryChangeRequest request);

    /**
     * 品类删除
     *
     * @param categoryBizId 品类BizId
     */
    void deleteCategory(String categoryBizId);

    /**
     * 添加功能到品类中
     *
     * @param request 参数
     */
    void addFeatureToCategory(OperateFeatureFromCategoryRequest request);

    /**
     * 从品类中删除功能
     *
     * @param request 参数
     */
    void deleteFeatureFromCategory(OperateFeatureFromCategoryRequest request);

    /**
     * 查询目录品类树状结构（目录+品类）
     *
     * @param containCategory 是否包含品类（默认为true）
     * @param containTop      是否包含顶级全部目录
     * @return 结果
     */
    List<CategoryTreeResponse> searchCategoryCatalogueTree(Boolean containCategory, Boolean containTop);

    /**
     * 查询目录树状结构（仅目录）
     *
     * @return 结果
     */
    List<CategoryTreeResponse> searchCatalogueTree();

    /**
     * 校验编码是否已存在（true可用，false不可用）
     *
     * @param code 编码
     * @param id   id
     * @return 结果
     */
    Boolean checkCodeUnique(String code, Long id);

    /**
     * 获取品类可以添加的功能数据
     *
     * @param request 参数
     * @return 结果集
     */
    IPage<?> searchCandidateData(CategoryFeatureQueryRequest request);

    /**
     * 获取品类的功能数据
     *
     * @param request 参数
     * @return 结果集
     */
    Page<?> searchFunctionPage(CategoryFeatureQueryRequest request);

    /**
     * 通过业务ID查寻品类详情
     *
     * @param categoryBizId 业务ID
     * @return 结果
     */
    CategoryInfoResponse searchCategoryInfo(String categoryBizId);

    /**
     * 目录名称校验是否可用
     *
     * @param id   id
     * @param name 名称
     * @return 结果
     */
    Boolean catalogueValidation(Long id, String name);


    /**
     * 品类名称校验是否可用
     *
     * @param id   id
     * @param name 名称
     * @return 结果
     */
    Boolean categoryValidation(Long id, String name);

    /**
     * 品类管理，所属功能变更
     *
     * @param request 参数
     */
    void functionCategoryChange(CategoryFeatureChangeRequest request);
}
