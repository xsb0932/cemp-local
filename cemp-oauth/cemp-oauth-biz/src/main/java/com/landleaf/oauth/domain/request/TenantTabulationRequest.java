package com.landleaf.oauth.domain.request;

import com.landleaf.comm.base.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 租户分页查询参数
 *
 * @author yue lin
 * @since 2023/6/1 11:07
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(name = "租户分页查询参数", description = "租户分页查询参数")
public class TenantTabulationRequest extends PageParam {

    /**
     * 租户名称查询
     */
    @Schema(description = "租户名称查询", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String name;

    /**
     * 租户状态0正常1停用
     */
    @Schema(description = "租户状态", example = "")
    private Short status;

    /**
     * 管理员手机号
     */
    @Schema(description = "管理员手机号", example = "")
    private String adminMobile;

    /**
     * 管理员邮箱
     */
    @Schema(description = "管理员邮箱", example = "")
    private String adminEmail;

    /**
     * 管理员用户名
     */
    @Schema(description = "管理员用户名", example = "")
    private String adminNickName;

}
