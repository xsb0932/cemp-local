package com.landleaf.oauth.api;

import com.landleaf.comm.base.pojo.Response;
import com.landleaf.oauth.api.enums.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Feign 服务 - 用户角色
 *
 * @author 张力方
 * @since 2023/6/12
 **/
@Tag(name = "Feign 服务 - 用户角色")
@FeignClient(name = ApiConstants.NAME)
public interface UserRoleApi {
    /**
     * 判断用户是否具有平台管理员权限
     *
     * @param userId 用户id
     */
    @PostMapping(ApiConstants.PREFIX + "/user-role/platform-admin")
    @Operation(summary = "判断用户是否具有平台管理员权限")
    Response<Boolean> isPlatformAdmin(@RequestParam("userId") Long userId);

    /**
     * 判断用户是否具有租户管理员权限
     *
     * @param userId 用户id
     */
    @PostMapping(ApiConstants.PREFIX + "/user-role/tenant-admin")
    @Operation(summary = "判断用户是否具有租户管理员权限")
    Response<Boolean> isTenantAdmin(@RequestParam("userId") Long userId);
}
