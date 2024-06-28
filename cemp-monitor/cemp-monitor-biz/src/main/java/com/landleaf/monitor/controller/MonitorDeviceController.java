package com.landleaf.monitor.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.monitor.domain.request.MonitorDeviceQueryRequest;
import com.landleaf.monitor.domain.response.MonitorDeviceListResponse;
import com.landleaf.monitor.service.MonitorDeviceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 项目管理下设备列表相关接口
 *
 * @author 张力方
 * @since 2023/7/20
 **/
@RestController
@RequiredArgsConstructor
@RequestMapping("/project/device")
@Tag(name = "项目管理下设备列表相关接口", description = "项目管理下设备列表相关接口")
public class MonitorDeviceController {
    private final MonitorDeviceService monitorDeviceService;

    /**
     * 分页查询设备列表
     *
     * @param request 请求
     */
    @Operation(summary = "分页查询设备列表")
    @GetMapping("/page-list")
    public Response<Page<MonitorDeviceListResponse>> pageQuery(@Validated MonitorDeviceQueryRequest request) {
        Page<MonitorDeviceListResponse> monitorDeviceListResponsePage = monitorDeviceService.pageQuery(request);
        return Response.success(monitorDeviceListResponsePage);
    }
}
