package com.landleaf.oauth.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.text.CharSequenceUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.landleaf.comm.exception.ServiceException;
import com.landleaf.comm.tenant.TenantContext;
import com.landleaf.comm.util.servlet.LoginUserUtil;
import com.landleaf.oauth.dal.mapper.*;
import com.landleaf.oauth.domain.entity.MenuEntity;
import com.landleaf.oauth.domain.entity.MenuSystemEntity;
import com.landleaf.oauth.domain.entity.ModuleEntity;
import com.landleaf.oauth.domain.entity.UserRoleEntity;
import com.landleaf.oauth.domain.enums.OpenWithEnum;
import com.landleaf.oauth.domain.enums.RoleDefaultModuleEnum;
import com.landleaf.oauth.domain.enums.RoleTypeEnum;
import com.landleaf.oauth.domain.request.MenuCreateRequest;
import com.landleaf.oauth.domain.request.MenuUpdateRequest;
import com.landleaf.oauth.domain.response.MenuTabulationResponse;
import com.landleaf.oauth.domain.response.ModuleMenuResponse;
import com.landleaf.oauth.domain.response.ModuleMenuTabulationResponse;
import com.landleaf.oauth.domain.response.ModuleResponse;
import com.landleaf.oauth.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.landleaf.oauth.domain.enums.ErrorCodeConstants.*;

/**
 * 菜单业务接口实现
 *
 * @author yue lin
 * @since 2023/6/1 13:11
 */
@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {

    private final MenuEntityMapper menuEntityMapper;
    private final MenuSystemMapper menuSystemMapper;
    private final ModuleEntityMapper moduleEntityMapper;
    private final UserEntityMapper userEntityMapper;
    private final UserRoleEntityMapper userRoleEntityMapper;

    @Override
    public List<ModuleMenuTabulationResponse> searchMenuTabulationByTenant() {
        List<MenuEntity> menuEntities = menuEntityMapper.selectList(null);
        return menuEntityToResponse(menuEntities);
    }

    @Override
    public List<ModuleMenuTabulationResponse> searchMenuTabulations(String tenantId) {
        platformAdminIgnore();
        LambdaQueryWrapper<MenuEntity> wrapper = Wrappers.<MenuEntity>lambdaQuery()
                .eq(TenantContext.isIgnore(), MenuEntity::getTenantId, tenantId);
        List<MenuEntity> menuEntities = menuEntityMapper.selectList(wrapper);
        return menuEntityToResponse(menuEntities);
    }

    @Override
    public List<ModuleMenuTabulationResponse> searchMenuTabulationByUser() {
        List<Long> moduleIds = userEntityMapper.searchMenusByUser(LoginUserUtil.getLoginUserId());
        if (CollUtil.isEmpty(moduleIds)) {
            return new ArrayList<>();
        }
        return menuEntityToResponse(menuEntityMapper.selectBatchIds(moduleIds));
    }

    @Override
    public List<ModuleMenuResponse> searchMouldMenuByLoginId(Long userId, Long tenantId) {
//        List<Long> moduleIds = userEntityMapper.searchMenusByUser(userId);
        List<Long> moduleIds = userEntityMapper.recursionMenusByUser(userId, tenantId);
        if (CollUtil.isEmpty(moduleIds)) {
            return new ArrayList<>();
        }
        List<ModuleMenuTabulationResponse> moduleMenuTabulationResponses = menuEntityToResponse(menuEntityMapper.selectBatchIds(moduleIds));
        List<ModuleMenuResponse> moduleMenuResponses = BeanUtil.copyToList(moduleMenuTabulationResponses, ModuleMenuResponse.class);
        // 处理当前用户默认使用模块
        handleDefaultModule(userId, moduleMenuResponses);
        return moduleMenuResponses;
    }

    @Override
    public void createMenu(MenuCreateRequest menuCreateRequest) {
        Assert.isTrue(userEntityMapper.searchIsPlatformAdmin(LoginUserUtil.getLoginUserId()), () -> new ServiceException(USER_ROLE_NOT_EXISTS));
        platformAdminIgnore();
        MenuSystemEntity menuSystemEntity = menuCreateRequest.toEntity();
        long maxSort = menuSystemMapper.selectList(null).stream()
                .map(MenuSystemEntity::getSort)
                .max(Long::compare)
                .orElse(0L);
        menuSystemEntity.setSort(maxSort + 1);
        menuSystemMapper.insert(menuSystemEntity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMenu(MenuUpdateRequest menuUpdateRequest) {
        boolean isTenantAdmin = userEntityMapper.searchIsTenantAdmin(LoginUserUtil.getLoginUserId());
        boolean isPlatformAdmin = userEntityMapper.searchIsPlatformAdmin(LoginUserUtil.getLoginUserId());
        Assert.isTrue(isTenantAdmin || isPlatformAdmin, () -> new ServiceException(USER_ROLE_NOT_EXISTS));
        MenuEntity menu = menuEntityMapper.selectById(menuUpdateRequest.getId());
        Assert.notNull(menu, () -> new ServiceException(MENU_NOT_EXISTS));
        MenuEntity menuEntity = menuUpdateRequest.toEntity();
        boolean updatePathStatus = CharSequenceUtil.equals(OpenWithEnum.NEW_WINDOW.getValue(), menu.getOpenWith())
                || Objects.isNull(menuEntity.getPath())
                || CharSequenceUtil.equals(menu.getPath(), menuEntity.getPath());
        Assert.isTrue(updatePathStatus, () -> new ServiceException(MENU_PATH_CANNOT_BE_MODIFIED));
        menuEntityMapper.updateById(menuEntity);
    }

    @Override
    public void deleteMenu(Long menuId) {
        Assert.isTrue(userEntityMapper.searchIsTenantAdmin(LoginUserUtil.getLoginUserId()), () -> new ServiceException(USER_ROLE_NOT_EXISTS));
        platformAdminIgnore();
        MenuEntity menuEntity = menuEntityMapper.selectById(menuId);
        Assert.notNull(menuEntity, () -> new ServiceException(MENU_NOT_EXISTS));
        Assert.isFalse(
                menuEntityMapper.exists(Wrappers.<MenuEntity>lambdaQuery().eq(MenuEntity::getParentId, menuId)),
                () -> new ServiceException(MENU_EXISTS_CHILDREN)
        );
        menuEntityMapper.deleteById(menuId);
    }

    @Override
    public List<ModuleResponse> searchModules() {
        // 查询当前租户下拥有的moduleIds
        List<Long> moduleIds = menuEntityMapper.selectList(null)
                .stream()
                .map(MenuEntity::getModuleId)
                .distinct()
                .toList();
        return moduleEntityMapper.selectBatchIds(moduleIds)
                .stream()
                .map(ModuleResponse::fromEntity)
                .toList();
    }

    @Override
    public List<MenuTabulationResponse> searchModuleMenus(Long moduleId) {
        TenantContext.setIgnore(true);
        return menuEntityMapper.searchMenuTree(moduleId, TenantContext.getTenantId());
    }

    @Override
    public List<ModuleMenuTabulationResponse> searchMenuTabulation() {
        TenantContext.setIgnore(true);
        List<MenuSystemEntity> menuSystemEntities = menuSystemMapper.selectList(null);
        return menuSystemEntityToResponse(menuSystemEntities);
    }

    private List<ModuleMenuTabulationResponse> menuEntityToResponse(List<MenuEntity> menuEntities) {
        List<ModuleMenuTabulationResponse> moduleMenuTabulationResponses = new ArrayList<>();
        if (CollUtil.isNotEmpty(menuEntities)) {
            List<Long> moduleIds = menuEntities.stream()
                    .map(MenuEntity::getModuleId)
                    .distinct()
                    .toList();

            List<ModuleEntity> moduleEntities = moduleEntityMapper.selectList(
                    Wrappers.<ModuleEntity>lambdaQuery().in(ModuleEntity::getId, moduleIds)
            );

            for (ModuleEntity moduleEntity : moduleEntities) {
                List<ModuleMenuTabulationResponse.MenuResponse> menuResponses = new ArrayList<>();
                Map<Long, ModuleMenuTabulationResponse.MenuResponse> collect = menuEntities.stream()
                        .filter(it -> it.getModuleId().equals(moduleEntity.getId()))
                        .map(ModuleMenuTabulationResponse.MenuResponse::fromEntity)
                        .sorted(Comparator.comparing(ModuleMenuTabulationResponse.MenuResponse::getMenuSort))
                        .collect(Collectors.toMap(
                                ModuleMenuTabulationResponse.MenuResponse::getMenuId,
                                Function.identity(),
                                (v1, v2) -> v1,
                                LinkedHashMap::new
                        ));

                for (Map.Entry<Long, ModuleMenuTabulationResponse.MenuResponse> longMenuResponseEntry : collect.entrySet()) {
                    ModuleMenuTabulationResponse.MenuResponse menuResponse = longMenuResponseEntry.getValue();
                    ModuleMenuTabulationResponse.MenuResponse response = collect.get(menuResponse.getParentId());
                    if (Objects.isNull(response)) {
                        menuResponses.add(menuResponse);
                    } else {
                        response.getChildren().add(menuResponse);
                    }
                }
                ModuleMenuTabulationResponse moduleMenuResponse = ModuleMenuTabulationResponse.fromEntity(moduleEntity);
                moduleMenuResponse.setMenus(menuResponses);

                moduleMenuTabulationResponses.add(moduleMenuResponse);
            }
        }
        return moduleMenuTabulationResponses;
    }

    private List<ModuleMenuTabulationResponse> menuSystemEntityToResponse(List<MenuSystemEntity> menuEntities) {
        List<ModuleMenuTabulationResponse> moduleMenuTabulationResponses = new ArrayList<>();
        if (CollUtil.isNotEmpty(menuEntities)) {
            List<Long> moduleIds = menuEntities.stream()
                    .map(MenuSystemEntity::getModuleId)
                    .distinct()
                    .toList();

            List<ModuleEntity> moduleEntities = moduleEntityMapper.selectList(
                    Wrappers.<ModuleEntity>lambdaQuery().in(ModuleEntity::getId, moduleIds)
            );

            for (ModuleEntity moduleEntity : moduleEntities) {
                List<ModuleMenuTabulationResponse.MenuResponse> menuResponses = new ArrayList<>();
                Map<Long, ModuleMenuTabulationResponse.MenuResponse> collect = menuEntities.stream()
                        .filter(it -> it.getModuleId().equals(moduleEntity.getId()))
                        .map(ModuleMenuTabulationResponse.MenuResponse::fromSystemEntity)
                        .sorted(Comparator.comparing(ModuleMenuTabulationResponse.MenuResponse::getMenuSort))
                        .collect(Collectors.toMap(
                                ModuleMenuTabulationResponse.MenuResponse::getMenuId,
                                Function.identity(),
                                (v1, v2) -> v1,
                                LinkedHashMap::new
                        ));

                for (Map.Entry<Long, ModuleMenuTabulationResponse.MenuResponse> longMenuResponseEntry : collect.entrySet()) {
                    ModuleMenuTabulationResponse.MenuResponse menuResponse = longMenuResponseEntry.getValue();
                    ModuleMenuTabulationResponse.MenuResponse response = collect.get(menuResponse.getParentId());
                    if (Objects.isNull(response)) {
                        menuResponses.add(menuResponse);
                    } else {
                        response.getChildren().add(menuResponse);
                    }
                }
                ModuleMenuTabulationResponse moduleMenuResponse = ModuleMenuTabulationResponse.fromEntity(moduleEntity);
                moduleMenuResponse.setMenus(menuResponses);

                moduleMenuTabulationResponses.add(moduleMenuResponse);
            }
        }
        return moduleMenuTabulationResponses;
    }

    private void handleDefaultModule(Long userId, List<ModuleMenuResponse> moduleMenuResponses) {
        // 平台管理员角色
        List<UserRoleEntity> platform = userRoleEntityMapper.getByUserIdAndRoleType(userId, RoleTypeEnum.PLATFORM.getType());
        if (!CollectionUtils.isEmpty(platform)) {
            for (ModuleMenuResponse moduleMenuResponse : moduleMenuResponses) {
                String moduleCode = moduleMenuResponse.getModuleCode();
                if (RoleDefaultModuleEnum.PLATFORM.getModuleCode().equals(moduleCode)) {
                    moduleMenuResponse.setIsDefault(Boolean.TRUE);
                } else {
                    moduleMenuResponse.setIsDefault(Boolean.FALSE);
                }
            }
            return;
        }
        // 租户管理员角色
        List<UserRoleEntity> tenant = userRoleEntityMapper.getByUserIdAndRoleType(userId, RoleTypeEnum.TENANT.getType());
        if (!CollectionUtils.isEmpty(tenant)) {
            for (ModuleMenuResponse moduleMenuResponse : moduleMenuResponses) {
                String moduleCode = moduleMenuResponse.getModuleCode();
                if (RoleDefaultModuleEnum.TENANT.getModuleCode().equals(moduleCode)) {
                    moduleMenuResponse.setIsDefault(Boolean.TRUE);
                } else {
                    moduleMenuResponse.setIsDefault(Boolean.FALSE);
                }
            }
            return;
        }
        // 其他角色
        List<UserRoleEntity> other = userRoleEntityMapper.getByUserIdAndRoleType(userId, RoleTypeEnum.OTHER.getType());
        if (!CollectionUtils.isEmpty(other)) {
            for (ModuleMenuResponse moduleMenuResponse : moduleMenuResponses) {
                String moduleCode = moduleMenuResponse.getModuleCode();
                if (RoleDefaultModuleEnum.OTHER.getModuleCode().equals(moduleCode)) {
                    moduleMenuResponse.setIsDefault(Boolean.TRUE);
                } else {
                    moduleMenuResponse.setIsDefault(Boolean.FALSE);
                }
            }
        }
    }

    /**
     * 平台管理员设置忽略租户
     */
    private void platformAdminIgnore() {
        if (userEntityMapper.searchIsPlatformAdmin(LoginUserUtil.getLoginUserId())) {
            TenantContext.setIgnore(true);
        }
    }

}
