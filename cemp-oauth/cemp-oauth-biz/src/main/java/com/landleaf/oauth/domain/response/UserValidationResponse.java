package com.landleaf.oauth.domain.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 用户参数校验
 *
 * @author yue lin
 * @since 2023/6/29 13:21
 */
@Data
@Schema(description = "用户参数校验")
public class UserValidationResponse {

    /**
     * 邮箱（true可用）
     */
    @Schema(description = "邮箱（true可用）")
    private Boolean email;

    /**
     * 手机号（true可用）
     */
    @Schema(description = "手机号（true可用）")
    private Boolean mobile;

}
