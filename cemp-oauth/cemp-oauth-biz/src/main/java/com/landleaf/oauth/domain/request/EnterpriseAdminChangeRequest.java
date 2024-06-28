package com.landleaf.oauth.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 企业管理员变更
 *
 * @author yue lin
 * @since 2023/6/9 15:26
 */
@Data
public class EnterpriseAdminChangeRequest {

    /**
     * 被转移的租户ID
     */
    @Schema(description = "被转移的租户ID")
    private Long tenantId;

    /**
     * 新企业管理员ID
     */
    @NotNull(message = "新企业管理员不能为空")
    @Schema(description = "新企业管理员ID")
    private Long userId;

    /**
     * 移交后的角色ID
     */
    @NotNull(message = "移交后角色不能为空")
    @Schema(description = "移交后的角色ID")
    private Long roleId;

}
