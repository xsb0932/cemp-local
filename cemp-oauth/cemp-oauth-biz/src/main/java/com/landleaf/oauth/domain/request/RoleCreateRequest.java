package com.landleaf.oauth.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * RoleCreateRequest
 *
 * @author 张力方
 * @since 2023/6/9
 **/
@Data
@Schema(name = "角色创建请求参数", description = "角色创建请求参数")
public class RoleCreateRequest {
    /**
     * 租户id
     */
    @NotNull(message = "租户id不能为空")
    @Schema(description = "租户id", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long tenantId;

    /**
     * 角色名称
     */
    @NotBlank(message = "角色名称不能为空")
    @Size(min = 1, max = 30, message = "角色名称长度区间{min}-{max}")
    @Schema(description = "角色名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "12431")
    private String name;

    /**
     * 角色状态（0正常 1停用）
     * <p>
     * 目前默认都是0
     */
    @Schema(description = "角色状态（0正常 1停用）", example = "0")
    private Short status = 0;

    /**
     * 角色下菜单ids
     */
    @Schema(description = "角色下菜单ids", example = "[1,2,3]")
    private List<Long> menuIds;
}
