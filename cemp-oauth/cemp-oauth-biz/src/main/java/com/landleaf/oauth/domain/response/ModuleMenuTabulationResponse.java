package com.landleaf.oauth.domain.response;

import com.google.common.collect.Lists;
import com.landleaf.oauth.domain.entity.MenuEntity;
import com.landleaf.oauth.domain.entity.MenuSystemEntity;
import com.landleaf.oauth.domain.entity.ModuleEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 获取模块菜单列表
 *
 * @author yue lin
 * @since 2023/6/1 13:13
 */
@Data
@Schema(name = "模块菜单列表", description = "模块菜单列表")
public class ModuleMenuTabulationResponse {

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
     * 模块下菜单
     */
    @Schema(description = "模块下菜单")
    private List<MenuResponse> menus;

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
         * 菜单类型（1目录 2菜单 3按钮（预留））
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
         * 菜单备注
         */
        @Schema(description = "菜单备注", example = "菜单备注")
        private String menuRemark;

        /**
         * 菜单唯一标识符
         */
        @Schema(description = "菜单唯一标识符", example = "菜单唯一标识符")
        private String permission;

        /**
         * 父级ID
         */
        @Schema(description = "父级ID")
        private Long parentId;

        /**
         * 打开方式
         */
        @Schema(description = "打开方式")
        private String openWith;

        /**
         * 下级菜单
         */
        @Schema(description = "下级菜单")
        private List<MenuResponse> children;

        /**
         * 将实体转为返回接口对象
         * @param menuEntity 实体
         * @return 接口对象
         */
        public static MenuResponse fromEntity(MenuEntity menuEntity) {
            MenuResponse menuResponse = new MenuResponse();
            menuResponse.setMenuId(menuEntity.getId());
            menuResponse.setMenuName(menuEntity.getName());
            menuResponse.setMenuType(menuEntity.getType());
            menuResponse.setMenuPath(menuEntity.getPath());
            menuResponse.setMenuSort(menuEntity.getSort());
            menuResponse.setMenuIcon(menuEntity.getIcon());
            menuResponse.setParentId(menuEntity.getParentId());
            menuResponse.setMenuRemark(menuEntity.getRemark());
            menuResponse.setPermission(menuEntity.getPermission());
            menuResponse.setOpenWith(menuEntity.getOpenWith());
            menuResponse.setChildren(Lists.newArrayList());
            return menuResponse;
        }

        /**
         * 将实体转为返回接口对象
         * @param menuEntity 实体
         * @return 接口对象
         */
        public static MenuResponse fromSystemEntity(MenuSystemEntity menuEntity) {
            MenuResponse menuResponse = new MenuResponse();
            menuResponse.setMenuId(menuEntity.getId());
            menuResponse.setMenuName(menuEntity.getName());
            menuResponse.setMenuType(menuEntity.getType());
            menuResponse.setMenuPath(menuEntity.getPath());
            menuResponse.setMenuSort(menuEntity.getSort());
            menuResponse.setMenuIcon(menuEntity.getIcon());
            menuResponse.setParentId(menuEntity.getParentId());
            menuResponse.setMenuRemark(menuEntity.getRemark());
            menuResponse.setPermission(menuEntity.getPermission());
            menuResponse.setOpenWith(menuEntity.getOpenWith());
            menuResponse.setChildren(Lists.newArrayList());
            return menuResponse;
        }

    }

    public static ModuleMenuTabulationResponse fromEntity(ModuleEntity moduleEntity){
        ModuleMenuTabulationResponse moduleMenuTabulationResponse = new ModuleMenuTabulationResponse();
        moduleMenuTabulationResponse.setModuleId(moduleEntity.getId());
        moduleMenuTabulationResponse.setModuleCode(moduleEntity.getCode());
        moduleMenuTabulationResponse.setModuleName(moduleEntity.getName());
        moduleMenuTabulationResponse.setMenus(Lists.newArrayList());
        return moduleMenuTabulationResponse;
    }

}
