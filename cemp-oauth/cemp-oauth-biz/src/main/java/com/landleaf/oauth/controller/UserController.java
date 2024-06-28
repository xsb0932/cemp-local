package com.landleaf.oauth.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.oauth.domain.request.*;
import com.landleaf.oauth.domain.response.UserInfoResponse;
import com.landleaf.oauth.domain.response.UserSelectiveResponse;
import com.landleaf.oauth.domain.response.UserTabulationResponse;
import com.landleaf.oauth.domain.response.UserValidationResponse;
import com.landleaf.oauth.service.UserService;
import com.landleaf.operatelog.core.annotations.OperateLog;
import com.landleaf.operatelog.core.enums.ModuleTypeEnums;
import com.landleaf.operatelog.core.enums.OperateTypeEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户接口
 *
 * @author yue lin
 * @since 2023/6/6 11:14
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
@Tag(name = "用户接口")
public class UserController {

    private final UserService userService;

    /**
     * 查询用户列表
     * <p>
     * 平台管理查询所有，租户管理查询当前租户
     *
     * @param request 查询条件
     * @return 结果集
     */
    @GetMapping("/users")
    @Operation(summary = "查询用户列表")
    public Response<IPage<UserTabulationResponse>> searchUsers(UserTabulationRequest request) {
        return Response.success(userService.searchUsers(request));
    }

    /**
     * 获取当前用户详细信息
     *
     * @param userId 用户ID
     * @return 结果
     */
    @GetMapping("/info/{userId}")
    @Operation(summary = "获取当前用户详细信息")
    public Response<UserInfoResponse> searchUserInfo(@PathVariable("userId") Long userId) {
        return Response.success(userService.searchUserInfo(userId));
    }

    /**
     * 删除目标用户
     *
     * @param userId 用户ID
     * @return 结果
     */
    @DeleteMapping("/{userId}")
    @Operation(summary = "删除目标用户")
    @OperateLog(module = ModuleTypeEnums.OAUTH, name = "删除用户", type = OperateTypeEnum.DELETE)
    public Response<Void> deleteUser(@PathVariable("userId") Long userId) {
        userService.deleteUser(userId);
        return Response.success();
    }

    /**
     * 变更用户信息
     *
     * @param request 参数
     * @return 结果
     */
    @PutMapping("/change")
    @Operation(summary = "变更用户信息")
    @OperateLog(module = ModuleTypeEnums.OAUTH, name = "变更用户信息", type = OperateTypeEnum.UPDATE)
    public Response<Void> changeUser(@Validated @RequestBody UserChangeRequest request) {
        userService.changeUser(request);
        return Response.success();
    }

    /**
     * 创建用户信息
     *
     * @param request 参数
     * @return 结果
     */
    @PostMapping("/creation")
    @Operation(summary = "创建用户信息")
    @OperateLog(module = ModuleTypeEnums.OAUTH, name = "创建用户", type = OperateTypeEnum.CREATE)
    public Response<Void> createUser(@Validated @RequestBody UserChangeRequest request) {
        userService.createUser(request);
        return Response.success();
    }

    /**
     * 重置用户密码
     *
     * @param userId 用户ID
     * @return 结果
     */
    @PutMapping("/password/reset/{userId}")
    @Operation(summary = "重置用户密码")
    @OperateLog(module = ModuleTypeEnums.OAUTH, name = "重置用户密码", type = OperateTypeEnum.UPDATE)
    public Response<Void> resetPassword(@PathVariable("userId") Long userId) {
        userService.resetPassword(userId);
        return Response.success();
    }

    /**
     * 用户修改密码
     *
     * @return 结果
     */
    @PutMapping("/password/change")
    @Operation(summary = "用户修改密码")
    @OperateLog(module = ModuleTypeEnums.OAUTH, name = "用户修改密码", type = OperateTypeEnum.UPDATE)
    public Response<Void> changePassword(@Validated @RequestBody UserPasswordChangeRequest request) {
        userService.changePassword(request);
        return Response.success();
    }

    /**
     * 查询租户的用户列表（默认当前租户）
     *
     * @return 结果
     */
    @GetMapping("/users/tenant")
    @Operation(summary = "查询当前租户的用户列表")
    public Response<List<UserSelectiveResponse>> tenantUsers(@RequestParam(required = false) Long tenantId) {
        return Response.success(userService.tenantUsers(tenantId));
    }

    /**
     * 查询租户的用户列表（默认当前租户）
     *
     * @return 结果
     */
    @GetMapping("/users/tenant/v2")
    @Operation(summary = "查询当前租户的用户列表")
    public Response<List<UserSelectiveResponse>> tenantUsersV2(@RequestParam(required = false) Long tenantId) {
        return Response.success(userService.tenantUsers(tenantId));
    }

    /**
     * 校验参数是否可用
     *
     * @param request 参数
     * @return 结果
     */
    @GetMapping("/param/validation")
    @Operation(summary = "校验参数是否可用")
    public Response<UserValidationResponse> validationParam(UserValidationRequest request) {
        return Response.success(userService.validationParam(request));
    }

    /**
     * 忘记密码
     *
     * @param request 参数
     * @return 结果
     */
    @PostMapping("/password/forgot")
    @Operation(summary = "忘记密码")
    @OperateLog(module = ModuleTypeEnums.OAUTH, name = "找回密码", type = OperateTypeEnum.CREATE)
    public Response<Void> forgotPassword(@RequestBody @Validated(ForgotPasswordRequest.Rest.class) ForgotPasswordRequest request) {
        userService.forgotPassword(request);
        return Response.success();
    }

    /**
     * 忘记密码-发送口令
     *
     * @param request 参数
     * @return 结果
     */
    @PostMapping("/password/forgot/code")
    @Operation(summary = "忘记密码-发送口令")
    public Response<String> forgotPasswordCode(@RequestBody @Validated(ForgotPasswordRequest.Code.class) ForgotPasswordRequest request) {
        return Response.success(userService.forgotPasswordCode(request));
    }

}
