package com.landleaf.bms.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.landleaf.bms.domain.request.*;
import com.landleaf.bms.domain.response.ProductProductParameterListResponse;
import com.landleaf.bms.service.ProductProductParameterService;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.operatelog.core.annotations.OperateLog;
import com.landleaf.operatelog.core.enums.ModuleTypeEnums;
import com.landleaf.operatelog.core.enums.OperateTypeEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 产品-产品参数相关接口
 *
 * @author 张力方
 * @since 2023/7/3
 **/
@RequiredArgsConstructor
@RestController
@RequestMapping("/product/product-parameter")
@Tag(name = "产品-产品参数相关接口")
public class ProductProductParameterController {
    private final ProductProductParameterService productProductParameterService;

    /**
     * 产品-新增自定义产品参数
     *
     * @param request 新增请求参数
     */
    @PostMapping("/custom")
    @Operation(summary = "产品-新增自定义产品参数", description = "产品-新增自定义产品参数")
    @OperateLog(module = ModuleTypeEnums.BMS, name = "产品-新增产品参数", type = OperateTypeEnum.CREATE)
    public Response<Void> addCustomProductParameter(@Validated @RequestBody CustomProductParameterAddRequest request) {
        productProductParameterService.addCustomProductParameter(request);
        return Response.success();
    }

    /**
     * 产品-编辑产品参数
     *
     * @param request 编辑产品参数
     */
    @PutMapping
    @Operation(summary = "产品-编辑产品参数", description = "产品-编辑产品参数")
    public Response<Void> editProductParameter(@Validated @RequestBody ProductProductParameterEditRequest request) {
        productProductParameterService.editProductParameter(request);
        return Response.success();
    }

    /**
     * 产品-删除产品参数
     *
     * @param id 产品参数id
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "产品-编辑产品参数", description = "产品-编辑产品参数")
    @OperateLog(module = ModuleTypeEnums.BMS, name = "产品-删除产品参数", type = OperateTypeEnum.DELETE)
    public Response<Void> deleteProductParameter(@PathVariable Long id) {
        productProductParameterService.deleteProductParameter(id);
        return Response.success();
    }

    /**
     * 产品-分页查询产品参数接口
     *
     * @param request 请求参数
     * @return 产品参数列表
     */
    @GetMapping("/page-list")
    @Operation(summary = "产品-分页查询产品参数接口", description = "产品-分页查询接口")
    public Response<Page<ProductProductParameterListResponse>> pageQueryProductParameter(@Validated ProductFeatureQueryRequest request) {
        Page<ProductProductParameterListResponse> productProductParameterListResponsePage = productProductParameterService.pageQueryProductParameter(request);
        return Response.success(productProductParameterListResponsePage);
    }

    /**
     * 产品-设置产品参数值
     * <p>
     * 只有非系统默认功能才能设置值
     *
     * @param request 请求参数
     * @return 产品参数列表
     */
    @PutMapping("/value")
    @Operation(summary = "产品-设置产品参数值", description = "产品-设置产品参数值")
    public Response<Void> updateProductParameterValue(@RequestBody @Validated ProductParameterValueUpdateRequest request) {
        productProductParameterService.updateProductParameterValue(request);
        return Response.success();
    }
}
