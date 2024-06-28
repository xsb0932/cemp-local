package com.landleaf.oauth.api;

import com.landleaf.comm.base.pojo.Response;
import com.landleaf.oauth.api.dto.UserDTO;
import com.landleaf.oauth.api.dto.UserEmailDTO;
import com.landleaf.oauth.api.enums.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Yang
 */
@Tag(name = "Feign 服务 - 用户")
@FeignClient(name = ApiConstants.NAME)
public interface UserRpcApi {

    @GetMapping(ApiConstants.PREFIX + "/user/info/{userId}")
    @Operation(summary = "获取用户信息")
    Response<UserDTO> getUserInfo(@PathVariable(value = "userId") Long userId);

    @PostMapping(ApiConstants.PREFIX + "/user/info")
    @Operation(summary = "获取用户信息集合")
    Response<List<UserDTO>> getUserInfoList(@RequestBody List<Long> userIds);

    @PostMapping(ApiConstants.PREFIX + "/user/email")
    @Operation(summary = "获取用户邮箱信息")
    Response<List<UserEmailDTO>> getUsersEmail(@RequestBody List<Long> userIds);

    @PostMapping(ApiConstants.PREFIX + "/user/email-by-tenant")
    @Operation(summary = "根据租户编号，获取admin用户邮箱信息")
    Response<UserEmailDTO> getAdminUserEmail(@RequestBody Long tenantId);

    @GetMapping(ApiConstants.PREFIX + "/user/check-password")
    @Operation(summary = "校验用户密码是否正确")
    Response<Boolean> checkUserPassword(@RequestParam("userId") Long userId, @RequestParam("userPassword") String userPassword);
}
