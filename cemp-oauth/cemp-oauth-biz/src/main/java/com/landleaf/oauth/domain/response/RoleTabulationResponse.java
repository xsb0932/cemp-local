package com.landleaf.oauth.domain.response;

import com.landleaf.oauth.domain.entity.RoleEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 角色列表返回值
 *
 * @author yue lin
 * @since 2023/6/3 17:41
 */
@Data
@Schema(name = "角色列表返回值", description = "角色列表返回值")
public class RoleTabulationResponse {
    /**
     * 角色id
     */
    @Schema(name = "id")
    private Long id;

    /**
     * 角色名称
     */
    @Schema(description = "角色名称")
    private String name;

    public static RoleTabulationResponse from(RoleEntity roleEntity) {
        RoleTabulationResponse response = new RoleTabulationResponse();
        response.setId(roleEntity.getId());
        response.setName(roleEntity.getName());
        return response;
    }

}
