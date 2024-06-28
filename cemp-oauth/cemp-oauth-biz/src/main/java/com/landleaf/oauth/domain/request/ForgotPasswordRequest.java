package com.landleaf.oauth.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 密码重置参数
 *
 * @author yue lin
 * @since 2023/7/26 14:50
 */
@Data
public class ForgotPasswordRequest {

    /**
     * 账号（手机号或邮箱）
     */
    @NotBlank(groups = {Code.class, Rest.class}, message = "账号不能为空")
    @Schema(description = "账号（手机号或邮箱）")
    private String account;

    /**
     * 口令(发送口令不必传递)
     */
    @NotBlank(groups = {Rest.class}, message = "口令不能为空")
    @Schema(description = "口令")
    private String code;

    public interface Code {}

    public interface Rest {}

}
