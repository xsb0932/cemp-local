package com.landleaf.bms.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.landleaf.bms.domain.request.CustomDeviceServiceAddRequest;
import com.landleaf.bms.domain.request.ProductDeviceServiceEditRequest;
import com.landleaf.bms.domain.request.ProductFeatureQueryRequest;
import com.landleaf.bms.api.dto.ProductDeviceServiceListResponse;
import com.landleaf.bms.service.ProductDeviceServiceService;
import com.landleaf.comm.base.pojo.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 产品-设备服务相关接口
 *
 * @author 张力方
 * @since 2023/7/3
 **/
@RequiredArgsConstructor
@RestController
@RequestMapping("/product/device-service")
@Tag(name = "产品-设备服务相关接口")
public class ProductDeviceServiceController {
    private final ProductDeviceServiceService productDeviceServiceService;

    /**
     * 产品-新增自定义设备服务
     *
     * @param request 新增请求参数
     */
    @PostMapping("/custom")
    @Operation(summary = "产品-新增自定义设备服务", description = "产品-新增自定义设备服务")
    public Response<Void> addCustomDeviceService(@Validated @RequestBody CustomDeviceServiceAddRequest request) {
        productDeviceServiceService.addCustomDeviceService(request);
        return Response.success();
    }

    /**
     * 产品-编辑设备服务
     *
     * @param request 编辑设备服务
     */
    @PutMapping
    @Operation(summary = "产品-编辑设备服务", description = "产品-编辑设备服务")
    public Response<Void> editDeviceService(@Validated @RequestBody ProductDeviceServiceEditRequest request) {
        productDeviceServiceService.editDeviceService(request);
        return Response.success();
    }

    /**
     * 产品-删除设备服务
     *
     * @param id 设备服务id
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "产品-编辑设备服务", description = "产品-编辑设备服务")
    public Response<Void> deleteDeviceService(@PathVariable Long id) {
        productDeviceServiceService.deleteDeviceService(id);
        return Response.success();
    }

    /**
     * 产品-分页查询设备服务接口
     *
     * @param request 请求参数
     * @return 设备服务列表
     */
    @GetMapping("/page-list")
    @Operation(summary = "产品-分页查询设备服务接口", description = "产品-分页查询接口")
    public Response<Page<ProductDeviceServiceListResponse>> pageQueryDeviceService(@Validated ProductFeatureQueryRequest request) {
        Page<ProductDeviceServiceListResponse> productDeviceServiceListResponsePage = productDeviceServiceService.pageQueryDeviceService(request);
        return Response.success(productDeviceServiceListResponsePage);
    }

}
