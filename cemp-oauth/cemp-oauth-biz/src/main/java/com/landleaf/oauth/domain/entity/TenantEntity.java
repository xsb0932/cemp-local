package com.landleaf.oauth.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.landleaf.pgsql.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 租户表
 *
 * @author yue lin
 * @since 2023/6/1 9:18
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value = "tb_tenant")
public class TenantEntity extends BaseEntity {
    /**
     * 租户id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 租户企业名称
     */
    @TableField(value = "name")
    private String name;

    /**
     * 租户企业code
     */
    @TableField(value = "code")
    private String code;

    /**
     * 租户状态（0正常 1停用）
     */
    @TableField(value = "status")
    private Short status;

    /**
     * 社会信用代码
     */
    @TableField(value = "social_credit_code")
    private String socialCreditCode;

    /**
     * 营业执照
     */
    @TableField(value = "business_license")
    private String businessLicense;

    /**
     * logo
     */
    @TableField(value = "logo")
    private String logo;

    /**
     * 地址
     */
    @TableField(value = "address")
    private String address;

    /**
     * 租户管理员账号id
     */
    @TableField(value = "admin_id")
    private Long adminId;

    /**
     * 备注
     */
    @TableField(value = "remark")
    private String remark;

    /**
     * 租户业务id（全局唯一）
     */
    @TableField(value = "biz_tenant_id")
    private String bizTenantId;

    /**
     * 月报周期
     */
    @TableField(value = "reporting_cycle")
    private String reportingCycle;
}