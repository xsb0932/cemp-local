package com.landleaf.oauth.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.landleaf.oauth.domain.request.RoleCreateRequest;
import com.landleaf.oauth.domain.request.RoleListRequest;
import com.landleaf.oauth.domain.request.RoleUpdateRequest;
import com.landleaf.oauth.domain.response.RoleListResponse;
import com.landleaf.oauth.domain.response.SimpleRoleListResponse;
import com.landleaf.oauth.domain.response.UserLoginSuccessResponse;

import java.util.List;

/**
 * 角色
 *
 * @author 张力方
 * @since 2023/6/2
 **/
public interface RoleService {
    /**
     * 获取当前登录用户角色列表
     *
     * @param userId 用户id
     * @return 角色列表
     */
    List<UserLoginSuccessResponse.UserRoleResponse> getRolesByCurrentLoginUser(Long userId);

    /**
     * 租户管理员分页查询当前租户下角色列表
     *
     * @param request 查询条件
     * @return 下角色列表
     */
    Page<RoleListResponse> getRoleListResponse(RoleListRequest request);

    /**
     * 创建角色
     *
     * @param request 创建参数
     */
    void createRole(RoleCreateRequest request);

    /**
     * 更新角色信息
     *
     * @param request 更新参数
     */
    void updateRole(RoleUpdateRequest request);

    /**
     * 删除角色
     *
     * @param roleId 角色id
     */
    void deleteRole(Long roleId);

    /**
     * 角色列表查询接口
     * <p>
     * 用于查询当前租户下非管理员的角色列表
     */
    List<SimpleRoleListResponse> getSimpleRoleListResponse();

    List<SimpleRoleListResponse> getSimpleRoleListByTenant(Long tenantId);

}
