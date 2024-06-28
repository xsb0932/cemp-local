package com.landleaf.oauth.domain.response;

import com.landleaf.oauth.domain.entity.UserEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 用户选择列表
 *
 * @author yue lin
 * @since 2023/6/13 10:44
 */
@Data
@Schema(description = "用户选择列表")
public class UserSelectiveResponse {

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private Long userId;

    /**
     * 用户账号
     */
    @Schema(description = "用户账号")
    private String username;

    /**
     * 用户名
     */
    @Schema(description = "用户名")
    private String nickname;

    /**
     * 邮箱
     */
    @Schema(description = "邮箱")
    private String email;

    /**
     * 电话
     */
    @Schema(description = "电话")
    private String mobile;

    /**
     * 租户ID
     */
    @Schema(description = "租户ID")
    private Long tenantId;

    public static UserSelectiveResponse fromEntity(UserEntity entity) {
        UserSelectiveResponse response = new UserSelectiveResponse();
        response.setUserId(entity.getId());
        response.setUsername(entity.getUsername());
        response.setNickname(entity.getNickname());
        response.setEmail(entity.getEmail());
        response.setMobile(entity.getMobile());
        response.setTenantId(entity.getTenantId());
        return response;
    }

}
