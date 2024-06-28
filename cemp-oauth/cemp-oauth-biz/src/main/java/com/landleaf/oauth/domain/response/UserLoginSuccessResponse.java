package com.landleaf.oauth.domain.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 用户登陆成功返回参数
 *
 * @author yue lin
 * @since 2023/6/2 9:38
 */
@Data
@Schema(name = "用户登陆成功返回参数", description = "用户登陆成功返回参数")
public class UserLoginSuccessResponse {

    /**
     * 基本用户信息
     */
    @Schema(description = "基本用户信息")
    private UserLoginResponse authInfo;

    /**
     * 用户所在租户的logo
     */
    @Schema(description = "用户所在租户的logo")
    private String tenantLogo;

    /**
     * 模块菜单权限信息
     */
    @Schema(description = "模块菜单权限信息")
    private List<ModuleMenuResponse> moduleMenuInfo;

    /**
     * 用户角色权限信息
     */
    @Schema(description = "用户角色权限信息")
    private List<UserRoleResponse> roleInfo;

    @Data
    @Accessors(chain = true)
    public static class UserLoginResponse {
        /**
         * 用户id
         */
        @Schema(description = "用户编号", example = "1024")
        private Long userId;
        /**
         * 用户账号
         */
        @Schema(description = "用户账号", example = "1024")
        private String username;
        /**
         * 用户名
         */
        @Schema(description = "用户名", example = "1024")
        private String nickname;
        /**
         * 租户id
         */
        @Schema(description = "租户编号", example = "2048")
        private Long tenantId;
        /**
         * token
         */
        @Schema(description = "访问令牌", example = "happy")
        private String token;
    }

    @Data
    public static class UserRoleResponse {
        /**
         * 角色id
         */
        private Long roleId;

        /**
         * 角色名称
         */
        private String roleName;

        /**
         * 角色类型（1平台管理员 2租户管理员 3普通角色）
         */
        private Short roleType;
    }

}
