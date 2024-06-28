package com.landleaf.bms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.landleaf.bms.domain.request.ProductAddRequest;
import com.landleaf.bms.domain.request.ProductChangeStatusRequest;
import com.landleaf.bms.domain.request.ProductEditRequest;
import com.landleaf.bms.domain.request.ProductPageListRequest;
import com.landleaf.bms.domain.response.CategoryTreeResponse;
import com.landleaf.bms.domain.response.ProductDetailsResponse;
import com.landleaf.bms.domain.response.ProductResponse;

import java.util.List;

/**
 * ProductRepositoryService
 *
 * @author 张力方
 * @since 2023/7/5
 **/
public interface ProductManageService {

    /**
     * 新增产品库产品
     */
    void addProduct(ProductAddRequest request);

    /**
     * 编辑产品库产品
     */
    void editProduct(ProductEditRequest request);

    /**
     * 删除产品库产品
     *
     * @param productId 产品id
     */
    void deleteProduct(Long productId);

    /**
     * 分页查询产品库产品
     */
    Page<ProductResponse> pageQuery(ProductPageListRequest request);

    /**
     * 改变产品状态
     */
    void changeProductStatus(ProductChangeStatusRequest request);

    /**
     * 获取品类树
     *
     * @return 品类树
     */
    List<CategoryTreeResponse> getCategoryTree();

    void unrefProduct(Long productId);

    ProductDetailsResponse getProductDetails(Long productId);
}
