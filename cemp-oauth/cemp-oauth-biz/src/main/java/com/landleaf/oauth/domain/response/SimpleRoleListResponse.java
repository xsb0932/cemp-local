package com.landleaf.oauth.domain.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * SimpleRoleListResponse
 *
 * @author 张力方
 * @since 2023/6/9
 **/
@Data
@Schema(description = "角色简单返回")
public class SimpleRoleListResponse {
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
     * 角色类型（1平台管理员 2租户管理员 3普通角色）
     */
    @Schema(description = "角色类型（1平台管理员 2租户管理员 3普通角色）")
    private Short type;
}
