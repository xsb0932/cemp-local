package com.landleaf.oauth.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 租户参数校验请求
 *
 * @author yue lin
 * @since 2023/6/28 14:27
 */
@Data
@Schema(description = "租户参数校验请求")
public class TenantValidationRequest {

    /**
     * 租户ID
     * 新增时参数校验不传递，更新时传递。排除当前租户
     */
    @Schema(description = "租户ID，新增时参数校验不传递，更新时传递。排除当前租户")
    private Long tenantId;

    /**
     * 租户企业名称
     */
    @Schema(description = "租户企业名称")
    private String name;

    /**
     * 租户企业编码
     */
    @Schema(description = "租户企业编码")
    private String code;

    /**
     * 社会信用代码
     */
    @Schema(description = "社会信用代码")
    private String socialCreditCode;

    /**
     * 租户管理员手机号
     */
    @Schema(description = "租户管理员手机号")
    private String adminMobile;

    /**
     * 租户管理员邮箱
     */
    @Schema(description = "租户管理员邮箱")
    private String adminEmail;

    /**
     * 租户管理员账号
     */
    @Schema(description = "租户管理员账号")
    private String adminUserName;

}
