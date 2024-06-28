package com.landleaf.oauth.domain.response;

import com.landleaf.bms.api.dto.UserManageNodeResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Set;

/**
 * 用户列表
 *
 * @author yue lin
 * @since 2023/6/9 15:45
 */
@Data
@Schema(description = "用户列表")
public class UserTabulationResponse {

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private Long id;

    /**
     * 企业名称
     */
    @Schema(description = "企业名称")
    private String tenantName;

    /**
     * 企业名称
     */
    @Schema(description = "租户编号")
    private Long tenantId;

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
    private String roleNames;

    /**
     * 角色类型
     */
    @Schema(description = "角色类型")
    private Set<Short> roleTypes;

    /**
     * 项目权限
     */
    @Schema(description = "项目权限")
    private UserManageNodeResponse userManageNodeResponse;

}
