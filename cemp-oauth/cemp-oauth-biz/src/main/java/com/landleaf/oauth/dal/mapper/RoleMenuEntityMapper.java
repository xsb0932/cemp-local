package com.landleaf.oauth.dal.mapper;

import com.landleaf.oauth.domain.entity.MenuEntity;
import com.landleaf.oauth.domain.entity.RoleMenuEntity;
import com.landleaf.pgsql.extension.ExtensionMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 角色菜单
 *
 * @author yue lin
 * @since 2023/6/1 9:18
 */
@Mapper
public interface RoleMenuEntityMapper extends ExtensionMapper<RoleMenuEntity> {

    /**
     * 查询是否存在当前关系数据，包括已被删除
     * @param roleId    角色ID
     * @param menuId    菜单ID
     * @param tenantId  租户ID
     * @return  结果
     */
    MenuEntity searchRoleMenu(@Param("roleId") Long roleId,
                              @Param("menuId") Long menuId,
                              @Param("tenantId") Long tenantId);

    /**
     * 恢复已被删除的关系
     * @param id    菜单ID
     */
    void recoverDeletedRoleMenu(@Param("id") Long id);

}
