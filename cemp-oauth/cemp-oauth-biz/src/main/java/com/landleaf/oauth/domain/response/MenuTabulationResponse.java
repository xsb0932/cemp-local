package com.landleaf.oauth.domain.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 菜单列表
 *
 * @author yue lin
 * @since 2023/6/9 15:12
 */
@Data
@Schema(description = "菜单列表")
public class MenuTabulationResponse {

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
     * 打开方式
     */
    @Schema(description = "打开方式")
    private String openWith;

    /**
     * 父级ID
     */
    @Schema(description = "父级ID")
    private Long parentId;

    /**
     * 下级菜单
     */
    @Schema(description = "下级菜单")
    private List<MenuTabulationResponse> children;

}
