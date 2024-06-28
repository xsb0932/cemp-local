package com.landleaf.bms.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.landleaf.bms.domain.request.FeatureQueryRequest;
import com.landleaf.bms.domain.request.ProductParameterChangeRequest;
import com.landleaf.bms.domain.response.ProductParameterTabulationResponse;
import com.landleaf.bms.service.ProductParameterService;
import com.landleaf.comm.base.pojo.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 功能管理-产品参数相关接口
 *
 * @author 张力方
 * @since 2023/6/25
 **/
@RequiredArgsConstructor
@RestController
@RequestMapping("/function/product/parameter")
@Tag(name = "功能管理-产品参数相关接口")
public class ProductParameterController {

    private final ProductParameterService productParameterService;

    /**
     * 新增产品参数
     *
     * @param request 产品参数信息
     * @return 产品参数id
     */
    @PostMapping
    @Operation(summary = "新增产品参数", description = "新增产品参数")
    public Response<Long> add(@RequestBody ProductParameterChangeRequest.Create request) {
        Long id = productParameterService.create(request);
        return Response.success(id);
    }

    /**
     * 编辑产品参数
     *
     * @param request 产品参数信息
     * @return 产品参数id
     */
    @PutMapping
    @Operation(summary = "编辑产品参数", description = "编辑产品参数")
    public Response<Long> update(@RequestBody ProductParameterChangeRequest.Update request) {
        Long id = productParameterService.update(request);
        return Response.success(id);
    }

    /**
     * 删除产品参数
     *
     * @param id 产品参数id
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除产品参数", description = "删除产品参数")
    public Response<Void> delete(@PathVariable("id") Long id) {
        productParameterService.delete(id);
        return Response.success();
    }

    /**
     * 分页列表查询产品参数
     */
    @GetMapping("/page")
    @Operation(summary = "分页列表查询产品参数", description = "分页列表查询产品参数")
    public Response<Page<ProductParameterTabulationResponse>> pageList(@Validated FeatureQueryRequest request) {
        return Response.success(productParameterService.searchProductParameterTabulation(request));
    }

}
