package com.landleaf.oauth.domain.request;

import com.landleaf.oauth.domain.entity.MenuEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 菜单变更请求参数
 *
 * @author yue lin
 * @since 2023/6/1 15:09
 */
@Data
@Schema(name = "菜单变更请求参数", description = "菜单变更请求参数")
public class MenuUpdateRequest {

    /**
     * id
     */
    @NotNull(message = "id不能为空")
    @Schema(description = "id", example = "1")
    private Long id;

    /**
     * 菜单名
     */
    @NotBlank(message = "菜单名称不能为空")
    @Schema(description = "菜单名", example = "1")
    private String menuName;

//    /**
//     * 标识(平台管理员)
//     */
//    @Schema(description = "标识(平台管理员)", example = "1")
//    private String permission;
//
//    /**
//     * 菜单类型(平台管理员)
//     */
//    @Schema(description = "菜单类型(平台管理员)", example = "1")
//    private String menuType;
//
    /**
     * 菜单路径(平台管理员)
     */
    @Schema(description = "菜单路径(平台管理员)", example = "1")
    private String menuPath;
//
//    /**
//     * 菜单图标
//     */
//    @NotBlank(message = "菜单图标不能为空")
//    @Schema(description = "菜单图标", example = "1")
//    private String menuIcon;

    /**
     * 打开方式(平台管理员)
     */
    @Schema(description = "打开方式", example = "1")
    private String openWith;

    /**
     * 备注
     */
    @Schema(description = "备注", example = "1")
    private String menuRemark;

    public MenuEntity toEntity() {
        MenuEntity menuEntity = new MenuEntity();
        menuEntity.setId(this.id);
        menuEntity.setName(this.menuName);
        menuEntity.setPath(this.menuPath);
        menuEntity.setRemark(this.menuRemark);
        return menuEntity;
    }

}
