package com.landleaf.oauth.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.landleaf.comm.exception.ServiceException;
import com.landleaf.comm.tenant.TenantContext;
import com.landleaf.comm.util.servlet.LoginUserUtil;
import com.landleaf.oauth.dal.mapper.RoleEntityMapper;
import com.landleaf.oauth.dal.mapper.RoleMenuEntityMapper;
import com.landleaf.oauth.dal.mapper.UserRoleEntityMapper;
import com.landleaf.oauth.domain.entity.RoleEntity;
import com.landleaf.oauth.domain.entity.RoleMenuEntity;
import com.landleaf.oauth.domain.entity.UserRoleEntity;
import com.landleaf.oauth.domain.enums.ErrorCodeConstants;
import com.landleaf.oauth.domain.enums.MenuConstants;
import com.landleaf.oauth.domain.enums.RoleTypeEnum;
import com.landleaf.oauth.domain.request.RoleCreateRequest;
import com.landleaf.oauth.domain.request.RoleListRequest;
import com.landleaf.oauth.domain.request.RoleUpdateRequest;
import com.landleaf.oauth.domain.response.RoleListResponse;
import com.landleaf.oauth.domain.response.SimpleRoleListResponse;
import com.landleaf.oauth.domain.response.UserLoginSuccessResponse;
import com.landleaf.oauth.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

import static com.landleaf.oauth.domain.enums.ErrorCodeConstants.USER_NOT_PERMISSION;
import static com.landleaf.oauth.domain.enums.ErrorCodeConstants.USER_ROLE_EXISTS;

/**
 * 角色
 *
 * @author 张力方
 * @since 2023/6/2
 **/
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleEntityMapper roleEntityMapper;
    private final UserRoleEntityMapper userRoleEntityMapper;
    private final RoleMenuEntityMapper roleMenuEntityMapper;

    @Override
    public List<UserLoginSuccessResponse.UserRoleResponse> getRolesByCurrentLoginUser(Long userId) {
        return roleEntityMapper.getRolesByCurrentLoginUser(userId);
    }

    @Override
    public Page<RoleListResponse> getRoleListResponse(RoleListRequest request) {
        // 校验用户权限
        checkUserRole();
        // 判断当前用户是否是平台管理员
        if (isPlatformAdmin()) {
            TenantContext.setIgnore(true);
        }

        // 角色基本列表
        Page<RoleListResponse> roleListResponse = roleEntityMapper.getRoleListResponse(
                Page.of(request.getPageNo(), request.getPageSize()),
                request);
        List<RoleListResponse> records = roleListResponse.getRecords();
        for (RoleListResponse roleInfo : records) {
            Long roleId = roleInfo.getId();
            // 角色权限信息
            List<RoleListResponse.ModuleMenu> moduleMenuList = new ArrayList<>();
            List<RoleListResponse.ModuleMenuIds> moduleMenuIdsList = new ArrayList<>();
            List<RoleListResponse.Menu> roleModuleMenuList = roleEntityMapper.getRoleModuleMenuList(roleId);
            LinkedHashMap<Long, List<RoleListResponse.Menu>> moduleMenusMap = roleModuleMenuList.stream()
                    .collect(Collectors.groupingBy(RoleListResponse.Menu::getModuleId, LinkedHashMap::new, Collectors.toList()));
            moduleMenusMap.forEach((key, value) -> {
                RoleListResponse.ModuleMenu moduleMenu = new RoleListResponse.ModuleMenu();
                moduleMenu.setModuleId(key);
                RoleListResponse.Menu menu = value.get(0);
                moduleMenu.setModuleCode(menu.getModuleCode());
                moduleMenu.setModuleName(menu.getModuleName());
                List<RoleListResponse.Menu> menus = buildMenuTree(value);
                moduleMenu.setMenus(menus);
                moduleMenuList.add(moduleMenu);
                RoleListResponse.ModuleMenuIds moduleMenuIds = new RoleListResponse.ModuleMenuIds();
                moduleMenuIds.setModuleId(key);
                moduleMenuIds.setModuleCode(menu.getModuleCode());
                moduleMenuIds.setModuleName(moduleMenuIds.getModuleName());
                moduleMenuIds.setMenuIds(value.stream().map(RoleListResponse.Menu::getMenuId).toList());
                moduleMenuIdsList.add(moduleMenuIds);
            });
            roleInfo.setRolePermissions(moduleMenuList);
            roleInfo.setModuleMenuIds(moduleMenuIdsList);

            // 角色用户信息
            List<RoleListResponse.User> userList = roleEntityMapper.getRoleUserList(roleId);
            roleInfo.setUserList(userList);
            roleInfo.setUserNum(userList == null ? 0 : userList.size());
        }

        return roleListResponse;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createRole(RoleCreateRequest request) {
        // 校验用户权限
        checkUserRole();
        TenantContext.setIgnore(true);
        // 创建角色
        Long tenantId = request.getTenantId();
        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setTenantId(tenantId);
        roleEntity.setType(RoleTypeEnum.OTHER.getType());
        roleEntity.setStatus(request.getStatus());
        roleEntity.setName(request.getName());
        roleEntityMapper.insert(roleEntity);

        // 创建角色菜单权限
        List<Long> menuIds = request.getMenuIds();
        if (CollectionUtils.isEmpty(menuIds)) {
            return;
        }
        List<RoleMenuEntity> roleMenuEntityList = new ArrayList<>();
        for (Long menuId : menuIds) {
            RoleMenuEntity roleMenuEntity = new RoleMenuEntity();
            roleMenuEntity.setRoleId(roleEntity.getId());
            roleMenuEntity.setMenuId(menuId);
            roleMenuEntity.setTenantId(tenantId);
            roleMenuEntityList.add(roleMenuEntity);
        }
        roleMenuEntityMapper.insertBatchSomeColumn(roleMenuEntityList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRole(RoleUpdateRequest request) {
        // 校验用户权限
        checkUserRole();
        TenantContext.setIgnore(true);
        // 创建角色
        Long roleId = request.getId();
        RoleEntity roleEntity = roleEntityMapper.selectById(roleId);
        roleEntity.setStatus(request.getStatus());
        roleEntity.setName(request.getName());
        roleEntityMapper.updateById(roleEntity);

        // 变更角色菜单权限
        List<Long> menuIds = request.getMenuIds();
        roleMenuEntityMapper.delete(Wrappers.<RoleMenuEntity>lambdaQuery().eq(RoleMenuEntity::getRoleId, roleId));
        if (CollectionUtils.isEmpty(menuIds)) {
            return;
        }
        List<RoleMenuEntity> roleMenuEntityList = new ArrayList<>();
        for (Long menuId : menuIds) {
            RoleMenuEntity roleMenuEntity = new RoleMenuEntity();
            roleMenuEntity.setRoleId(roleEntity.getId());
            roleMenuEntity.setMenuId(menuId);
            roleMenuEntity.setTenantId(roleEntity.getTenantId());
            roleMenuEntityList.add(roleMenuEntity);
        }
        roleMenuEntityMapper.insertBatchSomeColumn(roleMenuEntityList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRole(Long roleId) {
        // 校验用户权限
        checkUserRole();
        // 如果角色下有关联用户，则不允许删除
        List<UserRoleEntity> userRoleEntities = userRoleEntityMapper.selectList(Wrappers.<UserRoleEntity>lambdaQuery()
                .eq(UserRoleEntity::getRoleId, roleId));
        if (!CollectionUtils.isEmpty(userRoleEntities)) {
            throw new ServiceException(USER_ROLE_EXISTS);
        }
        // 删除角色
        roleEntityMapper.deleteById(roleId);
        // 删除角色权限
        roleMenuEntityMapper.delete(Wrappers.<RoleMenuEntity>lambdaQuery().eq(RoleMenuEntity::getRoleId, roleId));
        // 删除角色用户
        userRoleEntityMapper.delete(Wrappers.<UserRoleEntity>lambdaQuery().eq(UserRoleEntity::getRoleId, roleId));
    }

    @Override
    public List<SimpleRoleListResponse> getSimpleRoleListByTenant(Long tenantId) {
        // 校验用户权限
        checkUserRole();
        TenantContext.setIgnore(true);
        // 判断当前用户是否是平台管理员
        if (!isPlatformAdmin()) {
            Long loginTenantId = LoginUserUtil.getLoginTenantId();
            if (!loginTenantId.equals(tenantId)) {
                throw new ServiceException(USER_NOT_PERMISSION);
            }
        }
        return roleEntityMapper.getSimpleRoleListByTenant(tenantId);
    }

    @Override
    public List<SimpleRoleListResponse> getSimpleRoleListResponse() {
        // 校验用户权限
        checkUserRole();
        return roleEntityMapper.getSimpleRoleListResponse();
    }

    private List<RoleListResponse.Menu> buildMenuTree(List<RoleListResponse.Menu> menuList) {
        if (CollectionUtils.isEmpty(menuList)) {
            return null;
        }
        List<RoleListResponse.Menu> menuTree = menuList.stream().filter(item -> item.getParentId().equals(MenuConstants.rootId)).toList();
        recursiveBuildRoleListResponse(menuTree, menuList);
        return menuTree;
    }

    private void recursiveBuildRoleListResponse(List<RoleListResponse.Menu> menuTree, List<RoleListResponse.Menu> menuList) {
        for (RoleListResponse.Menu menu : menuTree) {
            List<RoleListResponse.Menu> children = menuList.stream().filter(item -> item.getParentId().equals(menu.getMenuId())).toList();
            if (!CollectionUtils.isEmpty(children)) {
                menu.setChildren(children);
                recursiveBuildRoleListResponse(children, menuList);
            }
        }
    }

    /**
     * 判断当前用户是否是平台管理员
     *
     * @return 是 true
     */
    private boolean isPlatformAdmin() {
        Long userId = LoginUserUtil.getLoginUserId();
        List<UserRoleEntity> userRoleByRoleType = roleEntityMapper.getUserRoleByRoleType(RoleTypeEnum.PLATFORM.getType(), userId);
        return !CollectionUtils.isEmpty(userRoleByRoleType);
    }

    /**
     * 判断当前用户是否是租户管理员
     *
     * @return 是 true
     */
    private boolean isTenantAdmin() {
        Long userId = LoginUserUtil.getLoginUserId();
        List<UserRoleEntity> userRoleByRoleType = roleEntityMapper.getUserRoleByRoleType(RoleTypeEnum.TENANT.getType(), userId);
        return !CollectionUtils.isEmpty(userRoleByRoleType);
    }

    private void checkUserRole() {
        Boolean tenantAdmin = isTenantAdmin();
        if (tenantAdmin.equals(Boolean.FALSE)) {
            Boolean platformAdmin = isPlatformAdmin();
            if (platformAdmin.equals(Boolean.FALSE)) {
                throw new ServiceException(ErrorCodeConstants.USER_ROLE_NOT_EXISTS);
            }
        }
    }
}
