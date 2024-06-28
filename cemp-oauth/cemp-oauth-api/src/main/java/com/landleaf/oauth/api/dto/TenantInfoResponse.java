package com.landleaf.oauth.api.dto;

import lombok.Data;

/**
 * 租户表
 *
 * @author yue lin
 * @since 2023/6/1 9:18
 */
@Data
public class TenantInfoResponse {
    /**
     * 租户id
     */
    private Long id;

    /**
     * 租户企业名称
     */
    private String name;

    /**
     * 租户企业code
     */
    private String code;

    /**
     * 租户状态（0正常 1停用）
     */
    private Short status;

    /**
     * 社会信用代码
     */
    private String socialCreditCode;

    /**
     * 营业执照
     */
    private String businessLicense;

    /**
     * logo
     */
    private String logo;

    /**
     * 地址
     */
    private String address;

    /**
     * 租户管理员账号id
     */
    private Long adminId;

    /**
     * 备注
     */
    private String remark;

    /**
     * 租户业务id（全局唯一）
     */
    private String bizTenantId;

    /**
     * 月报周期
     */
    private String reportingCycle;
}
