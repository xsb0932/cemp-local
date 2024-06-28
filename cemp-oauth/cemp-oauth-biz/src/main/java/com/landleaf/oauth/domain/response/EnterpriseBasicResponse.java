package com.landleaf.oauth.domain.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 企业基本信息
 *
 * @author yue lin
 * @since 2023/6/9 13:41
 */
@Data
public class EnterpriseBasicResponse {

    /**
     * 租户id
     */
    @Schema(description = "租户ID", example = "0001")
    private Long id;

    /**
     * 租户企业名称
     */
    @Schema(description = "企业名称", example = "租户企业名称")
    private String name;

    /**
     * 租户企业code
     */
    @Schema(description = "企业编码", example = "租户企业名称")
    private String code;

    /**
     * 社会信用代码
     */
    @Schema(description = "社会信用代码", example = "社会信用代码")
    private String socialCreditCode;

    /**
     * 营业执照
     */
    @Schema(description = "营业执照", example = "营业执照")
    private String businessLicense;

    /**
     * logo
     */
    @Schema(description = "logo", example = "logo")
    private String logo;

    /**
     * 地址
     */
    @Schema(description = "地址", example = "山东省日照市市辖区东张乡")
    private String address;

    /**
     * 租户状态（0正常 1停用）
     */
    @Schema(description = "租户状态", example = "租户状态")
    private Short status;

    /**
     * 管理员账号
     */
    @Schema(description = "管理员账号", example = "zhangHao")
    private String adminUsername;

    /**
     * 租户管理员用户名
     */
    @Schema(description = "管理员用户名", example = "张三")
    private String adminNickName;

    /**
     * 租户管理员手机号
     */
    @Schema(description = "管理员手机号", example = "12345678901")
    private String adminMobile;

    /**
     * 租户管理员邮箱
     */
    @Schema(description = "管理员邮箱", example = "1234@email.com")
    private String adminEmail;

    /**
     * 备注
     */
    @Schema(description = "备注", example = "备注")
    private String remark;

}
