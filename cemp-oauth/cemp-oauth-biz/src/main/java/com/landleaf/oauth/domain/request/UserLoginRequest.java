package com.landleaf.oauth.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 用户登录请求对象
 *
 * @author 张力方
 * @since 2023/6/1
 **/
@Data
@Schema(name = "用户登录请求对象", description = "手机号/邮箱密码登录")
public class UserLoginRequest {
    /**
     * 手机号或邮箱
     */
    @Schema(description = "手机号或邮箱", requiredMode = Schema.RequiredMode.REQUIRED, example = "15712345678")
    @NotBlank(message = "手机号或邮箱不能为空")
    private String mobileOrEmail;
    /**
     * 密码
     */
    @Schema(description = "密码", requiredMode = Schema.RequiredMode.REQUIRED, example = "Qwer@1234")
    @NotBlank(message = "密码不能为空")
    private String password;

    /**
     * 验证码
     * <p>
     * TODO 预留字段，暂未实现
     */
    @Schema(description = "密码", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String captcha;
}
