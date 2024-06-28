package com.landleaf.oauth.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.oauth.domain.request.RoleCreateRequest;
import com.landleaf.oauth.domain.request.RoleListRequest;
import com.landleaf.oauth.domain.request.RoleUpdateRequest;
import com.landleaf.oauth.domain.response.RoleListResponse;
import com.landleaf.oauth.domain.response.SimpleRoleListResponse;
import com.landleaf.oauth.service.RoleService;
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
 * 角色接口
 *
 * @author yue lin
 * @since 2023/6/3 17:11
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/role")
@Tag(name = "角色接口")
public class RoleController {

    private final RoleService roleService;

    /**
     * 根据租户查询角色列表
     *
     * @param tenantId 租户id
     * @return 角色列表
     */
    @GetMapping("/tenant/list")
    @Operation(summary = "根据租户查询角色列表")
    public Response<List<SimpleRoleListResponse>> getSimpleRoleListByTenantResponse(@RequestParam Long tenantId) {
        List<SimpleRoleListResponse> roleListResponse = roleService.getSimpleRoleListByTenant(tenantId);
        return Response.success(roleListResponse);
    }

    /**
     * 角色列表查询接口
     * <p>
     * 用于查询当前租户下非管理员的角色列表
     *
     * @return 角色列表
     */
    @GetMapping("/list")
    @Operation(summary = "角色列表查询接口", description = "用于查询当前租户下非管理员的角色列表")
    public Response<List<SimpleRoleListResponse>> getSimpleRoleListResponse() {
        List<SimpleRoleListResponse> roleListResponse = roleService.getSimpleRoleListResponse();
        return Response.success(roleListResponse);
    }

    /**
     * 角色列表分页查询接口
     * <p>
     * 给租户管理员使用
     *
     * @param request 查询条件
     * @return 角色列表
     */
    @GetMapping("/page/list")
    @Operation(summary = "角色列表分页查询接口", description = "给租户管理员使用")
    public Response<Page<RoleListResponse>> getRoleListResponse(@Validated RoleListRequest request) {
        Page<RoleListResponse> roleListResponse = roleService.getRoleListResponse(request);
        return Response.success(roleListResponse);
    }

    /**
     * 创建角色
     *
     * @param request 创建参数
     */
    @PostMapping
    @Operation(summary = "创建角色")
    @OperateLog(module = ModuleTypeEnums.OAUTH, name = "创建角色", type = OperateTypeEnum.CREATE)
    public Response<Void> createRole(@RequestBody @Validated RoleCreateRequest request) {
        roleService.createRole(request);
        return Response.success();
    }

    /**
     * 更新角色信息
     *
     * @param request 更新参数
     */
    @PutMapping
    @Operation(summary = "更新角色信息")
    @OperateLog(module = ModuleTypeEnums.OAUTH, name = "更新角色", type = OperateTypeEnum.UPDATE)
    public Response<Void> updateRole(@RequestBody @Validated RoleUpdateRequest request) {
        roleService.updateRole(request);
        return Response.success();
    }

    /**
     * 删除角色
     *
     * @param roleId 角色id
     */
    @DeleteMapping("/{roleId}")
    @Operation(summary = "删除角色")
    @OperateLog(module = ModuleTypeEnums.OAUTH, name = "删除角色", type = OperateTypeEnum.DELETE)
    public Response<Void> deleteRole(@PathVariable Long roleId) {
        roleService.deleteRole(roleId);
        return Response.success();
    }

}
