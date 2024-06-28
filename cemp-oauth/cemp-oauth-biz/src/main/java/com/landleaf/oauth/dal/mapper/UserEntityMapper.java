package com.landleaf.oauth.dal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.landleaf.oauth.domain.entity.UserEntity;
import com.landleaf.oauth.domain.request.UserTabulationRequest;
import com.landleaf.oauth.domain.response.UserTabulationResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

import static com.landleaf.comm.constance.OauthConstant.PLATFORM_ADMIN_ROLE_TYPE;
import static com.landleaf.comm.constance.OauthConstant.TENANT_ADMIN_ROLE_TYPE;

/**
 * 用户
 *
 * @author yue lin
 * @since 2023/6/1 9:18
 */
@Mapper
public interface UserEntityMapper extends BaseMapper<UserEntity> {

    /**
     * 查询当前用户所拥有的菜单权限
     *
     * @param userId 用户ID
     * @return 结果集
     */
    List<Long> searchMenusByUser(@Param("userId") Long userId);

    /**
     * 用户是否拥有角色类型
     *
     * @param userId   用户ID
     * @param roleType 角色类型1平台管理2租户管理3普通
     * @return 结果集
     */
    int searchCountRole(@Param("userId") Long userId, @Param("roleType") Short roleType);

    /**
     * 查询租户下所有用户
     *
     * @param tenantId 租户
     * @return 结果集
     */
    List<UserEntity> searchUsersByTenant(Long tenantId);

    /**
     * 用户是否为平台管理员
     *
     * @param userId 用户ID
     * @return 结果集
     */
    default boolean searchIsPlatformAdmin(Long userId) {
        return searchCountRole(userId, PLATFORM_ADMIN_ROLE_TYPE) != 0;
    }

    /**
     * 用户是否为租户管理员
     *
     * @param userId 用户ID
     * @return 结果集
     */
    default boolean searchIsTenantAdmin(Long userId) {
        return searchCountRole(userId, TENANT_ADMIN_ROLE_TYPE) != 0;
    }

    /**
     * 分页查询用户列表
     *
     * @param request 参数
     * @param page    分页
     * @return 结果
     */
    Page<UserTabulationResponse> searchUsers(@Param("page") Page<UserTabulationResponse> page,
                                             @Param("request") UserTabulationRequest request);

    List<Long> recursionMenusByUser(@Param("userId") Long userId, @Param("tenantId") Long tenantId);
}
