package com.landleaf.oauth.domain.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 租户参数校验返回, true通过，false已存在
 *
 * @author yue lin
 * @since 2023/6/28 14:27
 */
@Data
@Schema(description = "租户参数校验返回, true通过，false已存在")
public class TenantValidationResponse {

    /**
     * 租户企业名称
     */
    @Schema(description = "租户企业名称")
    private Boolean name;

    /**
     * 租户企业编码
     */
    @Schema(description = "租户企业编码")
    private Boolean code;

    /**
     * 社会信用代码
     */
    @Schema(description = "社会信用代码")
    private Boolean socialCreditCode;

    /**
     * 租户管理员手机号
     */
    @Schema(description = "租户管理员手机号")
    private Boolean adminMobile;

    /**
     * 租户管理员邮箱
     */
    @Schema(description = "租户管理员邮箱")
    private Boolean adminEmail;

    /**
     * 租户管理员账号
     */
    @Schema(description = "租户管理员账号")
    private Boolean adminUserName;

}
