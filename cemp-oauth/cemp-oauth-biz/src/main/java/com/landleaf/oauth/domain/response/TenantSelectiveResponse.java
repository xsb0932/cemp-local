package com.landleaf.oauth.domain.response;

import com.landleaf.oauth.domain.entity.TenantEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 租户选择框列表参数
 *
 * @author yue lin
 * @since 2023/6/13 9:32
 */
@Data
@Schema(description = "租户选择框列表参数")
public class TenantSelectiveResponse {

    /**
     * 租户ID
     */
    @Schema(description = "租户ID")
    private Long tenantId;

    /**
     * 租户名称
     */
    @Schema(description = "租户名称")
    private String tenantName;

    public static TenantSelectiveResponse fromEntity(TenantEntity entity) {
        TenantSelectiveResponse response = new TenantSelectiveResponse();
        response.setTenantId(entity.getId());
        response.setTenantName(entity.getName());
        return response;
    }

}
