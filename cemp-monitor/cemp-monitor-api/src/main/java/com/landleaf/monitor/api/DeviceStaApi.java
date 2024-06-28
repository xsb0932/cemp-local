package com.landleaf.monitor.api;

import com.landleaf.comm.base.pojo.Response;
import com.landleaf.monitor.api.dto.DeviceStaDTO;
import com.landleaf.monitor.api.dto.ProjectStaDTO;
import com.landleaf.monitor.enums.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 监测平台设备统计接口")
public interface DeviceStaApi {
    String HEADER_TENANT_ID = "X-Tenant-Id";

    @GetMapping(ApiConstants.PREFIX + "/list-sta-device-by-category")
    @Operation(summary = "根据品类获取设备统计任务的设备信息")
    Response<List<DeviceStaDTO>> listStaDeviceByCategory(@RequestHeader(name = HEADER_TENANT_ID) Long tenantId, @RequestParam("categoryType") String categoryType);

    @GetMapping(ApiConstants.PREFIX + "/list-project-by-category")
    @Operation(summary = "获取租户下设备的项目集合")
    Response<List<ProjectStaDTO>> listStaProject(@RequestHeader(name = HEADER_TENANT_ID) Long tenantId);
}
