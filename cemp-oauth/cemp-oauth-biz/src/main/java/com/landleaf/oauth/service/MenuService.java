package com.landleaf.oauth.service;

import com.landleaf.oauth.domain.request.MenuCreateRequest;
import com.landleaf.oauth.domain.request.MenuUpdateRequest;
import com.landleaf.oauth.domain.response.MenuTabulationResponse;
import com.landleaf.oauth.domain.response.ModuleMenuResponse;
import com.landleaf.oauth.domain.response.ModuleMenuTabulationResponse;
import com.landleaf.oauth.domain.response.ModuleResponse;

import java.util.List;

/**
 * 菜单业务接口
 *
 * @author yue lin
 * @since 2023/6/1 13:11
 */
public interface MenuService {

    /**
     * 查询当前租户，拥有到菜单权限
     *
     * @return 结果集
     */
    List<ModuleMenuTabulationResponse> searchMenuTabulationByTenant();

    /**
     * 查询指定租户下权限菜单
     *
     * @param tenantId 租户ID
     * @return 结果集
     */
    List<ModuleMenuTabulationResponse> searchMenuTabulations(String tenantId);

    /**
     * 查询当前登录用户，拥有到菜单权限
     *
     * @return 结果集
     */
    List<ModuleMenuTabulationResponse> searchMenuTabulationByUser();

    /**
     * 查询用户ID，拥有到菜单权限
     *
     * @return 结果集
     */
    List<ModuleMenuResponse> searchMouldMenuByLoginId(Long userId, Long tenantId);

    /**
     * 平台管理员新增菜单
     *
     * @param menuCreateRequest 参数
     */
    void createMenu(MenuCreateRequest menuCreateRequest);

    /**
     * 更新菜单
     *
     * @param menuUpdateRequest 参数
     */
    void updateMenu(MenuUpdateRequest menuUpdateRequest);

    /**
     * 删除菜单
     *
     * @param menuId 菜单ID
     */
    void deleteMenu(Long menuId);

    /**
     * 查询模块列表（当前租户）
     *
     * @return 结果
     */
    List<ModuleResponse> searchModules();

    /**
     * 查询模块下菜单列表
     *
     * @param moduleId 模块ID
     * @return 结果
     */
    List<MenuTabulationResponse> searchModuleMenus(Long moduleId);

    List<ModuleMenuTabulationResponse> searchMenuTabulation();

}
