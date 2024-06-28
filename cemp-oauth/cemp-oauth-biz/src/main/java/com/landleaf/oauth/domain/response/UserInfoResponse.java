package com.landleaf.oauth.domain.response;

import com.landleaf.oauth.domain.entity.UserEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 用户详情
 *
 * @author yue lin
 * @since 2023/6/9 16:09
 */
@Data
@Schema(description = "用户详情")
public class UserInfoResponse {

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private Long id;

    /**
     * 企业ID
     */
    @Schema(description = "企业ID")
    private Long tenantId;

    /**
     * 企业名称
     */
    @Schema(description = "企业名称")
    private String tenantName;

    /**
     * 用户名
     */
    @Schema(description = "用户名")
    private String username;

    /**
     * 姓名
     */
    @Schema(description = "姓名")
    private String nickname;

    /**
     * 用户状态
     */
    @Schema(description = "用户状态")
    private Short status;

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
     * 用户角色
     */
    @Schema(description = "用户角色")
    private List<Long> roleIds;

    /**
     * 权限类型
     */
    @Schema(description = "权限类型")
    private Short nodeType;

    /**
     * 用户项目权限
     */
    @Schema(description = "用户项目权限")
    private List<Long> nodeIds;

    public static UserInfoResponse fromEntity(UserEntity entity) {
        UserInfoResponse response = new UserInfoResponse();
        response.setId(entity.getId());
        response.setTenantId(entity.getTenantId());
        response.setUsername(entity.getUsername());
        response.setNickname(entity.getNickname());
        response.setEmail(entity.getEmail());
        response.setMobile(entity.getMobile());
        return response;
    }

}
