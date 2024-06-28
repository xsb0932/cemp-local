package com.landleaf.bms.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.landleaf.bms.domain.request.DeviceServiceChangeRequest;
import com.landleaf.bms.domain.request.FeatureQueryRequest;
import com.landleaf.bms.domain.response.DeviceServiceTabulationResponse;
import com.landleaf.bms.service.DeviceServiceService;
import com.landleaf.comm.base.pojo.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 设备服务接口
 *
 * @author yue lin
 * @since 2023/6/26 15:05
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/function/device/service")
@Tag(name = "功能管理-设备服务接口")
public class DeviceServiceController {

    private final DeviceServiceService deviceServiceService;

    /**
     * 分页查询设备服务列表
     *
     * @param request 参数
     * @return  结果集
     */
    @Operation(summary = "分页查询设备服务列表", description = "分页查询设备服务列表")
    @GetMapping("/page")
    public Response<Page<DeviceServiceTabulationResponse>> searchDeviceServiceTabulation(FeatureQueryRequest request) {
        return Response.success(deviceServiceService.searchDeviceServiceTabulation(request));
    }

    /**
     * 删除设备服务
     * @param id id
     * @return  结果
     */
    @Operation(summary = "删除设备服务", description = "删除设备服务")
    @DeleteMapping("/{id}")
    public Response<Void> deleteDeviceService(@PathVariable("id") Long id) {
        deviceServiceService.deleteDeviceService(id);
        return Response.success();
    }

    /**
     * 创建设备服务
     * @param request 参数
     * @return  结果
     */
    @Operation(summary = "创建设备服务", description = "创建设备服务")
    @PostMapping
    public Response<Long> createDeviceService(@RequestBody DeviceServiceChangeRequest.Create request) {
        return Response.success(deviceServiceService.createDeviceService(request));
    }

    /**
     * 更新设备服务
     * @param request 参数
     * @return  结果
     */
    @Operation(summary = "更新设备服务", description = "更新设备服务")
    @PutMapping
    public Response<Long> updateDeviceService(@RequestBody DeviceServiceChangeRequest.Update request) {
        return Response.success(deviceServiceService.updateDeviceService(request));
    }

}
