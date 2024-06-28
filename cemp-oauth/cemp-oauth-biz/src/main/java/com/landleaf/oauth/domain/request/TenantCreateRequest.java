package com.landleaf.oauth.domain.request;

import cn.hutool.core.util.RandomUtil;
import com.landleaf.comm.constance.PatternConstant;
import com.landleaf.oauth.domain.entity.TenantEntity;
import com.landleaf.oauth.domain.entity.UserEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;

import java.util.List;

/**
 * 租户新增参数
 *
 * @author yue lin
 * @since 2023/6/1 10:26
 */
@Data
@Schema(name = "租户新增参数", description = "租户新增参数")
public class TenantCreateRequest {

    /**
     * 租户信息
     */
    @Valid
    @NotNull(message = "租户信息不能为空")
    @Schema(description = "租户信息")
    private Tenant tenant;

    /**
     * 菜单权限
     */
    @Valid
    @NotNull(message = "菜单权限不能为空")
    @Schema(description = "菜单权限")
    private Menu menu;


    @Data
    public static class Tenant {

        /**
         * 租户企业名称
         */
        @NotBlank(message = "租户企业名称不能为空")
        @Size(min = 1, max = 30, message = "租户企业名称长度区间{min}-{max}")
        @Schema(description = "租户企业名称", example = "租户企业名称")
        private String name;

        /**
         * 租户企业编码
         */
        @NotBlank(message = "租户企业编码不能为空")
        @Pattern(regexp = PatternConstant.TENANT_CODE_PATTERN, message = "租户企业编码格式错误")
        @Size(min = 1, max = 8, message = "租户企业编码长度区间{min}-{max}")
        @Schema(description = "租户企业编码", example = "租户企业名称")
        private String code;

        /**
         * 社会信用代码
         */
        @NotBlank(message = "社会信用代码不能为空")
        @Size(min = 18, max = 18, message = "社会信用代码长度为{min}")
        @Schema(description = "社会信用代码", example = "社会信用代码")
        private String socialCreditCode;

        /**
         * 营业执照
         */
        @NotBlank(message = "营业执照不能为空")
        @Schema(description = "营业执照", example = "营业执照")
        private String businessLicense;

        /**
         * logo
         */
        @NotBlank(message = "Logo不能为空")
        @Schema(description = "logo", example = "logo")
        private String logo;

        /**
         * 地址
         */
        @NotBlank(message = "地址不能为空")
        @Size(min = 1, max = 100, message = "地址长度区间{min}-{max}")
        @Schema(description = "地址", example = "山东省日照市市辖区东张乡")
        private String address;

        /**
         * 备注
         */
        @Schema(description = "备注", example = "备注")
        private String remark;

        /**
         * 租户管理员账号
         */
        @NotBlank(message = "租户管理员账号不能为空")
        @Size(min = 1, max = 30, message = "租户管理员账号长度区间{min}-{max}")
        @Schema(description = "租户管理员账号", example = "zhangHao")
        private String adminUserName;

        /**
         * 租户管理员用户名
         */
        @NotBlank(message = "租户管理员用户名不能为空")
        @Size(min = 1, max = 30, message = "租户管理员用户名长度区间{min}-{max}")
        @Schema(description = "租户管理员用户名", example = "张三")
        private String adminNickName;

        /**
         * 租户管理员邮箱
         */
        @NotBlank(message = "租户管理员邮箱不能为空")
        @Email(message = "租户管理员邮箱格式错误")
        @Size(max = 50, message = "租户管理员邮箱长度不能超过{max}")
        @Schema(description = "租户管理员邮箱", example = "1234@email.com")
        private String adminEmail;

        /**
         * 租户管理员手机号
         */
        @NotBlank(message = "租户管理员手机号不能为空")
        @Pattern(regexp = PatternConstant.PHONE_PATTERN, message = "手机号码格式错误")
        @Schema(description = "租户管理员手机号", example = "12345678901")
        private String adminMobile;

        /**
         * 月报周期
         */
        @NotNull(message = "月报周期不能为null")
        @Schema(description = "月报周期", example = "0")
        private String reportingCycle;


        public UserEntity toUserEntity() {
            UserEntity userEntity = new UserEntity();
            userEntity.setUsername(this.adminUserName);
            userEntity.setSalt(RandomUtil.randomString(15));
            userEntity.setNickname(this.adminNickName);
            userEntity.setEmail(this.adminEmail);
            userEntity.setMobile(this.adminMobile);
            userEntity.setStatus((short) 0);
            return userEntity;
        }

        public TenantEntity toTenantEntity() {
            TenantEntity tenantEntity = new TenantEntity();
            tenantEntity.setName(this.name);
            tenantEntity.setCode(this.code);
            tenantEntity.setSocialCreditCode(this.socialCreditCode);
            tenantEntity.setBusinessLicense(this.businessLicense);
            tenantEntity.setLogo(this.logo);
            tenantEntity.setAddress(this.address);
            tenantEntity.setRemark(this.remark);
            tenantEntity.setReportingCycle(this.reportingCycle);
            return tenantEntity;
        }

    }

    @Data
    public static class Menu {

        /**
         * 菜单唯一标识符
         */
        @NotEmpty(message = "菜单权限不能为空")
        @Schema(description = "菜单唯一标识", example = "1")
        private List<String> permissions;

    }

}
