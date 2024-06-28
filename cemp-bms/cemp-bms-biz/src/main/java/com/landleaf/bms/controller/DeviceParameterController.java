package com.landleaf.bms.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.landleaf.bms.domain.request.DeviceParameterChangeRequest;
import com.landleaf.bms.domain.request.FeatureQueryRequest;
import com.landleaf.bms.domain.response.DeviceParameterTabulationResponse;
import com.landleaf.bms.service.DeviceParameterService;
import com.landleaf.comm.base.pojo.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 设备参数接口
 *
 * @author yue lin
 * @since 2023/6/26 15:05
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/function/device/parameter")
@Tag(name = "功能管理-设备参数接口")
public class DeviceParameterController {

    private final DeviceParameterService deviceParameterService;

    /**
     * 分页查询设备参数列表
     *
     * @param request 参数
     * @return  结果集
     */
    @Operation(summary = "分页查询设备参数列表", description = "分页查询设备参数列表")
    @GetMapping("/page")
    public Response<Page<DeviceParameterTabulationResponse>> searchDeviceParameterTabulation(FeatureQueryRequest request) {
        return Response.success(deviceParameterService.searchDeviceParameterTabulation(request));
    }

    /**
     * 删除设备参数
     * @param id id
     * @return  结果
     */
    @Operation(summary = "删除设备参数", description = "删除设备参数")
    @DeleteMapping("/{id}")
    public Response<Void> deleteDeviceParameter(@PathVariable("id") Long id) {
        deviceParameterService.deleteDeviceParameter(id);
        return Response.success();
    }

    /**
     * 创建设备参数
     * @param request 参数
     * @return  结果
     */
    @Operation(summary = "创建设备参数", description = "创建设备参数")
    @PostMapping
    public Response<Long> createDeviceParameter(@RequestBody DeviceParameterChangeRequest.Create request) {
        return Response.success(deviceParameterService.createDeviceParameter(request));
    }

    /**
     * 更新设备参数
     * @param request 参数
     * @return  结果
     */
    @Operation(summary = "更新设备参数", description = "更新设备参数")
    @PutMapping
    public Response<Long> updateDeviceParameter(@RequestBody DeviceParameterChangeRequest.Update request) {
        return Response.success(deviceParameterService.updateDeviceParameter(request));
    }

}
