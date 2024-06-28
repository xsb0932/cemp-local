package com.landleaf.oauth.controller;

import com.landleaf.comm.base.pojo.Response;
import com.landleaf.oauth.domain.request.UserLoginRequest;
import com.landleaf.oauth.domain.response.UserLoginSuccessResponse;
import com.landleaf.oauth.domain.response.UserPermissionsResponse;
import com.landleaf.oauth.service.AuthService;
import com.landleaf.operatelog.core.annotations.OperateLog;
import com.landleaf.operatelog.core.enums.ModuleTypeEnums;
import com.landleaf.operatelog.core.enums.OperateTypeEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 认证相关接口
 *
 * @author 张力方
 * @since 2023/6/1
 **/
@Tag(name = "认证")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 使用邮箱/手机号密码登录
     *
     * @param req 请求参数
     * @return token
     */
    @PostMapping("/login")
    @Operation(summary = "使用邮箱/手机号密码登录")
    @OperateLog(module = ModuleTypeEnums.OAUTH, name = "登录", type = OperateTypeEnum.OTHER)
    public Response<UserLoginSuccessResponse> login(@RequestBody @Validated UserLoginRequest req) {
        UserLoginSuccessResponse resp = authService.login(req);
        return Response.success(resp);
    }

    /**
     * 登出系统
     */
    @DeleteMapping("/logout")
    @Operation(summary = "登出系统")
    @OperateLog(module = ModuleTypeEnums.OAUTH, name = "登出", type = OperateTypeEnum.OTHER)
    public Response<Void> logout() {
        authService.logout();
        return Response.success();
    }

    /**
     * 获取当前用户下模块/权限
     *
     * @return 当前用户下模块/权限列表
     */
    @GetMapping("/module-menus")
    @Operation(summary = "获取当前用户下模块/权限")
    public Response<UserPermissionsResponse> getModuleMenus() {
        return Response.success(authService.getModuleMenus());
    }

}
