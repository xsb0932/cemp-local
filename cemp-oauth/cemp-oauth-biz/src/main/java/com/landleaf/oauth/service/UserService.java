package com.landleaf.oauth.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.landleaf.oauth.domain.request.*;
import com.landleaf.oauth.domain.response.UserInfoResponse;
import com.landleaf.oauth.domain.response.UserSelectiveResponse;
import com.landleaf.oauth.domain.response.UserTabulationResponse;
import com.landleaf.oauth.domain.response.UserValidationResponse;

import java.util.List;

/**
 * UserService
 *
 * @author 张力方
 * @since 2023/6/8
 **/
public interface UserService {

    /**
     * 校验手机号或者邮箱是否存在
     *
     * @param email  邮箱
     * @param mobile 手机号
     * @return 结果
     */
    boolean verifyEmailMobile(String email, String mobile);

    /**
     * 重置用户密码
     *
     * @param userId 用户ID
     */
    void resetPassword(Long userId);

    /**
     * 分页查询用户
     *
     * @param request 参数
     * @return 结果集
     */
    IPage<UserTabulationResponse> searchUsers(UserTabulationRequest request);

    /**
     * 删除用户
     *
     * @param userId 用户ID
     */
    void deleteUser(Long userId);

    /**
     * 变更用户信息
     *
     * @param request 参数
     */
    void changeUser(UserChangeRequest request);

    /**
     * 创建用户信息
     *
     * @param request 参数
     */
    void createUser(UserChangeRequest request);

    /**
     * 获取当前用户详细信息
     *
     * @param userId 用户ID
     * @return 结果
     */
    UserInfoResponse searchUserInfo(Long userId);

    /**
     * 查询当前住下的用户列表
     *
     * @param tenantId 租户ID
     * @return 结果
     */
    List<UserSelectiveResponse> tenantUsers(Long tenantId);

    /**
     * 用户修改密码
     *
     * @param request 参数
     */
    void changePassword(UserPasswordChangeRequest request);

    /**
     * 校验参数是否已经存在
     *
     * @param request 参数
     * @return 结果
     */
    UserValidationResponse validationParam(UserValidationRequest request);

    /**
     * 忘记密码
     *
     * @param request 参数
     */
    void forgotPassword(ForgotPasswordRequest request);

    /**
     * 忘记密码-发送口令
     *
     * @param request 请求
     * @return 邮箱
     */
    String forgotPasswordCode(ForgotPasswordRequest request);
}
