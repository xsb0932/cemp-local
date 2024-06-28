package com.landleaf.oauth.domain.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * RoleListResponse
 *
 * @author 张力方
 * @since 2023/6/9
 **/
@Data
@Schema(description = "角色列表")
public class RoleListResponse {
    /**
     * 角色id
     */
    @Schema(description = "角色id")
    private Long id;

    /**
     * 角色名称
     */
    @Schema(description = "角色名称")
    private String name;

    /**
     * 企业id
     */
    @Schema(description = "企业id")
    private String tenantId;

    /**
     * 企业名称
     */
    @Schema(description = "企业名称")
    private String tenantName;

    /**
     * 用户状态（0正常 1停用）
     */
    @Schema(description = "用户状态（0正常 1停用）")
    private Short status;

    /**
     * 角色类型（1平台管理员 2租户管理员 3普通角色）
     */
    @Schema(description = "角色类型（1平台管理员 2租户管理员 3普通角色）")
    private Short type;

    /**
     * 角色权限
     * <p>
     * 用于列表查看角色权限
     */
    @Schema(description = "角色权限")
    private List<ModuleMenu> rolePermissions;

    /**
     * 角色拥有的模块菜单id列表
     * <p>
     * 用于编辑回填选择框
     */
    @Schema(description = "角色拥有的模块菜单id列表")
    private List<ModuleMenuIds> moduleMenuIds;

    /**
     * 角色下用户数量
     */
    @Schema(description = "角色下用户数量")
    private Integer userNum;

    /**
     * 角色下用户列表
     * <p>
     * 用于列表查看用户列表
     */
    @Schema(description = "角色下用户列表")
    private List<User> userList;

    @Data
    @Schema(description = "模块菜单")
    public static class ModuleMenuIds {
        /**
         * 模块id
         */
        @Schema(description = "模块id")
        private Long moduleId;
        /**
         * 模块名称
         */
        @Schema(description = "模块名称")
        private String moduleName;
        /**
         * 模块编码
         */
        @Schema(description = "模块编码")
        private String moduleCode;
        /**
         * 模块下菜单ids
         */
        @Schema(description = "模块下菜单ids")
        private List<Long> menuIds;
    }

    @Data
    @Schema(description = "模块菜单")
    public static class ModuleMenu {
        /**
         * 模块id
         */
        @Schema(description = "模块id")
        private Long moduleId;
        /**
         * 模块名称
         */
        @Schema(description = "模块名称")
        private String moduleName;
        /**
         * 模块编码
         */
        @Schema(description = "模块编码")
        private String moduleCode;
        /**
         * 模块下菜单
         */
        @Schema(description = "模块下菜单")
        private List<Menu> menus;
    }

    @Schema(description = "菜单")
    @Data
    public static class Menu {
        /**
         * 模块名称
         */
        @Schema(description = "模块名称")
        private String moduleName;
        /**
         * 模块id
         */
        @Schema(description = "模块id")
        private Long moduleId;

        /**
         * 模块code
         */
        @Schema(description = "模块code")
        private String moduleCode;

        /**
         * 菜单id
         */
        @Schema(description = "菜单id")
        private Long menuId;

        /**
         * 菜单名称
         */
        @Schema(description = "菜单名称")
        private String menuName;

        /**
         * 权限标识
         */
        @Schema(description = "权限标识")
        private String menuPermission;

        /**
         * 菜单类型（1目录 2菜单 3按钮（预留））
         */
        @Schema(description = "菜单类型（1目录 2菜单 3按钮（预留））")
        private Long type;

        /**
         * 菜单路由地址
         */
        @Schema(description = "菜单路由地址")
        private String menuPath;

        /**
         * 父菜单id
         */
        @Schema(description = "父菜单id")
        private Long parentId;

        /**
         * 菜单图标
         */
        @Schema(description = "菜单图标")
        private String menuIcon;

        /**
         * 子集
         */
        @Schema(description = "子集")
        private List<Menu> children;

    }

    @Data
    @Schema(description = "用户")
    public static class User {
        /**
         * 用户id
         */
        @Schema(description = "用户id")
        private Long userId;
        /**
         * 用户名称
         */
        @Schema(description = "用户名称")
        private String username;
        /**
         * 用户昵称
         */
        @Schema(description = "用户昵称")
        private String nickname;
    }

}
