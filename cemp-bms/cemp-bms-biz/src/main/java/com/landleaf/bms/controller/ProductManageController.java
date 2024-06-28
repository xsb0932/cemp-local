package com.landleaf.bms.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.landleaf.bms.domain.request.ProductAddRequest;
import com.landleaf.bms.domain.request.ProductChangeStatusRequest;
import com.landleaf.bms.domain.request.ProductEditRequest;
import com.landleaf.bms.domain.request.ProductPageListRequest;
import com.landleaf.bms.domain.response.CategoryTreeResponse;
import com.landleaf.bms.domain.response.ProductDetailsResponse;
import com.landleaf.bms.domain.response.ProductResponse;
import com.landleaf.bms.service.ProductManageService;
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
 * 产品管理相关接口
 *
 * @author 张力方
 * @since 2023/7/3
 **/
@RequiredArgsConstructor
@RestController
@RequestMapping("/product/manage")
@Tag(name = "产品管理相关接口")
public class ProductManageController {
    private final ProductManageService productManageService;

    /**
     * 产品管理-新增产品
     *
     * @param request 产品新增参数
     */
    @PostMapping
    @Operation(summary = "产品管理-新增产品", description = "产品管理-新增产品")
    @OperateLog(module = ModuleTypeEnums.BMS, name = "添加产品", type = OperateTypeEnum.CREATE)
    public Response<Void> addProduct(@Validated @RequestBody ProductAddRequest request) {
        productManageService.addProduct(request);
        return Response.success();
    }

    /**
     * 产品管理-编辑产品
     *
     * @param request 产品编辑参数
     */
    @PutMapping
    @Operation(summary = "产品管理-编辑产品", description = "产品管理-编辑产品")
    @OperateLog(module = ModuleTypeEnums.BMS, name = "修改产品", type = OperateTypeEnum.UPDATE)
    public Response<Void> editProduct(@Validated @RequestBody ProductEditRequest request) {
        productManageService.editProduct(request);
        return Response.success();
    }

    /**
     * 产品管理-删除产品
     *
     * @param productId 产品id
     */
    @DeleteMapping("/{productId}")
    @Operation(summary = "产品管理-删除产品", description = "产品管理-删除产品")
    @OperateLog(module = ModuleTypeEnums.BMS, name = "删除产品", type = OperateTypeEnum.DELETE)
    public Response<Void> deleteProduct(@PathVariable Long productId) {
        productManageService.deleteProduct(productId);
        return Response.success();
    }

    /**
     * 产品管理-解除产品库关联关系
     *
     * @param productId 产品id
     */
    @DeleteMapping("/ref/{productId}")
    @Operation(summary = "产品管理-解除产品库关联关系", description = "产品管理-解除产品库关联关系")
    public Response<Void> unrefProduct(@PathVariable Long productId) {
        productManageService.unrefProduct(productId);
        return Response.success();
    }

    /**
     * 产品管理-获取产品详情
     *
     * @param productId 产品id
     */
    @GetMapping("/{productId}")
    @Operation(summary = "产品管理-获取产品详情", description = "产品管理-获取产品详情")
    public Response<ProductDetailsResponse> getProductDetails(@PathVariable Long productId) {
        ProductDetailsResponse response = productManageService.getProductDetails(productId);
        return Response.success(response);
    }

    /**
     * 产品管理-分页查询接口
     *
     * @param request 请求参数
     * @return 产品列表
     */
    @GetMapping("/page-list")
    @Operation(summary = "产品管理-分页查询接口", description = "产品管理-分页查询接口")
    public Response<Page<ProductResponse>> pageQuery(@Validated ProductPageListRequest request) {
        Page<ProductResponse> productResponsePage = productManageService.pageQuery(request);
        return Response.success(productResponsePage);
    }

    /**
     * 产品管理-编辑产品状态
     *
     * @param request 产品状态编辑参数
     */
    @PutMapping("/status")
    @Operation(summary = "产品管理-编辑产品状态", description = "产品管理-编辑产品状态")
    public Response<Void> changeProductStatus(@Validated @RequestBody ProductChangeStatusRequest request) {
        productManageService.changeProductStatus(request);
        return Response.success();
    }

    /**
     * 产品管理-获取品类树
     *
     * @return 品类树
     */
    @GetMapping("/category/tree")
    @Operation(summary = "产品管理-获取品类树", description = "产品管理-获取品类树")
    public Response<List<CategoryTreeResponse>> getCategoryTree() {
        List<CategoryTreeResponse> response = productManageService.getCategoryTree();
        return Response.success(response);
    }
}
