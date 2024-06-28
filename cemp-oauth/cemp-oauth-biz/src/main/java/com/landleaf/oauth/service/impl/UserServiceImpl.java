package com.landleaf.oauth.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.landleaf.bms.api.UserManagementNodeApi;
import com.landleaf.bms.api.dto.UserManageNodeIdsResponse;
import com.landleaf.bms.api.dto.UserManageNodeResponse;
import com.landleaf.bms.api.dto.UserNodeUpdateRequest;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.comm.exception.BusinessException;
import com.landleaf.comm.exception.ServiceException;
import com.landleaf.comm.exception.enums.GlobalErrorCodeConstants;
import com.landleaf.comm.license.LicenseCheck;
import com.landleaf.comm.tenant.TenantContext;
import com.landleaf.comm.util.servlet.LoginUserUtil;
import com.landleaf.mail.domain.param.UserCodeMail;
import com.landleaf.mail.domain.param.UserPasswordMail;
import com.landleaf.mail.service.MailService;
import com.landleaf.oauth.dal.mapper.RoleEntityMapper;
import com.landleaf.oauth.dal.mapper.TenantEntityMapper;
import com.landleaf.oauth.dal.mapper.UserEntityMapper;
import com.landleaf.oauth.dal.mapper.UserRoleEntityMapper;
import com.landleaf.oauth.dal.redis.TokenRedisDAO;
import com.landleaf.oauth.domain.entity.RoleEntity;
import com.landleaf.oauth.domain.entity.TenantEntity;
import com.landleaf.oauth.domain.entity.UserEntity;
import com.landleaf.oauth.domain.entity.UserRoleEntity;
import com.landleaf.oauth.domain.enums.RoleTypeEnum;
import com.landleaf.oauth.domain.request.*;
import com.landleaf.oauth.domain.response.UserInfoResponse;
import com.landleaf.oauth.domain.response.UserSelectiveResponse;
import com.landleaf.oauth.domain.response.UserTabulationResponse;
import com.landleaf.oauth.domain.response.UserValidationResponse;
import com.landleaf.oauth.password.PasswordUtil;
import com.landleaf.oauth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

import static com.landleaf.oauth.domain.enums.ErrorCodeConstants.*;
import static com.landleaf.redis.constance.KeyConstance.FORGOT_PASSWORD;

/**
 * UserServiceImpl
 *
 * @author 张力方
 * @since 2023/6/8
 **/
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserEntityMapper userEntityMapper;
    private final UserRoleEntityMapper userRoleEntityMapper;
    private final RoleEntityMapper roleEntityMapper;
    private final TenantEntityMapper tenantEntityMapper;
    private final UserManagementNodeApi userManagementNodeApi;
    private final MailService mailService;
    private final TokenRedisDAO tokenRedisDAO;
    private final RedisTemplate<String, String> redisTemplate;
    private final LicenseCheck licenseCheck;

    @Override
    public boolean verifyEmailMobile(String email, String mobile) {
        TenantContext.setIgnore(true);
        return !userEntityMapper.exists(Wrappers.<UserEntity>lambdaQuery()
                .eq(CharSequenceUtil.isNotBlank(email), UserEntity::getEmail, email)
                .eq(CharSequenceUtil.isNotBlank(mobile), UserEntity::getMobile, mobile));
    }

    @Override
    public void resetPassword(Long userId) {
        validatePermissions();
        platformAdminIgnore();
        Assert.notNull(userId, "参数异常");
        UserEntity userEntity = userEntityMapper.selectById(userId);
        Assert.notNull(userEntity, () -> new ServiceException(USER_NOT_EXISTS));
        String generatePassword = PasswordUtil.generatePassword(userEntity);
        userEntityMapper.updateById(userEntity);
        // 踢出重置密码的用户
        tokenRedisDAO.kickOutUser(userId);
        // 邮件发送
        mailService.sendMailAsync(UserPasswordMail.resetPassword(userEntity.getEmail(), userEntity.getEmail(),
                userEntity.getMobile(), generatePassword));
    }

    @Override
    public IPage<UserTabulationResponse> searchUsers(UserTabulationRequest request) {
        validatePermissions();
        platformAdminIgnore();
        // 租户管理员
        if (userEntityMapper.searchIsTenantAdmin(LoginUserUtil.getLoginUserId())) {
            request.setTenantId(null);
        }
        return userEntityMapper.searchUsers(Page.of(request.getPageNo(), request.getPageSize()), request)
                .convert(it -> {
                    Response<UserManageNodeResponse> response = userManagementNodeApi.getUserManageNodes(it.getId());
                    if (response != null && response.isSuccess()) {
                        it.setUserManageNodeResponse(response.getResult());
                    }
                    return it;
                });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(Long userId) {
        validatePermissions();
        platformAdminIgnore();
        boolean isAdmin = userEntityMapper.searchIsTenantAdmin(userId) || userEntityMapper.searchIsPlatformAdmin(userId);
        Assert.isFalse(isAdmin, () -> new ServiceException(USER_CANNOT_BE_DELETED));

        Assert.notNull(userId, "参数异常");
        UserEntity userEntity = userEntityMapper.selectById(userId);
        Assert.notNull(userEntity, () -> new ServiceException(USER_NOT_EXISTS));
        userEntityMapper.deleteById(userId);
        // 删除用户角色表
        userRoleEntityMapper.delete(Wrappers.<UserRoleEntity>lambdaQuery().eq(UserRoleEntity::getUserId, userId));
        // 删除用户节点
        UserNodeUpdateRequest updateRequest = new UserNodeUpdateRequest();
        updateRequest.setUserId(userId);
        updateRequest.setTenantId(userEntity.getTenantId());
        updateRequest.setType((short) 0);
        updateRequest.setNodeIds(null);
        userManagementNodeApi.updateUserNode(updateRequest);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changeUser(UserChangeRequest request) {
        validatePermissions();
        platformAdminIgnore();
        UserEntity entity = request.toEntity();
        Long userId = entity.getId();
        Assert.notNull(userEntityMapper.selectById(userId), () -> new ServiceException(USER_NOT_EXISTS));
        entity.setSalt(null);
        userEntityMapper.updateById(entity);

        // 处理用户角色
        List<Long> roleIds = request.getRoleIds();
        validateUserRole(roleIds, entity.getTenantId(), entity.getId());

        List<UserRoleEntity> userRoleEntities = userRoleEntityMapper.searchUsers(userId);
        List<Long> exist = userRoleEntities.stream().map(UserRoleEntity::getRoleId).distinct().toList();

        List<Long> recoverIds = userRoleEntities.stream()
                .filter(it -> roleIds.contains(it.getRoleId()))
                .filter(it -> it.getDeleted() != 0)
                .map(UserRoleEntity::getId)
                .toList();
        if (CollUtil.isNotEmpty(recoverIds)) {
            userRoleEntityMapper.recoverDeletedUserRole(recoverIds);
        }

        List<Long> deleteIds = userRoleEntities.stream()
                .filter(it -> !roleIds.contains(it.getRoleId()))
                .filter(it -> it.getDeleted() == 0)
                .map(UserRoleEntity::getId)
                .toList();
        if (CollUtil.isNotEmpty(deleteIds)) {
            userRoleEntityMapper.deleteBatchIds(deleteIds);
        }

        List<UserRoleEntity> userRoleEntityList = roleIds.stream()
                .filter(it -> !exist.contains(it))
                .distinct()
                .map(it -> {
                    UserRoleEntity userRole = new UserRoleEntity();
                    userRole.setUserId(userId);
                    userRole.setRoleId(it);
                    userRole.setTenantId(entity.getTenantId());
                    return userRole;
                }).toList();
        if (CollUtil.isNotEmpty(userRoleEntityList)) {
            userRoleEntityMapper.insertBatchSomeColumn(userRoleEntityList);
        }
        executeUserNodes(request, entity.getTenantId(), userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createUser(UserChangeRequest request) {
        validatePermissions();
        platformAdminIgnore();
        // check license
        if (licenseCheck.getUserLimit() > 0) {
            // 如果设置值为-1
            long count = userEntityMapper.selectCount(Wrappers.emptyWrapper() );
            if (count >= licenseCheck.getUserLimit()) {
                // 抛出指定异常
                throw new BusinessException(GlobalErrorCodeConstants.USER_LIMIT.getCode(), GlobalErrorCodeConstants.USER_LIMIT.getMsg());
            }
        }

        UserEntity entity = request.toEntity();
        boolean isPlatformAdmin = userEntityMapper.searchIsPlatformAdmin(LoginUserUtil.getLoginUserId());
        Assert.isFalse(!isPlatformAdmin && !Objects.equals(TenantContext.getTenantId(), entity.getTenantId()),
                () -> new ServiceException(USER_ROLE_NOT_EXISTS));
        String generatePassword = PasswordUtil.generatePassword(entity);
        userEntityMapper.insert(entity);
        Long userId = entity.getId();

        // 处理用户角色
        List<Long> roleIds = request.getRoleIds();
        validateUserRole(roleIds, entity.getTenantId(), null);

        List<UserRoleEntity> userRoleEntityList = roleIds.stream()
                .map(it -> {
                    UserRoleEntity userRoleEntity = new UserRoleEntity();
                    userRoleEntity.setUserId(userId);
                    userRoleEntity.setRoleId(it);
                    userRoleEntity.setTenantId(entity.getTenantId());
                    return userRoleEntity;
                }).toList();
        userRoleEntityMapper.insertBatchSomeColumn(userRoleEntityList);

        // 处理用户节点权限
        executeUserNodes(request, entity.getTenantId(), userId);
        // 邮件发送
        mailService.sendMailAsync(UserPasswordMail.userCreate(entity.getEmail(), entity.getEmail(),
                entity.getMobile(), generatePassword));
    }

    @Override
    public UserInfoResponse searchUserInfo(Long userId) {
        Assert.notNull(userId, "参数异常");
        validatePermissions();
        platformAdminIgnore();
        UserEntity userEntity = userEntityMapper.selectById(userId);
        Assert.notNull(userEntity, () -> new ServiceException(USER_NOT_EXISTS));
        TenantEntity tenantEntity = tenantEntityMapper.selectById(userEntity.getTenantId());
        LambdaQueryWrapper<UserRoleEntity> lambdaQueryWrapper = Wrappers.<UserRoleEntity>lambdaQuery().eq(UserRoleEntity::getUserId, userId);
        List<Long> roleIds = userRoleEntityMapper.selectList(lambdaQueryWrapper)
                .stream()
                .map(UserRoleEntity::getRoleId)
                .distinct()
                .toList();
        UserInfoResponse userInfoResponse = UserInfoResponse.fromEntity(userEntity);
        userInfoResponse.setTenantName(tenantEntity.getName());
        userInfoResponse.setRoleIds(roleIds);
        Response<UserManageNodeIdsResponse> response = userManagementNodeApi.getUserManageNodeIds(userId);
        if (response != null && response.isSuccess()) {
            UserManageNodeIdsResponse result = response.getResult();
            userInfoResponse.setNodeType(result.getType());
            userInfoResponse.setNodeIds(result.getNodeIds());
        }
        return userInfoResponse;
    }

    @Override
    public List<UserSelectiveResponse> tenantUsers(Long tenantId) {
        TenantContext.setIgnore(true);
        // 如果不为平台管理员，且没有传递租户,则默认当前租户
        if (Objects.isNull(tenantId)) {
            tenantId = TenantContext.getTenantId();
        }
        boolean isPlatformAdmin = userEntityMapper.searchIsPlatformAdmin(LoginUserUtil.getLoginUserId());
        Assert.isTrue(isPlatformAdmin || tenantId.equals(TenantContext.getTenantId()), () -> new ServiceException(USER_ROLE_NOT_EXISTS));

        return userEntityMapper.selectList(Wrappers.<UserEntity>lambdaQuery()
                        .eq(UserEntity::getTenantId, tenantId)
                ).stream()
                .map(UserSelectiveResponse::fromEntity)
                .toList();
    }

    @Override
    public void changePassword(UserPasswordChangeRequest request) {
        String newPassword = request.getNewPassword();
        String confirmPassword = request.getConfirmPassword();
        Assert.equals(newPassword, confirmPassword, () -> new ServiceException(USER_NEW_PASSWORD_FAILED));
        UserEntity userEntity = userEntityMapper.selectById(request.getId());
        Assert.notNull(userEntity, () -> new ServiceException(USER_NOT_EXISTS));
        // 验证旧密码是否正确
        String password = userEntity.getPassword();
        String salt = userEntity.getSalt();
        boolean equals = CharSequenceUtil.equals(SecureUtil.md5(salt + request.getOldPassword()), password);
        Assert.isTrue(equals, () -> new ServiceException(USER_PASSWORD_FAILED));
        // 设置新的密码
        userEntity.setPassword(SecureUtil.md5(salt + newPassword));
        userEntityMapper.updateById(userEntity);
    }

    private void validateUserRole(List<Long> roleIds, Long tenantId, Long userId) {
        Assert.notEmpty(roleIds, "角色不能为空");
        boolean isAdmin = Objects.nonNull(userId) && (userEntityMapper.searchIsTenantAdmin(userId) || userEntityMapper.searchIsTenantAdmin(userId));
        Long selectCount = roleEntityMapper.selectCount(
                Wrappers.<RoleEntity>lambdaQuery().in(RoleEntity::getId, roleIds)
                        .eq(TenantContext.isIgnore(), RoleEntity::getTenantId, tenantId)
                        .notIn(!isAdmin, RoleEntity::getType, RoleTypeEnum.TENANT.getType(), RoleTypeEnum.PLATFORM.getType())
        );
        Assert.isTrue(selectCount == roleIds.size(), "请检查角色是否可以使用");
    }

    @Override
    public UserValidationResponse validationParam(UserValidationRequest request) {
        TenantContext.setIgnore(true);
        UserValidationResponse validationResponse = new UserValidationResponse();
        if (CharSequenceUtil.isNotBlank(request.getEmail())) {
            validationResponse.setEmail(!userEntityMapper.exists(Wrappers.<UserEntity>lambdaQuery()
                    .ne(Objects.nonNull(request.getId()), UserEntity::getId, request.getId())
                    .eq(UserEntity::getEmail, request.getEmail())));
        }
        if (CharSequenceUtil.isNotBlank(request.getMobile())) {
            validationResponse.setMobile(!userEntityMapper.exists(Wrappers.<UserEntity>lambdaQuery()
                    .ne(Objects.nonNull(request.getId()), UserEntity::getId, request.getId())
                    .eq(UserEntity::getMobile, request.getMobile())));
        }
        return validationResponse;
    }

    @Override
    public void forgotPassword(ForgotPasswordRequest request) {
        TenantContext.setIgnore(true);
        UserEntity userEntity = userEntityMapper.selectOne(Wrappers.<UserEntity>lambdaQuery()
                .eq(UserEntity::getEmail, request.getAccount())
                .or()
                .eq(UserEntity::getMobile, request.getAccount())
        );
        String codeKey = FORGOT_PASSWORD + userEntity.getEmail();
        Assert.notNull(userEntity, () -> new ServiceException(USER_NOT_EXISTS));
        ValueOperations<String, String> opsForValue = redisTemplate.opsForValue();
        String code = opsForValue.get(codeKey);
        Assert.isTrue(CharSequenceUtil.isNotBlank(code), "忘记密码口令不存在");
        Assert.isTrue(CharSequenceUtil.equals(code, request.getCode()), "忘记密码口令错误");
        String generatePassword = PasswordUtil.generatePassword(userEntity);
        userEntityMapper.updateById(userEntity);
        // 踢出重置密码的用户
        tokenRedisDAO.kickOutUser(userEntity.getId());
        // 邮件发送
        mailService.sendMailAsync(UserPasswordMail.resetPassword(userEntity.getEmail(), userEntity.getEmail(),
                userEntity.getMobile(), generatePassword));
        // 删除口令
        redisTemplate.delete(codeKey);
    }

    @Override
    public String forgotPasswordCode(ForgotPasswordRequest request) {
        TenantContext.setIgnore(true);
        UserEntity userEntity = userEntityMapper.selectOne(Wrappers.<UserEntity>lambdaQuery()
                .eq(UserEntity::getEmail, request.getAccount())
                .or()
                .eq(UserEntity::getMobile, request.getAccount())
        );
        String codeKey = FORGOT_PASSWORD + userEntity.getEmail();
        Assert.notNull(userEntity, () -> new ServiceException(USER_NOT_EXISTS));
        String code = RandomUtil.randomStringUpper(6);
        ValueOperations<String, String> opsForValue = redisTemplate.opsForValue();
        Boolean hasKey = redisTemplate.hasKey(codeKey);
        Assert.isFalse(Boolean.TRUE.equals(hasKey), "口令已发送，请查看邮箱。十分钟内请勿再次操作!");
        opsForValue.set(codeKey, code, Duration.ofMinutes(10));
        mailService.sendMailAsync(UserCodeMail.forgotPasswordCode(userEntity.getEmail(), code));
        return userEntity.getEmail();
    }

    /**
     * 验证登陆用户权限
     */
    private void validatePermissions() {
        Long loginUserId = LoginUserUtil.getLoginUserId();
        boolean isTenantAdmin = userEntityMapper.searchIsTenantAdmin(loginUserId);
        boolean isPlatformAdmin = userEntityMapper.searchIsPlatformAdmin(loginUserId);
        Assert.isTrue(isTenantAdmin || isPlatformAdmin, () -> new ServiceException(USER_ROLE_NOT_EXISTS));
    }

    /**
     * 平台管理员设置忽略租户
     */
    private void platformAdminIgnore() {
        if (userEntityMapper.searchIsPlatformAdmin(LoginUserUtil.getLoginUserId())) {
            TenantContext.setIgnore(true);
        }
    }

    /**
     * 处理用户节点
     *
     * @param request  请求
     * @param tenantId 租户ID
     * @param userId   用户ID
     */
    private void executeUserNodes(UserChangeRequest request, Long tenantId, Long userId) {
        // 处理用户节点权限
        if (CollUtil.isNotEmpty(request.getNodeIds())) {
            UserNodeUpdateRequest updateRequest = new UserNodeUpdateRequest();
            updateRequest.setUserId(userId);
            updateRequest.setTenantId(tenantId);
            updateRequest.setType(request.getNodeType());
            updateRequest.setNodeIds(request.getNodeIds());
            userManagementNodeApi.updateUserNode(updateRequest);
        }
    }

}
