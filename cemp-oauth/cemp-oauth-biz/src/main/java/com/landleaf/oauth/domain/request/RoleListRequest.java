package com.landleaf.oauth.domain.request;

import com.landleaf.comm.base.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * RoleListRequest
 *
 * @author 张力方
 * @since 2023/6/9
 **/
@Data
@Schema(name = "角色列表查询请求参数", description = "角色列表查询请求参数")
public class RoleListRequest extends PageParam {

    /**
     * 角色名称
     */
    @Schema(description = "角色名称", example = "john")
    private String name;

    /**
     * 租户名称
     */
    @Schema(description = "租户名称", example = "john")
    private String tenantName;

    /**
     * 租户id
     */
    @Schema(description = "租户id", example = "john")
    private Integer tenantId;
}
