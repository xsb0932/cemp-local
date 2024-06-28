package com.landleaf.bms.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.landleaf.bms.domain.request.DeviceEventChangeRequest;
import com.landleaf.bms.domain.request.FeatureQueryRequest;
import com.landleaf.bms.domain.response.DeviceEventTabulationResponse;
import com.landleaf.bms.service.DeviceEventService;
import com.landleaf.comm.base.pojo.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 设备事件接口
 *
 * @author yue lin
 * @since 2023/6/26 15:05
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/function/device/event")
@Tag(name = "功能管理-设备事件接口")
public class DeviceEventController {

    private final DeviceEventService deviceEventService;

    /**
     * 分页查询设备事件列表
     *
     * @param request 参数
     * @return  结果集
     */
    @Operation(summary = "分页查询设备事件列表", description = "分页查询设备事件列表")
    @GetMapping("/page")
    public Response<Page<DeviceEventTabulationResponse>> searchDeviceEventTabulation(FeatureQueryRequest request) {
        return Response.success(deviceEventService.searchDeviceEventTabulation(request));
    }

    /**
     * 删除设备事件
     * @param id id
     * @return  结果
     */
    @Operation(summary = "删除设备事件", description = "删除设备事件")
    @DeleteMapping("/{id}")
    public Response<Void> deleteDeviceEvent(@PathVariable("id") Long id) {
        deviceEventService.deleteDeviceEvent(id);
        return Response.success();
    }

    /**
     * 创建设备事件
     * @param request 参数
     * @return  结果
     */
    @Operation(summary = "创建设备事件", description = "创建设备事件")
    @PostMapping
    public Response<Long> createDeviceEvent(@RequestBody DeviceEventChangeRequest.Create request) {
        return Response.success(deviceEventService.createDeviceEvent(request));
    }

    /**
     * 更新设备事件
     * @param request 参数
     * @return  结果
     */
    @Operation(summary = "更新设备事件", description = "更新设备事件")
    @PutMapping
    public Response<Long> updateDeviceEvent(@RequestBody DeviceEventChangeRequest.Update request) {
        return Response.success(deviceEventService.updateDeviceEvent(request));
    }

}
