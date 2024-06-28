package com.landleaf.oauth.dal.mapper;

import com.landleaf.oauth.domain.entity.UserRoleEntity;
import com.landleaf.oauth.domain.enums.RoleTypeEnum;
import com.landleaf.pgsql.extension.ExtensionMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户角色
 *
 * @author yue lin
 * @since 2023/6/1 9:18
 */
@Mapper
public interface UserRoleEntityMapper extends ExtensionMapper<UserRoleEntity> {
    /**
     * 根据用户id和角色类型查询角色
     * <p>
     * 注意角色必须是启用的
     *
     * @param userId   用户id
     * @param roleType 角色类型
     * @return 用户角色
     */
    List<UserRoleEntity> getByUserIdAndRoleType(@Param("userId") Long userId, @Param("roleType") Short roleType);

    /**
     * 查询用户所有的角色关系（包括已被删除）
     *
     * @param userId 用户ID
     * @return 结果
     */
    List<UserRoleEntity> searchUsers(Long userId);

    /**
     * 恢复被删除的数据
     *
     * @param ids IDS
     */
    void recoverDeletedUserRole(@Param("ids") List<Long> ids);

    /**
     * 根据租户id和角色类型查询角色
     * <p>
     * 注意角色必须是启用的
     *
     * @param tenantId   用户id
     * @param roleType 角色类型
     * @return 用户角色
     */
    List<UserRoleEntity> getByTenantIdAndRoleType(@Param("tenantId")Long tenantId, @Param("roleType") Short roleType);
}
