package com.landleaf.bms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.landleaf.bms.domain.entity.CategoryEntity;
import com.landleaf.bms.domain.request.*;
import com.landleaf.bms.domain.response.CategoryTreeListResponse;
import com.landleaf.bms.domain.response.CategoryTreeResponse;
import com.landleaf.bms.domain.response.RepoProductDetailsResponse;
import com.landleaf.bms.domain.response.RepoProductResponse;

import java.util.List;

/**
 * ProductRepositoryService
 *
 * @author 张力方
 * @since 2023/7/5
 **/
public interface ProductRepositoryService {

    /**
     * 新增产品库产品
     */
    void addRepoProduct(ProductAddRequest request);

    /**
     * 编辑产品库产品
     */
    void editRepoProduct(ProductEditRequest request);

    /**
     * 删除产品库产品
     *
     * @param productId 产品id
     */
    void deleteRepoProduct(Long productId);

    /**
     * 分页查询产品库产品
     */
    Page<RepoProductResponse> pageQuery(ProductPageListRequest request);

    /**
     * 改变产品状态
     */
    void changeProductStatus(ProductChangeStatusRequest request);

    /**
     * 关联产品库产品
     */
    void refProduct(RepoProductRefRequest request);

    /**
     * 获取产品品类树
     *
     * @return 品类树
     */
    List<CategoryTreeResponse> getCategoryTree();

    CategoryTreeResponse covertList2Tree(List<CategoryTreeListResponse> categoryTreeListResponses, List<CategoryEntity> categoryEntities);

    void initFunctions(Long productId, Long categoryId);

    RepoProductDetailsResponse getRepoProductDetails(Long productId);

}
