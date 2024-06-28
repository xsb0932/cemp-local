package com.landleaf.bms.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.landleaf.bms.domain.request.DeviceAttributeChangeRequest;
import com.landleaf.bms.domain.request.FeatureQueryRequest;
import com.landleaf.bms.domain.response.DeviceAttributeTabulationResponse;
import com.landleaf.bms.service.DeviceAttributeService;
import com.landleaf.comm.base.pojo.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 设备属性接口
 *
 * @author yue lin
 * @since 2023/6/26 15:05
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/function/device/attribute")
@Tag(name = "功能管理-设备属性接口")
public class DeviceAttributeController {

    private final DeviceAttributeService deviceAttributeService;

    /**
     * 分页查询设备属性列表
     *
     * @param request 参数
     * @return  结果集
     */
    @Operation(summary = "分页查询设备属性列表", description = "分页查询设备属性列表")
    @GetMapping("/page")
    public Response<Page<DeviceAttributeTabulationResponse>> searchDeviceAttributeTabulation(FeatureQueryRequest request) {
        return Response.success(deviceAttributeService.searchDeviceAttributeTabulation(request));
    }

    /**
     * 删除设备属性
     * @param id id
     * @return  结果
     */
    @Operation(summary = "删除设备属性", description = "删除设备属性")
    @DeleteMapping("/{id}")
    public Response<Void> deleteDeviceAttribute(@PathVariable("id") Long id) {
        deviceAttributeService.deleteDeviceAttribute(id);
        return Response.success();
    }

    /**
     * 创建设备属性
     * @param request 参数
     * @return  结果
     */
    @Operation(summary = "创建设备属性", description = "创建设备属性")
    @PostMapping
    public Response<Long> createDeviceAttribute(@RequestBody DeviceAttributeChangeRequest.Create request) {
        return Response.success(deviceAttributeService.createDeviceAttribute(request));
    }

    /**
     * 更新设备属性
     * @param request 参数
     * @return  结果
     */
    @Operation(summary = "更新设备属性", description = "更新设备属性")
    @PutMapping
    public Response<Long> updateDeviceAttribute(@RequestBody DeviceAttributeChangeRequest.Update request) {
        return Response.success(deviceAttributeService.updateDeviceAttribute(request));
    }

}
