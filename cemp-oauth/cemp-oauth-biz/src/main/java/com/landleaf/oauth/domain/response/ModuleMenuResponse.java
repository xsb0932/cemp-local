package com.landleaf.oauth.domain.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 模块菜单
 *
 * @author 张力方
 * @since 2023/6/2
 **/
@Data
public class ModuleMenuResponse {

    /**
     * 模块ID
     */
    @Schema(description = "模块ID", example = "1")
    private Long moduleId;

    /**
     * 模块Code
     */
    @Schema(description = "模块Code", example = "code")
    private String moduleCode;

    /**
     * 模块名
     */
    @Schema(description = "模块名", example = "模块名")
    private String moduleName;

    /**
     * 是否默认展示的模块
     */
    @Schema(description = "是否默认展示的模块", example = "true")
    private Boolean isDefault;

    /**
     * 模块下菜单
     */
    @Schema(description = "模块下菜单")
    private List<ModuleMenuResponse.MenuResponse> menus;

    @Data
    @Schema(description = "模块菜单")
    public static class MenuResponse {

        /**
         * 菜单ID
         */
        @Schema(description = "菜单ID", example = "1")
        private Long menuId;

        /**
         * 菜单名
         */
        @Schema(description = "菜单名", example = "菜单名")
        private String menuName;

        /**
         * 菜单类型
         */
        @Schema(description = "菜单类型", example = "菜单类型")
        private String menuType;

        /**
         * 菜单路径
         */
        @Schema(description = "菜单路径", example = "/device/monitor")
        private String menuPath;

        /**
         * 菜单排序
         */
        @Schema(description = "菜单排序", example = "1")
        private Long menuSort;

        /**
         * 菜单图标
         */
        @Schema(description = "菜单图标", example = "菜单图标")
        private String menuIcon;

        /**
         * 父级ID
         */
        @Schema(description = "父级ID")
        private Long parentId;

        /**
         * 菜单唯一标识符
         */
        @Schema(description = "菜单唯一标识符", example = "菜单唯一标识符")
        private String permission;

        /**
         * 打开方式
         */
        @Schema(description = "打开方式")
        private String openWith;

        /**
         * 下级菜单
         */
        @Schema(description = "下级菜单")
        private List<ModuleMenuResponse.MenuResponse> children;

    }

}

