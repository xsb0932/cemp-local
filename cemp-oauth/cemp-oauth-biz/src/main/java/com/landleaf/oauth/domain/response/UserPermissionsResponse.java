package com.landleaf.oauth.domain.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 查询当前用户的权限信息
 *
 * @author yue lin
 * @since 2023/7/3 10:25
 */
@Data
@Schema(description = "查询当前用户的权限信息")
public class UserPermissionsResponse {

    /**
     * 模块菜单信息
     */
    @Schema(description = "模块菜单信息")
    private List<ModuleMenuResponse> moduleMenus;

    /**
     * 用户所在租户的logo
     */
    @Schema(description = "模块菜单信息")
    private String tenantLogo;

}
