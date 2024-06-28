package com.landleaf.oauth.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.text.CharSequenceUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.landleaf.bms.api.DictApi;
import com.landleaf.bms.api.ManagementNodeApi;
import com.landleaf.bms.api.dto.DictDataResponse;
import com.landleaf.bms.api.dto.ManagementNodeRootCreateRequest;
import com.landleaf.comm.tenant.TenantContext;
import com.landleaf.comm.util.servlet.LoginUserUtil;
import com.landleaf.mail.domain.param.UserPasswordMail;
import com.landleaf.mail.service.MailService;
import com.landleaf.oauth.dal.mapper.*;
import com.landleaf.oauth.dal.redis.TokenRedisDAO;
import com.landleaf.oauth.domain.entity.*;
import com.landleaf.oauth.domain.enums.RoleTypeEnum;
import com.landleaf.oauth.domain.request.*;
import com.landleaf.oauth.domain.response.TenantInfoResponse;
import com.landleaf.oauth.domain.response.TenantSelectiveResponse;
import com.landleaf.oauth.domain.response.TenantTabulationResponse;
import com.landleaf.oauth.domain.response.TenantValidationResponse;
import com.landleaf.oauth.password.PasswordUtil;
import com.landleaf.oauth.service.TenantService;
import com.landleaf.pgsql.core.BizSequenceService;
import com.landleaf.pgsql.enums.BizSequenceEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.landleaf.comm.constance.OauthConstant.*;

/**
 * 租户业务实现
 *
 * @author yue lin
 * @since 2023/6/1 9:36
 */
@Service
@RequiredArgsConstructor
public class TenantServiceImpl implements TenantService {

    private final TenantEntityMapper tenantEntityMapper;
    private final UserEntityMapper userEntityMapper;
    private final MenuEntityMapper menuEntityMapper;
    private final MenuSystemMapper menuSystemMapper;
    private final RoleEntityMapper roleEntityMapper;
    private final UserRoleEntityMapper userRoleEntityMapper;
    private final RoleMenuEntityMapper roleMenuEntityMapper;
    private final BizSequenceService bizSequenceService;
    private final TokenRedisDAO tokenRedisDAO;
    private final ManagementNodeApi managementNodeApi;
    private final MailService mailService;
    private final DictApi dictApi;

    @Override
    public IPage<TenantTabulationResponse> searchTenantTabulation(TenantTabulationRequest tenantTabulationRequest) {
        platformAdminIgnore();
        return tenantEntityMapper.searchTenantTabulation(
                Page.of(tenantTabulationRequest.getPageNo(), tenantTabulationRequest.getPageSize()),
                tenantTabulationRequest);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTenant(Long tenantId) {
        Assert.isTrue(userEntityMapper.searchIsPlatformAdmin(LoginUserUtil.getLoginUserId()), "权限不足");
        TenantContext.setIgnore(true);
        Assert.notNull(tenantId, "删除租户时，参数异常");
        Assert.notNull(tenantEntityMapper.selectById(tenantId), "目标租户不存在");
        tenantEntityMapper.deleteById(tenantId);
        //删除租户下的用户
        deleteTenantUsers(tenantId);
        // 删除租户下角色
        deleteTenantRoles(tenantId);
        // 删除租户下菜单
        deleteTenantMenus(tenantId);
        // 删除关联表
        deleteTenantAssociateTables(tenantId);
        // 踢出当前租户下所有用户
        kickOutUser(tenantId);
        // 删除关联租户字典
        dictApi.deleteTenantDictData(tenantId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createTenant(TenantCreateRequest tenantCreateRequest) {
        Assert.isTrue(userEntityMapper.searchIsPlatformAdmin(LoginUserUtil.getLoginUserId()), "权限不足");
        TenantContext.setIgnore(true);
        // 创建租户基本信息
        TenantCreateRequest.Tenant tenant = tenantCreateRequest.getTenant();
        TenantEntity tenantEntity = tenant.toTenantEntity();
        // 判断租户code是否存在
        verifyTenantName(tenantEntity.getName(), null);
        verifyTenantCode(tenantEntity.getCode(), null);
        verifySocialCreditCode(tenantEntity.getSocialCreditCode(), null);
        tenantEntity.setStatus(ENABLE_STATUS);
        tenantEntity.setBizTenantId(bizSequenceService.next(BizSequenceEnum.TENANT));
        tenantEntityMapper.insert(tenantEntity);
        Long tenantEntityId = tenantEntity.getId();

        // 创建管理员用户
        UserEntity userEntity = tenant.toUserEntity();
        // 判断租户管理员手机号邮箱是否存在
        Assert.isFalse(userEntityMapper.exists(Wrappers.<UserEntity>lambdaQuery()
                .eq(UserEntity::getMobile, userEntity.getMobile())
                .or()
                .eq(UserEntity::getEmail, userEntity.getEmail())
                .or()
                .eq(UserEntity::getUsername, userEntity.getUsername())
        ), "管理员用户名、手机号或邮箱已存在");
        userEntity.setTenantId(tenantEntityId);
        String generatePassword = PasswordUtil.generatePassword(userEntity);
        userEntityMapper.insert(userEntity);
        Long userEntityId = userEntity.getId();

        // 回写租户管理员
        tenantEntity.setAdminId(userEntityId);
        tenantEntityMapper.updateById(tenantEntity);

        // 角色创建
        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setName("租户管理员");
        roleEntity.setStatus(ENABLE_STATUS);
        roleEntity.setType(TENANT_ADMIN_ROLE_TYPE);
        roleEntity.setTenantId(tenantEntityId);
        roleEntityMapper.insert(roleEntity);
        Long roleEntityId = roleEntity.getId();

        // 用户角色关联
        UserRoleEntity userRole = new UserRoleEntity();
        userRole.setUserId(userEntityId);
        userRole.setRoleId(roleEntityId);
        userRole.setTenantId(tenantEntityId);
        userRoleEntityMapper.insert(userRole);

        // 租户管理员菜单权限处理
        TenantCreateRequest.Menu menu = tenantCreateRequest.getMenu();
        if (CollUtil.isNotEmpty(menu.getPermissions())) {
            Map<Long, List<MenuEntity>> menuMap = menuEntityMapper.recursionMenuByPermission(menu.getPermissions(),
                            TenantContext.getTenantId())
                    .stream()
                    .peek(it -> it.setCreateTime(null))
                    .peek(it -> it.setUpdateTime(null))
                    .peek(it -> it.setTenantId(tenantEntityId))
                    .collect(Collectors.groupingBy(MenuEntity::getParentId));

            List<Long> keys = menuMap.keySet().stream().sorted().toList();

            Map<Long, Long> idMap = new HashMap<>();
            for (Long key : keys) {
                for (MenuEntity menuEntity : menuMap.get(key)) {
                    Long idOld = menuEntity.getId();
                    menuEntity.setId(null);
                    if (menuEntity.getParentId() != 0L) {
                        menuEntity.setParentId(idMap.get(menuEntity.getParentId()));
                    }
                    Long idNeo = createMenu(menuEntity);
                    idMap.put(idOld, idNeo);
                }
            }

            List<RoleMenuEntity> roleMenuEntityList = idMap.values().stream()
                    .map(it -> {
                        RoleMenuEntity roleMenuEntity = new RoleMenuEntity();
                        roleMenuEntity.setRoleId(roleEntityId);
                        roleMenuEntity.setMenuId(it);
                        roleMenuEntity.setTenantId(tenantEntityId);
                        return roleMenuEntity;
                    }).toList();
            roleMenuEntityMapper.insertBatchSomeColumn(roleMenuEntityList);
        }

        // 创建租户根管理节点
        ManagementNodeRootCreateRequest managementNodeRootCreateRequest = new ManagementNodeRootCreateRequest();
        managementNodeRootCreateRequest.setTenantId(tenantEntityId);
        managementNodeRootCreateRequest.setTenantCode(tenantEntity.getCode());
        managementNodeRootCreateRequest.setTenantName(tenantEntity.getName());
        managementNodeRootCreateRequest.setTenantAdminId(tenantEntity.getAdminId());
        managementNodeApi.createTenantRootNode(managementNodeRootCreateRequest);

        // 初始化租户数据字典
        dictApi.initTenantDictData(tenantEntityId);

        // 邮件发送
        mailService.sendMailAsync(UserPasswordMail.tenantCreate(userEntity.getEmail(), userEntity.getEmail(),
                userEntity.getMobile(), generatePassword));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTenantBasic(TenantBasicUpdateRequest tenantBasicUpdateRequest) {
        Long loginUserId = LoginUserUtil.getLoginUserId();
        //Assert.isTrue(userEntityMapper.searchIsPlatformAdmin(loginUserId), "权限不足");
        TenantContext.setIgnore(true);
        TenantEntity tenantEntity = tenantBasicUpdateRequest.toTenantEntity();
        Long tenantEntityId = tenantEntity.getId();
        Assert.notNull(tenantEntityMapper.selectById(tenantEntityId), "目标租户不存在");
        verifyTenantName(tenantEntity.getName(), tenantEntityId);
        tenantEntityMapper.updateById(tenantEntity);

        // 进行菜单权限更新
        // 更新后需要有的权限
        List<String> permissions = tenantBasicUpdateRequest.getPermissions();
        List<MenuSystemEntity> menuFuture = menuSystemMapper.recursionMenuByPermission(permissions);
        List<String> menuPermissionFuture = menuFuture.stream().map(MenuSystemEntity::getPermission).distinct().toList();

        List<MenuEntity> menuOld = menuEntityMapper.selectList(
                Wrappers.<MenuEntity>lambdaQuery().eq(MenuEntity::getTenantId, tenantEntityId));
        List<String> menuPermissionOld = menuOld.stream().map(MenuEntity::getPermission).distinct().toList();

        List<String> tenantSpecialPermission = menuEntityMapper.selectTenantSpecialPermission(tenantEntityId);

        // 需要新增的菜单
        List<Long> addMenus = menuFuture
                .stream()
                .filter(it -> !menuPermissionOld.contains(it.getPermission()))
                .map(MenuSystemEntity::getId)
                .toList();
        // 需要删除的菜单
        List<Long> deleteMenus = menuOld
                .stream()
                .filter(o -> !tenantSpecialPermission.contains(o.getPermission()))
                .filter(it -> !menuPermissionFuture.contains(it.getPermission()))
                .map(MenuEntity::getId)
                .toList();

        if (CollectionUtil.isNotEmpty(deleteMenus)) {
//            //排除 标准功能
//            List<Long> menus = menuEntityMapper.excludeSysMenu(deleteMenus);
//            if(CollectionUtil.isNotEmpty(menus)){
            menuEntityMapper.deleteBatchIds(deleteMenus);
            roleMenuEntityMapper.delete(Wrappers.<RoleMenuEntity>lambdaQuery().in(RoleMenuEntity::getMenuId, deleteMenus));
//            }
        }

        Map<Long, Long> idMap = new HashMap<>();
        if (CollectionUtil.isNotEmpty(addMenus)) {
            List<MenuSystemEntity> menuEntities = menuSystemMapper.selectBatchIds(addMenus)
                    .stream()
                    .sorted(Comparator.comparingLong(MenuSystemEntity::getParentId))
                    .toList();
            for (MenuSystemEntity menuSystemEntity : menuEntities) {
                Long idOld = menuSystemEntity.getId();
                MenuEntity deletedMenu = menuEntityMapper.searchDeletedMenu(menuSystemEntity.getPermission(), tenantEntityId);
                if (Objects.nonNull(deletedMenu)) {
                    menuEntityMapper.recoverDeletedMenu(deletedMenu.getId());
                    idMap.put(idOld, deletedMenu.getId());
                } else {
                    MenuEntity menuEntity = new MenuEntity();
                    BeanUtils.copyProperties(menuSystemEntity, menuEntity);
                    menuEntity.setId(null);
                    menuEntity.setTenantId(tenantEntityId);
                    if (menuEntity.getParentId() != 0L) {
                        MenuSystemEntity entity = menuSystemMapper.selectById(menuSystemEntity.getParentId());
                        MenuEntity selectOne = menuEntityMapper.selectOne(
                                Wrappers.<MenuEntity>lambdaQuery()
                                        .eq(MenuEntity::getTenantId, tenantEntityId)
                                        .eq(MenuEntity::getPermission, entity.getPermission())
                        );
                        if (Objects.isNull(selectOne)) {
                            menuEntity.setParentId(idMap.get(menuEntity.getParentId()));
                        } else {
                            menuEntity.setParentId(selectOne.getId());
                        }
                    }
                    Long idNeo = createMenu(menuEntity);
                    idMap.put(idOld, idNeo);
                }
            }
        }

        // 查询租户管理员
        Long id = tenantBasicUpdateRequest.getId();
        Long roleId;
        if (id.equals(1L)) {
            roleId = roleEntityMapper.selectOne(
                    Wrappers.<RoleEntity>lambdaQuery()
                            .eq(RoleEntity::getTenantId, tenantEntityId)
                            .eq(RoleEntity::getType, RoleTypeEnum.PLATFORM.getType())
            ).getId();
        } else {
            roleId = roleEntityMapper.selectOne(
                    Wrappers.<RoleEntity>lambdaQuery()
                            .eq(RoleEntity::getTenantId, tenantEntityId)
                            .eq(RoleEntity::getType, RoleTypeEnum.TENANT.getType())
            ).getId();
        }
        List<RoleMenuEntity> roleMenuEntityList = idMap.values()
                .stream()
                .map(it -> {
                    RoleMenuEntity roleMenuEntity = new RoleMenuEntity();
                    roleMenuEntity.setRoleId(roleId);
                    roleMenuEntity.setMenuId(it);
                    roleMenuEntity.setTenantId(tenantEntityId);
                    return roleMenuEntity;
                }).toList();
        if (CollectionUtil.isNotEmpty(roleMenuEntityList)) {
            for (RoleMenuEntity roleMenuEntity : roleMenuEntityList) {
                MenuEntity menuEntity = roleMenuEntityMapper.searchRoleMenu(roleId, roleMenuEntity.getMenuId(), tenantEntityId);
                if (Objects.nonNull(menuEntity)) {
                    if (menuEntity.getDeleted() == 1) {
                        roleMenuEntityMapper.recoverDeletedRoleMenu(menuEntity.getId());
                    }
                } else {
                    roleMenuEntityMapper.insert(roleMenuEntity);
                }
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTenantStatus(Long tenantId, Short disableStatus) {
        Assert.notNull(tenantId, "参数异常");
        Assert.checkBetween(disableStatus, ENABLE_STATUS, DISABLE_STATUS, "参数异常");
        Assert.isTrue(userEntityMapper.searchIsPlatformAdmin(LoginUserUtil.getLoginUserId()), "权限不足");
        TenantContext.setIgnore(true);
        TenantEntity tenantEntity = tenantEntityMapper.selectById(tenantId);

        Assert.isFalse(Objects.equals(tenantEntity.getStatus(), disableStatus), "租户不需要变更状态");
        tenantEntity.setStatus(disableStatus);
        tenantEntityMapper.updateById(tenantEntity);
        // TODO 是否禁用/启用租户下所有租户，如果禁用，是否踢出所有登陆用户
    }

    @Override
    public TenantInfoResponse searchTenantBasicInfo(Long tenantId) {
        Assert.notNull(tenantId, "参数异常");
        TenantContext.setIgnore(true);
        Assert.notNull(tenantEntityMapper.selectById(tenantId), "目标租户不存在");

        // 由于测试想要租户菜单分配页面效果与角色菜单页面分配效果一致（其实产品设计的租户菜单分配和角色分配逻辑是不一样的）而新加的逻辑。ε=(´ο｀*)))唉
        List<String> unShowPermissions = menuEntityMapper.selectTenantUnShowPermissions(tenantId);

        Map<Long, List<String>> modulePermissions = menuEntityMapper.selectList(Wrappers.<MenuEntity>lambdaQuery().eq(MenuEntity::getTenantId, tenantId))
                .stream()
                .filter(menuEntity -> !unShowPermissions.contains(menuEntity.getPermission()))
                .collect(Collectors.groupingBy(
                        MenuEntity::getModuleId,
                        Collectors.mapping(MenuEntity::getPermission, Collectors.toList())
                ));

        TenantInfoResponse tenantInfoResponse = tenantEntityMapper.searchTenantBasicInfo(tenantId);
        Map<String, String> cycleTypeDicMap = dictApi.getDictDataList("REPORTING_CYCLE").getCheckedData().stream().collect(Collectors.toMap(DictDataResponse::getValue, DictDataResponse::getLabel));
        tenantInfoResponse.setReportingCycleDesc(cycleTypeDicMap.get(tenantInfoResponse.getReportingCycle()));

        tenantInfoResponse.setModulePermissions(modulePermissions);
        return tenantInfoResponse;
    }

    @Override
    public List<TenantSelectiveResponse> searchTenantSelective() {
        platformAdminIgnore();
        Long loginUserId = LoginUserUtil.getLoginUserId();
        if (userEntityMapper.searchIsPlatformAdmin(loginUserId)) {
            return tenantEntityMapper.selectList(null)
                    .stream()
                    .map(TenantSelectiveResponse::fromEntity)
                    .toList();
        } else if (userEntityMapper.searchIsTenantAdmin(loginUserId)) {
            return Lists.newArrayList(TenantSelectiveResponse.fromEntity(tenantEntityMapper.selectById(TenantContext.getTenantId())));
        } else {
            throw new RuntimeException("权限不足");
        }
    }

    @Override
    public TenantInfoResponse searchEnterpriseBasic(Long tenantId) {
        boolean isPlatformAdmin = userEntityMapper.searchIsPlatformAdmin(LoginUserUtil.getLoginUserId());
        TenantContext.setIgnore(true);
        if (Objects.isNull(tenantId) || !isPlatformAdmin) {
            tenantId = TenantContext.getTenantId();
        }
        TenantEntity tenantEntity = tenantEntityMapper.selectById(tenantId);
        Assert.notNull(tenantEntity, "目标不存在");
        TenantInfoResponse resp = tenantEntityMapper.searchTenantBasicInfo(tenantId);
        Map<String, String> cycleTypeDicMap = dictApi.getDictDataList("REPORTING_CYCLE").getCheckedData().stream().collect(Collectors.toMap(DictDataResponse::getValue, DictDataResponse::getLabel));
        resp.setReportingCycleDesc(cycleTypeDicMap.get(resp.getReportingCycle()));
        return resp;
    }

    @Override
    public void updateEnterprise(EnterpriseUpdateRequest request) {
        Long loginUserId = LoginUserUtil.getLoginUserId();
        boolean isPlatformAdmin = userEntityMapper.searchIsPlatformAdmin(loginUserId);
        boolean isTenantAdmin = userEntityMapper.searchIsTenantAdmin(loginUserId);
        Assert.isTrue(isTenantAdmin || isPlatformAdmin, "权限不足");
        if (isPlatformAdmin) {
            TenantContext.setIgnore(true);
        }
        TenantEntity tenantEntity = request.toEntity();
        Assert.notNull(tenantEntityMapper.selectById(request.getId()), "目标不存在");
        verifyTenantCode(tenantEntity.getCode(), tenantEntity.getId());
        verifyTenantName(tenantEntity.getName(), tenantEntity.getId());
        tenantEntityMapper.updateById(tenantEntity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changeEnterpriseAdmin(EnterpriseAdminChangeRequest request) {
        TenantContext.setIgnore(true);
        Long loginUserId = LoginUserUtil.getLoginUserId();
        Long tenantId = Objects.isNull(request.getTenantId()) ? TenantContext.getTenantId() : request.getTenantId();

        Long neoAdminId = request.getUserId();
        Long roleId = request.getRoleId();

        boolean isPlatformAdmin = userEntityMapper.searchIsPlatformAdmin(loginUserId);
        boolean isTenantAdmin = userEntityMapper.searchIsTenantAdmin(loginUserId);
        Assert.isTrue(isTenantAdmin || isPlatformAdmin, "权限不足");
        Assert.isTrue(isPlatformAdmin || tenantId.equals(TenantContext.getTenantId()), "没有目标租户权限");
        Assert.isFalse(neoAdminId.equals(loginUserId), "管理员移交目标用户与当前管理员相同");

        TenantEntity tenantEntity = tenantEntityMapper.selectById(tenantId);
        Assert.notNull(tenantEntity, "目标不存在");
        // 变更租户管理员
        Long oldAdminId = tenantEntity.getAdminId();
        tenantEntity.setAdminId(neoAdminId);
        tenantEntityMapper.updateById(tenantEntity);

        // 判断角色是否属于租户
        LambdaQueryWrapper<RoleEntity> wrapper = Wrappers.<RoleEntity>lambdaQuery()
                .eq(RoleEntity::getTenantId, tenantId)
                .eq(RoleEntity::getId, roleId);
        Assert.isTrue(roleEntityMapper.exists(wrapper), "角色不存在于目标租户");

        // 查询当前租户管理员角色信息
        LambdaQueryWrapper<RoleEntity> queryWrapper = Wrappers.<RoleEntity>lambdaQuery()
                .eq(RoleEntity::getTenantId, tenantId)
                .in(RoleEntity::getType, RoleTypeEnum.TENANT.getType(), RoleTypeEnum.PLATFORM.getType());
        RoleEntity roleEntity = roleEntityMapper.selectOne(queryWrapper);
        Assert.isFalse(roleId.equals(roleEntity.getId()), "无法获取管理员权限");
        LambdaQueryWrapper<UserRoleEntity> lambdaQueryWrapper = Wrappers.<UserRoleEntity>lambdaQuery()
                .eq(UserRoleEntity::getTenantId, tenantId)
                .eq(UserRoleEntity::getUserId, oldAdminId)
                .eq(UserRoleEntity::getRoleId, roleEntity.getId());
        // 删除之前的管理员-用户角色关联表
        userRoleEntityMapper.delete(lambdaQueryWrapper);
        // 插入新的用户角色关联表
        // 判断当前管理员是否拥有目标角色ID
        boolean exists = userRoleEntityMapper.exists(
                Wrappers.<UserRoleEntity>lambdaQuery()
                        .eq(UserRoleEntity::getUserId, oldAdminId)
                        .eq(UserRoleEntity::getRoleId, roleId)
        );
        // 如果已经拥有，则不再添加
        if (!exists) {
            UserRoleEntity userRoleEntity = new UserRoleEntity();
            userRoleEntity.setTenantId(tenantId);
            userRoleEntity.setUserId(oldAdminId);
            userRoleEntity.setRoleId(roleId);
            userRoleEntityMapper.insert(userRoleEntity);
        }

        UserRoleEntity userRoleEntity1 = new UserRoleEntity();
        userRoleEntity1.setTenantId(tenantId);
        userRoleEntity1.setUserId(neoAdminId);
        userRoleEntity1.setRoleId(roleEntity.getId());
        userRoleEntityMapper.insert(userRoleEntity1);

        // 踢出两个用户，重新登陆
        tokenRedisDAO.kickOutUser(neoAdminId);
        tokenRedisDAO.kickOutUser(oldAdminId);
    }

    @Override
    public TenantValidationResponse validationParam(TenantValidationRequest request) {
        TenantContext.setIgnore(true);
        Long tenantId = request.getTenantId();
        TenantValidationResponse validationResponse = new TenantValidationResponse();
        Long tenantAdminUser = null;
        if (Objects.nonNull(tenantId)) {
            TenantEntity tenantEntity = tenantEntityMapper.selectById(tenantId);
            Assert.notNull(tenantEntity, "租户不存在");
            tenantAdminUser = tenantEntity.getAdminId();
        }
        if (CharSequenceUtil.isNotBlank(request.getCode())) {
            validationResponse.setCode(!tenantEntityMapper.exists(Wrappers.<TenantEntity>lambdaQuery()
                    .eq(TenantEntity::getCode, request.getCode())
                    .ne(Objects.nonNull(tenantId), TenantEntity::getId, tenantId)));
        }
        if (CharSequenceUtil.isNotBlank(request.getName())) {
            validationResponse.setName(!tenantEntityMapper.exists(Wrappers.<TenantEntity>lambdaQuery()
                    .eq(TenantEntity::getName, request.getName())
                    .ne(Objects.nonNull(tenantId), TenantEntity::getId, tenantId)));
        }
        if (CharSequenceUtil.isNotBlank(request.getSocialCreditCode())) {
            validationResponse.setSocialCreditCode(!tenantEntityMapper.exists(Wrappers.<TenantEntity>lambdaQuery()
                    .eq(TenantEntity::getSocialCreditCode, request.getSocialCreditCode())
                    .ne(Objects.nonNull(tenantId), TenantEntity::getId, tenantId)));
        }
        if (CharSequenceUtil.isNotBlank(request.getAdminUserName())) {
            validationResponse.setAdminUserName(!userEntityMapper.exists(Wrappers.<UserEntity>lambdaQuery()
                    .eq(UserEntity::getUsername, request.getAdminUserName())
                    .ne(Objects.nonNull(tenantId), UserEntity::getId, tenantAdminUser)));
        }
        if (CharSequenceUtil.isNotBlank(request.getAdminMobile())) {
            validationResponse.setAdminMobile(!userEntityMapper.exists(Wrappers.<UserEntity>lambdaQuery()
                    .eq(UserEntity::getMobile, request.getAdminMobile())
                    .ne(Objects.nonNull(tenantId), UserEntity::getId, tenantAdminUser)));
        }
        if (CharSequenceUtil.isNotBlank(request.getAdminEmail())) {
            validationResponse.setAdminEmail(!userEntityMapper.exists(Wrappers.<UserEntity>lambdaQuery()
                    .eq(UserEntity::getEmail, request.getAdminEmail())
                    .ne(Objects.nonNull(tenantId), UserEntity::getId, tenantAdminUser)));
        }
        return validationResponse;
    }

    /**
     * 踢出租户下所有的用户
     *
     * @param tenantId 租户ID
     */
    private void kickOutUser(Long tenantId) {
        tokenRedisDAO.kickOutTenantUser(tenantId);
    }

    /**
     * 删除租户下的用户
     *
     * @param tenantId 租户
     */
    private void deleteTenantUsers(Long tenantId) {
        List<Long> userIds = userEntityMapper.selectList(Wrappers.<UserEntity>lambdaQuery().eq(UserEntity::getTenantId, tenantId))
                .stream()
                .map(UserEntity::getId)
                .toList();
        if (CollectionUtil.isNotEmpty(userIds)) {
            userEntityMapper.deleteBatchIds(userIds);
        }
    }

    /**
     * 删除租户下的角色
     *
     * @param tenantId 租户
     */
    private void deleteTenantRoles(Long tenantId) {
        List<Long> roles = roleEntityMapper.selectList(Wrappers.<RoleEntity>lambdaQuery().eq(RoleEntity::getTenantId, tenantId))
                .stream()
                .map(RoleEntity::getId)
                .toList();
        if (CollectionUtil.isNotEmpty(roles)) {
            roleEntityMapper.deleteBatchIds(roles);
        }
    }

    /**
     * 删除用户角色、角色菜单关联表
     *
     * @param tenantId 租户ID
     */
    private void deleteTenantAssociateTables(Long tenantId) {
        List<Long> ids = userRoleEntityMapper.selectList(Wrappers.<UserRoleEntity>lambdaQuery().eq(UserRoleEntity::getTenantId, tenantId))
                .stream()
                .map(UserRoleEntity::getId)
                .toList();
        if (CollectionUtil.isNotEmpty(ids)) {
            userRoleEntityMapper.deleteBatchIds(ids);
        }
        List<Long> ids2 = roleMenuEntityMapper.selectList(Wrappers.<RoleMenuEntity>lambdaQuery().eq(RoleMenuEntity::getTenantId, tenantId))
                .stream()
                .map(RoleMenuEntity::getId)
                .toList();
        if (CollectionUtil.isNotEmpty(ids2)) {
            roleMenuEntityMapper.deleteBatchIds(ids2);
        }
    }

    /**
     * 删除租户下的菜单
     *
     * @param tenantId 租户
     */
    private void deleteTenantMenus(Long tenantId) {
        List<Long> menus = menuEntityMapper.selectList(Wrappers.<MenuEntity>lambdaQuery().eq(MenuEntity::getTenantId, tenantId))
                .stream()
                .map(MenuEntity::getId)
                .toList();
        if (CollectionUtil.isNotEmpty(menus)) {
            menuEntityMapper.deleteBatchIds(menus);
        }
    }

    /**
     * 给租户管理员创建菜单
     *
     * @param menuEntity 实体
     * @return 新的ID
     */
    private Long createMenu(MenuEntity menuEntity) {
//        LambdaQueryWrapper<MenuEntity> wrapper = Wrappers.<MenuEntity>lambdaQuery()
//                .eq(MenuEntity::getModuleId, menuEntity.getModuleId())
//                .eq(MenuEntity::getParentId, menuEntity.getParentId())
//                .eq(TenantBaseEntity::getTenantId, menuEntity.getTenantId());
//        Long sortNow = menuEntityMapper.selectList(wrapper)
//                .stream()
//                .map(MenuEntity::getSort)
//                .max(Long::compareTo)
//                .map(it -> it + 1)
//                .orElse(1L);
//        menuEntity.setSort(sortNow);
        menuEntityMapper.insert(menuEntity);
        return menuEntity.getId();
    }

    /**
     * 校验租户名称是否存在
     *
     * @param tenantName 租户名称
     */
    private void verifyTenantName(String tenantName, Long tenantId) {
        boolean exists = tenantEntityMapper.exists(Wrappers.<TenantEntity>lambdaQuery()
                .eq(TenantEntity::getName, tenantName)
                .ne(Objects.nonNull(tenantId), TenantEntity::getId, tenantId)
        );
        Assert.isFalse(exists, "租户名称已存在");
    }

    /**
     * 校验租户编码是否存在
     *
     * @param tenantCode 租户编码
     */
    private void verifyTenantCode(String tenantCode, Long tenantId) {
        boolean exists = tenantEntityMapper.exists(Wrappers.<TenantEntity>lambdaQuery()
                .eq(TenantEntity::getCode, tenantCode)
                .ne(Objects.nonNull(tenantId), TenantEntity::getId, tenantId)
        );
        Assert.isFalse(exists, "租户编码已存在");
    }

    /**
     * 校验租户社会信用代码是否存在
     *
     * @param socialCreditCode 社会信用代码
     */
    private void verifySocialCreditCode(String socialCreditCode, Long tenantId) {
        boolean exists = tenantEntityMapper.exists(Wrappers.<TenantEntity>lambdaQuery()
                .eq(TenantEntity::getSocialCreditCode, socialCreditCode)
                .ne(Objects.nonNull(tenantId), TenantEntity::getId, tenantId)
        );
        Assert.isFalse(exists, "租户社会信用代码已存在");
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
