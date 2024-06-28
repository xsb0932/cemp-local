package com.landleaf.oauth.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.landleaf.comm.tenant.TenantContext;
import com.landleaf.comm.util.servlet.LoginUserUtil;
import com.landleaf.oauth.dal.mapper.TenantEntityMapper;
import com.landleaf.oauth.dal.mapper.UserEntityMapper;
import com.landleaf.oauth.dal.redis.TokenRedisDAO;
import com.landleaf.oauth.domain.entity.TenantEntity;
import com.landleaf.oauth.domain.entity.UserEntity;
import com.landleaf.oauth.domain.request.UserLoginRequest;
import com.landleaf.oauth.domain.response.ModuleMenuResponse;
import com.landleaf.oauth.domain.response.UserLoginSuccessResponse;
import com.landleaf.oauth.domain.response.UserPermissionsResponse;
import com.landleaf.oauth.service.AuthService;
import com.landleaf.oauth.service.MenuService;
import com.landleaf.oauth.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.landleaf.comm.exception.util.ServiceExceptionUtil.exception;
import static com.landleaf.oauth.domain.enums.ErrorCodeConstants.AUTH_LOGIN_BAD_CREDENTIALS;

/**
 * AuthServiceImpl
 *
 * @author 张力方
 * @since 2023/6/1
 **/
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserEntityMapper userEntityMapper;
    private final TokenRedisDAO tokenRedisDAO;
    private final MenuService menuService;
    private final RoleService roleService;
    private final TenantEntityMapper tenantEntityMapper;

    @Override
    public UserLoginSuccessResponse login(UserLoginRequest req) {
        // 登录忽略租户设置
        TenantContext.setIgnore(true);
        // 验证密码
        UserLoginSuccessResponse userLoginSuccessResponse = new UserLoginSuccessResponse();
        String mobileOrEmail = req.getMobileOrEmail();
        String reqPassword = req.getPassword();
        UserEntity user = userEntityMapper.selectOne(Wrappers.<UserEntity>lambdaQuery()
                .eq(UserEntity::getMobile, mobileOrEmail)
                .or().eq(UserEntity::getEmail, mobileOrEmail));
        if (ObjectUtils.isEmpty(user)) {
            throw exception(AUTH_LOGIN_BAD_CREDENTIALS);
        }
        String password = user.getPassword();
        String salt = user.getSalt();
        if (!SecureUtil.md5(salt + reqPassword).equals(password)) {
            throw exception(AUTH_LOGIN_BAD_CREDENTIALS);
        }
        // 登录成功，生成token
        String token = createToken();
        Long userId = user.getId();
        Long tenantId = user.getTenantId();
        tokenRedisDAO.set(user, token);
        // 获取基本用户信息
        UserLoginSuccessResponse.UserLoginResponse userLoginResponse =
                new UserLoginSuccessResponse.UserLoginResponse()
                        .setUserId(userId)
                        .setUsername(user.getUsername())
                        .setNickname(user.getNickname())
                        .setTenantId(tenantId)
                        .setToken(token);
        userLoginSuccessResponse.setAuthInfo(userLoginResponse);
        // 获取当前用户模块菜单
        List<ModuleMenuResponse> moduleMenuResponses = menuService.searchMouldMenuByLoginId(userId, tenantId);
        userLoginSuccessResponse.setModuleMenuInfo(moduleMenuResponses);
        // 获取当前用户角色
        List<UserLoginSuccessResponse.UserRoleResponse> roleInfo = roleService.getRolesByCurrentLoginUser(userId);
        userLoginSuccessResponse.setRoleInfo(roleInfo);
        // 查询用户所在租户的Logo
        TenantEntity tenantEntity = tenantEntityMapper.selectById(tenantId);
        Assert.notNull(tenantEntity, "目标租户不存在");
        userLoginSuccessResponse.setTenantLogo(tenantEntity.getLogo());
        return userLoginSuccessResponse;
    }

    @Override
    public void logout() {
        String token = LoginUserUtil.getToken();
        if (token != null) {
            tokenRedisDAO.delete(token);
        }
    }

    @Override
    public UserPermissionsResponse getModuleMenus() {
        UserPermissionsResponse userPermissionsResponse = new UserPermissionsResponse();
        Long userId = LoginUserUtil.getLoginUserId();
        UserEntity userEntity = userEntityMapper.selectById(userId);
        Assert.notNull(userEntity, "目标用户不存在");
        TenantEntity tenantEntity = tenantEntityMapper.selectById(userEntity.getTenantId());
        Assert.notNull(tenantEntity, "目标用户不存在");
        userPermissionsResponse.setTenantLogo(tenantEntity.getLogo());
        // 获取当前用户模块菜单
        TenantContext.setIgnore(true);
        List<ModuleMenuResponse> moduleMenuResponses = menuService.searchMouldMenuByLoginId(userId, userEntity.getTenantId());
        userPermissionsResponse.setModuleMenus(moduleMenuResponses);
        return userPermissionsResponse;
    }

    private String createToken() {
        return IdUtil.fastSimpleUUID();
    }
}
