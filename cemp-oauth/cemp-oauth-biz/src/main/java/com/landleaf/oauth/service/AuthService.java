package com.landleaf.oauth.service;

import com.landleaf.oauth.domain.request.UserLoginRequest;
import com.landleaf.oauth.domain.response.UserLoginSuccessResponse;
import com.landleaf.oauth.domain.response.UserPermissionsResponse;

/**
 * 认证服务
 *
 * @author 张力方
 * @since 2023/6/1
 **/
public interface AuthService {
    /**
     * 用户登录
     *
     * @param req 用户登录请求
     * @return token
     */
    UserLoginSuccessResponse login(UserLoginRequest req);

    /**
     * 用户登出
     */
    void logout();

    /**
     * 获取当前用户模块菜单
     */
    UserPermissionsResponse getModuleMenus();

}
