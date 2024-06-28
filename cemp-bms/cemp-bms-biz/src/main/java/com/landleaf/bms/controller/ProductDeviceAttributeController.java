package com.landleaf.bms.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.landleaf.bms.domain.request.CustomDeviceAttributeAddRequest;
import com.landleaf.bms.domain.request.ProductDeviceAttributeEditRequest;
import com.landleaf.bms.domain.request.ProductFeatureQueryRequest;
import com.landleaf.bms.api.dto.ProductDeviceAttributeListResponse;
import com.landleaf.bms.service.ProductDeviceAttributeService;
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
 * 产品-设备属性相关接口
 *
 * @author 张力方
 * @since 2023/7/3
 **/
@RequiredArgsConstructor
@RestController
@RequestMapping("/product/device-attribute")
@Tag(name = "产品-设备属性相关接口")
public class ProductDeviceAttributeController {
    private final ProductDeviceAttributeService productDeviceAttributeService;

    /**
     * 产品-新增自定义设备属性
     *
     * @param request 新增请求参数
     */
    @PostMapping("/custom")
    @Operation(summary = "产品-新增自定义设备属性", description = "产品-新增自定义设备属性")
    @OperateLog(module = ModuleTypeEnums.BMS, name = "产品-新增设备属性", type = OperateTypeEnum.CREATE)
    public Response<Void> addCustomDeviceAttribute(@Validated @RequestBody CustomDeviceAttributeAddRequest request) {
        productDeviceAttributeService.addCustomDeviceAttribute(request);
        return Response.success();
    }

    /**
     * 产品-编辑设备属性
     *
     * @param request 编辑设备属性
     */
    @PutMapping
    @Operation(summary = "产品-编辑设备属性", description = "产品-编辑设备属性")
    public Response<Void> editDeviceAttribute(@Validated @RequestBody ProductDeviceAttributeEditRequest request) {
        productDeviceAttributeService.editDeviceAttribute(request);
        return Response.success();
    }

    /**
     * 产品-删除设备属性
     *
     * @param id 设备属性id
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "产品-编辑设备属性", description = "产品-编辑设备属性")
    @OperateLog(module = ModuleTypeEnums.BMS, name = "产品-删除设备属性", type = OperateTypeEnum.DELETE)
    public Response<Void> deleteDeviceAttribute(@PathVariable Long id) {
        productDeviceAttributeService.deleteDeviceAttribute(id);
        return Response.success();
    }

    /**
     * 产品-分页查询设备属性接口
     *
     * @param request 请求参数
     * @return 设备属性列表
     */
    @GetMapping("/page-list")
    @Operation(summary = "产品-分页查询设备属性接口", description = "产品-分页查询接口")
    public Response<Page<ProductDeviceAttributeListResponse>> pageQueryDeviceAttribute(@Validated ProductFeatureQueryRequest request) {
        Page<ProductDeviceAttributeListResponse> productDeviceAttributeListResponsePage = productDeviceAttributeService.pageQueryDeviceAttribute(request);
        return Response.success(productDeviceAttributeListResponsePage);
    }

}
