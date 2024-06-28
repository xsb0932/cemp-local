package com.landleaf.bms.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.landleaf.bms.domain.request.*;
import com.landleaf.bms.api.dto.ProductDeviceEventListResponse;
import com.landleaf.bms.service.ProductDeviceEventService;
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
 * 产品-设备事件相关接口
 *
 * @author 张力方
 * @since 2023/7/3
 **/
@RequiredArgsConstructor
@RestController
@RequestMapping("/product/device-event")
@Tag(name = "产品-设备事件相关接口")
public class ProductDeviceEventController {
    private final ProductDeviceEventService productDeviceEventService;

    /**
     * 产品-新增自定义设备事件
     *
     * @param request 新增请求参数
     */
    @PostMapping("/custom")
    @Operation(summary = "产品-新增自定义设备事件", description = "产品-新增自定义设备事件")
    @OperateLog(module = ModuleTypeEnums.BMS, name = "产品-新增设备事件", type = OperateTypeEnum.CREATE)
    public Response<Void> addCustomDeviceEvent(@Validated @RequestBody CustomDeviceEventAddRequest request) {
        productDeviceEventService.addCustomDeviceEvent(request);
        return Response.success();
    }

    /**
     * 产品-编辑设备事件
     *
     * @param request 编辑设备事件
     */
    @PutMapping
    @Operation(summary = "产品-编辑设备事件", description = "产品-编辑设备事件")
    public Response<Void> editDeviceEvent(@Validated @RequestBody ProductDeviceEventEditRequest request) {
        productDeviceEventService.editDeviceEvent(request);
        return Response.success();
    }

    /**
     * 产品-删除设备事件
     *
     * @param id 设备事件id
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "产品-编辑设备事件", description = "产品-编辑设备事件")
    @OperateLog(module = ModuleTypeEnums.BMS, name = "产品-删除设备事件", type = OperateTypeEnum.DELETE)
    public Response<Void> deleteDeviceEvent(@PathVariable Long id) {
        productDeviceEventService.deleteDeviceEvent(id);
        return Response.success();
    }

    /**
     * 产品-分页查询设备事件接口
     *
     * @param request 请求参数
     * @return 设备事件列表
     */
    @GetMapping("/page-list")
    @Operation(summary = "产品-分页查询设备事件接口", description = "产品-分页查询接口")
    public Response<Page<ProductDeviceEventListResponse>> pageQueryDeviceEvent(@Validated ProductFeatureQueryRequest request) {
        Page<ProductDeviceEventListResponse> productDeviceEventListResponsePage = productDeviceEventService.pageQueryDeviceEvent(request);
        return Response.success(productDeviceEventListResponsePage);
    }

}
