package com.landleaf.oauth.domain.request;

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
public class UserValidationRequest {

    /**
     * 用户ID
     * 新增时校验不传递，更新时传递
     */
    @Schema(description = "用户ID, 新增时校验不传递，更新时传递")
    private Long id;

    /**
     * 邮箱
     */
    @Schema(description = "邮箱")
    private String email;

    /**
     * 手机号
     */
    @Schema(description = "手机号")
    private String mobile;

}
