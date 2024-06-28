package com.landleaf.oauth.dal.mapper;

import com.landleaf.oauth.domain.entity.MenuEntity;
import com.landleaf.oauth.domain.response.MenuTabulationResponse;
import com.landleaf.pgsql.extension.ExtensionMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 菜单
 *
 * @author yue lin
 * @since 2023/6/1 9:18
 */
@Mapper
public interface MenuEntityMapper extends ExtensionMapper<MenuEntity> {

    /**
     * 查询租户下，已删除的某唯一标识菜单
     *
     * @param permission 唯一标识
     * @param tenantId   租户
     * @return 结果
     */
    MenuEntity searchDeletedMenu(@Param("permission") String permission, @Param("tenantId") Long tenantId);

    /**
     * 恢复已被删除的菜单
     *
     * @param menuId 菜单ID
     */
    void recoverDeletedMenu(@Param("menuId") Long menuId);

    /**
     * 获取当前租户下菜单最大的排序好
     *
     * @param tenantId 租户ID
     * @return 最大序号
     */
    long searchTenantMaxSort(Long tenantId);

    /**
     * 查询模块树状结构
     *
     * @param moduleId 模块ID
     * @param tenantId 租户ID
     * @return 结果
     */
    List<MenuTabulationResponse> searchMenuTree(@Param("moduleId") Long moduleId, @Param("tenantId") Long tenantId);

    /**
     * 查询子节点数据
     *
     * @param parentId 父节点ID
     * @return 结果
     */
    List<MenuTabulationResponse> searchMenuChildren(@Param("parentId") Long parentId);


    /**
     * 通过唯一标识符，查询出包括父级菜单在内的所有菜单信息（调用前忽略租户拦截器）
     *
     * @param permissions 唯一标识符
     * @param tenantId    租户ID
     * @return 结果集
     */
    List<MenuEntity> recursionMenuByPermission(@Param("permissions") List<String> permissions, @Param("tenantId") Long tenantId);

    List<Long> excludeSysMenu(@Param("deleteMenus") List<Long> deleteMenus);

    List<String> selectTenantSpecialPermission(@Param("tenantId") Long tenantId);

    List<String> selectTenantUnShowPermissions(@Param("tenantId") Long tenantId);
}
