package com.landleaf.bms.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.landleaf.bms.domain.request.*;
import com.landleaf.bms.domain.response.DeviceParameterDetailResponse;
import com.landleaf.bms.api.dto.ProductDeviceParameterListResponse;
import com.landleaf.bms.service.ProductDeviceParameterService;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.operatelog.core.annotations.OperateLog;
import com.landleaf.operatelog.core.enums.ModuleTypeEnums;
import com.landleaf.operatelog.core.enums.OperateTypeEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 产品-设备参数相关接口
 *
 * @author 张力方
 * @since 2023/7/3
 **/
@RequiredArgsConstructor
@RestController
@RequestMapping("/product/device-parameter")
@Tag(name = "产品-设备参数相关接口")
public class ProductDeviceParameterController {

    private final ProductDeviceParameterService productDeviceParameterService;

    /**
     * 产品-新增自定义设备参数
     *
     * @param request 新增请求参数
     */
    @PostMapping("/custom")
    @Operation(summary = "产品-新增自定义设备参数", description = "产品-新增自定义设备参数")
    @OperateLog(module = ModuleTypeEnums.BMS, name = "产品-新增设备参数", type = OperateTypeEnum.CREATE)
    public Response<Void> addCustomDeviceParameter(@Validated @RequestBody CustomDeviceParameterAddRequest request) {
        productDeviceParameterService.addCustomDeviceParameter(request);
        return Response.success();
    }

    /**
     * 产品-编辑设备参数
     *
     * @param request 编辑设备参数
     */
    @PutMapping
    @Operation(summary = "产品-编辑设备参数", description = "产品-编辑设备参数")
    public Response<Void> editDeviceParameter(@Validated @RequestBody ProductDeviceParameterEditRequest request) {
        productDeviceParameterService.editDeviceParameter(request);
        return Response.success();
    }

    /**
     * 产品-删除设备参数
     *
     * @param id 设备参数id
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "产品-编辑设备参数", description = "产品-编辑设备参数")
    @OperateLog(module = ModuleTypeEnums.BMS, name = "产品-删除设备参数", type = OperateTypeEnum.DELETE)
    public Response<Void> deleteDeviceParameter(@PathVariable Long id) {
        productDeviceParameterService.deleteDeviceParameter(id);
        return Response.success();
    }

    /**
     * 产品-分页查询设备参数接口
     *
     * @param request 请求参数
     * @return 设备参数列表
     */
    @GetMapping("/page-list")
    @Operation(summary = "产品-分页查询设备参数接口", description = "产品-分页查询接口")
    public Response<Page<ProductDeviceParameterListResponse>> pageQueryDeviceParameter(@Validated ProductFeatureQueryRequest request) {
        Page<ProductDeviceParameterListResponse> productDeviceParameterListResponsePage = productDeviceParameterService.pageQueryDeviceParameter(request);
        return Response.success(productDeviceParameterListResponsePage);
    }

    /**
     * 根据产品查询设备参数
     *
     * @param
     * @return 设备参数列表
     */
    @GetMapping("/listByProduct")
    @Operation(summary = "产品-设备参数列表", description = "产品-设备参数列表")
    public Response<List<DeviceParameterDetailResponse>> listByProduct(@Parameter(description = "产品id") @RequestParam("productId") String productId) {
        return Response.success(productDeviceParameterService.listByProduct(productId));
    }

}
