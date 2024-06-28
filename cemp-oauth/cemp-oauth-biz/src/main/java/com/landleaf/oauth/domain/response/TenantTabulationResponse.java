package com.landleaf.oauth.domain.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 租户基础信息
 *
 * @author yue lin
 * @since 2023/6/1 10:26
 */
@Data
@Schema(name = "租户列表信息", description = "租户列表信息")
public class TenantTabulationResponse {

    /**
     * 租户id
     */
    @Schema(description = "租户ID", example = "0001")
    private Long id;

    /**
     * 租户企业名称
     */
    @Schema(description = "租户企业名称", example = "租户企业名称")
    private String name;

    /**
     * 租户企业code
     */
    @Schema(description = "租户企业编码", example = "CODE")
    private String code;

    /**
     * 租户状态（0正常 1停用）
     */
    @Schema(description = "租户状态", example = "1")
    private Short status;

    /**
     * 地址
     */
    @Schema(description = "地址", example = "山东省日照市市辖区东张乡")
    private String address;

    /**
     * 租户管理员用户名
     */
    @Schema(description = "租户管理员用户名", example = "张三")
    private String adminNickName;

    /**
     * 租户管理员邮箱
     */
    @Schema(description = "租户管理员邮箱", example = "1234@email.com")
    private String adminEmail;

    /**
     * 租户管理员手机号
     */
    @Schema(description = "租户管理员手机号", example = "12345678901")
    private String adminMobile;

    /**
     * 备注
     */
    @Schema(description = "备注", example = "备注")
    private String remark;

    /**
     * 租户业务id（全局唯一）
     */
    @Schema(description = "租户业务id", example = "C00001")
    private String bizTenantId;

    /**
     * 租户当前拥有的模块菜单
     * <p>
     * {
     * moduleId: [permissions]
     * }
     */
    @Schema(description = "租户当前拥有的模块菜单")
    private Map<Long, List<String>> modulePermissions;


}
