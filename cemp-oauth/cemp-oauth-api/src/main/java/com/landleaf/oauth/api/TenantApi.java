package com.landleaf.oauth.api;

import com.landleaf.comm.base.pojo.Response;
import com.landleaf.oauth.api.dto.StaTenantDTO;
import com.landleaf.oauth.api.dto.TenantInfoResponse;
import com.landleaf.oauth.api.enums.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Feign 服务 - 租户
 *
 * @author 张力方
 * @since 2023/6/13
 **/
@Tag(name = "Feign 服务 - 租户")
@FeignClient(name = ApiConstants.NAME)
public interface TenantApi {

    /**
     * 获取租户信息
     *
     * @param tenantId 租户id
     */
    @PostMapping(ApiConstants.PREFIX + "/tenant")
    @Operation(summary = "获取租户信息")
    Response<TenantInfoResponse> getTenantInfo(@RequestParam("tenantId") Long tenantId);

    @GetMapping(ApiConstants.PREFIX + "/list-sta-job-tenant")
    @Operation(summary = "获取设备统计任务的租户信息")
    Response<List<StaTenantDTO>> listStaJobTenant();

    @GetMapping(ApiConstants.PREFIX + "/tenant-is-admin")
    @Operation(summary = "获取租户信息")
    Response<Boolean> tenantIsAdmin(@RequestParam("tenantId") Long tenantId);

    @GetMapping(ApiConstants.PREFIX + "/get-tenant-admin")
    @Operation(summary = "获取管理员租户id")
    Response<Long> getTenantAdmin();

    @GetMapping(ApiConstants.PREFIX + "/get-tenant-by-code")
    @Operation(summary = "获取租户id")
    Response<Long> getTenantIdByCode(@RequestParam("code") String code);
}
