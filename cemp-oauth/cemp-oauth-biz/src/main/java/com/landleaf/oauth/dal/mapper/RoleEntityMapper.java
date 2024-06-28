package com.landleaf.oauth.dal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.landleaf.oauth.domain.entity.RoleEntity;
import com.landleaf.oauth.domain.entity.UserRoleEntity;
import com.landleaf.oauth.domain.request.RoleListRequest;
import com.landleaf.oauth.domain.response.RoleListResponse;
import com.landleaf.oauth.domain.response.SimpleRoleListResponse;
import com.landleaf.oauth.domain.response.UserLoginSuccessResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色
 *
 * @author yue lin
 * @since 2023/6/1 9:18
 */
@Mapper
public interface RoleEntityMapper extends BaseMapper<RoleEntity> {
    /**
     * 获取当前登录用户角色列表
     *
     * @param userId 用户id
     * @return 角色列表
     */
    List<UserLoginSuccessResponse.UserRoleResponse> getRolesByCurrentLoginUser(Long userId);

    /**
     * 查询角色列表
     *
     * @param request 请求参数
     * @return 角色列表
     */
    Page<RoleListResponse> getRoleListResponse(@Param("page") Page<RoleListResponse> page,
                                               @Param("request") RoleListRequest request);

    /**
     * 查询角色权限列表
     *
     * @param roleId 角色id
     * @return 权限列表
     */
    List<RoleListResponse.Menu> getRoleModuleMenuList(@Param("roleId") Long roleId);

    /**
     * 查询角色用户列表
     *
     * @param roleId 角色id
     * @return 用户列表
     */
    List<RoleListResponse.User> getRoleUserList(@Param("roleId") Long roleId);

    /**
     * 获取用户指定角色类型角色
     *
     * @param roleType 角色类型
     * @param userId   用户id
     */
    List<UserRoleEntity> getUserRoleByRoleType(@Param("roleType") Short roleType, @Param("userId") Long userId);

    /**
     * 角色列表查询接口
     * <p>
     * 用于查询当前租户下非管理员的角色列表
     */
    List<SimpleRoleListResponse> getSimpleRoleListResponse();

    List<SimpleRoleListResponse> getSimpleRoleListByTenant(@Param("tenantId") Long tenantId);
}
