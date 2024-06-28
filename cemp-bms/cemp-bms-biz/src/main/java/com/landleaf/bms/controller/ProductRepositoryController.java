package com.landleaf.bms.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.landleaf.bms.domain.request.*;
import com.landleaf.bms.domain.response.CategoryTreeResponse;
import com.landleaf.bms.domain.response.RepoProductDetailsResponse;
import com.landleaf.bms.domain.response.RepoProductResponse;
import com.landleaf.bms.service.ProductRepositoryService;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.operatelog.core.annotations.OperateLog;
import com.landleaf.operatelog.core.enums.ModuleTypeEnums;
import com.landleaf.operatelog.core.enums.OperateTypeEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 产品中心-产品库相关接口
 *
 * @author 张力方
 * @since 2023/7/3
 **/
@RequiredArgsConstructor
@RestController
@RequestMapping("/product/repo")
@Tag(name = "产品中心-产品库相关接口")
public class ProductRepositoryController {
    private final ProductRepositoryService productRepositoryService;

    /**
     * 产品库-新增产品
     *
     * @param request 产品新增参数
     */
    @PostMapping
    @Operation(summary = "产品库-新增产品", description = "产品库-新增产品")
    public Response<Void> addRepoProduct(@Validated @RequestBody ProductAddRequest request) {
        productRepositoryService.addRepoProduct(request);
        return Response.success();
    }

    /**
     * 产品库-编辑产品
     *
     * @param request 产品编辑参数
     */
    @PutMapping
    @Operation(summary = "产品库-编辑产品", description = "产品库-编辑产品")
    public Response<Void> editRepoProduct(@Validated @RequestBody ProductEditRequest request) {
        productRepositoryService.editRepoProduct(request);
        return Response.success();
    }

    /**
     * 产品库-删除产品
     *
     * @param productId 产品id
     */
    @DeleteMapping("/{productId}")
    @Operation(summary = "产品库-删除产品", description = "产品库-删除产品")
    public Response<Void> deleteRepoProduct(@PathVariable Long productId) {
        productRepositoryService.deleteRepoProduct(productId);
        return Response.success();
    }

    /**
     * 产品库-获取产品详情
     *
     * @param productId 产品id
     */
    @GetMapping("/{productId}")
    @Operation(summary = "产品库-获取产品详情", description = "产品库-获取产品详情")
    public Response<RepoProductDetailsResponse> getRepoProductDetails(@PathVariable Long productId) {
        RepoProductDetailsResponse response = productRepositoryService.getRepoProductDetails(productId);
        return Response.success(response);
    }

    /**
     * 产品库-分页查询接口
     *
     * @param request 请求参数
     * @return 产品列表
     */
    @GetMapping("/page-list")
    @Operation(summary = "产品库-分页查询接口", description = "产品库-分页查询接口")
    public Response<Page<RepoProductResponse>> pageQuery(@Validated ProductPageListRequest request) {
        Page<RepoProductResponse> repoProductResponsePage = productRepositoryService.pageQuery(request);
        return Response.success(repoProductResponsePage);
    }

    /**
     * 产品库-编辑产品状态
     *
     * @param request 产品状态编辑参数
     */
    @PutMapping("/status")
    @Operation(summary = "产品库-编辑产品状态", description = "产品库-编辑产品状态")
    @OperateLog(module = ModuleTypeEnums.BMS, name = "产品库-编辑产品状态", type = OperateTypeEnum.UPDATE)
    public Response<Void> changeProductStatus(@Validated @RequestBody ProductChangeStatusRequest request) {
        productRepositoryService.changeProductStatus(request);
        return Response.success();
    }

    /**
     * 产品库-产品管理引用产品库
     * <p>
     * 只有已发布的产品才能被引用
     *
     * @param request 引用产品请求
     */
    @PostMapping("/ref/product")
    @Operation(summary = "产品库-产品管理引用产品库", description = "产品库-产品管理引用产品库")
    public Response<Void> refProduct(@Validated @RequestBody RepoProductRefRequest request) {
        productRepositoryService.refProduct(request);
        return Response.success();
    }

    /**
     * 产品库-获取品类树
     *
     * @return 品类树
     */
    @GetMapping("/category/tree")
    @Operation(summary = "产品库-获取品类树", description = "产品库-获取品类树")
    public Response<List<CategoryTreeResponse>> getCategoryTree() {
        List<CategoryTreeResponse> response = productRepositoryService.getCategoryTree();
        return Response.success(response);
    }

}
