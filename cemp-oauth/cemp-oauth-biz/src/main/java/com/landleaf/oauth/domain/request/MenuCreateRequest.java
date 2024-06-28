package com.landleaf.oauth.domain.request;

import com.landleaf.oauth.domain.entity.MenuSystemEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 菜单管理员新增请求参数
 *
 * @author yue lin
 * @since 2023/6/1 15:09
 */
@Data
@Schema(name = "菜单管理员新增请求参数", description = "菜单管理员新增请求参数")
public class MenuCreateRequest {

    /**
     * 模块ID
     */
    @NotNull(message = "模块ID不能为空")
    @Schema(description = "模块ID", example = "1")
    private Long moduleId;

    /**
     * 模块Code
     */
    @NotBlank(message = "模块编码不能为空")
    @Schema(description = "模块Code", example = "MODULE")
    private String moduleCode;

    /**
     * 父级Id
     */
    @NotNull(message = "父级ID不能为空")
    @Schema(description = "父级Id", example = "1")
    private Long parentId;

    /**
     * 菜单名
     */
    @NotBlank(message = "菜单名称不能为空")
    @Schema(description = "菜单名", example = "1")
    private String menuName;

    /**
     * 标识
     */
    @NotBlank(message = "标识不能为空")
    @Schema(description = "标识", example = "1")
    private String permission;

    /**
     * 菜单类型
     */
    @NotBlank(message = "菜单类型不能为空")
    @Schema(description = "菜单类型", example = "1")
    private String menuType;

    /**
     * 菜单路径
     */
    @NotBlank(message = "菜单路径不能为空")
    @Schema(description = "菜单路径", example = "1")
    private String menuPath;

    /**
     * 菜单图标
     */
    @NotBlank(message = "菜单图标不能为空")
    @Schema(description = "菜单图标", example = "1")
    private String menuIcon;

    /**
     * 打开方式
     */
    @NotBlank(message = "打开方式不能为空")
    @Schema(description = "打开方式", example = "1")
    private String openWith;

    /**
     * 备注
     */
    @Schema(description = "备注", example = "1")
    private String menuRemark;

    public MenuSystemEntity toEntity() {
        MenuSystemEntity menuEntity = new MenuSystemEntity();
        menuEntity.setModuleId(this.moduleId);
        menuEntity.setModuleCode(this.moduleCode);
        menuEntity.setName(this.menuName);
        menuEntity.setPermission(this.permission);
        menuEntity.setType(this.menuType);
        menuEntity.setPath(this.menuPath);
        menuEntity.setParentId(this.parentId);
        menuEntity.setIcon(this.menuIcon);
        menuEntity.setOpenWith(this.openWith);
        menuEntity.setRemark(this.menuRemark);
        return menuEntity;
    }

}
