package com.landleaf.oauth.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * RoleUpdateRequest
 *
 * @author 张力方
 * @since 2023/6/9
 **/
@Data
@Schema(name = "角色更新请求参数", description = "角色更新请求参数")
public class RoleUpdateRequest {
    /**
     * 角色id
     */
    @NotNull(message = "角色id不能为空")
    @Schema(description = "角色id", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    /**
     * 角色名称
     */
    @NotBlank(message = "角色名称不能为空")
    @Size(min = 1, max = 30, message = "角色名称长度区间{min}-{max}")
    @Schema(description = "角色名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "管理员")
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
     * <p>
     * 为空则删除角色下所有菜单权限
     */
    @Schema(description = "角色下菜单ids", example = "[1,2,3]")
    private List<Long> menuIds;
}
