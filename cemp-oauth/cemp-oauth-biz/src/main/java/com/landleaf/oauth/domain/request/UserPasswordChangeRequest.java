package com.landleaf.oauth.domain.request;

import com.landleaf.web.validation.Update;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 用户变更参数
 *
 * @author yue lin
 * @since 2023/6/9 16:35
 */
@Data
@Schema(description = "用户变更参数")
public class UserPasswordChangeRequest {

    /**
     * 用户ID
     */
    @NotNull(groups = Update.class, message = "用户主键不能为空")
    @Schema(description = "用户ID")
    private Long id;

    /**
     * 旧密码（md5）
     */
    @NotBlank(message = "旧密码不能为空")
    @Schema(description = "旧密码（md5）")
    private String oldPassword;

    /**
     * 新密码（md5）
     */
    @NotBlank(message = "新密码不能为空")
    @Schema(description = "新密码（md5）")
    private String newPassword;

    /**
     * 新密码（md5）
     */
    @NotBlank(message = "确认新密码不能为空")
    @Schema(description = "确认新密码（md5）")
    private String confirmPassword;

}
